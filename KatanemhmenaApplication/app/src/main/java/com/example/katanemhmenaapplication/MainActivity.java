package com.example.katanemhmenaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername;

    private Button btnLoginSignup;

    private TextView txtInfo;

    private String username;

    private UserNode xrhsths = new UserNode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        edtUsername = (EditText) findViewById(R.id.edtUsername);

        btnLoginSignup = (Button) findViewById(R.id.btnLoginSignup);

        txtInfo = (TextView) findViewById(R.id.txtInfo);

        File path = getApplicationContext().getFilesDir();
        File history = new File(path,"hist.txt");
        try {
            if (!history.createNewFile()){
                System.out.println(history.getAbsolutePath());
                Scanner myReader = new Scanner(history);
                String opwsthes;
                while(myReader.hasNextLine()){
                    opwsthes=myReader.nextLine();
                    String[] pinakas= opwsthes.split(" ");

                    xrhsths.read_pointer.put(pinakas[0],Integer.valueOf(pinakas[1]));


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }





        btnLoginSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //username pou mas dinei apo to input
                username = edtUsername.getText().toString();

                //mas phgainei sto allo parathyro pou tha exei ta available topics
                if (username.length()>0) {
                    xrhsths.userName = username;
                    handleLoginSignup();
                }
            }



        });



    }






    private void handleLoginSignup(){

        //kati isws prepei na grapsoume gia na pairnei to username o kwdikas mas idk
        //Toast.makeText(com.example.katanemhmenaapplication.MainActivity.this, "Welcome "+xrhsths.userName+ "!",Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(this, MessagesWindow.class);
        intent.putExtra("USER", xrhsths);
        Toast.makeText(MainActivity.this, "Welcome "+xrhsths.userName+ "!",Toast.LENGTH_SHORT).show();

        startActivity(intent);

    }

}