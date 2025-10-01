package com.example.cattletrackingapp.ui.cattle

// This page is the state manager for the cattle list screen. It's necessary
// to keep our app clean, reactive, and scalable.

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

data class CattleUiState(
    val cows: List<CowUi> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CattleListViewModel @Inject constructor(
    private val repo: CowsRepository
) : ViewModel() {

    var uiState by mutableStateOf(CattleUiState())
        private set

    init { refresh() }

    fun refresh() {
        uiState = uiState.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.fetchCattleList().map { it.toUi() } }
                .onSuccess { uiState = CattleUiState(cows = it, loading = false) }
                .onFailure { uiState = CattleUiState(loading = false, error = it.message ?: "Failed to load") }
        }
    }
}
