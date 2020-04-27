package com.chanaka.bodima;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 Activité permettant de paramétrer son compte
 */

public class EditProfileActivity extends AppCompatActivity {

    private EditText inputEmail, inputNewPassword, inputPasswordConfirmation, inputOldPassword;
    private ImageButton btnRetour;
    private Button btnEdit;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        inputEmail = (EditText) findViewById(R.id.email);
        inputOldPassword = (EditText) findViewById(R.id.oldPassword);
        inputNewPassword = (EditText) findViewById(R.id.newPassword);
        inputPasswordConfirmation = (EditText) findViewById(R.id.passwordConfirmation);
        btnRetour = (ImageButton) findViewById(R.id.btn_params_retour);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        inputEmail.setText(auth.getCurrentUser().getEmail());


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean changeEmail = false;
                Boolean changePassword = true;

                final String email = inputEmail.getText().toString();
                final String oldPassword = inputOldPassword.getText().toString();
                final String newPassword = inputNewPassword.getText().toString();
                final String passwordConfirmation = inputPasswordConfirmation.getText().toString();

                final FirebaseUser user = auth.getInstance().getCurrentUser();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter an Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!email.equals(user.getEmail())) changeEmail = true;

                if (TextUtils.isEmpty(oldPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter Your current Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(passwordConfirmation)) {
                    changePassword = false;
                } else {
                    if (!newPassword.equals(passwordConfirmation)) {
                        Toast.makeText(getApplicationContext(), "Error..!! Check your Passwords", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Enter a new Password of 6 characters or more", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                progressBar.setVisibility(View.VISIBLE);


                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                if(changePassword) {
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Password has been updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error..!! Password change not successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Current password is Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if(changeEmail) {
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Email address has been update", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error..!! Email address update not successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Current password is Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
}
