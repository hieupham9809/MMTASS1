package com.example.tuankiet.myapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.tuankiet.myapp.service.MainService;

import michat.model.MESSAGE_CONSTANT;

public class FriendListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        callService();

    }

    private IntentFilter mIntentFilter;

    void callService(){
        mIntentFilter=new IntentFilter();
        Intent serviceIntent=new Intent(FriendListActivity.this,MainService.class);
        serviceIntent.putExtra(MESSAGE_CONSTANT.RECEIVE_USERS_ONLINE,"get");
        startService(serviceIntent);
    }
}
