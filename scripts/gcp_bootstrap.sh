#!/usr/bin/env bash
# gcp_bootstrap.sh
# Jednorazowa konfiguracja GCP dla CI/CD (Artifact Registry, SA, role, WIF).
# Uruchamiaj na Macu lokalnie albo w Cloud Shell.
set -euo pipefail

########################
# ✏️ KONFIGURACJA
########################
PROJECT_ID="${PROJECT_ID:-proof-of-authenticity}"  # <-- podmień jeśli inny
REGION="${REGION:-europe-central2}"                # region Artifact Registry i Cloud Run (Warszawa)
REPO_NAME="${REPO_NAME:-ktor-poa}"                 # Artifact Registry repo (format: Docker)
SERVICE_NAME="${SERVICE_NAME:-ktor-api}"           # nazwa usługi Cloud Run (informacyjnie)

# Konta serwisowe
DEPLOY_SA_ID="${DEPLOY_SA_ID:-gh-deployer}"        # SA używany przez GitHub Actions do deployu
RUNTIME_SA_ID="${RUNTIME_SA_ID:-ktor-runtime}"     # SA przypisane do Cloud Run (runtime)

# Workload Identity Federation (GitHub → GCP)
WIF_POOL_ID="${WIF_POOL_ID:-github}"               # ID puli
WIF_PROVIDER_ID="${WIF_PROVIDER_ID:-github-provider}" # ID providera
GH_OWNER="${GH_OWNER:-OWNER}"                      # <-- podmień: nazwa organizacji/użytkownika na GitHubie
GH_REPO="${GH_REPO:-REPO}"                         # <-- podmień: nazwa repozytorium
GH_BRANCH="${GH_BRANCH:-main}"                     # gałąź, z której wolno robić deploy (refs/heads/main)

########################
# 🔎 Wymagania
########################
if ! command -v gcloud >/dev/null 2>&1; then
  echo "❌ Brak Google Cloud SDK. Zainstaluj: brew install --cask google-cloud-sdk"
  exit 1
fi

echo "➡️ Używam projektu: ${PROJECT_ID}"
gcloud config set project "${PROJECT_ID}" >/dev/null

# Pobierz numer projektu (potrzebny do WIF)
PROJECT_NUMBER="$(gcloud projects describe "${PROJECT_ID}" --format='value(projectNumber)')"
if [[ -z "${PROJECT_NUMBER}" ]]; then
  echo "❌ Nie mogę odczytać PROJECT_NUMBER dla ${PROJECT_ID}"
  exit 1
fi

DEPLOY_SA_EMAIL="${DEPLOY_SA_ID}@${PROJECT_ID}.iam.gserviceaccount.com"
RUNTIME_SA_EMAIL="${RUNTIME_SA_ID}@${PROJECT_ID}.iam.gserviceaccount.com"
WIF_PROVIDER_RESOURCE="projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${WIF_POOL_ID}/providers/${WIF_PROVIDER_ID}"

echo "ℹ️  PROJECT_NUMBER = ${PROJECT_NUMBER}"
echo "ℹ️  DEPLOY_SA      = ${DEPLOY_SA_EMAIL}"
echo "ℹ️  RUNTIME_SA     = ${RUNTIME_SA_EMAIL}"
echo "ℹ️  WIF provider   = ${WIF_PROVIDER_RESOURCE}"
echo "ℹ️  GitHub repo    = ${GH_OWNER}/${GH_REPO} @ ${GH_BRANCH}"

if [[ "${GH_OWNER}" == "OWNER" || "${GH_REPO}" == "REPO" ]]; then
  echo "❌ Ustaw GH_OWNER i GH_REPO na prawidłowe wartości (organizacja/użytkownik i repozytorium)."
  exit 1
fi

########################
# ✅ Włącz wymagane API
########################
echo "▶️  Włączam API (Run, Artifact Registry, IAM, IAM Credentials, Cloud Build)..."
gcloud services enable \
  run.googleapis.com \
  artifactregistry.googleapis.com \
  iam.googleapis.com \
  iamcredentials.googleapis.com \
  cloudbuild.googleapis.com

########################
# 🗃️  Artifact Registry (repo Docker)
########################
echo "▶️  Tworzę (o ile brak) repozytorium Artifact Registry: ${REPO_NAME} w ${REGION}"
if ! gcloud artifacts repositories describe "${REPO_NAME}" --location="${REGION}" >/dev/null 2>&1; then
  gcloud artifacts repositories create "${REPO_NAME}" \
    --repository-format=docker \
    --location="${REGION}" \
    --description="Docker images for Cloud Run"
else
  echo "ℹ️  Repozytorium już istnieje."
fi

########################
# 👤 Konta serwisowe
########################
create_sa_if_missing() {
  local sa_id="$1"
  local display_name="$2"
  if ! gcloud iam service-accounts describe "${sa_id}@${PROJECT_ID}.iam.gserviceaccount.com" >/dev/null 2>&1; then
    echo "▶️  Tworzę SA: ${sa_id} (${display_name})"
    gcloud iam service-accounts create "${sa_id}" --display-name="${display_name}"
  else
    echo "ℹ️  SA ${sa_id} już istnieje."
  fi
}

create_sa_if_missing "${DEPLOY_SA_ID}" "GitHub Actions deployer"
create_sa_if_missing "${RUNTIME_SA_ID}" "Cloud Run runtime SA"

########################
# 🔐 Role dla SA (least privilege)
########################
echo "▶️  Nadaję role dla deployera (run.admin, iam.serviceAccountUser, artifactregistry.writer)..."
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${DEPLOY_SA_EMAIL}" \
  --role="roles/run.admin" >/dev/null

gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${DEPLOY_SA_EMAIL}" \
  --role="roles/iam.serviceAccountUser" >/dev/null

gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${DEPLOY_SA_EMAIL}" \
  --role="roles/artifactregistry.writer" >/dev/null

echo "▶️  Nadaję rolę dla runtime SA (artifactregistry.reader)..."
gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
  --member="serviceAccount:${RUNTIME_SA_EMAIL}" \
  --role="roles/artifactregistry.reader" >/dev/null

########################
# 🔗 Workload Identity Federation (OIDC z GitHub Actions)
########################
echo "▶️  Tworzę (o ile brak) Workload Identity Pool: ${WIF_POOL_ID}"
if ! gcloud iam workload-identity-pools describe "${WIF_POOL_ID}" --location="global" >/dev/null 2>&1; then
  gcloud iam workload-identity-pools create "${WIF_POOL_ID}" \
    --location="global" \
    --display-name="GitHub Actions Pool"
else
  echo "ℹ️  Pool już istnieje."
fi

echo "▶️  Tworzę (o ile brak) OIDC provider: ${WIF_PROVIDER_ID}"
if ! gcloud iam workload-identity-pools providers describe "${WIF_PROVIDER_ID}" \
     --location="global" --workload-identity-pool="${WIF_POOL_ID}" >/dev/null 2>&1; then
  gcloud iam workload-identity-pools providers create-oidc "${WIF_PROVIDER_ID}" \
    --location="global" \
    --workload-identity-pool="${WIF_POOL_ID}" \
    --display-name="GitHub OIDC" \
    --issuer-uri="https://token.actions.githubusercontent.com" \
    --attribute-mapping="google.subject=assertion.sub,attribute.repository=assertion.repository,attribute.ref=assertion.ref" \
    --attribute-condition="attribute.repository==\"${GH_OWNER}/${GH_REPO}\" && attribute.ref==\"refs/heads/${GH_BRANCH}\""
else
  echo "ℹ️  Provider już istnieje."
fi

echo "▶️  Daję puli prawo do podszywania się pod ${DEPLOY_SA_EMAIL} (workloadIdentityUser)..."
gcloud iam service-accounts add-iam-policy-binding "${DEPLOY_SA_EMAIL}" \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${WIF_POOL_ID}/attribute.repository/${GH_OWNER}/${GH_REPO}" >/dev/null

########################
# ✅ Podsumowanie i hinty do GitHub Actions
########################
WIF_PROVIDER_RESOURCE="projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${WIF_POOL_ID}/providers/${WIF_PROVIDER_ID}"
AR_HOST="${REGION}-docker.pkg.dev"
IMAGE_PATH="${AR_HOST}/${PROJECT_ID}/${REPO_NAME}/${SERVICE_NAME}:\$GITHUB_SHA"

cat <<EOF

✅ Gotowe!

Najważniejsze wartości do użycia w GitHub Actions:

  PROJECT_ID:        ${PROJECT_ID}
  PROJECT_NUMBER:    ${PROJECT_NUMBER}
  REGION:            ${REGION}
  REPOSITORY:        ${REPO_NAME}
  SERVICE:           ${SERVICE_NAME}

  # Workload Identity Federation
  WIF_PROVIDER:      ${WIF_PROVIDER_RESOURCE}
  DEPLOY_SA:         ${DEPLOY_SA_EMAIL}

  # Runtime service account (Cloud Run)
  RUNTIME_SA:        ${RUNTIME_SA_EMAIL}

  # Artifact Registry (logowanie Docker)
  AR_HOST:           ${AR_HOST}

Przykład w workflow:
  - uses: google-github-actions/auth@v2
    with:
      workload_identity_provider: ${WIF_PROVIDER_RESOURCE}
      service_account: ${DEPLOY_SA_EMAIL}

  - name: Configure Docker for Artifact Registry
    run: gcloud auth configure-docker ${AR_HOST} --quiet

  - name: Build & Push
    run: |
      docker build -t ${IMAGE_PATH} .
      docker push ${IMAGE_PATH}

  - name: Deploy
    run: |
      gcloud run deploy ${SERVICE_NAME} \\
        --image ${AR_HOST}/${PROJECT_ID}/${REPO_NAME}/${SERVICE_NAME}:\$GITHUB_SHA \\
        --region ${REGION} \\
        --platform managed \\
        --allow-unauthenticated \\
        --service-account ${RUNTIME_SA_EMAIL} \\
        --port 8080

EOF
