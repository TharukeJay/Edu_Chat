package com.tharuke.Edu_Chat.chatModule.ChatModule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    MaterialEditText firstName, lastName;
    Button btn_update;
    RadioButton radioButton;
    RadioGroup radioGroup;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        btn_update = findViewById(R.id.btn_update);
        radioGroup = findViewById(R.id.radioGroup);

        auth = FirebaseAuth.getInstance();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton = findViewById(selectedId);

                String txt_firstName = firstName.getText().toString();
                String txt_lastName = lastName.getText().toString();
                if(TextUtils.isEmpty(txt_firstName) || TextUtils.isEmpty(txt_lastName)){
                    Toast.makeText(ProfileUpdate.this,"All fileds are required",Toast.LENGTH_SHORT).show();
                }else{
                    update(txt_firstName,txt_lastName,radioButton.getText().toString());
                }

            }
        });
    }
    private void update(final String firstName, String lastName,String radioValue){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",userId);
        hashMap.put("firstName",firstName);
        hashMap.put("lastName",lastName);
        hashMap.put("gender",radioValue);

        reference.updateChildren(hashMap);

        Toast.makeText(ProfileUpdate.this,"Updated",Toast.LENGTH_SHORT).show();
    }
}