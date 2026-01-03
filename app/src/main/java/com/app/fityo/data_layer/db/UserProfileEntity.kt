package com.app.fityo.data_layer.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.fityo.dominio.AthleticDiscipline
import com.app.fityo.dominio.BiologicalSex
import com.app.fityo.dominio.UserProfile

/**
 * Entity Room per il profilo utente.
 * Contiene i dati biometrici e la disciplina atletica.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val sex: String,           // "MALE" o "FEMALE"
    val discipline: String,     // Nome enum AthleticDiscipline
    val createdAt: Long,
    val updatedAt: Long
) {
    /**
     * Converte l'entity nel modello di dominio.
     */
    fun toDomainModel(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            sex = BiologicalSex.valueOf(sex),
            discipline = AthleticDiscipline.valueOf(discipline),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        /**
         * Crea un'entity dal modello di dominio.
         */
        fun fromDomainModel(profile: UserProfile): UserProfileEntity {
            return UserProfileEntity(
                id = profile.id,
                name = profile.name,
                age = profile.age,
                heightCm = profile.heightCm,
                weightKg = profile.weightKg,
                sex = profile.sex.name,
                discipline = profile.discipline.name,
                createdAt = profile.createdAt,
                updatedAt = profile.updatedAt
            )
        }
    }
}
