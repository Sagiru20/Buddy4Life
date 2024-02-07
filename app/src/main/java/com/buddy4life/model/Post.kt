package com.buddy4life.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey val name: String,
    val breed: String,
    val age: Int,
    val description: String
)

