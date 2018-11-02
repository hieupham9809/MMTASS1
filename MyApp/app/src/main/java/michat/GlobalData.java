package michat;

import com.example.tuankiet.myapp.chatorm.SugarUser;

import michat.model.IP;

import java.util.ArrayList;
import java.util.HashMap;

import michat.model.IPINFO;
import michat.model.Message;
import michat.model.User;

public class GlobalData {
    HashMap<String,ArrayList<Message>> resent;//receive attributes null, chua tin nhan duoc gui tu owner nhung chua nhan duoc ack tu nguoi nhan
    HashMap<String,Boolean> isConnected;

    public SugarUser getOwner() {
        return owner;
    }

    public void setOwner(SugarUser owner) {
        this.owner = owner;
    }

    SugarUser owner;
    private  static GlobalData instance;
    private GlobalData(){
        resent=new HashMap<>();
        isConnected=new HashMap<>();
    }
    public static synchronized GlobalData getInstance(){
        if(instance==null)
            instance=new GlobalData();
        return instance;
    }

    public Boolean getIsConnected(String username) {
        return isConnected.get(username);
    }

    public void addConnected(String username,boolean val) {
        isConnected.put(username,val);
    }
}
