package com.example.tuankiet.myapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.service.MainService;
import com.example.tuankiet.myapp.voicecall.ContactManager;
import com.example.tuankiet.myapp.voicecall.MainActivity2;
import com.example.tuankiet.myapp.voicecall.ReceiveCallActivity;
import com.google.gson.Gson;
import com.orm.SugarContext;
import com.orm.SugarDb;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import michat.FileUtils;
import michat.GlobalData;
import michat.localDB.MessageCountDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.MESSAGE;
import michat.model.User;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button loginBtn=findViewById(R.id.email_sign_in_button);
        SugarDb db=new SugarDb(this);
        db.onCreate(db.getDB());
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView txtEmail=findViewById(R.id.email);
                EditText txtPassword=findViewById(R.id.password);
                String username=txtEmail.getText().toString();
                String password=txtPassword.getText().toString();
                SugarContext.init(getApplicationContext());
                boolean login=true;
                login=SugarUser.LoginUser(username,password);

                if(login) {
                    Log.d("token",Constant.TOKEN);
                    SugarUser usr = SugarUser.findByName(username);
                    if (usr == null || !usr.getRole().equals("owner")) {
                        SugarMessage.deleteAll(SugarMessage.class);
                        SugarRoom.deleteAll(SugarRoom.class);
                        SugarUser.deleteAll(SugarUser.class);
                        usr = SugarUser.getUser(username);
                        usr.save();
                        usr.setRole("owner");
                        GlobalData.getInstance().setOwner(usr);
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }
                    Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
                    serviceIntent.setAction(MESSAGE.SEND_INIT_SESSION);
                    startService(serviceIntent);
                    Constant.ip = Constant.getIP(getApplicationContext());
                    requestRecordAudioPermission();
                    Intent intent = new Intent(MainActivity.this, TabActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
//                    SugarUser sugarUser=SugarUser.findByName(txtEmail.getText().toString());
//                    if(sugarUser==null){
//                        sugarUser=new SugarUser(txtEmail.getText().toString(),"https://ssl.gstatic.com/images/branding/product/1x/avatar_circle_blue_512dp.png",txtEmail.getText().toString(),"owner","Nam","1/1/1998");
//                        sugarUser.save();
//                    }
//                    GlobalData.getInstance().setOwner(sugarUser);
//                    Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
//                    serviceIntent.setAction(MESSAGE.SEND_INIT_SESSION);
//                    startService(serviceIntent);
//                    Constant.ip = Constant.getIP(getApplicationContext());
//                    requestRecordAudioPermission();
//                    Intent intent = new Intent(MainActivity.this, TabActivity.class);
//                    startActivity(intent);
            }
        });
    }
    private void requestRecordAudioPermission() {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (this.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {

        } else {

            Toast.makeText(this, "This app needs to record audio through the microphone....", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{requiredPermission}, 101);
        }


    }
}
