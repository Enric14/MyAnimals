package com.enric.myanimals.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enric.myanimals.domain.model.User
import com.enric.myanimals.domain.usecase.FirebaseLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: FirebaseLoginUseCase
): ViewModel() {

    private val _loginState: MutableLiveData<Result<User>> = MutableLiveData()
    val loginState: LiveData<Result<User>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = loginUseCase.login(email, password)
            _loginState.value = result
        }
    }
}