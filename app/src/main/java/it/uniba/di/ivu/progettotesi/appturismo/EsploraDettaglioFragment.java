package it.uniba.di.ivu.progettotesi.appturismo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import it.uniba.di.ivu.progettotesi.appturismo.MainActivity;
import it.uniba.di.ivu.progettotesi.appturismo.R;


public class EsploraDettaglioFragment extends Fragment {

    private FragmentListener mListener;
    private ViewPager viewPager;
    private ArrayList<String> sezioni;
    private ArrayList<ArrayList<String>> sottosezioni;
    private ImageView immagine;
    private TabLayout tabLayout;
    static String citta;
    private ImageView imageView;
    private String foto,descrizione;
    private Adapter adapter;
    private Citta ci;
    View view;

    public static EsploraDettaglioFragment newInstance() {
        Bundle args = new Bundle();
        EsploraDettaglioFragment fragment = new EsploraDettaglioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean isBackFromB;
    @Override
    public void onPause() {
        super.onPause();
        isBackFromB = true;
        //adapter=null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(citta);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inbox, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        mListener.enableCollapse();
       // immagine=(ImageView)getActivity().findViewById(R.id.backdrop);
       // immagine.setVisibility(View.VISIBLE);
       // tabLayout=(TabLayout)getActivity().findViewById(R.id.tabs);
       // tabLayout.setVisibility(View.VISIBLE);
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            /*citta = bundle.getString("nome_citta", "nome_citta");
            foto = bundle.getString("foto", "foto");
            descrizione = bundle.getString("descrizione", "descrizione");
            */
            ci=bundle.getParcelable("citta");
            citta = ci.nome_citta;
            foto = ci.foto;
            descrizione = ci.descrizione;
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(citta);
        }

        adapter = new Adapter(getChildFragmentManager());
       // if (viewPager != null) {
            setupViewPager(viewPager);
            //qui stava ((MainActivity) getActivity()).getTabLayout().setupWithViewPager(viewPager);
            adapter.notifyDataSetChanged();
       // }


        Log.e("EsploraDettaglio","onCreateView");
        return view;
    }

    @Override
    public void  onDestroyView(){
        super.onDestroyView();
        mListener.disableCollapse();
        mListener.setHomeButton();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        int id;
        if (context instanceof FragmentListener) {
            mListener = (FragmentListener) context;
            //mListener.enableCollapse();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void writeInSdCard(JSONObject json){
        String jsonString = json.toString();
        byte[] jsonArray = jsonString.getBytes();

        File fileToSaveJson = new File(Environment.getExternalStorageDirectory(), "AppTurismo");
        // if external memory exists and folder with name Notes
        if (!fileToSaveJson.exists()) {
            fileToSaveJson.mkdirs(); // this will create folder
        }
        File filepath = new File(fileToSaveJson, "strutturaApp_" + citta +".json");  // file path to save
        if(!filepath.exists()){
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
        }

        //}

    }

    public void ReadFile (ArrayList<String> sezioni, ArrayList<ArrayList<String>> sottosezioni){
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
                Log.e("finally: ","bene");
                stream.close();
            }

            JSONObject jsonobject=new JSONObject(json);
            JSONArray jsonArray=jsonobject.getJSONArray("contenuto");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jo=jsonArray.getJSONObject(i);
                String sezione=jo.names().toString().replaceAll("[^a-zA-Z0-9]", "");
                sezioni.add(sezione);
                JSONArray ja=jo.getJSONArray(sezione);
                ArrayList<String> prova=null;
                ArrayList<String> sottosez=new ArrayList<>();
                for(int j=0; j<ja.length(); j++){
                    JSONObject sottosezione=ja.getJSONObject(j);
                    String b=sottosezione.names().toString().replaceAll("[^a-zA-Z0-9]", "");
                    //String n=sottosezione.getString(b);
                    sottosez.add(b);
                }
                sottosezioni.add(sottosez);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        //SE VOGLIO POPOLARE IL JSON DALLA CARTELLA ASSETS
        //getJSONdata();

        //SE VOGLIO POPOLARLO DAL SERVER
        new AsyncTaskParseJson().execute();
    }

    /*
    private void getJSONdata() {
        //ArrayList<String> sezioni=new ArrayList<>();
        //ArrayList<ArrayList<String>> sottosezioni=new ArrayList<>();
        sezioni=new ArrayList<>();
        sottosezioni=new ArrayList<>();
        String json;
        json=loadJSONFromAsset();
        //json=getJSONFromUrl(url);
        try{
            JSONObject jsonobject=new JSONObject(json);
            JSONArray jsonArray=jsonobject.getJSONArray("contenuto");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jo=jsonArray.getJSONObject(i);
                String sezione=jo.names().toString().replaceAll("[^a-zA-Z0-9]", "");
                sezioni.add(sezione);
                JSONArray ja=jo.getJSONArray(sezione);
                ArrayList<String> prova=null;
                ArrayList<String> sottosez=new ArrayList<>();
                for(int j=0; j<ja.length(); j++){
                    JSONObject sottosezione=ja.getJSONObject(j);
                    String b=sottosezione.names().toString().replaceAll("[^a-zA-Z0-9]", "");
                    String n=sottosezione.getString(b);
                    sottosez.add(n);
                }
                sottosezioni.add(sottosez);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        impostaTab(sezioni, sottosezioni);

    }
*/

    public void impostaTab(ArrayList<String> sezioni,ArrayList<ArrayList<String>> sottosezioni){
        for(int i=0; i<sezioni.size();i++){
            //aggiungo un fragment come tab creando una nuova istanza(tab,sottotab) e passando il nome del tab sezioni.get(i)
            adapter.addFragment(EsploraContenutoFragment.newInstance(sezioni.get(i),sottosezioni.get(i),citta,foto,descrizione), sezioni.get(i));
        }
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //Toast.makeText(getContext(), "impostatab", Toast.LENGTH_SHORT).show();
    }

    /*
    public String loadJSONFromAsset() {
        String json = null;
        try {
            String file;
            if("it".equalsIgnoreCase(getString(R.string.language))) file= "contenuto.json";
            else file="contenuto_eng.json";
            InputStream is = getActivity().getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
*/

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();


        public Adapter(FragmentManager fm) {
            super(fm);
            notifyDataSetChanged();
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
            notifyDataSetChanged();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    public interface FragmentListener {
        void disableCollapse();
        void enableCollapse();
        void setImmagine(String url);
        void setBackButton();
        void setHomeButton();
    }




    /////////////////////////////////////
    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJsonTAG.java";

        private ProgressDialog progressDialog = new ProgressDialog(getContext());
        // set your json string url here
        String yourJsonStringUrl = "http://conselettronica.altervista.org/contenuto.json";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private ArrayList<String> sezioni;
        private ArrayList<ArrayList<String>> sottosezioni;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Attendere...");
            progressDialog.show();
            //citta = getArguments().getString("nome_citta");
            //String descrizione = getArguments().getString("descrizione");
            String url = getArguments().getString("url");
            //((MainActivity) getActivity()).getSupportActionBar().setTitle(citta);
            mListener.enableCollapse();
            //mListener.setImmagine(url);
            mListener.setBackButton();
            sezioni=new ArrayList<>();
            sottosezioni=new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... arg0) {
            JsonParser jParser = new JsonParser();
            // get json string from url

            //Prova con dati da altervista
            /*JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);
            writeInSdCard(json);
            ReadFile(sezioni,sottosezioni);*/

            //QUI DEVO FARE LA QUERY AL DB

            Gson gson = new Gson();
            String obj = gson.toJson(ci.struttura);
            Log.e("obj ",obj);
            String prova=obj.replace(":{",":[{");
            prova=prova.replace(",", "},{");
            prova=prova.replace("}}", "}]}");
            prova=prova.replace("}}", "}]}");
            Log.e("json ",prova);
            try {
                JSONObject json=new JSONObject(prova);
                writeInSdCard(json);
                ReadFile(sezioni,sottosezioni);
                Log.d("json ", json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            impostaTab(sezioni, sottosezioni);
            ((MainActivity) getActivity()).getTabLayout().setupWithViewPager(viewPager);
            this.progressDialog.dismiss();
            Log.e("popostexecute","fatto");
        }
    }
    /////////////////////////////////////////
}
