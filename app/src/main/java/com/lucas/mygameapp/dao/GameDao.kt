package com.lucas.mygameapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lucas.mygameapp.model.Game

@Dao
interface GameDao {
    @Query("SELECT * FROM game")
    fun getAll(): List<Game>

    @Query("SELECT * FROM game WHERE game_id IN (:gameIds)")
    fun loadAllByIds(gameIds: IntArray): List<Game>

    @Query("SELECT * FROM game WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Game

    @Query("SELECT * FROM game WHERE game_id = :id LIMIT 1")
    fun findById(id: Int): Game

    @Insert
    fun insertAll(vararg games: Game)

    @Delete
    fun delete(game: Game)
}