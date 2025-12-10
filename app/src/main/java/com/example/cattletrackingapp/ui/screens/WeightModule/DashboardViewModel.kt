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

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val calfRepo: CalvesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalvesUiState())
    val uiState: StateFlow<CalvesUiState> = _uiState

    fun loadCalves() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Fetch full herd
                val calves = calfRepo.fetchCalves()

                // Get 4 special calves for stats/cards
                val calfStats = getData(calves)

                _uiState.update {
                    it.copy(calves = calfStats, isLoading = false)
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // Returns the 4 special calves (heaviest, lightest, max avg gain, min avg gain)
    private fun getData(calves: List<Calf>): List<Calf> {
        val values = mutableListOf<Calf>()

        val maxCalf = calves.maxByOrNull { it.current_weight ?: 0.0 }
        val minCalf = calves.minByOrNull { it.current_weight ?: 0.0 }
        val maxAvgGainCalf = calves.maxByOrNull { it.avg_gain ?: 0.0 }
        val minAvgGainCalf = calves.minByOrNull { it.avg_gain ?: 0.0 }

        listOfNotNull(maxCalf, minCalf, maxAvgGainCalf, minAvgGainCalf).forEach { values.add(it) }

        return values
    }

    // Returns herd metrics (total number of calves and average weight)
    suspend fun getHerdMetrics(): List<Any> {
        val fullCalves = calfRepo.fetchCalves()
        return getHerdSize(fullCalves)
    }

    private fun getHerdSize(calves: List<Calf>): List<Any> {
        val values = mutableListOf<Any>()
        val totalNumCalves = calves.count()
        val avgCalfWeight = if (totalNumCalves > 0) {
            calves.sumOf { it.current_weight ?: 0.0 } / totalNumCalves
        } else 0.0

        values.add(totalNumCalves)
        values.add(avgCalfWeight)
        return values
    }
}