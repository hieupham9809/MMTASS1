package com.example.tuankiet.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.List;

import michat.GlobalData;
import michat.localDB.MessageDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.DialogList;
import michat.model.MESSAGES;
import michat.model.Message;
import michat.model.User;

public class DialogListActivity extends AppCompatActivity {

    private IntentFilter mIntentFilter;
    DialogsList dialogList;
    DialogsListAdapter adapter;
    UserDatabaseHandler udh;
    MessageDatabaseHandler mdh;
    @Override
    protected void onStart() {
        super.onStart();
        udh=new UserDatabaseHandler(this);
        mdh=new MessageDatabaseHandler(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_list);



        mIntentFilter=new IntentFilter(MESSAGES.RECEIVE_MSG);
        callService();

        //setting dialog list
        dialogList = findViewById(R.id.dialogsList);
        adapter = new DialogsListAdapter<>(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        });
        dialogList.setAdapter(adapter);
        //

        renderDialog();
        //Load Database


    }
        private BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(MESSAGES.RECEIVE_MSG)) {
                    renderDialog();
                }
            }
        };

    void callService(){
        mIntentFilter=new IntentFilter();
        Intent serviceIntent=new Intent(DialogListActivity.this,MainService.class);
        startService(serviceIntent);
    }
    void renderDialog(){

        List<User> usrLst = udh.getAllUser();
        List<IDialog> diaLst = new ArrayList<>();

        for (User i : usrLst) {
            if (!i.getRole().equals("owner")) {
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
        adapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(IDialog dialog) {
                Intent serviceIntent=new Intent(DialogListActivity.this,MainService.class);
                User friend=udh.getUserWithId(dialog.getId());
                serviceIntent.setAction(MESSAGES.SEND_CONNECT);
                serviceIntent.putExtra("connect",friend.getName());
                startService(serviceIntent);
                Intent intent=new Intent(DialogListActivity.this,MessageListActivity.class);
                intent.putExtra("ownerId", GlobalData.getInstance().getOwner().getName());
                intent.putExtra("friendId",dialog.getId());
                startActivity(intent);
            }
        });
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
}
/*
toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        toolbar.setTitle("Shop");
        private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_shop:
                    toolbar.setTitle("Shop");
                    return true;
                case R.id.navigation_gifts:
                    toolbar.setTitle("My Gifts");
                    return true;
                case R.id.navigation_cart:
                    toolbar.setTitle("Cart");
                    return true;
                case R.id.navigation_profile:
                    toolbar.setTitle("Profile");
                    return true;
            }
            return false;
        }
    };
}
 */
