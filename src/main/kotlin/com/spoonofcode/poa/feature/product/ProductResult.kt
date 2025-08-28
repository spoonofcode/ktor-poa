package com.spoonofcode.poa.feature.product

sealed class ProductResult {
    object Success : ProductResult()
    object Error : ProductResult()
    object ProductNotFound : ProductResult()
    object UserNotFound : ProductResult()
}