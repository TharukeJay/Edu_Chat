package com.tharuke.Edu_Chat.chatModule.Fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tharuke.Edu_Chat.R;
import com.tharuke.Edu_Chat.chatModule.Adapter.ChatRoomAdapter;
import com.tharuke.Edu_Chat.chatModule.Adapter.UserAdapter;
import com.tharuke.Edu_Chat.chatModule.Model.ChatRoom;
import com.tharuke.Edu_Chat.chatModule.Model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment {

    private RecyclerView recyclerView;

    private ChatRoomAdapter chatRoomAdapter;
    private List<ChatRoom> chatRoomList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatRoomList = new ArrayList<>();
        readChatRooms();

        return view;
    }

    private void readChatRooms() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat_rooms");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatRoomList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);

                        assert chatRoom != null;
                        assert firebaseUser != null;
                            chatRoomList.add(chatRoom);

                    }

                chatRoomAdapter = new ChatRoomAdapter(getContext(), chatRoomList);
                    recyclerView.setAdapter(chatRoomAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}