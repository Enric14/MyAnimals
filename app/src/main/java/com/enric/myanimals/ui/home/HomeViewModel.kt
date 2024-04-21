package com.enric.myanimals.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enric.myanimals.data.remote.FirebasePetRepositoryImpl
import com.enric.myanimals.domain.model.Pet
import com.enric.myanimals.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val petRepositoryImpl: FirebasePetRepositoryImpl
):ViewModel(){

    private var _uiState = MutableStateFlow<HomeUIState>(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState

    init {
        getData()
    }

    fun getData() {
        getAllPets()
    }

    fun logout(navigateToLogin: () -> Unit) {
        viewModelScope.launch (Dispatchers.IO ) {
            authRepository.logout()
        }
        navigateToLogin()
    }

    private fun getAllPets() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                petRepositoryImpl.getAllPets()
            }
            _uiState.update { it.copy(pets = response) }
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            val success = petRepositoryImpl.deletePet(pet)
            if (success) {
                getData()
            }
        }
    }
}
data class HomeUIState(
    val pets: MutableList<Pet> = mutableListOf()
)