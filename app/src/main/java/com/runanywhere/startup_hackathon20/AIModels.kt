package com.runanywhere.startup_hackathon20

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * PRODUCTION-READY AI PIPELINE v2.0
 *
 * Two-step process with extensive error handling:
 * 1. ML Kit OCR: Extract text from nutrition label (on-device, fast)
 * 2. Google Gemini: Structure text into JSON (cloud-based, reliable)
 *
 * Features:
 * - Automatic retry with exponential backoff
 * - Timeout protection
 * - API key validation
 * - Model availability check
 * - Comprehensive error logging
 * - Graceful fallback handling
 * - Input validation
 * - Memory-efficient processing
 */

private const val TAG = "AIModels"
private const val OCR_TIMEOUT_MS = 15000L // 15 seconds
private const val LLM_TIMEOUT_MS = 30000L // 30 seconds
private const val MAX_RETRIES = 3
private const val MAX_TEXT_LENGTH = 2000

// IMPORTANT: Replace with your actual Gemini API Key
private const val GEMINI_API_KEY = "AIzaSyDzKdT6p0WpoiU8C6koAIdIZMVwC_7ZGkI"

/**
 * Step 1: ML Kit OCR with timeout and retry
 * Extracts text from nutrition label image using Google's on-device ML Kit
 */
suspend fun performOcrInference(bitmap: Bitmap): String {
    return withContext(Dispatchers.IO) {
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "🚀 STARTING OCR PROCESS")
        Log.d(TAG, "═══════════════════════════════════════")

        var lastException: Exception? = null

        // Try up to MAX_RETRIES times
        repeat(MAX_RETRIES) { attempt ->
            try {
                Log.d(
                    TAG,
                    "📷 OCR attempt ${attempt + 1}/$MAX_RETRIES - Starting ML Kit text recognition"
                )

                // Validate input
                if (bitmap.width <= 0 || bitmap.height <= 0) {
                    throw IllegalArgumentException("Invalid bitmap dimensions: ${bitmap.width}x${bitmap.height}")
                }

                // Process with timeout
                val result = withTimeoutOrNull(OCR_TIMEOUT_MS) {
                    processOcrWithMLKit(bitmap)
                }

                if (result != null && result.isNotBlank()) {
                    Log.d(TAG, "✅ OCR SUCCESS: Extracted ${result.length} characters")
                    Log.d(TAG, "Preview: ${result.take(150).replace("\n", " ")}...")
                    Log.d(TAG, "═══════════════════════════════════════")
                    return@withContext result
                } else {
                    Log.w(TAG, "⚠️ OCR returned empty text on attempt ${attempt + 1}")
                }

            } catch (e: Exception) {
                lastException = e
                Log.e(
                    TAG,
                    "❌ OCR attempt ${attempt + 1} failed: ${e.javaClass.simpleName}: ${e.message}"
                )
                e.printStackTrace()

                if (attempt < MAX_RETRIES - 1) {
                    val delayMs = (attempt + 1) * 500L // Exponential backoff
                    Log.d(TAG, "⏳ Retrying in ${delayMs}ms...")
                    delay(delayMs)
                }
            }
        }

        // All retries failed - return error message with context
        Log.e(TAG, "❌ ALL OCR ATTEMPTS FAILED after $MAX_RETRIES tries")
        Log.e(TAG, "═══════════════════════════════════════")
        return@withContext createOcrErrorText(lastException)
    }
}

/**
 * Internal ML Kit processing
 */
private suspend fun processOcrWithMLKit(bitmap: Bitmap): String {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    try {
        // Convert to InputImage
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        // Process image
        val visionText = recognizer.process(inputImage).await()

        // Extract and clean text
        val rawText = visionText.text
        val cleanedText = rawText
            .lines()
            .filter { it.isNotBlank() }
            .joinToString("\n")

        Log.d(TAG, "📝 ML Kit processed ${visionText.textBlocks.size} text blocks")

        return cleanedText

    } finally {
        // Always close recognizer
        try {
            recognizer.close()
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Failed to close recognizer: ${e.message}")
        }
    }
}

/**
 * Step 2: Gemini LLM with timeout, retry, and validation
 * Converts extracted text into structured nutrition JSON
 */
suspend fun performLlmInference(text: String, systemPrompt: String): String {
    return withContext(Dispatchers.IO) {
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "🤖 STARTING GEMINI ANALYSIS")
        Log.d(TAG, "═══════════════════════════════════════")

        var lastException: Exception? = null

        // Validate API key
        if (GEMINI_API_KEY.isBlank() || GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE") {
            val errorMsg = "❌ GEMINI API KEY NOT CONFIGURED!"
            Log.e(TAG, errorMsg)
            Log.e(TAG, "Please set your API key in AIModels.kt")
            Log.e(TAG, "═══════════════════════════════════════")
            return@withContext createFallbackJson("API key not configured")
        }

        // Validate input
        if (text.isBlank()) {
            Log.w(TAG, "⚠️ Empty text provided to LLM, returning fallback")
            Log.e(TAG, "═══════════════════════════════════════")
            return@withContext createFallbackJson("No text to analyze")
        }

        // Check for OCR errors
        if (text.contains("OCR_ERROR") || text.contains("SCAN_FAILED")) {
            Log.w(TAG, "⚠️ OCR error detected in text, returning fallback")
            Log.e(TAG, "═══════════════════════════════════════")
            return@withContext createFallbackJson("OCR failed - please try again")
        }

        // Truncate if needed
        val processedText = if (text.length > MAX_TEXT_LENGTH) {
            Log.d(TAG, "✂️ Truncating text from ${text.length} to $MAX_TEXT_LENGTH chars")
            text.take(MAX_TEXT_LENGTH)
        } else {
            text
        }

        // Try up to MAX_RETRIES times
        repeat(MAX_RETRIES) { attempt ->
            try {
                Log.d(TAG, "🔄 LLM attempt ${attempt + 1}/$MAX_RETRIES - Calling Google Gemini")

                val result = withTimeoutOrNull(LLM_TIMEOUT_MS) {
                    callGeminiAPI(processedText)
                }

                if (result != null && isValidJson(result)) {
                    Log.d(TAG, "✅ LLM SUCCESS: Generated valid JSON (${result.length} chars)")
                    Log.d(TAG, "═══════════════════════════════════════")
                    return@withContext result
                } else {
                    Log.w(TAG, "⚠️ LLM returned invalid JSON on attempt ${attempt + 1}")
                    if (result != null) {
                        Log.w(TAG, "Response preview: ${result.take(100)}")
                    }
                }

            } catch (e: Exception) {
                lastException = e
                Log.e(
                    TAG,
                    "❌ LLM attempt ${attempt + 1} failed: ${e.javaClass.simpleName}"
                )
                Log.e(TAG, "Error message: ${e.message}")
                e.printStackTrace()

                // Check for specific error types
                when {
                    e.message?.contains("API key", ignoreCase = true) == true -> {
                        Log.e(TAG, "🚫 API KEY ERROR - Please check your Gemini API key")
                        Log.e(TAG, "═══════════════════════════════════════")
                        return@withContext createFallbackJson("Invalid API key")
                    }

                    e.message?.contains("not found", ignoreCase = true) == true -> {
                        Log.e(TAG, "🚫 MODEL NOT FOUND ERROR")
                        Log.e(TAG, "Attempting to use alternative model...")
                    }

                    e.message?.contains("quota", ignoreCase = true) == true -> {
                        Log.e(TAG, "🚫 QUOTA EXCEEDED - API rate limit reached")
                        Log.e(TAG, "═══════════════════════════════════════")
                        return@withContext createFallbackJson("API quota exceeded")
                    }
                }

                if (attempt < MAX_RETRIES - 1) {
                    val delayMs = (attempt + 1) * 1000L // Exponential backoff
                    Log.d(TAG, "⏳ Retrying in ${delayMs}ms...")
                    delay(delayMs)
                }
            }
        }

        // All retries failed
        Log.e(TAG, "❌ ALL LLM ATTEMPTS FAILED after $MAX_RETRIES tries")
        Log.e(TAG, "Last error: ${lastException?.message}")
        Log.e(TAG, "═══════════════════════════════════════")

        return@withContext createFallbackJson(
            "Analysis failed - ${lastException?.message?.take(100) ?: "Unknown error"}"
        )
    }
}

/**
 * Internal Gemini API call with improved error handling
 */
private suspend fun callGeminiAPI(text: String): String {
    Log.d(TAG, "🔧 Initializing Gemini model...")

    try {
        // Try gemini-1.5-flash first
        val model = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.2f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )

        Log.d(TAG, "📡 Model initialized: gemini-1.5-flash")

        val prompt = buildGeminiPrompt(text)
        Log.d(TAG, "📤 Sending prompt to Gemini (${prompt.length} chars)...")

        val response = model.generateContent(prompt)
        val responseText = response.text ?: ""

        if (responseText.isBlank()) {
            throw IllegalStateException("Gemini returned empty response")
        }

        Log.d(TAG, "📥 Gemini response received: ${responseText.length} chars")
        Log.d(TAG, "Response preview: ${responseText.take(100)}...")

        // Extract JSON from response
        return extractJsonFromResponse(responseText)

    } catch (e: Exception) {
        Log.e(TAG, "❌ Gemini API call failed")
        Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
        Log.e(TAG, "Error message: ${e.message}")

        // Try alternative model
        if (e.message?.contains("not found", ignoreCase = true) == true) {
            Log.d(TAG, "🔄 Trying alternative model: gemini-pro")
            return callGeminiAPIWithFallbackModel(text)
        }

        throw e
    }
}

/**
 * Fallback to gemini-pro if gemini-1.5-flash is not available
 */
private suspend fun callGeminiAPIWithFallbackModel(text: String): String {
    try {
        val model = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.2f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )

        Log.d(TAG, "📡 Fallback model initialized: gemini-pro")

        val prompt = buildGeminiPrompt(text)
        val response = model.generateContent(prompt)
        val responseText = response.text ?: ""

        if (responseText.isBlank()) {
            throw IllegalStateException("Gemini returned empty response")
        }

        Log.d(TAG, "📥 Fallback model response received: ${responseText.length} chars")
        return extractJsonFromResponse(responseText)

    } catch (e: Exception) {
        Log.e(TAG, "❌ Fallback model also failed: ${e.message}")
        throw e
    }
}

/**
 * Builds optimized prompt for Gemini
 */
private fun buildGeminiPrompt(text: String): String {
    return """
You are a nutrition label analyzer. Extract data from this text and return ONLY valid JSON.

EXTRACTED TEXT:
$text

RETURN THIS EXACT JSON STRUCTURE (numbers only, no units):
{
  "productName": "inferred product name or 'Food Product'",
  "calories": 0,
  "sugar": 0,
  "sodium": 0,
  "totalFat": 0,
  "saturatedFat": 0,
  "fiber": 0,
  "protein": 0,
  "allergens": [],
  "watchlistIngredients": []
}

RULES:
1. Return ONLY the JSON object
2. No markdown, no backticks, no explanations
3. All numbers must be numeric values (not strings)
4. Use 0 if value not found
5. allergens array: check for Milk, Eggs, Peanuts, Tree Nuts, Soy, Wheat, Fish, Shellfish
6. watchlistIngredients: check for Aspartame, Red 40, High Fructose Corn Syrup

Extract the nutrition data now:
    """.trimIndent()
}

/**
 * Extracts JSON from Gemini response
 */
private fun extractJsonFromResponse(response: String): String {
    // Remove markdown formatting
    var cleaned = response
        .replace("```json", "")
        .replace("```", "")
        .trim()

    // Find JSON boundaries
    val start = cleaned.indexOf('{')
    val end = cleaned.lastIndexOf('}')

    if (start >= 0 && end > start) {
        cleaned = cleaned.substring(start, end + 1)
    } else {
        throw IllegalStateException("No valid JSON found in response")
    }

    // Basic validation
    if (!cleaned.contains("\"productName\"")) {
        throw IllegalStateException("Invalid JSON structure - missing required fields")
    }

    return cleaned
}

/**
 * Validates JSON structure
 */
private fun isValidJson(json: String): Boolean {
    if (json.isBlank()) return false

    val trimmed = json.trim()
    if (!trimmed.startsWith('{') || !trimmed.endsWith('}')) return false

    // Check for required fields
    val requiredFields = listOf("productName", "calories", "allergens")
    return requiredFields.all { field ->
        trimmed.contains("\"$field\"")
    }
}

/**
 * Creates fallback JSON when analysis fails
 */
private fun createFallbackJson(reason: String = "Analysis failed"): String {
    Log.w(TAG, "Creating fallback JSON: $reason")

    return """
{
  "productName": "Scan Again - $reason",
  "calories": 0,
  "sugar": 0,
  "sodium": 0,
  "totalFat": 0,
  "saturatedFat": 0,
  "fiber": 0,
  "protein": 0,
  "allergens": [],
  "watchlistIngredients": [],
  "_error": "$reason"
}
    """.trimIndent()
}

/**
 * Creates error text when OCR fails
 */
private fun createOcrErrorText(exception: Exception?): String {
    val errorMsg = when {
        exception?.message?.contains("timeout", ignoreCase = true) == true ->
            "OCR timeout - image too complex"

        exception is IllegalArgumentException ->
            "Invalid image format"

        else ->
            "OCR failed - ${exception?.message ?: "Unknown error"}"
    }

    return "OCR_ERROR: $errorMsg\n\nPlease try:\n- Better lighting\n- Clearer photo\n- Closer to label\n- Hold steady"
}

/**
 * Health check for AI services
 */
suspend fun performAIHealthCheck(): Pair<Boolean, String> {
    return withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Performing AI services health check...")

            // Check ML Kit
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.close()
            Log.d(TAG, "✅ ML Kit OCR: OK")

            // Check Gemini connectivity
            val testModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = GEMINI_API_KEY
            )

            val testResponse = withTimeoutOrNull(5000L) {
                testModel.generateContent("Say 'OK'")
            }

            if (testResponse?.text?.contains("OK", ignoreCase = true) == true) {
                Log.d(TAG, "✅ Gemini API: OK")
                return@withContext Pair(true, "All AI services operational")
            } else {
                Log.w(TAG, "⚠️ Gemini API: Degraded")
                return@withContext Pair(false, "Gemini API not responding correctly")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Health check failed: ${e.message}")
            return@withContext Pair(false, "AI services unavailable: ${e.message}")
        }
    }
}
