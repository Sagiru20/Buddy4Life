package com.buddy4life.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.buddy4life.base.MyApplication
import com.buddy4life.model.Post

@Database(entities = [Post::class], version = 6, exportSchema = false)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("ERROR!! Application context not available")

        Room.databaseBuilder(
            context, AppLocalDbRepository::class.java, "Buddy4Life.db"
        ).fallbackToDestructiveMigration().build()
    }
}