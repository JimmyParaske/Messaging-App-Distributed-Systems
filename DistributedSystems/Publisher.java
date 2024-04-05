import ergasiakatanemhmena.paketo.doulepse.Pair;
import ergasiakatanemhmena.paketo.doulepse.Value;

import java.io.*;
import java.math.*;
import java.security.*;
import java.util.*;

class Publisher implements Runnable{
    ObjectOutputStream out;
    ObjectInputStream in;
    String userName;
    String name_of_topic;
    List<String> BrokerList;
    int broker_id;
    int jump;
    Object read;

    public Publisher(ObjectOutputStream out,ObjectInputStream in,String userName,String name_of_topic,List<String> BrokerList,int jump,Object read,int broker_id){
        this.out = out;
        this.in= in;
        this.name_of_topic = name_of_topic;
        this.userName= userName;
        this.BrokerList = BrokerList;
        this.jump = jump;
        this.read= read;
        this.broker_id= broker_id;
    }

    public void run() {
        this.Behaviour();
    }

    public synchronized void Behaviour(){ //na mhn ginetai taytoxronh epejergasia sygkekrimenhs mnhmhs apo ta threads 
        try{int action = 0;
            if(this.broker_id == this.hash_routine(this.name_of_topic)){ //eimaste ston sosto broker
                this.out.writeInt(1);
                this.out.flush();
                register(this.name_of_topic);
                
            }
            else{       //den eimaste ston sosto broker
                action = 4;
                this.out.writeInt(0);
                this.out.flush();
                this.jump =1; //allagh broker
                synchronized(read){
                    read.notifyAll();
                }
            }
            Scanner scanner = new Scanner(System.in);
            while(action!=4){ //menu
                System.out.println("1. Switch/create topic");
                System.out.println("2. Publish a message to a topic");
                System.out.println("4. Disconnect");
                System.out.println("Give number of desired action");
                synchronized(read){
                    System.out.println("New Messages");
                    read.notifyAll();
                }
                action = scanner.nextInt();
                out.writeInt(action);
                out.flush();
                if(action == 1){
                    System.out.println("Give desired topic");
                    scanner.nextLine();
                    this.name_of_topic = scanner.nextLine();
                    action = 4;
                    this.out.writeInt(0);
                    this.out.flush();
                    this.jump =1; //allagh broker 
                    synchronized(read){
                        read.notifyAll();
                    }
                }
                else if(action == 2){
                    System.out.println("1. Message"); //epilogh ti minimatos thelei o xrhsths na steilei
                    System.out.println("2. File");
                    int answer = scanner.nextInt();
                    out.writeInt(answer);
                    out.flush();
                    if(answer == 1){
                        scanner.nextLine();
                        System.out.println("Give message");
                        String txt = scanner.nextLine();
                        Value message = new Value(userName, txt);
                        Publish(message);//kanei publish to minima 
                        
                    }
                    else{
                        scanner.nextLine();
                        System.out.println("Give file name");
                        String path = scanner.nextLine();
                        this.out.writeUTF(path);
                        this.out.flush();
                        FileSplit(path);//splitarei to arxeio wste na mporei na stalthei se chunks
                        out.writeInt(0);
                        out.flush();
                    }
                    System.out.println("woke up");
                }
                else{
                    jump = 0;
                    synchronized(read){
                        read.notifyAll();
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void register(String topic){ //eggrafh se ena topic 
        try {
            this.out.writeUTF(topic);
            this.out.flush();
            this.out.writeUTF(this.userName);
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void Publish(Value message){
        push(message);
    }


//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void push(Value message){ //stelnei ta mhnymata se morfh pair me to onoma toy apostolea kai ton mhnyma toy 
        try {
            Pair pair = new Pair<String,Value>(this.name_of_topic, message);
            
            this.out.writeObject(pair);
            this.out.flush();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
   
    public void FileSplit(String path){
            File ifile = new File(path); 
            FileInputStream fis;
            int fileSize = (int) ifile.length();
            int  read = 0, readLength = 512 * 1024;
            byte[] byteChunk;
            List<byte[]> files = new ArrayList<byte[]>();
            try {
                fis = new FileInputStream(ifile);
                while (fileSize > 0) {
                    byteChunk = new byte[readLength];
                    read = fis.read(byteChunk, 0, readLength);
                    fileSize -= read;
                    // assert(read==byteChunk.length);
                   
                    files.add(byteChunk);
                    
                    // File f1 = new  File(newName);
                    // chunk = new FileOutputStream(f1);
                    // chunk.write(byteChunk);
                    // chunk.flush();
                    // chunk.close();
                    this.out.writeInt(1);
                    this.out.flush();
                    this.Publish(new Value(this.userName, byteChunk));
                    byteChunk = null;
                    // chunk = null;
                }
                fis.close();
                fis = null;
                // ifile.delete();
                
                }catch (FileNotFoundException e ){
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    }


//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

public int hash_routine(String topicname){
    String sha1 = encryptThisString(topicname);
    BigInteger parsedsha1 = new BigInteger(sha1,16);
    BigInteger b1 = new BigInteger("3");
    int parsed = (parsedsha1.mod(b1)).intValue();
    return parsed;
}

//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

public String encryptThisString(String input)
{
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte[] messageDigest = md.digest(input.getBytes());

        BigInteger no = new BigInteger(1, messageDigest);

        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        // return the HashText
        return hashtext;
    }

    // For specifying wrong message digest algorithms
    catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
}
    
}
