package com.example.cattletrackingapp.ui.screens.DetailPages.BullDetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.CowVaccine
import com.example.cattletrackingapp.data.remote.Models.CowVaccineWithName
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowVaccinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BullDetailViewModel @Inject constructor(
    private val bullRepo: BullsRepository,
    private val calfRepo: CalvesRepository,
    private val cowVaccineRepo: CowVaccinesRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val bull: BullUi? = null,
        val calfList: List<Calf> = emptyList(),
        val cowVaccineList: List<CowVaccineWithName> = emptyList(),
        val updateSuccess: Boolean? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState

    var saveState by mutableStateOf(UiState())
        private set


    fun loadBullDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            try {
                val bull = bullRepo.getBullById(id)

                // Fetch calves using local or remote source
                val calves = bull?.let { bullData ->
                    calfRepo.allCalvesByParentTag(bullData.id)
                } ?: emptyList()

                // Try to fetch vaccines, ignore errors if offline
                val vaccines = try {
                    cowVaccineRepo.getCowVaccineByAnimalId(id)
                } catch (e: Exception) {
                    emptyList()
                }

                _uiState.update {
                    it.copy(
                        loading = false,
                        bull = bull?.toUi(),
                        calfList = calves,
                        cowVaccineList = vaccines
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loading = false, error = e.message ?: "Something went wrong")
                }
            }

        }
    }

    fun updateBull(bullUi: BullUi) {
        val bull = bullUi.toModel()
        saveState = saveState.copy(loading = true, updateSuccess = null, error = null)

        viewModelScope.launch {
            runCatching { bullRepo.updateBull(bull) }
                .onSuccess {
                    if (it) {
                        saveState = saveState.copy(loading = false, updateSuccess = true)

                        // also update detail UiState so UI refreshes with new values
                        _uiState.update { it.copy(bull = bullUi) }
                    } else {
                        saveState = saveState.copy(
                            loading = false,
                            updateSuccess = false,
                            error = "Failed to save cow"
                        )
                    }
                }
                .onFailure { e ->
                    saveState = saveState.copy(
                        loading = false,
                        updateSuccess = false,
                        error = e.message ?: "Unknown error"
                    )
                }
        }
    }
}