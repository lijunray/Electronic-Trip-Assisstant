package com.nyu.cs9033.eta.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.Person;


public class ListViewAdapter extends BaseAdapter {
    private List<Person> friends;
    private static HashMap<Integer, Boolean> isSelected;
    private Context context;
    private LayoutInflater inflater = null;

    public ListViewAdapter(List<Person> friends, Context context) {
        this.context = context;
        this.friends = friends;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }

    private void initDate() {
        for (int i = 0; i < friends.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.name);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Person friend = friends.get(position);
        holder.tv.setText(friend.getName());
        holder.cb.setChecked(getIsSelected().get(position));
        if (getIsSelected().get(position)) {
            convertView.setBackground(context.getResources().getDrawable(R.drawable.item_background));
        }
        else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.create_background));
        }
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ListViewAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tv;
        public CheckBox cb;
    }
}  