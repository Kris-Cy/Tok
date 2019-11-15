package com.sleepy.tok.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.sleepy.tok.R;
import com.sleepy.tok.data_models.Messages;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {

    private FirebaseAuth mAuth;
    private List<Messages> messagesList;


    public MessagesAdapter (List<Messages>messagesList){
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_message, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid().toString();

        Messages messages = messagesList.get(position);

        String senderID=messages.getFrom().toString();
        String messageType = messages.getMessage_type();

        if(senderID.equals(currentUserID)){
            holder.tvMessage.setBackgroundResource(R.drawable.bg_user_message);
        }
        else{
            holder.tvMessage.setBackgroundResource(R.drawable.bg_other_message);
        }

        if(messageType.equals("text")){
            holder.ivImageMessage.setVisibility(View.GONE);
            holder.tvMessage.setText(messages.getMessage());
        }
        else{
            holder.ivImageMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setVisibility(View.GONE);
            Picasso.get().load(messages.getMessage()).placeholder(R.drawable.ic_camera_light_grey).into(holder.ivImageMessage);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
