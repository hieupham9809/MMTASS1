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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import michat.model.MESSAGES;
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

    @Override
    protected void onStart() {
        super.onStart();
        mIntentFilter=new IntentFilter(MESSAGES.RECEIVE_USERS_ONLINE);
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause(){
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(FriendListActivity.this,intent.getAction(),Toast.LENGTH_SHORT).show();
            if (intent.getAction().equals(MESSAGES.RECEIVE_USERS_ONLINE)) {
                String output=intent.getStringExtra(MESSAGES.RECEIVE_USERS_ONLINE);

                try {
                    ArrayList<User> users=new ObjectMapper().readValue(output,new TypeReference<ArrayList<User>>(){});


                    final ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(new MyListAdapter(FriendListActivity.this, users));

                    // Khi người dùng click vào các ListItem
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                            Object o = listView.getItemAtPosition(position);
                            User country = (User) o;
                            Intent intent=new Intent(FriendListActivity.this,MessageListActivity.class);
                            intent.putExtra("ownerId", "tuankiet");
                            intent.putExtra("friendId",((User) o).getId());
                            startActivity(intent);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    void callService(){
        mIntentFilter=new IntentFilter();
        Intent serviceIntent=new Intent(FriendListActivity.this,MainService.class);
        serviceIntent.putExtra(MESSAGES.RECEIVE_USERS_ONLINE,"get");
        startService(serviceIntent);
    }
}
