package com.lucas.mygameapp.database

import androidx.room.TypeConverter
import java.util.Date

class Converter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toListString(string: String?): List<String>? {
        return string?.let { it.split(";") }
    }

    @TypeConverter
    fun fromListString(listString: List<String>?): String? {
        return listString?.joinToString(";")
    }
}