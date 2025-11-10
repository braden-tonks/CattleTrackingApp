package com.example.cattletrackingapp.ui.components

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
class NFCViewModel @Inject constructor(
    private val calfRepo: CalvesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalvesUiState())
    val uiState: StateFlow<CalvesUiState> = _uiState

    /**
     * Loads a calf from repository based on the tag ID
     */
    fun loadCalves(tagNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val calf = calfRepo.getCalfById(tagNumber)
                _uiState.update {
                    it.copy(
                        calves = listOfNotNull(calf),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearState() {
        _uiState.update { CalvesUiState() }
    }
}
