import java.io.*;
import java.math.*;
import java.net.*;
import java.security.*;
import java.util.*;

class Thread_UserNode {
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    String name_of_topic;
    int jump = 0;
    HashMap<String, Integer> read_pointer;
    
    public static void main(String[] args) {
        HashMap<String, Integer> topic_read_indexes = new HashMap<String, Integer>();
        Object read = new Object();
        Thread_UserNode user = new Thread_UserNode(); //dhmioyrgei xrhsth san thread
        Scanner scanner = new Scanner(System.in);
        System.out.println("What's your name?");
        user.userName = scanner.next();
        Integer[] brokers_port = {5001,5001,5001};
        String[] brokers_ip = {"192.168.1.3","192.168.1.3","192.168.1.3"};
        Random rand = new Random();
        int broker_id = rand.nextInt(brokers_port.length);
        int random_num_port = brokers_port[broker_id];           //pairnei ena tyxaio port gia thn proth syndesh toy se enan broker
        String random_num_ip = brokers_ip[broker_id];  
        Socket requestSocket;
        try {
            requestSocket = new Socket(random_num_ip, random_num_port); // localhost gia na epikoinwnoun ypologistes sto idio diktyo
            user.out = new ObjectOutputStream(requestSocket.getOutputStream());
            user.in = new ObjectInputStream(requestSocket.getInputStream());
            List<String> Broker_list = (List<String>) user.in.readObject();
            System.out.println("Type desired topic to register/create/send messages.");
            user.name_of_topic = scanner.next();
            Publisher publisher = new Publisher(user.out,user.in,user.userName,user.name_of_topic,Broker_list,user.jump,read,broker_id);
            Thread t_pub = new Thread(publisher);
            Consumer consumer =  new Consumer(user.out,user.in,user.name_of_topic,user.jump,read,topic_read_indexes);
            Thread t_consumer = new Thread(consumer);
            //dhmioyrgia threads ksexorista gia publisher kai consumer
            t_pub.start();
            t_consumer.start();   
            t_pub.join(); //to join perimenei mexri to allo thread na teleivsei
            t_consumer.join();
            user.jump= publisher.jump;  
            user.name_of_topic = publisher.name_of_topic;
            
            user.read_pointer = consumer.read_pointer;
            while(user.jump==1){ //gia thn allagh tou client se katallilo broker otan xreiazetai me bash to hash tou topic
                broker_id= user.hash_routine(user.name_of_topic);
                user.disconnect(requestSocket);
                requestSocket = new Socket(Broker_list.get(broker_id*2), Integer.parseInt(Broker_list.get((2*broker_id)+1)));
                user.out = new ObjectOutputStream(requestSocket.getOutputStream());
                user.in = new ObjectInputStream(requestSocket.getInputStream());
                Broker_list = (List<String>) user.in.readObject();
                read = new Object();
                publisher = new Publisher(user.out,user.in,user.userName,user.name_of_topic,Broker_list,user.jump,read,broker_id);
                t_pub = new Thread(publisher);
                consumer =  new Consumer(user.out,user.in,user.name_of_topic,0,read,user.read_pointer);
                t_consumer = new Thread(consumer);
                //dhmioyrgia threads ksexorista gia publisher kai consumer
                t_pub.start();
                t_consumer.start();   
                t_pub.join(); //to join perimenei mexri to allo thread na teleivsei
                t_consumer.join();
                user.jump= publisher.jump;  
                user.name_of_topic = publisher.name_of_topic;
                user.read_pointer = consumer.read_pointer;
                 
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }   
    }

    public void disconnect(Socket requestSocket){
        try {  
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

//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

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

        
}