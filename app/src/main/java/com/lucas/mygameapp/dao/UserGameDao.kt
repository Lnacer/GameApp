package com.lucas.mygameapp.dao

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.model.UserGame
import java.util.Date

@Dao
interface UserGameDao {
    @Query("SELECT * FROM usergame ORDER BY user_game_created_date DESC, usergame_id DESC")
    fun getAll(): List<UserGame>

    @Query("SELECT * FROM usergame WHERE user_game_start_play_on IS NOT NULL ORDER BY user_game_start_play_on DESC")
    fun getAllPlayaedWithDate(): List<UserGame>

    @Query("SELECT * FROM usergame WHERE (user_game_start_play_on >= :startDate AND user_game_start_play_on <= :endDate) ORDER BY user_game_start_play_on ASC")
    fun getAllPlayedBetweenDates(startDate : Date, endDate : Date): List<UserGame>

    @Query("SELECT * FROM usergame WHERE (user_game_stop_play_on >= :startDate AND user_game_stop_play_on <= :endDate AND game_status = 'FINISHED') ORDER BY user_game_stop_play_on ASC")
    fun getAllBeatenBetweenDates(startDate : Date, endDate : Date): List<UserGame>

    @Query("SELECT * FROM usergame WHERE user_game_stop_play_on IS NOT NULL AND game_status = 'FINISHED' ORDER BY user_game_stop_play_on DESC")
    fun getAllBeatenWithDate(): List<UserGame>

    @Query("SELECT * FROM usergame WHERE usergame.game_id = :gameId LIMIT 1")
    fun getUserGameByGameId(gameId: Int): UserGame?

    @Query("SELECT * FROM usergame WHERE game_status = :gameStatus ORDER BY user_game_created_date DESC, usergame_id DESC")
    fun getAllByStatus(gameStatus : GameStatus): List<UserGame>

    @Query("SELECT * FROM usergame WHERE usergame_id IN (:userGameIds)")
    fun loadAllByIds(userGameIds: IntArray): List<UserGame>

    @Insert
    fun insertAll(vararg userGame: UserGame)

    @Insert
    fun insertUserGame(userGame: UserGame) : Long

    @Update
    fun updateUserGame(userGame: UserGame)

    @Delete
    fun delete(userGame: UserGame)
}