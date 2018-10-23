package michat.localDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageCountDatabaseHandler extends SQLiteOpenHelper {
    private final static String DATABASE_NAME="chat";
    private final static String KEY_COUNT="count";
    private final static String TABLE_NAME="message_count";
    private final static int DATABASE_VERSION=4;
    public MessageCountDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userTable=String.format("CREATE TABLE %s (%s INTEGER)",TABLE_NAME,KEY_COUNT);
        db.execSQL(userTable);
        String sql="INSERT INTO "+TABLE_NAME+" VALUES('0')";
        db.execSQL(sql);
    }
    public void createTable(){
        String userTable=String.format("CREATE TABLE %s (%s INTEGER)",TABLE_NAME,KEY_COUNT);
        getWritableDatabase().execSQL(userTable);
        String sql="INSERT INTO "+TABLE_NAME+" VALUES('0')";
        getWritableDatabase().execSQL(sql);
    }
    public int getCount(){
        SQLiteDatabase db=this.getReadableDatabase();
        String sql="SELECT * from "+TABLE_NAME;
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor==null){
            onCreate(getWritableDatabase());
            return 0;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public void addCount(){
        String sql="update message_count set count=count+1";
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropUserTable=String.format("DROP TABLE IF EXISTS %s",TABLE_NAME);
        db.execSQL(dropUserTable);
        onCreate(db);
    }
}
