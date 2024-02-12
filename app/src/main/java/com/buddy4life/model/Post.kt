package com.buddy4life.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buddy4life.base.MyApplication
import com.google.apphosting.datastore.testing.DatastoreTestTrace.FirestoreV1Action.CreateDocument
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.type.Date




enum class Gender(private val label: String) {
    MALE("Male"), FEMALE("Female");

    override fun toString(): String {
        return this.label
    }
}

    @Entity
class Post(
        @PrimaryKey(true) val id: String,
        var name: String,
        val breed: String,
        val gender: Gender,
        val age: Int,
        var description: String,
        val dogImageUri: Uri? = null,
        val weight: Int? = null,
        val height: Int? = null,
        var createdTime: FieldValue,
        var lastUpdated: FieldValue
)
{


    constructor(
        name: String,
        breed: String,
        gender: Gender,
        age: Int,
        description: String,
        dogImageUri: Uri? = null,
        weight: Int? = null,
        height: Int? = null) : this("", name, breed, gender, age, description, dogImageUri, weight, height, FieldValue.serverTimestamp(), FieldValue.serverTimestamp())

    companion object {
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val BREED_KEY = "breed"
        const val GENDER_KEY = "gender"
        const val AGE_KEY = "age"
        const val DESCRIPTION_KEY = "description"
        const val DOG_IMAGE_URL_KEY = "dogImageUri"
        const val WEIGHT_KEY = "weight"
        const val HEIGHT_KEY = "height"
        const val CREATED_TIME_KEY = "createdTime"
        const val LAST_UPDATED_KEY = "lastUpdated"

        fun fromJSON(postJson: Map<String, Any>, postId: String ): Post {
            val id = postJson[ID_KEY] as? String ?: ""
            val name = postJson[NAME_KEY] as? String ?: ""
            val breed = postJson[BREED_KEY] as? String ?: ""
            val gender = postJson[GENDER_KEY] as? Gender ?: Gender.MALE
            val age = postJson[AGE_KEY] as? Long ?: 0
            val intAge = age.toInt()
            val description = postJson[DESCRIPTION_KEY] as? String ?: ""
            val dogImageUri = postJson[DOG_IMAGE_URL_KEY] as? Uri ?: null
            val weight = postJson[WEIGHT_KEY] as? Long ?: 0
            val intWeight = age.toInt()
            val height = postJson[HEIGHT_KEY] as? Long ?: 0
            val intHeight = age.toInt()
            val createdTime = postJson[CREATED_TIME_KEY] as? FieldValue ?: FieldValue.serverTimestamp()
            val lastUpdated = postJson[LAST_UPDATED_KEY] as? FieldValue ?: FieldValue.serverTimestamp()

            val post = Post(id, name, breed, gender, intAge, description, dogImageUri, intWeight, intHeight, createdTime, lastUpdated)

            return post
        }
    }

    val json: Map<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                NAME_KEY to name,
                BREED_KEY to breed,
                GENDER_KEY to gender,
                AGE_KEY to age,
                DESCRIPTION_KEY to description,
                DOG_IMAGE_URL_KEY to dogImageUri,
                WEIGHT_KEY to weight,
                HEIGHT_KEY to height,
                CREATED_TIME_KEY to createdTime ,
                LAST_UPDATED_KEY to lastUpdated,
            )
        }
}

