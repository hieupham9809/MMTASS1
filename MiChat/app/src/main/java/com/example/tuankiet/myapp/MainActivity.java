package com.example.tuankiet.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import michat.FileUtils;
import michat.GlobalData;
import michat.localDB.MessageCountDatabaseHandler;
import michat.localDB.MessageDatabaseHandler;
import michat.localDB.UserDatabaseHandler;
import michat.model.MESSAGE;
import michat.model.User;

public class MainActivity extends AppCompatActivity {
    private UserDatabaseHandler udh=new UserDatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button loginBtn=findViewById(R.id.email_sign_in_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView txtEmail=findViewById(R.id.email);


                ///IF LOGIN FILE IS EXIST PASSOVER LOGIN TASK
                if(!FileUtils.fileExist(MainActivity.this,txtEmail.getText().toString())){
                    ///
                    ///     PERFORM LOGIN TASK
                    ///
                    ///create table user
                    ///insert owner user
                    ///create login file
                }
                User owner=null;
                String user=txtEmail.getText().toString();
                if(udh.getUser(user)==null) {
                    if (user.equals("tuankiet")) {
                        udh.addUser(new User("1", "tuankiet", "Luong Tuan Kiet", "https://www.w3schools.com/howto/img_avatar.png", "owner", "30/04/1997", "Nam"));
                    } else if (user.equals("hieu")) {
                        udh.addUser(new User("2", "hieu", "Pham Minh Hieu", "https://i.imgur.com/I80W1Q0.png", "owner", "30/04/1997", "Nam"));
                    } else if (user.equals("huy")) {
                        udh.addUser(new User("3", "huy", "Nguyen Khac Quang Huy", "http://www.zoomyourtraffic.com/wp-content/uploads/avatar-4.png", "owner", "30/04/1997", "Nam"));
                    }
                }
                try {
                    owner = udh.getUser(txtEmail.getText().toString());
                }
                catch (Exception e){

                }
                if(!udh.isTableExist("message_count")){
                    new MessageCountDatabaseHandler(MainActivity.this).createTable();
                }
//                if(owner==null) {
//                    getApplicationContext().deleteDatabase("chat");
//                    MessageDatabaseHandler mdh=new MessageDatabaseHandler(MainActivity.this);
//                    MessageCountDatabaseHandler mcdh=new MessageCountDatabaseHandler(MainActivity.this);
//                    mcdh.getCount();
//                    udh=new UserDatabaseHandler(MainActivity.this);
//                    owner = new User("1", txtEmail.getText().toString(), "Luong Tuan Kiet", "https://www.w3schools.com/howto/img_avatar.png", "owner", "30/04/1997", "Nam");
//                    udh.addUser(owner);
//                }
                GlobalData.getInstance().setOwner(owner);
                Toast.makeText(MainActivity.this,owner.getName(),Toast.LENGTH_SHORT).show();
                Intent serviceIntent=new Intent(MainActivity.this,MainService.class);
                serviceIntent.setAction(MESSAGE.SEND_INIT_SESSION);
                serviceIntent.putExtra("username",txtEmail.getText().toString());
                startService(serviceIntent);
                Intent intent = new Intent(MainActivity.this, TabActivity.class);
                startActivity(intent);

            }
        });
    }

}
