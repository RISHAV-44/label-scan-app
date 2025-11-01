package com.runanywhere.startup_hackathon20.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.runanywhere.startup_hackathon20.models.ScanResult
import kotlinx.coroutines.tasks.await
import java.util.Date

data class ScanHistoryItem(
    val id: String = "",
    val userId: String = "",
    val scanResult: ScanResult = ScanResult(),
    val timestamp: Date = Date(),
    val imageUrl: String? = null
)

class ScanHistoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val scansCollection = firestore.collection("scans")

    /**
     * Save a scan result to Firestore
     */
    suspend fun saveScan(
        userId: String,
        scanResult: ScanResult
    ): Result<String> {
        return try {
            Log.d("ScanHistoryRepo", "Saving scan for user: $userId")

            val scanData = hashMapOf(
                "userId" to userId,
                "productName" to scanResult.productName,
                "calories" to scanResult.calories,
                "sugar" to scanResult.sugar,
                "sodium" to scanResult.sodium,
                "totalFat" to scanResult.totalFat,
                "saturatedFat" to scanResult.saturatedFat,
                "fiber" to scanResult.fiber,
                "protein" to scanResult.protein,
                "allergens" to scanResult.allergens,
                "watchlistIngredients" to scanResult.watchlistIngredients,
                "timestamp" to Date()
            )

            Log.d("ScanHistoryRepo", "Scan data prepared: $scanData")

            val documentRef = scansCollection.add(scanData).await()
            Log.d("ScanHistoryRepo", "Scan saved with ID: ${documentRef.id}")

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Log.e("ScanHistoryRepo", "Failed to save scan: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get scan history for a user
     */
    suspend fun getUserScans(
        userId: String,
        limit: Int = 20
    ): Result<List<ScanHistoryItem>> {
        return try {
            Log.d("ScanHistoryRepo", "Fetching scans for user: $userId")

            val snapshot = scansCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            Log.d("ScanHistoryRepo", "Found ${snapshot.documents.size} scans")

            val scans = snapshot.documents.mapNotNull { doc ->
                try {
                    ScanHistoryItem(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        scanResult = ScanResult(
                            productName = doc.getString("productName"),
                            calories = doc.getLong("calories")?.toInt(),
                            sugar = doc.getLong("sugar")?.toInt(),
                            sodium = doc.getLong("sodium")?.toInt(),
                            totalFat = doc.getLong("totalFat")?.toInt(),
                            saturatedFat = doc.getLong("saturatedFat")?.toInt(),
                            fiber = doc.getLong("fiber")?.toInt(),
                            protein = doc.getLong("protein")?.toInt(),
                            allergens = doc.get("allergens") as? List<String> ?: emptyList(),
                            watchlistIngredients = doc.get("watchlistIngredients") as? List<String>
                                ?: emptyList()
                        ),
                        timestamp = doc.getDate("timestamp") ?: Date(),
                        imageUrl = doc.getString("imageUrl")
                    )
                } catch (e: Exception) {
                    Log.e("ScanHistoryRepo", "Error parsing scan document: ${e.message}")
                    null
                }
            }

            Log.d("ScanHistoryRepo", "Successfully parsed ${scans.size} scans")
            Result.success(scans)
        } catch (e: Exception) {
            Log.e("ScanHistoryRepo", "Failed to fetch scans: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get a single scan by ID
     */
    suspend fun getScanById(scanId: String): Result<ScanHistoryItem?> {
        return try {
            Log.d("ScanHistoryRepo", "Fetching scan by ID: $scanId")
            val doc = scansCollection.document(scanId).get().await()

            if (doc.exists()) {
                Log.d("ScanHistoryRepo", "Found scan with ID: $scanId")
                val scanItem = ScanHistoryItem(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    scanResult = ScanResult(
                        productName = doc.getString("productName"),
                        calories = doc.getLong("calories")?.toInt(),
                        sugar = doc.getLong("sugar")?.toInt(),
                        sodium = doc.getLong("sodium")?.toInt(),
                        totalFat = doc.getLong("totalFat")?.toInt(),
                        saturatedFat = doc.getLong("saturatedFat")?.toInt(),
                        fiber = doc.getLong("fiber")?.toInt(),
                        protein = doc.getLong("protein")?.toInt(),
                        allergens = doc.get("allergens") as? List<String> ?: emptyList(),
                        watchlistIngredients = doc.get("watchlistIngredients") as? List<String>
                            ?: emptyList()
                    ),
                    timestamp = doc.getDate("timestamp") ?: Date(),
                    imageUrl = doc.getString("imageUrl")
                )
                Log.d("ScanHistoryRepo", "Successfully parsed scan with ID: $scanId")
                Result.success(scanItem)
            } else {
                Log.d("ScanHistoryRepo", "Scan with ID $scanId not found")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("ScanHistoryRepo", "Failed to fetch scan: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a scan by ID
     */
    suspend fun deleteScan(scanId: String): Result<Unit> {
        return try {
            Log.d("ScanHistoryRepo", "Deleting scan with ID: $scanId")
            scansCollection.document(scanId).delete().await()
            Log.d("ScanHistoryRepo", "Scan with ID $scanId deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ScanHistoryRepo", "Failed to delete scan: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Delete all scans for a user
     */
    suspend fun deleteUserScans(userId: String): Result<Unit> {
        return try {
            Log.d("ScanHistoryRepo", "Deleting all scans for user: $userId")
            val snapshot = scansCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            Log.d("ScanHistoryRepo", "All scans for user $userId deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ScanHistoryRepo", "Failed to delete scans: ${e.message}", e)
            Result.failure(e)
        }
    }
}
