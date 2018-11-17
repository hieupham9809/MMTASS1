package com.example.tuankiet.myapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import michat.observer.Observer;

public class ThreadAccept extends Thread {
    public Socket getSocket() {
        return socket;
    }

    Socket socket;
    String owner;
    Context context;
    final static String rootPath=Environment.getExternalStorageDirectory()+java.io.File.separator+"com.example.tuankiet.michat";
    public ThreadAccept(Socket socket,Context context) {
        this.socket = socket;
        this.context=context;
    }
    public static void writeToFile(String filename, String base64) throws Exception {
        File file=new File(filename);
        if (!file.exists())
            if (!file.createNewFile()) {
                throw new Exception("Error in creating file");
            } else {
                int i = 1;
                filename = filename + "(" + i + ")";
                file = new File(filename);
                while (!file.createNewFile()) {
                    i++;
                    if (i > 1000)
                        throw new Exception("Error in creating file loop");
                }
            }
        byte[] decodedBytes = Base64.decode(base64,Base64.DEFAULT);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(decodedBytes);
        fos.close();
    }

    public void run() {

        BufferedReader inputStream=null;
        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String msg;
        while (true) {
            try {
                if ((msg = inputStream.readLine()) != null) {
                    Log.d("Messegae coming",msg);
                    ComingMessage cm = ComingMessage.fromString(msg);
                    owner = cm.getOwner();
                    if (cm.getHeader().equals("init")) {
                        Clients.getInstance().add(this, owner);
                    }
                    else {
                        SugarUser sugarUser = SugarUser.findByName(cm.getOwner());
                        if (sugarUser == null) {
                            //tao user moi bang httprequest to server
                            sugarUser = SugarUser.loadUser(cm.getOwner());
//                        List<SugarUser> all=SugarUser.getAllUser();
//                        for(SugarUser i: all){
//                            if(i.getName().equals(cm.getOwner())){
//                                sugarUser=i;
//                                break;
//                            }
//                        }
                            sugarUser.save();
                        }
                        //check room chat co trong database chua
                        //Can xem xet lai doan nay
                        SugarRoom room = null;
                        if (cm.getGroup() == null)
                            room = SugarRoom.findByName(sugarUser.getName());
                        else room = SugarRoom.findGroupByName(cm.getGroup());
                        if (room == null) {
                            if (cm.getGroup() == null) {
                                //make new room
                                room = new SugarRoom(cm.getOwner());
                                room.setGroup(false);
                                HashSet<String> members = new HashSet<>();
                                members.add(cm.getOwner());
                                room.setMembers(new Gson().toJson(members));
                                room.save();
                            } else {
                                //tao group: TODO
                                //group: NAME:OWNER:MEMBERS
                                String group[] = cm.getGroup().split(":");
                                String name = group[0];
                                String owner = group[1];
                                String members = group[2];
                                room = new SugarRoom();
                                Log.d("CREATE NEW ROOM", group[0]);
                                room.setGroup(true);
                                room.setMembers(members);
                                room.setName(name);
                                room.setOwner(owner);
                                room.save();
                            }
                        }

                        //luu tin nhan vao database
                        SugarMessage sugarMessage = null;
                        if (cm.getHeader().equals("msg")) {
                            {
                                sugarMessage = new SugarMessage(cm.getMsg(), sugarUser.getId(), room.getId(), new Date(Long.parseLong(cm.getCreatedAt())), new Date(), null, null);
                                sugarMessage.save();
                            }
                        } else if (cm.getHeader().equals("image")) {
                            sugarMessage = new SugarMessage(null, sugarUser.getId(), room.getId(), new Date(Long.parseLong(cm.getCreatedAt())), new Date(), null, cm.getMsg());
                            sugarMessage.save();

                        }
                        if (sugarMessage != null) {
                            Intent intent = new Intent();
                            intent.setAction("onMsg");
                            intent.putExtra("id", sugarMessage.getId());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

}
