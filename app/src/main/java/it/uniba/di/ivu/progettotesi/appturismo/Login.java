package it.uniba.di.ivu.progettotesi.appturismo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_SIGNUP = 0;

    public static final String PREFS_NAME = "dati";

    private EditText editEmail;
    private EditText editPassword;
    private TextView registrati;
    private static Utente user=new Utente();
    private String email;
    private String password;
    private Button btnLogin;
    private ProgressDialog mProgressDialog;
    private static Boolean b=false;
    public static Toolbar toolbar;
    private SignInButton btnAccediGoogle;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_GOOGLE = 007;

    private LoginButton btnAccediFacebook;
    private CallbackManager callbackManager;

    private String nome;
    private String immagineProfilo;

    public static Boolean facebook;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        btnAccediGoogle = (SignInButton) findViewById(R.id.btn_accedi_google);
        btnAccediGoogle.setSize(SignInButton.SIZE_STANDARD);
        btnAccediGoogle.setScopes(gso.getScopeArray());
        btnAccediGoogle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnAccediGoogle.setColorScheme(SignInButton.COLOR_LIGHT);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editEmail= (EditText) findViewById(R.id.edit_email);
        editPassword= (EditText) findViewById(R.id.edit_password);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    SharedPreferences settings;
                    SharedPreferences.Editor editor;
                    settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    editor = settings.edit();

                    editor.putString("nome", user.getDisplayName());
                    editor.putString("email", user.getEmail());
                    editor.putString("id", user.getUid());
                    editor.putString("immagineProfilo", immagineProfilo);
                    editor.apply();
                    DatabaseReference mUtente= FirebaseDatabase.getInstance().getReference();
                    mUtente.child("Utente").child(user.getUid()).setValue(new Utente(user.getEmail()));
                    onLoginSuccess();
                } else {
                }
            }
        };



        //LOGIN STANDARD
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //ACCESSO CON GOOGLE
        btnAccediGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent, REQUEST_GOOGLE);
            }
        });



        //ACCESSO CON FACEBOOK
        btnAccediFacebook = (LoginButton)findViewById(R.id.login_button);
        btnAccediFacebook.setReadPermissions("public_profile");
        btnAccediFacebook.setReadPermissions("email,publish_actions");
        btnAccediFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this, "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });



        registrati = (TextView) findViewById(R.id.link_registrati);
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registrazione.class);
                FirebaseAuth.getInstance().signOut();
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        showProgressDialog();


        password = editPassword.getText().toString();
        email = editEmail.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                onLoginFailed();
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage( getResources().getString(R.string.caricamento));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public boolean validate() {
        boolean valid = true;

        String password = editPassword.getText().toString();
        String email = editEmail.getText().toString();

        if (password.isEmpty()) {
            editPassword.setError( getResources().getString(R.string.inseriscipassword));
            valid = false;
        } else {
            editPassword.setError(null);
        }

        if (email.isEmpty()) {
            editEmail.setError( getResources().getString(R.string.inserisciusername));
            valid = false;
        } else {
            editEmail.setError(null);
        }

        return valid;
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(),  getResources().getString(R.string.loginfallito), Toast.LENGTH_LONG).show();
    }
    public void onLoginSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);

            }else{
                Toast.makeText(Login.this, getResources().getString(R.string.accessofallito), Toast.LENGTH_SHORT).show();
            }
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, getResources().getString(R.string.accessofallito), Toast.LENGTH_SHORT).show();
                        } else {
                            nome = acct.getDisplayName();
                            email = acct.getEmail();

                            if(acct.getPhotoUrl() != null)
                                immagineProfilo=acct.getPhotoUrl().toString();

                            /*SharedPreferences settings;
                            SharedPreferences.Editor editor;
                            settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                            editor = settings.edit();
                            editor.putString("immagineProfilo", immagineProfilo);
                            editor.apply();
                            onLoginSuccess();*/
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(final AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, getResources().getString(R.string.accessofallito), Toast.LENGTH_SHORT).show();
                        }else{
                            GraphRequest request = GraphRequest.newMeRequest(
                                    token,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(final JSONObject object, GraphResponse response) {
                                            final JSONObject jsonObject = response.getJSONObject();
                                            nome = "";
                                            email = "";
                                            immagineProfilo = "";
                                            try {
                                                nome = jsonObject.getString("name");
                                                email =  jsonObject.getString("email");
                                                immagineProfilo = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");

                                                SharedPreferences settings;
                                                SharedPreferences.Editor editor;
                                                settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                                editor = settings.edit();
                                                editor.putString("immagineProfilo", immagineProfilo);
                                                editor.apply();
                                                //onLoginSuccess();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,picture");
                            parameters.putString("type", "large");
                            parameters.putBoolean("redirect",true);
                            request.setParameters(parameters);
                            request.executeAsync();

                        }

                    }
                });
    }
}


