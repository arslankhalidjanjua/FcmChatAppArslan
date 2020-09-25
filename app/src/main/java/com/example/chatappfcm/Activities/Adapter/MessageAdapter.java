package com.example.chatappfcm.Activities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappfcm.Activities.Models.Chat;
import com.example.chatappfcm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    //message adapter things here
    public static final int Msg_Type_Left = 0;
    public static final int Msg_Type_Right = 1;

    //firebase message chat fuser here
    FirebaseUser fuser;


    private Context context;

    //chat here
    private List<Chat> mChat;

    public MessageAdapter(Context context, List<Chat> mChat) {
        this.context = context;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //mesage realted stuff here for inflaloter

        if (viewType == Msg_Type_Right) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);

        }

        }

        //bind view is for chat things here
        @Override
        public void onBindViewHolder (@NonNull MessageAdapter.ViewHolder holder,int i){
            final Chat chat=mChat.get(i); //get position
            holder.show_message.setText(chat.getMessage()); //get message text here


            //holder.name.setText(userName);

        }


        @Override
        public int getItemCount () {
            return mChat.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView show_message;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                show_message = itemView.findViewById(R.id.show_message);
            }
        }


        //on get item view type is for adpter message / chat
        @Override
        public int getItemViewType ( int position){

            fuser = FirebaseAuth.getInstance().getCurrentUser();
            if (mChat.get(position).getSender().equals(fuser.getUid())) {
                return Msg_Type_Right;
            } else {
                return Msg_Type_Left;
            }

        }
    }
