package com.example.tuankiet.myapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import michat.GlobalData;
import com.MyListAdapter;

public class AddGroupActivity extends AppCompatActivity {

    List<UserAdapter> users=new ArrayList<>();
    ArrayList<String> members=new ArrayList<>();
    private MyListAdapter adapter;
    private ListView listView;
    SugarRoom room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        long roomId=intent.getLongExtra("roomId",-1);
        Log.d("TAG", String.valueOf(roomId));
        room=SugarRoom.findById(SugarRoom.class,roomId);
        setContentView(R.layout.activity_add_member);
        listView=findViewById(R.id.list);
        try {
            List<SugarUser> lst= SugarUser.getAllUser();
            for(SugarUser i:lst){
                if(!i.getName().equals(GlobalData.getInstance().getOwner().getName())){
                    UserAdapter userAdapter=i.toUserAdapter();
                    userAdapter.setStatus("");
                    users.add(userAdapter);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter=new MyListAdapter(getApplicationContext(),users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                UserAdapter userAdapter=(UserAdapter)a.getItemAtPosition(position);
                if(userAdapter.getStatus().equals("Added"))
                {
                    userAdapter.setStatus("");
                    members.remove(userAdapter.getName());
                }
                else {
                    userAdapter.setStatus("Added");
                    members.add(userAdapter.getName());
                }
                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(String i: members){
                    room.addMember(i);
                }
                finish();
            }
        });
    }
    }

