package it.uniba.di.ivu.progettotesi.appturismo;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.uniba.di.ivu.progettotesi.appturismo.Percorso.PercorsoActivity;

public class EsploraContenutoFragment extends Fragment {

    private FragmentListener2 mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnTag;

    private static final String ARG_PARAM2 = "param2";
    private static final ArrayList<String> ARG_PARAM3=new ArrayList<>();

    // TODO: Rename and change types of parameters
    private ArrayList<String> mParam3; //contiene i pulsanti del tab
    private String mParam2;  //Titolo del tab
    private String citta;
    static String nome_pulsante;
    private ArrayList<Button> listaBottoni;
    private HorizontalScrollView layout;
    private LinearLayout row;
    private String foto;
    private String descrizione;
    private CustomAdapterContenuto customAdapterContenuto;
    private RecyclerView mHorizontalRecycler;
    private AdapterHorizontal ad;

    public EsploraContenutoFragment() {
        // Required empty public constructor
    }

    public static EsploraContenutoFragment newInstance(String param2,ArrayList<String> param3, String citta,String foto,String descrizione) {
        EsploraContenutoFragment fragment = new EsploraContenutoFragment();
        Bundle args = new Bundle();
        args.putString("sometitle2", param2);
        args.putStringArrayList("sometitle3", param3);
        args.putString("citta", citta);
        args.putString("foto", foto);
        args.putString("descrizione", descrizione);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isBackFromB;
    @Override
    public void onResume(){
        super.onResume();
        //customAdapterContenuto.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        super.onPause();
        isBackFromB = true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isBackFromB=false;
        mParam2 = getArguments().getString("sometitle2");
        mParam3=getArguments().getStringArrayList("sometitle3");
        citta=getArguments().getString("citta");
        foto=getArguments().getString("foto");
        descrizione=getArguments().getString("descrizione");

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_esplora_contenuto, container, false);

        setHasOptionsMenu(true);
        listaBottoni=new ArrayList<>();
        mListener.enableCollapse();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        layout = (HorizontalScrollView) view. findViewById(R.id.horiz);
        //mHorizontalRecycler = (RecyclerView) view.findViewById(R.id.horizontalrecyclerview);
        row= new LinearLayout(getContext());
        mRecyclerView.setHasFixedSize(true);

        nome_pulsante=mParam3.get(0); //setto la prima recyclerview con il primo pulsante premuto

        setupRecyclerView(mRecyclerView, nome_pulsante);

       /* ad=new AdapterHorizontal(mParam3);
        mHorizontalRecycler.setLayoutManager(layoutManager);
        mHorizontalRecycler.setAdapter(ad);*/


        for (int j = 0; j < mParam3.size(); j++) {
            btnTag = new Button(getContext());

            btnTag.setBackgroundResource(R.drawable.button_red_round_corners);
            btnTag.setTextColor(Color.WHITE);
            //btnTag.setPadding(16,16,16,16);
            btnTag.setPadding(36,8,36,8);
            btnTag.setText(mParam3.get(j));

            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
           // btnTag.setGravity(Gravity.CENTER);
            setMargins(btnTag, 16, 32, 16, 32);

            btnTag.setId(j + 1);

            if(j==0) btnTag.setBackgroundResource(R.drawable.button_amber_round_corners);
            listaBottoni.add(btnTag);
            row.addView(btnTag);

            listaBottoni.add(btnTag);
        }


        layout.addView(row);

        for(int i=0; i<listaBottoni.size(); i++){
            final int finalI = i;
            listaBottoni.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listaBottoni.get(finalI).setBackgroundResource(R.drawable.button_amber_round_corners);
                    for(int j=0; j<listaBottoni.size(); j++){
                        if(listaBottoni.get(j)!=listaBottoni.get(finalI)) listaBottoni.get(j).setBackgroundResource(R.drawable.button_red_round_corners);
                    }
                    setupRecyclerView(mRecyclerView, listaBottoni.get(finalI).getText().toString());
                }
            });

        }


        return view;
    }

    //metodo per settare i margin dei pulsanti
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mappa_customizza, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.mode_map:
                i=new Intent(getContext(),MappaContenutoActivity.class);
                i.putExtra("tab", mParam2);
                startActivity(i);
                return true;
            case R.id.mode_customize:
               /* i=new Intent(getContext(),CustomizzaSezioneActivity.class);
                i.putExtra("tab", mParam2);
                i.putStringArrayListExtra("sottosezioni", mParam3);
                i.putExtra("citta", citta);
                startActivity(i);*/
                FragmentManager fm = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                CustomizzaSezioneFragment esplora = new CustomizzaSezioneFragment();

                Bundle bundle = new Bundle();
                bundle.putString("tab", mParam2);
                bundle.putStringArrayList("sottosezioni", mParam3);
                bundle.putString("citta", citta);
                bundle.putString("foto", foto);
                bundle.putString("descrizione", descrizione);
                esplora.setArguments(bundle);
                ft.replace(R.id.fragment, esplora);
                ft.addToBackStack(null);
                ft.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener2) {
            mListener = (FragmentListener2) context;
            //mListener.enableCollapse();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentInteractionListener");
        }
    }


    @Override
    public void onDetach(){
        super.onDetach();
        //mListener.chiudi();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        setHasOptionsMenu(false);
    }

    private void setupRecyclerView(final RecyclerView recyclerView, String nome_bottone) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final ArrayList<Contenuto> cont=new ArrayList<>();
        String url;
        if(nome_bottone.equalsIgnoreCase("hotel")) url="download/barletta.jpg";
        else url="download/barletta.jpg";
        for(int i=0; i<10; i++){
            cont.add(new Contenuto(nome_bottone,url,nome_bottone+" "+i,"3201122333",41.3159368,16.2634822));
        }
        customAdapterContenuto=new CustomAdapterContenuto(cont);
        recyclerView.setAdapter(customAdapterContenuto);
    }

    public interface FragmentListener2 {
        void enableCollapse();
    }

    public class CustomAdapterContenuto extends RecyclerView.Adapter
            <CustomAdapterContenuto.ListItemViewHolder> {

        private List<Contenuto> items;
        private SparseBooleanArray selectedItems;

        CustomAdapterContenuto(List<Contenuto> modelData) {
            if (modelData == null) {
                throw new IllegalArgumentException("modelData must not be null");
            }
            items = modelData;
            selectedItems = new SparseBooleanArray();
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_contenuto, viewGroup, false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ListItemViewHolder viewHolder, final int position) {
            viewHolder.card_title.setText(items.get(position).getTitolo());
            Glide.with(getContext()).load(items.get(position).getImmagine()).centerCrop().into(viewHolder.card_image);
            //viewHolder.card_image.setImageBitmap(items.get(position).getImmagine());
            viewHolder.card_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.card_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String t=items.get(position).getTitolo();

                    //Toast.makeText(getContext(),viewHolder.card_title.getText(),Toast.LENGTH_SHORT).show();
                    //PER ANDARE AL PERCORSO
                    /*
                    Intent i=new Intent(getContext(), PercorsoActivity.class);
                    i.putExtra("dato", "barletta, via lattanzio");
                    startActivity(i);
                    */
                    Intent i=new Intent(getContext(), DettaglioActivity.class);
                    // i.putExtra("attivita",t);
                    // i.putExtra("url",items.get(position).getImmagine());
                    Bundle extras = new Bundle();
                    extras.putParcelable("contenuto", items.get(position));
                    boolean b=false;
                    if(mParam2.equalsIgnoreCase("interessi") || mParam2.equalsIgnoreCase("interests")) b=true;
                    i.putExtras(extras);
                    i.putExtra("valore",b);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        //Put or delete selected position into SparseBooleanArray
        public void selectView(int position, boolean value) {
            if (value)
                selectedItems.put(position, value);
            else
                selectedItems.delete(position);

            notifyDataSetChanged();
        }

        public final class ListItemViewHolder extends RecyclerView.ViewHolder {
            TextView card_title;
            ImageView card_image;

            public ListItemViewHolder(View itemView) {
                super(itemView);
                card_title = (TextView) itemView.findViewById(R.id.card_title);
                card_image=(ImageView) itemView.findViewById(R.id.card_image);
            }
        }
    }



    ////////////////
    public class AdapterHorizontal extends RecyclerView.Adapter
            <AdapterHorizontal.ListItemViewHolder> {

        private List<String> items;

        AdapterHorizontal(List<String> modelData) {
            if (modelData == null) {
                throw new IllegalArgumentException("modelData must not be null");
            }
            items = modelData;
            notifyDataSetChanged();
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_pulsante, viewGroup, false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ListItemViewHolder viewHolder, final int position) {
            viewHolder.card_title.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public final class ListItemViewHolder extends RecyclerView.ViewHolder {
            TextView card_title;

            public ListItemViewHolder(View itemView) {
                super(itemView);
                card_title = (TextView) itemView.findViewById(R.id.text1);

            }
        }
    }
    /////////////////
}