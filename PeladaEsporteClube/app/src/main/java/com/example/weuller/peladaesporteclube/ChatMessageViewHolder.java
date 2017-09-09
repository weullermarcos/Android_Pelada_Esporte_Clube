package com.example.weuller.peladaesporteclube;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weuller.peladaesporteclube.Models.ChatMessage;

/**
 * Created by weullermarcos on 09/09/17.
 */

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserName;
    public TextView txtChatMessage;
    public ImageView imgUserImage;
    public ChatMessage chatMessage;

    public ChatMessageViewHolder(View itemView) {
        super(itemView);

        txtUserName = (TextView) itemView.findViewById(R.id.list_item_message_txtUserName);
        txtChatMessage = (TextView) itemView.findViewById(R.id.list_item_message_txtChatMessage);
        imgUserImage = (ImageView) itemView.findViewById(R.id.list_item_message_imgUserImage);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        params.setMargins(35,0,0,0);

    }
}
