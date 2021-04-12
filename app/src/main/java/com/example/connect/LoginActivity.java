package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //variable declaration
    private FirebaseAuth mAuth;
    private Button SignUp;
    private EditText Email;
    private EditText Password;
    private Button Login;
    private ProgressDialog Loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Initialization();
        //send to SignUp activity
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendToSignUpActivity();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginAccount();
            }
        });

    }

    public void LoginAccount()
    {
        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this,"Enter email address",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this,"Enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Loading.setTitle("Sign in");
            Loading.setMessage("Signing in");
            Loading.setCanceledOnTouchOutside(true);
            Loading.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        SendToMainActivity();
                    }
                    else
                    {
                        String error = task.getException().toString();
                        Toast.makeText(LoginActivity.this,"Error : "+error,Toast.LENGTH_SHORT).show();
                    }
                    Loading.dismiss();

                }
            });
        }
    }

    //Initializing fields
    public void Initialization()
    {
        SignUp = findViewById(R.id.signup);
        Email = findViewById(R.id.LoginEmail);
        Password = findViewById(R.id.LoginPassword);
        Login = findViewById(R.id.login);
        Loading = new ProgressDialog(this);
    }
    //send user to home screen
    public void SendToMainActivity()
    {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
    //send user to sign up screen
    public void SendToSignUpActivity()
    {
        Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(i);
    }
}