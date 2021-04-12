package com.example.connect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.Edits;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton SendButton;
    private EditText GroupMessage;
    private ScrollView scrollView;
    private TextView GroupChatScreen;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,GroupNameRef,GroupMessageKeyRef;
    private String GroupName , CurrentUserID , CurrentUserName , CurrentDate , CurrentTime ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        GroupName = getIntent().getExtras().get("SelectedGroupName").toString();

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(GroupName);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupName);

        Initializing();

        GetUserInfoFromDatabase();

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendMessageToDatabase();

                GroupMessage.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if(snapshot.exists())
                {
                    ShowMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if(snapshot.exists())
                {
                    ShowMessages(snapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Initializing()
    {
        SendButton = findViewById(R.id.send_message);
        GroupMessage = findViewById(R.id.msg_text_box);
        scrollView = findViewById(R.id.group_chat_scrollview);
        GroupChatScreen = findViewById(R.id.group_chat_display_view);
    }

    public void GetUserInfoFromDatabase()
    {
        UsersRef.child(CurrentUserID ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    CurrentUserName = snapshot.child("Name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void SendMessageToDatabase()
    {
        String message = GroupMessage.getText().toString();
        String messageKey = GroupNameRef.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this,"Enter message",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar CalendarForDate = Calendar.getInstance();
            SimpleDateFormat CurrentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            CurrentDate = CurrentDateFormat.format(CalendarForDate.getTime());

            Calendar CalendarForTime = Calendar.getInstance();
            SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("hh:mm a");
            CurrentTime = CurrentTimeFormat.format(CalendarForTime.getTime());

            HashMap<String, Object> GroupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(GroupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> MessageInfo = new HashMap<>();
            MessageInfo.put("Name", CurrentUserName);
            MessageInfo.put("Message", message);
            MessageInfo.put("Date", CurrentDate);
            MessageInfo.put("Time", CurrentTime);
            GroupMessageKeyRef.updateChildren(MessageInfo);
        }

    }
    public void ShowMessages(DataSnapshot snapshot)
    {
        Iterator iterator = snapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String Date = (String) ((DataSnapshot)iterator.next()).getValue();
            String Message = (String) ((DataSnapshot)iterator.next()).getValue();
            String Name = (String) ((DataSnapshot)iterator.next()).getValue();
            String Time = (String) ((DataSnapshot)iterator.next()).getValue();

            GroupChatScreen.append(Name + " : \n" + Message + "\n" + Date + "  " + Time + "\n\n\n");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}