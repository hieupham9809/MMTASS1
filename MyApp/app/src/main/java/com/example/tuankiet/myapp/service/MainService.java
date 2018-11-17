package com.example.tuankiet.myapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.voicecall.ContactManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import michat.GlobalData;
import michat.model.MESSAGE_CONSTANT;

public class MainService extends Service {
    boolean isConnectServer=false;
    static final String LOG_TAG = "UDPchat";
    protected static final int LISTENER_PORT = 50003;
    protected static final int BUF_SIZE = 1024;
    protected ContactManager contactManager;
    protected String displayName;
    protected boolean STARTED = false;
    protected boolean IN_CALL = false;
    protected boolean LISTEN = false;

    public final static String EXTRA_CONTACT = "michat.CallOut.CONTACT";
    public final static String EXTRA_IP = "michat.CallOut.IP";
    public final static String EXTRA_DISPLAYNAME = "michat.CallOut.DISPLAYNAME";


    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ThreadListen thread=new ThreadListen(getApplicationContext());
        thread.start();
        startCallListener();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(intent.getAction()!=null){
            switch (intent.getAction()){
                case MESSAGE_CONSTANT.SEND_INIT_SESSION:
                    ThreadServer threadServer=new ThreadServer(getApplicationContext());
                    Clients.getInstance().setThreadServer(threadServer);
                    isConnectServer=Clients.getInstance().getThreadServer().sendInit();
                    break;
                case MESSAGE_CONSTANT.SEND_MESSAGE:
                    if(!isConnectServer){
                        break;
                    }
                    int id=intent.getIntExtra("id",-1);
                    SugarMessage sugarMessage=SugarMessage.findById(SugarMessage.class,id);
                    SugarRoom room=SugarRoom.findById(SugarRoom.class,sugarMessage.getRoomId());
                    try {
                        BufferedWriter os=new BufferedWriter(new OutputStreamWriter(Clients.getInstance().clients.get(room.getName()).getOutputStream()));
                        ComingMessage cm=new ComingMessage();
                        cm.setCreatedAt(String.valueOf(sugarMessage.getCreatedAt().getTime()));
                        cm.setGroup(null);
                        cm.setHeader("msg");
                        cm.setMsg(sugarMessage.getText());
                        cm.setOwner(GlobalData.getInstance().getOwner().getName());
                        String out=cm.toJson();
                        os.write(out);
                        os.newLine();
                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return START_REDELIVER_INTENT;
    }
    private void startCallListener() {
        // Creates the listener thread
        LISTEN = true;
        Thread listener = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    // Set up the socket and packet to receive
                    Log.i(LOG_TAG, "Incoming call listener started");
                    DatagramSocket socket = new DatagramSocket(LISTENER_PORT);
                    socket.setSoTimeout(15000);
                    byte[] buffer = new byte[BUF_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, BUF_SIZE);
                    while(LISTEN) {
                        // Listen for incoming call requests
                        try {
                            //Log.i(LOG_TAG, "Listening for incoming calls");
                            socket.receive(packet);
                            String data = new String(buffer, 0, packet.getLength());
                            Log.i(LOG_TAG, "Packet received from "+ packet.getAddress() +" with contents: " + data);
                            String action = data.substring(0, 4);
                            if(action.equals("CAL:")) {
                                // Received a call request. Start the ReceiveCallActivity
                                String address = packet.getAddress().toString();
                                String name = data.substring(4, packet.getLength());

                                Intent intent = new Intent();
                                intent.setAction("onCall");
                                intent.putExtra(EXTRA_CONTACT, name);
                                intent.putExtra(EXTRA_IP, address.substring(1, address.length()));
                                IN_CALL = true;
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                            else {
                                // Received an invalid request
                                Log.w(LOG_TAG, packet.getAddress() + " sent invalid message: " + data);
                            }
                        }
                        catch(Exception e) {}
                    }
                    Log.i(LOG_TAG, "Call Listener ending");
                    socket.disconnect();
                    socket.close();
                }
                catch(SocketException e) {

                    Log.e(LOG_TAG, "SocketException in listener " + e);
                }
            }
        });
        listener.start();
    }


}

