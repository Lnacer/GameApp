package com.lucas.mygameapp.view.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date


class DatePickerFragment(private val date: Date?, val callback: (Date) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val defaultZoneId = ZoneId.systemDefault()

        val localDate = LocalDate.of(year, month + 1, day)

        val date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant())
        callback(date)
    }
}