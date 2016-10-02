package it.uniba.di.ivu.progettotesi.appturismo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

public class Luogo {

    String titolo;
    String url;
    LatLng latLng;

    public Luogo(String titolo,String url,LatLng latLng){
        this.titolo=titolo;
        this.url=url;
        this.latLng=latLng;
    }

    public LatLng getLatLng(){return latLng;}
    public String getTitolo(){return titolo;}
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
