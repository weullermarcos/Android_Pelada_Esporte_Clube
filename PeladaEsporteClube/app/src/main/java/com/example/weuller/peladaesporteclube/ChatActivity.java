package com.example.weuller.peladaesporteclube;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.weuller.peladaesporteclube.Adapters.ChatMessageRecyclerViewAdapter;
import com.example.weuller.peladaesporteclube.Models.ChatMessage;
import com.example.weuller.peladaesporteclube.Services.DialogService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Button btnSend;
    private EditText edtMessage;
    private ChatMessageRecyclerViewAdapter adpChatMessage;

    private String user;

    private List<ChatMessage> messageList = new ArrayList<>();
    private DialogService dialog = new DialogService();

    private RecyclerView rcvMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        edtMessage = (EditText) findViewById(R.id.chat_edtMessage);
        btnSend = (Button) findViewById(R.id.chat_btnSend);
        rcvMessages = (RecyclerView) findViewById(R.id.chat_rcvMessages);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("messages");
        mAuth = FirebaseAuth.getInstance();

        adpChatMessage = new ChatMessageRecyclerViewAdapter(this, messageList);
        rcvMessages.setAdapter(adpChatMessage);
        rcvMessages.setItemAnimator(new DefaultItemAnimator());
        rcvMessages.setLayoutManager(new LinearLayoutManager(this));


        if(mAuth.getCurrentUser().getDisplayName() != null) {

            user = mAuth.getCurrentUser().getDisplayName();

            //mAuth.getCurrentUser().getPhotoUrl();

            if(user != null && user.isEmpty())
                user = mAuth.getCurrentUser().getEmail();

        }

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

                    dialog.hideProgressDialog();
                    adpChatMessage.notifyDataSetChanged();

                    rcvMessages.scrollToPosition(messageList.size() - 1);
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

        //Enviar mensagens
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                String currentDateTime = sdf.format(c.getTime());

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setUser(user);
                chatMessage.setMessage(edtMessage.getText().toString().trim());
                chatMessage.setDate(currentDateTime);

                DatabaseReference newPostRef = myRef.push();
                newPostRef.setValue(chatMessage);

                edtMessage.setText("");
            }
        });
    }
}
