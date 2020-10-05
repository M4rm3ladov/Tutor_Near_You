package com.example.tutornearyou;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.tutornearyou.Model.TutorInfoModel;
import com.example.tutornearyou.Services.MyFirebaseMessagingService;

public class CommonClass {
    public static final String TUTOR_INFO_REFERENCE = "TutorInfo";
    public static final String TUTORS_LOCATION_REFERENCES = "TutorsLocation";
    public static final String TOKEN_REFERRENCE = "Token";
    public static final String NOTIF_TITLE = "title";
    public static final String NOTIF_CONTENT = "body";

    public static TutorInfoModel currentUser;

    public static String buildWelcomeMessage() {
        if (CommonClass.currentUser != null){
            return ("Welcome: ")
                    + (CommonClass.currentUser.getFirstName())
                    + (" ")
                    + (CommonClass.currentUser.getLastName().toString());
        }else{
            return "";
        }
    }

    public static void showNotification(Context context, int id, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "tutor_near_you";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Tutor Near You", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Tutor Near You");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_baseline_school_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_baseline_school_24));
        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
        }
}
