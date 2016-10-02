package it.uniba.di.ivu.progettotesi.appturismo;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
public class CustomAdapterDiario extends RecyclerView.Adapter
        <CustomAdapterDiario.ListItemViewHolder> {

    private List<Pagina> items;
    private Context mContext;
    private SparseBooleanArray selectedItems;

    CustomAdapterDiario(List<Pagina> modelData,Context context) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = modelData;
        mContext=context;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.text_row_pagina_diario, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        Pagina model = items.get(position);
        viewHolder.nome_pagina.setText(String.valueOf(model.getDescrizione()));
        viewHolder.data.setText(String.valueOf(model.getDataPagina()));
       // viewHolder.thumbnail.setImageBitmap(model.getImmagine());
      //  viewHolder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext).load(model.getImmagine()).centerCrop().into(viewHolder.thumbnail);
        viewHolder.ora.setText(String.valueOf(model.getOra()));
        viewHolder.card.setActivated(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeSelection() {
        selectedItems = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !selectedItems.get(position));
    }
    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            selectedItems.put(position, value);
        else
            selectedItems.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return selectedItems.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return selectedItems;
    }
    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView nome_pagina;
        TextView data;
        TextView ora;
        ImageView thumbnail;
        CardView card;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            nome_pagina = (TextView) itemView.findViewById(R.id.nome_pagina);
            data = (TextView) itemView.findViewById(R.id.data);
            ora= (TextView) itemView.findViewById(R.id.ora);
            thumbnail=(ImageView) itemView.findViewById(R.id.thumbnail);
            card=(CardView) itemView.findViewById(R.id.card_view);
        }
    }
}