package com.example.tuankiet.myapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.service.Clients;
import com.example.tuankiet.myapp.service.ComingMessage;
import com.example.tuankiet.myapp.service.ThreadAccept;
import com.example.tuankiet.myapp.voicecall.MainActivity2;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import michat.GlobalData;
import michat.model.Message;
import michat.observer.Observer;
import michat.observer.Observerable;
import michat.socketChat.Client;

import android.util.Base64;


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
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Intent intent=getIntent();;
        sugarRoom=SugarRoom.findById(SugarRoom.class,Integer.parseInt(intent.getStringExtra("roomId")));
        HashSet<String> members=new Gson().fromJson(sugarRoom.getMembers(),new TypeToken<HashSet<String>>(){}.getType());
        for(String i: members)
            ownerRoom.add(SugarUser.findByName(i));
        input=findViewById(R.id.input);
        msgList=findViewById(R.id.messagesList);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"+sugarRoom.getName()+"</font>"));
        adapter=new MessagesListAdapter<>(String.valueOf(GlobalData.getInstance().getOwner().getId()), new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        });
        msgList.setAdapter(adapter);
        Iterator<SugarMessage> lstMsg=SugarMessage.findAsIterator(SugarMessage.class,"ROOM_ID=?",String.valueOf(sugarRoom.getId()));
        while(lstMsg.hasNext()){
            Message msg=lstMsg.next().toMessage();
            if(msg.getImageUrl()!=null){
                Uri uri=Uri.parse(msg.getImageUrl());
                getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION|intent.getFlags());
            }
            adapter.addToStart(msg,true);
        }
        input.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("file/*");

                chooseFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, 123);
            }
        });
        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                SugarMessage sugarMessage=new SugarMessage();
                sugarMessage.setText(input.toString());
                sugarMessage.setImageUrl(null);
                sugarMessage.setCreatedAt(new Date());
                sugarMessage.setOwner(GlobalData.getInstance().getOwner().getId());
                sugarMessage.setReceivedAt(null);
                sugarMessage.setReadAt(new Date());
                sugarMessage.setRoomId(sugarRoom.getId());
                sugarMessage.setStatus(null);
                sugarMessage.save();
                adapter.addToStart(sugarMessage.toMessage(),true);
                /*
                 *Gui tin nhan
                 * Neu user chua connect toi minh thi minh tao socket connect toi user
                 * Nguoc lai lay outputstream tu socket ra de gui tin nhan
                 * Neu khong co exception thi coi nhu gui thanh cong
                 * cap nhan truong receive cua sugarMessage
                 * TODO
                 */
                try {

                    for (SugarUser sugarUser : ownerRoom) {
                        ComingMessage cm = new ComingMessage();
                        cm.setOwner(GlobalData.getInstance().getOwner().getName());
                        cm.setMsg(sugarMessage.getText());
                        if (ownerRoom.size() > 0) cm.setGroup(sugarRoom.getName());
                        cm.setHeader("msg");
                        cm.setCreatedAt(String.valueOf(new Date().getTime()));
                        ThreadAccept accept = null;
                        if ((accept = Clients.getInstance().getClient(sugarUser.getName())) == null) {
                            String[] ip = Clients.getInstance().getThreadServer().sendGetUser(sugarUser.getName()).split("/");
                            Socket socket = new Socket(InetAddress.getByName(ip[0]), 3000);
                            accept = new ThreadAccept(socket,getApplicationContext());
                            Clients.getInstance().add(accept, sugarUser.getName());
                            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                            bw.write("init");
                        }
                        if (accept == null) {
                            Toast.makeText(getApplicationContext(),"Can't connect to user "+sugarUser.getName(),Toast.LENGTH_SHORT).show();
                            continue;
                        }
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                        bw.write(cm.toJson());
                        bw.newLine();
                        bw.flush();
                    }
                    sugarMessage.setReceivedAt(new Date());
                    sugarMessage.save();
                    adapter.update(sugarMessage.toMessage());
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return true;
            }

        });
        /////////////


    }
    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = getContentResolver().query( contentUri, proj, null, null,null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode!=RESULT_CANCELED)
        switch(requestCode) {
            case 123:
                if(resultCode == RESULT_OK){
                    imageUri = imageReturnedIntent.getData();
                    grantUriPermission(this.getPackageName(),imageUri,imageReturnedIntent.getFlags()&Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                    }
                    else
                    try {
                        byte[] data=new byte[(int)inputStream.available()];
                        inputStream.read(data);
                        String base64=Base64.encodeToString(data,Base64.DEFAULT);
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
                        sugarMessage.setText(imageUri.toString());
                        sugarMessage.save();
                        adapter.addToStart(sugarMessage.toMessage(),true);

                        /*
                         *Gui tin nhan
                         * Neu user chua connect toi minh thi minh tao socket connect toi user
                         * Nguoc lai lay outputstream tu socket ra de gui tin nhan
                         * Neu khong co exception thi coi nhu gui thanh cong
                         * cap nhan truong receive cua sugarMessage
                         * TODO
                         */
                        for (SugarUser sugarUser : ownerRoom) {
                            ComingMessage cm = new ComingMessage();
                            cm.setOwner(GlobalData.getInstance().getOwner().getName());
                            cm.setMsg(base64);
                            if (ownerRoom.size() > 0) cm.setGroup(sugarRoom.getName());
                            if(isImage)
                                cm.setHeader("image/image.jpg");
                            else cm.setHeader("file/file.txt");
                            cm.setCreatedAt(String.valueOf(new Date().getTime()));
                            ThreadAccept accept = null;
                            if ((accept = Clients.getInstance().getClient(sugarUser.getName())) == null) {
                                String[] ip = Clients.getInstance().getThreadServer().sendGetUser(sugarUser.getName()).split("/");
                                Socket socket = new Socket(InetAddress.getByName(ip[0]), 3000);
                                accept = new ThreadAccept(socket,getApplicationContext());
                                Clients.getInstance().add(accept, sugarUser.getName());
                                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                                bw.write("init");
                            }
                            if (accept == null) {
                                Toast.makeText(getApplicationContext(),"Can't connect to user "+sugarUser.getName(),Toast.LENGTH_SHORT).show();
                                continue;
                            }
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                            bw.write(cm.toJson());
                            bw.newLine();
                            try{
                            bw.flush();}
                            catch (SocketException e){
                                String[] ip = Clients.getInstance().getThreadServer().sendGetUser(sugarUser.getName()).split("/");
                                Socket socket = new Socket(InetAddress.getByName(ip[0]), 3000);
                                accept = new ThreadAccept(socket,getApplicationContext());
                                Clients.getInstance().add(accept, sugarUser.getName());
                                bw=new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                                bw.write("init");
                            }
                        }
                        sugarMessage.setReceivedAt(new Date());
                        sugarMessage.save();
                        sugarMessage.setText(null);
                        adapter.update(sugarMessage.toMessage());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.call:
                //adding init request
                Intent intent = new Intent(MessageListActivity.this, MakeCallActivity.class);
                intent.putExtra(EXTRA_IP,Clients.getInstance().getClient(ownerRoom.get(0).getName()).getSocket().getInetAddress().getHostName());
                intent.putExtra(EXTRA_DISPLAYNAME, ownerRoom.get(0).getDisplayName());
                startActivity(intent);
                return true;
                
                
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void onMessageComing(long id) {
        SugarMessage msg=SugarMessage.findById(SugarMessage.class,id);
        Message message=msg.toMessage();
        Log.d("New message from",message.getUser().getName());
        String owner=message.getUser().getName();
        boolean push=true;
        for(SugarUser i : ownerRoom){
            if(!i.getName().equals(owner))
            {
                push=false;
                break;
            }
        }
        if(push)
        {
            if(!adapter.update(message)) {
                msg.setReadAt(new Date());
                adapter.addToStart(message, true);
            }
        }
        else{
            //connect server get ip
            CharSequence name="Mi Chat";
            String description="Mo ta";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager=getSystemService(NotificationManager.class);
                builder = new NotificationCompat.Builder(this, Constant.CHANNEL_ID);
                builder.setSmallIcon(R.drawable.ic_messenger);
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
            else if(intent.getAction().equals("onCall")){
                Toast.makeText(getApplicationContext(),"Receiving a call",Toast.LENGTH_SHORT).show();
                Intent mintent = new Intent(MessageListActivity.this, ReceiveCallActivity.class);
                mintent.putExtra(EXTRA_CONTACT, intent.getStringExtra(EXTRA_CONTACT));
                mintent.putExtra(EXTRA_IP, intent.getStringExtra(EXTRA_IP));
                startActivity(mintent);
            }
        }
    };
    BroadcastReceiver receiverCall = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("onCall")){
                Toast.makeText(getApplicationContext(),"Receiving a call",Toast.LENGTH_SHORT).show();
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
