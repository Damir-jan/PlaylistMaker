package com.practicum.playlistmaker.search

import com.practicum.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}