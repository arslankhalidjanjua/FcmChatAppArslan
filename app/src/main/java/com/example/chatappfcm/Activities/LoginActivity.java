package com.example.chatappfcm.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chatappfcm.Activities.Models.User;
import com.example.chatappfcm.Activities.Viewpager.DashBoardActivity;
import com.example.chatappfcm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    //email and password here
    EditText email_ed_login, password_ed_login;

    //firebase things
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        //if user is already logged in  ChatActivity
        if (auth.getCurrentUser() != null) {
        Intent i = new Intent(this, DashBoardActivity.class);
           startActivity(i);
        }

        email_ed_login = findViewById(R.id.email_ed_login);
        password_ed_login = findViewById(R.id.password_ed_login);

        reference = FirebaseDatabase.getInstance().getReference().child("users");


    }

    //user login here
    public void loginUser(View v) {
        String email = email_ed_login.getText().toString();
        String password = password_ed_login.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),"Logged In",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent( LoginActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Wrong email and password ",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    //user register here
    public void gotoRegister (View v){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


}