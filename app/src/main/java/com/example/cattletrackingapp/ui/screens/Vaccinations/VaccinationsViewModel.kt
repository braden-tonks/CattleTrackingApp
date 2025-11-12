package com.example.cattletrackingapp.ui.screens.Vaccinations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowVaccinesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.data.repository.VaccinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

/** Vaccine row used by the UI. */
data class VaccineUi(
    val id: String,
    val name: String,
    val description: String? = null,
    val notes: String? = null,
    val vaccineId: String? = null
)

/** Minimal animal record for the Assign step. */
data class AnimalHit(val id: String, val tag: String, val gender: String? = null)

@HiltViewModel
class VaccinationsViewModel @Inject constructor(
    private val repo: VaccinesRepository,
    private val cowsRepo: CowsRepository,
    private val bullsRepo: BullsRepository,
    private val calvesRepo: CalvesRepository,
    val cowVaccinesRepo: CowVaccinesRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val vaccines: List<VaccineUi> = emptyList(),
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())
        private set

    /** Cached full lists (by group) for the Assign step. */
    var animalsByGroup: Map<TargetGroup, List<AnimalHit>> by mutableStateOf(emptyMap())
        private set

    // ADD near other vars
    var toastMessage by mutableStateOf<String?>(null)
        private set

    fun clearToast() { toastMessage = null }

    // CALL this from the Finish button
    fun logVaccinations(
        vaccines: List<String>,
        group: TargetGroup,
        animalIds: List<String>,
        dateGiven: String,
        remarks: String?,
        onDone: (Boolean) -> Unit = {}
    ) {
        if (vaccines.isEmpty() || animalIds.isEmpty()) {
            toastMessage = "Pick at least one vaccine and one animal."
            onDone(false)
            return
        }
        viewModelScope.launch {
            runCatching {
                cowVaccinesRepo.insertMany(
                    vaccines = vaccines,
                    group = group,
                    animalIds = animalIds,
                    dateGiven = dateGiven,
                    remarks = remarks
                )
            }.onSuccess { count ->
                toastMessage = "Logged $count vaccination(s)."
                onDone(true)             // ← notify success
            }.onFailure { e ->
                toastMessage = "Failed to log: ${e.message ?: "unknown error"}"
                onDone(false)            // ← notify failure
            }
        }
    }

    init {
        refresh()
    }

    /** Prefer vaccine_number; otherwise a shortened id. */
    private fun displayIdOf(id: String?, vaccineNumber: Int?): String? {
        return vaccineNumber?.toString()
            ?: id?.takeIf { it.isNotBlank() }?.take(8)
    }

    /** Load/refresh the catalog of vaccines. */
    fun refresh() {
        uiState = UiState(loading = true)
        viewModelScope.launch {
            runCatching {
                repo.fetchVaccines().map { v ->
                    VaccineUi(
                        id = v.id.orEmpty(),
                        name = v.name,
                        description = v.description,
                        notes = v.notes,
                        vaccineId = displayIdOf(v.id, v.vaccine_number)
                    )
                }
            }.onSuccess { list ->
                uiState = UiState(loading = false, vaccines = list)
            }.onFailure { e ->
                uiState = UiState(loading = false, error = e.message ?: "Failed to load")
            }
        }
    }

    /** Add a vaccine (name required, description/notes nullable). */
    fun addVaccine(
        name: String,
        description: String? = null,
        notes: String? = null,
        onDone: (ok: Boolean, err: String?) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                repo.addVaccine(
                    com.example.cattletrackingapp.data.model.Vaccine(
                        name = name,
                        description = description,
                        notes = notes
                    )
                )
            }.onSuccess { ok ->
                if (ok) {
                    refresh()
                    onDone(true, null)
                } else {
                    onDone(false, "Insert failed")
                }
            }.onFailure { e ->
                onDone(false, e.message ?: "Insert failed")
            }
        }
    }
    fun ensureAnimalsLoaded(
        group: TargetGroup,
        onError: (String) -> Unit = {}
    ) {
        // Already loaded? skip
        animalsByGroup[group]?.let { return }

        viewModelScope.launch {
            runCatching<List<AnimalHit>> {
                when (group) {
                    TargetGroup.Cows -> cowsRepo.getCows().mapNotNull { cow ->
                        val id = cow.id ?: return@mapNotNull null
                        AnimalHit(id = id, tag = cow.tag_number)
                    }

                    TargetGroup.Bulls -> bullsRepo.getBulls().mapNotNull { bull ->
                        val id = bull.id ?: return@mapNotNull null
                        AnimalHit(id = id, tag = bull.tag_number)
                    }

                    TargetGroup.Calves -> calvesRepo.getCalves().mapNotNull { calf ->
                        val id = calf.id ?: return@mapNotNull null
                        AnimalHit(id = id, tag = calf.tag_number, gender = calf.sex)
                    }
                }
            }.onSuccess { list: List<AnimalHit> ->
                animalsByGroup = animalsByGroup.toMutableMap().apply { put(group, list) }
            }.onFailure { e: Throwable ->
                onError(e.message ?: "Failed to load ${group.label.lowercase()}")
            }
        }
    }
}
