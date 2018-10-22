package michat.model;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;

public class DialogList implements IDialog{
        private String id;
        private String dialogPhoto;
        private String dialogName;
        private ArrayList<IUser> users;
        private IMessage lastMessage;
        private int unreadCount;

    public DialogList(String id, String dialogPhoto, String dialogName, ArrayList<IUser> users, IMessage lastMessage, int unreadCount) {
        this.id = id;
        this.dialogPhoto = dialogPhoto;
        this.dialogName = dialogName;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    @Override
        public String getId() {
            return id;
        }

        @Override
        public String getDialogPhoto() {
            return dialogPhoto;
        }

        @Override
        public String getDialogName() {
            return dialogName;
        }

        @Override
        public ArrayList<IUser> getUsers() {
            return users;
        }

        @Override
        public IMessage getLastMessage() {
            return lastMessage;
        }

        @Override
        public void setLastMessage(IMessage lastMessage) {
            this.lastMessage = lastMessage;
        }

        @Override
        public int getUnreadCount() {
            return unreadCount;
        }
}
