package com.buddy4life.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buddy4life.model.User.UserModel

enum class Gender(private val label: String) {
    MALE("Male"), FEMALE("Female");

    override fun toString(): String {
        return this.label
    }
}

enum class Category(private val label: String) {
    ADOPTION_PROPOSAL("Adoption Proposal"),
    ADOPTION_REQUEST("Adoption Request"),
    DOG_LOST("Dog Lost");

    override fun toString(): String {
        return this.label
    }
}

    @Entity
class Post(
        @PrimaryKey() val id: String,
        var name: String,
        val breed: String,
        val gender: Gender,
        val age: Int,
        var description: String,
        val dogImageUri: String? = null,
        val category: Category,
        val weight: Int? = null,
        val height: Int? = null,
        var createdTime: Long,
        var lastUpdated: Long,
        var ownerId: String? = ""
)
{

    constructor(
        name: String,
        breed: String,
        gender: Gender,
        age: Int,
        description: String,
        dogImageUri: String? = null,
        category: Category,
        weight: Int? = null,
        height: Int? = null) : this("", name, breed, gender, age, description, dogImageUri, category, weight, height, System.currentTimeMillis() , System.currentTimeMillis( ), UserModel.instance.currentUser()?.uid)

    companion object {
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val BREED_KEY = "breed"
        const val GENDER_KEY = "gender"
        const val AGE_KEY = "age"
        const val DESCRIPTION_KEY = "description"
        const val DOG_IMAGE_URL_KEY = "dogImageUri"
        const val CATEGORY_KEY = "category"
        const val WEIGHT_KEY = "weight"
        const val HEIGHT_KEY = "height"
        const val CREATED_TIME_KEY = "createdTime"
        const val LAST_UPDATED_KEY = "lastUpdated"
        const val OWNER_ID_KEY = "ownerId"

        fun fromJSON(postJson: Map<String, Any>, postId: String): Post {

            val id = postId as? String ?: ""
            val name = postJson[NAME_KEY] as? String ?: ""
            val breed = postJson[BREED_KEY] as? String ?: ""
            val gender = postJson[GENDER_KEY] as? Gender ?: Gender.MALE
            val age = postJson[AGE_KEY] as? Long ?: 0
            val intAge = age.toInt()
            val description = postJson[DESCRIPTION_KEY] as? String ?: ""
            val dogImageUri = postJson[DOG_IMAGE_URL_KEY] as? String ?: ""
            val category = postJson[CATEGORY_KEY] as? Category ?: Category.ADOPTION_REQUEST
            val weight = postJson[WEIGHT_KEY] as? Long ?: 0
            val intWeight = weight.toInt()
            val height = postJson[HEIGHT_KEY] as? Long ?: 0
            val intHeight = height.toInt()
            val createdTime = postJson[CREATED_TIME_KEY] as? Long ?: 0
            val lastUpdated = postJson[LAST_UPDATED_KEY] as? Long ?: 0
            val ownerEmail = postJson[OWNER_ID_KEY] as? String ?: ""

            val post = Post(id, name, breed, gender, intAge, description, dogImageUri, category, intWeight, intHeight, createdTime, lastUpdated, ownerEmail)

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
                CATEGORY_KEY to category,
                WEIGHT_KEY to weight,
                HEIGHT_KEY to height,
                CREATED_TIME_KEY to createdTime ,
                LAST_UPDATED_KEY to lastUpdated,
                OWNER_ID_KEY to ownerId
            )
        }
}

