package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final String TAG = "ProfileActivity";
    private EditText Name;
    private EditText AboutMe;
    private Button Update;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializingFields();
        try
        {
            getSupportActionBar().setTitle("Profile");
        }
        catch (NullPointerException exception)
        {
            Log.w(TAG,"Profile Toolbar does not exist : "+exception);
        }

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateProfile();
            }
        });

        getUserInfo();

    }
    public void getUserInfo()
    {
        RootRef.child("Users").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if((snapshot.exists()) && (snapshot.hasChild("Name")) && (snapshot.hasChild("About Me")))
                {
                    String getName = snapshot.child("Name").getValue().toString();
                    String getStatus = snapshot.child("About Me").getValue().toString();

                    Name.setText(getName);
                    AboutMe.setText(getStatus);
                }
                if((snapshot.exists()) && (snapshot.hasChild("Name")))
                {
                    String getName = snapshot.child("Name").getValue().toString();
                    Name.setText(getName);
                }
                else
                {
                    Toast.makeText(ProfileActivity.this,"Please update your profile",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void UpdateProfile()
    {
        String UserName = Name.getText().toString();
        String UserAboutMe = AboutMe.getText().toString();

        if(TextUtils.isEmpty(UserName))
        {
            Toast.makeText(this,"Enter Name",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(UserAboutMe))
        {
            Toast.makeText(this,"Enter Status",Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String , String> ProfileMap = new HashMap<>();
            ProfileMap.put("Name" , UserName);
            ProfileMap.put("About Me" , UserAboutMe);
            RootRef.child("Users").child(CurrentUserID).setValue(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        SendToMainActivity();
                        Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String msg = task.getException().toString();
                        Toast.makeText(getApplicationContext(),"Error"+msg,Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void InitializingFields()
    {
        Name = findViewById(R.id.name);
        AboutMe = findViewById(R.id.about_me);
        Update = findViewById(R.id.update);
    }
    public void SendToMainActivity()
    {
        Intent i = new Intent(ProfileActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}