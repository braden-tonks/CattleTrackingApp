package com.example.cattletrackingapp.ui.screens.AddCow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.repository.CowsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AddCowViewModel @Inject constructor(
    private val repo: CowsRepository
) : ViewModel() {

    var saveState by mutableStateOf<String?>(null)
        private set

    fun saveCow(cow: Cow) {
        viewModelScope.launch {
            try {
                val success = repo.addCow(cow)
                saveState = if (success) "Saved!" else "Failed"
            } catch (e: Exception) {
                saveState = "Error: ${e.message}"
            }
        }
    }
}