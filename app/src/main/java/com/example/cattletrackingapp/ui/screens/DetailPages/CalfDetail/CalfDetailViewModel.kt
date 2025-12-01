package com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Bull
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.Cow
import com.example.cattletrackingapp.data.remote.Models.CowVaccine
import com.example.cattletrackingapp.data.remote.Models.CowVaccineWithName
import com.example.cattletrackingapp.data.remote.Models.Weight
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowVaccinesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.data.repository.WeightsRepository
import com.example.cattletrackingapp.ui.screens.AddPages.AddCalf.AddCalfUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CalfDetailViewModel @Inject constructor(
    private val calfRepo: CalvesRepository,
    private val cowRepo: CowsRepository,
    private val bullRepo: BullsRepository,
    private val cowVaccineRepo: CowVaccinesRepository,
    private val weightRepo: WeightsRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val calf: CalfUi? = null,
        val cowVaccineList: List<CowVaccineWithName> = emptyList(),
        val weightList: List<Weight> = emptyList(),
        val updateSuccess: Boolean? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState

    var saveState by mutableStateOf(UiState())
        private set

    var cows by mutableStateOf<List<Cow>>(emptyList())
        private set

    var bulls by mutableStateOf<List<Bull>>(emptyList())
        private set



    init {
        loadCows()
        loadBulls()
    }


    fun loadCalfDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            try {
                val calf = calfRepo.getCalfById(id)


                // Try to fetch vaccines, ignore errors if offline
                val vaccines = try {
                    cowVaccineRepo.getCowVaccineByAnimalId(id)
                } catch (e: Exception) {
                    emptyList()
                }

                val weights = try {
                    weightRepo.getWeightById(id)
                } catch (e: Exception) {
                    emptyList()
                }
                println("SyncDebug: Found ${weights.size} weights for calf $id")

                _uiState.update {
                    it.copy(
                        loading = false,
                        calf = calf?.toUi(),
                        cowVaccineList = vaccines,
                        weightList = weights
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loading = false, error = e.message ?: "Something went wrong")
                }
            }

        }
    }

    private fun loadCows() {
        viewModelScope.launch {
            cowRepo.allCows.collect { cowList ->
                cows = cowList
            }
        }
    }

    private fun loadBulls() {
        viewModelScope.launch {
            bullRepo.allBulls.collect { bullList ->
                bulls = bullList
            }
        }
    }

    fun updateCalf(calfUi: CalfUi) {
        val calf = calfUi.toModel()
        saveState = saveState.copy(loading = true, updateSuccess = null, error = null)

        viewModelScope.launch {
            runCatching { calfRepo.updateCalf(calf) }
                .onSuccess {
                    if (it) {
                        saveState = saveState.copy(loading = false, updateSuccess = true)

                        // also update detail UiState so UI refreshes with new values
                        _uiState.update { it.copy(calf = calfUi) }
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