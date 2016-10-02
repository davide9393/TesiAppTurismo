package it.uniba.di.ivu.progettotesi.appturismo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrazione extends AppCompatActivity{

    private static final String TAG = "SignupActivity";
    public static final String PREFS_NAME = "dati";

    private Button btnRegistrati;
    private SignInButton btnRegistratiGoogle;
    private String nome,cognome;
    private EditText editNome;
    private EditText editCognome;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editRePassword;
    private TextView login;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registrazione");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editNome= (EditText) findViewById(R.id.edit_nome);
        editCognome= (EditText) findViewById(R.id.edit_cognome);
        editPassword= (EditText) findViewById(R.id.edit_password);
        editRePassword= (EditText) findViewById(R.id.edit_reEnterPassword);
        editEmail= (EditText) findViewById(R.id.edit_email);

        btnRegistrati = (Button) findViewById(R.id.btn_registati);
        btnRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Caricamento");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        showProgressDialog();

        nome = editNome.getText().toString();
        cognome = editCognome.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String reEnterPassword = editRePassword.getText().toString();


        mAuth.createUserWithEmailAndPassword(email,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registrazione.this, getResources().getString(R.string.registrazionefallita), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(Registrazione.this, getResources().getString(R.string.registrazionecompletata), Toast.LENGTH_SHORT).show();
                onSignupSuccess();
            }
        });


        hideProgressDialog();
    }





    public boolean validate() {
        boolean valid = true;

        String nome = editNome.getText().toString();
        String cognome = editCognome.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String reEnterPassword = editRePassword.getText().toString();

        if (nome.isEmpty()) {
            editNome.setError(getResources().getString(R.string.inseriscinome));
            valid = false;
        } else {
            editNome.setError(null);
        }

        if (cognome.isEmpty()) {
            editCognome.setError(getResources().getString(R.string.inseriscicognome));
            valid = false;
        } else {
            editCognome.setError(null);
        }

        if (email.isEmpty()) {
            editEmail.setError(getResources().getString(R.string.inserisciindirizzoemail));
            valid = false;
        } else {
            editEmail.setError(null);
        }

        if (password.isEmpty()) {
            editPassword.setError(getResources().getString(R.string.inserisciindirizzoemail));
            valid = false;
        } else {
            editPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            editRePassword.setError(getResources().getString(R.string.passwordnoncorrsipondente));
            valid = false;
        } else {
            editRePassword.setError(null);
        }

        return valid;
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.registrazionefallita), Toast.LENGTH_LONG).show();
    }

    public void onSignupSuccess() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome + " " + cognome).build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        DatabaseReference mUtente= FirebaseDatabase.getInstance().getReference();
                        mUtente.child("Utente").child(user.getUid()).setValue(new Utente(user.getEmail()));
                    }
                }
            });}


        setResult(RESULT_OK, null);

        Intent intent = new Intent(Registrazione.this, Login.class);
        //  intent.putExtra("nome", );
        Toast.makeText(Registrazione.this, getResources().getString(R.string.puoiaccedere), Toast.LENGTH_SHORT).show();
        startActivity(intent);

        finish();
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

