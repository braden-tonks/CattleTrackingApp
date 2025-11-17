package com.example.cattletrackingapp.ui.screens.WeightModule.addweight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.Weight
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.WeightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.inject.Inject

data class CalvesUiState(
    val calvesNotWeighedToday: List<Calf> = emptyList(),
    val calvesWeighedToday: List<Calf> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    // dialog state
    val dialogVisible: Boolean = false,
    val dialogCalf: Calf? = null,
    val submitLoading: Boolean = false
)

@HiltViewModel
class AddWeightViewModel @Inject constructor(
    private val calfRepo: CalvesRepository,
    private val weightsRepo: WeightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalvesUiState(isLoading = true))
    val uiState: StateFlow<CalvesUiState> = _uiState

    private var allCalves: List<Calf> = emptyList()
    private var allWeights: List<Weight> = emptyList()

    init {
        fetchAll()
    }

    private fun fetchAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                allCalves = calfRepo.fetchCalves()
                allWeights = try { weightsRepo.fetchWeights() } catch (e: Exception) { emptyList() }

                splitListsAndEmit()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed loading calves"
                )
            }
        }
    }

    private fun splitListsAndEmit() {
        val today = LocalDate.now()
        val weighedIdsToday = allWeights.mapNotNull { w ->
            val date = parseDateSafe(w.date_weighed)
            if (date == today) w.calf_id else null
        }.toSet()

        val (weighed, notWeighed) = allCalves.partition { calf ->
            calf.id != null && weighedIdsToday.contains(calf.id)
        }

        _uiState.value = _uiState.value.copy(
            calvesNotWeighedToday = notWeighed,
            calvesWeighedToday = weighed,
            isLoading = false,
            error = null
        )
    }

    private fun parseDateSafe(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            // try parsing ISO offset date/time first
            OffsetDateTime.parse(dateStr).toLocalDate()
        } catch (_: Exception) {
            try {
                // fallback: take first 10 chars (YYYY-MM-DD)
                LocalDate.parse(dateStr.take(10))
            } catch (_: Exception) {
                null
            }
        }
    }

    // UI actions
    fun onCalfClicked(calf: Calf) {
        _uiState.value = _uiState.value.copy(dialogVisible = true, dialogCalf = calf)
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(dialogVisible = false, dialogCalf = null, submitLoading = false)
    }

    fun submitWeight(weightValue: Double) {
        val calf = _uiState.value.dialogCalf ?: return
        val calfId = calf.id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(submitLoading = true)

            // Build Weight object. Fill cow_id/bull_id if you want; set date_weighed to today
            val weightEntry = Weight(
                id = null,
                cow_id = null,
                calf_id = calfId,
                bull_id = null,
                weight = weightValue,
                date_weighed = LocalDate.now().toString(),
                created_at = null
            )

            val days = calf.birth_date?.let { birthDateStr ->
            parseDateSafe(birthDateStr)?.let { birthDate ->
                java.time.temporal.ChronoUnit.DAYS.between(birthDate, LocalDate.now()).toInt()
            }
        } ?: 0



            val success = try {
                weightsRepo.addWeight(weightEntry)
                calfRepo.reloadCalfweight(weightEntry, calf.id, days)
            } catch (e: Exception) {
                false
            }

            if (success) {
                // update local lists without refetch
                // update calf's current_weight locally and move it to weighed list
                val updatedCalf = calf.copy(current_weight = weightValue)

                allCalves = allCalves.map { if (it.id == calfId) updatedCalf else it }

                // add new weight to local weights
                allWeights = allWeights + weightEntry

                splitListsAndEmit()
                _uiState.value = _uiState.value.copy(dialogVisible = false, dialogCalf = null, submitLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    submitLoading = false,
                    error = "Failed to submit weight. Please try again."
                )
            }
        }
    }
}
