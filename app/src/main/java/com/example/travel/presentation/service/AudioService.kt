package com.example.travel.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.ImageButton
import android.widget.RemoteViews
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.travel.MainActivity
import com.example.travel.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class AudioService : Service() {
    val TAG = "TAG"
    private val CHANNEL_ID = "chanel_id_exam_01"
    var mediaPlayer: MediaPlayer? = null
    private var trackList: ArrayList<AudioData>? = null
    var positNow = 0
    var countCallCommand = 0

    private val ACTION_PAUSE = 1
    private val ACTION_RESUME = 2
    private val ACTION_CLEAR = 3
    private val ACTION_PRE = 4
    private val ACTION_NEXT = 5

    private var isPrepared = false
    private var playingJob: Job? = null

    val scope = CoroutineScope(Dispatchers.Default)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        //create channel for notification
        creatChannelNoti()
    }

    // Класс для взаимодействия с клиентом
    private val binder = LocalBinder()

    // Binder
    inner class LocalBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        @Suppress("UNCHECKED_CAST")
        val _trackList = intent?.getSerializableExtra("tracklist") as? ArrayList<AudioData>

        if (_trackList != null) {
            trackList = _trackList
        }
//        if (countCallCommand == 0) {
        Log.d(TAG, "onStartCommand: $trackList")
//            scope.launch {
//                async{
        startMusic(trackList!![positNow].media)
//                }.await()
//
//            }

//        }
//        else {
//            Log.d(TAG, "onStartCommand: nextMusic")
//            nextMusic()
//
//        }
        countCallCommand++


        //create notification
//        sendNoti(trackList!!, positNow)

        val actionMusic = intent!!.getIntExtra("action_mucsic_backser", 0)
        handleActionMusic(actionMusic)

        return START_STICKY
    }


    override fun onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }

//    override fun onBind(intent: Intent): IBinder {
//        TODO("Return the communication channel to the service.")
//    }


    private fun startMusic(filePath: String) {
        Log.d(TAG, "filePath: ${filePath}")
//        mediaPlayer?.release()
        scope.launch {
            delay(2000)
            mediaPlayer = MediaPlayer().apply {
//            mediaPlayer?.let {

                setDataSource(filePath)
                prepare()
                start()
                playingJob = scope.launch {
                    while (isPlaying) {
                        sendLocData(duration, currentPosition)
                        delay(50)
                    }
                }.also { it.invokeOnCompletion {
                    sendLocData(0, 0)
                } }

            }
        }


//                scope.launch {
//                    async(Dispatchers.Main) {
//                    }.await()
//                }
//            }

//            try {
//                setDataSource(filePath)
//                // Подготавливаем асинхронно
//                prepareAsync()
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            setOnPreparedListener { mp ->
//                // MediaPlayer готов, можно получить duration и начать воспроизведение
//                isPrepared = true
//                mp.start()
//            }
//
//            setOnErrorListener { mp, what, extra ->
//                // Обработка ошибок
//                true
//            }

    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

//    fun getDuration(): Int? {
//        // Убедимся, что mediaPlayer подготовлен перед получением длительности
//        if (isPrepared) {
//            return mediaPlayer?.duration
//        }
//        return null
//    }

    fun stopPlayer() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()

        }
        scope.launch {
            playingJob?.cancelAndJoin()
        }

    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun seekTo(pos: Int) {
        mediaPlayer?.seekTo(pos)
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_RESUME -> resumeMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_CLEAR -> clearMusic()
            ACTION_PRE -> preMusic()
            ACTION_NEXT -> nextMusic()
            else -> return
        }
    }

    private fun nextMusic() {
//        if (positNow == trackList!!.size - 1) {
//            pauseMusic()
//        } else {
//            positNow++
        mediaPlayer!!.release()
        startMusic(trackList!![positNow].media)
        sendNoti(trackList!!, positNow)
//        }
    }

    private fun preMusic() {
        if (positNow == 0) {
            pauseMusic()
        } else {
            positNow--
            mediaPlayer!!.release()
            startMusic(trackList!![positNow].media)
            sendNoti(trackList!!, positNow)
        }
    }

    fun clearMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                stopSelf()
            }
        }
    }

    fun pauseMusic() {
//        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
//            mediaPlayer?.pause()
//            sendNoti(trackList!!, positNow)
//        }

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                sendNoti(trackList!!, positNow)
            }
        }
    }

    private fun resumeMusic() {
//        if (mediaPlayer != null && mediaPlayer?.isPlaying == false) {
////            mediaPlayer?.setDataSource(filePath)
////            mediaPlayer?.prepare()
//            mediaPlayer?.start()
//            sendNoti(trackList!!, positNow)
//        }

        if (mediaPlayer == null) {
            // Если mediaPlayer == null, возможно, его следует инициализировать и запустить
            trackList?.let {
                if (positNow < it.size) {
                    startMusic(it[positNow].media)
                }
            }
        } else {
            if (!(mediaPlayer?.isPlaying ?: true)) {
                mediaPlayer?.start()
                sendNoti(trackList!!, positNow)
            }
        }
    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(context, AudioReceiver::class.java)
        intent.putExtra("action_music", action)
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun creatChannelNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Noti Title"
            val descrip = "Noti Descrip"
            val impor = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, impor).apply {
                description = descrip
            }
            channel.setSound(null, null)
            val notiMana: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notiMana.createNotificationChannel(channel)
        }
    }

    private fun sendNoti(trackList: ArrayList<AudioData>, positNow: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val remoteview = RemoteViews(packageName, R.layout.layout_custom_notification)
//        remoteview.setTextViewText(R.id.tv_title, trackList[positNow].title)
//        remoteview.setTextViewText(R.id.tv_single, trackList[positNow].singer)
//        remoteview.setImageViewBitmap(
//            R.id.img_song,
//            BitmapFactory.decodeResource(
//                applicationContext.resources,
//                trackList[positNow].thumbnail
//            )
//        )
        remoteview.setImageViewResource(R.id.img_play_pause, R.drawable.pause)
        if (mediaPlayer!!.isPlaying) {
            remoteview.setOnClickPendingIntent(
                R.id.img_play_pause,
                getPendingIntent(this, ACTION_PAUSE)
            )
            remoteview.setImageViewResource(R.id.img_play_pause, R.drawable.pause)

        } else {
            remoteview.setOnClickPendingIntent(
                R.id.img_play_pause,
                getPendingIntent(this, ACTION_RESUME)
            )
            remoteview.setImageViewResource(R.id.img_play_pause, R.drawable.conti)
        }
        remoteview.setOnClickPendingIntent(
            R.id.img_clear,
            getPendingIntent(this, ACTION_CLEAR)
        )
        remoteview.setOnClickPendingIntent(
            R.id.img_pre,
            getPendingIntent(this, ACTION_PRE)
        )
        remoteview.setOnClickPendingIntent(
            R.id.img_next,
            getPendingIntent(this, ACTION_NEXT)
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setCustomContentView(remoteview)
            .setSound(null)
            .build()
        startForeground(1, builder)
    }

    private fun sendLocData(duration: Int, currentPosition: Int) {
        val i = Intent(DURATION)
        i.putExtra(DURATION, duration)
        i.putExtra(CURPOS, currentPosition)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i)
    }

    companion object {
        const val DURATION = "duration"
        const val CURPOS = "curpos"
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L
    }
}