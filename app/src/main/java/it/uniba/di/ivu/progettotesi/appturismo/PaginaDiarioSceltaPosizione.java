package it.uniba.di.ivu.progettotesi.appturismo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class PaginaDiarioSceltaPosizione extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener{

    private SupportMapFragment mapFragment;
    private String position="";
    private GoogleMap mMap;
    LatLng latlng;
    Button btnInviaPos;
    double latitude;
    double longitude;

    public static Toolbar toolbar;
    SupportPlaceAutocompleteFragment autocompleteFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_diario_scelta_posizione);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnInviaPos = (Button) findViewById(R.id.btnInviaPos);
        btnInviaPos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(latlng==null){
                    Toast.makeText(PaginaDiarioSceltaPosizione.this, getResources().getString(R.string.scegliunaposizione) ,Toast.LENGTH_LONG).show();
                }else {
                    latitude = latlng.latitude;
                    longitude = latlng.longitude;
                    Intent intent=new Intent();
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("position",position);
                    setResult(4, intent);
                    finish();
                }
            }
        });

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                this.getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latlng = place.getLatLng();
                position = (place.getAddress()).toString();
                markerposizione(latlng);
            }

            @Override
            public void onError(Status status) {
                Log.i("posto", "An error occurred: " + status);
                Toast.makeText(PaginaDiarioSceltaPosizione.this, getResources().getString(R.string.errore), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private Marker marker;
    private void markerposizione(LatLng latLng) {
        String title = "Qui";

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(title);

        if ( mMap!=null ) {
            if (marker != null)
                marker.remove();

            marker = mMap.addMarker(markerOptions);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            latlng=latLng;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(PaginaDiarioSceltaPosizione.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PaginaDiarioSceltaPosizione.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(41.87194, 12.567379999999957), 5));

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        latlng=null;
        position=null;
        markerposizione(latLng);
        autocompleteFragment.setText("");
        latlng=latLng;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                latitude = 0.0;
                longitude = 0.0;
                Intent intent=new Intent();
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude", longitude);
                setResult(4,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
