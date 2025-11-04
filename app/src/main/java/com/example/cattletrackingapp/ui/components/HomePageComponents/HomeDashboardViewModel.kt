package com.example.cattletrackingapp.ui.components.HomePageComponents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeDashboardUiState(
    val totalCattle: Int = 0,
    val totalCows: Int = 0,
    val totalCalves: Int = 0,
    val totalBulls: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)


@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
    private val cowRepo: CowsRepository,
    private val calfRepo: CalvesRepository,
    private val bullRepo: BullsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeDashboardUiState())
    val uiState: StateFlow<HomeDashboardUiState> = _uiState

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val countList = loadCattleCount()

                _uiState.update {
                    it.copy(
                        totalCattle = countList[0],
                        totalBulls = countList[1],
                        totalCows = countList[2],
                        totalCalves = countList[3],
                        isLoading = false,
                        error = null
                    )
                }



            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun loadCattleCount(): List<Int> {
        val bullCount = bullRepo.getBullCount() ?: 0
        val cowCount = cowRepo.getCowCount() ?: 0
        val calfCount = calfRepo.getCalfCount() ?: 0

        val totalCattle = bullCount + cowCount + calfCount
        val countList = listOf(totalCattle, bullCount, cowCount, calfCount)

        return countList
    }
}