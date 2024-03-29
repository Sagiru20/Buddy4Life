package com.buddy4life.model.Post

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buddy4life.base.MyApplication
import com.buddy4life.model.User.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase

enum class Gender(private val label: String) {
    MALE("Male"), FEMALE("Female");

    override fun toString(): String {
        return this.label
    }
}

@Entity
data class Post(
    @PrimaryKey() val id: String,
    var name: String,
    val breed: String,
    val gender: Gender,
    val age: Int,
    var description: String,
    val dogImageUri: String? = null,
    val weight: Int? = null,
    val height: Int? = null,
    var createdTime: Long,
    var lastUpdated: Long? = null,
    var ownerId: String? = "",
    var isExists: Boolean = true,
    var breedDescription: String? = ""
) {

    constructor(
        name: String,
        breed: String,
        gender: Gender,
        age: Int,
        description: String,
        dogImageUri: String? = null,
        weight: Int? = null,
        height: Int? = null,
        breedDescription: String? = ""
    ) : this(
        "",
        name,
        breed,
        gender,
        age,
        description,
        dogImageUri,
        weight,
        height,
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        Firebase.auth.currentUser?.uid,
        true,
        breedDescription
    )

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
        const val OWNER_ID_KEY = "ownerId"
        const val IS_EXISTS_KEY = "isExists"
        const val BREED_DESCRIPTION_KEY="breedDescription"
        const val GET_LAST_UPDATED = "get_last_updated"


        var lastUpdated: Long
            get() {
                return MyApplication.Globals.appContext?.getSharedPreferences(
                    "TAG", Context.MODE_PRIVATE
                )?.getLong(GET_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals?.appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.edit()?.putLong(GET_LAST_UPDATED, value)?.apply()
            }


        fun fromJSON(postJson: Map<String, Any>, postId: String): Post {
            val id = postId as? String ?: ""
            val name = postJson[NAME_KEY] as? String ?: ""
            val breed = postJson[BREED_KEY] as? String ?: ""
            var gender: Gender

            try {
                gender = Gender.valueOf(postJson[GENDER_KEY].toString().uppercase())
            } catch (e: IllegalArgumentException) {
                gender = Gender.MALE
            }

            val age = postJson[AGE_KEY] as? Long ?: 0
            val intAge = age.toInt()
            val description = postJson[DESCRIPTION_KEY] as? String ?: ""
            val dogImageUri = postJson[DOG_IMAGE_URL_KEY] as? String ?: ""
            val weight = postJson[WEIGHT_KEY] as? Long ?: 0
            val intWeight = weight.toInt()
            val height = postJson[HEIGHT_KEY] as? Long ?: 0
            val intHeight = height.toInt()
            val createdTime = postJson[CREATED_TIME_KEY] as? Long ?: 0
            val ownerEmail = postJson[OWNER_ID_KEY] as? String ?: ""
            val isExists = postJson[IS_EXISTS_KEY] as? Boolean ?: false
            val breedDescription = postJson[BREED_DESCRIPTION_KEY] as? String ?: ""

            val post = Post(
                id,
                name,
                breed,
                gender,
                intAge,
                description,
                dogImageUri,
                intWeight,
                intHeight,
                createdTime,
                0,
                ownerEmail,
                isExists,
                breedDescription
            )

            val timestamp: Timestamp? = postJson[LAST_UPDATED_KEY] as? Timestamp
            timestamp?.let {
                post.lastUpdated = it.seconds
            }

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
                CREATED_TIME_KEY to createdTime,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
                OWNER_ID_KEY to ownerId,
                IS_EXISTS_KEY to isExists,
                BREED_DESCRIPTION_KEY to breedDescription
            )
        }

    val markForDeletion: Map<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
                IS_EXISTS_KEY to false
            )
        }
}

