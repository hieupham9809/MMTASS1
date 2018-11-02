package com.example.tuankiet.myapp.fragment;

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

import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.service.MainService;
import com.example.tuankiet.myapp.MessageListActivity;
import com.example.tuankiet.myapp.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import michat.GlobalData;
import michat.localDB.MessageDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.MESSAGE;
import michat.model.MyListAdapter;
import michat.model.User;
import michat.observer.Observer;
public class FriendListFragment extends Fragment {
    MyListAdapter adapter;
    List<SugarUser> users=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.activity_friend_list,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity v=getActivity();
        final ListView listView = v.findViewById(R.id.listView);

        try {
            users=SugarUser.getAllUser();
            for(SugarUser i:users){
                if(i.getName().equals(GlobalData.getInstance().getOwner().getName())){
                    users.remove(i);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter=new MyListAdapter(getContext(),users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                SugarUser iuser = (SugarUser) o;
                Intent intent=new Intent(getContext(),MessageListActivity.class);
                if(SugarUser.findByName(iuser.getName())==null)
                {
                    iuser.save();
                }

                SugarRoom room=SugarRoom.findByName(iuser.getName());
                if(room==null) {
                    room=new SugarRoom();
                    room.setName(iuser.getName());
                    HashSet<String> set=new HashSet<>();
                    set.add(iuser.getName());
                    room.setMembers(new Gson().toJson(set));
                    room.save();
                    Observer.getInstance().notifyonAddDialog(room);

                }
                intent.putExtra("roomId",String.valueOf(room.getId()));
                startActivity(intent);
            }
        });
    }


}
