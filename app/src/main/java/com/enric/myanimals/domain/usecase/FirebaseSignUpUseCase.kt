package com.enric.myanimals.domain.usecase

import com.enric.myanimals.domain.model.User
import com.enric.myanimals.domain.repository.AuthRepository
import com.enric.myanimals.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
){
    suspend fun signUp(email: String, password: String): Result<User> {
        val signUpResult = authRepository.signUp(email, password)
        return if (signUpResult.isSuccess) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { firebaseUser ->
                val uid = firebaseUser.uid
                val user = User(uid, email)
                val saveUserResult = userRepository.saveUser(user)
                if (saveUserResult.isSuccess) {
                    Result.success(user)
                } else {
                    Result.failure(saveUserResult.exceptionOrNull()!!)
                }
            } ?: Result.failure(NullPointerException("Usuario de Firebase nulo"))
        } else {
            Result.failure(signUpResult.exceptionOrNull()!!)
        }
    }
}