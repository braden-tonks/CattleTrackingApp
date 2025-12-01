package com.example.cattletrackingapp.ui.screens.HerdList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.local.sync.SyncManager
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.components.CattleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class CattleListUiState(
    val allAnimals: List<AnimalUi> = emptyList(),
    val animals: List<AnimalUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HerdListViewModel @Inject constructor(
    private val cowRepo: CowsRepository,
    private val calfRepo: CalvesRepository,
    private val bullRepo: BullsRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CattleListUiState())
    val uiState: StateFlow<CattleListUiState> = _uiState


    init {
        // Start collecting data right away when ViewModel is created
        observeAllAnimals()
    }

    /** Automatically observe and merge all animals from local DB */
    private fun observeAllAnimals() {
        viewModelScope.launch {
            combine(
                cowRepo.allActiveCows,
                calfRepo.allActiveCalves,
                bullRepo.allActiveBulls
            ) { cows, calves, bulls ->
                val allAnimals = (
                        cows.map { it.toAnimalUi() } +
                                calves.map { it.toAnimalUi() } +
                                bulls.map { it.toAnimalUi() }
                        ).sortedWith(
                        compareBy<AnimalUi> {
                            it.tagNumber.filter { ch -> ch.isDigit() }
                                .takeIf { s -> s.isNotEmpty() }
                                ?.toIntOrNull() ?: Int.MAX_VALUE
                        }.thenBy { it.tagNumber.lowercase(Locale.getDefault()) }
                    )

                allAnimals
            }.collect { animals ->
                _uiState.update { it.copy(allAnimals = animals, animals = animals, isLoading = false, error = null) }
            }
        }
    }

    /** Optional manual sync when the page opens */
    fun syncOnLaunch() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                syncManager.syncAll()
                // Data automatically refreshes since we’re observing local DB
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /** Filter current animal list by selected tab */
    fun filterByType(type: CattleType) {
        viewModelScope.launch {
            val allAnimals = _uiState.value.allAnimals
            val filtered = when (type) {
                CattleType.ALL -> allAnimals
                CattleType.COW -> allAnimals.filter { it.type == CattleType.COW }
                CattleType.CALF -> allAnimals.filter { it.type == CattleType.CALF }
                CattleType.BULL -> allAnimals.filter { it.type == CattleType.BULL }
            }
            _uiState.update { it.copy(animals = filtered) }
        }
    }
}
