package com.runanywhere.startup_hackathon20.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.startup_hackathon20.data.ScanHistoryItem
import com.runanywhere.startup_hackathon20.data.ScanHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = ScanHistoryRepository()

    private val _recentScans = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val recentScans: StateFlow<List<ScanHistoryItem>> = _recentScans.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadRecentScans(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("HomeViewModel", "Loading scans for userId: $userId")

            val result = repository.getUserScans(userId)

            result.onSuccess { scans ->
                Log.d("HomeViewModel", "Loaded ${scans.size} scans successfully")
                _recentScans.value = scans
            }.onFailure { exception ->
                Log.e("HomeViewModel", "Failed to load scans: ${exception.message}")
                _recentScans.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    fun deleteScan(scanId: String) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Deleting scan: $scanId")
            repository.deleteScan(scanId)
        }
    }
}
