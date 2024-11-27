package com.lucas.mygameapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lucas.mygameapp.model.User
import com.lucas.mygameapp.model.UserGame

@Dao
interface UserDao {
    @Query("SELECT user_id, user_name, user_access_token, user_picture_path, user_created_date FROM user")
    fun getAll(): List<User>

    @Query("SELECT user_id, user_name, user_access_token, user_picture_path, user_created_date FROM user LIMIT 1")
    fun getUser(): User

    @Query("SELECT user_id, user_name, user_access_token, user_picture_path, user_created_date FROM user WHERE user_id = :id LIMIT 1")
    fun findById(id: Int): User

    @Insert
    fun insertAll(vararg users: User)

    @Update
    fun updateUser(user: User)
}