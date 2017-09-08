package com.example.weuller.peladaesporteclube;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Models.ChatMessage;
import com.example.weuller.peladaesporteclube.Services.DialogService;
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
    Button btnSend;
    private ListView lstMessages;
    private ArrayAdapter<ChatMessage> adpMessages;

    private List<ChatMessage> messageList = new ArrayList<>();
    private DialogService dialog = new DialogService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        lstMessages = (ListView) findViewById(R.id.chat_lstMessages);
        btnSend = (Button) findViewById(R.id.chat_btnSend);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("messages");
        mAuth = FirebaseAuth.getInstance();

        adpMessages = new ArrayAdapter<ChatMessage>(this, android.R.layout.simple_list_item_1);
        lstMessages.setAdapter(adpMessages);

        dialog.showProgressDialog("Carregando mensagens...", "Aguarde", ChatActivity.this);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    messageList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        ChatMessage message = postSnapshot.getValue(ChatMessage.class);

                        messageList.add(message);
                    }

                    populateList();
                }

                catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w("LOG", "Failed to read value.", error.toException());

                dialog.hideProgressDialog();
                Toast.makeText(ChatActivity.this, "Erro ao carregar mensagens. verifique a sua conexão com a internet.", Toast.LENGTH_SHORT).show();

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setUser("Waldiney");
                chatMessage.setMessage("Bora bora galera.. jogar futebol");
                chatMessage.setDate("08/09/2017 - 20:50");

                DatabaseReference newPostRef = myRef.push();
                newPostRef.setValue(chatMessage);

            }
        });
    }

    private void populateList(){

        adpMessages.clear();

        for (ChatMessage localMessage: messageList) {

            adpMessages.add(localMessage);
        }

        dialog.hideProgressDialog();
    }
}
