package com.tharuke.educhat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tharuke.educhat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getCanonicalName();
    private CoordinatorLayout loginCoordinatorLayout;
    private EditText lgnEmail, lgnPassword;
    private TextInputLayout ilEmail, ilPassword;
    private String email, password;
    private Button btnLogin;
    private TextView lgnRegister, lgnForgotPassword;
    private ProgressBar progressBar;
    private Context context;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUI();
        signOut();
        mAuth = FirebaseAuth.getInstance();
        this.context = this;
        setListeners();
    }

    private void setUI() {

        ilEmail = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        ilPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        lgnEmail = (EditText) findViewById(R.id.sup_email);
        lgnPassword = (EditText) findViewById(R.id.sup_Password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.loginCoordinatorLayout);
        TextView tvAppName = (TextView) findViewById(R.id.appName);

        lgnRegister = (TextView) findViewById(R.id.tvRegister);
        SpannableString register = new SpannableString(this.getResources().getString(R.string.login_register));
        register.setSpan(new UnderlineSpan(), 0, register.length(), 0);
        lgnRegister.setText(register);

        lgnForgotPassword = (TextView) findViewById(R.id.lgnForgotPassword);
        SpannableString forgotPassword = new SpannableString(this.getResources().getString(R.string.login_forgot_password));
        forgotPassword.setSpan(new UnderlineSpan(), 0, forgotPassword.length(), 0);
        lgnForgotPassword.setText(forgotPassword);

    }

    private void setListeners() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                extractFieldsFromUI();
                loginProcess();
            }
        });

//        lgnForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetPassword();
//            }
//        });

        lgnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registerIntent);
                LoginActivity.this.finish();
            }
        });

    }

    private void loginProcess() {

            btnLogin.setEnabled(false);
            btnLogin.setText(R.string.login_progress_message_authenticating);
            progressBar.setVisibility(View.VISIBLE);
            loginFirebaseAuth(email, password);
    }

    private void loginFirebaseAuth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            resetUI();
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void resetUI() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnLogin.setText(R.string.login);
    }

    private void extractFieldsFromUI() {

        email = lgnEmail.getText().toString().trim().toLowerCase();
        password = lgnPassword.getText().toString();
    }

    public static void signOut() {
        FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().signOut();
    }
}