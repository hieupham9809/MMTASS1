package com.example.tuankiet.myapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

import michat.GlobalData;
import michat.localDB.MessageDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.IP;
import michat.model.IPINFO;
import michat.model.MESSAGE;
import michat.model.Message;
import michat.model.Msg;
import michat.model.Packet;
import michat.model.User;
import michat.socketChat.Client;

import static android.widget.Toast.*;

public class MainService extends Service {
    Context context;
    MessageDatabaseHandler mdh;
    UserDatabaseHandler udh;
    DatagramSocket ds;
    static int NOT_CONNECT = 0;
    static int CONNECT_SUCCESS = 1;
    static int CONNECT_FAILED = -1;
    static int NOT_INIT_SESSION = 0;
    static int INISESSION_SUCCESS = 1;
    static int INITSESSION_FAILED = -1;
    static int NOT_GET_FRIEND = 0;
    static int GET_FRIEND_SUCESS = 1;
    static int GET_FRIEND_FAILED = -1;
    static int NOT_SYN=0;
    static int SYN_FAILED=-1;
    static int SYN_SUCCESS=1;
    static String SERVER_ADDR = "192.168.43.250";
    static int SERVER_PORT = 30000;
    static int APPLICATION_PORT = 10000;

    int initSession;
    int connected;
    int syn;
    int getFriend;

    public MainService() {
    }

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.mdh = new MessageDatabaseHandler(context);
        this.udh = new UserDatabaseHandler(context);

        initSession = NOT_INIT_SESSION;
        try {
            this.ds = new DatagramSocket(APPLICATION_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Thread receive = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            DatagramPacket dp = null;
                            String s=null;
                            Packet msg=null;

                            try {
                                dp = Client.getPacket(ds);
                                s = Client.getString(dp);
                                Log.d("MSG COMMING",s);
                                msg = new ObjectMapper().readValue(s, Packet.class);

                            } catch (Exception e) {

                                e.printStackTrace();
                                continue;
                            }
                            switch (msg.getType()) {
                                case MESSAGE.MSG_TYPE_MESSAGE:
                                    //if user owner cua tin nhan chua co trong csdl thi them user vao
                                    User user = udh.getUser(msg.getData().getOwner());
                                    if (user == null) {
                                        //Nen goi http request toi server de lay du lieu ve user
                                        udh.addUser(new User("userid", msg.getData().getOwner(), "No Name", "", "friend", "1/1/2001", "Nam"));
                                        mdh.setTableName(msg.getData().getOwner());
                                        mdh.createTable();
                                    }
                                    //nen check tin nhan co ton tai trong database hay khong?
                                    Message message = new Message(msg.getData().getId(), msg.getData().getText(), user, new Date(Long.parseLong(msg.getData().getCreatedAt())), new Date(), null);
                                    mdh.setTableName(message.getUser().getName());
                                    if(!mdh.isTableExist(message.getUser().getName()))
                                        mdh.createTable();
                                    mdh.addMessage(message);
                                    Log.d("MSG COMMING",message.getText());
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction(MESSAGE.RECEIVE_MSG);
                                    broadcastIntent.putExtra("tableName", message.getUser().getName());
                                    broadcastIntent.putExtra("id",message.getId());
                                    sendBroadcast(broadcastIntent);
                                    break;
                                case MESSAGE.MSG_TYPE_CONNECT:
                                    connected=CONNECT_SUCCESS;
                                    Log.d("OWNER2",msg.getData().getOwner());
                                    if(msg.getData().getText().equals("public"))
                                        GlobalData.getInstance().getIP(msg.getData().getOwner()).setIsPub(true);
                                    else
                                        GlobalData.getInstance().getIP(msg.getData().getOwner()).setIsPub(false);
                                    break;
                                case MESSAGE.MSG_TYPE_RESPONE_SYN:
                                    Log.d("Response syn",msg.getData().getText());
                                    if (msg.getData().getText().equals("404")) {
                                        syn = SYN_FAILED;
                                    } else {
                                        syn = SYN_SUCCESS;
                                        connected=CONNECT_SUCCESS;
                                        String address[] = msg.getData().getText().split(":");
                                        String owner = msg.getData().getOwner();
                                        Log.d("OWNER1",owner);
                                        GlobalData.getInstance().addIP(owner, new IP(address[0], Integer.parseInt(address[1])),new IP(address[2],Integer.parseInt(address[3])));
//                                        int totalTime=0;
//                                        try {
//                                            while (connected == NOT_CONNECT && totalTime < 3000) {
//                                                Msg m = new Msg();
//                                                m.setCreatedAt("");
//                                                m.setId("");
//                                                m.setOwner(GlobalData.getInstance().getOwner().getName());
//                                                Packet p = new Packet();
//                                                p.setType("connect");
//                                                p.setData(m);
//                                                Log.d("msg owner send",m.getOwner());
//                                                p.getData().setText("public");
//
//                                                Client.sendString(ds, new ObjectMapper().writeValueAsString(p), InetAddress.getByName(GlobalData.getInstance().getIP(owner).getPubIP().getIp()), GlobalData.getInstance().getIP(owner).getPubIP().getPort());
//
//                                                p.getData().setText("private");
//
//                                                Client.sendString(ds, new ObjectMapper().writeValueAsString(p), InetAddress.getByName(GlobalData.getInstance().getIP(owner).getPriIP().getIp()), GlobalData.getInstance().getIP(owner).getPriIP().getPort());
//
//                                                Thread.sleep(300);
//                                                totalTime+=300;
//                                            }
//                                        }
//                                        catch (Exception e){
//                                            e.printStackTrace();
//                                        }
                                    }
                                    break;
                                case MESSAGE.MSG_TYPE_INIT_SESSION:
                                    if (msg.getData().getText().equals("200"))
                                        initSession = INISESSION_SUCCESS;
                                    else if (msg.getData().getText().equals("404"))
                                        initSession = INITSESSION_FAILED;
                                    break;
                                case MESSAGE.MSG_TYPE_ONLINE:
                                    getFriend = GET_FRIEND_SUCESS;
                                    String output = msg.getData().getText();
                                    broadcastIntent = new Intent();
                                    broadcastIntent.setAction(MESSAGE.RECEIVE_USERS_ONLINE);
                                    broadcastIntent.putExtra(MESSAGE.RECEIVE_USERS_ONLINE, output);
                                    sendBroadcast(broadcastIntent);
                                    break;

                            }
                        }
                    }
                });
        receive.start();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(intent.getAction()!=null)
        switch (intent.getAction()){
            case MESSAGE.SEND_INIT_SESSION:
                initSession=NOT_INIT_SESSION;
                String username = intent.getStringExtra("username");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int totalTime=0;
                        try {
                            while (initSession == NOT_INIT_SESSION&&totalTime<3000) {
                                Client.sendString(ds, "init/" + username+"/"+InetAddress.getLocalHost().getHostAddress()+"/"+APPLICATION_PORT, InetAddress.getByName(SERVER_ADDR), SERVER_PORT);
                                totalTime+=300;
                                Thread.sleep(300);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(initSession==NOT_INIT_SESSION) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MESSAGE.INIT_SESSION_FAILED);
                            sendBroadcast(broadcastIntent);
                        }
                    }
                }).start();

                break;
            case MESSAGE.SEND_GET_USER_ONLINE:
                getFriend = NOT_GET_FRIEND;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int totalTime=0;
                        try {
                            while (getFriend == NOT_GET_FRIEND&&totalTime<3000) {
                                Client.sendString(ds, "getOnline/All", InetAddress.getByName(SERVER_ADDR), SERVER_PORT);
                                Thread.sleep(300);
                                totalTime+=300;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(getFriend==NOT_GET_FRIEND){
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MESSAGE.GET_USER_ONLINE_FAILED);
                            sendBroadcast(broadcastIntent);
                        }
                    }
                }).start();

                break;
            case MESSAGE.SEND_SYN:
                syn = NOT_SYN;
                connected=NOT_CONNECT;
                String u = intent.getStringExtra("user");
                Toast.makeText(context,"SEND SYN",LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int totalTime=0;
                        try {
                            while (syn == NOT_SYN&&totalTime<3000) {
                                Log.d("SEING SYN>>>", "syn/" +GlobalData.getInstance().getOwner().getName()+"/"+u);
                                Client.sendString(ds, "syn/" +GlobalData.getInstance().getOwner().getName().toString()+"/"+u, InetAddress.getByName(SERVER_ADDR), SERVER_PORT);
                                Thread.sleep(300);
                                totalTime+=300;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(syn==NOT_SYN){
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MESSAGE.SYN_FAILED);
                            sendBroadcast(broadcastIntent);
                        }
                    }
                }).start();


                break;
            case MESSAGE.SEND_MESSAGE:
                if (connected == CONNECT_SUCCESS) {
                    String msg = intent.getStringExtra("msg");
                    String owner = intent.getStringExtra("owner");
                    IPINFO ip = GlobalData.getInstance().getIP(owner);
                    try {
                        if(ip.getIsPub())
                        Client.sendString(ds, msg, InetAddress.getByName(ip.getPubIP().getIp()), ip.getPubIP().getPort());//resotre getbyname
                        else
                            Client.sendString(ds, msg, InetAddress.getByName(ip.getPubIP().getIp()), ip.getPubIP().getPort());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "User not onlined! Message will be saved!", LENGTH_SHORT).show();
                }
                break;
        }
//        if (intent.getStringExtra("username")!=null) {
//            initSession=NOT_INIT_SESSION;
//            String username = intent.getStringExtra("username");
//            try {
//                while (initSession == NOT_INIT_SESSION) {
//                    Client.sendString(this.ds, "init/" + username, InetAddress.getByName(SERVER_ADDR), SERVER_PORT);
//                    Thread.sleep(500);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (intent.getStringExtra("msg") != null) {
//            if (connected == CONNECT_SUCCESS) {
//                String msg = intent.getStringExtra("msg");
//                String owner = intent.getStringExtra("owner");
//                IP ip = GlobalData.getInstance().getIP(owner);
//                try {
//                    Client.sendString(ds, msg, InetAddress.getByName(ip.getIp()), ip.getPort());//resotre getbyname
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Toast.makeText(context, "User not onlined! Message will be saved!", LENGTH_SHORT).show();
//            }
//        }
//        if (intent.getStringExtra(MESSAGE.RECEIVE_USERS_ONLINE) != null) {
//            getFriend = NOT_GET_FRIEND;
//            try {
//                while (getFriend == NOT_GET_FRIEND) {
//                    Client.sendString(this.ds, "getOnline/All", InetAddress.getByName(SERVER_ADDR), SERVER_PORT);
//                    Thread.sleep(500);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (intent.getStringExtra("connect") != null) {
//            connected = NOT_CONNECT;
//            String connect = intent.getStringExtra("connect");
//            try {
//                while (connected != NOT_CONNECT) {
//                    Client.sendString(ds, "connect/" + connect, InetAddress.getByName(SERVER_ADDR), SERVER_PORT);
//                    Thread.sleep(500);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return START_REDELIVER_INTENT;
    }
}

