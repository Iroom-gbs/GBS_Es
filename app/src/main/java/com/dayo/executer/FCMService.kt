package com.dayo.executer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.dayo.executer.data.DataManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {
    companion object {
        var ALWAYS_RECEVE_TIMETABLE_DATA = "${DataManager.classInfo}TOKEN_ALWAYS_TIMETABLE"
        val DEBUG = "DEBUG"
        fun reinitToken() {
            ALWAYS_RECEVE_TIMETABLE_DATA = "${DataManager.classInfo}TOKEN_ALWAYS_TIMETABLE"
        }
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(App.appContext, msg.notification?.body.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if(DataManager.alwaysReceiveTimeTableData)
            FirebaseMessaging.getInstance().subscribeToTopic(ALWAYS_RECEVE_TIMETABLE_DATA)
        if(DataManager.receiveDebugFCMData)
            FirebaseMessaging.getInstance().subscribeToTopic(DEBUG)
    }
}