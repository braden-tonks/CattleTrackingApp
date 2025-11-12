package com.example.cattletrackingapp.ui.screens.AddPages.AddCalf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.BullIdAndTag
import com.example.cattletrackingapp.data.model.Calf
import com.example.cattletrackingapp.data.model.CowIdAndTag
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

data class AddCalfUiState(
    val loading: Boolean = false,
    val success: Boolean? = null, // null = idle, true = saved, false = error
    val errorMessage: String? = null
)

@HiltViewModel
class AddCalfViewModel @Inject constructor(
    private val cowRepo: CowsRepository,
    private val bullRepo: BullsRepository,
    private val calveRepo: CalvesRepository
) : ViewModel() {

    var saveState by mutableStateOf(AddCalfUiState())
        private set

    var cowTags by mutableStateOf<List<CowIdAndTag>>(emptyList())
        private set

    var bullTags by mutableStateOf<List<BullIdAndTag>>(emptyList())
        private set


    init {
        loadCowTags()
        loadBullTags()
    }

    private fun loadCowTags() {
        viewModelScope.launch {
            cowTags = runCatching { cowRepo.fetchCattleIdsAndTags() }
                .getOrElse { emptyList() }
        }
    }

    private fun loadBullTags() {
        viewModelScope.launch {
            bullTags = runCatching { bullRepo.fetchBullIdsAndTags() }
                .getOrElse { emptyList() }
        }
    }


    fun saveCalf(calf: Calf) {
        saveState = saveState.copy(loading = true, success = null, errorMessage = null)

        viewModelScope.launch {
            runCatching { calveRepo.addCalf(calf) }
                .onSuccess {
                    if (it) {
                        saveState = saveState.copy(loading = false, success = true)
                    } else {
                        saveState = saveState.copy(
                            loading = false,
                            success = false,
                            errorMessage = "Failed to save cow"
                        )
                    }
                }
                .onFailure { e ->
                    saveState = saveState.copy(
                        loading = false,
                        success = false,
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
        }
    }
}