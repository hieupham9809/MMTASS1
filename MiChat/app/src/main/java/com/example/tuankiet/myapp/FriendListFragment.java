package com.example.tuankiet.myapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import michat.localDB.MessageDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.MESSAGES;
import michat.model.MyListAdapter;
import michat.model.User;

public class FriendListFragment extends Fragment {
    private IntentFilter mIntentFilter;
    MyListAdapter adapter;
    ArrayList<User> users=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mIntentFilter=new IntentFilter(MESSAGES.RECEIVE_USERS_ONLINE);
        getContext().registerReceiver(mReceiver,mIntentFilter);
        return inflater.inflate(R.layout.activity_friend_list,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity v=getActivity();
        final ListView listView = v.findViewById(R.id.listView);
        adapter=new MyListAdapter(getContext(),users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                User country = (User) o;
                Intent intent=new Intent(getContext(),MessageListActivity.class);
                intent.putExtra("ownerId", "tuankiet");
                intent.putExtra("friendId",((User) o).getName());
                UserDatabaseHandler udh=new UserDatabaseHandler(getContext());
                if(udh.getUser(((User) o).getName())==null)
                {
                    //nguoi dung chua tung chat lan nao
                    MessageDatabaseHandler mdh=new MessageDatabaseHandler(getContext());
                    mdh.setTableName(((User) o).getName());
                    mdh.createTable();
                    ((User)o).setRole("friend");
                    udh.addUser((User)o);
                }
                startActivity(intent);
            }
        });
        callService();
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getContext(),intent.getAction(),Toast.LENGTH_SHORT).show();
            if (intent.getAction().equals(MESSAGES.RECEIVE_USERS_ONLINE)) {
                String output=intent.getStringExtra(MESSAGES.RECEIVE_USERS_ONLINE);
                try {
                    users.clear();
                    users.addAll(new ObjectMapper().readValue(output,new TypeReference<ArrayList<User>>(){}));
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    //
    //Doan code nay con bi loi Unregister exception.
    //
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getContext().unregisterReceiver(mReceiver);
        }
        catch (Exception e){

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getContext().unregisterReceiver(mReceiver);
        }
        catch (Exception e){

        }

    }

    @Override
    public void onResume() {
        super.onResume();
            getContext().registerReceiver(mReceiver,mIntentFilter);

    }
    void callService(){
        mIntentFilter=new IntentFilter();
        Intent serviceIntent=new Intent(getContext(),MainService.class);
        serviceIntent.setAction(MESSAGES.SEND_GET_USER_ONLINE);
        getContext().startService(serviceIntent);
    }
}
