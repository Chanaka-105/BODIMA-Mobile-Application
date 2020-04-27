package com.chanaka.bodima;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chanaka.bodima.adapters.User;
import com.chanaka.bodima.auth.Startup_View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.chanaka.bodima.helpers.TokenGenerator;


public class CreateGroupActivity extends AppCompatActivity {

    private static final int ID_LENGTH = 6;

    private EditText inputNew, inputRejoin;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private Button btnNew, btnRejoin,btn_backtologin;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        inputNew = (EditText) findViewById(R.id.newgroup);
        inputRejoin = (EditText) findViewById(R.id.rejoingroup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnNew = (Button) findViewById(R.id.btn_newgroup);
        btnRejoin = (Button) findViewById(R.id.btn_rejoingroup);
        btn_backtologin=(Button)findViewById(R.id.btn_back_to_login);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newGroup = inputNew.getText().toString();
                final String GroupId = TokenGenerator.getRandomToken(6);

                DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid() + "/name");
                databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("Admin").setValue("Admin");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Group creation and registration
                        DatabaseReference ref = database.getReference();
                        ref.child("groupes").child(GroupId).child("users").child(auth.getCurrentUser().getUid()).setValue(dataSnapshot.getValue());
                        ref.child("groupes").child(GroupId).child("name").setValue(newGroup);
                        ref.child("users").child(auth.getCurrentUser().getUid()).child("groupe").setValue(GroupId);


                        //Transfer of data to the next activity
                        Intent intent=new Intent(CreateGroupActivity.this, ConfirmationGroupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        intent.putExtra("type", "new");
                        intent.putExtra("groupName", newGroup);
                        intent.putExtra("groupId", GroupId);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }

                });
            }
        });


        btn_backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateGroupActivity.this, Startup_View.class));
            }
        });

        //Button to join an existing group
        btnRejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String groupId = inputRejoin.getText().toString().trim();

                if(groupId.length() == ID_LENGTH) {

                    DatabaseReference ref = database.getReference("groupes/" + groupId);

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String groupName = (String) dataSnapshot.child("name").getValue();

                            if(groupName != null) {

                                DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid() + "/name");
                                databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("Admin").setValue("Member");

                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        DatabaseReference ref = database.getReference();
                                        ref.child("users").child(auth.getCurrentUser().getUid()).child("groupe").setValue(groupId);
                                        ref.child("groupes").child(groupId).child("users").child(auth.getCurrentUser().getUid()).setValue(dataSnapshot.getValue());

                                        Intent intent = new Intent(CreateGroupActivity.this, ConfirmationGroupActivity.class);
                                        intent.putExtra("type", "reJoin");
                                        intent.putExtra("groupName", groupName);
                                        intent.putExtra("groupId", groupId);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            } else {
                                Toast.makeText(CreateGroupActivity.this, "The group does not exist Check the ID",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                } else {
                    Toast.makeText(CreateGroupActivity.this, "The ID is Invalid, Ask your friends to Resend",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
