package it.uniba.di.ivu.progettotesi.appturismo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Diario {
    public String datacreazione;
    public String utente;
    public Map<String, Boolean> pagina= new HashMap<>();

    public Diario(){}
    public Diario(String datacreazione,String utente){
        this.datacreazione=datacreazione;
        this.utente=utente;
    }
    public void setPagina(String s,Boolean b){
        this.pagina.put(s,b);
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("datacreazione", datacreazione);
        result.put("utente", utente);
        result.put("pagina",pagina);

        return result;
    }

}

