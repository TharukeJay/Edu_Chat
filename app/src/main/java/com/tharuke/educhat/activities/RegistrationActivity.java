package com.tharuke.educhat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
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

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getCanonicalName();
    private CoordinatorLayout userRegCoordinatorLayout;
    private TextInputLayout ilEmail, ilPassword, ilConfirmPassword;
    private EditText snpEmail, snpPassword, snpConfirmPassword;
    private String confirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    //firebase auth
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setUI();
        setListeners();
    }

    private void setUI() {

        ilEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        ilPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        ilConfirmPassword = (TextInputLayout) findViewById(R.id.inputLayoutConfirmPassword);
        snpEmail = (EditText) findViewById(R.id.sup_email);
        snpPassword = (EditText) findViewById(R.id.sup_Password);
        snpConfirmPassword = (EditText) findViewById(R.id.sup_ConfirmPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userRegCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.userRegCoordinatorLayout);
        TextView tvAppName = (TextView) findViewById(R.id.appName);


//        etEmail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppConstants.PROFILE_DATA_EMAIL_MAX_LENGTH)});
//        etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppConstants.REG_PASSWORD_MAX_LENGTH)});
//        etConfirmPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppConstants.REG_PASSWORD_MAX_LENGTH)});


    }

    private void setListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                extractFieldsFromUI();
//                startRegistrationProcess();
                signUpInFirebaseAuth();
            }
        });
    }

    private void startRegistrationProcess() {
        btnRegister.setEnabled(false);
        btnRegister.setText(R.string.user_register_registering_msg);
        progressBar.setVisibility(View.VISIBLE);
        signUpInFirebaseAuth();

    }

    private void signUpInFirebaseAuth() {
        String email = snpEmail.getText().toString().toLowerCase();
        String password = snpPassword.getText().toString();

        Log.d("Tag", "credentials ===> " + email + " ===> " + password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Sign Up Failed!: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        } else {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                });

    }

//    private void extractFieldsFromUI() {
//        email = snpEmail.getText().toString().toLowerCase();
//        password = snpPassword.getText().toString();
//        confirmPassword = snpConfirmPassword.getText().toString();
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        RegistrationActivity.this.finish();
    }
}