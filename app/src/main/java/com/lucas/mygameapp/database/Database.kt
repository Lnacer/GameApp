package com.lucas.mygameapp.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private val DATABASE_NAME : String = "my_game_app"

class Database {

    companion object {
        private var _instance : AppDatabase? = null

        fun getInstance(applicationContext : Context) : AppDatabase {
            if (_instance == null) {
                _instance = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, DATABASE_NAME
                ).addMigrations(MIGRATION_2_3).build()
            }
            return _instance!!
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `User` add user_picture BLOB")
            }
        }
    }
}