package com.enric.myanimals.domain.model

import java.io.Serializable

data class Pet(
    val uid: String = "", // Identificador único del usuario que añade la mascota
    val id: String = "", // Identificador único de la mascota
    val bornDate: String = "", // Fecha de nacimiento de la mascota
    val breed: String = "", // Raza de la mascota
    val chip: String = "", // Número de chip de la mascota
    var imageUrl: String = "", // URL de la imagen de la mascota
    val name: String = "", // Nombre de la mascota
    val species: String = "" // Especie de la mascota (perro, gato, etc.)
) :Serializable
