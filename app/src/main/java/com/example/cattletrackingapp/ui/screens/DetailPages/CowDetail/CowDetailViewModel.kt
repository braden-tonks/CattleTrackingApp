package com.example.cattletrackingapp.ui.screens.cowdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.CowVaccineWithName
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowVaccinesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail.CowUi
import com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail.toModel
import com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CowDetailViewModel @Inject constructor(
    private val cowRepo: CowsRepository,
    private val calfRepo: CalvesRepository,
    private val cowVaccineRepo: CowVaccinesRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val cow: CowUi? = null,
        val calfList: List<Calf> = emptyList(),
        val cowVaccineList: List<CowVaccineWithName> = emptyList(),
        val updateSuccess: Boolean? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState

    var saveState by mutableStateOf(UiState())
        private set


    fun loadCowDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            try {
                val cow = cowRepo.getCowById(id)

                // Fetch calves using local or remote source
                val calves = cow?.let { cowData ->
                    calfRepo.allCalvesByParentTag(cowData.id)
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
                        cow = cow?.toUi(),
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

    fun updateCow(cowUi: CowUi) {
        val cow = cowUi.toModel()
        saveState = saveState.copy(loading = true, updateSuccess = null, error = null)

        viewModelScope.launch {
            runCatching { cowRepo.updateCow(cow) }
                .onSuccess {
                    if (it) {
                        saveState = saveState.copy(loading = false, updateSuccess = true)

                        // also update detail UiState so UI refreshes with new values
                        _uiState.update { it.copy(cow = cowUi) }
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