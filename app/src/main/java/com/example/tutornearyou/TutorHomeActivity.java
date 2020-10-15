package com.example.tutornearyou;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tutornearyou.Utils.UserUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.round;

public class TutorHomeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 7171;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;

    // image select
    private AlertDialog waitingDialog;
    private Uri imageUri;
    private ImageView img_avatar;

    private StorageReference storageReference;


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

        init();
    }

    private void init() {

        // selecting image
        waitingDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Waiting...")
                .create();

        storageReference = FirebaseStorage.getInstance().getReference();
        // end

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
                                    //.setTextColor(ContextCompat.getColor(TutorHomeActivity.this,R.color.colorAccent));
                    dialog.show();
                }
                return true;
            }
        });

        // set data for user
        View headerView = navigationView.getHeaderView(0);
        TextView text_name = headerView.findViewById(R.id.text_name);
        TextView text_phone = headerView.findViewById(R.id.text_phone);
        // image
        img_avatar = headerView.findViewById(R.id.image_avatar);

        text_name.setText(CommonClass.buildWelcomeMessage());
        text_phone.setText(CommonClass.currentUser != null ? CommonClass.currentUser.getPhoneNumber() : "");

        // image
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        if (CommonClass.currentUser != null && CommonClass.currentUser.getAvatar() != null &&
            !TextUtils.isEmpty(CommonClass.currentUser.getAvatar())){
            Glide.with(this)
                    .load(CommonClass.currentUser.getAvatar())
                    .into(img_avatar);
        } // end
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            if(data != null && data.getData() != null){
                showDialogUpload(data);
            }
        }
    }

    private void showDialogUpload(Intent data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TutorHomeActivity.this)
                .setTitle("Select Image")
                .setMessage("Do you wish to change avatar?")
                .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("UPLOAD", (dialogInterface, i) -> {

                    imageUri = data.getData();
                    img_avatar.setImageURI(imageUri);

                        if(imageUri != null){
                            waitingDialog.setMessage("Uploading...");
                            waitingDialog.show();

                            String unique_name = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            StorageReference avatarFolder = storageReference.child("avatars/"+unique_name);

                            avatarFolder.putFile(imageUri)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            waitingDialog.dismiss();
                                            Snackbar.make(drawer,e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        avatarFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Map<String, Object> updateData = new HashMap<>();
                                                updateData.put("avatar", uri.toString());

                                                UserUtils.updateUser(drawer,updateData);
                                            }
                                        });
                                    }
                                    waitingDialog.dismiss();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    waitingDialog.setMessage(new StringBuilder("Uploading:").append(round(progress)).append("%"));
                                }
                            });
                        }

                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(android.R.color.holo_red_dark)));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        //.setTextColor(getResources().getColor(R.color.colorAccent));
        dialog.show();
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