package com.sleepy.tok.search_activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sleepy.tok.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersViewHolder extends RecyclerView.ViewHolder {
    public TextView tvOtherUserScreenName, tvOtherUserBio;
    public ImageView ivOtherUserThumbnail;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);
        tvOtherUserScreenName = itemView.findViewById (R.id.tvOtherUserScreenName);
        tvOtherUserBio = itemView.findViewById(R.id.tvOtherUserBio);
        ivOtherUserThumbnail = itemView.findViewById(R.id.ivOtherUserThumbnail);
    }
}
