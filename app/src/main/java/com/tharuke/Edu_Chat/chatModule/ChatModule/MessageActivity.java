package com.tharuke.Edu_Chat.chatModule.ChatModule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

    private static final int RESULT_LOAD_IMG = 1;
    private static final int LAUNCH_MAIN_ACTIVITY = 2;

    private static final String tfModel = "fer2013_mini_XCEPTION.102-0.66.pb";

    private static final int REQUEST_CODE_PERMISSION = 2;

    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    protected MaterialListView mListView;
    protected String mTestImgPath;

    CircleImageView profile_image;
    TextView username, mPredict;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_camera, btn_send, btn_gallery, btn_file_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;
    String userid;

    ValueEventListener seenListener;

    String modelName = null;

    public Bundle bundle = new Bundle();

    String class_name_new = "";
    private DatabaseReference RootRef;


//    @ViewById(R.id.btn_camera)
//    protected ImageButton Btn_camera;

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
//        saveModel();
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
        btn_camera = findViewById(R.id.btn_camera);
        text_send = findViewById(R.id.text_send);
        btn_gallery = findViewById(R.id.btn_gallery);
        mListView = findViewById(R.id.material_listview);
        btn_file_send = findViewById(R.id.send_file_btn);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("firebase current user ===> " + fuser);


//        btn_camera.setOnClickListener(view -> {
//
//            Intent intent = new Intent(this, MainActivity_.class);
//            Bundle b = new Bundle();
//            b.putString("btnCamera", "camera");
//            intent.putExtras(b);
//            startActivityForResult(intent, LAUNCH_MAIN_ACTIVITY);
//        });
//        @Override
//        protected void onActivityResult(int requestCode,int resultCode,Intent data){
//            super.onActivityResult(requestCode,resultCode,data);
//            if(requestCode == RESULT_OK){
//                Bitmap b = (Bitmap)data.getExtras().get("data");
//                myImage.setImagebitmap(b);
//            }
//        }

//        btn_gallery.setOnClickListener(view -> {
//
//            Intent intent = new Intent(this, MainActivity_.class);
////            Bundle b = new Bundle();
////            b.putString("btnGallery", "gallery");
////            intent.putExtras(b);
//            startActivityForResult(intent, LAUNCH_MAIN_ACTIVITY);
//        });

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
            CharSequence[] options = new CharSequence[]
                    {
                            "Images",
                            "PDF File",
                            "Ms Word File"
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the File");

            builder.setItems(options, (dialogInterface, i) -> {
                if (i == 0) {
                    checker = "image";

//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 438);
                }
                if (i == 1) {
                    checker = "pdf";
                }
                if (i == 2) {
                    checker = "docx";
                }
            });
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

        reference.child("Chats").push().setValue(hashMap);
//        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//               .child(fuser.getUid())
//               .child(userid);
//       chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//           @Override
//           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.exists()){
//                    chatRef.child("id").setValue(userid);
//                }
//           }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError databaseError) {
//
//           }
//       });

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
            if (requestCode == LAUNCH_MAIN_ACTIVITY) {
                String result = data.getStringExtra("emojiString");
                text_send.setText(result);
            } else {
                text_send.setText("");
            }

            //share files in on activity result method
            if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                loadingBar.setTitle("Sending File");
                loadingBar.setMessage("Please wait,We are sending that file..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                fileUri = data.getData();

                if (!checker.equals("image")) {


                } else if (checker.equals("image")) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image File");

                    final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                    final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                    DatabaseReference userMessageKeyRef = RootRef.child("Message")
                            .child(messageSenderID).child(messageReceiverID).push();

                    final String messagePushID = userMessageKeyRef.getKey();
                    final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");
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

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("sender", messageSenderID);
                            messageTextBody.put("receiver", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
//                                    messageTextBody.put("puttime", saveCurrentTime);
//                                    messageTextBody.put("date", saveCurrentDate);


                            Map mesageBodyDetails = new HashMap();
                            mesageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            mesageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(mesageBodyDetails).addOnCompleteListener((task1)->{
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
                } else {
                    Toast.makeText(this, "Nothing selected, Error.", Toast.LENGTH_SHORT).show();
                }

        }

    } catch(
    Exception e)

    {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
    }

}

}
