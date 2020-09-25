package com.example.chatappfcm.Activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappfcm.Activities.Adapter.UserRecyclerAdap;
import com.example.chatappfcm.Activities.Models.Chat;
import com.example.chatappfcm.Activities.Models.User;
import com.example.chatappfcm.Activities.Notify.Token;
import com.example.chatappfcm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    //Adpter and recyclr view.. chats fragemtns is to display you recently chatted with
    RecyclerView recyclerView;
    UserRecyclerAdap userRecyclerAdap;

    List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats2, container, false);

        recyclerView = view.findViewById(R.id.recyclr_view);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(fuser.getUid())) {
                        usersList.add(chat.getReciever());
                    }
                    if (chat.getReciever().equals(fuser.getUid())) {
                        usersList.add(chat.getSender());

                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //gettoken ins depreciated here fcm
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;

    }

    //udpate token fcm
    //https://stackoverflow.com/questions/56987101/onmessagereceivedremotemessage-remotemessage-firebase-chat-null-error-when-usi
    //updateToken
    public void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }


    //https://stackoverflow.com/questions/61268353/nullpointerexception-while-trying-to-receive-data-from-firebase-realtime-databas

    private void readChats() {
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (String id : usersList) {
                        assert user != null;
                        if (user.getUid().equals(id)) {
                            if (mUsers.size() != 0) {
                                for (User user1 : mUsers) {
                                    if (!user.getUid().equals(user1.getUid())) {
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userRecyclerAdap = new UserRecyclerAdap(getContext(), mUsers);
                recyclerView.setAdapter(userRecyclerAdap);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}