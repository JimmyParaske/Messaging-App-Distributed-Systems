package com.example.katanemhmenaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

public class Pop extends MainActivity implements Serializable {


    private Button btnkataxorisi;
    private EditText edtinsertTopic;
    private String topicname;

    public static final String TOPICNAME="TOPICNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        btnkataxorisi = (Button) findViewById(R.id.btnkataxorisi);
        edtinsertTopic = (EditText) findViewById(R.id.edtinsertTopic);





        btnkataxorisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                topicname = edtinsertTopic.getText().toString();

                if (topicname.length() >0) {


                    Intent i = new Intent(Pop.this, MessagesWindow.class);
                    i.putExtra(TOPICNAME, topicname);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }


        });
    }
}
