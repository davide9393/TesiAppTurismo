package it.uniba.di.ivu.progettotesi.appturismo;


import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class ScontoAdapter extends RecyclerView.Adapter<ScontoAdapter.MyViewHolder> {

    private Context mContext;
    private List<Sconto> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titolo, descrizione;
        public ImageView immagine;
        public ImageButton imageButton;
        public CardView card;
        public Animation animFadeIn;
        private Boolean state=false;
        private int valore=0;



        public MyViewHolder(View view) {
            super(view);
            immagine=(ImageView)view.findViewById(R.id.card_image_esp);
            titolo= (TextView)view.findViewById(R.id.card_title_esp);
            descrizione = (TextView) view.findViewById(R.id.card_text_esp);
            card=(CardView) view.findViewById(R.id.card_view_esp);
            imageButton=(ImageButton) view.findViewById(R.id.expand);


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


    }


    public ScontoAdapter(Context mContext, List<Sconto> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dettaglio_attivita, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Sconto sconto = albumList.get(position);
        holder.titolo.setText(sconto.getNome_sconto());
        holder.descrizione.setText(sconto.getDescrizione());

        // loading album cover using Glide library
        Glide.with(mContext).load(sconto.getImmagine()).centerCrop().into(holder.immagine);

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
