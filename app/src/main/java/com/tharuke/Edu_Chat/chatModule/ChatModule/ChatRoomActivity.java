package com.tharuke.Edu_Chat.chatModule.ChatModule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tharuke.Edu_Chat.R;
import com.tharuke.Edu_Chat.chatModule.Adapter.ChatRoomListAdapter;
import com.tharuke.Edu_Chat.chatModule.Adapter.MessageAdapter;
import com.tharuke.Edu_Chat.chatModule.Model.ChatRoomList;
import com.tharuke.Edu_Chat.chatModule.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    FirebaseUser fuser;
    DatabaseReference reference, userReference;
    ImageButton btn_send,btn_file_send;
    EditText text_send;
    ChatRoomListAdapter messageAdapter;
    RecyclerView recyclerView;
    private String userId;
    private String displayName;
    Intent intent;
    List<ChatRoomList> mchat;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        btn_file_send = findViewById(R.id.send_file_btn);
        intent = getIntent();

        final String grpId = intent.getStringExtra("grpId");
        final String displayName = intent.getStringExtra("displayName");
        final String thumbnail = intent.getStringExtra("imageUrl");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(view -> {

            String msg = text_send.getText().toString();
            if (!msg.equals("")) {
                sendMessage(fuser.getUid(), grpId, msg);
            } else {
                Toast.makeText(ChatRoomActivity.this, "you can't send empty message", Toast.LENGTH_SHORT).show();
            }
            text_send.setText("");
        });

        userReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                imageUrl = user.getImageUrl();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("ChatRoomList").child(grpId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatRoomList chatRoomList = dataSnapshot.getValue(ChatRoomList.class);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("chat_room_pics/" + thumbnail);
                username.setText(displayName);
                if(thumbnail.equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(storageRef).into(profile_image);
                }

                readMessages(fuser.getUid(), grpId, imageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender,final String grpId, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);

        reference.child("ChatRoomList").child(grpId).push().setValue(hashMap);

    }

    private void readMessages(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();
        final String grpId = intent.getStringExtra("grpId");
        reference = FirebaseDatabase.getInstance().getReference("ChatRoomList").child(grpId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatRoomList chat = snapshot.getValue(ChatRoomList.class);
//                    if (chat.getSender().equals(myid)) {
                        mchat.add(chat);
//                    }

                    messageAdapter = new ChatRoomListAdapter(ChatRoomActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}