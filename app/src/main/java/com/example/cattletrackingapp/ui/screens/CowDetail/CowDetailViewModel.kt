package com.example.cattletrackingapp.ui.screens.cowdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.UIModel.CowUi
import com.example.cattletrackingapp.ui.UIModel.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CowDetailViewModel @Inject constructor(
    private val repo: CowsRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val cow: CowUi? = null,
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())
        private set

    fun load(id: String) {
        // guard: don't re-fetch same id if already loaded
        if (uiState.cow?.id == id && !uiState.loading) return

        uiState = UiState(loading = true)
        viewModelScope.launch {
            runCatching { repo.getCowById(id)?.toUi() }
                .onSuccess { cow ->
                    uiState = UiState(
                        loading = false,
                        cow = cow,
                        error = if (cow == null) "Cow not found" else null
                    )
                }
                .onFailure { e ->
                    uiState = UiState(loading = false, error = e.message ?: "Failed to load")
                }
        }
    }
}
