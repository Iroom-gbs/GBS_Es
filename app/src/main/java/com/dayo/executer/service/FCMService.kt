package com.dayo.executer.service

import android.util.Log
import android.widget.Toast
import com.dayo.executer.App
import com.dayo.executer.data.DataManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {
    companion object {
        var ALWAYS_RECEIVE_TIMETABLE_DATA = "${DataManager.classInfo}TOKEN_ALWAYS_TIMETABLE"
        var SWD_RECEIVE_TIMETABLE_DATA = "${DataManager.classInfo}TOKEN_SWD_TIMETABLE"
        const val DEBUG = "DEBUG"
        const val ASCK_ALERT = "TOKEN_ASCK"

        fun reinitToken() {
            ALWAYS_RECEIVE_TIMETABLE_DATA = "${DataManager.classInfo}TOKEN_ALWAYS_TIMETABLE"
            SWD_RECEIVE_TIMETABLE_DATA = "${DataManager.classInfo}TOKEN_SWD_TIMETABLE"
        }
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        Log.d("FCM", msg.notification?.body.toString())
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(App.appContext, msg.notification?.body.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if(DataManager.alwaysReceiveTimeTableData)
            FirebaseMessaging.getInstance().subscribeToTopic(ALWAYS_RECEIVE_TIMETABLE_DATA)
        if(DataManager.receiveSwdTimeTableData)
            FirebaseMessaging.getInstance().subscribeToTopic(SWD_RECEIVE_TIMETABLE_DATA)
        if(DataManager.receiveDebugFCMData)
            FirebaseMessaging.getInstance().subscribeToTopic(DEBUG)
        if(DataManager.alwaysReceiveAsckAlert)
            FirebaseMessaging.getInstance().subscribeToTopic(ASCK_ALERT)
    }
}