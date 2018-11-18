package com.example.tuankiet.myapp.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.GroupAdapter;
import com.example.tuankiet.myapp.R;
import com.example.tuankiet.myapp.UserAdapter;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import michat.GlobalData;

public class AddGroupFragment extends Fragment{
    GroupAdapter adapter;
    ArrayList<UserAdapter> users;
    private ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.activity_add_group,container,false);
    }

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_tab, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);
                    adapter.getFilter().filter(newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity v=getActivity();
        listView = v.findViewById(R.id.list);
        users=new ArrayList<>();
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
        adapter=new GroupAdapter(getContext(),users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                UserAdapter userAdapter=(UserAdapter)a.getItemAtPosition(position);
                Toast.makeText(getContext(),"Select user "+userAdapter.getName(),Toast.LENGTH_SHORT).show();
                if(userAdapter.getStatus().equals("Added"))
                    userAdapter.setStatus("");
                else userAdapter.setStatus("Added");
                ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        });
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_group_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Name for new Group:");
                final EditText input = new EditText(getContext());
                builder.setView(input);
                builder.setPositiveButton("Add New", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        ArrayList<String> members=new ArrayList<>();
                        for(UserAdapter i:users){
                            SugarUser usr=null;
                            if(i.getStatus().equals("Added"))
                            {
                                members.add(i.getName());
                                usr=SugarUser.loadUser(i.getName());
                            } else continue;
                            usr.save();
                        }
                        if(members.size()==0){

                            Toast.makeText(getContext(),"Create group failed: ",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SugarRoom room=new SugarRoom();
                        room.setMembers(new Gson().toJson(members));
                        room.setName("Group "+m_Text);
                        room.setOwner(GlobalData.getInstance().getOwner().getName());
                        room.setGroup(true);
                        room.save();

                        Intent intent = new Intent();
                        intent.setAction("onCreate");
                        intent.putExtra("id", room.getId());
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
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
        });
    }


}
