package com.buddy4life.dao

import androidx.room.Dao
import androidx.room.Query
import com.buddy4life.model.Post.Post

interface UserDao {

    @Dao
    interface PostDao {

        @Query("SELECT * FROM Post WHERE id LIKE :id LIMIT 1")
        fun getById(id: Long): Post
    }


}