package com.example.tuankiet.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tuankiet.myapp.service.MainService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.IOException;
import java.util.ArrayList;

import michat.model.MESSAGE;
import michat.model.MyListAdapter;
import michat.model.User;

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
        serviceIntent.putExtra(MESSAGE.RECEIVE_USERS_ONLINE,"get");
        startService(serviceIntent);
    }
}
