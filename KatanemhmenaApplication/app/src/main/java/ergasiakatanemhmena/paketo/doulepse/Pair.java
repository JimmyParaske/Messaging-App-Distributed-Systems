package ergasiakatanemhmena.paketo.doulepse;

import java.io.Serializable;

public class Pair<String , Value> implements Serializable  {
   
    private final String value1; 
    private final Value value2;


    public Pair(String value1, Value value2){
        this.value1 = value1;
        this.value2 = value2;
    }

    public String getValue1(){
        return this.value1; 
    }

    public Value getValue2(){
        return this.value2; 
    }
}
