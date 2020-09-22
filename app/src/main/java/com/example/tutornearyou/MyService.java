package com.example.tutornearyou;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;

public class MyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("La puta la madre","imung Mama");
        AuthUI.getInstance()
                .signOut(this);
        super.onTaskRemoved(rootIntent);
    }
}
