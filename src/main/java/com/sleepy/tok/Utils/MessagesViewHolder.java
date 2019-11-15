package com.sleepy.tok.Utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sleepy.tok.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ImageView ivImageMessage;

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        tvMessage = itemView.findViewById(R.id.tvMessage);
        ivImageMessage =itemView.findViewById(R.id.ivImageMessage);
    }
}
