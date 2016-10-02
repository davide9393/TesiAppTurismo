package it.uniba.di.ivu.progettotesi.appturismo;

import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Morolla on 23/09/2016.
 */
public class Sconto {
    String nome_sconto;
    String descrizione;
    String url;

    public Sconto(String nome_sconto,String descrizione,String url){
        this.nome_sconto=nome_sconto;
        this.descrizione=descrizione;
        this.url=url;
    }

    public String getNome_sconto(){return nome_sconto;}
    public String getDescrizione(){return descrizione;}
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
