package com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tuankiet.myapp.R;
import com.example.tuankiet.myapp.chatorm.SugarMessage;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrimAdapter extends BaseAdapter{
    private ArrayList<SugarMessage> lst;
    private LayoutInflater layoutInflater;
    private Context context;

    public TrimAdapter(Context aContext,  List<SugarMessage> listData) {
        this.context = aContext;
        this.lst = (ArrayList<SugarMessage>) listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return lst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_message_trim, null);
            holder = new TrimAdapter.ViewHolder();
            holder.flagView=(CircleImageView)convertView.findViewById(R.id.avatar) ;
            holder.trimMessage=(TextView)convertView.findViewById(R.id.trim_item);
            convertView.setTag(holder);
        } else {
            holder = (TrimAdapter.ViewHolder) convertView.getTag();
        }

        SugarMessage country = this.lst.get(position);
        holder.trimMessage.setText(country.getText());
        Picasso.get().load(SugarUser.findById(SugarUser.class,country.getOwner()).getAvatar()).into(holder.flagView);
        return convertView;
    }
    static class ViewHolder {
        CircleImageView flagView;
        TextView trimMessage;
    }
}
