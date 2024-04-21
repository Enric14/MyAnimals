package com.enric.myanimals.data.remote

import android.net.Uri
import android.util.Log
import com.enric.myanimals.data.response.PetResponse
import com.enric.myanimals.data.utils.FirebaseConstants.PETS_COLLECTION
import com.enric.myanimals.data.utils.FirebaseConstants.USERS_COLLECTION
import com.enric.myanimals.domain.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebasePetRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getAllPets(): MutableList<Pet> {
        val uid = getCurrentUserId() ?: return mutableListOf()
        return firestore.collection(USERS_COLLECTION)
            .document(uid)
            .collection(PETS_COLLECTION)
            .get().await().map { pet ->
                pet.toObject(PetResponse::class.java).toDomain()
            }.toMutableList()

    }

    suspend fun uploadAndDownloadImage(uri: Uri): String {
        return suspendCancellableCoroutine<String> { suspendCancellable ->
            val reference = storage.reference.child("download/${uri.lastPathSegment}")
            reference.putFile(uri, createMetaData()).addOnSuccessListener {
                downloadImage(it, suspendCancellable)
            }.addOnFailureListener {
                suspendCancellable.resume("")
            }
        }
    }

    private fun downloadImage(
        uploadTask: UploadTask.TaskSnapshot,
        suspendCancellable: CancellableContinuation<String>
    ) {
        uploadTask.storage.downloadUrl.addOnSuccessListener {
            suspendCancellable.resume(it.toString())
        }.addOnFailureListener {
            suspendCancellable.resume("")
        }
    }

    private fun createMetaData(): StorageMetadata {
        val metadata = storageMetadata {
            contentType = "image/jpg"
            setCustomMetadata("date", Date().time.toString())
        }
        return metadata
    }

    private fun generatePetId(): String {
        return Date().time.toString()
    }

    suspend fun uploadNewPet(
        bornDate: String,
        breed: String,
        chip: String,
        imageUrl: String,
        name: String,
        species: String
    ): Boolean {
        val uid = getCurrentUserId() ?: return false
        val id = generatePetId()
        val petMap = hashMapOf(
            "uid" to uid,
            "id" to id,
            "bornDate" to bornDate,
            "breed" to breed,
            "chip" to chip,
            "image" to imageUrl,
            "name" to name,
            "species" to species,
        )

        return suspendCancellableCoroutine { cancellableCoroutine ->
            firestore.collection(USERS_COLLECTION).document(uid).collection(PETS_COLLECTION)
                .document(id).set(petMap).addOnCompleteListener {
                cancellableCoroutine.resume(true)
            }.addOnFailureListener {
                cancellableCoroutine.resume(false)
            }
        }
    }

    suspend fun getPetById(pet: Pet): Pet? {
        val uid = getCurrentUserId() ?: return null
        return try {
            val petSnapshot = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(PETS_COLLECTION)
                .document(pet.id)
                .get()
                .await()

            if (petSnapshot.exists()) {
                val fetchedPet = petSnapshot.toObject(PetResponse::class.java)?.toDomain()
                if (fetchedPet != null) {
                    fetchedPet.imageUrl = petSnapshot.getString("image") ?: ""
                }
                Log.d("FirebasePetRepositoryImpl", "Pet details fetched: $fetchedPet")
                fetchedPet
            } else {
                Log.d("FirebasePetRepositoryImpl", "Pet not found for ID: ${pet.id}")
                null
            }
        } catch (e: Exception) {
            Log.e("FirebasePetRepositoryImpl", "Error fetching pet details", e)
            null
        }
    }

    suspend fun deletePet(pet: Pet): Boolean {
        val uid = getCurrentUserId() ?: return false
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(PETS_COLLECTION)
                .document(pet.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updatePet(pet: Pet): Boolean {
        val uid = getCurrentUserId() ?: return false
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(PETS_COLLECTION)
                .document(pet.id)
                .set(pet)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}