package com.example.katanemhmenaapplication;
import ergasiakatanemhmena.paketo.doulepse.Pair;
import ergasiakatanemhmena.paketo.doulepse.Value;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ChatRoom extends AppCompatActivity {

    ArrayList<Pair> chatroommsg = new ArrayList<Pair>();

    private Button btnpisw;
    private Button btnsendmsg;
    private Button btnphotovideo;
    private TextView viewText;
    private ListView listchatroom;
    private EditText edtgrapseEdo;
    private Button btndeikseconvo;

    private String textinput;

    UserNode xrhsths;

    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    ArrayList<Value> mhnymata = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);


        btnpisw = (Button) findViewById(R.id.btnpisw);
        btnsendmsg = (Button) findViewById(R.id.btnsendmsg);
        btnphotovideo = (Button) findViewById(R.id.btnphotovideo);
        btndeikseconvo = (Button) findViewById(R.id.btndeikseconvo);

        viewText = (TextView) findViewById(R.id.viewText);
        edtgrapseEdo = (EditText) findViewById(R.id.edtgrapseEdo);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycle);

        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list);


        //listchatroom=(ListView)findViewById(R.id.listchatroom);
        //listchatroom.setAdapter(adapter);

        xrhsths = (UserNode) getIntent().getSerializableExtra("USER");

        File path = getApplicationContext().getFilesDir();
        File topics = new File(path,xrhsths.name_of_topic+".txt");
        try {
            Scanner chatroom = new Scanner(topics);
            while(chatroom.hasNextLine()){

                String chatroomstring = chatroom.nextLine();
                //adapter.add(chatroomstring);
                String[] pinakas= chatroomstring.split(" ");
                String msg1="";
                for(int s=1; s<pinakas.length; s++){

                    msg1=msg1+" "+pinakas[s];
                }
                Value v = new Value(pinakas[0],msg1);
                mhnymata.add(v);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        MessagesAdapter adapt = new MessagesAdapter(this,mhnymata);
        System.out.println(recycler==null);
        recycler.setAdapter(adapt);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        btnpisw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ChatRoom.this, MessagesWindow.class);
                i.putExtra("USER",xrhsths);
                startActivity(i);
            }


        });


        btndeikseconvo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                try {
                    new DeikseConvo().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                chatroommsg = xrhsths.messages;
                xrhsths.messages = new ArrayList<Pair>();

                for(Pair p:chatroommsg) {
                    String msg =  p.getValue1() + ": "+ (String) p.getValue2();
                    Value val = new Value(p.getValue1()+": ",(String)p.getValue2()+" string"+"\n");
                    mhnymata.add(val);
                    adapt.notifyDataSetChanged();
                }



                try {

                    File path = getApplicationContext().getFilesDir();
                    File history = new File(path,"hist.txt");
                    String opwsthes;
                    Scanner mychat = new Scanner(history);
                    int grammi=0;
                    while(mychat.hasNextLine()) {
                        opwsthes=mychat.nextLine();
                        String[] pinakas = opwsthes.split(" ");
                        if(pinakas[0].equals(xrhsths.name_of_topic)){

                            List<String> lines = Files.readAllLines(history.toPath());
                            lines.set(grammi, xrhsths.name_of_topic + " " + xrhsths.read_pointer.get(xrhsths.name_of_topic));
                            Files.write(history.toPath(), lines);
                        }
                        grammi++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });


        btnsendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    new Push().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                File path = getApplicationContext().getFilesDir();
                File topics = new File(path,xrhsths.name_of_topic+".txt");

                String input = edtgrapseEdo.getText().toString();
                textinput = xrhsths.userName + ": "+ input;

                try {

                    FileWriter fw = new FileWriter(topics,true);
                    //BufferedWriter bw = new BufferedWriter(fw);
                    //bw.write(topic+" "+0);
                    fw.write(textinput+" string"+"\n");
                    fw.close();



                    xrhsths.read_pointer.put(xrhsths.name_of_topic, xrhsths.read_pointer.get(xrhsths.name_of_topic)+1);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(edtgrapseEdo.getText().toString().length() > 0)
                {

                    Value val = new Value(xrhsths.userName + ": ",input+" string"+"\n");
                    mhnymata.add(val);
                    adapt.notifyDataSetChanged();
                }

            }
        });

    }



    private  class DeikseConvo extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                xrhsths.out.writeInt(3);
                xrhsths.out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            xrhsths.ViewConvo(xrhsths.name_of_topic, xrhsths.read_pointer);






            return null;
        }
    }


    private  class Push extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                xrhsths.out.writeInt(2);
                xrhsths.out.flush();
                xrhsths.out.writeInt(1);
                xrhsths.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Value message = new Value(xrhsths.userName, edtgrapseEdo.getText().toString());

            xrhsths.push(message);

            return null;
        }
    }



}