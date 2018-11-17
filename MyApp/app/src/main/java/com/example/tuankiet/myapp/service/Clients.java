package com.example.tuankiet.myapp.service;

import java.util.HashMap;

public class Clients {
    private Clients(){

    }
    private static Clients instance;
    HashMap<String,ThreadAccept> clients=new HashMap<>();
    ThreadServer threadServer;
    public synchronized void add(ThreadAccept client, String owner){
        if(clients.containsKey(owner)){
            clients.replace(owner,client);
        }
        else clients.put(owner,client);
    }
    public synchronized void remove(String owner){
        if(clients.containsKey(owner)){
            clients.remove(owner);
        }
    }
    public synchronized static Clients getInstance(){
        if(instance==null){
            instance=new Clients();
        }
        return instance;
    }
    public ThreadAccept getClient(String username){
        return clients.get(username);
    }
    public void setThreadServer(ThreadServer threadServer){
        this.threadServer=threadServer;
    }

    public ThreadServer getThreadServer() {
        return threadServer;
    }
}
