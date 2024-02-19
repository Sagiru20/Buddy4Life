package com.buddy4life.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.buddy4life.model.Post.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM Post")
    fun getAll(): List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg post: Post)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM Post WHERE id LIKE :id LIMIT 1")
    fun getById(id: Long): Post
}
