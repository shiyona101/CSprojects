package com.example.hackathon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMsgs> messages;

    public static final int VIEW_TYPE_USER = 1;
    public static final int VIEW_TYPE_BOT = 2;

    public MessageAdapter(List<ChatMsgs> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMsgs message = messages.get(position);
        if (message.isSentByUser()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_bot, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMsgs message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).textView.setText(message.getContent());
        } else {
            ((BotMessageViewHolder) holder).textView.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView userIcon;

        public UserMessageViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewMessage);
            userIcon = itemView.findViewById(R.id.userIcon);
        }
    }

    public static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView botIcon;

        public BotMessageViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewMessage);
            botIcon = itemView.findViewById(R.id.botIcon);
        }
    }
}
