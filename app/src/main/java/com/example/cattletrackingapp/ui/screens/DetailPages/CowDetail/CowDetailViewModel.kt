package com.example.cattletrackingapp.ui.screens.cowdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.data.model.CowVaccine
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowVaccinesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail.CowUi
import com.example.cattletrackingapp.ui.screens.DetailPages.CowDetail.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
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
        val cowVaccineList: List<CowVaccine> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState


    fun loadCowDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            try {
                val cowDeferred = async { cowRepo.getCowById(id) }
                val calvesDeferred = async { calfRepo.getCalvesByDamId(id) }
                val vaccinesDeferred = async { cowVaccineRepo.getCowVaccineByAnimalId(id) }

                val cow = cowDeferred.await()
                val calves = calvesDeferred.await()
                val vaccines = vaccinesDeferred.await()

                val cowUi = cow?.toUi()

                _uiState.update {
                    it.copy(
                        loading = false,
                        cow = cowUi,
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