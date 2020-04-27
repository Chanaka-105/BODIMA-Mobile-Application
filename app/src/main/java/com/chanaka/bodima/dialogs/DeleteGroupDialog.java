package com.chanaka.bodima.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.chanaka.bodima.auth.LoginActivity;
import com.chanaka.bodima.R;

import java.util.Map;


public class DeleteGroupDialog extends Dialog {

    private Activity c;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Button exitButton, confirmButton;
    private ProgressBar progressBar;
    private String groupId;

    public DeleteGroupDialog(Activity a, String groupId) {
        super(a);
        this.c = a;
        this.groupId = groupId;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_group);

        exitButton = (Button) findViewById(R.id.btn_exit);
        confirmButton = (Button) findViewById(R.id.btn_confirmer);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroupe();
            }
        });
    }


    private void deleteGroupe() {

        DatabaseReference ref = database.getReference("groupes/" + groupId + "/users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> usersGroupe = (Map<String, String>) dataSnapshot.getValue();

                for (Map.Entry<String, String> entry : usersGroupe.entrySet()) {
                    database.getReference("users/" + entry.getKey() + "/groupe").setValue(null);
                }
                database.getReference("groupes/" + groupId).setValue(null);

                Toast.makeText(c.getApplicationContext(), "The group has been permanently deleted", Toast.LENGTH_LONG).show();
                c.startActivity(new Intent(c, LoginActivity.class));
                c.finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
