package it.uniba.di.ivu.progettotesi.appturismo;


import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class PulsantiCustomAdapter extends RecyclerView.Adapter<PulsantiCustomAdapter.ViewHolder> {
    private Context mContext;
    private List<PulsantiCustom> mPulsanti;
    private final OnStartDragListener mDragStartListener;

    public PulsantiCustomAdapter(Context context, List<PulsantiCustom> movies,OnStartDragListener dragStartListener){
        mDragStartListener = dragStartListener;
        this.mContext = context;
        this.mPulsanti = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_pulsante, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindPulsante(mPulsanti.get(position));
        holder.handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPulsanti.size();
    }

    public void remove(int position) {
        mPulsanti.remove(position);
        notifyItemRemoved(position);
    }

    public PulsantiCustom getItem(int position){
        return mPulsanti.get(position);
    }

    public void swap(int firstPosition, int secondPosition){
        Collections.swap(mPulsanti, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView movieNameTextView;
        public final ImageView handle;

        public ViewHolder(View view){
            super(view);
            movieNameTextView = (TextView) view.findViewById(R.id.nome_pulsante);
            handle=(ImageView) view.findViewById(R.id.handle);
        }

        public void bindPulsante(PulsantiCustom pulsante){
            this.movieNameTextView.setText(pulsante.getNome());
        }
    }
}
