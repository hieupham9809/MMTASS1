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

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import michat.observer.Observer;

public class ThreadAccept extends Thread {
    BufferedReader inputStream;
    BufferedWriter outputStream;

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

        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
                    String folder = rootPath + java.io.File.separator + owner;
                    File file = new File(folder);
                    if (!file.exists())
                        if (!file.mkdirs()) {
                            throw new Exception("Error in creating directory:"+folder);
                        }
                    //check user co trong database chua

                    SugarUser sugarUser=SugarUser.findByName(cm.getOwner());
                    if(sugarUser==null){
                        //tao user moi bang httprequest to server
                        sugarUser=SugarUser.getUser(cm.getOwner());
                        sugarUser.save();
                    }
                    //check room chat co trong database chua
                    SugarRoom room=SugarRoom.findByName(cm.getOwner());
                    if(room==null){
                        //make new room
                        room=new SugarRoom(cm.getOwner());
                        HashSet<String> members=new HashSet<>();
                        members.add(cm.getOwner());
                        room.setMembers(new Gson().toJson(members));
                        room.save();
                    }
                    //luu tin nhan vao database
                    SugarMessage sugarMessage=null;
                    if (cm.getHeader().equals("msg")) {
                        {
                            sugarMessage=new SugarMessage(cm.getMsg(),sugarUser.getId(),room.getId(),new Date(Long.parseLong(cm.getCreatedAt())),new Date(),null,null);
                            sugarMessage.save();
                        }
                    } else if (cm.getHeader().equals("image")) {
                        String filename = rootPath + "/" + owner + "/" + cm.getHeader().split("/")[1];
                        writeToFile(filename,cm.getMsg());
                        sugarMessage=new SugarMessage(null,sugarUser.getId(),room.getId(),new Date(Long.parseLong(cm.getCreatedAt())),new Date(),null,filename);
                        sugarMessage.save();

                    } else if (cm.getHeader().equals("file")) {
                        String filename = rootPath + "/" + owner + "/" + cm.getHeader().split("/")[1];
                        writeToFile(filename,cm.getMsg());
                        sugarMessage=new SugarMessage(filename,sugarUser.getId(),room.getId(),new Date(Long.parseLong(cm.getCreatedAt())),new Date(),null,null);
                        sugarMessage.save();
                    }
                    if(sugarMessage!=null){
                        Log.d("new message","notified");
                        //notifi
                        Intent intent=new Intent();
                        intent.setAction("onMsg");
                        intent.putExtra("id",sugarMessage.getId());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

}
