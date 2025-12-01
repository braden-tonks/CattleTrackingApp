package com.example.cattletrackingapp.ui.components.nfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.Weight
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.WeightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.inject.Inject

data class WeighInNfcUiState(
    val calf: Calf? = null,
    val isLoading: Boolean = false,
    val submitLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WeighInNfcViewModel @Inject constructor(
    private val calfRepo: CalvesRepository,
    private val weightsRepo: WeightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeighInNfcUiState())
    val uiState: StateFlow<WeighInNfcUiState> = _uiState

    /**
     * Load calf by tag (only calves supported)
     */
    fun loadCalfByTag(tagNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }
            try {
                val calf = calfRepo.getCalfById(tagNumber)
                if (calf != null) {
                    _uiState.update { it.copy(calf = calf, isLoading = false) }
                } else {
                    _uiState.update { it.copy(calf = null, isLoading = false, error = "Calf not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Failed loading calf") }
            }
        }
    }

    /**
     * Submit a new weight for the loaded calf.
     * Mirrors the behavior in your AddWeightViewModel:
     * - create Weight with calf_id and today's date
     * - add via weightsRepo.addWeight(...)
     * - call calfRepo.reloadCalfweight(...) to update calf's persisted weight
     */
    fun submitWeight(value: Double) {
        val calf = _uiState.value.calf ?: return
        val calfId = calf.id ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(submitLoading = true, error = null) }

            val weightEntry = Weight(
                id = null,
                cow_id = null,
                calf_id = calfId,
                bull_id = null,
                weight = value,
                date_weighed = LocalDate.now().toString(),
                created_at = null
            )

            val days = calf.birth_date?.let { birthStr ->
                parseDateSafe(birthStr)?.let { birthDate ->
                    java.time.temporal.ChronoUnit.DAYS.between(birthDate, LocalDate.now()).toInt()
                }
            } ?: 0

            val success = try {
                weightsRepo.addWeight(weightEntry)
                // reloadCalfweight returns Boolean in your code; keep same expectation
                calfRepo.reloadCalfweight(weightEntry, calfId, days)
            } catch (e: Exception) {
                false
            }

            if (success) {
                // update UI state with updated calf current weight
                val updatedCalf = calf.copy(current_weight = value)
                _uiState.update {
                    it.copy(
                        calf = updatedCalf,
                        submitLoading = false,
                        success = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(submitLoading = false, error = "Failed to submit weight. Please try again.")
                }
            }
        }
    }

    fun resetSuccessAndForm() {
        _uiState.update { WeighInNfcUiState() }
    }

    private fun parseDateSafe(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            OffsetDateTime.parse(dateStr).toLocalDate()
        } catch (_: Exception) {
            try {
                LocalDate.parse(dateStr.take(10))
            } catch (_: Exception) {
                null
            }
        }
    }
}