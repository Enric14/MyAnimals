package com.enric.myanimals.data.response

import com.enric.myanimals.domain.model.Pet

data class PetResponse(
    val uid: String = "",
    val id: String = "",
    val bornDate: String = "",
    val breed: String = "",
    val chip: String = "",
    val image: String = "",
    val name: String = "",
    val species: String = "",


    ) {
    fun toDomain(): Pet {
        return Pet(
            uid = uid,
            id = id,
            bornDate = bornDate,
            breed = breed,
            chip = chip,
            imageUrl = image,
            name = name,
            species = species
            )
    }
}



