package com.runanywhere.startup_hackathon20.models

import kotlinx.serialization.Serializable

@Serializable
data class ScanResult(
    val scanId: String? = null,
    val timestamp: Long? = null,
    val productName: String? = null,
    val calories: Int? = null,
    val sugar: Int? = null,
    val sodium: Int? = null,
    val totalFat: Int? = null,
    val saturatedFat: Int? = null,
    val fiber: Int? = null,
    val protein: Int? = null,
    val allergens: List<String>? = null,
    val watchlistIngredients: List<String>? = null
)
