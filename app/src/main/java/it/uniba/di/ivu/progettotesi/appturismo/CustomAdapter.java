package it.uniba.di.ivu.progettotesi.appturismo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    //private String[] mDataset;
    private List<String> mDataset;
    private DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
    private Context mContext;
    private String nome;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nomeDiario;
        //public ImageView overflow;
        public Button action;
        public ImageView immagine;

        /*
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Diario");
                                //in questo modo quando siamo offline mette l'operazione in cosa e la farà
                                // quando la connessione ritornerà
                               // mDatabase.keepSynced(true);
                                mDatabase.child(nome).removeValue();
        */

        public ViewHolder(View view) {
            super(view);
            immagine=(ImageView)view.findViewById(R.id.thumbnail);
            nomeDiario= (TextView)view.findViewById(R.id.nomeDiario);
          //  overflow = (ImageView) view.findViewById(R.id.overflow);
            action=(Button) view.findViewById(R.id.action_button);
            action.setOnClickListener(this);
            immagine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    DettaglioDiarioFragmentMappa dettaglio = new DettaglioDiarioFragmentMappa();

                    Bundle bundle = new Bundle();
                    bundle.putString("titolodiario", Utility.uid+mDataset.get(getAdapterPosition()));
                    dettaglio.setArguments(bundle);
                    ft.replace(R.id.fragment, dettaglio);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }

        @Override
        public void onClick(final View v) {
            //delete(getAdapterPosition());

            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(mContext, v);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.menu_diario, popup.getMenu());
            final DatabaseReference mDiario= mDatabase.child("Diario");
            nome=Utility.uid+mDataset.get(getAdapterPosition());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.modifica:
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
                            builder2.setTitle(mContext.getResources().getString(R.string.rinominadiario));
                            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View dialogView = inflater.inflate(R.layout.layout_nome_diario, null);
                            final TextView t=(TextView) dialogView.findViewById(R.id.input);
                            builder2.setView(dialogView);

                            // Set up the buttons
                            builder2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                   // mDiario.child(nome);
                                    //Diario d=new Diario();
                                    dialog.dismiss();
                                    rename(getAdapterPosition(), t.getText().toString());
                                    String newname=Utility.uid+t.getText().toString();
                                    rinomina(mDiario, nome, newname);
                                }
                            });
                            builder2.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder2.show();
                            return true;
                        case R.id.cancella:
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(mContext.getResources().getString(R.string.cancelladiario)+nome+"?");
                            //builder.setIcon(R.drawable.ic_action_action_delete);
                            // Set up the buttons
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   ;
                                    //in questo modo quando siamo offline mette l'operazione in cosa e la farà
                                    // quando la connessione ritornerà
                                    // mDatabase.keepSynced(true);
                                //    Toast.makeText(mContext,"custom",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    delete(getAdapterPosition());
                                   cancella(mDiario,nome);

                                    //dire a vito di fare la transizione dopo l'eliminazione altrimenti non si vede
                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                            return true;
                        default:
                    }
                    return false;
                }
            });

            popup.show();//showing popup menu
        }

    }

    public void delete(int position) { //removes the row
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void rename(int pos,String nome){
        mDataset.set(pos, nome);
        notifyDataSetChanged();
    }


    public CustomAdapter(Context mContext,List<String> myDataset) {
        mDataset = myDataset;
        this.mContext = mContext;
    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nomeDiario.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void rinomina(final DatabaseReference mDiario, final String nome, final String newName){
        mDiario.child(nome).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Diario value = dataSnapshot.getValue(Diario.class);
                // contiene tutti gli attributi di quell'id
                mDiario.child(nome).removeValue();
                mDiario.child(newName).setValue(value);
                Set<String> pagineset = value.pagina.keySet();
                if (!pagineset.isEmpty()) {
                    DatabaseReference mPagina = mDatabase.child("Pagina");
                  //  ArrayList<String> pagine = new ArrayList<String>();
                    Iterator<String> iterator = pagineset.iterator();
                    while (iterator.hasNext()) {
                        String page=iterator.next();
                        mPagina.child(page).child("diario").setValue(newName);

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // non ha i permessi per leggere i dati e mi ritorna il motivo dell'errore
                Toast.makeText(mContext,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void cancella(final DatabaseReference mDiario, final String nome){
        mDiario.child(nome).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Diario value = dataSnapshot.getValue(Diario.class);
                // contiene tutti gli attributi di quell'id
                mDiario.child(nome).removeValue();
                Set<String> pagineset = value.pagina.keySet();
                if (!pagineset.isEmpty()) {
                    DatabaseReference mPagina = mDatabase.child("Pagina");
                    //  ArrayList<String> pagine = new ArrayList<String>();
                    Iterator<String> iterator = pagineset.iterator();
                    while (iterator.hasNext()) {
                        String page=iterator.next();
                        mPagina.child(page).removeValue();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // non ha i permessi per leggere i dati e mi ritorna il motivo dell'errore
                Toast.makeText(mContext,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }
/* mi prende tutte le chiavi di un oggetto
    private List<String> getKeys(Integer value, stringSet){
        List<String> keys = new ArrayList<String>();
        for(String key : stringSet.keySet()){
            if(team1.get(key).equals(value)){
                keys.add(key);
            }
        }
        return keys;
    }
*/
}