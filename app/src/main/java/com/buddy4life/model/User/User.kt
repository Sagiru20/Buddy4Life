package com.buddy4life.model.User

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey() var uid: String,
    var name: String,
    var photoUrl: String? = null,
    var email: String,
) {


    constructor(
        name: String,
        photoUrl: String? = null,
        email: String,) : this("", name, photoUrl, email)


    companion object {

        const val UID_KEY = "uid"
        const val NAME_KEY = "name"
        const val EMAIL_KEY = "email"
        const val PHOTO_URL_KEY = "photoUrl"

//        fun toUser(user: FirebaseUser ): User {
//
//            val uid = user.uid ?: ""
//            val name = user.displayName ?: ""
//            val photoUrl = user.photoUrl ?: ""
//            val email = user.email ?: ""
//            val photoUrlString :String? = photoUrl?.toString()
//
//            val user = User(uid, name , photoUrlString, email)
//
//            return user
//        }

        fun fromJSON(userJson: Map<String, Any>): User {

            val id = userJson[UID_KEY] as? String ?: ""
            val name = userJson[NAME_KEY] as? String ?: ""
            val email = userJson[EMAIL_KEY] as? String ?: ""
            val photoUrl = userJson[PHOTO_URL_KEY] as? String ?: ""
//            val photoUrlString :String? = photoUrl?.toString()

            val user = User(id, name , photoUrl, email)

            return user
        }


    }


    val json: Map<String, Any?>
        get() {
            return hashMapOf(
                UID_KEY to uid,
                NAME_KEY to name,
                PHOTO_URL_KEY to photoUrl,
                EMAIL_KEY to email
            )
        }



}