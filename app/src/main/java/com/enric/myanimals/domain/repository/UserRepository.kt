package com.enric.myanimals.domain.repository

import com.enric.myanimals.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>

    suspend fun getUser(uid:String): Result<User>
}