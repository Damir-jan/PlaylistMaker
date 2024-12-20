package com.practicum.playlistmaker.utils

object TraksCount {
    fun getTracksCountText(count: Int): String {
        val forms = arrayOf("трек", "трека", "треков")
        val form = when {
            count % 10 == 1 && count % 100 != 11 -> 0
            count % 10 in 2..4 && (count % 100 !in 12..14) -> 1
            else -> 2
        }
        return " ${forms[form]}"
    }

    fun getMinutesWord(count: Int): String {
        val minutesWord = when (count % 10) {
            1 -> " минута"
            2, 3, 4 -> " минуты"
            else -> " минут"
        }
        return minutesWord
    }
}