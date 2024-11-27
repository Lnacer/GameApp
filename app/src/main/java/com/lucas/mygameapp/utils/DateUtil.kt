package com.lucas.mygameapp.utils

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class DateUtil {

    companion object {

        fun LocalDateToDate(localDate : LocalDate) : Date {
            return localDate.toDate()
        }

        fun LocalDate.toDate() : Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

    }
}