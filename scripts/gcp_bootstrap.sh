#!/usr/bin/env bash
# gcp_bootstrap_all.sh
# Kompletny bootstrap: API, Artifact Registry, SA + role, WIF (GitHub OIDC),
# uprawnienia dla użytkownika oraz agentów Cloud Run. Idempotentny.
set -euo pipefail

########################################
# ✏️ KONFIGURACJA (możesz nadpisać env)
########################################
PROJECT_ID="${PROJECT_ID:-proof-of-authenticity}"
REGION="${REGION:-europe-central2}"                 # Cloud Run & Artifact Registry
REPO_NAME="${REPO_NAME:-ktor-poa}"                  # Artifact Registry (docker)
SERVICE_NAME="${SERVICE_NAME:-ktor-api}"            # Cloud Run service

# Konta serwisowe
DEPLOY_SA_ID="${DEPLOY_SA_ID:-gh-deployer}"         # do deployu z GitHub Actions
RUNTIME_SA_ID="${RUNTIME_SA_ID:-ktor-runtime}"      # runtime Cloud Run

# Workload Identity Federation (GitHub → GCP)
WIF_POOL_ID="${WIF_POOL_ID:-github}"
WIF_PROVIDER_ID="${WIF_PROVIDER_ID:-github-provider}"
GH_OWNER="${GH_OWNER:-spoonofcode}"                 # GitHub owner (user/org)
GH_REPO="${GH_REPO:-ktor-poa}"                      # GitHub repo
GH_BRANCH="${GH_BRANCH:-main}"                      # gałąź (refs/heads/main)
STRICT_BRANCH_CONDITION="${STRICT_BRANCH_CONDITION:-true}"  # true/false

# Uprawnienia dla CIEBIE (użytkownika)
USER_EMAIL="${USER_EMAIL:-$(gcloud config get-value account 2>/dev/null || true)}"
GRANT_ADMIN="${GRANT_ADMIN:-false}"                 # nadaj też role admin? true/false

########################################
# 🔎 Wymagania
########################################
if ! command -v gcloud >/dev/null 2>&1; then
  echo "❌ Brak Google Cloud SDK. Zainstaluj: https://cloud.google.com/sdk"
  exit 1
fi

if [[ -z "${USER_EMAIL}" ]]; then
  echo "❌ Nie wykryto USER_EMAIL. Użyj: gcloud auth login lub podaj USER_EMAIL=you@example.com"
  exit 1
fi

echo "➡️ Projekt: ${PROJECT_ID}"
gcloud config set project "${PROJECT_ID}" >/dev/null

PROJECT_NUMBER="$(gcloud projects describe "${PROJECT_ID}" --format='value(projectNumber)')"
if [[ -z "${PROJECT_NUMBER}" ]]; then
  echo "❌ Nie mogę odczytać PROJECT_NUMBER dla ${PROJECT_ID}"
  exit 1
fi

DEPLOY_SA_EMAIL="${DEPLOY_SA_ID}@${PROJECT_ID}.iam.gserviceaccount.com"
RUNTIME_SA_EMAIL="${RUNTIME_SA_ID}@${PROJECT_ID}.iam.gserviceaccount.com"
SERVICE_AGENT="service-${PROJECT_NUMBER}@serverless-robot-prod.iam.gserviceaccount.com"
WIF_PROVIDER_RESOURCE="projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${WIF_POOL_ID}/providers/${WIF_PROVIDER_ID}"
AR_HOST="${REGION}-docker.pkg.dev"

echo "ℹ️  PROJECT_NUMBER      = ${PROJECT_NUMBER}"
echo "ℹ️  DEPLOY_SA           = ${DEPLOY_SA_EMAIL}"
echo "ℹ️  RUNTIME_SA          = ${RUNTIME_SA_EMAIL}"
echo "ℹ️  Cloud Run agent SA  = ${SERVICE_AGENT}"
echo "ℹ️  WIF provider        = ${WIF_PROVIDER_RESOURCE}"
echo "ℹ️  GitHub repo         = ${GH_OWNER}/${GH_REPO} @ ${GH_BRANCH}"
echo "ℹ️  USER_EMAIL          = ${USER_EMAIL}"

if [[ "${GH_OWNER}" == "OWNER" || "${GH_REPO}" == "REPO" ]]; then
  echo "❌ Ustaw GH_OWNER/GH_REPO na prawidłowe wartości."
  exit 1
fi

########################################
# 🔧 Funkcje pomocnicze
########################################
ensure_api() {
  local api="$1"
  gcloud services enable "${api}" --quiet >/dev/null || true
}

ensure_repo() {
  local repo="${1}"
  local region="${2}"
  if ! gcloud artifacts repositories describe "${repo}" --location="${region}" >/dev/null 2>&1; then
    echo "▶️  Tworzę Artifact Registry: ${repo} (${region})"
    gcloud artifacts repositories create "${repo}" \
      --repository-format=docker \
      --location="${region}" \
      --description="Docker images for Cloud Run"
  else
    echo "ℹ️  Artifact Registry ${repo} już istnieje."
  fi
}

ensure_sa() {
  local sa_id="$1"
  local sa_email="${sa_id}@${PROJECT_ID}.iam.gserviceaccount.com"
  local display="$2"
  if ! gcloud iam service-accounts describe "${sa_email}" >/dev/null 2>&1; then
    echo "▶️  Tworzę SA: ${sa_id} (${display})"
    gcloud iam service-accounts create "${sa_id}" --display-name="${display}"
  else
    echo "ℹ️  SA ${sa_id} już istnieje."
  fi
}

grant_project_role() {
  local member="$1"  # user:..., serviceAccount:...
  local role="$2"
  gcloud projects add-iam-policy-binding "${PROJECT_ID}" \
    --member="${member}" \
    --role="${role}" \
    --quiet >/dev/null || {
      echo "⚠️  Nie udało się nadać roli ${role} dla ${member}."
      return 1
    }
  echo "✅ Nadano ${role} → ${member}"
}

ensure_wif_pool() {
  local pool_id="$1"
  if ! gcloud iam workload-identity-pools describe "${pool_id}" --location="global" >/dev/null 2>&1; then
    echo "▶️  Tworzę Workload Identity Pool: ${pool_id}"
    gcloud iam workload-identity-pools create "${pool_id}" \
      --location="global" \
      --display-name="GitHub Actions Pool"
  else
    echo "ℹ️  Pool ${pool_id} już istnieje."
  fi
}

upsert_wif_provider() {
  local pool_id="$1"
  local provider_id="$2"
  local owner_repo="${GH_OWNER}/${GH_REPO}"
  local condition
  if [[ "${STRICT_BRANCH_CONDITION}" == "true" ]]; then
    condition="attribute.repository==\"${owner_repo}\" && attribute.ref==\"refs/heads/${GH_BRANCH}\""
  else
    condition="attribute.repository==\"${owner_repo}\""
  fi

  local mapping="google.subject=assertion.sub,attribute.repository=assertion.repository,attribute.ref=assertion.ref,attribute.repository_owner=assertion.repository_owner,attribute.workflow_ref=assertion.workflow_ref,attribute.actor=assertion.actor"

  if gcloud iam workload-identity-pools providers describe "${provider_id}" \
       --location="global" --workload-identity-pool="${pool_id}" >/dev/null 2>&1; then
    echo "▶️  Aktualizuję OIDC provider: ${provider_id}"
    gcloud iam workload-identity-pools providers update-oidc "${provider_id}" \
      --project="${PROJECT_ID}" \
      --location="global" \
      --workload-identity-pool="${pool_id}" \
      --attribute-mapping="${mapping}" \
      --attribute-condition="${condition}" >/dev/null
  else
    echo "▶️  Tworzę OIDC provider: ${provider_id}"
    gcloud iam workload-identity-pools providers create-oidc "${provider_id}" \
      --project="${PROJECT_ID}" \
      --location="global" \
      --workload-identity-pool="${pool_id}" \
      --display-name="GitHub OIDC" \
      --issuer-uri="https://token.actions.githubusercontent.com" \
      --attribute-mapping="${mapping}" \
      --attribute-condition="${condition}" >/dev/null
  fi
}

bind_workload_identity_user() {
  local principal="principalSet://iam.googleapis.com/projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${WIF_POOL_ID}/attribute.repository/${GH_OWNER}/${GH_REPO}"
  echo "▶️  Nadaję roles/iam.workloadIdentityUser dla ${DEPLOY_SA_EMAIL} -> ${principal}"
  gcloud iam service-accounts add-iam-policy-binding "${DEPLOY_SA_EMAIL}" \
    --role="roles/iam.workloadIdentityUser" \
    --member="${principal}" \
    --quiet >/dev/null
}

########################################
# ✅ API wymagane
########################################
echo "▶️  Włączam wymagane API…"
ensure_api run.googleapis.com
ensure_api artifactregistry.googleapis.com
ensure_api iam.googleapis.com
ensure_api iamcredentials.googleapis.com
ensure_api cloudbuild.googleapis.com

########################################
# 🗃️  Artifact Registry
########################################
ensure_repo "${REPO_NAME}" "${REGION}"

########################################
# 👤 Konta serwisowe
########################################
ensure_sa "${DEPLOY_SA_ID}"  "GitHub Actions deployer"
ensure_sa "${RUNTIME_SA_ID}" "Cloud Run runtime SA"

########################################
# 🔐 Role dla SA
########################################
echo "▶️  Role dla deployera (run.admin, iam.serviceAccountUser, artifactregistry.writer)…"
grant_project_role "serviceAccount:${DEPLOY_SA_EMAIL}" "roles/run.admin"
grant_project_role "serviceAccount:${DEPLOY_SA_EMAIL}" "roles/iam.serviceAccountUser"
grant_project_role "serviceAccount:${DEPLOY_SA_EMAIL}" "roles/artifactregistry.writer"

echo "▶️  Role dla runtime SA (artifactregistry.reader)…"
grant_project_role "serviceAccount:${RUNTIME_SA_EMAIL}" "roles/artifactregistry.reader"

echo "▶️  Pozwalam deployerowi używać runtime SA (iam.serviceAccountUser na runtime SA)…"
gcloud iam service-accounts add-iam-policy-binding "${RUNTIME_SA_EMAIL}" \
  --member="serviceAccount:${DEPLOY_SA_EMAIL}" \
  --role="roles/iam.serviceAccountUser" \
  --quiet >/dev/null

########################################
# 🔗 Workload Identity Federation
########################################
ensure_wif_pool "${WIF_POOL_ID}"
upsert_wif_provider "${WIF_POOL_ID}" "${WIF_PROVIDER_ID}"
bind_workload_identity_user

########################################
# 👤 Uprawnienia dla Twojego użytkownika
########################################
echo "▶️  Nadaję role dla użytkownika (${USER_EMAIL}) do podglądu Cloud Run…"
grant_project_role "user:${USER_EMAIL}" "roles/viewer"
grant_project_role "user:${USER_EMAIL}" "roles/run.viewer"
grant_project_role "user:${USER_EMAIL}" "roles/artifactregistry.reader"
if [[ "${GRANT_ADMIN}" == "true" ]]; then
  echo "▶️  (ADMIN) Role administracyjne dla użytkownika…"
  grant_project_role "user:${USER_EMAIL}" "roles/run.admin"
fi

########################################
# 🤖 Role dla Cloud Run Service Agent
########################################
echo "▶️  Zapewniam role dla Cloud Run Service Agent…"
grant_project_role "serviceAccount:${SERVICE_AGENT}" "roles/run.serviceAgent"
grant_project_role "serviceAccount:${SERVICE_AGENT}" "roles/artifactregistry.reader"

########################################
# 🔍 Podgląd ustawień providera
########################################
echo "▶️  Provider – podsumowanie:"
gcloud iam workload-identity-pools providers describe "${WIF_PROVIDER_ID}" \
  --project="${PROJECT_ID}" --location=global --workload-identity-pool="${WIF_POOL_ID}" \
  --format="yaml(attributeMapping,attributeCondition,name)"

########################################
# 🧪 Sanity check (lista usług)
########################################
echo "▶️  Lista usług Cloud Run (${REGION}):"
if ! gcloud run services list --region "${REGION}" --format="table(METADATA.name,STATUS.url)"; then
  echo "⚠️  Nie udało się pobrać listy. Sprawdź konto/projekt/region."
fi

########################################
# ✅ Podsumowanie i hinty do GitHub Actions
########################################
IMAGE_PATH="${AR_HOST}/${PROJECT_ID}/${REPO_NAME}/${SERVICE_NAME}:\$GITHUB_SHA"

cat <<EOF

✅ Bootstrap zakończony.

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

Fragment workflow (krytyczne dwie linie):
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

🔎 Jeśli zobaczysz "unauthorized_client … attribute condition":
   - Sprawdź zgodność \${{ github.repository }} z ${GH_OWNER}/${GH_REPO}.
   - Jeśli odpalasz z innej gałęzi niż ${GH_BRANCH}, ustaw STRICT_BRANCH_CONDITION=false i ponów bootstrap.

EOF