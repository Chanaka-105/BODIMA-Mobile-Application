package com.chanaka.bodima.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.chanaka.bodima.R;

public class Dialog_About_us extends Dialog {

    private Activity c;
    private Button exitButton;
    private String about_us;

    public Dialog_About_us(Activity a) {
        super(a);
        this.c = a;
        this.about_us = about_us;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog__about_us);

        exitButton = (Button) findViewById(R.id.btn_close);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

