package com.example.cattletrackingapp.ui.screens.WeightModule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.data.repository.CalvesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalvesUiState(
    val calves: List<Calf> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WeightModuleViewModel @Inject constructor(
    private val calfRepo: CalvesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalvesUiState())
    val uiState: StateFlow<CalvesUiState> = _uiState

    fun loadCalves() {
        viewModelScope.launch {
            println("WeightModuleViewModel: loadCalves called")
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val calves = calfRepo.fetchCalves()
                    .sortedByDescending { it.current_weight ?: 0.0 }
                println("Fetched ${calves.size} calves")

                _uiState.update {
                    it.copy(calves = calves, isLoading = false)
                }
            } catch (e: Exception) {
                println("Error fetching calves: ${e.message}")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
