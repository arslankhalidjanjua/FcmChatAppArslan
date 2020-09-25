package com.example.chatappfcm.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappfcm.Activities.Adapter.MessageAdapter;
import com.example.chatappfcm.Activities.Models.Chat;
import com.example.chatappfcm.Activities.Models.User;
import com.example.chatappfcm.Activities.Notify.APIService;
import com.example.chatappfcm.Activities.Notify.Client;
import com.example.chatappfcm.Activities.Notify.Data;
import com.example.chatappfcm.Activities.Notify.MyResponse;
import com.example.chatappfcm.Activities.Notify.Sender;
import com.example.chatappfcm.Activities.Notify.Token;
import com.example.chatappfcm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//https://stackoverflow.com/questions/61972598/reference-firebasedatabase-getinstance-getreferenceusers-childuserid

public class MessageActivity extends AppCompatActivity {
    //fcm notify
    boolean notify = false;

    TextView username;
    FirebaseUser fuser;
    Intent intent;
    DatabaseReference reference;
    ImageButton btnSend_message;   //click pa setText empty of trext send message
    EditText txtSend_message;
    String userid;
    //Message Adapter things here
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView2;

    //Api service Fcm
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //apisercice fcm
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        //adapter Message recyclr view
        recyclerView2 = findViewById(R.id.recyclr_view2);
        recyclerView2.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);  //https://stackoverflow.com/questions/44873389/android-only-setstackfromend-only-if-recycleview-list-is-larger-than-screen
        recyclerView2.setLayoutManager(linearLayoutManager);


        username = findViewById(R.id.username);

        // _message things send and message both things
        btnSend_message = findViewById(R.id.btn_send);
        txtSend_message = findViewById(R.id.text_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        //here after hashmap
        btnSend_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fcm true
                notify = true;

                String msg = txtSend_message.getText().toString();

                if (!TextUtils.isEmpty(msg)) {
                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You cant send an empty message", Toast.LENGTH_SHORT).show();
                }

                txtSend_message.setText("");
            }
        });

        //making child and add the userid to it using getstringextra
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getName());

                //Message ADapter ki readMessage inside fuser
                readMessages(fuser.getUid(), userid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(String sender, final String reciever, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("reciever", reciever);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

        //fcm trigger at sendMessage Arslan
        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //on data is changed send the notification
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(reciever, user.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //sendNotification function
    private void sendNotification(String reciever, final String username, final String message) {
        //value event Listner fcm
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            //fcm see data class pojo Arslan yaha say data send hoga 5 chezn
            //userid is of sender and fuser id meri apni hy reciever ki
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + " : " + message, "New Message", userid);

                    Sender sender = new Sender(data, token.getToken());

                    //fcm apiservice
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //Message Adapter method
    private void readMessages(final String myid, final String userid) {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        //refernce dot set value on clicklistner
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReciever().equals(myid) && chat.getSender().equals(userid) || chat.getReciever().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }
                    //passing values from messge activity to adapter
                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat);
                    recyclerView2.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}