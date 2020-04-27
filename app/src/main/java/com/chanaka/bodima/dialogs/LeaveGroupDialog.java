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

import com.chanaka.bodima.R;
import com.chanaka.bodima.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;



public class LeaveGroupDialog extends Dialog {

    private Activity c;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Button exitButton, confirmButton;
    private ProgressBar progressBar;
    private String groupId;

    public LeaveGroupDialog(Activity a, String groupId) {
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
        setContentView(R.layout.dialog_leave_group);

        exitButton = (Button) findViewById(R.id.btn_exit_quitter_modal);
        confirmButton = (Button) findViewById(R.id.btn_confirmer_quitter_modal);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });
    }


    private void leaveGroup() {
        database.getReference("groupes/" + groupId + "/users/" + auth.getCurrentUser().getUid()).setValue(null);
        database.getReference("users/" + auth.getCurrentUser().getUid() + "/groupe").setValue(null);
        Toast.makeText(c.getApplicationContext(), "You have left the Group", Toast.LENGTH_LONG).show();
        c.startActivity(new Intent(c, LoginActivity.class));
        c.finish();
    }

}

