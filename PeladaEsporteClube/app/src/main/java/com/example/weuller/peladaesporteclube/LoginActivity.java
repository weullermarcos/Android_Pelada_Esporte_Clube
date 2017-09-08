package com.example.weuller.peladaesporteclube;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Services.DialogService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "LOG";

    EditText edtEmail, edtPassword;
    Button btnSigin, btnRegister;
    SignInButton btnGoogle;
    LoginButton btnFacebook;

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DialogService dialog = new DialogService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //verifica se o usuárioe está logado
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            goToMain();
            return;
        }

        edtEmail = (EditText) findViewById(R.id.login_edtEmail);
        edtPassword = (EditText) findViewById(R.id.login_edtPassword);
        btnSigin = (Button) findViewById(R.id.login_btnSigin);
        btnRegister = (Button) findViewById(R.id.login_btnRegister);
        btnGoogle = (SignInButton) findViewById(R.id.login_btnGoogle);
        btnFacebook = (LoginButton) findViewById(R.id.login_btnFacebook);
        btnFacebook.setReadPermissions("email", "public_profile");

        mCallbackManager = CallbackManager.Factory.create();

        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("LOG", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                Log.d("LOG", "facebook:onCancel");
                //updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {

                Log.d("LOG", "facebook:onError", error);
                //updateUI(null);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        btnSigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtEmail.getText().toString().trim().isEmpty() || edtPassword.getText().toString().trim().isEmpty()) {

                    Toast.makeText(LoginActivity.this, "Favor preencher todos os campos..", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.showProgressDialog("Efetuando Login", "Aguarde", LoginActivity.this);

                //FUNCIONANDO
                mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(! task.isSuccessful()){

                            dialog.hideProgressDialog();
                            Toast.makeText(LoginActivity.this, "Usuário ou senha inválida", Toast.LENGTH_SHORT).show();

                        }else{

                            user = task.getResult().getUser();

                            if(user != null){
                                goToMain();
                            }
                        }
                    }
                });

                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User is signed in
                            Log.d("LOG", "onAuthStateChanged:signed_in:" + user.getUid());
                        } else {
                            // User is signed out
                            Log.d("LOG", "onAuthStateChanged:signed_out");
                        }
                    }
                };
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });

        //        Login com Google
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        dialog.showProgressDialog("Efetuando Login", "Aguarde", LoginActivity.this);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {

                dialog.hideProgressDialog();
                Log.d("LOG", result.toString());
                Toast.makeText(LoginActivity.this, "Deu Erro..", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            //Facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d("LOG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("LOG", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {

                            goToMain();
                        }
                        else {

                            dialog.hideProgressDialog();
                            Log.w("LOG", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Erro ao autenticar.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        dialog.hideProgressDialog();
        Log.d("LOG", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Erro no Google Play Services.", Toast.LENGTH_SHORT).show();
    }

    private void handleFacebookAccessToken(AccessToken token) {

        Log.d("LOG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMain();
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                        //hideProgressDialog();
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void goToMain(){

        dialog.hideProgressDialog();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
