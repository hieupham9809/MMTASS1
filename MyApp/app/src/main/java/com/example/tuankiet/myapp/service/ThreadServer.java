package com.example.tuankiet.myapp.service;

import android.util.Log;

import com.example.tuankiet.myapp.Constant;
import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import michat.GlobalData;
import michat.model.MESSAGE;
import michat.observer.Observer;
import michat.socketChat.Client;

public class ThreadServer extends Thread {
    Socket socket=null;
    private BufferedReader inputStream=null;
    private BufferedWriter outputStream=null;
    public ThreadServer(){
        try {
            socket=new Socket(InetAddress.getByName(Constant.SERVER_ADDR),Constant.SERVER_PORT);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
//        if(socket==null) return;
//        String msg;
//        while (true) {
//            try {
//                if ((msg = inputStream.readLine()) != null) {
//                    ComingMessage cm=ComingMessage.fromString(msg);
//                    switch (cm.getHeader()){
//                        case "getUser":
//                                if(cm.getMsg().equals("404")) {
//                                    GlobalData.getInstance().addConnected(cm.getOwner(),false);
//                                }
//                                else {
//                                    String IP[]=cm.getMsg().split("/");
//                                    Socket socket=new Socket(InetAddress.getByName(IP[2]),Integer.parseInt(IP[3]));
//                                    ThreadAccept accept=new ThreadAccept(socket);
//                                    Clients.getInstance().add(accept,cm.getOwner());
//                                    GlobalData.getInstance().addConnected(cm.getOwner(),true);
//                                }
//                            break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

//        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//        bw.write(MESSAGE.SEND_INIT_SESSION+"/"+ GlobalData.getInstance().getOwner().getName()+"/"+Constant.getIP()+"/"+"3000");
//        bw.newLine();
//        bw.flush();
    }
    public boolean sendInit() {
        try {
            outputStream.write(MESSAGE.SEND_INIT_SESSION+"/"+GlobalData.getInstance().getOwner().getName()+"/"+Constant.ip+"/"+Constant.LOCAL_PORT);
            outputStream.newLine();
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String sendGetUser(String username){
        try{
            outputStream.write("getUser/"+username);
            outputStream.newLine();
            outputStream.flush();
            String msg;
            while(true){
                if ((msg = inputStream.readLine()) != null){
                    ComingMessage cm=ComingMessage.fromString(msg);
                    if(cm.getHeader().equals("getUser")){
                        if(cm.getMsg().equals("404")) return null;
                        else return cm.getMsg();
                    }
                    else return null;
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public String sendGetOnline(){
        try{
            outputStream.write("getUser");
            outputStream.newLine();
            outputStream.flush();
            String msg;
            while(true){
                if ((msg = inputStream.readLine()) != null){
                    ComingMessage cm=ComingMessage.fromString(msg);
                    if(cm.getHeader().equals("getUser")){
                        if(cm.getMsg().equals("404")) return null;
                        else return cm.getMsg();
                    }
                    else return null;
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean sendForward(String userTo, String msg){
        try{
            outputStream.write("forward/"+GlobalData.getInstance().getOwner().getName()+"/"+userTo+"/"+msg);
            outputStream.newLine();
            outputStream.flush();
            return true;

        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
    public void sendClose(){
        try{
            outputStream.write("close");
            outputStream.newLine();
            outputStream.flush();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }
}
