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
import com.google.firebase.database.FirebaseDatabase;
import com.chanaka.bodima.MainActivity;
import com.chanaka.bodima.R;

public class RemoveUserDialog extends Dialog {

    private Activity c;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Button exitButton, confirmButton;
    private ProgressBar progressBar;
    private String groupId;
    private String userId;

    public RemoveUserDialog(Activity a, String groupId, String userId) {
        super(a);
        this.c = a;
        this.groupId = groupId;
        this.userId = userId;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_remove_user);

        exitButton = (Button) findViewById(R.id.btn_exit_supprimer_user_modal);
        confirmButton = (Button) findViewById(R.id.btn_confirmer_supprimer_user_modal);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUser();
            }
        });
    }



    private void removeUser() {
        database.getReference("groupes/" + groupId + "/users/" + userId).setValue(null);
        database.getReference("users/" + userId + "/groupe").setValue(null);
        Toast.makeText(c.getApplicationContext(), "The User has been Removed", Toast.LENGTH_LONG).show();
        c.startActivity(new Intent(c, MainActivity.class));
        c.finish();
    }

}
