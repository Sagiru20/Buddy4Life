package com.buddy4life.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.apphosting.datastore.testing.DatastoreTestTrace.FirestoreV1Action.CreateDocument

@Entity
data class Post(
    @PrimaryKey val name: String,
    val breed: String,
    val age: Int,
    val description: String,
    val gender: String,
    val createdTime: String,
    val lastUpdated: String,
    val postId: String,
    val dogImageUrl: String
) {

    companion object {
        const val NAME_KEY = "name"
        const val BREED_KEY = "breed"
        const val AGE_KEY = "age"
        const val DESCRIPTION_KEY = "description"
        const val GENDER_KEY = "gender"
        const val CREATED_TIME_KEY = "createdTime"
        const val LAST_UPDATED_KEY = "lastUpdated"
        const val POST_ID_KEY = "postId"
        const val DOG_IMAGE_URL_KEY = "dogImageUrl"

        //        const val AVATAR_URL_KEY = "avatarUrl"
//        const val LAST_UPDATED = "lastUpdated"
        fun fromJSON(json: Map<String, Any>): Post {
            val name = json[NAME_KEY] as? String ?: ""
            val breed = json[BREED_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val age = json[AGE_KEY] as? Long ?: 0
            val intAge = age.toInt()
            val gender = json[GENDER_KEY] as? String ?: ""
            val createdTime = json[CREATED_TIME_KEY] as? String ?: ""
            val lastUpdated = json[LAST_UPDATED_KEY] as? String ?: ""
            val postId = json[POST_ID_KEY] as? String ?: ""
            val dogImageUrl = json[DOG_IMAGE_URL_KEY] as? String ?: ""

            val post = Post(name, breed, intAge, description, gender, createdTime, lastUpdated, postId, dogImageUrl)

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
                DESCRIPTION_KEY to description,
                AGE_KEY to age,
                GENDER_KEY to gender,
                CREATED_TIME_KEY to createdTime ,
                LAST_UPDATED_KEY to lastUpdated,
                POST_ID_KEY to postId,
                DOG_IMAGE_URL_KEY to dogImageUrl

            )
//    LAST_UPDATED to FieldValue.serverTimestamp()

        }

}

