package com.lucas.mygameapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lucas.mygameapp.model.Platform

@Dao
interface PlatformDao {
    @Query("SELECT * FROM platform ORDER BY platform_generation DESC")
    fun getAll(): List<Platform>

    @Insert
    fun insertAll(platforms: List<Platform>)
}