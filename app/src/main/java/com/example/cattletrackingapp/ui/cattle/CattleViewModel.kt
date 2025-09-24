package com.example.cattletrackingapp.ui.cattle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.repository.CowsRepository
import kotlinx.coroutines.launch

// ViewModel: survives configuration changes (like screen rotation)
// and holds the cattle list state for the UI.
class CattleViewModel(
    private val repo: CowsRepository = CowsRepository()
) : ViewModel() {

    // UI state that Compose will observe
    // - `mutableStateOf` creates an observable state holder
    // - `var uiState` is what the UI reads
    // - `private set` means only this ViewModel can update it
    var uiState by mutableStateOf(CattleUiState())
        private set

    // init block runs immediately when ViewModel is created
    // Here, it triggers the first fetch so the UI shows data on load
    init { refresh() }

    // Public function to (re)load cattle list from the repository
    fun refresh() {
        uiState = uiState.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                // Ask the repository for the cattle list
                val list = repo.fetchCattleList().map { cow -> cow.toUi() }
                uiState = CattleUiState(cows = list, loading = false)
            } catch (e: Exception) {
                uiState = CattleUiState(loading = false, error = e.message ?: "Failed to load")
            }
        }
    }
}
