package it.latendatv.app

import android.app.Activity
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import it.latendatv.app.databinding.ActivityMainBinding


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true
    private var isPlayed = true
    var audioManager: AudioManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setView()
        preparePlayer()
    }

    private fun setView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    /*override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }*/

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        //super.dispatchKeyEvent(event)
        println(event.action.toString() + " " + event.keyCode + " - " + event.unicodeChar.toChar())
        if (event.action == KeyEvent.ACTION_UP) {
            /*if (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                isPlayed = if (isPlayed) {
                    exoPlayer?.pause()
                    false
                } else {
                    exoPlayer?.play()
                    true
                }
            }*/
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                if (isPlayed) {
                    moveTaskToBack(true)
                    exoPlayer?.stop()
                    isPlayed = false
                }
            }
        }
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }

            if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        }
        return true
    }

    private fun preparePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.playWhenReady = true
        binding.playerView.player = exoPlayer
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
        val mediaItem = MediaItem.fromUri(URL)
        val mediaSource =
            HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        exoPlayer?.apply {
            setMediaSource(mediaSource)
            seekTo(playbackPosition)
            playWhenReady = playWhenReady
            prepare()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onStop() {
        //super.onStop()
        exoPlayer?.stop()
        isPlayed = false
        super.onStop()
    }

    /*override fun onPause() {
        super.onPause()
        //releasePlayer()
    }*/

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    /*override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }*/

    override fun onRestart() {
        preparePlayer()

        isPlayed = true
        super.onRestart()
    }

    companion object {
        const val URL = "https://latendatv.it/livestream"
    }
}
