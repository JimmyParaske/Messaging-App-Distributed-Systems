package ergasiakatanemhmena.paketo.doulepse;

import java.io.Serializable;
import java.util.*;

public class Topic implements Serializable {
    public String name;
    public List<Object> messages = new ArrayList<Object>(); //ta mhnumata sto sygkekrimeno topic
    public List<String> subs = new ArrayList<String>(); //oi xrhstes pou einai registered sto sygkekrimeno topic

    public Topic(String name){
        this.name = name;
    }

    public void addMessage(Value message){
        this.messages.add(message);;
    }

    public void addSubs(String username){
        this.subs.add(username);
    }
    
}
