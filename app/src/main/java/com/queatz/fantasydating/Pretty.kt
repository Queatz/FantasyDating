package com.queatz.fantasydating

import com.queatz.on.On
import java.text.SimpleDateFormat
import java.util.*

class Pretty constructor(private val on: On) {

    private val simpleDateFormat = SimpleDateFormat("MMM d ", Locale.US)
    private val simpleDateFormatWithYear = SimpleDateFormat("MMM d, YYYY", Locale.US)

    fun date(date: Date?): String {
        if (date == null) {
            return "Unknown"
        }

        val time = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        time.time = date

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val yesterday = Calendar.getInstance()
        yesterday.time = today.time
        yesterday.add(Calendar.DATE, -1)

        val thisYear = Calendar.getInstance()
        thisYear.time = today.time
        thisYear.set(Calendar.DAY_OF_YEAR, 0)

        if (time.after(today)) {
            return "Today"
        }

        if (time.after(yesterday)) {
            return "Yesterday"
        }

        if (time.after(thisYear)) {
            return simpleDateFormat.format(time.time)
        }

        return simpleDateFormatWithYear.format(time.time)
    }

}
