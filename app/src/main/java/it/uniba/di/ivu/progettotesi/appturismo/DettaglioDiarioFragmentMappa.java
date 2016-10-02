package it.uniba.di.ivu.progettotesi.appturismo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Iterator;


public class DettaglioDiarioFragmentMappa extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private FragmentListener mListener;
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    private String TAG="Diariofragment";
    private FloatingActionButton fab;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private DatabaseReference mPagina;
    private  ValueEventListener listerin;
    private  ChildEventListener listerin2;
    private  LatLngBounds.Builder builder;
    View customMarkerView;
    private ImageView mMarkerImageView;
    private  ArrayList<PaginaDiario> pagineDiario;
    private  ArrayList<String> id;
    private ClusterManager<AppClusterItem> mClusterManager=null;

    ActionBarDrawerToggle mDrawerToggle;
    String titoloDiario;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_dettaglio_diario_mappa, container, false);


        buildGoogleApiClient();

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            titoloDiario = bundle.getString("titolodiario", "diario");
            String sub=titoloDiario.substring(Utility.uid.length(),titoloDiario.length());
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(sub);
        }

        setHasOptionsMenu(true);
        mListener.setBackButton();

        mDrawerToggle=((MainActivity) getActivity()).mDrawerToggle;
        customMarkerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuovaPagina = new Intent(getContext(), InserisciPaginaDiario.class);
                nuovaPagina.putExtra("titolodiario", titoloDiario);
              //  id=null;
             //   pagineDiario=null;
                startActivity(nuovaPagina);


            }
        });

        return v;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_diario_mappa, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mode_list){
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            DettaglioDiarioFragmentLista pag=new DettaglioDiarioFragmentLista();
            Bundle bundle=new Bundle();
            bundle.putString("titolodiario",titoloDiario);
            pag.setArguments(bundle);
            ft.replace(R.id.fragment, pag);
            ft.addToBackStack(null);
            getActivity().getSupportFragmentManager().popBackStack();
            ft.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }
    @Override
    public void onDestroyView() {
        try{
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(mapFragment);
            ft.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

      mPagina.removeEventListener(listerin);
     //   mPagina.removeEventListener(listerin2);
        super.onDestroyView();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(),"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getContext(),"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        else{
            mMap.setMyLocationEnabled(true);
            getInfoDB();
          //  setUpClustering();

        }
    }


    private void getInfoDB(){

//        Log.d("dimensione iniziale",Integer.toString(pagineDiario.size()));
        mPagina=mDatabase.child("Pagina");
        mPagina.keepSynced(true);
        listerin=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                builder = new LatLngBounds.Builder();
                pagineDiario=new ArrayList<PaginaDiario>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                mClusterManager=null;
                while (iterator.hasNext()) {
                    DataSnapshot dato = iterator.next();
                    Pagina value = dato.getValue(Pagina.class);
                    if (value!=null && value.diario.equals(titoloDiario)) {
                        Double lat = Double.parseDouble(value.latitudine);
                        Double lon = Double.parseDouble(value.longitudine);
                        String percorso = value.percorso;
                        String descr = value.descrizione;
                        String indirizzo = value.indirizzo;
                        String data = value.data;
                        String ora = value.ora;
                        PaginaDiario p=new PaginaDiario(lat, lon, percorso, descr, indirizzo, data, ora,titoloDiario, mMarkerImageView, customMarkerView);
                        pagineDiario.add(p);
                        builder.include(new LatLng(lat, lon));

                        setUpClustering(p);
                    }

                }

                if (!pagineDiario.isEmpty()) {
                    LatLngBounds bounds = builder.build();
                    int padding = 100; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        };
        mPagina.addValueEventListener(listerin);

    }

    private void setUpClustering(PaginaDiario p) {
        if(mClusterManager==null){
            mClusterManager = new ClusterManager<AppClusterItem>(this.getActivity(), mMap);//this.getactivity
            mClusterManager.setRenderer(new OwnIconRenderer(getContext().getApplicationContext(), mMap, mClusterManager));
        }


        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addClusterMarkers(mClusterManager,p);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<AppClusterItem>() {
            @Override
            public boolean onClusterItemClick(AppClusterItem item) {
                Intent intent = new Intent(getActivity(), DettaglioFotoDiarioActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("pagina", item.getPaginaDiario());
                extras.putString("titoloDiario", titoloDiario);
                intent.putExtras(extras);
                startActivity(intent);
                return true;
            }
        });
    }
    private void addClusterMarkers(ClusterManager<AppClusterItem> mClusterManager,PaginaDiario p) {

        // Set some lat/lng coordinates to start with.

        Log.d("dimensione al cluster",Integer.toString(pagineDiario.size()));
      //  for(int j=0; j<pagineDiario.size(); j++){
            //AppClusterItem offsetItem = new AppClusterItem(latitude, longitude, BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(customMarkerView, R.drawable.foto)));
            AppClusterItem offsetItem=new AppClusterItem(p);
            mClusterManager.addItem(offsetItem);
     //   }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //   mMap.setMyLocationEnabled(true);
                buildGoogleApiClient();

                mGoogleApiClient.connect();
            } else {
                // User refused to grant permission.
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            mListener = (FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    public interface FragmentListener {
        void setBackButton();
    }

}