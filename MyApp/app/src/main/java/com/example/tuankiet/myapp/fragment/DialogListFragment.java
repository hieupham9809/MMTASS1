package com.example.tuankiet.myapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tuankiet.myapp.MessageListActivity;
import com.example.tuankiet.myapp.R;
import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.service.MainService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import michat.GlobalData;
import michat.model.DialogList;
import michat.model.MESSAGE_CONSTANT;
import michat.model.Message;
import michat.model.User;
import michat.observer.Observer;
import michat.observer.Observerable;

public class DialogListFragment extends Fragment implements Observerable {
    DialogsList dialogList;
    DialogsListAdapter adapter;
    List<IDialog> diaLst;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diaLst=new ArrayList<>();
        adapter = new DialogsListAdapter<>(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
                Transformation transformation = new CircleTransform();

                Picasso.get().load(url)
                        .fit()
                        .transform(transformation)
                        .into(imageView);
            }
        });
        adapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(IDialog dialog) {

                Intent serviceIntent = new Intent(getContext(), MainService.class);
                serviceIntent.setAction(MESSAGE_CONSTANT.SEND_CONNECT);
                serviceIntent.putExtra("roomId", dialog.getId());
                getContext().startService(serviceIntent);

                Intent intent = new Intent(getContext(), MessageListActivity.class);
                intent.putExtra("roomId", dialog.getId());
                startActivity(intent);
            }
        });
        adapter.setOnDialogLongClickListener(new DialogsListAdapter.OnDialogLongClickListener<IDialog>() {
            @Override
            public void onDialogLongClick(IDialog dialog) {
                showDeleteDialog(((DialogList)dialog).getId());

            }
        });
        Observer.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        //EVENT FOR DIALOG LIST

        return inflater.inflate(R.layout.activity_dialog_list,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity v = getActivity();
        dialogList = v.findViewById(R.id.dialogsList);
        dialogList.setAdapter(adapter);
        adapter.clear();
        diaLst.clear();
        Iterator<SugarRoom> rooms = SugarRoom.findAll(SugarRoom.class);
        while (rooms.hasNext()) {
            SugarRoom room = rooms.next();
            ArrayList<IUser> users = new ArrayList<>();
            ArrayList<String> members;
            members=new Gson().fromJson(room.getMembers(),new TypeToken<ArrayList<String>>(){}.getType());
            List<SugarUser> sugarUsers = new ArrayList<SugarUser>() {{
                add(GlobalData.getInstance().getOwner());
            }};
            for(String i:members){
                SugarUser sugarUser=SugarUser.findByName(i);
                if(sugarUser==null) {
                    sugarUser=SugarUser.loadUser(i);
                    if(sugarUser!=null) sugarUser.save();
                }
                if(sugarUser==null){
                    Toast.makeText(getContext(),"Members not exist "+i,Toast.LENGTH_SHORT).show();
                    continue;
                }
                sugarUsers.add(sugarUser);
            }
            for (SugarUser i : sugarUsers) if(i!=null) users.add(i.toUser());

            List<SugarMessage> lstMsg = SugarMessage.findWithQuery(SugarMessage.class, "SELECT * FROM SUGAR_MESSAGE WHERE ROOM_ID = ?  ORDER BY ID DESC LIMIT 1", String.valueOf(room.getId()));
            Message lastMsg = null;
            if (lstMsg.size() > 0) lastMsg = lstMsg.get(0).toMessage();
            long unRead = SugarMessage.count(SugarMessage.class, "READ_AT IS NULL AND ROOM_ID=?", new String[]{String.valueOf(room.getId())});

            if(room.isGroup()) {
                diaLst.add(new DialogList(String.valueOf(room.getId()), "https://cdn4.iconfinder.com/data/icons/business-card-contact/512/Users_Avatar_Team-512.png", room.getName(), users, lastMsg, (int) unRead));
            }
            else{
                SugarUser ownerRoom=SugarUser.findByName(members.get(0));
                diaLst.add(new DialogList(String.valueOf(room.getId()), GlobalData.getInstance().getOwner().getAvatar(), ownerRoom.getDisplayName(), users, lastMsg, (int) unRead));
            }
        }
        adapter.addItems(diaLst);
    }
    public void showDeleteDialog(String id){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Warning!");
        builder.setMessage("Are you want to delete dialog?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SugarRoom room=SugarRoom.findById(SugarRoom.class,Long.parseLong(id));
                room.delete();
                adapter.deleteById(String.valueOf(id));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    @Override
    public void onMessageComing(long id) {
        SugarMessage sugarMessage=SugarMessage.findById(SugarMessage.class,id);
        Message message=sugarMessage.toMessage();
        if(!adapter.updateDialogWithMessage(String.valueOf(sugarMessage.getRoomId()),message)){
            adapter.addItem(adapter.getItemCount(),new DialogList(String.valueOf(sugarMessage.getRoomId()),message.getUser().getAvatar(),((User)message.getUser()).getFullName(),
                    new ArrayList<IUser>(){{add(GlobalData.getInstance().getOwner().toUser());add(message.getUser());}},message,1));
        }
    }

    @Override
    public void onAddDialog(SugarRoom sugarRoom) {
        ArrayList<String> members=new Gson().fromJson(sugarRoom.getMembers(),new TypeToken<ArrayList<String>>(){}.getType());
        SugarUser ownerRoom=SugarUser.findByName(members.get(0));
        ArrayList<IUser> users = new ArrayList<IUser>() {{
            add(GlobalData.getInstance().getOwner().toUser());
            add(ownerRoom.toUser());
        }};
        Message lastMsg = null;
        List<SugarMessage> lstMsg = SugarMessage.findWithQuery(SugarMessage.class, "SELECT * FROM SUGAR_MESSAGE WHERE ROOM_ID = ?  ORDER BY ID DESC LIMIT 1", String.valueOf(sugarRoom.getId()));
        if (lstMsg.size() > 0) lastMsg = lstMsg.get(0).toMessage();
        long unRead = SugarMessage.count(SugarMessage.class, "READ_AT IS NULL AND ROOM_ID=?", new String[]{String.valueOf(sugarRoom.getId())});
        DialogList dialogList=new DialogList(String.valueOf(sugarRoom.getId()), ownerRoom.getAvatar(), ownerRoom.getDisplayName(), users, lastMsg, (int) unRead);
        adapter.addItem(adapter.getItemCount(),dialogList);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("onMsg")){
                onMessageComing(intent.getLongExtra("id",-1));
            }
        }
    };
    IntentFilter intentFilter=new IntentFilter("onMsg");
    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
