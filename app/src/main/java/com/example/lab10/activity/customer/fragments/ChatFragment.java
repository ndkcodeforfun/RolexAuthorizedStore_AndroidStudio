package com.example.lab10.activity.customer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.api.Message.MessageRepository;
import com.example.lab10.model.MessageDtoRequest;
import com.example.lab10.model.MessageDtoResponse;
import com.example.lab10.model.ChatHistoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private int customerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("customerId", -1);

        Log.d("ChatFragment", "Retrieved CustomerId: " + customerId);

        if (customerId != -1) {
            loadChatHistory(customerId);
        } else {
            Toast.makeText(getContext(), "Customer ID không tìm thấy", Toast.LENGTH_SHORT).show();
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return view;
    }

    private void loadChatHistory(int customerId) {
        MessageRepository.getChatHistoryByCustomerId(customerId).enqueue(new Callback<ChatHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatHistoryResponse> call, Response<ChatHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatAdapter.setMessages(response.body().getMessageHistory());
                } else {
                    Log.e("ChatFragment", "Tải lịch sử chat thất bại (onResponse)");
                }
            }

            @Override
            public void onFailure(Call<ChatHistoryResponse> call, Throwable t) {
                Log.e("ChatFragment", "Tải lịch sử chat thất bại (onFailure)", t);
            }
        });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString();
        if (content.isEmpty()) {
            return;
        }

        Log.d("ChatFragment", "Đang gửi tin nhắn với CustomerId: " + customerId);

        MessageDtoRequest request = new MessageDtoRequest();
        request.setCustomerId(customerId);
        request.setContent(content);
        request.setSendTime(java.time.LocalDateTime.now().toString());
        request.setType("CUSTOMER");

        MessageRepository.sendMessage(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadChatHistory(customerId);
                    editTextMessage.setText("");
                } else {
                    Log.e("ChatFragment", "Gửi tin nhắn thất bại (onResponse)");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ChatFragment", "Gửi tin nhắn thất bại (onFailure)", t);
            }
        });
    }

    private class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_CUSTOMER = 1;
        private static final int VIEW_TYPE_ADMIN = 2;

        private List<MessageDtoResponse> messages;

        @Override
        public int getItemViewType(int position) {
            MessageDtoResponse message = messages.get(position);
            if ("CUSTOMER".equals(message.getType())) {
                return VIEW_TYPE_CUSTOMER;
            } else {
                return VIEW_TYPE_ADMIN;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE_CUSTOMER) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_customer, parent, false);
                return new CustomerViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_admin, parent, false);
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

        class CustomerViewHolder extends RecyclerView.ViewHolder {
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

        class AdminViewHolder extends RecyclerView.ViewHolder {
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



}
