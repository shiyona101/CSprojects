package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<ChatMsgs> messageList;
    private EditText editTextMessage;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = editTextMessage.getText().toString();
                if (!messageContent.isEmpty()) {
                    messageList.add(new ChatMsgs(messageContent, true));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    editTextMessage.setText("");

                    // Simulate a bot response
                    messageList.add(new ChatMsgs(generateBotResponse(messageContent), false));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }
        });
    }

    private String generateBotResponse(String userMessage) {
        userMessage = userMessage.toLowerCase();

        if (userMessage.contains("hello") || (userMessage.contains("hi") || (userMessage.contains("hey")))){
            return "Hi there! How can I help you today?";
        }

        if (userMessage.contains("about drug") || userMessage.contains("about alcohol")|| userMessage.contains("about substance")){
            return "Check the Substance Abuse tab for more information!";
        }

        if (userMessage.contains("about mental health")) {
            return "Check the Mental Health tab for more information!";
        }

        if (userMessage.contains("about physical therapy") || userMessage.contains("about physical therapy")) {
            return "Check the Physiotherapy tab for more information!";
        }

        if (userMessage.contains("helpline")) {
            return "National Suicide Prevention Lifeline: 988\n\nAmerican Foundation for Suicide Prevention:\nText TALK to 741741\n\nNew Jersey Suicide Prevention Hopeline: 855-654-6735";
        }

        if (userMessage.contains("revive")) {
            return "Revive is an app to help track your days that you are clean and motivate you to go further in your journey! ";
        }

        else {
            return "I'm sorry, I don't understand that. Can you please rephrase?";
        }
    }


}
