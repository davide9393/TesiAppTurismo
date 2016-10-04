package it.uniba.di.ivu.progettotesi.appturismo;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class EsploraFragment extends Fragment {


    private FragmentListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<String> s;
    ArrayList<Citta> citta;
    CollapsingToolbarLayout collapsingToolbar;
    private List<String> mDataset;
    private Context mContext;
    // private EsploraFragmentListener mListener;

    public static EsploraFragment newInstance() {
        Bundle args = new Bundle();
        EsploraFragment fragment = new EsploraFragment();
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
        Utility.showProgressDialog(getContext());
        View v =  inflater.inflate(R.layout.fragment_esplora,container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.esplora));

        mListener.disableCollapse();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view_esp);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);



        DatabaseReference mCitta= FirebaseDatabase.getInstance().getReference().child("Citta");
        mCitta.keepSynced(true);
        mCitta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                citta=new ArrayList<>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot dato = iterator.next();
                    Citta c = dato.getValue(Citta.class);
                //    Log.d("desc ",c.foto);
                    c.setNome_citta(dato.getKey());
                    citta.add(c);

                }

                mAdapter = new CustomAdapterEsplora(getContext(), citta);
                mRecyclerView.setAdapter(mAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Utility.hideProgressDialog();
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
        void disableCollapse();
    }

}