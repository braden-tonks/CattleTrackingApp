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

//data class CalvesUiState(
//    val calves: List<Calf> = emptyList(),
//    val isLoading: Boolean = false,
//    val error: String? = null,
//)



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
                val calves = calfRepo.fetchCalves()

                val calfStats = getData(calves)

                _uiState.update {
                    it.copy(calves = calfStats, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

//    fun toggleSortOrder() {
//        val newDescending = !_uiState.value.isDescending
//        val sorted = sortCalves(_uiState.value.calves, newDescending)
//
//        _uiState.update {
//            it.copy(
//                isDescending = newDescending,
//                calves = sorted
//            )
//        }
//    }

    private fun getData(calves: List<Calf>): List<Calf> {
        val values = mutableListOf<Calf>()

        val maxCalf = calves.maxByOrNull { it.current_weight ?: 0.0 }
        val minCalf = calves.minByOrNull { it.current_weight ?: 0.0 }

        listOfNotNull(maxCalf, minCalf).forEach { values.add(it) }

        return values
    }





//        return if (descending) {
//            calves.sortedByDescending { it.current_weight ?: 0.0 }
//        } else {
//            calves.sortedBy { it.current_weight ?: 0.0 }
//        }
//    }
}
