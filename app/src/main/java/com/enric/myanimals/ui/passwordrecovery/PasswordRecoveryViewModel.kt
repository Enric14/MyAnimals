package com.enric.myanimals.ui.passwordrecovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enric.myanimals.domain.usecase.FirebasePasswordRecoveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val firebasePasswordRecoveryUseCase: FirebasePasswordRecoveryUseCase
): ViewModel() {

    private val _recoveryState: MutableLiveData<Result<Unit>> = MutableLiveData()
    val recoveryState: LiveData<Result<Unit>> = _recoveryState

    fun recoverPassword(email: String) {
        viewModelScope.launch {
            val result = firebasePasswordRecoveryUseCase.recoverPassword(email)
            _recoveryState.value = result
        }
    }
}