import ergasiakatanemhmena.paketo.doulepse.Topic;
import ergasiakatanemhmena.paketo.doulepse.Value;
import ergasiakatanemhmena.paketo.doulepse.Pair;


import java.io.*;
import java.net.*;
import java.util.*;


class BrokerResponses implements Runnable{  //xrhsimopoieitai gia na kanoume execute me threads
    List<Topic> topics = new ArrayList<Topic>();
    List<String> usernamestable = new ArrayList<String>();
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket connection;
    
    int closing = 0;

    public BrokerResponses(Socket connection, List<Topic> topics , List<String> usernamestable){
        this.connection = connection;
        this.topics = topics;
        this.usernamestable = usernamestable;
        
    }
//-------------------------------------------------------------------------------------------------------------------------------

    public void run(){
        System.out.println("Thread started");
        try {
            out = new ObjectOutputStream(this.connection.getOutputStream());
            in = new ObjectInputStream(this.connection.getInputStream());
            this.out.writeObject(CreateBrokerList());
            this.out.flush();
            int Switch = 0;//xrhsimopoieitai gia thn allagh broker me bash to hash
            String topic= "";
            int action = 0;
            String Username = "";


            while(action != 4){ 
                System.out.println("Waiting for user input...");
                action = this.in.readInt();
                
                if ( action == 1){
                    System.out.println(1);
                    Switch = this.in.readInt();
                    System.out.println("Switch : " + Switch);
                    if(Switch==1){
                        topic = this.in.readUTF();
                        Username = this.in.readUTF();
                        subscribe(topic,Username);
                    }
                    else{
                        action=4;

                    }
                }
                if (action == 2){
                    int answer = this.in.readInt();
                    if(answer == 1){
                        ReceiveMessageText();
                    }
                    else{
                        ReceiveMessageFile(topic,Username);
                    }
                }
                else if (action == 3){

                    this.pull();
                }
               
                
            }
            System.out.println("Thread closed");
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }
    
    public void subscribe(String topic_name, String user_name){ //o xrhsths ginetai subscribed sthn lista toy topic poy ton endiaferei, me thn bohtheia ths parakatw methodou
                                                                //an to topic pou zhta na egrafei den yparxei, tote to dhmioyrgoyme
            System.out.println("subscribe");
            String userName = user_name;
            String topicname = topic_name;
            boolean found = false;
            for(Topic topic:this.topics){
                if (topic.name.equals(topicname) ){
                    found = true;
                    topic.addSubs(userName);
                    System.out.println("Registered to topic!");
                }
            }
            if(!found){
                
                System.out.println("ergasiakatanemhmena.paketo.doulepse.Topic created!");
                createNewTopic(topicname,userName);
            }

    }

    public void createNewTopic(String topic_name,String username){ //dhmioyrgoiyme neo topic an ayto den yparxei hdh
        boolean found = false;
        for(Topic topic:this.topics){
            if (topic.name.equals(topic_name) ){
                found = true;
                System.out.println("ergasiakatanemhmena.paketo.doulepse.Topic already exists!");
            }
        }
        if(!found){
            
            Topic topic = new Topic(topic_name);
            topic.addSubs(username); // prosthetoume mesa sto topic enan xrhsth, ginetai subscribed sto topic auto
            this.topics.add(topic); // prosthetoume to topic sthn lista me ta topics
            System.out.println("Successfully added the topic");
        }
    }

    public void pull(){ //trabaei ta dedomena apo ena sygkekrimeno topic enos broker(mhnimata, onomataxrhstvn klp)
        System.out.println("pull");
        try{    
        String topicname = this.in.readUTF();
        int read_pointer = this.in.readInt();        
        int i=0;
        boolean found= false;
        Topic t1 = null;
        while(!found && i < this.topics.size()){
            if (topicname.equals(this.topics.get(i).name)){ //briskoume to zhtoumeno topic
                found = true;
                t1 = this.topics.get(i);
            }    
            i++;
        }
        this.out.writeInt(t1.messages.size()-1);
        this.out.flush();
        System.out.println("read pointer : " + read_pointer);
        
        //for(Object message: t1.messages){
        for(i  = read_pointer; i<t1.messages.size();i++){
            Value valued_message = (Value) t1.messages.get(i); //kataskeyh pair me onoma apostolea kai to mhnuma toy
            if(valued_message.getValueMessage().getClass().getSimpleName().equals("ArrayList")){
                boolean first =true;
                for(byte[] chunk : ((List<byte[]>) valued_message.getValueMessage())){
                    Pair pair = new Pair<String,Object>(valued_message.getPublisher(), chunk);
                    this.out.writeObject(pair);
                    this.out.flush();
                    if(first){
                        first = false;
                        this.out.writeInt(((List<byte[]>) valued_message.getValueMessage()).size());
                        this.out.flush();
                    }
                }
            }else{
                Pair pair = new Pair<String,Object>(valued_message.getPublisher(), valued_message.getValueMessage());
                this.out.writeObject(pair);
                this.out.flush();
            }
        }
    }catch(IOException e){
        e.printStackTrace();
    }
    }



    public List<String> CreateBrokerList(){ //diabazei apo to arxeio txt (me vash thn apo katw methodo) ta stoixeia ton brokers kai ta apothikeyei se mia lista
        List<String> BrokersList = new ArrayList<String>();
        String row;
        for(int x = 0 ; x<6 ; x++){
            row = readtest(x);
            BrokersList.add(row);
        }
        return BrokersList;
    }

    public String readtest(int x){ //diabazei stoixeia apo ena arxeio
        try {
            String data = null;
            File myObj = new File("brokers.txt"); //allagh path analoga ton ypologisth 
            Scanner myReader = new Scanner(myObj);
            
            int i = 0;
            while(i<=x){
                data = myReader.nextLine();
                i++;
            }
            myReader.close();
            return data;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null;
        }
        }

    public void ReceiveMessageText(){ //lambvanei ena object morfhs pair apo ton client me stoixeia to mhnima kai to topic pou stelnetai
        System.out.println("receive text");//kai to prosthetei sthn lista me ta minimata gia to antistoixo topic
    try {
            Pair message = (Pair)this.in.readObject();
            String topic = (String) message.getValue1();
            for(Topic t:this.topics){
                if (t.name.equals(topic) ){
                    t.addMessage((Value) message.getValue2());    
                }
            }          
        } catch (ClassNotFoundException e) {
            
            e.printStackTrace();
        }
        
        catch (IOException e) {
            
            e.printStackTrace();
        }
    }

    public synchronized void  ReceiveMessageFile(String topic,String Username){ //lambvanei ena object morfhs pair apo ton client me stoixeia to mhnima kai to topic pou stelnetai
        System.out.println("receive file");//kai to prosthetei sthn lista me ta minimata gia to antistoixo topic
    try {   String filename = this.in.readUTF();
            int continue_Recieve = this.in.readInt();   
            List<byte[]> chunks = new ArrayList<byte[]>();
            while(continue_Recieve==1){
                Pair message = (Pair)this.in.readObject();
                chunks.add((byte[]) ((Value) message.getValue2()).getValueMessage());  
                continue_Recieve = this.in.readInt();
            }
            Value message = new Value(Username,chunks);
            for(Topic t:this.topics){
                    if (t.name.equals(topic) ){
                        t.addMessage(message);   
                        message = new Value(Username, filename);
                        t.addMessage(message); 
                    }
                }     
        } catch (ClassNotFoundException e) {
            
            e.printStackTrace();
        }
        
        catch (IOException e) {
            
            e.printStackTrace();
        }
    }
}