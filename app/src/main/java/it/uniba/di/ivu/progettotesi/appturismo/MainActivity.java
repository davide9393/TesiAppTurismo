package it.uniba.di.ivu.progettotesi.appturismo;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements
        EsploraFragment.FragmentListener,
        EsploraContenutoFragment.FragmentListener2,
        EsploraDettaglioFragment.FragmentListener,
        DiarioFragment.FragmentListener,
        DettaglioDiarioFragmentMappa.FragmentListener,
        DettaglioDiarioFragmentLista.FragmentListener,
        GoogleApiClient.OnConnectionFailedListener,
        CustomizzaSezioneFragment.FragmentListener{

    DrawerLayout drawerLayout;
    public static Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imageView;
    ActionBar actionBar;
    private AppBarLayout appBar;
    private TabLayout tabLayout;
    public static final String PREFS_NAME = "dati";
    public static Boolean persistenceEnabled=false;
    private static Boolean control=false;
    public static GoogleApiClient mGoogleApiClient;

    static FragmentManager manager;
    static Context context;
    public static AppCompatActivity activity;
    public static ActionBarDrawerToggle mDrawerToggle;

    private TextView nome;
    private TextView email;
    private ImageView immagineProfilo;
    private String uri;

    public CollapsingToolbarLayout getCollapsingToolbar() {
        return collapsingToolbar;
    }
    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View view =  navigationView.getHeaderView(0);
        TextView nomecognome = (TextView) view.findViewById(R.id.text_nome);
        TextView email = (TextView) view.findViewById(R.id.text_email);
        immagineProfilo = (ImageView) view.findViewById(R.id.immagineProfilo);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String e=settings.getString("email", "nullo");
        uri=settings.getString("immagineProfilo", "nullo");

        if (!e.equals("nullo")){
            String n=settings.getString("nome", "nullo");
            nomecognome.setText(n);
            email.setText(e);
            control=true;
            Utility.uid =settings.getString("id","nullo");

            if(!uri.equals("nullo")){
                Glide.with(getApplicationContext()).load(uri).into(immagineProfilo);
            }

        }



        if(!persistenceEnabled){
            persistenceEnabled=true;
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        imageView = (ImageView) findViewById(R.id.backdrop);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        context = getApplicationContext();
        activity = MainActivity.this;
        manager = getSupportFragmentManager();
        //actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);

        /////
        mDrawerToggle = new ActionBarDrawerToggle(
                this,  drawerLayout,toolbar,
                R.string.app_name, R.string.action_settings
        );
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        /////

        //NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        setupNavigationDrawerContent(navigationView);

        //First fragment
        setFragment(0);

        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void disableCollapse() {
        imageView.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        collapsingToolbar.setTitleEnabled(false);
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        //params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        //        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
    }

    @Override
    public void enableCollapse() {
        imageView.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        // collapsingToolbar.setTitleEnabled(true);
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        //params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        //        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
    }

    //metodo da modificare per prendere la foto da firebase
    @Override
    public void setImmagine(String url){
        // String filePath = Environment.getExternalStorageDirectory()
        //        .getAbsolutePath() + File.separator + "download/davide.jpg";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + url;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void setBackButton(){
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setHomeButton(){
        mDrawerToggle = new ActionBarDrawerToggle(
                this,  drawerLayout,toolbar,
                R.string.app_name, R.string.action_settings
        );
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        NavigationView navigationView=(NavigationView) findViewById(R.id.navigation_view);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            FragmentManager manager = getSupportFragmentManager();
            if(manager.getBackStackEntryCount() > 1) {
                super.onBackPressed();
                Fragment currentFragment = (Fragment) manager.findFragmentById(R.id.fragment);
                if (currentFragment instanceof EsploraFragment){
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
                else if (currentFragment instanceof EsploraDettaglioFragment){
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
                else if(currentFragment instanceof DiarioFragment){
                    navigationView.getMenu().getItem(2).setChecked(true);
                }
                else if(currentFragment instanceof StarredFragment){
                    navigationView.getMenu().getItem(1).setChecked(true);
                }
                else if(currentFragment instanceof CouponFragment){
                    navigationView.getMenu().getItem(3).setChecked(true);
                }
            }
            else{
                // Toast.makeText(MainActivity.this,"Vuoi uscire?",Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.vuoiuscire)+"?")
                        .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish();
                // User refused to grant permission.
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_inbox:
                                menuItem.setChecked(true);
                                setFragment(0);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_starred:
                                menuItem.setChecked(true);
                                setFragment(1);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_diario:
                                menuItem.setChecked(true);
                                setFragment(2);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_miei_coupon:
                                Toast.makeText(MainActivity.this, "i", Toast.LENGTH_SHORT).show();
                                menuItem.setChecked(true);
                                setFragment(3);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_settings:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.item_navigation_drawer_help_and_feedback:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_logout:
                                menuItem.setChecked(true);
                                setFragment(4);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });
    }

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                FragmentManager fm=getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment, EsploraFragment.newInstance());
                ft.addToBackStack(null);
                ft.commit();
                fm.executePendingTransactions();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                StarredFragment starredFragment = new StarredFragment();
                fragmentTransaction.replace(R.id.fragment, starredFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 2:
                if(control) {
                    FragmentManager fm2=getSupportFragmentManager();
                    FragmentTransaction ft2 = fm2.beginTransaction();
                    ft2.replace(R.id.fragment, DiarioFragment.newInstance());
                    ft2.addToBackStack(null);
                    ft2.commit();
                    fm2.executePendingTransactions();
                }else {
                    Intent login = new Intent(MainActivity.this, Login.class);
                    startActivity(login);
                }
                break;
            case 3:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                CouponFragment couponFragment = new CouponFragment();
                fragmentTransaction.replace(R.id.fragment, couponFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 4:
                /*SharedPreferences sp=getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                control=false;
                Utility.uid=null;
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                finish();*/
                SharedPreferences sp=getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                control=false;
                Utility.uid =null;
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                finish();

                break;
        }
    }






}
