package com.example.tutornearyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tutornearyou.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainActivity extends AppCompatActivity {

    Button mbtnSignIn, mbtnRegister;
    RelativeLayout rootLayout;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // firebase instantiate
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("Users");

        // buttons instantiate
        mbtnRegister = findViewById(R.id.btn_register);
        mbtnSignIn = findViewById(R.id.btn_sign_in);
        rootLayout = findViewById(R.id.rootLayout);

        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        mbtnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText edtEmail = layout_login.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = layout_login.findViewById(R.id.edtPassword);

        dialog.setView(layout_login);

        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // check validation
                if (TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please fill in email address", Snackbar.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    Snackbar.make(rootLayout, "Please fill in password", Snackbar.LENGTH_SHORT).show();
                }

                // login
                firebaseAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, Welcome.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed: "+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // check validation
                if (TextUtils.isEmpty(edtEmail.getText().toString().trim())){
                    Toast.makeText(rootLayout, "Please fill in email address", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())){
                    Snackbar.make(rootLayout, "Please fill in password", Snackbar.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(edtName.getText().toString().trim())){
                    Snackbar.make(rootLayout, "Please fill in name", Snackbar.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(edtPhone.getText().toString().trim())){
                    Snackbar.make(rootLayout, "Please fill in phone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }else if (edtPassword.getText().toString().trim().length() < 6){
                    Snackbar.make(rootLayout, "Password is too weak(A-Z, a-z, 1-9, must be at least 6 characters long)", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // register new user
                firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // save user to firebase database
                                User user = new User();
                                user.setEmail(edtEmail.getText().toString());
                                user.setPassword(edtPassword.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPhone(edtPhone.getText().toString());

                                // use email to key
                                users.child(firebaseAuth.getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showLoginDialog();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(rootLayout, "Failed: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Failed: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                });
            }

        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
