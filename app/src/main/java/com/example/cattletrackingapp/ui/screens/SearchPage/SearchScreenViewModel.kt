package com.example.cattletrackingapp.ui.screens.SearchPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import com.example.cattletrackingapp.ui.components.CattleType
import com.example.cattletrackingapp.ui.screens.HerdList.AnimalUi
import com.example.cattletrackingapp.ui.screens.HerdList.toAnimalUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class SearchBarUiState(
    val animals: List<AnimalUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchBarViewModel @Inject constructor(
    private val cowRepo: CowsRepository,
    private val calfRepo: CalvesRepository,
    private val bullRepo: BullsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchBarUiState())
    val uiState: StateFlow<SearchBarUiState> = _uiState

    private var searchJob: Job? = null
    private val debounceMillis: Long = 300L


    /**
     * Call this on every query change.
     * Debounces the input to avoid unnecessary searches.
     */
    fun onQueryChange(type: CattleType = CattleType.ALL, tagNumber: String) {
        _uiState.update { it.copy(isLoading = tagNumber.isNotBlank(), error = null) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debounceMillis)

            if (tagNumber.isBlank()) {
                _uiState.update { it.copy(animals = emptyList(), isLoading = false, error = null) }
                return@launch
            }

            try {
                val result = searchAllAnimals(tagNumber)

                _uiState.update {
                    it.copy(
                        animals = result.sortedWith(
                            compareBy<AnimalUi> {
                                it.tagNumber.filter { ch -> ch.isDigit() }
                                    .takeIf { s -> s.isNotEmpty() }
                                    ?.toIntOrNull() ?: Int.MAX_VALUE
                            }.thenBy { it.tagNumber.lowercase(Locale.getDefault()) }
                        ),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun searchAllAnimals(tagNumber: String): List<AnimalUi> = coroutineScope {
        val cowsDeferred = async { cowRepo.searchCowByTag(tagNumber).map { it.toAnimalUi() } }
        val calvesDeferred = async { calfRepo.searchCalfByTag(tagNumber).map { it.toAnimalUi() } }
        val bullsDeferred = async { bullRepo.searchBullByTag(tagNumber).map { it.toAnimalUi() } }

        cowsDeferred.await() + calvesDeferred.await() + bullsDeferred.await()
    }
}
