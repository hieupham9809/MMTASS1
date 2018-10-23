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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import michat.GlobalData;
import michat.localDB.MessageDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.DialogList;
import michat.model.MESSAGES;
import michat.model.Message;
import michat.model.User;

public class DialogListFragment extends Fragment {
    private IntentFilter mIntentFilter;
    DialogsList dialogList;
    DialogsListAdapter adapter;
    UserDatabaseHandler udh;
    MessageDatabaseHandler mdh;
    List<IDialog> diaLst = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mIntentFilter=new IntentFilter(MESSAGES.RECEIVE_USERS_ONLINE);
        getContext().registerReceiver(mReceiver,mIntentFilter);
        return inflater.inflate(R.layout.activity_dialog_list,container,false);
    }
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getContext(),intent.getAction(),Toast.LENGTH_SHORT).show();
            if (intent.getAction().equals(MESSAGES.RECEIVE_MSG)) {
                //onNewMessage();
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callService();
        udh=new UserDatabaseHandler(getContext());
        mdh=new MessageDatabaseHandler(getContext());
        //setting dialog list
        Activity v=getActivity();
        dialogList = v.findViewById(R.id.dialogsList);
        adapter = new DialogsListAdapter<>(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        });
        dialogList.setAdapter(adapter);
        List<User> usrLst = new ArrayList<>();
        usrLst.addAll(udh.getAllUser());


        for (User i : usrLst) {
            if (!i.getRole().equals("owner")) {
                mdh.setTableName(i.getName());
                Log.d("role",i.getRole());
                mdh.setTableName(i.getName());
                Message lastMsg = mdh.getLastMessage();
                int unRead = mdh.getUnreadMessage().size();
                ArrayList<IUser> users = new ArrayList<>();
                for (String username : mdh.getAllFriendsChatWith()) {
                    users.add(udh.getUser(username));
                }

                diaLst.add(new DialogList(i.getName(), i.getAvatar(), i.getFullName(), users, lastMsg, unRead));
            }
        }
        adapter.addItems(diaLst);

        //EVENT FOR DIALOG LIST
        adapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(IDialog dialog) {
                Intent serviceIntent=new Intent(getContext(),MainService.class);
                User friend=udh.getUser(dialog.getId());
                serviceIntent.setAction(MESSAGES.SEND_SYN);
                serviceIntent.putExtra("user",friend.getName());
                getContext().startService(serviceIntent);
                Intent intent=new Intent(getContext(),MessageListActivity.class);
                intent.putExtra("friendId",dialog.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mReceiver,mIntentFilter);
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
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getContext().unregisterReceiver(mReceiver);
        }
        catch (Exception e){

        }
    }
    void callService(){
        mIntentFilter=new IntentFilter();
        Intent serviceIntent=new Intent(getContext(),MainService.class);
        serviceIntent.setAction(MESSAGES.SEND_GET_USER_ONLINE);
        getContext().startService(serviceIntent);
    }
    private void onNewMessage(IMessage message){
        if(!adapter.updateDialogWithMessage(message.getUser().getName(),message)){
            diaLst.add(new DialogList(message.getUser().getName(),message.getUser().getAvatar(),((User)message.getUser()).getFullName(),
                    new ArrayList<IUser>(){{add(GlobalData.getInstance().getOwner());add(message.getUser());}},message,1));
            adapter.notifyDataSetChanged();
        }
    }
}
