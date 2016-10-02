package it.uniba.di.ivu.progettotesi.appturismo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class Pagina implements Parcelable {
    public String latitudine;
    public String longitudine;
    public String descrizione;
    public String indirizzo;
    public String data;
    public String ora;
    public String diario;
    public String percorso;

    public static final Parcelable.Creator<Pagina> CREATOR
            = new Parcelable.Creator<Pagina>() {
        public Pagina createFromParcel(Parcel in) {
            return new Pagina(in);
        }

        public Pagina[] newArray(int size) {
            return new Pagina[size];
        }
    };

    public Pagina(){};

    private Pagina(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        latitudine = in.readString();
        longitudine = in.readString();
        percorso =in.readString();
        descrizione=in.readString();
        indirizzo =in.readString();
        data=in.readString();
        ora=in.readString();
        diario=in.readString();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(latitudine);
        out.writeString(longitudine);
        out.writeString(percorso);
        out.writeString(descrizione);
        out.writeString(indirizzo);
        out.writeString(data);
        out.writeString(ora);
        out.writeString(diario);
    }
    public int describeContents() {
        return 0;
    }

    public Pagina(String latitudine,String longitudine, String url,String descrizione,String luogo,String data,String ora,String diario){
        this.latitudine=latitudine;
        this.longitudine=longitudine;
        this.percorso =url;
        this.descrizione=descrizione;
        this.indirizzo =luogo;
        this.data=data;
        this.ora=ora;
        this.diario=diario;
    }
   /* public Pagina(String latitudine,String longitudine, String url,String luogo,String data,String ora){
        this.latitudine=latitudine;
        this.longitudine=longitudine;
        this.percorso =url;
        this.descrizione="niente";
        this.indirizzo =luogo;
        this.data=data;
        this.ora=ora;
    }*/

    String getPercorso(){return percorso;}
    String getLongitudine(){return longitudine;}
    String getLatitudine(){return latitudine;}
    String getDescrizione(){return descrizione;}
    String getIndirizzo(){return indirizzo;}
    String getDataPagina(){return data;}
    String getOra(){return ora;}
    String getDiario(){return diario;}
    Uri getImmagine(){
        String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + percorso;
        File file = new File(filePath);
        Uri uri=Uri.fromFile(file);
        return uri;
    }

}


