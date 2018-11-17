package michat.cloudDB;
import android.app.DownloadManager;
import android.os.AsyncTask;
import android.util.Log;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import michat.model.Message;
import michat.model.User;

public class CloudDatabase implements IDatabase {

    @Override
    public ArrayList<User> getAllFriends() {
        RequestParams params=new RequestParams();
        params.put("apiKey",HttpUtils.API_KEY);
        HttpUtils.get("databases/chat/collections/friend_info", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        return null;
    }

    @Override
    public User getFriend(String id) {
        return null;
    }

    @Override
    public ArrayList<User> getUsersChatWith() {
        return null;
    }

    @Override
    public ArrayList<Message> getMessage(String idUserChatWith, int limit) {
        return null;
    }
}