package com.example.cattletrackingapp.ui.screens.CalfDetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.data.repository.CalvesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalfDetailUiState(
    val isLoading: Boolean = false,
    val calf: Calf? = null,
    val error: String? = null
)

@HiltViewModel
class CalfDetailViewModel @Inject constructor(
    private val calfRepo: CalvesRepository
) : ViewModel() {
    var uiState by mutableStateOf(CalfDetailUiState())
        private set

    fun load(id: String) {
        // guard: don't re-fetch same id if already loaded
        if (uiState.calf?.id == id && !uiState.isLoading) return

        uiState = CalfDetailUiState(isLoading = true)
        viewModelScope.launch {
            runCatching { calfRepo.getCalfById(id) }
                .onSuccess { calf ->
                    uiState = CalfDetailUiState(
                        isLoading = false,
                        calf = calf,
                        error = if (calf == null) "Calf not found" else null
                    )
                }
                .onFailure { e ->
                    uiState = CalfDetailUiState(isLoading = false, error = e.message ?: "Failed to load")
                }
        }
    }
}