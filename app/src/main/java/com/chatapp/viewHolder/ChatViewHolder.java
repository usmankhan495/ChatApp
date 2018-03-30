package com.chatapp.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.R;

/**
 * Created by Usman on 3/28/18.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView mMessageTextView;
    public TextView mNameTextView;
    public ImageView mImageView;
    public ChatViewHolder(View itemView) {
        super(itemView);

        mMessageTextView=(TextView)itemView.findViewById(R.id.message);
        mNameTextView=(TextView)itemView.findViewById(R.id.name);
        mImageView=(ImageView)itemView.findViewById(R.id.image);

    }


}
