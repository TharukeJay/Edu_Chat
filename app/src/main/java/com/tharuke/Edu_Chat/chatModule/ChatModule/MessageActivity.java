package com.tharuke.Edu_Chat.chatModule.ChatModule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dexafree.materialList.view.MaterialListView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.tharuke.Edu_Chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tharuke.Edu_Chat.chatModule.Adapter.MessageAdapter;
import com.tharuke.Edu_Chat.chatModule.ChatModule.MainActivity;
import com.tharuke.Edu_Chat.chatModule.Model.Chat;
import com.tharuke.Edu_Chat.chatModule.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";


    private String messageReceiverID, messageSenderID;

    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;

    private static final int LAUNCH_MAIN_ACTIVITY = 2;



    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    protected MaterialListView mListView;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send,btn_file_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String modelName = null;

    public Bundle bundle = new Bundle();

    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        modelName = null;


        //send pdf file
        loadingBar = new ProgressDialog(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        mListView = findViewById(R.id.material_listview);
        btn_file_send = findViewById(R.id.send_file_btn);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("firebase current user ===> " + fuser);

        btn_send.setOnClickListener(view -> {

            String msg = text_send.getText().toString();
            if (!msg.equals("")) {
                sendMessage(fuser.getUid(), userid, msg);
            } else {
                Toast.makeText(MessageActivity.this, "you can't send empty message", Toast.LENGTH_SHORT).show();
            }
            text_send.setText("");
        });

        //share files
        btn_file_send.setOnClickListener(view -> {
            System.out.println("pdf button clicked");
            CharSequence options[] = new CharSequence[]
                    {
                            "Images",
                            "PDF File",
                            "Ms Word File"
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
            builder.setTitle("Select the File");
            builder.setItems(options, (dialogInterface, i) -> {
                if (i == 0) {
                    checker = "image";

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selece Image"), 438);

                }
                if (i == 1) {
                    checker = "pdf";
                }
                if (i == 2) {
                    checker = "docx";
                }
            });
            builder.show();

        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageUrl().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }

                readMessages(fuser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(userid);

    }

    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String sender,final String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("type", "msg");

        reference.child("Chats").push().setValue(hashMap);

    }


    private void readMessages(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //share files in on activity result method
            if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                loadingBar.setTitle("Sending File");
                loadingBar.setMessage("Please wait,Sending that file..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                fileUri = data.getData();

                if (!checker.equals("image")) {
                    //Todo : write pdf file send method

                } else {
                    System.out.println("Image checke id ====> " + checker);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Image_Files");

                    final String userid = intent.getStringExtra("userid");
                    fuser = FirebaseAuth.getInstance().getCurrentUser();

//                    DatabaseReference userMessageKeyRef = RootRef.child("Chats").push();

//                    final String messagePushID = userMessageKeyRef.getKey();
//                    System.out.println("User key ====> " + messagePushID);
                    final StorageReference filePath = storageReference.child(System.currentTimeMillis() + "." + "jpg");
                    uploadTask = filePath.putFile(fileUri);

                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            RootRef = FirebaseDatabase.getInstance().getReference("Chats");

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", fuser.getUid());
                            hashMap.put("receiver", userid);
                            hashMap.put("message", myUrl);
                            hashMap.put("isseen", false);
                            hashMap.put("type" , checker);

//                            reference.child("Chats").push().setValue(hashMap);

                            RootRef.push().setValue(hashMap).addOnCompleteListener((task1)->{
                                if (task1.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Toast.makeText(MessageActivity.this, "Message sent Successfuly.", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                                text_send.setText("");
                            });

                        }

                    });
                }

        }

    } catch(Exception e)

    {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
    }

}

}
