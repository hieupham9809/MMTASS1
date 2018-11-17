package michat.cloudDB;

import java.util.ArrayList;

import michat.model.Message;
import michat.model.User;

public interface IDatabase {
    ArrayList<User> getAllFriends();
    User getFriend(String id);
    ArrayList<User> getUsersChatWith();
    ArrayList<Message> getMessage(String idUserChatWith,int limit);
}
