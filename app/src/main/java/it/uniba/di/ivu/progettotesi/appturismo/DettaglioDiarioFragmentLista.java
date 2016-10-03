package it.uniba.di.ivu.progettotesi.appturismo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class DettaglioDiarioFragmentLista extends Fragment {

    private FragmentListener mListener;
    String titoloDiario;
    CustomAdapterDiario mAdapter;
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Pagina> pagineDiario;
    private ActionMode mActionMode;
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    private String TAG="Diariofragment2";
    static boolean var;

    private FloatingActionButton fab;

    public static DettaglioDiarioFragmentLista newInstance() {
        Bundle args = new Bundle();
        DettaglioDiarioFragmentLista fragment = new DettaglioDiarioFragmentLista();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_dettaglio_diario_lista, container, false);

        fab = (FloatingActionButton) v.findViewById(R.id.fab2);

        var=false;
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            titoloDiario = bundle.getString("titolodiario", "diario");
            String sub=titoloDiario.substring(Utility.uid.length(),titoloDiario.length());
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(sub);
        }

        setHasOptionsMenu(true);
        //mListener.setBackButton();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        getInfoDB();

        implementRecyclerViewClickListeners();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuovaPagina = new Intent(getContext(), InserisciPaginaDiario.class);
                nuovaPagina.putExtra("titolodiario", titoloDiario);

                startActivity(nuovaPagina);

            }
        });

        return v;

    }



    private void implementRecyclerViewClickListeners() {
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null && var == true)
                    onListItemSelect(position);
                else {
                    Intent intent = new Intent(getActivity(), DettaglioFotoDiarioActivity.class);
                    Bundle extras = new Bundle();
                    Toast.makeText(getContext(), "else", Toast.LENGTH_SHORT).show();
                    Pagina pagina = pagineDiario.get(position);
                    extras.putParcelable("pagina", pagina);
                    extras.putString("titoloDiario", titoloDiario);
                    intent.putExtras(extras);
                    startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    private void getInfoDB(){

     /*   DatabaseReference mPagina=mDatabase.child("Pagina");
        mPagina.keepSynced(true);
        mPagina.addValueEventListener(new ValueEventListener() {
            int i = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pagineDiario = new ArrayList<Pagina>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot dato = iterator.next();
                    Pagina value = dato.getValue(Pagina.class);
                    if (value != null && value.diario.equals(titoloDiario)) {
                        pagineDiario.add(value);
                    }

                }

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new CustomAdapterDiario(pagineDiario, getContext());
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });*/


        DatabaseReference mPagina=mDatabase.child("Pagina");
        pagineDiario=new ArrayList<Pagina>();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Log.d("chiamata ", "onchildadded");
                // funziona: al 1 avvio dell'activity mi restituisce la lista di tutti gli elementi
                // ad ogni aggiunta di un elemento mi restituisce solo l'elemento aggiunto
                //non ha bisogno di un iterator a differenza del value event

                //Log.d(TAG, "onChildAdded: chiave nodo precedente " + previousChildName);

                Pagina value = dataSnapshot.getValue(Pagina.class);
                if (value != null && value.diario.equals(titoloDiario)) {
                    pagineDiario.add(value);
                }


                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new CustomAdapterDiario(pagineDiario, getContext());
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                //non sono sicuro su questa
                Log.d("chiamata ", "onchildchanged");
                for(int i=0;i<pagineDiario.size();i++){
                    if(pagineDiario.get(i).percorso.contains(dataSnapshot.getKey())){
                        Pagina p=dataSnapshot.getValue(Pagina.class);
                        pagineDiario.remove(i);
                        pagineDiario.add(i,p);
                        break;
                    }
                }


           /*     mAdapter = new CustomAdapterDiario(pagineDiario, getContext());
                mRecyclerView.setAdapter(mAdapter);
*/
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("chiamata ", "onchildremove");
                Log.d("remove ", dataSnapshot.getKey());
                for(int i=0;i<pagineDiario.size();i++){
                    if(pagineDiario.get(i).percorso.contains(dataSnapshot.getKey())) {
                        Log.d(" va if ", dataSnapshot.getKey());
                        pagineDiario.remove(i);
                    }
                }
               /* mAdapter = new CustomAdapterDiario(pagineDiario, getContext());
                mRecyclerView.setAdapter(mAdapter);*/

                mAdapter.notifyDataSetChanged();
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
        mPagina.addChildEventListener(childEventListener);

        mPagina.keepSynced(true);

    }




    //List item select method
    private void onListItemSelect(int position) {
        mAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;//Check if any items are already selected or not


        if (hasCheckedItems && var==false){
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new Toolbar_Actionmode_Callback(getActivity(),mAdapter,pagineDiario));
            var=true;
        }

        // mActionMode.finish();
        else if (!hasCheckedItems){
            // there no selected items, finish the actionMode
            mActionMode.finish();
            var=false;
        }


        if (mActionMode != null && var==true){
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(mAdapter
                    .getSelectedCount()) + " selezionati");
        }



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_diario_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mode_map){

            FragmentTransaction ft=getFragmentManager().beginTransaction();
            DettaglioDiarioFragmentMappa pag=new DettaglioDiarioFragmentMappa();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            mListener = (FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseCategoriesFragmentListener");
        }
    }

    public interface FragmentListener {
        void setBackButton();
    }

}
