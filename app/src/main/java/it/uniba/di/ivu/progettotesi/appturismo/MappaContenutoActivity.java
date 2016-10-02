package it.uniba.di.ivu.progettotesi.appturismo;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.util.ArrayList;


public class MappaContenutoActivity  extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static Toolbar toolbar;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double lat;
    private double lon;
    LatLng latLng;
    SupportMapFragment mapFragment;
    Marker currLocationMarker;
    LocationRequest mLocationRequest;
    String latelong;
    RecyclerView rv;

    private int mScreenWidth = 0;
    private int mHeaderItemWidth = 0;
    private int mCellWidth = 0;
    private LinearLayoutManager mLayoutManager;
    private CustomAdapterHorizontal adapter;
    //private ArrayList<Luogo> cont;
    private ArrayList<Contenuto> luoghi;
    private String tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa_contenuto);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //initSnapshots();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            tab=extras.getString("tab");
            getSupportActionBar().setTitle(getResources().getString(R.string.mappa) + " " + tab);
        }

        rv=(FlingRecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(false);
        mLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(mLayoutManager);

       /*
       cont=new ArrayList<>();
        LatLng l=new LatLng(41.3143563,16.271048999999948);
        cont.add(new Luogo("Luogo 1","download/barletta.jpg",l));
        LatLng l2=new LatLng(41.31284530000001,16.2710558);
        cont.add(new Luogo("Luogo 2","download/barletta.jpg",l2));
        LatLng l3=new LatLng(41.3159368,16.2634822);
        cont.add(new Luogo("Luogo 3","download/barletta.jpg",l3));
        */
        luoghi=new ArrayList<>();
        luoghi.add(new Contenuto("Luogo 1", "download/barletta.jpg","descrizione luogo 1","3200011222", 41.3143563,16.271048999999948));
        luoghi.add(new Contenuto("Luogo 2", "download/barletta.jpg","descrizione luogo 2","3200011222", 41.31284530000001,16.2710558));
        luoghi.add(new Contenuto("Luogo 3", "download/barletta.jpg","descrizione luogo 3","3200011222", 41.3159368,16.2634822));

        adapter=new CustomAdapterHorizontal(luoghi,getApplicationContext());
        rv.setAdapter(adapter);


        rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rv, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                /*CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(cont.get(position).getLatLng()).zoom(18).build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));*/
                Intent i=new Intent(MappaContenutoActivity.this, DettaglioActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("contenuto", luoghi.get(position));
                boolean b=false;
                if(tab.equalsIgnoreCase("interessi") || tab.equalsIgnoreCase("interests")) b=true;
                i.putExtras(extras);
                i.putExtra("valore",b);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == recyclerView.SCROLL_STATE_IDLE) {
                    //LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
                    int firstvis = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int lastvis = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    Log.e("last:",Integer.toString(lastvis));
                    Log.e("first:",Integer.toString(firstvis));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(luoghi.get(firstvis).getLatLng()).zoom(18).build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
            }
        });

        mapFragment.getMapAsync(MappaContenutoActivity.this);

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        //PER SETTARE LO ZOOM SU TUTTI I MARKER
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int j=0; j<luoghi.size(); j++) {
            builder.include(luoghi.get(j).getLatLng());
        }
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        // mMap.moveCamera(cu);
        mMap.animateCamera(cu);

        //Setto lo zoom al primo elemento della lista
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(luoghi.get(0).getLatLng()).zoom(18).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //place marker at current position
        //mGoogleMap.clear();

        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        ////////////////
        latelong=location.getLatitude()+","+location.getLongitude();


        //If you only need one location, unregister the listener
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (ContextCompat.checkSelfPermission(MappaContenutoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MappaContenutoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else{
            mMap.setMyLocationEnabled(true);
            buildGoogleApiClient();

            mGoogleApiClient.connect();

            for(int i=0; i<luoghi.size(); i++){
                mMap.addMarker(new MarkerOptions().position(luoghi.get(i).getLatLng())).setTitle(luoghi.get(i).getTitolo());
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
}
