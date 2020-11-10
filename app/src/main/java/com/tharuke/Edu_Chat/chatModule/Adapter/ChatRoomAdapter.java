package com.tharuke.Edu_Chat.chatModule.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tharuke.Edu_Chat.R;
import com.tharuke.Edu_Chat.chatModule.ChatModule.ChatRoomActivity;
import com.tharuke.Edu_Chat.chatModule.ChatModule.MessageActivity;
import com.tharuke.Edu_Chat.chatModule.Model.ChatRoom;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatRoom> chatRoomList;

    public ChatRoomAdapter(Context mContext, List<ChatRoom> chatRoomList){
        this.chatRoomList = chatRoomList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_room_item, viewGroup, false);
        return new ChatRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChatRoom chatList = chatRoomList.get(position);
        holder.username.setText(chatList.getDisplayName());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("chat_room_pics/" + chatList.getThumbnail());
        if(chatList.getThumbnail().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Picasso.with(mContext).load(chatList.getThumbnail()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                intent.putExtra("grpId", chatList.getId());
                intent.putExtra("displayName", chatList.getDisplayName());
                intent.putExtra("imageUrl", chatList.getThumbnail());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}
