package com.example.tuankiet.myapp.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tuankiet.myapp.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadListen extends Thread{
    private final Context context;

    public ThreadListen(Context context){
        this.context=context;
    }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket=new ServerSocket(Constant.LOCAL_PORT);
            while (true){
                Socket client=serverSocket.accept();
                Log.d("Client","Connected new");
                new ThreadAccept(client,context).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
