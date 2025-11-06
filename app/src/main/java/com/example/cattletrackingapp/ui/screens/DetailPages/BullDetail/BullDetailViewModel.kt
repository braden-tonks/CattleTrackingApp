package com.example.cattletrackingapp.ui.screens.DetailPages.BullDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.CowVaccine
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowVaccinesRepository
import com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
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
        val cowVaccineList: List<CowVaccine> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState


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
}