package com.enric.myanimals.ui.addpet

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enric.myanimals.data.remote.FirebasePetRepositoryImpl
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddPetViewModel @Inject constructor(
    private val firebasePetRepositoryImpl: FirebasePetRepositoryImpl):ViewModel(){

    private var _uiState = MutableStateFlow<AddPetState>(AddPetState())
    val uiState: StateFlow<AddPetState> = _uiState

    fun onNameChanged(name: CharSequence?) {
        _uiState.update { it.copy(name = name.toString()) }
    }

    fun onChipChanged(chip: CharSequence?) {
        _uiState.update { it.copy(chip = chip.toString()) }
    }

    fun onSpeciesChanged(species: CharSequence?) {
        _uiState.update { it.copy(species = species.toString()) }
    }

    fun onBornDateChanged(bornDate: CharSequence?) {
        _uiState.update { it.copy(bornDate = bornDate.toString()) }
    }

    fun onBreedChanged(breed: CharSequence?) {
        _uiState.update { it.copy(breed = breed.toString()) }
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            showLoading(true)
            val result: String = withContext(Dispatchers.IO) {
                firebasePetRepositoryImpl.uploadAndDownloadImage(uri)
            }
            _uiState.update { it.copy(imageUrl = result) }
            showLoading(false)
        }
    }

    private fun showLoading(show:Boolean) {
        _uiState.update { it.copy(isLoading = show) }
    }

    fun onAddPetSelected(onSuccessPet: () -> Unit) {
        viewModelScope.launch {
            showLoading(true)
            val result = withContext(Dispatchers.IO) {
                firebasePetRepositoryImpl.uploadNewPet(
                    _uiState.value.bornDate,
                    _uiState.value.breed,
                    _uiState.value.chip,
                    _uiState.value.imageUrl,
                    _uiState.value.name,
                    _uiState.value.species,
                    )
            }

            if (result) {
                onSuccessPet()
            } else {
                _uiState.update { it.copy(error = "Ha habido un error") }
            }

            showLoading(false)
        }
    }

}

data class AddPetState(
    val name:String = "",
    val chip: String = "",
    val species: String = "",
    val bornDate: String = "",
    val breed: String = "",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {

    fun isValidPet():Boolean
    = name.isNotBlank() && chip.isNotBlank() && species.isNotBlank() && bornDate.isNotBlank() && breed.isNotBlank()

}