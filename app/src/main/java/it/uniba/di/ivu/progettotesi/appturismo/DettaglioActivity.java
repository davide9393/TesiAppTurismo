package it.uniba.di.ivu.progettotesi.appturismo;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.ivu.progettotesi.appturismo.Percorso.PercorsoActivity;

public class DettaglioActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View fabMappa, fabChiama;
    private FloatingActionButton fabStar;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;


    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<Sconto> scontoList;
    private CardView cardview;
    private ImageView image;
    private boolean b;
    private TextView textView;
    private boolean preferito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio);


        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        appbar.addOnOffsetChangedListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        image=(ImageView) findViewById(R.id.backdrop);
        textView=(TextView) findViewById(R.id.textview);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //String tab=extras.getString("attivita");
            Contenuto c=extras.getParcelable("contenuto");
            b=extras.getBoolean("valore");
            getSupportActionBar().setTitle(c.getTitolo());
            Glide.with(DettaglioActivity.this).load(c.getImmagine()).centerCrop().into(image);
            textView.setText(c.getDescrizione());
            //scontoList=c.getArraySconti();
        }


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        fabMappa = findViewById(R.id.fab1);
        fabChiama = findViewById(R.id.fab2);
        fabStar = (FloatingActionButton)findViewById(R.id.fab3);

        fabStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!preferito){
                    fabStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_white_18dp));
                    preferito=true;
                }
                else{
                    fabStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_white_18dp));
                    preferito=false;
                }
            }
        });

        if(!b){
            scontoList = new ArrayList<>();
            adapter = new ScontoAdapter(this, scontoList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            //recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            getInfo();
            recyclerView.setAdapter(adapter);
            fabChiama.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "3209326966"));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            });
        }
        else{
            recyclerView.setVisibility(View.GONE);
            fabChiama.setVisibility(View.GONE);
            //fabMappa.setVisibility(View.GONE);
        }

        fabMappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DettaglioActivity.this, PercorsoActivity.class);
                startActivity(i);
            }
        });


    }


    @Override
    public void onOffsetChanged (AppBarLayout appBarLayout,int i){
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(fabMappa).scaleY(0).scaleX(0).start();
                ViewCompat.animate(fabChiama).scaleY(0).scaleX(0).start();
                ViewCompat.animate(fabStar).scaleY(0).scaleX(0).start();

            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(fabMappa).scaleY(1).scaleX(1).start();
                ViewCompat.animate(fabChiama).scaleY(1).scaleX(1).start();
                ViewCompat.animate(fabStar).scaleY(1).scaleX(1).start();
            }
        }
    }


    public static void start(Context c) {
        c.startActivity(new Intent(c, DettaglioActivity.class));
    }


    private void getInfo() {
        Sconto a = new Sconto("Sconto 1", "primo coupon", "download/barletta.jpg");
        scontoList.add(a);
        a = new Sconto("Sconto 2", "secondo coupon", "download/barletta.jpg");
        scontoList.add(a);
        a = new Sconto("Sconto 3", "secondo coupon", "download/barletta.jpg");
        scontoList.add(a);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
