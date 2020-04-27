package com.chanaka.bodima;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chanaka.bodima.adapters.Message;
import com.chanaka.bodima.adapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private Context activity;
    private EditText inputMessage;
    private Button btnSend;
    private ImageButton btnBack;
    private ListView listMessages;
    private ImageButton btnClearChat;
    private ArrayAdapter<Message> adapter;
    private Map<String, String> userInfos;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activity = this;

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnBack = (ImageButton) findViewById(R.id.btn_chat_back);
        listMessages = (ListView) findViewById(R.id.listview_messages);
        btnClearChat=(ImageButton)findViewById(R.id.btn_clear_chat);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        userInfos = new HashMap<>();
        final ArrayList<Message> messages = new ArrayList<>();

        adapter = new MessageAdapter(ChatActivity.this, R.layout.row_list_messages, messages);
        listMessages.setAdapter(adapter);

        DatabaseReference ref = database.getReference("users/" + auth.getCurrentUser().getUid());



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfos = (Map<String, String>) dataSnapshot.getValue();
                DatabaseReference ref = database.getReference("groupes/" + userInfos.get("groupe") + "/messages");
                progressBar.setVisibility(View.INVISIBLE);
                ref.addChildEventListener(new ChildEventListener() {


                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Message message = dataSnapshot.getValue(Message.class);
                        message.set_id(dataSnapshot.getKey());
                        messages.add(message);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}

                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Button Clear Chat
        btnClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference ref = database.getReference("groupes/" + userInfos.get("groupe") + "/messages");
                ref.removeValue();

                Intent intent=new Intent(ChatActivity.this,ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Toast.makeText(getApplicationContext(), "Your chat has been clear", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputMessage.getText().toString();
                DatabaseReference ref = database.getReference("groupes/" + userInfos.get("groupe") + "/messages");


                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getApplicationContext(), "Enter a Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                inputMessage.setText("");


                Map<String,String> datas = new HashMap<>();
                datas.put("message", message);
                datas.put("date", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
                datas.put("user", userInfos.get("name"));
                datas.put("userid", auth.getCurrentUser().getUid());

                String key = ref.push().getKey();
                ref.child(key).setValue(datas);
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


}
