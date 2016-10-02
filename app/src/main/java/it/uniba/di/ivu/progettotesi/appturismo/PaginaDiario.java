package it.uniba.di.ivu.progettotesi.appturismo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;

public class PaginaDiario implements Parcelable {
    private double latitudine;
    private double longitudine;
    private String descrizione;
    private String luogo;
    private String data,ora;
    private String diario;
    private BitmapDescriptor markerDescriptor;
    private Bitmap mBitmap;
    private String url;
    private ImageView mMarkerImageView;
    private View customMarkerView;
    String uri;

    public static final Parcelable.Creator<PaginaDiario> CREATOR
            = new Parcelable.Creator<PaginaDiario>() {
        public PaginaDiario createFromParcel(Parcel in) {
            return new PaginaDiario(in);
        }

        public PaginaDiario[] newArray(int size) {
            return new PaginaDiario[size];
        }
    };

    private PaginaDiario(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        latitudine = in.readDouble();
        longitudine = in.readDouble();
        url=in.readString();
        descrizione=in.readString();
        luogo=in.readString();
        data=in.readString();
        ora=in.readString();
        diario=in.readString();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(latitudine);
        out.writeDouble(longitudine);
        out.writeString(url);
        out.writeString(descrizione);
        out.writeString(luogo);
        out.writeString(data);
        out.writeString(ora);
        out.writeString(diario);
    }
    public int describeContents() {
        return 0;
    }

    public PaginaDiario(double latitudine,double longitudine, String url,String descrizione,String luogo,String data,String ora,String diario, ImageView mMarkerImageView, View customMarkerView){
        this.latitudine=latitudine;
        this.longitudine=longitudine;
        this.mMarkerImageView=mMarkerImageView;
        this.customMarkerView=customMarkerView;
        this.url=url;
        this.descrizione=descrizione;
        this.luogo=luogo;
        this.data=data;
        this.ora=ora;
        this.diario=diario;
        this.markerDescriptor=getBitmap(customMarkerView,url);
    }

    BitmapDescriptor getImmagineMappa(){return markerDescriptor;}

    private BitmapDescriptor getBitmap(View v, String url){
        Bitmap b=getMarkerBitmapFromView(v,url);

        markerDescriptor= BitmapDescriptorFactory.fromBitmap(b);
        return markerDescriptor;
    }

    String getUrl(){return url;}
    double getLongitudine(){return longitudine;}
    double getLatitudine(){return latitudine;}
    String getDescrizione(){return descrizione;}
    String getLuogo(){return  luogo;}
    String getDataPagina(){return data;}
    String getOra(){return ora;}
    String getDiario(){return diario;}
    Bitmap getImmagine(){
        String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + url;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;
    }



    private Bitmap getMarkerBitmapFromView(View view, String resId) {
       String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + resId;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);


        //Bitmap bitmap = BitmapFactory.decodeFile(resId);
        mMarkerImageView.setImageBitmap(bitmap);
        mMarkerImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();


        mBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);

        mBitmap = addBorderToBitmap(mBitmap, 6, Color.WHITE);

        // Add a border around the bitmap as shadow
        mBitmap = addBorderToBitmap(mBitmap, 3, Color.LTGRAY);

        // Set the ImageView image as drawable object
        mMarkerImageView.setImageBitmap(mBitmap);

        //mBitmap=addBorderToBitmap(mBitmap,10,Color.BLUE);


        return mBitmap;
    }

    protected Bitmap addBorderToBitmap(Bitmap srcBitmap, int borderWidth, int borderColor){
        // Initialize a new Bitmap to make it bordered bitmap
        Bitmap dstBitmap = Bitmap.createBitmap(
                srcBitmap.getWidth() + borderWidth*2, // Width
                srcBitmap.getHeight() + borderWidth*2, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        /*
            Canvas
                La classe Canvas tiene le chiamate "disegnare" .
                Per disegnare qualcosa , avete bisogno di 4 componenti di base:
                una bitmap per tenere i pixel ,
                una Canvas per ospitare le chiamate draw ( la scrittura in bitmap ) ,
                un disegno primitivo (ad esempio Rect , Percorso , testo, bitmap ) ,
                e un Paint ( per descrivere i colori e stili per il disegno ).
        */
        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(dstBitmap);

        // Initialize a new Paint instance to draw border
        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

        /*
            Rect
    Il rettangolo è rappresentato dalle coordinate dei suoi 4 bordi ( sinistra, in alto , in basso a destra ) .
    Questi campi sono accessibili direttamente .
    Utilizzare larghezza ( ) e l'altezza ( ) per recuperare larghezza e l'altezza del rettangolo .
    Nota : la maggior parte dei metodi non verificare che le coordinate sono ordinati in modo corretto
    ( cioè a sinistra < = destra e dall'alto < = in basso ) .
        */
        /*
            Rect(int left, int top, int right, int bottom)
                Create a new rectangle with the specified coordinates.
        */

        // Initialize a new Rect instance
        /*
            We set left = border width /2, because android draw border in a shape
            by covering both inner and outer side.
            By padding half border size, we included full border inside the canvas.
        */
        Rect rect = new Rect(
                borderWidth / 2,
                borderWidth / 2,
                canvas.getWidth() - borderWidth / 2,
                canvas.getHeight() - borderWidth / 2
        );

        /*
            public void drawRect (Rect r, Paint paint)
                Draw the specified Rect using the specified Paint. The rectangle will be filled
                or framed based on the Style in the paint.

            Parameters
                r : The rectangle to be drawn.
                paint : The paint used to draw the rectangle

        */
        // Draw a rectangle as a border/shadow on canvas
        canvas.drawRect(rect,paint);

        /*
            public void drawBitmap (Bitmap bitmap, float left, float top, Paint paint)
                Draw the specified bitmap, with its top/left corner at (x,y), using the specified
                paint, transformed by the current matrix.

                Note: if the paint contains a maskfilter that generates a mask which extends beyond
                the bitmap's original width/height (e.g. BlurMaskFilter), then the bitmap will be
                drawn as if it were in a Shader with CLAMP mode. Thus the color outside of the
                original width/height will be the edge color replicated.

                If the bitmap and canvas have different densities, this function will take care of
                automatically scaling the bitmap to draw at the same density as the canvas.

            Parameters
                bitmap : The bitmap to be drawn
                left : The position of the left side of the bitmap being drawn
                top : The position of the top side of the bitmap being drawn
                paint : The paint used to draw the bitmap (may be null)
        */

        // Draw source bitmap to canvas
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        /*
            public void recycle ()
                Free the native object associated with this bitmap, and clear the reference to the
                pixel data. This will not free the pixel data synchronously; it simply allows it to
                be garbage collected if there are no other references. The bitmap is marked as
                "dead", meaning it will throw an exception if getPixels() or setPixels() is called,
                and will draw nothing. This operation cannot be reversed, so it should only be
                called if you are sure there are no further uses for the bitmap. This is an advanced
                call, and normally need not be called, since the normal GC process will free up this
                memory when there are no more references to this bitmap.
        */
        srcBitmap.recycle();

        // Return the bordered circular bitmap
        return dstBitmap;
    }

}


