package com.runanywhere.startup_hackathon20.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.startup_hackathon20.data.ScanHistoryRepository
import com.runanywhere.startup_hackathon20.models.ScanResult
import com.runanywhere.startup_hackathon20.performLlmInference
import com.runanywhere.startup_hackathon20.performOcrInference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ScanViewModel : ViewModel() {

    private val repository = ScanHistoryRepository()

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun processFoodLabel(bitmap: Bitmap, userId: String?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("ScanViewModel", "Starting scan processing for userId: $userId")

                // Step 1: Perform OCR to extract text from image using local AI
                val rawText = performOcrInference(bitmap)
                Log.d("ScanViewModel", "OCR extracted text length: ${rawText.length}")

                // Step 2: Define system prompt for LLM
                val systemPrompt = """
                    Extract nutrition info and return as JSON.
                """.trimIndent()

                // Step 3: Use local LLM to extract structured data
                val jsonResult = performLlmInference(rawText, systemPrompt)
                Log.d("ScanViewModel", "LLM result: $jsonResult")

                // Step 4: Parse JSON result
                val result = try {
                    // Extract JSON from the response (in case there's extra text)
                    val jsonStart = jsonResult.indexOf('{')
                    val jsonEnd = jsonResult.lastIndexOf('}') + 1

                    if (jsonStart >= 0 && jsonEnd > jsonStart) {
                        val jsonString = jsonResult.substring(jsonStart, jsonEnd)
                        json.decodeFromString<ScanResult>(jsonString)
                    } else {
                        // Fallback if no JSON found
                        ScanResult(
                            productName = "Unknown Product",
                            calories = 0,
                            sugar = 0,
                            sodium = 0,
                            totalFat = 0,
                            saturatedFat = 0,
                            fiber = 0,
                            protein = 0,
                            allergens = emptyList(),
                            watchlistIngredients = emptyList()
                        )
                    }
                } catch (e: Exception) {
                    Log.e("ScanViewModel", "JSON parsing failed: ${e.message}")
                    // Fallback result if JSON parsing fails
                    ScanResult(
                        productName = "Scan Error",
                        calories = null,
                        sugar = null,
                        sodium = null,
                        totalFat = null,
                        saturatedFat = null,
                        fiber = null,
                        protein = null,
                        allergens = null,
                        watchlistIngredients = null
                    )
                }

                Log.d("ScanViewModel", "Parsed result: $result")

                // Step 5: Save to Firestore if user is logged in
                if (userId != null) {
                    Log.d("ScanViewModel", "Attempting to save scan to Firestore...")
                    repository.saveScan(userId, result).onSuccess { scanId ->
                        Log.d("ScanViewModel", "Scan saved successfully with ID: $scanId")
                        _scanResult.value = result.copy(
                            scanId = scanId,
                            timestamp = System.currentTimeMillis()
                        )
                    }.onFailure { exception ->
                        Log.e("ScanViewModel", "Failed to save scan: ${exception.message}")
                        // Still show result even if save fails
                        _scanResult.value = result.copy(timestamp = System.currentTimeMillis())
                    }
                } else {
                    Log.w("ScanViewModel", "User not logged in, scan not saved to Firestore")
                    _scanResult.value = result.copy(timestamp = System.currentTimeMillis())
                }

                _isLoading.value = false

            } catch (e: Exception) {
                Log.e("ScanViewModel", "Error processing image: ${e.message}", e)
                _error.value = "Error processing image: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearResult() {
        _scanResult.value = null
    }
}
