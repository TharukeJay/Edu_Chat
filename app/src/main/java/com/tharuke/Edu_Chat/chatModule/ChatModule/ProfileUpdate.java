package com.tharuke.Edu_Chat.chatModule.ChatModule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tharuke.Edu_Chat.R;

import java.util.HashMap;

public class ProfileUpdate extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button btn_update;

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        username = findViewById(R.id.username);
        email = findViewById(R.id.regEmail);
        password = findViewById(R.id.password);
        btn_update = findViewById(R.id.btn_update);
        RadioButton radioButton2,radioButton3,radioButton4;

        auth = FirebaseAuth.getInstance();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(ProfileUpdate.this,"All fileds are required",Toast.LENGTH_SHORT).show();
                }else if(txt_password.length() < 6){
                    Toast.makeText(ProfileUpdate.this,"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                }else{
//                    update(txt_username,txt_email,txt_password);
                }

            }
        });
    }
//    private void update(final String username, String email, String password){
//
//        auth.updateCurrentUser(email, password)
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()){
//                        FirebaseUser firebaseUser = auth.getCurrentUser();
//                        assert firebaseUser != null;
//                        String userid = firebaseUser.getUid();
//
//                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
//
//                        HashMap<String, String> hashMap = new HashMap<>();
//                        hashMap.put("id", userid);
//                        hashMap.put("username",username);
//                        hashMap.put("imageUrl","default");
//                        hashMap.put("status","offline");
//                        hashMap.put("search",username.toLowerCase());
//
//                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>(){
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task){
//                                if(task.isSuccessful()){
//                                    Intent intent= new Intent(ProfileUpdate.this, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                    finish();
//                                    Toast.makeText(ProfileUpdate.this,"Profile Updated",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }else{
//                        Toast.makeText(ProfileUpdate.this,"you can't Update with this email or password",Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
}