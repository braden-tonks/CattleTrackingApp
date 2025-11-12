package com.example.cattletrackingapp.ui.screens.WeightModule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Calf
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
    val error: String? = null,
    val isDescending: Boolean = true
)



@HiltViewModel
class WeightModuleViewModel @Inject constructor(
    private val calfRepo: CalvesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalvesUiState())
    val uiState: StateFlow<CalvesUiState> = _uiState

    fun loadCalves() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val calves = calfRepo.fetchCalves()

                val sortedCalves = sortCalves(calves, _uiState.value.isDescending)

                _uiState.update {
                    it.copy(calves = sortedCalves, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleSortOrder() {
        val newDescending = !_uiState.value.isDescending
        val sorted = sortCalves(_uiState.value.calves, newDescending)

        _uiState.update {
            it.copy(
                isDescending = newDescending,
                calves = sorted
            )
        }
    }

    private fun sortCalves(calves: List<Calf>, descending: Boolean): List<Calf> {
        return if (descending) {
            calves.sortedByDescending { it.current_weight ?: 0.0 }
        } else {
            calves.sortedBy { it.current_weight ?: 0.0 }
        }
    }
}
