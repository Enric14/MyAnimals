package com.enric.myanimals.ui.welcome

import androidx.lifecycle.ViewModel
import com.enric.myanimals.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private fun isUserLogged(): Boolean {
        return authRepository.isUserLogged()
    }

    fun checkDestination(): WelcomeDestination {
        val isUserLogged = isUserLogged()
        return if (isUserLogged) {
            WelcomeDestination.Home
        } else {
            WelcomeDestination.Login
        }
    }
}

sealed class WelcomeDestination {
    object Login : WelcomeDestination()
    object Home : WelcomeDestination()
}