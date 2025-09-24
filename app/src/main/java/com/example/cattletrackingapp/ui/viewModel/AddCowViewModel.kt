package com.example.cattletrackingapp.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.model.Cow
import com.example.cattletrackingapp.data.repository.CowsRepository
import kotlinx.coroutines.launch

class AddCowViewModel(private val repo: CowsRepository) : ViewModel() {

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