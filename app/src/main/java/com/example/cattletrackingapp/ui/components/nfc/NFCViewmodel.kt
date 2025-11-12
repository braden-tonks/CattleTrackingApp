package com.example.cattletrackingapp.ui.components.nfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.components.CattleType
import com.example.cattletrackingapp.ui.components.nfc.AnimalUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NFCUiState(
    val animals: List<AnimalUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NFCViewModel @Inject constructor(
    private val cowRepo: CowsRepository,
    private val calfRepo: CalvesRepository,
    private val bullRepo: BullsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NFCUiState())
    val uiState: StateFlow<NFCUiState> = _uiState

    /**
     * Loads an animal dynamically depending on CattleType
     */
    fun loadAnimal(tagNumber: String, type: CattleType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Convert to AnimalUi inside each branch so the compiler knows the type
                val animalUi: AnimalUi? = when (type) {
                    CattleType.COW -> cowRepo.getCowById(tagNumber)?.toAnimalUi()
                    CattleType.CALF -> calfRepo.getCalfById(tagNumber)?.toAnimalUi()
                    CattleType.BULL -> bullRepo.fetchBullById(tagNumber)?.toAnimalUi()
                    CattleType.ALL -> findAnimalAcrossAll(tagNumber)
                }

                _uiState.update {
                    if (animalUi != null) {
                        val updatedList = it.animals.toMutableList().apply {
                            if (none { existing -> existing.id == animalUi.id }) {
                                add(animalUi)
                            }
                        }
                        it.copy(animals = updatedList, isLoading = false)
                    } else {
                        it.copy(isLoading = false)
                    }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Searches across all repositories if type = ALL
     */
    private suspend fun findAnimalAcrossAll(tagNumber: String): AnimalUi? {
        val cow = cowRepo.getCowById(tagNumber)
        if (cow != null) return cow.toAnimalUi()

        val calf = calfRepo.getCalfById(tagNumber)
        if (calf != null) return calf.toAnimalUi()

        val bull = bullRepo.getBullById(tagNumber)
        if (bull != null) return bull.toAnimalUi()

        return null
    }

    fun clearState() {
        _uiState.update { NFCUiState() }
    }
}