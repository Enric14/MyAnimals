package com.enric.myanimals.data.remote

import android.util.Log
import com.enric.myanimals.data.utils.FirebaseConstants
import com.enric.myanimals.data.utils.FirebaseConstants.USERS_COLLECTION
import com.enric.myanimals.domain.model.User
import com.enric.myanimals.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): UserRepository {
    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            Log.d("FirestoreUserRepository", "Guardando usuario en Firestore: $user")
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(user)
                .await()
            Log.d("FirestoreUserRepository", "Usuario guardado correctamente en Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreUserRepository", "Error al guardar usuario en Firestore", e)
            Result.failure(e)
        }
    }

    override suspend fun getUser(uid: String): Result<User> {
        return try {
            val userDocument = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            if (userDocument.exists()) {
                val user = userDocument.toObject(User::class.java)
                user?.let {
                    Result.success(it)
                } ?: Result.failure<User>(NullPointerException("Error al convertir el documento a objeto User"))
            } else {
                Result.failure(NoSuchElementException("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}