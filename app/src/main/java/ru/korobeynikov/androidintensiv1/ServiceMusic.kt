package ru.korobeynikov.androidintensiv1

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder

class ServiceMusic : Service() {

    private val binder = MyBinder()
    lateinit var mediaPlayer: MediaPlayer

    override fun onBind(p0: Intent?) = binder

    fun startMusic() = mediaPlayer.start()

    fun pauseMusic() = mediaPlayer.pause()

    fun changeMusic(idSong: Int) {
        mediaPlayer.stop()
        mediaPlayer = MediaPlayer.create(this, idSong)
        startMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.pause()
    }

    inner class MyBinder : Binder() {
        fun getService() = this@ServiceMusic
    }
}