import ergasiakatanemhmena.paketo.doulepse.Pair;

import java.io.*;
import java.util.*;

class Consumer implements Runnable{
    ObjectOutputStream out;
    ObjectInputStream in;
    String topic;
    int size = 0 ;
    int jump;
    Object read;
    HashMap<String, Integer> read_pointer;

    public Consumer(ObjectOutputStream out,ObjectInputStream in,String topic,int jump, Object read, HashMap<String,Integer> read_pointer){
        this.out=out;
        this.in = in;
        this.topic = topic;
        this.jump = jump;
        this.read = read;
        this.read_pointer = read_pointer;
        if(!this.read_pointer.containsKey(topic)){
            this.read_pointer.put(topic, 0);
        }
    }

    public synchronized void run() {
        while(jump==0){//trexei mono otan eimaste ston sosto broker, an to jump ginei 1 tote allazoyme broker
                
            try {
                    synchronized(this.read){
                        
                        read.wait();}
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            try {
                this.out.writeInt(3);
                this.out.flush();
                jump = this.in.readInt();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(jump==0){
            ViewConvo(this.topic,this.read_pointer);
            }
            
    }
}
        
    
    
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public  void ViewConvo(String topic,HashMap<String, Integer> read_pointer){ // emfanizei thn lista minimatwn gia ena sygkekrimeno topic
        try {
            this.out.writeUTF(topic);
            this.out.flush();
            this.out.writeInt(read_pointer.get(topic));
            this.out.flush();
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
                        System.out.println(msg.getValue1() + " : " + (String) msg.getValue2());
                        
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

//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
}
