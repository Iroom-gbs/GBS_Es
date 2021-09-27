package com.dayo.executer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dayo.executer.App
import com.dayo.executer.MainActivity
import com.dayo.executer.data.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class AsckService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "asck") //오레오 부터 channelId가 반드시 필요하다.

        builder.setContentTitle("포그라운드 서비스")
        builder.setContentText("포그라운 서비스 실행중")

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        builder.setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(
                    "asck",
                    "자가진단",
                    NotificationManager.IMPORTANCE_NONE
                )
            )
        }
        startForeground(1, builder.build())
        CoroutineScope(Dispatchers.IO).launch {
            val url: URL
            var `is`: InputStream? = null
            val br: BufferedReader
            var line: String?

            try {
                url =
                    URL("http://20.41.76.129/hcs?name=${DataManager.asckName}&birth=${DataManager.asckBirth}&pw=${DataManager.asckPW}")
                `is` = url.openStream() // throws an IOException
                br = BufferedReader(InputStreamReader(`is`))
                while (br.readLine().also { line = it } != null)
                    line?.let{Toast.makeText(App.appContext, it, Toast.LENGTH_LONG).show()}
            } catch (mue: MalformedURLException) {
                mue.printStackTrace()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                } catch (ioe: IOException) {
                    // nothing to see here
                }
                stopSelf()
            }
        }
        return START_STICKY
    }
}