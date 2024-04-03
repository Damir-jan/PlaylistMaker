package com.practicum.playlistmaker


private fun Long.formatToMinutesAndSeconds(): String {
        val minutes = this / 60000
        val seconds = (this % 60000) / 1000
        return "%02d:%02d".format(minutes, seconds)
    }




