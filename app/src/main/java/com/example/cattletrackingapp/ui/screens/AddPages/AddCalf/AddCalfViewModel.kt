package com.example.cattletrackingapp.ui.screens.AddPages.AddCalf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Bull
import com.example.cattletrackingapp.data.remote.Models.Calf
import com.example.cattletrackingapp.data.remote.Models.Cow
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

    var cows by mutableStateOf<List<Cow>>(emptyList())
        private set

    var bulls by mutableStateOf<List<Bull>>(emptyList())
        private set


    init {
        loadCows()
        loadBulls()
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