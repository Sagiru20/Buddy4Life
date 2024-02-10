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
    @PrimaryKey(true) val id: Long,
    val name: String,
    val breed: String,
    val gender: Gender,
    val age: Int,
    val description: String,
    val weight: Int? = null,
    val height: Int? = null,
) {
    constructor(
        name: String, breed: String, gender: Gender, age: Int, description: String
    ) : this(0, name, breed, gender, age, description)
}