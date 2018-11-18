package com.example.tuankiet.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuankiet.myapp.chatorm.SugarRoom;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.fragment.AddGroupFragment;
import com.example.tuankiet.myapp.fragment.CircleTransform;
import com.example.tuankiet.myapp.fragment.DialogListFragment;
import com.example.tuankiet.myapp.fragment.FriendListFragment;
import com.example.tuankiet.myapp.voicecall.ReceiveCallActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import michat.GlobalData;

import static com.example.tuankiet.myapp.MessageListActivity.EXTRA_CONTACT;

public class TabActivity extends AppCompatActivity {

    private String[] tabs = {"Tin nhắn", "Hoạt động", "Khác"};
    public static int[] resourceIds = {
            R.layout.activity_dialog_list
            ,R.layout.activity_friend_list
            ,R.layout.activity_add_group
    };

    private PagerAdapter adapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        addControl();

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_tab_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        ImageView imageView=findViewById(R.id.user_avatar);
        Transformation transformation = new CircleTransform();
        Picasso.get().load(GlobalData.getInstance().getOwner().getAvatar())
                .fit()
                .transform(transformation)
                .into(imageView);
        TextView userDisplay=findViewById(R.id.user_display_name);
        userDisplay.setText(GlobalData.getInstance().getOwner().getDisplayName());
        TextView email=findViewById(R.id.email);
        email.setText(GlobalData.getInstance().getOwner().getName()+"@gmail.com");
        ImageView logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TabActivity.this,MainActivity.class);
                SugarUser.findOwner().delete();
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void addControl(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(adapter);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 0:
                        fab.hide();
                        break;
                    case 1:
                        fab.hide();
                        break;
                    case 2:
                        fab.show();
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setOffscreenPageLimit(3);
        tabLayout=findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tab, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        EditText editText = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        editText.setHintTextColor(getResources().getColor(R.color.light_gray));
        editText.setTextColor(getResources().getColor(R.color.black));

        ImageView icon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        icon.setColorFilter(Color.BLACK);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
        }

        return super.onOptionsItemSelected(item);
    }



    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment frag=null;
            switch (position){
                case 0: frag= new DialogListFragment();
                this.notifyDataSetChanged();
                break;
                case 1: frag= new FriendListFragment();
                break;
                case 2: frag= new AddGroupFragment();
                break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }
    public final static String EXTRA_CONTACT = "michat.CallOut.CONTACT";
    public final static String EXTRA_IP = "michat.CallOut.IP";
    BroadcastReceiver receiverCall = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("onCall")){
                Intent mintent = new Intent(TabActivity.this, ReceiveCallActivity.class);
                mintent.putExtra(EXTRA_CONTACT, intent.getStringExtra(EXTRA_CONTACT));
                mintent.putExtra(EXTRA_IP, intent.getStringExtra(EXTRA_IP));
                startActivity(mintent);
            }
        }
    };
    IntentFilter filterCall=new IntentFilter("onCall");
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverCall,filterCall);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverCall);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverCall,filterCall);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverCall);
    }
}
