package com.enric.myanimals.domain.usecase

import android.util.Log
import com.enric.myanimals.domain.model.User
import com.enric.myanimals.domain.repository.AuthRepository
import com.enric.myanimals.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend fun login(email: String, password: String): Result<User> {
        val loginResult = authRepository.login(email, password)
        if (loginResult.isSuccess) {
            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(NullPointerException("Usuario de Firebase nulo"))
            val uid = currentUser.uid
            Log.d("FirebaseLoginUseCase", "UID de usuario obtenido: $uid")
            val user = userRepository.getUser(uid).getOrElse {
                Log.e("FirebaseLoginUseCase", "Error al obtener usuario de Firestore", it)
                return Result.failure(it)
            }
            Log.d("FirebaseLoginUseCase", "Usuario obtenido de Firestore: $user")
            return Result.success(user)
        } else {
            Log.e("FirebaseLoginUseCase", "Error al iniciar sesión", loginResult.exceptionOrNull())
            return Result.failure(loginResult.exceptionOrNull()!!)
        }
    }
}