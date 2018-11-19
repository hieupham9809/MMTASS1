package com.example.tuankiet.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.GroupAdapter;
import com.TrimAdapter;
import com.example.tuankiet.myapp.HttpRequest.FileUpload;
import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.service.Clients;
import com.example.tuankiet.myapp.service.ComingMessage;
import com.example.tuankiet.myapp.service.ThreadAccept;
import com.example.tuankiet.myapp.voicecall.MakeCallActivity;
import com.example.tuankiet.myapp.voicecall.ReceiveCallActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import michat.GlobalData;
import michat.model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageListActivity extends AppCompatActivity{

    SugarRoom sugarRoom;
    List<SugarUser> ownerRoom=new ArrayList<>();
    MessagesListAdapter<Message> adapter;
    MessageInput input;
    MessagesList msgList;
    private Uri imageUri;
    
    public final static String EXTRA_CONTACT = "michat.CallOut.CONTACT";
    public final static String EXTRA_IP = "michat.CallOut.IP";
    public final static String EXTRA_DISPLAYNAME = "michat.CallOut.DISPLAYNAME";
    private NotificationManager notificationManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    ArrayList<String> members;
    private TrimAdapter trimAdapter;
    private List<SugarMessage> trimData;
    private ListView trim;

    private void initDrawerLayout(){
        
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
    }
    private void initAdapter(){
        adapter=new MessagesListAdapter<>(String.valueOf(GlobalData.getInstance().getOwner().getId()), new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);

            }
        });
        adapter.setOnMessageClickListener(new MessagesListAdapter.OnMessageClickListener<Message>() {
            @Override
            public void onMessageClick(Message message) {

            }
        });
        adapter.setOnMessageLongClickListener(new MessagesListAdapter.OnMessageLongClickListener<Message>() {
            @Override
            public void onMessageLongClick(Message message) {
                showDeleteDialog(message.getId());
            }
        });
    }
    void initActionBar(){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        if(sugarRoom.isGroup())
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"+sugarRoom.getName()+"</font>"));
        else actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"+SugarUser.findByName(members.get(0)).getDisplayName()));
    }
    void loadMessage(){
        msgList.setAdapter(adapter);
        Iterator<SugarMessage> lstMsg=SugarMessage.findAsIterator(SugarMessage.class,"ROOM_ID=?",String.valueOf(sugarRoom.getId()));
        while(lstMsg.hasNext()){
            SugarMessage msg=lstMsg.next();
            Message mess=msg.toMessage();
            if(msg.getReadAt()==null){
                msg.setReadAt(new Date());
                msg.save();
            }
            adapter.addToStart(mess,true);
        }
        input.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("*/*");
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, 123);
            }
        });
        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                SugarMessage sugarMessage=new SugarMessage();
                sugarMessage.setText(input.toString());
                sugarMessage.setCreatedAt(new Date());
                sugarMessage.setOwner(GlobalData.getInstance().getOwner().getId());
                sugarMessage.setReadAt(new Date());
                sugarMessage.setRoomId(sugarRoom.getId());
                sugarMessage.save();
                adapter.addToStart(sugarMessage.toMessage(),true);
                SendMessage(sugarMessage,null);
                return true;
            }

        });
    }
    void initComponent(){
        drawerLayout = findViewById(R.id.activity_tab_drawer);
        input=findViewById(R.id.input);
        msgList=findViewById(R.id.messagesList);
        CircleImageView imageView=findViewById(R.id.avatar_user);
        listView = findViewById(R.id.listView);
        trim=findViewById(R.id.trimMessage);
        if(members.size()==1)
            Picasso.get().load(SugarUser.findByName(members.get(0)).getAvatar()).into(imageView);
        else Picasso.get().load(GlobalData.getInstance().getOwner().getAvatar()).into(imageView);
    }
    void initTrimListView(){
        
        trimData=SugarMessage.getTrimMessage(sugarRoom.getId());
        trimAdapter=new TrimAdapter(this,trimData);
        trim.setAdapter(trimAdapter);
    }
    void initGroupListView(){
        users=new ArrayList<>();
        for(String i:members){
            users.add(SugarUser.findByName(i).toUserAdapter());
        }
        listadapter = new GroupAdapter(this, users);
        listView.setAdapter(listadapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                UserAdapter usr=(UserAdapter)parent.getItemAtPosition(position);
                showDeleteDialog(usr);
                return false;
            }
        });
        ImageView imageView1=findViewById(R.id.add);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sugarRoom.isGroup()) return;
                Intent intent1=new Intent(MessageListActivity.this,AddGroupActivity.class);
                intent1.putExtra("roomId",sugarRoom.getId());
                startActivity(intent1);
            }
        });
    }
    public void showDeleteDialog(String id){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        builder.setMessage("Are you want to delete message?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SugarMessage mess=SugarMessage.findById(SugarMessage.class,Long.parseLong(id));
                mess.delete();
                adapter.deleteById(String.valueOf(id));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Trim Message", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                String[] ids=sugarRoom.deleteAllMessage();
//                adapter.deleteByIds(ids);
                SugarMessage mess=SugarMessage.findById(SugarMessage.class,Long.parseLong(id));
                mess.setTrim(!mess.isTrim());
                mess.save();
                if(mess.isTrim())
                trimData.add(mess);
                else trimData.remove(mess);
                trimAdapter.notifyDataSetChanged();

            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        
        Intent intent=getIntent();;
        sugarRoom=SugarRoom.findById(SugarRoom.class,Integer.parseInt(intent.getStringExtra("roomId")));
        members=new Gson().fromJson(sugarRoom.getMembers(),new TypeToken<ArrayList<String>>(){}.getType());
        for(String i: members)
            ownerRoom.add(SugarUser.findByName(i));
        initComponent();
        initDrawerLayout();
        initActionBar();
        initAdapter();
        loadMessage();
        initTrimListView();
        initGroupListView();
        /////////////
    }
    GroupAdapter listadapter;
    public void showDeleteDialog(UserAdapter userAdapter){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        builder.setMessage("Are you want to remove member?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sugarRoom.removeMember(userAdapter.getName());
                users.remove(userAdapter);
                listadapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    ListView listView;
    ArrayList<UserAdapter> users;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    void SendMessage(SugarMessage msg, String imageURL){
        ComingMessage cm = new ComingMessage();
        cm.setOwner(GlobalData.getInstance().getOwner().getName());
        cm.setMsg(msg.getText());
    //    ArrayList<String> status=new Gson().fromJson(msg.getStatus(),new TypeToken<ArrayList<String>>(){}.getType());
   //     if(status==null) status=new ArrayList<String>();
        ArrayList<SugarUser> sendList = (ArrayList<SugarUser>) ((ArrayList<SugarUser>) ownerRoom).clone();
        if(sugarRoom.isGroup()) {
            sendList = (ArrayList<SugarUser>) ((ArrayList<SugarUser>) ownerRoom).clone();
            SugarUser owner = SugarUser.findByName(sugarRoom.getOwner());
            if (owner == null) owner = SugarUser.loadUser(sugarRoom.getOwner());
            sendList.add(owner);
        }
     //   if(msg.getReceivedAt()!=null&&status.size()==sendList.size()) return;
            for (SugarUser sugarUser : sendList) {
                if(sugarUser.getName().equals(GlobalData.getInstance().getOwner().getName())) continue;
       //         if (status.contains(sugarUser.getName())) continue;
                if (sugarRoom.isGroup()) cm.setGroup(sugarRoom.getName()+":"+sugarRoom.getOwner()+":"+sugarRoom.getMembers());
                if(imageURL!=null){
                    cm.setMsg(imageURL);
                    cm.setHeader("image");
                }else
                cm.setHeader("msg");
                cm.setCreatedAt(String.valueOf(new Date().getTime()));
                ThreadAccept accept = null;
                if ((accept = Clients.getInstance().getClient(sugarUser.getName())) == null) {
                    String ip1 = Clients.getInstance().getThreadServer().sendGetUser(sugarUser.getName());
                    if(ip1==null){
                        Toast.makeText(this,"User "+sugarUser.getName()+" not online",Toast.LENGTH_SHORT).show();
                        continue;
                    }
                    String[] ip=ip1.split("/");
                    Socket socket = null;
                    try {
                        socket = new Socket(InetAddress.getByName(ip[0]), 3000);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"IP not found",Toast.LENGTH_SHORT).show();
                        continue;
                    }
                    accept = new ThreadAccept(socket,getApplicationContext());
                    Clients.getInstance().add(accept, sugarUser.getName());
                    BufferedWriter bw= null;
                    try {
                        bw = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Can't create new socket",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        ComingMessage cms=new ComingMessage();
                        cms.setHeader("init");
                        bw.write(cms.toJson());
                        bw.newLine();
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Can't send init request",Toast.LENGTH_SHORT).show();
                    }
                }
                if (accept == null) {
                    Toast.makeText(getApplicationContext(),"Can't connect to user "+sugarUser.getName(),Toast.LENGTH_SHORT).show();
                    continue;
                }
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                    bw.write(cm.toJson());
                    bw.newLine();
                    bw.flush();
        //            status.add(sugarUser.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                    //Co the pipe cu da bi broken
                    try {
                        Socket socket = null;
                        String ip1 = Clients.getInstance().getThreadServer().sendGetUser(sugarUser.getName());
                        if(ip1==null){
                            Toast.makeText(this,"User "+sugarUser.getName()+" not online",Toast.LENGTH_SHORT).show();
                            continue;
                        }
                        String[] ip=ip1.split("/");
                        socket = new Socket(InetAddress.getByName(ip[0]), 3000);
                        accept = new ThreadAccept(socket, getApplicationContext());
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                        ComingMessage cms=new ComingMessage();
                        cms.setHeader("init");
                        bw.write(cms.toJson());
                        bw.newLine();
                        bw.flush();
                        //send lai tin nhan
                        bw.write(cm.toJson());
                        bw.newLine();
                        bw.flush();
                    }
                    catch (IOException e1){
                        e1.printStackTrace();
                        Toast.makeText(this,"Can't create socket connect",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            msg.setReceivedAt(new Date());
  //          msg.setStatus(new Gson().toJson(status));
            msg.save();
            adapter.update(msg.toMessage());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if(requestCode!= Activity.RESULT_CANCELED&& imageReturnedIntent!=null)
        switch(requestCode) {
            case 123:
                if(resultCode == Activity.RESULT_OK){
                    imageUri = imageReturnedIntent.getData();
                    InputStream inputStream = null;
                    try {
                        inputStream=getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    long size= 0;//MB
                    try {
                        size = inputStream.available()/1024/1024;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(size>4)
                    {
                        Toast.makeText(this,"FILE SIZE MUST LESS THAN 4MB",Toast.LENGTH_SHORT).show();
                    } {
                        boolean isImage=false;
                        String[] imageExtension=new String[]{"jpg","png","gif","jpeg"};
                        for(String extension:imageExtension)
                        {
                            if(imageUri.toString().toLowerCase().endsWith(extension))
                            {
                                isImage=true;
                                break;
                            }
                        }

                        SugarMessage sugarMessage=new SugarMessage(null,GlobalData.getInstance().getOwner().getId(),sugarRoom.getId(),new Date(),null,new Date(),null);
                        if(isImage) sugarMessage.setImageUrl(imageUri.toString());
                        else
                        sugarMessage.setText(imageUri.getPath());
                        sugarMessage.save();
                        Message mess=sugarMessage.toMessage();
                        adapter.addToStart(mess,true);
                        FileUpload.uploadFile(imageUri, MessageListActivity.this, new Callback<FileUpload>() {
                            @Override
                            public void onResponse(Call<FileUpload> call, Response<FileUpload> response) {
                                SendMessage(sugarMessage,response.body().getUrl());
                                sugarMessage.setImageUrl(response.body().getUrl());
                            }

                            @Override
                            public void onFailure(Call<FileUpload> call, Throwable t) {
                                Toast.makeText(getApplicationContext(),"Send file failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.title_bar_with_button,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.call:
                //adding init request
                SugarUser sugarUser=ownerRoom.get(0);
                ThreadAccept accept = null;
                if ((accept = Clients.getInstance().getClient(sugarUser.getName())) == null) {
                    String ip1 = Clients.getInstance().getThreadServer().sendGetUser(sugarUser.getName());
                    if(ip1==null){
                        Toast.makeText(this,"User "+sugarUser.getName()+" not online",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    String[] ip=ip1.split("/");
                    Socket socket = null;
                    try {
                        socket = new Socket(InetAddress.getByName(ip[0]), 3000);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"IP not found",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    accept = new ThreadAccept(socket,getApplicationContext());
                    Clients.getInstance().add(accept, sugarUser.getName());
                    BufferedWriter bw= null;
                    try {
                        bw = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Can't create new socket",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        ComingMessage cms=new ComingMessage();
                        cms.setHeader("init");
                        bw.write(cms.toJson());
                        bw.newLine();
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Can't send init request",Toast.LENGTH_SHORT).show();
                    }
                }
                if (accept == null) {
                    Toast.makeText(getApplicationContext(),"Can't connect to user "+sugarUser.getName(),Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MessageListActivity.this, MakeCallActivity.class);
                intent.putExtra(EXTRA_IP,Clients.getInstance().getClient(ownerRoom.get(0).getName()).getSocket().getInetAddress().getHostName());
                intent.putExtra(EXTRA_DISPLAYNAME, ownerRoom.get(0).getDisplayName());
                intent.putExtra("USERNAME",GlobalData.getInstance().getOwner().getName());
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void onMessageComing(long id) {
        SugarMessage msg=SugarMessage.findById(SugarMessage.class,id);
        Message message=msg.toMessage();
        //Kiem tra tin nhan co phai thuoc room nay khong
        //Neu tin nhan thuoc room add to start
        //else notify
        if(msg.getRoomId()==sugarRoom.getId()) {
            //tin nhan thuoc room:
            if (!adapter.update(message)) {
                msg.setReadAt(new Date());
                adapter.addToStart(message, true);
                adapter.notifyDataSetChanged();
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager=getSystemService(NotificationManager.class);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constant.CHANNEL_ID);
                builder.setSmallIcon(R.drawable.ic_michat);
                builder.setContentTitle("Mi Chat");
                builder.setContentText(message.getUser().getName() + ":" + message.getText());
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(Integer.parseInt(message.getUser().getId()),builder.build());
            }

        }
}
    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("onMsg")){
                onMessageComing(intent.getLongExtra("id",-1));
            }
//            else if(intent.getAction().equals("onCall")){
//                Toast.makeText(getApplicationContext(),"Receiving a call",Toast.LENGTH_SHORT).show();
//                Intent mintent = new Intent(MessageListActivity.this, ReceiveCallActivity.class);
//                mintent.putExtra(EXTRA_CONTACT, intent.getStringExtra(EXTRA_CONTACT));
//                mintent.putExtra(EXTRA_IP, intent.getStringExtra(EXTRA_IP));
//                startActivity(mintent);
//            }
        }
    };
    BroadcastReceiver receiverCall = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("onCall")){
                Intent mintent = new Intent(MessageListActivity.this, ReceiveCallActivity.class);
                mintent.putExtra(EXTRA_CONTACT, intent.getStringExtra(EXTRA_CONTACT));
                mintent.putExtra(EXTRA_IP, intent.getStringExtra(EXTRA_IP));
                startActivity(mintent);
            }
        }
    };
    IntentFilter intentFilter=new IntentFilter("onMsg");
    IntentFilter filterCall=new IntentFilter("onCall");
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverCall,filterCall);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverCall);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SugarRoom sugarroom=SugarRoom.findById(SugarRoom.class,sugarRoom.getId());
        members = new Gson().fromJson(sugarroom.getMembers(), new TypeToken<ArrayList<String>>() {
        }.getType());
        users.clear();
        for(String i:members){
            SugarUser user=SugarUser.findByName(i);
            if(user==null) user=SugarUser.loadUser(i);
            users.add(user.toUserAdapter());
        }
        listadapter.notifyDataSetChanged();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverCall,filterCall);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverCall);
    }
}
