package com.practicum.playlistmaker.data.dto
import com.practicum.playlistmaker.domain.models.Track

class SearchResponse (val resultCount: Int, val results: List<Track>)