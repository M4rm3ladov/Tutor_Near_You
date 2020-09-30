package com.example.tutornearyou;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TutorHomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ABC");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_sign_out)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_sign_out){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TutorHomeActivity.this)
                            .setTitle("Sign Out")
                            .setMessage("Do you wish to sign out?")
                            .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("SIGN OUT", (dialogInterface, i) ->  AuthUI.getInstance()
                                    .signOut(TutorHomeActivity.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent loginIntent = new Intent(TutorHomeActivity.this, MainActivity.class);
                                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(loginIntent);
                                            finish();
                                        }
                                    }))
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialogInterface ->
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(android.R.color.holo_red_dark)));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                //.setTextColor(getResources().getColor(R.color.colorAccent));
                            dialog.show();
                }
                return true;
            }
        });

        // set data for user
        View headerView = navigationView.getHeaderView(0);
        TextView text_name = headerView.findViewById(R.id.text_name);
        TextView text_phone = headerView.findViewById(R.id.text_phone);
        TextView text_star = headerView.findViewById(R.id.text_star);

        text_name.setText(CommonClass.buildWelcomeMessage());
        text_phone.setText(CommonClass.currentUser != null ? CommonClass.currentUser.getPhoneNumber() : "");
        text_star.setText(CommonClass.currentUser != null ? String.valueOf(CommonClass.currentUser.getRating()) : "0.0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tutor_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}