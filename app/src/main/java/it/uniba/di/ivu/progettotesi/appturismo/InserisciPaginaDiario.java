package it.uniba.di.ivu.progettotesi.appturismo;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class InserisciPaginaDiario extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private int REQUEST_POSITION=4;
    private FloatingActionButton btnSelezionaFoto;
    private ImageView ViewFoto;
    private ImageButton btnPosizione;
    private ImageButton btnDataOra;
    private Button btnConfirm;
    private final static long LOCATION_DURATION_TIME = 20000;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private TextView mLocationTimeView;
    private TextView mGeoCoderView;
    private final static int MAX_GEOCODE_RESULTS = 1;
    private GoogleApiClient mGoogleApiClient;
    private double posizionelat;
    private double posizionelong;
    public Location location = new Location("");
    private String position=null;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String data=null;
    private String ora=null;
    private String imageFileName=null;
    private String latAsString;
    private String lonAsString;
    private String stringUri;
    private EditText editDidascalia;
    private String oldImageFileName="null";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "AT";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
    LocationManager locationManager;
    public static Toolbar toolbar;

    private Bitmap bitmap=null;
    private String titoloDiario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserisci_pagina_diario);

        Intent i=getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titoloDiario=i.getStringExtra("titolodiario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .build();


        mLocationTimeView = (TextView) findViewById(R.id.my_location_time);
        mGeoCoderView = (TextView) findViewById(R.id.my_location_geocoding);
        editDidascalia= (EditText) findViewById(R.id.editTextDidascalia);
        ViewFoto = (ImageView) findViewById(R.id.ivImage);
        btnSelezionaFoto = (FloatingActionButton) findViewById(R.id.btnSelectPhoto);
        btnPosizione = (ImageButton) findViewById(R.id.btnSelectPosition);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnDataOra = (ImageButton) findViewById(R.id.btnSelectDateHour);

        final DatabaseReference mpagina=mDatabase.child("Pagina");
        mpagina.keepSynced(true);


        mAlbumStorageDirFactory = new AlbumDirFactory();


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }


        ///accesso alla schermata dopo aver premuto su modifica pagina
        Bundle bundle=i.getExtras();
        Pagina modifica=bundle.getParcelable("pagina");
        if(modifica!=null){
            editDidascalia.setText(modifica.descrizione);
            mLocationTimeView.setText(modifica.data+", "+modifica.ora);
            mGeoCoderView.setText(modifica.indirizzo);

            titoloDiario=bundle.getString("titoloDiario");
            oldImageFileName=modifica.percorso.substring(21,modifica.percorso.length()-4);
            data=modifica.data;
            ora=modifica.data;
            latAsString=modifica.latitudine;
            lonAsString=modifica.longitudine;
            stringUri=modifica.percorso;

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + modifica.percorso;
            stringUri=filePath;

            Glide.with(getApplicationContext()).load(filePath).centerCrop().into(ViewFoto);

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            attivaGPS();
        }

        //SELEZIONA FOTO
        btnSelezionaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //VEDI FOTO
        ViewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vediFoto();
            }
        });

        //POSIZIONE
        btnPosizione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaPosizione();
            }
        });

        //DATA E ORA
        btnDataOra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selezionaDataOra();
            }
        });

        //CONFERMA
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ViewFoto.getDrawable()==null || mGeoCoderView.getText().toString()==null || data==null){
                    Toast.makeText(InserisciPaginaDiario.this, getResources().getString(R.string.datimancanti), Toast.LENGTH_SHORT).show();
                }else{
                    if(gallery){
                        try {
                            aggiungiFoto();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    final String substringUri=stringUri.substring(20);
                    imageFileName=stringUri.substring(41, stringUri.length() - 4);
                    Pagina pagina=new Pagina(latAsString,lonAsString,substringUri,editDidascalia.getText().toString(),mGeoCoderView.getText().toString(),data,ora,titoloDiario);

                    if(!oldImageFileName.equals(imageFileName)){
                        mpagina.getRoot().child("Diario").child(titoloDiario).child("pagina").child(oldImageFileName).removeValue();
                        mpagina.child(oldImageFileName).removeValue();

                        mpagina.child(imageFileName).setValue(pagina).addOnFailureListener(errorListener);
                        mpagina.getRoot().child("Diario").child(titoloDiario).child("pagina").child(imageFileName).setValue(true).addOnFailureListener(errorListener);
                        Toast.makeText(InserisciPaginaDiario.this, getResources().getString(R.string.successo), Toast.LENGTH_SHORT).show();

                    }
                    else {
                        mpagina.child(oldImageFileName).setValue(pagina).addOnFailureListener(errorListener);
                        Toast.makeText(InserisciPaginaDiario.this, getResources().getString(R.string.successo), Toast.LENGTH_SHORT).show();
                    }

                    finish();

                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume(){
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }














/////////////////SELEZIONE DELLA FOTO

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.scattafoto), getString(R.string.sceglidallagalleria), getString(R.string.annulla)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.sceglifoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.scattafoto))) {
                    cameraIntent();
                } else if (items[item].equals(getString(R.string.sceglidallagalleria))) {
                    galleryIntent();
                } else if (items[item].equals(getString(R.string.annulla))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    File f = null;
    ////////////////FOTOCAMERA
    private void cameraIntent() {
        gallery=false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            String timeStamp = new SimpleDateFormat(getString(R.string.simpledateformat)).format(new Date());
            imageFileName = JPEG_FILE_PREFIX + "_"+Utility.uid +"_"+timeStamp;
            File albumF = getAlbumDir();

            f = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
            mCurrentPhotoPath = f.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void onCaptureImageResult() {

        bitmap=null;

        if (mCurrentPhotoPath != null) {
            Glide.with(getApplicationContext()).load(mCurrentPhotoPath).centerCrop().into(ViewFoto);
            stringUri=mCurrentPhotoPath;
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    /*private void setPic() {

		*//* There isn't enough memory to open up more than a couple camera photos *//*
		*//* So pre-scale the target bitmap into which the file is decoded *//*

		*//* Get the size of the ImageView *//*
        *//*int targetW = ViewFoto.getWidth();
        int targetH = ViewFoto.getHeight();

		*//**//* Get the size of the image *//**//*
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		*//**//* Figure out which way needs to be reduced less *//**//*
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		*//**//* Set bitmap options to scale the image decode target *//**//*
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		*//**//* Decode the JPEG file into a Bitmap *//**//*
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);*//*


		*//* Associate the Bitmap to the ImageView *//*
        //ViewFoto.setImageBitmap(bitmap);
        //ViewFoto.setVisibility(View.VISIBLE);

        //ViewFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);


    }*/

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Glide.with(getApplicationContext()).load(imageUri).centerCrop().into(ViewFoto);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(stringUri);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

   /* private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        return f;
    }*/

    /*private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat(getString(R.string.simpledateformat)).format(new Date());
        imageFileName = JPEG_FILE_PREFIX + "_"+utente+"_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);

        return imageF;
    }*/

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("App Turismo", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    ///////////////////////////////////////////////////////////////


    /////////SCELTA DALLA GALLERIA

    private Boolean gallery=false;
    private void galleryIntent() {
        gallery=true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) throws IOException {
        bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ViewFoto.setImageBitmap(bitmap);
        ViewFoto.setVisibility(View.VISIBLE);
        ViewFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void aggiungiFoto() throws IOException {
        String timeStamp = new SimpleDateFormat(getString(R.string.simpledateformat)).format(new Date());
        imageFileName = JPEG_FILE_PREFIX + "_"+Utility.uid +"_"+timeStamp;
        OutputStream out;
        File createDir = getAlbumDir();
        f = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, createDir);
        f.createNewFile();
        out = new FileOutputStream(f);
        out.write(getBytesFromBitmap(bitmap));
        out.close();

        mCurrentPhotoPath = f.getAbsolutePath();
        stringUri=mCurrentPhotoPath;

        galleryAddPic();
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    ///////////////////////////////////////////////////////////



    ////////////SELEZIONE DELLA POSIZIONE
    private void selezionaPosizione() {
        final CharSequence[] items = {getString(R.string.posizionecorrente), getString(R.string.scegliposizione), getString(R.string.annulla)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.scegliposfoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.posizionecorrente))) {
                    posizioneCorrente();
                } else if (items[item].equals(getString(R.string.scegliposizione))) {
                    scegliPosizione();
                } else if (items[item].equals(getString(R.string.annulla))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    ///////////////////////POSIZIONE CORRENTE
    private void posizioneCorrente() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setExpirationDuration(LOCATION_DURATION_TIME);
        if (ActivityCompat.checkSelfPermission(InserisciPaginaDiario.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InserisciPaginaDiario.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                displayLocation(location);
            }
        });
    }

    private void displayLocation(Location currentLocation) {
        if (currentLocation != null) {
            latAsString=Double.toString(currentLocation.getLatitude());
            lonAsString=Double.toString(currentLocation.getLongitude());
            geoCodeLocation(currentLocation);
        } else {
            Toast.makeText(InserisciPaginaDiario.this, R.string.my_location_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    ////////////////SCELTA DALL' UTENTE
    private void scegliPosizione() {
        Intent sceglipos = new Intent(InserisciPaginaDiario.this, PaginaDiarioSceltaPosizione.class);
        startActivityForResult(sceglipos, REQUEST_POSITION);
    }

    private void onSelectPositionResult(Intent data){
        posizionelat = data.getExtras().getDouble("latitude");
        posizionelong = data.getExtras().getDouble("longitude");
        position = data.getExtras().getString("position");

        if(posizionelat==0.0 && posizionelong==0.0){}
        else if(position=="" || position==null) {
            latAsString=Double.toString(posizionelat);
            lonAsString=Double.toString(posizionelong);
            location.setLatitude(posizionelat);
            location.setLongitude(posizionelong);

            geoCodeLocation(location);
        }
        else{
            latAsString=Double.toString(posizionelat);
            lonAsString=Double.toString(posizionelong);
            mGeoCoderView.setText(position);
        }

    }


    //////////////////SCELTA DELLA DATA E DELL'ORA

    private void selezionaDataOra() {
        final CharSequence[] items = {getString(R.string.dataoracorrente), getString(R.string.sceglidataora), getString(R.string.annulla)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.sceglidataorafoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.dataoracorrente))) {
                    DataOraCorrente();
                } else if (items[item].equals(getString(R.string.sceglidataora))) {
                    scegliDataOra();
                } else if (items[item].equals(getString(R.string.annulla))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void DataOraCorrente() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            attivaGPS();
        }else{
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setNumUpdates(1)
                    .setExpirationDuration(LOCATION_DURATION_TIME);
            if (ActivityCompat.checkSelfPermission(InserisciPaginaDiario.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InserisciPaginaDiario.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    DataOra(location);
                }
            });
        }

    }

    private void DataOra(final Location currentLocation){
        long locationTime = System.currentTimeMillis();
        if (currentLocation != null) {
            locationTime = currentLocation.getTime();
        } else {
            Toast.makeText(this, R.string.my_location_not_available, Toast.LENGTH_SHORT).show();
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.my_location_format));
        final SimpleDateFormat time = new SimpleDateFormat(getResources().getString(R.string.my_time_format));
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(locationTime);
        final String timeAsString = sdf.format(calendar.getTime());
        final String timeAsString2 = time.format(calendar.getTime());
        data =timeAsString;
        ora =timeAsString2;
        mLocationTimeView.setText(data + " ," + ora);

    }


    ///////////////SCELTA DATA E ORA DELL'UTENTE
    private void scegliDataOra(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        data=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        TimePickerDialog timePickerDialog = new TimePickerDialog(InserisciPaginaDiario.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        ora=hourOfDay + ":" + minute;
                                        mLocationTimeView.setText(data+", "+ora);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }











    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                try {
                    onSelectFromGalleryResult(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult();
        }
        if(requestCode==REQUEST_POSITION){
            onSelectPositionResult(data);
        }
    }








    private final GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(InserisciPaginaDiario.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InserisciPaginaDiario.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        @Override
        public void onConnectionSuspended(int i) {}

    };

    private final GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(InserisciPaginaDiario.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                connectionResult.getErrorCode();
            }
        }
    };









    /////   GEOCODING   /////

    static double latitudinegeo;
    static double longitudinegeo;
    private Location location2=new Location("");
    private void geoCodeLocation(final Location location) {
        location2=location;
        if (null != location) {
            if (Geocoder.isPresent()) {
                final GeoCoderAsyncTask geoCoderAsyncTask = new GeoCoderAsyncTask(this, MAX_GEOCODE_RESULTS);
                geoCoderAsyncTask.execute(location);
                latitudinegeo=location.getLatitude();
                longitudinegeo=location.getLongitude();
            } else {
                Toast.makeText(InserisciPaginaDiario.this, R.string.my_location_geocoder_not_available, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(InserisciPaginaDiario.this, R.string.my_location_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    private class GeoCoderAsyncTask extends AsyncTask<Location, Void, List<Address>> {

        private static final String PROGRESS_TAG = "PROGRESS_TAG";
        private final WeakReference<InserisciPaginaDiario> inserisciPaginaDiarioWeakReference;
        private final int mMaxResult;
        private ProgressDialogFragment mProgressDialog;

        private GeoCoderAsyncTask(final InserisciPaginaDiario inserisciPaginaDiario, final int maxResult) {
            this.inserisciPaginaDiarioWeakReference = new WeakReference<InserisciPaginaDiario>(inserisciPaginaDiario);
            this.mMaxResult = maxResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final InserisciPaginaDiario inserisciPaginaDiario = inserisciPaginaDiarioWeakReference.get();
            if (inserisciPaginaDiario != null) {
                mProgressDialog = ProgressDialogFragment.get(R.string.loading_message);
                mProgressDialog.show(inserisciPaginaDiario.getSupportFragmentManager(), PROGRESS_TAG);
            }

        }

        @Override
        protected List<Address> doInBackground(Location... params) {
            final InserisciPaginaDiario inserisciPaginaDiario = inserisciPaginaDiarioWeakReference.get();
            if (inserisciPaginaDiario == null) {
                return null;
            }

            final Geocoder geocoder = new Geocoder(inserisciPaginaDiario, Locale.getDefault());
            final Location location = params[0];
            List<Address> geoAddresses = null;
            try {
                //do{
                geoAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), mMaxResult);
                //}
                // while(geoAddresses==null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return geoAddresses;
        }


        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);
            if (mProgressDialog != null) {
                mProgressDialog.dismissAllowingStateLoss();
            }

            final InserisciPaginaDiario inserisciPaginaDiario = inserisciPaginaDiarioWeakReference.get();
            if (inserisciPaginaDiario != null && addresses != null && addresses.size() > 0) {
                final Address address = addresses.get(0);

                final StringBuilder geoCoding = new StringBuilder();
                final int maxIndex = address.getMaxAddressLineIndex();
                for (int i = 0; i <= maxIndex; i++) {
                    geoCoding.append(address.getAddressLine(i));
                    if (i < maxIndex) {
                        geoCoding.append(", ");
                    }
                }
                inserisciPaginaDiario.mGeoCoderView.setText(geoCoding.toString());
            } else {
                if (inserisciPaginaDiario != null) {
                    //inserisciPaginaDiario.mGeoCoderView.setText(R.string.no_info);
                    Toast.makeText(InserisciPaginaDiario.this,getResources().getString(R.string.riprova),Toast.LENGTH_SHORT).show();
                    //inserisciPaginaDiario.mGeoCoderView.setText(Double.toString(latitudinegeo)+", "+Double.toString(longitudinegeo));
                }
            }
        }
    }
    public  OnFailureListener errorListener=new OnFailureListener() {
        @Override
        public void onFailure(Exception e) {
            Toast.makeText(InserisciPaginaDiario.this,getResources().getString(R.string.riprova),Toast.LENGTH_SHORT).show();
        }
    };



    public void attivaGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(InserisciPaginaDiario.this);
        builder.setMessage(getString(R.string.messposizionecorrente))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.attiva), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    public void vediFoto(){
        final Dialog builder = new Dialog(InserisciPaginaDiario.this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(InserisciPaginaDiario.this);

        Glide.with(InserisciPaginaDiario.this).load(stringUri).centerCrop().into(imageView);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }


}