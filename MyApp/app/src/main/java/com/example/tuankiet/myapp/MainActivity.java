package com.example.tuankiet.myapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.service.MainService;
import com.orm.SugarContext;
import com.orm.SugarDb;

import michat.GlobalData;
import michat.model.MESSAGE_CONSTANT;

public class MainActivity extends AppCompatActivity {
    EditText _emailText;
    EditText _passwordText;
    TextView _loginLink;
    TextView _signupLink;
    TextView _loginButton;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SugarDb db=new SugarDb(this);
        db.onCreate(db.getDB());
        _emailText=findViewById(R.id.input_email);
        _passwordText=findViewById(R.id.input_password);
        _loginButton=findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

//        _signupLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
//            }
//        });
    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        SugarContext.init(getApplicationContext());
        boolean login=true;
        login=SugarUser.LoginUser(email,password);
        int acong=email.indexOf('@');
        String name=email.substring(0,acong);
        if(login) {
            SugarUser usr = SugarUser.findByName(name);
            if (usr == null || !usr.getRole().equals("owner")) {
                SugarMessage.deleteAll(SugarMessage.class);
                SugarRoom.deleteAll(SugarRoom.class);
                SugarUser.deleteAll(SugarUser.class);
                usr = SugarUser.loadOwner();
                usr.setRole("owner");
                usr.save();
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            }
            GlobalData.getInstance().setOwner(usr);
            Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
            serviceIntent.setAction(MESSAGE_CONSTANT.SEND_INIT_SESSION);
            startService(serviceIntent);
            Constant.ip = Constant.getIP(getApplicationContext());
            requestRecordAudioPermission();
            onLoginSuccess();;

        }
        else {
            onLoginFailed();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(MainActivity.this, TabActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    private void requestRecordAudioPermission() {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (this.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {

        } else {

            Toast.makeText(this, "This app needs to record audio through the microphone....", Toast.LENGTH_SHORT).show();
            //requestPermissions(new String[]{requiredPermission}, 101);
        }


    }
}
