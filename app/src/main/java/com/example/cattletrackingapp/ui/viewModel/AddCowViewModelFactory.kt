package com.example.cattletrackingapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.viewModel.AddCowViewModel

class AddCowViewModelFactory(
    private val repo: CowsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCowViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddCowViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}