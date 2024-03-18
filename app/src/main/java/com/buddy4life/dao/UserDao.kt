package com.buddy4life.dao

import androidx.room.Dao
import androidx.room.Query
import com.buddy4life.model.Post.Post

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE uid LIKE :id LIMIT 1")
    fun getById(id: Long): Post
}