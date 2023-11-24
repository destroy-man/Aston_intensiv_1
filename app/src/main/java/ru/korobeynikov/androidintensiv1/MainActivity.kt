package ru.korobeynikov.androidintensiv1

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {

    private var isConfigChange = false
    private lateinit var sConn: ServiceConnection
    private lateinit var intentService: Intent
    private lateinit var fields: Array<Field>
    var songNumber = 0
    var listMusic = ArrayList<Int>()
    var bound = false
    lateinit var serviceMusic: ServiceMusic
    lateinit var serviceViewModel: ServiceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fields = R.raw::class.java.fields
        for (field in fields)
            listMusic.add(field.getInt(0))

        serviceViewModel = ViewModelProvider(this)[ServiceViewModel::class.java]

        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnPause = findViewById<Button>(R.id.btnPause)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)

        intentService = Intent(this, ServiceMusic::class.java)
        sConn = object : ServiceConnection {

            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                serviceMusic = (binder as ServiceMusic.MyBinder).getService()
                val mediaPlayer = MediaPlayer.create(this@MainActivity, listMusic[songNumber])
                serviceMusic.mediaPlayer = mediaPlayer
                bound = true
                val serviceLiveData = serviceViewModel.getData()
                serviceLiveData?.observe(this@MainActivity) {
                    songNumber = it[0] as Int
                    serviceMusic.mediaPlayer = it[1] as MediaPlayer
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                bound = false
            }
        }

        btnPlay.setOnClickListener {
            startMusic()
        }
        btnPause.setOnClickListener {
            pauseMusic()
        }
        btnNext.setOnClickListener {
            nextMusic()
        }
        btnPrevious.setOnClickListener {
            previousMusic()
        }

        bindService(intentService, sConn, 0)
        startService(intentService)
    }

    override fun onStop() {
        super.onStop()
        serviceViewModel.loadData(arrayOf(songNumber, serviceMusic.mediaPlayer))
    }

    private fun startMusic() {
        if (!bound) return
        serviceMusic.startMusic()
        Toast.makeText(this, "Играет песня ${fields[songNumber].name}", Toast.LENGTH_SHORT).show()
    }

    private fun pauseMusic() {
        if (!bound) return
        serviceMusic.pauseMusic()
    }

    private fun nextMusic() {
        if (!bound) return
        if (songNumber < listMusic.size - 1) {
            songNumber++
            serviceMusic.changeMusic(listMusic[songNumber])
            Toast.makeText(this, "Играет песня ${fields[songNumber].name}",
                Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this, "Это последняя песня!", Toast.LENGTH_SHORT).show()
    }

    private fun previousMusic() {
        if (!bound) return
        if (songNumber > 0) {
            songNumber--
            serviceMusic.changeMusic(listMusic[songNumber])
            Toast.makeText(this, "Играет песня ${fields[songNumber].name}",
                Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this, "Это первая песня!", Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isConfigChange = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!bound) return
        unbindService(sConn)
        bound = false
        if (!isConfigChange)
            stopService(intentService)
    }
}