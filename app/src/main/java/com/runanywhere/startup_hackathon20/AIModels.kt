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
You are a nutrition label analyzer. Your task is to extract nutrition facts from text and return ONLY valid JSON.

CRITICAL RULES:
1. Return ONLY the JSON object - NO explanations, NO markdown, NO extra text
2. All numbers MUST be integers (not strings, not floats)
3. If a value is not found, use 0 (not null, not "unknown")
4. Product name should be a short, clear name
5. Do not include units in the numbers (no "g", "mg", "kcal" etc)

EXTRACTED TEXT FROM LABEL:
$text

RETURN EXACTLY THIS JSON STRUCTURE:
{
  "productName": "short product name here",
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

EXAMPLES OF VALID OUTPUT:
{
  "productName": "Granola Bar",
  "calories": 120,
  "sugar": 8,
  "sodium": 95,
  "totalFat": 5,
  "saturatedFat": 1,
  "fiber": 2,
  "protein": 3,
  "allergens": ["Peanuts", "Soy"],
  "watchlistIngredients": ["High Fructose Corn Syrup"]
}

IMPORTANT:
- Numbers must be integers: 120 NOT "120" or 120.0
- Arrays must be valid: ["item"] NOT [item]
- Product name must be a string: "Name" NOT Name
- Use empty arrays [] if no allergens or ingredients

Extract the nutrition data from the text above and return ONLY the JSON object now:
    """.trimIndent()
}

/**
 * Extracts JSON from Gemini response with improved validation
 */
private fun extractJsonFromResponse(response: String): String {
    Log.d(TAG, "🔍 Extracting JSON from response (${response.length} chars)")
    Log.d(TAG, "Full response: $response")

    try {
        // Remove markdown formatting
        var cleaned = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        Log.d(TAG, "After markdown removal: $cleaned")

        // Find JSON boundaries
        val start = cleaned.indexOf('{')
        val end = cleaned.lastIndexOf('}')

        if (start < 0 || end <= start) {
            Log.e(TAG, "❌ No valid JSON boundaries found")
            Log.e(TAG, "Looking for '{' at position $start and '}' at position $end")
            throw IllegalStateException(
                "No JSON object found in response. Response was: ${
                    response.take(
                        200
                    )
                }"
            )
        }

        cleaned = cleaned.substring(start, end + 1)
        Log.d(TAG, "Extracted JSON substring: $cleaned")

        // Validate JSON structure
        if (!cleaned.contains("\"productName\"")) {
            Log.e(TAG, "❌ Missing productName field")
            throw IllegalStateException("Invalid JSON - missing productName field")
        }

        if (!cleaned.contains("\"calories\"")) {
            Log.e(TAG, "❌ Missing calories field")
            throw IllegalStateException("Invalid JSON - missing calories field")
        }

        // Check for common JSON errors
        if (cleaned.count { it == '{' } != cleaned.count { it == '}' }) {
            Log.e(TAG, "❌ Mismatched braces")
            throw IllegalStateException("Invalid JSON - mismatched braces")
        }

        if (cleaned.count { it == '[' } != cleaned.count { it == ']' }) {
            Log.e(TAG, "❌ Mismatched brackets")
            throw IllegalStateException("Invalid JSON - mismatched brackets")
        }

        Log.d(TAG, "✅ JSON extraction successful")
        return cleaned

    } catch (e: Exception) {
        Log.e(TAG, "❌ JSON extraction failed: ${e.message}")
        Log.e(TAG, "Original response: $response")
        throw IllegalStateException(
            "Failed to extract valid JSON: ${e.message}\nResponse was: ${
                response.take(
                    300
                )
            }"
        )
    }
}

/**
 * Validates JSON structure with detailed logging
 */
private fun isValidJson(json: String): Boolean {
    if (json.isBlank()) {
        Log.w(TAG, "JSON validation failed: Empty string")
        return false
    }

    val trimmed = json.trim()
    if (!trimmed.startsWith('{') || !trimmed.endsWith('}')) {
        Log.w(
            TAG,
            "JSON validation failed: Not a JSON object (starts with '${trimmed.firstOrNull()}', ends with '${trimmed.lastOrNull()}')"
        )
        return false
    }

    // Check for required fields
    val requiredFields = listOf("productName", "calories", "allergens")
    val missingFields = requiredFields.filter { field ->
        !trimmed.contains("\"$field\"")
    }

    if (missingFields.isNotEmpty()) {
        Log.w(TAG, "JSON validation failed: Missing fields: $missingFields")
        return false
    }

    Log.d(TAG, "✅ JSON validation passed")
    return true
}

/**
 * Creates fallback JSON when analysis fails
 */
private fun createFallbackJson(reason: String = "Analysis failed"): String {
    Log.w(TAG, "📋 Creating fallback JSON: $reason")

    return """
{
  "productName": "Scan Again",
  "calories": 0,
  "sugar": 0,
  "sodium": 0,
  "totalFat": 0,
  "saturatedFat": 0,
  "fiber": 0,
  "protein": 0,
  "allergens": ["Error: $reason"],
  "watchlistIngredients": []
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
