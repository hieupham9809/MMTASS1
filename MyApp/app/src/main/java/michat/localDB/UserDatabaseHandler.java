package michat.localDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import michat.model.User;


public class UserDatabaseHandler extends SQLiteOpenHelper{
    //kiet
    //id    username fullname avatar role ngaysinh gioitinh Ip Port
    //
    private static final String DATABASE_NAME="chat";
    private static final int DATABASE_VERSION=4;
    private  static final String TABLE_NAME="user";
    private static final String KEY_ID="id";
    private static final String KEY_USERNAME="username";
    private static final String KEY_FULLNAME="fullname";
    private static final String KEY_AVATAR="avatar";
    private static final String KEY_ROLE="role";
    private static final String KEY_NGAYSINH="ngaysinh";
    private static final String KEY_GIOITINH="gioitinh";
    private Context mContext;
    public UserDatabaseHandler(Context context){

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.mContext=context;
//        try {
//            File dbFile=mContext.getDatabasePath("/data/data/com.example.tuankiet.myapp/databases/chat");
//            if(dbFile.exists()) return;
//            Toast.makeText(context,"Make new database",Toast.LENGTH_SHORT).show();
//            InputStream myInput = mContext.getAssets().open("chat");
//            OutputStream myOutput = new FileOutputStream("/data/data/com.example.tuankiet.myapp/databases/chat");
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = myInput.read(buffer)) > 0) {
//                myOutput.write(buffer, 0, length);
//            }


//            myOutput.flush();
//            myOutput.close();
//            myInput.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
    @Override
    public void onCreate(SQLiteDatabase db){
       String userTable=String.format("CREATE TABLE %s(%s TEXT PRIMARY KEY,%s TEXT, %s TEXT,%s TEXT,%s TEXT, %s TEXT,%s TEXT)",
                TABLE_NAME,KEY_ID,KEY_USERNAME,KEY_FULLNAME,KEY_AVATAR,KEY_ROLE,KEY_NGAYSINH,KEY_GIOITINH);
       db.execSQL(userTable);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String dropUserTable=String.format("DROP TABLE IF EXISTS %s",TABLE_NAME);
        db.execSQL(dropUserTable);
        onCreate(db);
    }
    public void addUser(User user){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        int id=getAllUser().size();
        values.put(KEY_ID,String.valueOf(id+1));
        values.put(KEY_USERNAME,user.getName());
        values.put(KEY_FULLNAME,user.getFullName());
        values.put(KEY_AVATAR,user.getAvatar());
        values.put(KEY_ROLE,user.getRole());
        values.put(KEY_NGAYSINH,user.getNgaySinh());
        values.put(KEY_GIOITINH,user.getGioiTinh());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }
    public User getUser(String userId){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,KEY_USERNAME+"= ?",new String[]{userId},null,null,null);
        if(cursor!=null)
            cursor.moveToFirst();
        else return null;
        if(cursor.getCount()==0) return null;
        User user=new User(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID))),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
        return user;

    }
    public User getUserWithId(String userId){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,KEY_ID+"= ?",new String[]{userId},null,null,null);
        if(cursor!=null)
            cursor.moveToFirst();
        else return null;
        if(cursor.getCount()==0) return null;
        User user=new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
        return user;
    }
    public List<User> getAllUser(){
        List<User> userList=new ArrayList<>();
        String query="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            User user=new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
            userList.add(user);
            cursor.moveToNext();
        }
        return userList;
    }
    public void updateUser(User user){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_ID,user.getId());
        values.put(KEY_USERNAME,user.getName());
        values.put(KEY_FULLNAME,user.getFullName());
        values.put(KEY_AVATAR,user.getAvatar());
        values.put(KEY_ROLE,user.getRole());
        values.put(KEY_NGAYSINH,user.getNgaySinh());
        values.put(KEY_GIOITINH,user.getGioiTinh());
        db.update(TABLE_NAME,values,KEY_ID+" = ?",new String[]{user.getId()});
        db.close();
    }
    public void deleteUser(String userId){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+" =? ",new String[]{userId});
        db.close();
    }
    public void deleteAllUser(){
        SQLiteDatabase db=this.getWritableDatabase();;
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }
}
