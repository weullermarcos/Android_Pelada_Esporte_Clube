package com.example.weuller.peladaesporteclube.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weuller.peladaesporteclube.ChatMessageViewHolder;
import com.example.weuller.peladaesporteclube.Models.ChatMessage;
import com.example.weuller.peladaesporteclube.R;

import java.util.List;

/**
 * Created by weullermarcos on 09/09/17.
 */

public class ChatMessageRecyclerViewAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    private Context context;
    private List<ChatMessage> chatMessageList;

    public ChatMessageRecyclerViewAdapter(Context context, List<ChatMessage> chatMessageList) {

        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //cabalacho muito doido
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent, false);
        return new ChatMessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {

        ChatMessage chatMessage = chatMessageList.get(position);

        holder.txtUserName.setText(chatMessage.getUser());
        holder.txtChatMessage.setText(chatMessage.getMessage());
        holder.txtMessageDate.setText(chatMessage.getDate());

    }

    @Override
    public int getItemCount() {
        return this.chatMessageList.size();
    }
}
