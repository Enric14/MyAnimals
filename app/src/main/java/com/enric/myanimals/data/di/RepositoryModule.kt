package com.enric.myanimals.data.di

import com.enric.myanimals.data.remote.FirebaseAuthRepositoryImpl
import com.enric.myanimals.data.remote.FirestoreUserRepositoryImpl
import com.enric.myanimals.domain.repository.AuthRepository
import com.enric.myanimals.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(authRepository: FirebaseAuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindUserRepository(userRepository: FirestoreUserRepositoryImpl): UserRepository

}

