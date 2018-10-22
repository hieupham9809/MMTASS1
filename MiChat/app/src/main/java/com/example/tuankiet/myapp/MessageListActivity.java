package com.example.tuankiet.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Date;
import java.util.List;

import michat.GlobalData;
import michat.localDB.MessageCountDatabaseHandler;
import michat.localDB.MessageDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.MESSAGE;
import michat.model.Message;
import michat.model.Msg;
import michat.model.Packet;
import michat.model.User;

public class MessageListActivity extends AppCompatActivity {

    public static final String mBroadcastAction = "STRING_BROADCAST_ACTION";
    public static final String mInitAction="STRING_BROADCAST_ACTION";
    private IntentFilter mIntentFilter;
    private Toolbar mTopToolbar;
    private MessageDatabaseHandler mdh;
    private  UserDatabaseHandler udh;
    private MessageCountDatabaseHandler mcdh;
    private Uri imageUri=null;
    User friend=null;
    private User MsgLstOwner;
    MessagesListAdapter<Message> adapter;
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
    MessageInput input;
    MessagesList msgList;
    int id=0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.title_bar_with_button,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        mdh=new MessageDatabaseHandler(MessageListActivity.this);
        udh=new UserDatabaseHandler(MessageListActivity.this);
        mcdh=new MessageCountDatabaseHandler(MessageListActivity.this);
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(MESSAGE.RECEIVE_MSG);
        Intent intent=getIntent();;
        String friendId=intent.getStringExtra("friendId");
        input=findViewById(R.id.input);
        msgList=findViewById(R.id.messagesList);
        //open chat
        friend=udh.getUser(friendId);
        MsgLstOwner=friend;

        //mTopToolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(mTopToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"+friend.getFullName()+"</font>"));
        User Ifriend=friend;

        adapter=new MessagesListAdapter<>(GlobalData.getInstance().getOwner().getId(), new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        });
        msgList.setAdapter(adapter);
        mdh.setTableName(friend.getName());
        List<Message> lstMsg=mdh.getAllMessage();
        for(Message i:lstMsg) {
            if(i.getReadAt()==null||i.getReadAt().equals("")){
                i.setReadAt(new Date());
                mdh.updateMessage(i);
            }
            adapter.addToStart(i,false);
        }
        input.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

            }
        });
        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                Message msg=new Message(GlobalData.getInstance().getOwner().getName()+mcdh.getCount(),input.toString(),GlobalData.getInstance().getOwner(),new Date(),null,new Date());
                mcdh.addCount();
                SendMessage(msg);
                return true;
            }
        });

    }
    void SendMessage(Message msg){

        try {
            MessageDatabaseHandler mdh=new MessageDatabaseHandler(MessageListActivity.this);
            mdh.setTableName(friend.getName());
            mdh.addMessage(msg);
            Msg mmsg=new Msg();//msg.getId(),msg.getUser().getName(),String.valueOf(msg.getCreatedAt().getTime()),msg.getText());
            mmsg.setId(msg.getId());
            mmsg.setOwner(msg.getUser().getName());
            mmsg.setCreatedAt(String.valueOf(msg.getCreatedAt().getTime()));
            mmsg.setText(msg.getText());
            Packet p=new Packet();
            p.setType("msg");
            p.setData(mmsg);
            ObjectMapper om=new ObjectMapper();
            String output=om.writeValueAsString(p);
            //Client.sendString(ds,output, InetAddress,Port);
            Intent serviceIntent=new Intent(MessageListActivity.this,MainService.class);
            serviceIntent.setAction(MESSAGE.SEND_MESSAGE);
            serviceIntent.putExtra("msg", output);
            serviceIntent.putExtra("owner",friend.getName());
            startService(serviceIntent);


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        adapter.addToStart(msg,true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    imageUri = imageReturnedIntent.getData();
                    Message msg=new Message(GlobalData.getInstance().getOwner().getName()+mcdh.getCount(),"",GlobalData.getInstance().getOwner(),new Date(),null,new Date());
                    mcdh.addCount();
                    msg.setImageUrl(imageUri.toString());
                    SendMessage(msg);
                    Log.d("image url",imageUri.toString());
                }
                break;
        }
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE.RECEIVE_MSG)) {
                mdh.setTableName(intent.getStringExtra("tableName"));
                Message msg = mdh.getMessage(intent.getStringExtra("id"));
                Log.d("GEt msg id of",intent.getStringExtra("id"));
                String owner = intent.getStringExtra("tableName");
                Log.d("Receive Msg",MsgLstOwner.getName()+msg.getUser().getName());
                if (owner.equals(MsgLstOwner.getName())) {
                    msg.setReadAt(new Date());
                    mdh.updateMessage(msg);
                    adapter.addToStart(msg, true);
                } else {
                    //push notification
//                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                            .setSmallIcon(R.drawable.messenger)
//                            .setContentTitle("Tin nhan tu " +msg.getUser().getFullName())
//                            .setContentText(msg.getText())
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                }

            }
        }
    };

}
