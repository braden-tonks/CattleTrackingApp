package com.example.cattletrackingapp.ui.screens.AddPages.AddCow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.remote.Models.Cow
import com.example.cattletrackingapp.data.repository.CowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

data class AddCowUiState(
    val loading: Boolean = false,
    val success: Boolean? = null, // null = idle, true = saved, false = error
    val errorMessage: String? = null
)

@HiltViewModel
class AddCowViewModel @Inject constructor(
    private val repo: CowsRepository
) : ViewModel() {

    var saveState by mutableStateOf(AddCowUiState())
        private set

    fun saveCow(cow: Cow) {
        saveState = saveState.copy(loading = true, success = null, errorMessage = null)

        viewModelScope.launch {
            runCatching { repo.addCow(cow) }
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