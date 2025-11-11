package com.example.cattletrackingapp.ui.screens.DetailPages.CalfDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.CowVaccine
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowVaccinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CalfDetailViewModel @Inject constructor(
    private val calfRepo: CalvesRepository,
    private val cowVaccineRepo: CowVaccinesRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val calf: CalfUi? = null,
        val cowVaccineList: List<CowVaccine> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState


    fun loadCalfDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            try {
                val calfDeferred = async { calfRepo.getCalfById(id) }
                val vaccinesDeferred = async { cowVaccineRepo.getCowVaccineByAnimalId(id) }

                val calf = calfDeferred.await()
                val vaccines = vaccinesDeferred.await()

                val calfUi = calf?.toUi()

                _uiState.update {
                    it.copy(
                        loading = false,
                        calf = calfUi,
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