package com.example.connect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{
    //variable declaration
    private FirebaseUser CurrentUser;
    private String CurrentUserID;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        InitializingFields();

        setSupportActionBar(toolbar);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //Exception to check for tabs on home screen
        try
        {
            tabLayout.getTabAt(0).setText("CHATS");
            tabLayout.getTabAt(1).setText("GROUPS");
        }
        catch (NullPointerException exception)
        {
             Log.w(TAG,"couldn't load the tabs: "+exception);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        if(CurrentUser == null)
        {
            SendToLoginActivity();
        }
        else
        {
            CheckUserProfile();
        }
    }
    //check user profile
    //Check User Profile created or not
    private void CheckUserProfile()
    {
        RootRef.child("Users").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if((snapshot.child("Name").exists()))
                {
                   // Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent i = new Intent(MainActivity.this,ProfileActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //Menu for home screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_settings_menu,menu);
        return true;
    }
    //items in home screen menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.main_settings_profile:
                SendToProfileSettings();
                return true;
            case R.id.main_settings_logout:
                SendToLoginActivity();
                return true;
            case R.id.main_settings_create_group:
                RequestNewGroup();
                return true;
        }
        return true;
    }
    public void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.GroupChatDialogBox);
        builder.setTitle("Group Name");

        EditText GroupName = new EditText(MainActivity.this);
        builder.setView(GroupName);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String GrpName = GroupName.getText().toString();
                if(TextUtils.isEmpty(GrpName))
                {
                    Toast.makeText(getApplicationContext(),"Enter a valid Group Name",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(GrpName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        builder.show();
    }

    public void CreateNewGroup(String GroupName)
    {
        RootRef.child("Groups").child(GroupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,GroupName+" created successfully",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Initializing every field on main screen
    public void InitializingFields()
    {
        toolbar=findViewById(R.id.home_toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.home_view_pager);
    }
    //function to send user to profile activity
    public void SendToProfileSettings()
    {
        Intent i = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(i);
    }
    public void SendToLoginActivity()
    {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}