import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerRepository {

    var onPlayerCompletion: () -> Unit
    val playerDuration: Int
    val playerCurrentPosition: Int
    fun getCurrentPosition(): Int
    fun getDuration(): Int
    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun isPlaying(): Boolean
    fun releasePlayer()
}
