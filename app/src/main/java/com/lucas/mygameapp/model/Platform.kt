package com.lucas.mygameapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Platform(@PrimaryKey(autoGenerate = true)
                    @ColumnInfo(name = "platform_id")
                    val id : Int) {
    @ColumnInfo(name = "platform_abbreviation")
    var abbreviation : String? = null
    @ColumnInfo(name = "platform_name")
    var name : String? = null
    @ColumnInfo(name = "platform_generation")
    var generation : Int? = null
    @ColumnInfo(name = "platform_logo_url")
    var logoUrl : String? = null
}