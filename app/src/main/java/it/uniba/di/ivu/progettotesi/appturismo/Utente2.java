package it.uniba.di.ivu.progettotesi.appturismo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Morolla on 21/09/2016.
 */
public class Utente2 {
    public String cognome;
    public String nome;
    public String password;

    public Utente2(){}
    public Utente2(String nome, String cognome, String password){
        this.nome=nome;
        this.cognome=cognome;
        this.password=password;
    }

}
