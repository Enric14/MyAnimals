package com.enric.myanimals.ui.detailspet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enric.myanimals.data.remote.FirebasePetRepositoryImpl
import com.enric.myanimals.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val petRepositoryImpl: FirebasePetRepositoryImpl
):ViewModel(){

    private var _uiState = MutableStateFlow<DetailsUIState>(DetailsUIState())
    val uiState: StateFlow<DetailsUIState> = _uiState

    fun getPetDetails(pet: Pet) {
        viewModelScope.launch {
            val fetchedPet = petRepositoryImpl.getPetById(pet)
            _uiState.value = DetailsUIState(pet = fetchedPet)        }
    }
}
data class DetailsUIState(
    val pet: Pet? = null
)
