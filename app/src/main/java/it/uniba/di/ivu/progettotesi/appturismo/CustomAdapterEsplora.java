package it.uniba.di.ivu.progettotesi.appturismo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapterEsplora extends RecyclerView.Adapter<CustomAdapterEsplora.ViewHolder> {
    //private String[] mDataset;
   // private List<String> mDataset;
    private ArrayList<Citta> mDataset;
    private Context mContext;
    private String nome;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titolo;
        public ImageView immagine;

        public TextView descrizione;
        public ImageButton imageButton;
        public CardView card;
        public Animation animFadeIn;
        private Boolean state=false;
        private int valore=0;


        public ViewHolder(View view) {
            super(view);
            immagine=(ImageView)view.findViewById(R.id.card_image_esp);
            titolo= (TextView)view.findViewById(R.id.card_title_esp);
            descrizione = (TextView) view.findViewById(R.id.card_text_esp);
            card=(CardView) view.findViewById(R.id.card_view_esp);
            imageButton=(ImageButton) view.findViewById(R.id.expand);
            valore=card.getHeight();
            //rotazione icona


            //  overflow = (ImageView) view.findViewById(R.id.overflow);


            immagine.setOnClickListener(this);
            immagine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    EsploraDettaglioFragment esplora = new EsploraDettaglioFragment();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("citta",mDataset.get(getAdapterPosition()));
                    esplora.setArguments(bundle);
                    ft.replace(R.id.fragment, esplora);
                    ft.addToBackStack(null);
                    ft.commit();

                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                    //setta l'altezza della cardview al click della freccetta
                    int height = 900;
                    toggleCardViewnHeight(height);
                    descrizione.setSingleLine(false);
                }

                private void toggleCardViewnHeight(int height) {
                    int minHeight = card.getHeight();
                    if (!state) {
                        // expand
                        valore=card.getHeight();
                        expandView(height);

                        state=true;//'height' is the height of screen which we have measured already.
                    } else {
                        // collapse
                        state=false;
                        collapseView();

                    }
                }

                public void collapseView() {
                    //altezza precisa della card
                    int minHeight=valore;
                    //rotazione icona
                    imageButton.animate().rotation(0).start();
                    ValueAnimator anim = ValueAnimator.ofInt(card.getMeasuredHeightAndState(),
                            minHeight);
                    anim.addUpdateListener(
                            new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = card.getLayoutParams();
                            layoutParams.height = val;
                            card.setLayoutParams(layoutParams);
                            //descrizione.setSingleLine(true);
                        }
                    });
                    descrizione.setVisibility(View.INVISIBLE);
                    descrizione.setVisibility(View.GONE);
                    anim.start();
                }
                public void expandView(int height) {
                    //rotazione icona
                    imageButton.animate().rotation(180).start();
                    descrizione.startAnimation(animFadeIn);

                    ValueAnimator anim = ValueAnimator.ofInt(card.getMeasuredHeightAndState(),
                            height);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = card.getLayoutParams();
                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            card.setLayoutParams(layoutParams);
                            descrizione.setVisibility(View.VISIBLE);

                        }
                    });
                    anim.start();
                }

            });
        }

        @Override
        public void onClick(final View v) {
        }

    }

    public CustomAdapterEsplora(Context mContext,ArrayList<Citta> myDataset) {
        mDataset = myDataset;
        this.mContext = mContext;
    }

    @Override
    public CustomAdapterEsplora.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_esplora, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.titolo.setText(mDataset.get(position).getNome_citta());
        holder.descrizione.setText(mDataset.get(position).getDescrizione());
        holder.descrizione.setVisibility(View.GONE);
        Glide.with(mContext).load(mDataset.get(position).foto).centerCrop().into(holder.immagine);
        /*StorageReference mCitta= FirebaseStorage.getInstance().getReference().child(mDataset.get(position).foto);
        mCitta.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri).centerCrop().into(holder.immagine);
            }
        });*/
      //  Glide.with(mContext).load(s).centerCrop().into(holder.immagine);
        //set immagine
      //  Glide.with(mContext).load(mDataset.get(position).getImmagine()).centerCrop().into(holder.immagine);

        //holder.immagine.setImageURI(mDataset.get(position).getImmagine());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}