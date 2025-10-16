package com.example.cattletrackingapp.ui.screens.HerdList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.components.CattleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CattleListUiState(
    val animals: List<AnimalUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HerdListViewModel @Inject constructor(
    private val cowRepo: CowsRepository,
    private val calfRepo: CalvesRepository,
    private val bullRepo: BullsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CattleListUiState())
    val uiState: StateFlow<CattleListUiState> = _uiState


    fun loadAnimals(type: CattleType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = when (type) {
                    CattleType.ALL -> loadAllAnimals()
                    CattleType.COW -> cowRepo.fetchCattleList().map { it.toAnimalUi() }
                    CattleType.CALF -> calfRepo.fetchCalves().map { it.toAnimalUi() }
                    CattleType.BULL -> bullRepo.fetchBullsList().map { it.toAnimalUi() }
                }
                _uiState.update {
                    it.copy(
                        animals = result.sortedBy { a -> a.tagNumber.lowercase() },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun loadAllAnimals(): List<AnimalUi> {
        val cows = cowRepo.fetchCattleList().map { it.toAnimalUi() }
        val calves = calfRepo.fetchCalves().map { it.toAnimalUi() }
        val bulls = bullRepo.fetchBullsList().map { it.toAnimalUi() }

        return (cows + calves + bulls)
    }
}


