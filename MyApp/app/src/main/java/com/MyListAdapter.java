package com;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuankiet.myapp.R;
import com.example.tuankiet.myapp.UserAdapter;
import com.example.tuankiet.myapp.chatorm.SugarUser;
import com.example.tuankiet.myapp.service.Clients;
import com.example.tuankiet.myapp.service.ThreadServer;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyListAdapter extends BaseAdapter {
    private List<UserAdapter> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public MyListAdapter(Context aContext,  List<UserAdapter> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_user, null);
            holder = new ViewHolder();
            holder.flagView = (CircleImageView) convertView.findViewById(R.id.imageView_flag);
            holder.countryNameView = (TextView) convertView.findViewById(R.id.textView_countryName);
            holder.populationView = (TextView) convertView.findViewById(R.id.textView_population);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserAdapter country = this.listData.get(position);
        holder.countryNameView.setText(country.getDisplayName());
        holder.populationView.setText(country.getStatus());
        Picasso.get().load(country.getAvatar()).into(holder.flagView);

        return convertView;
    }


    static class ViewHolder {
        CircleImageView flagView;
        TextView countryNameView;
        TextView populationView;
    }
}
