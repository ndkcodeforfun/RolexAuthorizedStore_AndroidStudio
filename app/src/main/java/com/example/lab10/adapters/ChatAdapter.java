package com.example.lab10.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.model.MessageDtoResponse;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CUSTOMER_LEFT = 1;
    private static final int VIEW_TYPE_ADMIN_RIGHT = 2;

    private List<MessageDtoResponse> messages;

    @Override
    public int getItemViewType(int position) {
        MessageDtoResponse message = messages.get(position);
        if ("CUSTOMER".equals(message.getType())) {
            return VIEW_TYPE_CUSTOMER_LEFT;
        } else {
            return VIEW_TYPE_ADMIN_RIGHT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CUSTOMER_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_customer_left, parent, false);
            return new CustomerViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_admin_right, parent, false);
            return new AdminViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageDtoResponse message = messages.get(position);
        if (holder instanceof CustomerViewHolder) {
            ((CustomerViewHolder) holder).bind(message);
        } else {
            ((AdminViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public void setMessages(List<MessageDtoResponse> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewTime;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }

        void bind(MessageDtoResponse message) {
            textViewMessage.setText(message.getContent());
            textViewTime.setText(message.getSendTime());
        }
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewTime;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }

        void bind(MessageDtoResponse message) {
            textViewMessage.setText(message.getContent());
            textViewTime.setText(message.getSendTime());
        }
    }
}


