package michat.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import michat.model.Message;
import michat.model.User;


public class MessageDatabaseHandler extends SQLiteOpenHelper{
    private static final String DATABASE_NAME="chat";
    private static final int DATABASE_VERSION=4;
    private Context mContext;
    //Hieu
    //id    owner   msg createdAt   receivedAt
    //
    private String TABLE_NAME="message";
    private static final String KEY_ID="id";
    private static final String KEY_OWNER="owner";
    private static final String KEY_MSG="msg";
    private static final String KEY_CREATED="createdAt";
    private static final String KEY_RECEIVED="receivedAt";
    private static final String KEY_READ="readAt";


    public String getTableName(){
        return TABLE_NAME;
    }
    public void setTableName(String table){
        this.TABLE_NAME=table;
    }
    public void createTable(){
        SQLiteDatabase ourDatabase=this.getWritableDatabase();
        /*then call 'execSQL()' on it. Don't forget about using TableName Variable as tablename.*/
        String sql=String.format("CREATE TABLE IF NOT EXISTS %s(%s TEXT PRIMARY KEY,%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                TABLE_NAME,KEY_ID,KEY_MSG,KEY_OWNER,KEY_CREATED,KEY_RECEIVED,KEY_READ);//change or der!!!!!!
        ourDatabase.execSQL(sql);
    }
    public void deleteTable(String tableName){
        SQLiteDatabase ourDatabase=this.getWritableDatabase();
        String sql="DROP TABLE "+tableName;
        ourDatabase.execSQL(sql);
    }
    public MessageDatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.mContext=context;
    }
    public boolean isTableExist(String tableName){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name='"+tableName+"'",null);
        if(cursor!=null){
            if(cursor.getCount()>0){
                cursor.close();
                return true;
            }
        }
        return false;
    }
    public void addMessage(Message msg){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_ID,msg.getId());
        values.put(KEY_OWNER,msg.getUser().getName());
        values.put(KEY_MSG,msg.getText());
        values.put(KEY_CREATED,msg.getCreatedAt().getTime());
        if(msg.getReceivedAt()==null)
            values.put(KEY_RECEIVED,"");
        else values.put(KEY_RECEIVED,msg.getReceivedAt().getTime());
        if(msg.getReadAt()==null)
            values.put(KEY_READ,"");
        else values.put(KEY_READ,msg.getReadAt().getTime());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }
    public Message getMessage(String msgId){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,KEY_ID+"= ?",new String[]{msgId},null,null,null);
        if(cursor!=null)
            cursor.moveToFirst();
        else return null;
        if(cursor.getCount()==0) return null;
        User user=new UserDatabaseHandler(mContext).getUser(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
        Date receive=cursor.getString(cursor.getColumnIndex(KEY_RECEIVED)).equals("")?null:new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_RECEIVED))));
        Date read=null;
        if(cursor.getString(cursor.getColumnIndex(KEY_READ))!=null)
        {
            if(!cursor.getString(cursor.getColumnIndex(KEY_READ)).equals(""))
                read=new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_READ))));
        }
        Message msg=new Message(cursor.getString(cursor.getColumnIndex(KEY_ID)),cursor.getString(cursor.getColumnIndex(KEY_MSG)),user,new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CREATED)))),receive,read);
        return msg;

    }
    public List<Message> getAllMessage(){
        List<Message> msgList=new ArrayList<>();
        String query="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            User user=new UserDatabaseHandler(mContext).getUser(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
            Date receive=cursor.getString(cursor.getColumnIndex(KEY_RECEIVED)).equals("")?null:new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_RECEIVED))));
            Date read=null;
            if(cursor.getString(cursor.getColumnIndex(KEY_READ))!=null)
            {
                if(!cursor.getString(cursor.getColumnIndex(KEY_READ)).equals(""))
                read=new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_READ))));
            }
            Message msg=new Message(cursor.getString(cursor.getColumnIndex(KEY_ID)),cursor.getString(cursor.getColumnIndex(KEY_MSG)),user,new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CREATED)))),receive,read);
            msgList.add(msg);
            cursor.moveToNext();
        }
        return msgList;
    }
    public List<Message> getUnreadMessage(){
        List<Message> msgList=new ArrayList<>();
        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_READ+ " is NULL OR "+KEY_READ +"= ''";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            User user=new UserDatabaseHandler(mContext).getUser(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
            Date receive=cursor.getString(cursor.getColumnIndex(KEY_RECEIVED)).equals("")?null:new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_RECEIVED))));
            Date read=null;
            if(cursor.getString(cursor.getColumnIndex(KEY_READ))!=null)
            {
                if(!cursor.getString(cursor.getColumnIndex(KEY_READ)).equals(""))
                    read=new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_READ))));
            }
            Message msg=new Message(cursor.getString(cursor.getColumnIndex(KEY_ID)),cursor.getString(cursor.getColumnIndex(KEY_MSG)),user,new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CREATED)))),receive,read);
            msgList.add(msg);
            cursor.moveToNext();
        }
        return msgList;
    }
    public Message getLastMessage(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1",TABLE_NAME,KEY_RECEIVED),null);
        if(cursor!=null)
            cursor.moveToFirst();
        else return null;
        if(cursor.getCount()==0) return null;
        User user=new UserDatabaseHandler(mContext).getUser(cursor.getString(cursor.getColumnIndex(KEY_OWNER)));
        Date receive=cursor.getString(cursor.getColumnIndex(KEY_RECEIVED)).equals("")?null:new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_RECEIVED))));
        Date read=null;
        if(cursor.getString(cursor.getColumnIndex(KEY_READ))!=null)
        {
            if(!cursor.getString(cursor.getColumnIndex(KEY_READ)).equals(""))
                read=new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_READ))));
        }
        Message msg=new Message(cursor.getString(cursor.getColumnIndex(KEY_ID)),cursor.getString(cursor.getColumnIndex(KEY_MSG)),user,new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CREATED)))),receive,read);
        return msg;
    }
    public List<String> getAllFriendsChatWith(){
        SQLiteDatabase db=this.getWritableDatabase();
        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = db.rawQuery("select distinct owner from "+TABLE_NAME+" WHERE owner <> 'owner'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add(c.getString(0));
                c.moveToNext();
            }
        }
        return arrTblNames;
    }
    public void updateMessage(Message msg){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_OWNER,msg.getUser().getName());
        values.put(KEY_ID,msg.getId());
        values.put(KEY_CREATED,msg.getCreatedAt().getTime());
        values.put(KEY_MSG,msg.getText());
        if(msg.getReceivedAt()==null)
            values.put(KEY_RECEIVED,"");
        else values.put(KEY_RECEIVED,msg.getReceivedAt().getTime());
        if(msg.getReadAt()==null)
            values.put(KEY_READ,"");
        else values.put(KEY_READ,msg.getReadAt().getTime());
        db.update(TABLE_NAME,values,KEY_ID+" = ?",new String[]{msg.getId()});
        db.close();
    }
    public void deleteMessage(String msgId){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+" =? ",new String[]{msgId});
        db.close();
    }
    public void deleteAllMessage(){
        SQLiteDatabase db=this.getWritableDatabase();;
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
