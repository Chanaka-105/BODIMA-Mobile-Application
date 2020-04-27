package com.chanaka.bodima;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ConfirmationGroupActivity extends AppCompatActivity {

    private TextView groupIdText, confirmationGroupText;
    private Button btnContinuer;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_group);


        groupIdText = (TextView) findViewById(R.id.confirmationgroup_id);
        confirmationGroupText = (TextView) findViewById(R.id.confirmationgroup_text);
        btnContinuer = (Button) findViewById(R.id.btn_continue);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.getString("type").equals("new")) {

                confirmationGroupText.setText("YOU HAVE CREATED A GROUP " +"'"+ extras.getString("groupName") +"'"+ " SHARE THIS ID WITH YOUR FRIENDS TO JOIN");
                groupIdText.setText(extras.getString("groupId"));
            } else if(extras.getString("type").equals("reJoin")) {

                confirmationGroupText.setText("YOU HAVE JOINED A GROUP "+"'"+ extras.getString("groupName") +"'"+ " YOU CAN NOW START USING THIS APP");
                groupIdText.setVisibility(View.GONE);
            }
        }


        btnContinuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ConfirmationGroupActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
