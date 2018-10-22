package michat;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.*;

public class FileUtils {
    public static void createFile(Context ctx,String fileName,String content){

        FileOutputStream outputStream = null;
        try {
            outputStream = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean fileExist(Context ctx,String fname){
        File file = ctx.getFileStreamPath(fname);
        return file.exists();
    }
}
