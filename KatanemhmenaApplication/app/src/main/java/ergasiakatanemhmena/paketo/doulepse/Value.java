package ergasiakatanemhmena.paketo.doulepse;

import java.io.Serializable; //used for representing objects to stream of bytes

public class Value implements Serializable{
    
    private Object value_message; 
    private String publisher; 


    public Value(String publisher, Object value_message){
        this.value_message = value_message;
        this.publisher = publisher; 
    }

    public String getPublisher(){
        return publisher; 
    }
    
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public Object getValueMessage(){
        return this.value_message;
    }

    public void setValueMessage(Object value_message){
        this.value_message = value_message;
    }
}