# ⚠️ CRITICAL: 0-Token Generation Issue

## The Problem

**Both Qwen 0.5B AND Meta Llama 3 8B are generating 0 tokens!**

```
Qwen 0.5B:     ✅ streamGenerate completed with 0 tokens
Llama 3 8B:    ✅ streamGenerate completed with 0 tokens
```

This isn't a model capability issue - it's a **prompt format or generation parameter issue**.

## Why This Happens

When a model generates **0 tokens**, it means:

1. **EOS (End-of-Sequence) token triggered immediately** - Model thinks the response is complete
2. **Chat template formatting issue** - The automatic template might be adding extra tokens
3. **Generation parameters too restrictive** - Max tokens set too low or stop sequences too
   aggressive
4. **Prompt triggers immediate completion** - The format makes the model think it's done

## Root Cause Analysis

Looking at the logs, the SDK is using automatic chat template formatting:

```
LlamaCppService: 🔧 Applying llama.cpp chat template (model-specific, automatic)
LlamaCppService: Using model's chat template (length: 2507)
LlamaCppService: Formatted prompt (first 500 chars): <|im_start|>user...
```

The chat template wraps our prompt in special tokens like:

```
<|im_start|>user
[our prompt]
<|im_end|>
<|im_start|>assistant
```

Then the model sees `<|im_start|>assistant` and might immediately output `<|im_end|>` (which is an
EOS token), resulting in 0 actual content tokens.

## The Fix: Simplified Direct Prompting

I've changed the approach to use **minimal, direct prompts** without complex formatting:

### Before (Complex)

```kotlin
val fullPrompt = """
    $systemPrompt
    
    Here is the text extracted from a nutrition label via OCR:
    
    $truncatedText
    
    Analyze this text and extract the nutrition information.
    Return ONLY a valid JSON object with no markdown formatting...
    [200+ more words of instructions]
""".trimIndent()
```

Result: **0 tokens** (model confused by complexity or hits EOS)

### After (Simple)

```kotlin
val fullPrompt = """Extract nutrition data as JSON from this text:

$truncatedText

Return JSON with: productName, calories, sugar, sodium, totalFat, saturatedFat, fiber, protein, allergens[], watchlistIngredients[]

JSON:"""
```

**Key changes:**

1. **No complex system prompt** - Just direct instruction
2. **Minimal structure** - One clear task
3. **Ends with "JSON:"** - Primes the model to start generating JSON
4. **No chat template confusion** - Simple text-in, text-out

## Why This Should Work

1. **Prompt completion bias** - Ending with "JSON:" makes the model want to continue with JSON
2. **Clear expectation** - Model knows exactly what to output
3. **Minimal tokens before generation** - Less chance of hitting context limits
4. **Direct format** - No special tokens confusing the model

## Alternative Solutions (If This Fails)

If the simplified prompt still generates 0 tokens, the issue might be:

### 1. Generation Parameters

The SDK might be using restrictive parameters:

- `max_tokens = 0` or very low
- `temperature = 0` causing deterministic but possibly empty output
- Stop sequences including common JSON characters

### 2. Model Not Fully Loaded

```bash
adb logcat | grep "Model loaded"
```

Look for:

```
✅ SUCCESS! Meta Llama 3 8B loaded and ready!
```

### 3. Memory Issues

The 8B model needs ~2GB RAM. Check:

```bash
adb logcat | grep -E "(OutOfMemory|OOM)"
```

### 4. Try Without Chat Template

We might need to bypass the automatic chat template entirely and use raw text generation.

## Testing the Fix

### 1. Install Updated App

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 2. Clear App Data (Fresh Start)

```bash
adb shell pm clear com.runanywhere.startup_hackathon20
```

### 3. Watch Logcat

```bash
adb logcat | grep -E "(AIModels|LlamaCppService|streamGenerate)"
```

### 4. Scan a Label

Look for:

```
AIModels: Sending prompt to local LLM (XXX chars)...
LlamaCppService: 🚀 streamGenerate called
```

**Success indicators:**

```
✅ LlamaCppService: ✅ streamGenerate completed with 50+ tokens
✅ AIModels: LLM response received (200+ chars)
```

**Failure indicators:**

```
❌ LlamaCppService: ✅ streamGenerate completed with 0 tokens
❌ AIModels: W LLM returned empty response
```

## If Still 0 Tokens

The problem is deeper in the RunAnywhere SDK. We need to:

### Option 1: Use a Pre-Prompt

Add text that forces the model to start generating:

```kotlin
val fullPrompt = """
Product label text: $truncatedText

Based on the above, here is the nutrition data in JSON format:
{
  "productName": "
"""
```

This forces the model to complete the JSON since it's already started.

### Option 2: Manual Generation Parameters

Try to access lower-level generation settings if the SDK allows it:

```kotlin
// Hypothetical - check if SDK supports this
RunAnywhere.generateStream(
    prompt = fullPrompt,
    maxTokens = 512,  // Ensure enough tokens allowed
    temperature = 0.7,
    stopSequences = listOf("<|end|>")  // Only stop on specific sequences
)
```

### Option 3: Switch to Completion Mode

Some models work better with completion (continuing text) than chat mode:

```kotlin
val fullPrompt = """
This is a nutrition label:
$truncatedText

Nutrition data in JSON:
{"productName":"
"""
```

The model then completes the JSON.

## Workaround: Use External API Temporarily

If local models continue failing, temporarily use a cloud API to prove the app works:

1. **Google Gemini** (we already added this before)
2. **OpenAI GPT-4** (requires API key)
3. **Anthropic Claude** (requires API key)

Then switch back to local once we figure out the generation issue.

## Build Status

✅ **BUILD SUCCESSFUL** in 5s

- Simplified prompt implemented
- Ready to test
- Should reduce EOS triggering

## What I Suspect

The core issue is likely one of these:

1. **Chat template conflict** - The automatic template is adding tokens that trigger EOS
2. **Generation parameters** - The SDK defaults are too restrictive
3. **Model expectations** - The models expect a specific format we're not using

The simplified prompt should help with #1 and #3. If it still fails, we need to investigate #2 (SDK
parameters).

## Next Steps

1. ✅ Install updated app
2. ✅ Test with simplified prompt
3. ⚠️ If still 0 tokens → Check SDK documentation for generation parameters
4. ⚠️ Consider filing an issue with RunAnywhere SDK if it's their bug
5. ⚠️ Use external API as fallback if needed

---

**Try the simplified prompt first. If it still generates 0 tokens, the issue is in the SDK's
generation configuration, not our code.**