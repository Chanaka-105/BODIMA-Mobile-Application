package com.chanaka.bodima.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.chanaka.bodima.R;


public class IdentifyGroupDialog extends Dialog {

    private Activity c;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private TextView idGroupeText;
    private Button exitButton, copyButton;
    private ProgressBar progressBar;
    private String groupId;

    public IdentifyGroupDialog(Activity a) {
        super(a);
        this.c = a;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_identify_group);


        exitButton = (Button) findViewById(R.id.btn_exit);
        copyButton = (Button) findViewById(R.id.btn_copy_id);
        idGroupeText = (TextView) findViewById(R.id.txt_id);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        setIdentifyGroup();


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("id", groupId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(c.getApplicationContext(), "User ID has been copied to the Clipboard", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setIdentifyGroup() {

        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupId = (String) dataSnapshot.child("groupe").getValue();
                idGroupeText.setText(groupId);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
