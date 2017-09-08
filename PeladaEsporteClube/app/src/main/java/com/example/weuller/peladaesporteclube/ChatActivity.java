package com.example.weuller.peladaesporteclube;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.weuller.peladaesporteclube.Models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private ListView lstMessages;
    private ArrayAdapter<ChatMessage> adpMessages;

    private List<ChatMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        lstMessages = (ListView) findViewById(R.id.chat_lstMessages);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("messages");
        mAuth = FirebaseAuth.getInstance();

        adpMessages = new ArrayAdapter<ChatMessage>(this, android.R.layout.simple_list_item_1);
        lstMessages.setAdapter(adpMessages);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    messageList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        ChatMessage message = postSnapshot.getValue(ChatMessage.class);

                        Log.i("LOG","Usuario = " + message.getUser());

                        messageList.add(message);
                    }

                    populateList();
                }

                catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());
            }
        });

    }

    private void populateList(){

        adpMessages.clear();

        for (ChatMessage localMessage: messageList) {

            adpMessages.add(localMessage);

        }
    }
}
