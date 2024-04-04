package com.example.katanemhmenaapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MessagesWindow extends AppCompatActivity implements Serializable {


    private Button btnAddTopic;
    private Button btnlogout;

    private EditText topicname;

    private ListView lv;

    private String input;




    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;




    int i=0;

    UserNode xrhsths = new UserNode();


    ActivityResultLauncher<Intent> result = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {


                        String topic =  result.getData().getStringExtra(Pop.TOPICNAME);
                        boolean found = false;

                        for(String i: list){
                            if(i.equals(topic)){
                                found= true;
                            }
                        }

                        if(topic.length() > 0 && !found)
                        {
                            File path = getApplicationContext().getFilesDir();
                            File history = new File(path,"hist.txt");
                            File topics = new File(path,topic+".txt");

                            try {
                                topics.createNewFile();
                                FileWriter fw = new FileWriter(history,true);
                                //BufferedWriter bw = new BufferedWriter(fw);
                                //bw.write(topic+" "+0);
                                fw.write(topic+" "+0+"\n");
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            xrhsths.read_pointer.put(topic,0);
                            xrhsths.name_of_topic = topic;
                            adapter.add(topic);
                            Toast.makeText(MessagesWindow.this, "added " + xrhsths.name_of_topic, Toast.LENGTH_SHORT).show();

                            new Register().execute();

                        }




                    }

                }


            })
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_window);


        btnAddTopic = (Button) findViewById(R.id.btnAddTopic);
        btnlogout = (Button) findViewById(R.id.btnlogout);



        topicname = (EditText) findViewById(R.id.editText);




        xrhsths = (UserNode) getIntent().getSerializableExtra("USER");

        if(xrhsths.requestsocket == null) {
            new Initialize().execute();
        }


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list);


        lv=(ListView)findViewById(R.id.list);
        lv.setAdapter(adapter);

        lv.setClickable(true);

        File path = getApplicationContext().getFilesDir();
        File history = new File(path,"hist.txt");
        try {
            Scanner msgWindow = new Scanner(history);
            while(msgWindow.hasNextLine()){
                String tpc = msgWindow.nextLine();
                adapter.add(tpc.split(" ")[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        btnAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MessagesWindow.this, Pop.class);



                //startActivityForResult(a,999);
                result.launch(a);



            }


        });





        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MessagesWindow.this, "Logout successful!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MessagesWindow.this, MainActivity.class));
                new Disconnect().execute();
                System.exit(0);
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tpcname = (String) parent.getItemAtPosition(position);

                xrhsths.name_of_topic = tpcname;
                new Register().execute();
                Toast.makeText(MessagesWindow.this, "topic name is "+ xrhsths.name_of_topic,Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MessagesWindow.this, ChatRoom.class);

                i.putExtra("USER", xrhsths);


                startActivity(i);
            }
        });



    }



    private  class Register extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            int parsed = xrhsths.hash_routine(xrhsths.name_of_topic);
            System.out.println(parsed);
            if(parsed==xrhsths.broker_id){
                xrhsths.register(xrhsths.name_of_topic);

            }
            else{
                try {
                    xrhsths.out.writeInt(1);
                    xrhsths.out.flush();
                    xrhsths.out.writeInt(0);
                    xrhsths.out.flush();
                    xrhsths.disconnect(xrhsths.requestsocket);
                    xrhsths.requestsocket = new Socket(xrhsths.BrokerList.get(parsed*2), Integer.parseInt(xrhsths.BrokerList.get((2*parsed)+1)));
                    xrhsths.out = new ObjectOutputStream(xrhsths.requestsocket.getOutputStream());
                    xrhsths.in = new ObjectInputStream(xrhsths.requestsocket.getInputStream());
                    xrhsths.BrokerList = (List<String>) xrhsths.in.readObject();
                    xrhsths.register(xrhsths.name_of_topic);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    private  class Disconnect extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            xrhsths.disconnect(xrhsths.getsocket());



            return null;
        }
    }


    private class Initialize extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Integer[] brokers_port = {5001,5002,5003};
                String[] brokers_ip = {"192.168.1.3","192.168.1.3","192.168.1.3"};
                Random rand = new Random();
                xrhsths.broker_id = rand.nextInt(brokers_port.length);
                int random_num_port = brokers_port[xrhsths.broker_id];
                String random_num_ip = brokers_ip[xrhsths.broker_id];

                xrhsths.setsocket(new Socket(random_num_ip,random_num_port));

                xrhsths.out = new ObjectOutputStream(xrhsths.getsocket().getOutputStream());
                xrhsths.in = new ObjectInputStream(xrhsths.getsocket().getInputStream());

                xrhsths.BrokerList= (List<String>) xrhsths.in.readObject();


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


