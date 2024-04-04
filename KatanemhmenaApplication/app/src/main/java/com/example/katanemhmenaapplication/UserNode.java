package com.example.katanemhmenaapplication;

import ergasiakatanemhmena.paketo.doulepse.Pair;
import ergasiakatanemhmena.paketo.doulepse.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserNode implements Serializable {

    ArrayList<Pair> messages = new ArrayList<Pair>();
    //usernode
    String userName;
    static ObjectOutputStream out;
    static ObjectInputStream in;


    String name_of_topic;

    int jump = 0;
    HashMap<String, Integer> read_pointer = new HashMap<String,Integer>();

    //consumer
    String topic;
    int size = 0 ;
    Object read;

    //publisher
    List<String> BrokerList;
    int broker_id;

    static Socket requestsocket;

    public static synchronized Socket getsocket(){
        return requestsocket;
    }

    public static synchronized void setsocket(Socket socket){

        requestsocket = socket;
    }




    //usernode





    public void disconnect(Socket requestSocket){
        try {
            this.out.writeInt(4);
            this.out.flush();


            in.close(); out.close();
            requestSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    public int hash_routine(String topicname){ //hassharei to onoma toy topic me thn xrhsh toy SHA1 me thn bohteia ths parakato methodou
        String sha1 = encryptThisString(topicname);
        BigInteger parsedsha1 = new BigInteger(sha1,16);//dekaeksadiko
        BigInteger b1 = new BigInteger("3");
        int parsed = (parsedsha1.mod(b1)).intValue();
        return parsed;
    }

    public static String encryptThisString(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-1");


            // ypologizei to message digest tou input string
            // epistrefetai ws array apo byte
            byte[] messageDigest = md.digest(input.getBytes());

            // metatroph array se signum
            BigInteger no = new BigInteger(1, messageDigest);

            // metatroph se hex value
            String hashtext = no.toString(16);

            // prosthesi 0s gia na ginei 32bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }


            return hashtext;
        }


        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    //consumer

    public void ViewConvo(String topic, HashMap<String, Integer> read_pointer){ // emfanizei thn lista minimatwn gia ena sygkekrimeno topic
        try {
            out.writeUTF(topic);
            out.flush();
            System.out.println(read_pointer.get(topic));
            out.writeInt(read_pointer.get(topic));
            out.flush();
            int size = this.in.readInt();
            List<byte[]> chunks = new ArrayList<byte[]>();


            for(int i=this.read_pointer.get(topic); i<=size; i++){
                Pair msg = (Pair)this.in.readObject();

                if(msg.getValue2().getClass().getSimpleName().equals("byte[]")){
                    byte[] changed_file = (byte[]) msg.getValue2();
                    chunks.add(changed_file);
                    int chunks_size = this.in.readInt();
                    for(int x =0; x<chunks_size-1;x++){
                        msg = (Pair)this.in.readObject();
                        changed_file = (byte[]) msg.getValue2();
                        chunks.add(changed_file);
                    }
                    String filename = (String)((Pair)this.in.readObject()).getValue2();
                    this.FileMerge(chunks,filename);
                    System.out.println("Multimedia File " + filename +  " sent by " + msg.getValue1()+" saved to device!");
                }
                else if(msg.getValue2().getClass().getSimpleName().equals("String")){
                    messages.add(msg);
                    System.out.println(msg.getValue1() + ": " + (String) msg.getValue2());

                }
            }
            int topic_read_pointer =size + 1;
            this.read_pointer.remove(topic);
            this.read_pointer.put(topic, topic_read_pointer);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void FileMerge(List<byte[]> chunks,String filename){
        // String file_name = files.get(0).getName();
        // String[] split = file_name.split("0");
        // file_name = split[0] + split[1];
        File ofile = new File(filename);
        if(ofile.exists()){
        }
        else{
            FileOutputStream fos;
            FileInputStream fis;
            byte[] fileBytes;
            int bytesRead = 0;
            try {
                fos = new FileOutputStream(ofile,true);
                for (byte[] chunk : chunks) {
                    // fis = new FileInputStream(ofile);
                    // fileBytes = new byte[(int) file.length()];
                    // bytesRead = fis.read(fileBytes, 0,(int)  file.length());
                    // assert(bytesRead == fileBytes.length);
                    // assert(bytesRead == (int) file.length());

                    fos.write(chunk);
                    fos.flush();
                    // fileBytes = null;
                    // fis.close();
                    // fis = null;
                }
                fos.close();
                fos = null;
            }
            catch (FileNotFoundException e){
                e.printStackTrace();}
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }




    //publisher

    public void register(String topic){ //eggrafh se ena topic
        try {
            this.out.writeInt(1);
            this.out.flush();
            this.out.writeInt(1);
            this.out.flush();
            this.out.writeUTF(topic);
            this.out.flush();
            this.out.writeUTF(this.userName);
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void Publish(Value message){
        push(message);
    }


    public void push(Value message){ //stelnei ta mhnymata se morfh pair me to onoma toy apostolea kai ton mhnyma toy
        try {
            Pair pair = new Pair<String, Value>(this.name_of_topic, message);

            this.out.writeObject(pair);
            this.out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


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

}
