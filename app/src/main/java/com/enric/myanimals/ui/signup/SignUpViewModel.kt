package com.enric.myanimals.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enric.myanimals.domain.model.User
import com.enric.myanimals.domain.usecase.FirebaseSignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
private val signUpUseCase: FirebaseSignUpUseCase
): ViewModel() {

    private val _signUpState:MutableLiveData<Result<User>> = MutableLiveData()
    val signUpState:LiveData<Result<User>> = _signUpState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            val result = signUpUseCase.signUp(email, password)
            _signUpState.value = result
        }
    }
}