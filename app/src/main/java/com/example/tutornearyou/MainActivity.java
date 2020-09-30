package com.example.tutornearyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tutornearyou.Model.TutorInfoModel;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 7171;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference tutorInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startService(new Intent(getBaseContext(), MyService.class));

        firebaseDatabase = FirebaseDatabase.getInstance();
        tutorInfoRef = firebaseDatabase.getReference(CommonClass.TUTOR_INFO_REFERENCE);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    handleInfoRegister();
                } else {
                    handleLoginRegister();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        if (firebaseAuth != null && authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    private void handleInfoRegister() {
        tutorInfoRef.child(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            TutorInfoModel tutorInfoModel = snapshot.getValue(TutorInfoModel.class);
                            CommonClass.currentUser = tutorInfoModel;
                            startActivity(new Intent(MainActivity.this, TutorHomeActivity.class));

                        } else {
                            //showRegisterLayout();
                            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivity(registerIntent);
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void handleLoginRegister() {
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_main)
                .setPhoneButtonId(R.id.btn_phone_sign_in)
                .setGoogleButtonId(R.id.btn_google_sign_in)
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .setAuthMethodPickerLayout(authMethodPickerLayout)
                    .setAlwaysShowSignInMethodScreen(true)
                    .setTheme(R.style.AppTheme)
                    .build(), LOGIN_REQUEST_CODE);
    }

}
