package com.lucas.mygameapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

@Entity
class User(@ColumnInfo(name = "user_name") var name : String,
           @PrimaryKey
           @ColumnInfo(name = "user_id") val id: Int) {

    @ColumnInfo(name = "user_access_token")
    var accessToken : String? = null

    @Ignore
    var email : String? = null

    @ColumnInfo(name = "user_picture", typeAffinity = ColumnInfo.BLOB)
    var picture : ByteArray? = null

    @ColumnInfo(name = "user_picture_path")
    var picturePath : String? = null

    @ColumnInfo(name = "user_created_date")
    var createdDate : Date? = null
}