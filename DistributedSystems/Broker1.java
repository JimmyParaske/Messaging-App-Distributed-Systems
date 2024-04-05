import ergasiakatanemhmena.paketo.doulepse.Topic;

import java.io.*;
import java.net.*;
import java.util.*;


class Broker1 implements Serializable{
    List<Thread> connections = new ArrayList<>(); //oi syndeseis se enan broker
    List<Topic> topics = new ArrayList<Topic>(); //ta topics poy periexontai se enan broker
    List<String> usernamestable = new ArrayList<String>(); // ta onomata xrhstvn ston broker
    
    public static void main(String[] args) {
        new Broker1().openServer();
    }

    
    
    public void openServer() {
        ServerSocket providerSocket= null;
        while(true){
            try {
                // dhmioyrghse to provider socket kai apodexetai to connection me max 10 xrhstes
                
                providerSocket = new ServerSocket(5002, 10);
                Socket connection = providerSocket.accept();
                //kathe client einai ena thread 
                Thread client = new Thread(new BrokerResponses(connection,topics,usernamestable));
                connections.add(client);
                client.start();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    providerSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}

//-------------------------------------------------------------------------------------------------------------------------------

