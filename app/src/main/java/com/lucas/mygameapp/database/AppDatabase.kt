package com.lucas.mygameapp.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lucas.mygameapp.dao.GameDao
import com.lucas.mygameapp.dao.PlatformDao
import com.lucas.mygameapp.dao.UserDao
import com.lucas.mygameapp.dao.UserGameDao
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.model.User
import com.lucas.mygameapp.model.UserGame

@Database(entities = [Game::class, UserGame::class, User::class, Platform::class],
          version = 7, autoMigrations = [AutoMigration (from = 6, to = 7)], exportSchema = true)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao() : GameDao
    abstract fun userGameDao() : UserGameDao
    abstract fun userDao() : UserDao
    abstract fun platformDao() : PlatformDao
}

