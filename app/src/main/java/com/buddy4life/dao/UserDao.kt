package com.buddy4life.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.buddy4life.model.Post.Post
import com.buddy4life.model.User.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg user: User)

    @Query("SELECT * FROM User WHERE uid LIKE :id LIMIT 1")
    fun getById(id: String): LiveData<User>
}