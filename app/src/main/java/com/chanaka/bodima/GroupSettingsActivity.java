package com.chanaka.bodima;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chanaka.bodima.dialogs.DialogClear;
import com.chanaka.bodima.dialogs.IdentifyGroupDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.chanaka.bodima.dialogs.LeaveGroupDialog;
import com.chanaka.bodima.dialogs.DeleteGroupDialog;
import com.chanaka.bodima.adapters.User;
import com.chanaka.bodima.adapters.UserAdapter;
import com.chanaka.bodima.dialogs.RemoveUserDialog;

import java.util.ArrayList;

public class GroupSettingsActivity extends AppCompatActivity {

    private Button btnRemove, btnLeave, btnId;
    private ImageButton btnReturn;
    private ListView listUsers;
    private TextView groupName;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayAdapter<User> adapter;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        groupName = (TextView) findViewById(R.id.nom_groupe_texte);
        btnReturn = (ImageButton) findViewById(R.id.btn_groupe_return);
        btnRemove = (Button) findViewById(R.id.btn_delete_groupe);
        btnLeave = (Button) findViewById(R.id.btn_leave_groupe);
        btnId = (Button) findViewById(R.id.btn_display_id);
        listUsers = (ListView) findViewById(R.id.listview_users);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        final ArrayList<User> users = new ArrayList<>();

        adapter = new UserAdapter(GroupSettingsActivity.this, R.layout.row_list_users, users);
        listUsers.setAdapter(adapter);


        DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid() + "/groupe");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                groupId = (String) dataSnapshot.getValue();
                DatabaseReference refUsers = database.getReference("groupes/" + groupId + "/users");
                DatabaseReference refNom = database.getReference("groupes/" + groupId + "/name");

                refNom.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        groupName.setText((String) dataSnapshot.getValue());
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


                refUsers.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User user = new User(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                        users.add(user);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        User user = new User(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                        users.remove(user);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        DatabaseReference ref3 = database.getReference("users/" + auth.getCurrentUser().getUid());
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String admin = (String) dataSnapshot.child("Admin").getValue();

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (admin.equals("Admin")) {
                            DeleteGroupDialog deleteGroupDialog = new DeleteGroupDialog(GroupSettingsActivity.this, groupId);
                            deleteGroupDialog.show();
                        } else {
                            Toast.makeText(GroupSettingsActivity.this, "Only Admins Can Remove The Group", Toast.LENGTH_LONG).show();
                        }

                        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (admin.equals("Admin")) {
                                    User user = (User) parent.getItemAtPosition(position);
                                    RemoveUserDialog removeUserDialog = new RemoveUserDialog(GroupSettingsActivity.this, groupId, user.getId());
                                    removeUserDialog.show();
                                } else {
                                    Toast.makeText(GroupSettingsActivity.this, "Only Admins Can Remove A User", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        btnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentifyGroupDialog idGroupeDialog = new IdentifyGroupDialog(GroupSettingsActivity.this);
                idGroupeDialog.show();
            }
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeaveGroupDialog leaveGroupDialog = new LeaveGroupDialog(GroupSettingsActivity.this, groupId);
                leaveGroupDialog.show();
            }
        });


        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupSettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
}
