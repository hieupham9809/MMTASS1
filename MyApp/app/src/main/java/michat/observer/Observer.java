package michat.observer;

import android.support.v4.app.NotificationCompat;

import com.example.tuankiet.myapp.Constant;
import com.example.tuankiet.myapp.MainActivity;
import com.example.tuankiet.myapp.R;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.service.ComingMessage;

import java.util.ArrayList;

public class Observer {
    private static Observer sObserver=null;
    private ArrayList<Observerable> mObsererableList=new ArrayList<>();
    private Observer(){}
    public void register(final Observerable subcriber){
        if(subcriber!=null&&!mObsererableList.contains(subcriber)){
            mObsererableList.add(subcriber);
        }
    }
    public void unregister(final Observerable subcriber){
        if(subcriber!=null&&mObsererableList.contains(subcriber)){
            mObsererableList.remove(subcriber);
        }
    }
    public static Observer getInstance(){
        if(sObserver==null){
            sObserver=new Observer();
        }
        return sObserver;
    }

    public void notifyonMessage(long id){
        if(mObsererableList.isEmpty()){

        } else
        for(Observerable subcriber:mObsererableList){
            subcriber.onMessageComing(id);
        }
    }
    public void notifyonAddDialog(SugarRoom room){
        if(mObsererableList.isEmpty()){

        }
        else
        for(Observerable subcriber:mObsererableList){
            subcriber.onAddDialog(room);
        }
    }
    public void notifyonResponseServer(String event, ComingMessage cm){
        for(Observerable subscriber:mObsererableList){
            subscriber.onServerResponse(event,cm);
        }
    }
}
