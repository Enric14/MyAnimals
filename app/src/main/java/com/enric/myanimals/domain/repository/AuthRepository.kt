package com.enric.myanimals.domain.repository

interface AuthRepository {

    suspend fun login(email:String, password:String): Result<Unit>

    suspend fun signUp(email: String, password: String): Result<Unit>

    fun isUserLogged():Boolean

    fun logout()
}