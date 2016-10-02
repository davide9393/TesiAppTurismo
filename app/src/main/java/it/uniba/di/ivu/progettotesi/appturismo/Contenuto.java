package it.uniba.di.ivu.progettotesi.appturismo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;

public class Contenuto implements Parcelable{
    String titolo;
    String descrizione;
    String telefono;
    Double lat;
    Double lon;
    String url;

    public static final Parcelable.Creator<Contenuto> CREATOR
            = new Parcelable.Creator<Contenuto>() {
        public Contenuto createFromParcel(Parcel in) {
            return new Contenuto(in);
        }

        public Contenuto[] newArray(int size) {
            return new Contenuto[size];
        }
    };

    private Contenuto(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        titolo = in.readString();
        url = in.readString();
        descrizione=in.readString();
        telefono=in.readString();
        lat=in.readDouble();
        lon=in.readDouble();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(titolo);
        out.writeString(url);
        out.writeString(descrizione);
        out.writeString(telefono);
        out.writeDouble(lat);
        out.writeDouble(lon);
    }
    public int describeContents() {
        return 0;
    }

    public Contenuto(String titolo,String url,String descrizione,String telefono, Double lat,Double lon){
        this.titolo=titolo;
        this.url=url;
        this.descrizione=descrizione;
        this.telefono=telefono;
        this.lat=lat;
        this.lon=lon;
    }

    public String getTitolo(){return titolo;}
    public String getDescrizione(){return descrizione;}
    public LatLng getLatLng(){return new LatLng(lat,lon);}
    public String getTelefono(){return telefono;}

    Uri getImmagine(){
       /* String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + url;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;*/
        //String fileName = "alb.jpg";
        String completePath = Environment.getExternalStorageDirectory() + "/" + url;

        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);
        return imageUri;

    }
}
