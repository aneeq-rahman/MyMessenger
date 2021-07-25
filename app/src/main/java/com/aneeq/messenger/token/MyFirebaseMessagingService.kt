package com.aneeq.messenger.token

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService() {

    val TAG="service"
    override fun onMessageReceived(p0: RemoteMessage) {
       Log.d(TAG,"From" + p0.from)
        Log.d(TAG,"Notification Message Body:" +p0.notification?.body)
    }

}