package it.uniba.di.ivu.progettotesi.appturismo;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morolla on 30/09/2016.
 */
public class CustomizzaSezioneFragment extends Fragment implements OnStartDragListener{

    private String tab;
    private ItemTouchHelper helper;
    private List<PulsantiCustom> pulsantiList;
    private List<String> pulsantipassati;
    private RecyclerView pulsantiRecyclerView;
    private PulsantiCustomAdapter pulsantiAdapter;
    private String citta;
    private String foto;
    private String descrizione;
    private FragmentListener mListener;


    public CustomizzaSezioneFragment() {
        // Required empty public constructor
    }
    public interface FragmentListener {
        void disableCollapse();
        void setBackButton();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        int id;
        if (context instanceof FragmentListener) {
            mListener = (FragmentListener) context;
            //mListener.disableCollapse();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.activity_customizza_sezione, container, false);

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            tab = bundle.getString("tab", "tab");
            citta = bundle.getString("citta", "citta");
            foto = bundle.getString("foto", "foto");
            descrizione = bundle.getString("descrizione", "descrizione");
            pulsantipassati=bundle.getStringArrayList("sottosezioni");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.modificasezione) + " " + tab);
        }

        setHasOptionsMenu(true);
        mListener.disableCollapse();
        mListener.setBackButton();

        // Setup RecyclerView
        pulsantiRecyclerView = (RecyclerView) view.findViewById(R.id.pulsanti_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pulsantiRecyclerView.setLayoutManager(linearLayoutManager);

        // Setup Adapter
        pulsantiAdapter = new PulsantiCustomAdapter(getContext(), getPulsanti(),this);
        pulsantiRecyclerView.setAdapter(pulsantiAdapter);

        // Setup ItemTouchHelper
        ItemTouchHelper.Callback callback = new PulsanteTouchHelper(pulsantiAdapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(pulsantiRecyclerView);

        return view;
    }

    private List<PulsantiCustom> getPulsanti(){
        pulsantiList = new ArrayList<>();
        for(int i=0; i<pulsantipassati.size(); i++){
            pulsantiList.add(new PulsantiCustom(pulsantipassati.get(i)));
        }
        return pulsantiList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_customizza_pulsanti, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.conferma:
                //conferma();
                new AsyncTaskParseJson().execute();
                /*
                FragmentManager fm = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                EsploraDettaglioFragment esplora = new EsploraDettaglioFragment();

                Bundle bundle = new Bundle();
                Citta c=new Citta(foto,descrizione);
                c.setNome_citta(citta);
                bundle.putParcelable("citta",c);
                esplora.setArguments(bundle);
                ft.replace(R.id.fragment, esplora);
                ft.addToBackStack(null);
                ft.commit();*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        helper.startDrag(viewHolder);
    }

    public void conferma(){
        for(int i=0; i<pulsantiAdapter.getItemCount(); i++){
            PulsantiCustom p=pulsantiAdapter.getItem(i);
            // Toast.makeText(CustomizzaSezioneActivity.this, p.getNome(), Toast.LENGTH_SHORT).show();
        }
        ReadFile();
    }

    public void ReadFile(){
        try {
            File yourFile = new File(Environment.getExternalStorageDirectory(), "AppTurismo/strutturaApp_" + citta +".json");
            //il file va come parametro perchè da esso posso fare operazioni di lettura
            FileInputStream stream = new FileInputStream(yourFile);
            String json = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                json = Charset.defaultCharset().decode(bb).toString();
            }
            catch(Exception e){
                Log.e("primo catch: ","errore");
                e.printStackTrace();
            }
            finally {
                stream.close();
            }

            /*JSONObject jsonobject=new JSONObject(json);
            JSONArray jsonArray=jsonobject.getJSONArray("contenuto");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jo=jsonArray.getJSONObject(i);
                String sezione=jo.names().toString().replaceAll("[^a-zA-Z0-9]", "");
                if(sezione.equalsIgnoreCase(tab)){
                    JSONArray ja=jo.getJSONArray(sezione);
                    for(int j=0; j<ja.length(); j++){
                        JSONObject sottosezione=ja.getJSONObject(j);
                        sottosezione.put("sottosezione"+i, pulsantiAdapter.getItem(i).getNome());
                    }
                }
            }*/


            ///////////
            JSONObject jsonobject=new JSONObject(json);
            JSONArray jsonArray=jsonobject.getJSONArray("contenuto");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jo=jsonArray.getJSONObject(i);
                String sezione=jo.names().toString().replaceAll("[^a-zA-Z0-9]", "");
                //Toast.makeText(getContext(), sezione, Toast.LENGTH_SHORT).show();
                JSONArray ja=jo.getJSONArray(sezione);
                if(sezione.equalsIgnoreCase(tab)){
                    //Toast.makeText(getContext(), sezione, Toast.LENGTH_SHORT).show();
                    for(int j=0; j<ja.length(); j++){
                        JSONObject sottosezione=ja.getJSONObject(j);
                        String prova=sottosezione.names().toString().replaceAll("[^a-zA-Z0-9]", "");
                        // Toast.makeText(getContext(), prova, Toast.LENGTH_SHORT).show();
                        sottosezione.put(prova,pulsantiAdapter.getItem(j).getNome());
                    }
                }

                /**/
                //sottosezioni.add(sottosez);
            }
            ////////////
            writeInSdCard(jsonobject);
            String jjjj=jsonobject.toString();
            Log.e("json= ",jjjj);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writeInSdCard(JSONObject json){
        String jsonString = json.toString();
        byte[] jsonArray = jsonString.getBytes();

        File fileToSaveJson = new File(Environment.getExternalStorageDirectory(), "AppTurismo");
        // if external memory exists and folder with name Notes
        File filepath = new File(fileToSaveJson, "strutturaApp_" + citta +".json");  // file path to save

        //if(!filepath.exists()){
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filepath));
            bos.write(jsonArray);
            bos.flush();
            bos.close();

        } catch (FileNotFoundException e4) {
            // TODO Auto-generated catch block
            Log.e("primo catch:","errore");
            e4.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("secondo catch:","errore");
            e.printStackTrace();
        }
        finally {
            jsonArray=null;
            System.gc();
        }
        //}

        //}

    }


    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJsonTAG.java";

        private ProgressDialog progressDialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Attendere...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            conferma();
            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            /*FragmentManager fm = ((AppCompatActivity)getContext()).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            EsploraDettaglioFragment esplora = new EsploraDettaglioFragment();

            Bundle bundle = new Bundle();
            Citta c=new Citta(foto,descrizione);
            c.setNome_citta(citta);
            bundle.putParcelable("citta",c);
            esplora.setArguments(bundle);
            ft.replace(R.id.fragment, esplora);
            ft.addToBackStack(null);
            ft.commit();*/
            //boolean done = getFragmentManager().popBackStackImmediate();
            /*FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove(CustomizzaSezioneFragment.this);
            trans.commit();*/

            Toast.makeText(getContext(), getResources().getString(R.string.strutturaaggiornata), Toast.LENGTH_SHORT).show();
            this.progressDialog.dismiss();
            FragmentManager fm = ((AppCompatActivity)getContext()).getSupportFragmentManager();
            fm.popBackStack(null,(FragmentManager.POP_BACK_STACK_INCLUSIVE));

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment, EsploraFragment.newInstance());
            ft.addToBackStack(null);
            ft.commit();
            fm.executePendingTransactions();

            ft = fm.beginTransaction();
            EsploraDettaglioFragment esplora = new EsploraDettaglioFragment();

            Bundle bundle = new Bundle();
            Citta c=new Citta(foto,descrizione);
            c.setNome_citta(citta);
            bundle.putParcelable("citta", c);
            esplora.setArguments(bundle);
            ft.replace(R.id.fragment, esplora);
            ft.addToBackStack(null);
            ft.commit();

        }
    }
}