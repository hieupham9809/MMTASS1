package michat.observer;

import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.service.ComingMessage;

public interface Observerable {
    default  void onMessageComing(long id){
        return;
    }
    default void onAddDialog(SugarRoom sugarRoom){
        return;
    }
    default void onServerResponse(String event, ComingMessage cm){return;}
}
