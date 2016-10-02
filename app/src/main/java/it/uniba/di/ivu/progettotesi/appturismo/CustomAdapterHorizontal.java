package it.uniba.di.ivu.progettotesi.appturismo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;


public class CustomAdapterHorizontal extends RecyclerView.Adapter
        <CustomAdapterHorizontal.ListItemViewHolder> {

    private List<Contenuto> items;
    private Context context;
    private int selectedItem = -1;

    CustomAdapterHorizontal(List<Contenuto> modelData, Context context) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        items = modelData;
        this.context=context;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_horizontal_rw, viewGroup, false);
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        //screenWidth-100, ViewGroup.LayoutParams.WRAP_CONTENT
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth-100, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,8,8,8);
        itemView.setLayoutParams(layoutParams);

        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, final int position) {
        viewHolder.card_title.setText(items.get(position).getTitolo());
        Glide.with(context).load(items.get(position).getImmagine()).centerCrop().into(viewHolder.card_image);
        //viewHolder.card_image.setImageBitmap(items.get(position).getImmagine());
        viewHolder.card_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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