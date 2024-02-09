package com.buddy4life.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Gender(private val label: String) {
    MALE("Male"), FEMALE("Female");

    override fun toString(): String {
        return this.label
    }
}


@Entity
data class Post(
    @PrimaryKey val name: String,
    val breed: String,
    val gender: Gender,
    val age: Int,
    val description: String
)

