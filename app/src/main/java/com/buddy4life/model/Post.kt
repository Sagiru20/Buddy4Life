package com.buddy4life.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey val name: String,
    val breed: String,
    val age: Int,
) {

    companion object {
        const val NAME_KEY = "name"
        const val BREED_KEY = "breed"
        const val AGE_KEY = "age"

        //        const val AVATAR_URL_KEY = "avatarUrl"
//        const val LAST_UPDATED = "lastUpdated"
        fun fromJSON(json: Map<String, Any>): Post {
//            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val breed = json[BREED_KEY] as? String ?: ""
            val age = json[AGE_KEY] as? Long ?: 0
            val intAge = age.toInt()
//            val avatarUrl = json[AVATAR_URL_KEY] as? String ?: ""

            val post = Post(name, breed, intAge)

//            val timestamp: Timestamp? = json[LAST_UPDATED] as? Timestamp
//            timestamp?.let {
//                student.lastUpdated = it.seconds
//            }

            return post
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                NAME_KEY to name,
                BREED_KEY to breed,
                AGE_KEY to age
            )
//    AVATAR_URL_KEY to avatarUrl,
//    LAST_UPDATED to FieldValue.serverTimestamp()

        }

}

