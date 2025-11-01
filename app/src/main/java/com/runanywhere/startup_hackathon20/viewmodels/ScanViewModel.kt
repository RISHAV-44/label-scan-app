package com.runanywhere.startup_hackathon20.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.startup_hackathon20.data.ScanHistoryRepository
import com.runanywhere.startup_hackathon20.models.ScanResult
import com.runanywhere.startup_hackathon20.performLlmInference
import com.runanywhere.startup_hackathon20.performOcrInference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json

private const val TAG = "ScanViewModel"
private const val TOTAL_TIMEOUT_MS = 60000L // 60 seconds max for entire process

/**
 * PRODUCTION-READY SCAN VIEW MODEL
 *
 * Features:
 * - Comprehensive error handling
 * - Timeout protection
 * - Detailed logging
 * - Graceful degradation
 * - Proper state management
 * - Memory leak prevention
 */
class ScanViewModel : ViewModel() {

    private val repository = ScanHistoryRepository()

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _progressMessage = MutableStateFlow<String?>(null)
    val progressMessage: StateFlow<String?> = _progressMessage.asStateFlow()

    // Lenient JSON parser for better error recovery
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Main processing function with comprehensive error handling
     */
    fun processFoodLabel(bitmap: Bitmap, userId: String?) {
        // Prevent multiple concurrent scans
        if (_isLoading.value) {
            Log.w(TAG, "Scan already in progress, ignoring new request")
            return
        }

        viewModelScope.launch {
            var ocrText: String? = null
            var jsonResult: String? = null

            try {
                _isLoading.value = true
                _error.value = null
                _progressMessage.value = "Preparing scan..."

                Log.d(TAG, "═══════════════════════════════════════")
                Log.d(TAG, "🚀 STARTING SCAN PROCESS")
                Log.d(TAG, "User ID: ${userId ?: "anonymous"}")
                Log.d(TAG, "Image size: ${bitmap.width}x${bitmap.height}")
                Log.d(TAG, "═══════════════════════════════════════")

                // Validate bitmap
                if (!isValidBitmap(bitmap)) {
                    throw IllegalArgumentException("Invalid image: ${bitmap.width}x${bitmap.height}")
                }

                // Wrap entire process in timeout
                withTimeout(TOTAL_TIMEOUT_MS) {

                    // STEP 1: OCR (with timeout and retry built-in)
                    _progressMessage.value = "Extracting text from label..."
                    Log.d(TAG, "📷 STEP 1: Starting OCR...")

                    ocrText = performOcrInference(bitmap)

                    if (ocrText.isNullOrBlank()) {
                        throw IllegalStateException("OCR returned no text")
                    }

                    Log.d(TAG, "✅ OCR completed: ${ocrText?.length} characters")
                    Log.d(TAG, "OCR preview: ${ocrText?.take(100)?.replace("\n", " ")}")

                    // Check for OCR errors
                    if (ocrText?.contains("OCR_ERROR") == true) {
                        throw IllegalStateException("OCR failed: $ocrText")
                    }

                    // STEP 2: LLM Analysis (with timeout and retry built-in)
                    _progressMessage.value = "Analyzing nutrition data..."
                    Log.d(TAG, "🤖 STEP 2: Starting LLM analysis...")

                    jsonResult = performLlmInference(ocrText!!, "")

                    if (jsonResult.isNullOrBlank()) {
                        throw IllegalStateException("LLM returned no data")
                    }

                    Log.d(TAG, "✅ LLM completed: ${jsonResult?.length} characters")
                    Log.d(TAG, "JSON preview: ${jsonResult?.take(200)}")

                    // STEP 3: Parse JSON
                    _progressMessage.value = "Processing results..."
                    Log.d(TAG, "📊 STEP 3: Parsing JSON...")

                    val result = parseJsonToScanResult(jsonResult!!)

                    Log.d(TAG, "✅ Parsed: ${result.productName}")
                    Log.d(TAG, "Calories: ${result.calories}, Sugar: ${result.sugar}g")

                    // STEP 4: Save to Firestore
                    if (userId != null) {
                        _progressMessage.value = "Saving to history..."
                        Log.d(TAG, "💾 STEP 4: Saving to Firestore...")

                        repository.saveScan(userId, result).onSuccess { scanId ->
                            Log.d(TAG, "✅ Saved with ID: $scanId")
                            _scanResult.value = result.copy(
                                scanId = scanId,
                                timestamp = System.currentTimeMillis()
                            )
                        }.onFailure { saveException ->
                            Log.e(TAG, "⚠️ Save failed: ${saveException.message}")
                            // Still show result even if save fails
                            _scanResult.value = result.copy(
                                timestamp = System.currentTimeMillis()
                            )
                            _error.value = "Scan successful but couldn't save to history"
                        }
                    } else {
                        Log.w(TAG, "⚠️ No user ID, scan not saved")
                        _scanResult.value = result.copy(
                            timestamp = System.currentTimeMillis()
                        )
                    }

                    Log.d(TAG, "═══════════════════════════════════════")
                    Log.d(TAG, "🎉 SCAN COMPLETED SUCCESSFULLY")
                    Log.d(TAG, "═══════════════════════════════════════")
                }

                _progressMessage.value = null
                _isLoading.value = false

            } catch (e: CancellationException) {
                // Coroutine was cancelled - don't treat as error
                Log.w(TAG, "Scan cancelled by user")
                _error.value = "Scan cancelled"
                _progressMessage.value = null
                _isLoading.value = false

            } catch (e: Exception) {
                Log.e(TAG, "═══════════════════════════════════════")
                Log.e(TAG, "❌ SCAN FAILED: ${e.javaClass.simpleName}")
                Log.e(TAG, "Message: ${e.message}")
                Log.e(TAG, "OCR Text Length: ${ocrText?.length ?: 0}")
                Log.e(TAG, "JSON Result Length: ${jsonResult?.length ?: 0}")
                Log.e(TAG, "═══════════════════════════════════════", e)

                val errorMessage = when {
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Scan took too long - please try again with better lighting"

                    e.message?.contains("API key", ignoreCase = true) == true ->
                        "API configuration error - please contact support"

                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Network error - check your internet connection"

                    e is IllegalArgumentException ->
                        "Invalid image - please try taking the photo again"

                    ocrText?.contains("OCR_ERROR") == true ->
                        "Could not read label - try better lighting and hold steady"

                    else ->
                        "Scan failed: ${e.message ?: "Unknown error"}"
                }

                _error.value = errorMessage
                _progressMessage.value = null
                _isLoading.value = false

                // Set fallback result for debugging
                _scanResult.value = createDebugScanResult(errorMessage, ocrText, jsonResult)
            }
        }
    }

    /**
     * Validates bitmap before processing
     */
    private fun isValidBitmap(bitmap: Bitmap): Boolean {
        return try {
            bitmap.width > 0 &&
                    bitmap.height > 0 &&
                    bitmap.width <= 4096 &&
                    bitmap.height <= 4096 &&
                    !bitmap.isRecycled
        } catch (e: Exception) {
            Log.e(TAG, "Error validating bitmap: ${e.message}")
            false
        }
    }

    /**
     * Parses JSON string to ScanResult with extensive error handling
     */
    private fun parseJsonToScanResult(jsonString: String): ScanResult {
        try {
            // Clean JSON
            val cleanJson = cleanJsonString(jsonString)
            Log.d(TAG, "Cleaned JSON: $cleanJson")

            // Try parsing
            val result = json.decodeFromString<ScanResult>(cleanJson)

            // Validate result
            if (result.productName.isNullOrBlank()) {
                Log.w(TAG, "Product name is blank, using fallback")
                return result.copy(productName = "Food Product")
            }

            return result

        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing failed: ${e.message}")
            Log.e(TAG, "Failed JSON: $jsonString")

            // Try to extract any useful data manually
            return tryManualJsonParse(jsonString)
        }
    }

    /**
     * Cleans JSON string for parsing
     */
    private fun cleanJsonString(json: String): String {
        return json
            .trim()
            .replace("```json", "")
            .replace("```", "")
            .replace("\\_error", "_error") // Fix escaped underscore
            .let { cleaned ->
                // Extract just the JSON object
                val start = cleaned.indexOf('{')
                val end = cleaned.lastIndexOf('}')
                if (start >= 0 && end > start) {
                    cleaned.substring(start, end + 1)
                } else {
                    cleaned
                }
            }
    }

    /**
     * Attempts manual JSON parsing as fallback
     */
    private fun tryManualJsonParse(jsonString: String): ScanResult {
        Log.w(TAG, "Attempting manual JSON extraction...")

        return try {
            val productName = extractValue(jsonString, "productName") ?: "Food Product"
            val calories = extractNumberValue(jsonString, "calories")
            val sugar = extractNumberValue(jsonString, "sugar")
            val sodium = extractNumberValue(jsonString, "sodium")
            val totalFat = extractNumberValue(jsonString, "totalFat")
            val saturatedFat = extractNumberValue(jsonString, "saturatedFat")
            val fiber = extractNumberValue(jsonString, "fiber")
            val protein = extractNumberValue(jsonString, "protein")

            ScanResult(
                productName = productName,
                calories = calories,
                sugar = sugar,
                sodium = sodium,
                totalFat = totalFat,
                saturatedFat = saturatedFat,
                fiber = fiber,
                protein = protein,
                allergens = emptyList(),
                watchlistIngredients = emptyList()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Manual parsing also failed: ${e.message}")
            createErrorScanResult("Unable to parse nutrition data")
        }
    }

    /**
     * Extracts string value from JSON manually
     */
    private fun extractValue(json: String, key: String): String? {
        return try {
            val pattern = """"$key"\s*:\s*"([^"]*)"""".toRegex()
            pattern.find(json)?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extracts numeric value from JSON manually
     */
    private fun extractNumberValue(json: String, key: String): Int? {
        return try {
            val pattern = """"$key"\s*:\s*(\d+)""".toRegex()
            pattern.find(json)?.groupValues?.get(1)?.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Creates an error scan result
     */
    private fun createErrorScanResult(errorMsg: String): ScanResult {
        return ScanResult(
            productName = "Error: $errorMsg",
            calories = null,
            sugar = null,
            sodium = null,
            totalFat = null,
            saturatedFat = null,
            fiber = null,
            protein = null,
            allergens = emptyList(),
            watchlistIngredients = emptyList()
        )
    }

    /**
     * Creates debug scan result with diagnostic info
     */
    private fun createDebugScanResult(
        error: String,
        ocrText: String?,
        jsonResult: String?
    ): ScanResult {
        return ScanResult(
            productName = "Scan Failed",
            calories = 0,
            sugar = 0,
            sodium = 0,
            totalFat = 0,
            saturatedFat = 0,
            fiber = 0,
            protein = 0,
            allergens = listOf(
                "DEBUG INFO:",
                "Error: $error",
                "OCR: ${if (ocrText.isNullOrBlank()) "Failed" else "${ocrText.length} chars"}",
                "LLM: ${if (jsonResult.isNullOrBlank()) "Failed" else "${jsonResult.length} chars"}"
            ),
            watchlistIngredients = emptyList()
        )
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clear result state
     */
    fun clearResult() {
        _scanResult.value = null
        _progressMessage.value = null
    }

    /**
     * Cancel ongoing scan
     */
    fun cancelScan() {
        Log.d(TAG, "Cancelling scan...")
        _isLoading.value = false
        _progressMessage.value = null
    }

    /**
     * Clean up on ViewModel destruction
     */
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, cleaning up...")
        cancelScan()
    }
}
