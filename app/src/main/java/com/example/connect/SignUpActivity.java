package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private Button Register;
    private Button AlreadyHaveAccount;
    private EditText Email;
    private EditText Password;
    private FirebaseAuth mAuth;  //Firebase authentication variable
    private ProgressDialog Loading;  //loading bar during registration
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        Initialization();

        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendToLoginActivity();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterAccount();
            }
        });
    }

    public void RegisterAccount()
    {
        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(SignUpActivity.this,"Enter email address",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(SignUpActivity.this,"Enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Loading.setTitle("Creating Account");
            Loading.setMessage("PLease wait while we create account for you");
            Loading.setCanceledOnTouchOutside(true);
            Loading.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        String CurrentUserID = mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(CurrentUserID).setValue("");
                        SendToMainActivity();
                        Toast.makeText(SignUpActivity.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String error = task.getException().toString();
                        Toast.makeText(SignUpActivity.this,"Error : "+error,Toast.LENGTH_SHORT).show();
                    }
                    Loading.dismiss();

                }
            });
        }
    }

    //Initialization
    public void Initialization()
    {
        AlreadyHaveAccount = findViewById(R.id.already_have_account);
        Email = findViewById(R.id.SignUpEmail);
        Password = findViewById(R.id.SignUpPassword);
        Register = findViewById(R.id.register);
        Loading = new ProgressDialog(this);
    }
    public void SendToLoginActivity()
    {
        Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
    public void SendToMainActivity()
    {
        Intent i = new Intent(SignUpActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

}