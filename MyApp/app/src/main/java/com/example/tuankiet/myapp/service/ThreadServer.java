package com.example.tuankiet.myapp.service;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.tuankiet.myapp.Constant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import michat.GlobalData;
import michat.model.MESSAGE_CONSTANT;

public class ThreadServer extends Thread {
    Socket socket=null;
    private BufferedReader inputStream=null;
    private BufferedWriter outputStream=null;
    Context context;
    final static String rootPath= Environment.getExternalStorageDirectory()+java.io.File.separator+"com.example.tuankiet.michat";
    public ThreadServer(Context context){
        try {
            socket=new Socket(InetAddress.getByName(Constant.SERVER_ADDR),Constant.SERVER_PORT);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.context=context;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    String getuser=null;
    String getOnline=null;
    @Override
    public void run() {
//        if(socket==null) return;
//        String msg;
//        while (true) {
//            try {
//                if ((msg = inputStream.readLine()) != null) {
//                    ComingMessage cm = ComingMessage.fromString(msg);
//                    switch (cm.getHeader()){
//                        case "getUser":
//                            if(cm.getMsg().equals("404")) getuser="";
//                            else getuser=cm.getMsg();
//                            break;
//                        case "getOnline":
//                            getOnline=cm.getMsg();
//                            break;
//                        case "forward":
//                            String owner = cm.getOwner();
//                            String folder = rootPath + java.io.File.separator + owner;
//                            File file = new File(folder);
//                            if (!file.exists())
//                                if (!file.mkdirs()) {
//                                    throw new Exception("Error in creating directory:"+folder);
//                                }
//                            //check user co trong database chua
//
//                            SugarUser sugarUser=SugarUser.findByName(cm.getOwner());
//                            if(sugarUser==null){
//                                //tao user moi bang httprequest to server
//                                sugarUser=SugarUser.getUser(cm.getOwner());
//                                sugarUser.save();
//                            }
//                            //check room chat co trong database chua
//                            SugarRoom room=SugarRoom.findByName(cm.getOwner());
//                            if(room==null){
//                                //make new room
//                                room=new SugarRoom(cm.getOwner());
//                                HashSet<String> members=new HashSet<>();
//                                members.add(cm.getOwner());
//                                room.setMembers(new Gson().toJson(members));
//                                room.save();
//                            }
//                            //luu tin nhan vao database
//                            SugarMessage sugarMessage=null;
//                            sugarMessage=new SugarMessage(cm.getMsg(),sugarUser.getId(),room.getId(),new Date(Long.parseLong(cm.getCreatedAt())),new Date(),null,null);
//                            sugarMessage.save();
//                            if(sugarMessage!=null) {
//                                Log.d("new message", "notified");
//                                //notifi
//                                Intent intent = new Intent();
//                                intent.setAction("onMsg");
//                                intent.putExtra("id", sugarMessage.getId());
//                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                            }
//                            break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    public boolean sendInit() {
        try {
            outputStream.write(MESSAGE_CONSTANT.SEND_INIT_SESSION+"/"+GlobalData.getInstance().getOwner().getName()+"/"+Constant.ip+"/"+Constant.LOCAL_PORT);
            outputStream.newLine();
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String sendGetUser(String username){
        try {
            Toast.makeText(context,"Send get user",Toast.LENGTH_SHORT).show();
            outputStream.write("getUser/" + username);
            outputStream.newLine();
            outputStream.flush();
            String msg;
            while((msg=inputStream.readLine())!=null)
            {
                ComingMessage cm=ComingMessage.fromString(msg);
                if(!cm.getMsg().equals("404"))
                    return cm.getMsg();
                return null;
            }
        }
        catch (Exception e){
        }
        return null;
    }
    public String sendGetOnline(){
        try{
            outputStream.write("getOnline");
            outputStream.newLine();
            outputStream.flush();
            String msg;
            while((msg=inputStream.readLine())!=null)
            {
                return msg;
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
