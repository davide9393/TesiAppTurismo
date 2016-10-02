package it.uniba.di.ivu.progettotesi.appturismo;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DettaglioFotoDiarioActivity extends AppCompatActivity {

    ActionBarDrawerToggle mDrawerToggle;
    ActionBar actionBar;
    public static Toolbar toolbar;
    Pagina paginaDiario;
    ImageView imageView;
    TextView descrizione;
    TextView luogo;
    TextView data;
    TextView ora;
    String filePath;
    Bitmap bitmap;

    private CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        setContentView(R.layout.fragment_dettaglio_foto_diario);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView=(ImageView) findViewById(R.id.imageView);
        descrizione=(TextView) findViewById(R.id.titolo);
        luogo=(TextView) findViewById(R.id.luogo);
        data=(TextView) findViewById(R.id.data);
        ora=(TextView) findViewById(R.id.ora);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            paginaDiario = (Pagina) extras.getParcelable("pagina");

            Glide.with(this).load(paginaDiario.getImmagine()).centerCrop().into(imageView);

            descrizione.setText(paginaDiario.getDescrizione());
            luogo.setText(paginaDiario.getIndirizzo());
            data.setText(paginaDiario.getDataPagina());
            ora.setText(paginaDiario.getOra());

            /*imageView.buildDrawingCache();
            final Bitmap bmap = imageView.getDrawingCache();*/

            bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), paginaDiario.getImmagine());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            shareDialog = new ShareDialog(this);
            final ShareButton shareButton = (ShareButton)findViewById(R.id.share_btn);
            final SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).setCaption("Testing").build();
            final SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
            shareButton.setShareContent(content);
            assert shareButton != null;
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareDialog.show(content);
                }
            });






            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog builder = new Dialog(DettaglioFotoDiarioActivity.this);
                    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    builder.getWindow().setBackgroundDrawable(
                            new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //nothing;
                        }
                    });

                    ImageView imageView2 = new ImageView(DettaglioFotoDiarioActivity.this);
                    Glide.with(DettaglioFotoDiarioActivity.this).load(paginaDiario.getImmagine()).centerCrop().into(imageView2);
                    imageView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.hide();
                        }
                    });
                    builder.addContentView(imageView2, new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    builder.show();
                }
            });


        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_modify:
                //  Toast.makeText(DettaglioFotoDiarioActivity.this, "premuto", Toast.LENGTH_SHORT).show();
                Bundle extras = new Bundle();
                Intent intent = new Intent(DettaglioFotoDiarioActivity.this, InserisciPaginaDiario.class);
                Intent i=getIntent();
                extras.putParcelable("pagina", paginaDiario);
                extras.putString("titoloDiario", i.getStringExtra("titoloDiario"));
                intent.putExtras(extras);
                finish();
                startActivity(intent);
                return true;
            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(DettaglioFotoDiarioActivity.this);
                builder.setTitle(getResources().getString(R.string.cancellapaginadiario));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference mPagina=mDatabase.child("Pagina");
                        Intent i = getIntent();
                        String oldImageFileName = paginaDiario.percorso.substring(21, paginaDiario.percorso.length() - 4);

                        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File f = new File(baseDir + File.separator + paginaDiario.percorso);
                        f.delete();

                        finish();
                        mDatabase.child("Pagina").child(oldImageFileName).removeValue();
                        mDatabase.child("Diario").child(i.getStringExtra("titoloDiario")).child("pagina").child(oldImageFileName).removeValue();


                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dettaglio_pagina_diario, menu);
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
