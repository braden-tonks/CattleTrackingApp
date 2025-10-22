package com.example.cattletrackingapp.ui.screens.vaccinations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.repository.VaccinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

// 1) UI model now includes description + notes
data class VaccineUi(
    val id: String,
    val name: String,
    val description: String? = null,
    val notes: String? = null,
    val vaccineId: String? = null
)

@HiltViewModel
class VaccinationsViewModel @Inject constructor(
    private val repo: VaccinesRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val vaccines: List<VaccineUi> = emptyList(),
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())
        private set

    init { refresh() }

    //function that ensures the VaccineId will display properly
    private fun displayIdOf(
        id: String?,
        vaccineNumber: Int?
    ): String? {
        return vaccineNumber?.toString()
            ?: id?.takeIf { it.isNotBlank() }?.take(8)
    }

    fun refresh() {
        uiState = UiState(loading = true)
        viewModelScope.launch {
            runCatching {

                repo.fetchVaccines().map { v ->
                    VaccineUi(
                        id = v.id.orEmpty(),
                        name = v.name,
                        description = v.description,     // keep nullable
                        notes = v.notes,                  // keep nullable
                        vaccineId = displayIdOf(v.id, v.vaccine_number)
                    )
                }

            }.onSuccess { list ->
                uiState = UiState(vaccines = list, loading = false)
            }.onFailure { e ->
                uiState = UiState(loading = false, error = e.message ?: "Failed to load")
            }
        }
    }

        fun addVaccine(
        name: String,
        description: String? = null,
        notes: String? = null,
        onDone: (ok: Boolean, err: String?) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                val ok = repo.addVaccine(
                    com.example.cattletrackingapp.data.model.Vaccine(
                        name = name,
                        description = description,
                        notes = notes
                        // add farmer_id or other required fields here if your table demands them
                    )
                )
                ok
            }.onSuccess { ok ->
                if (ok) {
                    refresh()   // reload list so it appears
                    onDone(true, null)
                } else {
                    onDone(false, "Insert failed")
                }
            }.onFailure { e ->
                onDone(false, e.message ?: "Insert failed")
            }
        }
    }

}

