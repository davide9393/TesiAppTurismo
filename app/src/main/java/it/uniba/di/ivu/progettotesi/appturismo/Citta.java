package it.uniba.di.ivu.progettotesi.appturismo;

import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Morolla on 17/09/2016.
 */
public class Citta implements Parcelable {
    String nome_citta;
    String descrizione;
    String foto;
    public Map<String, Map<String,Map<String,Boolean>>> struttura= new HashMap<>();


    public static final Parcelable.Creator<Citta> CREATOR
            = new Parcelable.Creator<Citta>() {
        public Citta createFromParcel(Parcel in) {
            return new Citta(in);
        }

        public Citta[] newArray(int size) {
            return new Citta[size];
        }
    };

    private Citta(Parcel in) {
        readFromParcel(in);
    }


    public void readFromParcel(Parcel in) {
        nome_citta = in.readString();
        foto = in.readString();
        descrizione=in.readString();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(nome_citta);
        out.writeString(foto);
        out.writeString(descrizione);
    }
    public int describeContents() {
        return 0;
    }

    public Citta(){};
    public Citta(//String nome_citta,
                 String descrizione,String foto){
        // this.nome_citta=nome_citta;
        this.descrizione=descrizione;
        this.foto = foto;
    }

    public void setNome_citta(String nome_citta){
        this.nome_citta=nome_citta;
    }

    public String getNome_citta(){return nome_citta;}
    public String getDescrizione(){return descrizione;}
    public String getFoto(){return foto;}
    Uri getImmagine(){
       /* String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + foto;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;*/
        //String fileName = "alb.jpg";
        String completePath = Environment.getExternalStorageDirectory() + "/" + foto;

        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);
        return imageUri;

    }

    public void setStruttura(String s,Map<String,Map<String,Boolean>> b){
        this.struttura.put(s,b);
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("foto", foto);
        result.put("nome_citta", nome_citta);
        result.put("descrizione",descrizione);
        result.put("struttura",struttura);

        return result;
    }
}
