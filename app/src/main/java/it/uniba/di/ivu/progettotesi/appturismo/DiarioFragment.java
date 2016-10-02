package it.uniba.di.ivu.progettotesi.appturismo;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DiarioFragment extends Fragment {

    private FragmentListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;
    private DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDiario;
    private static final String TAG = "Activity";
    private Button action;
    List<String> diari;

    public static DiarioFragment newInstance() {
        Bundle args = new Bundle();
        DiarioFragment fragment = new DiarioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_diario,container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.titolofragmentdiario));

        mListener.setHomeButton();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        fab=(FloatingActionButton) v.findViewById(R.id.fab);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDiario =mDatabase.child("Diario");
        diari=new ArrayList<>();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // funziona: al 1 avvio dell'activity mi restituisce la lista di tutti gli elementi
                // ad ogni aggiunta di un elemento mi restituisce solo l'elemento aggiunto
                //non ha bisogno di un iterator a differenza del value event

                //Log.d(TAG, "onChildAdded: chiave nodo precedente " + previousChildName);

                    Diario value = dataSnapshot.getValue(Diario.class);// contiene tutti gli attributi di quell'id
                    //  Log.d(TAG, "Value event|chiave " + dato.getKey() + ", foto : " + value.datacreazione);
                String sub=dataSnapshot.getKey().substring(Utility.uid.length(),dataSnapshot.getKey().length());
                    if (value.utente.equals(Utility.uid) && !diari.contains(sub)) {
                        Log.d(TAG, "Value event|chiave " + dataSnapshot.getKey() + ", attivita :.. da aggiungere ");
                        diari.add(sub);
                    }


                mAdapter = new CustomAdapter(getContext(), diari);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        /*        String sub=previousChildName.substring(Utility.uid.length(), previousChildName.length());
                diari.remove(sub);
                sub=dataSnapshot.getKey().substring(Utility.uid.length(),dataSnapshot.getKey().length());
                diari.add(sub);*/
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                String sub=dataSnapshot.getKey().substring(Utility.uid.length(),dataSnapshot.getKey().length());
                diari.remove(sub);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getActivity(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mDiario.addChildEventListener(childEventListener);

        mDiario.keepSynced(true);
      /*  DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    Snackbar.make(v, "Attenzione sei offline! \n Ritorna online per registrare i cambiamenti.",Snackbar.LENGTH_INDEFINITE).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });*/






        //      mAdapter = new CustomAdapter(getContext(),s);
    //    mRecyclerView.setAdapter(mAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.nuovodiario));
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.layout_nome_diario, (ViewGroup) getView(), false);
                // Set up the nomeDiario
                final EditText nomeDiario = (EditText) viewInflated.findViewById(R.id.input);
                // Specify the type of nomeDiario expected; this, for example, sets the nomeDiario as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    //private void DataOra(final Location currentLocation){
                        //mLastLocation = currentLocation;
                        long locationTime = System.currentTimeMillis();
                        /*if (currentLocation != null) {
                            locationTime = currentLocation.getTime();
                        } else {
                            Toast.makeText(getActivity(), R.string.my_location_not_available, Toast.LENGTH_SHORT).show();
                        }*/
                        final SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.my_location_format));
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(locationTime);
                        final String timeAsString = sdf.format(calendar.getTime());
                    //}



                        Diario d= new Diario(timeAsString,Utility.uid);
                        mDiario.child(Utility.uid+nomeDiario.getText().toString()).setValue(d);
                      //  diari.add(nomeDiario.getText().toString());
                      //  mAdapter.notifyItemInserted(diari.size() + 1);
                        Toast.makeText(getContext(), getResources().getString(R.string.diariocrato), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        return v;

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
        void setHomeButton();
    }


}