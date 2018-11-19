package com.example.tuankiet.myapp.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.GroupAdapter;
import com.example.tuankiet.myapp.TabActivity;
import com.example.tuankiet.myapp.UserAdapter;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.MessageListActivity;
import com.example.tuankiet.myapp.R;
import com.example.tuankiet.myapp.service.Clients;
import com.example.tuankiet.myapp.service.ComingMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import michat.GlobalData;

import com.MyListAdapter;
import com.google.gson.reflect.TypeToken;

import michat.model.User;
import michat.observer.Observer;
public class FriendListFragment extends Fragment {
    MyListAdapter adapter;
    List<UserAdapter> users=new ArrayList<>();
    ArrayList<String> online=new ArrayList<>();
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
        ComingMessage cm=ComingMessage.fromString(Clients.getInstance().getThreadServer().sendGetOnline());
        online=new Gson().fromJson(cm.getMsg(),new TypeToken<ArrayList<String>>(){}.getType());
        try {
            List<SugarUser> lst=SugarUser.getAllUser();
            Log.d("SIZE", String.valueOf(lst.size()));
            for(SugarUser i:lst){
                if(!i.getName().equals(GlobalData.getInstance().getOwner().getName())) {
                    UserAdapter userAdapter=i.toUserAdapter();
                    users.add(userAdapter);
                    if(online.contains(i.getName())){
                        userAdapter.setStatus("Online");
                    }
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
                UserAdapter iuser = (UserAdapter) o;
                Intent intent=new Intent(getContext(),MessageListActivity.class);
                if(SugarUser.findByName(iuser.getName())==null)
                {
                    SugarUser.loadUser(iuser.getName()).save();
                }

                SugarRoom room=SugarRoom.findByName(iuser.getName());
                if(room==null) {
                    room=new SugarRoom();
                    room.setName(iuser.getDisplayName());
                    HashSet<String> set=new HashSet<>();
                    set.add(iuser.getName());
                    room.setMembers(new Gson().toJson(set));
                    room.setGroup(false);
                    room.save();
                    Observer.getInstance().notifyonAddDialog(room);
                }
                intent.putExtra("roomId",String.valueOf(room.getId()));
                startActivity(intent);
            }
        });
    }

}
