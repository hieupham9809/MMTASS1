package michat;

import michat.model.IP;

import java.util.ArrayList;
import java.util.HashMap;

import michat.model.IPINFO;
import michat.model.Message;
import michat.model.User;

public class GlobalData {
    HashMap<String,ArrayList<Message>> resent;//receive attributes null, chua tin nhan duoc gui tu owner nhung chua nhan duoc ack tu nguoi nhan
    HashMap<String,IPINFO> ipInfo;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    User owner;
    private  static GlobalData instance;
    private GlobalData(){
        resent=new HashMap<>();
        ipInfo=new HashMap<>();
    }
    public static synchronized GlobalData getInstance(){
        if(instance==null)
            instance=new GlobalData();
        return instance;
    }
    public void addIP(String username,IP pub,IP pri){
        if(ipInfo.get(username)==null)
        {
            ipInfo.put(username,new IPINFO(pub,pri));
        }
        else ipInfo.replace(username,new IPINFO(pub,pri));
    }
    public IPINFO getIP(String username){
        return ipInfo.get(username);
    }
}
