package com.example.cattletrackingapp.ui.screens.AddPages.AddBull

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.Bull
import com.example.cattletrackingapp.data.repository.BullsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

data class AddBullUiState(
    val loading: Boolean = false,
    val success: Boolean? = null, // null = idle, true = saved, false = error
    val errorMessage: String? = null
)

@HiltViewModel
class AddBullViewModel @Inject constructor(
    private val repo: BullsRepository
) : ViewModel() {

    var saveState by mutableStateOf(AddBullUiState())
        private set

    fun saveBull(bull: Bull) {
        saveState = saveState.copy(loading = true, success = null, errorMessage = null)

        viewModelScope.launch {
            runCatching { repo.addBull(bull) }
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