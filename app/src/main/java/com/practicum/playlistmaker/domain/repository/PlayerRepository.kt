import com.practicum.playlistmaker.domain.models.Track

interface PlayerRepository {

    var onPlayerStateChanged: (state: Int) -> Unit
    var onPlayerCompletion: () -> Unit
    val playerDuration: Int
    val playerCurrentPosition: Int

    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun releasePlayer()
}
