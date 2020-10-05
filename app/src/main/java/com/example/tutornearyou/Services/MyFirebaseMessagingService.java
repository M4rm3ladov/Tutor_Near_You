package com.example.tutornearyou.Services;

import com.example.tutornearyou.CommonClass;
import com.example.tutornearyou.Utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            UserUtils.updateToken(this, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> dataReceive = remoteMessage.getData();
        if(dataReceive != null){
            CommonClass.showNotification(this,new Random().nextInt(),
                    dataReceive.get(CommonClass.NOTIF_TITLE),
                    dataReceive.get(CommonClass.NOTIF_CONTENT),
                    null);
        }
    }
}
