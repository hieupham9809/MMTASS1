package com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuankiet.myapp.R;
import com.example.tuankiet.myapp.UserAdapter;
import com.example.tuankiet.myapp.service.Clients;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends BaseAdapter implements Filterable{
    private ArrayList<UserAdapter> listData;
    private ArrayList<UserAdapter> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public GroupAdapter(Context aContext,  ArrayList<UserAdapter> listData) {
        this.context = aContext;
        this.listData = listData;
        data=(ArrayList)listData.clone();
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
        MyListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_user, null);
            holder = new MyListAdapter.ViewHolder();
            holder.flagView = (CircleImageView) convertView.findViewById(R.id.imageView_flag);
            holder.countryNameView = (TextView) convertView.findViewById(R.id.textView_countryName);
            holder.populationView = (TextView) convertView.findViewById(R.id.textView_population);
            convertView.setTag(holder);
        } else {
            holder = (MyListAdapter.ViewHolder) convertView.getTag();
        }

        UserAdapter country = this.listData.get(position);
        holder.countryNameView.setText(country.getDisplayName());
        holder.populationView.setText(country.getStatus());
        Picasso.get().load(country.getAvatar()).into(holder.flagView);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listData.clear();
                listData.addAll((ArrayList<UserAdapter>)results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<UserAdapter> FilteredArrayNames = new ArrayList<UserAdapter>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < data.size(); i++) {
                    UserAdapter dataNames = data.get(i);
                    if (dataNames.getDisplayName().toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }
        };

        return filter;
    }


    static class ViewHolder {
        CircleImageView flagView;
        TextView countryNameView;
        TextView populationView;
    }
}
