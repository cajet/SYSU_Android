package com.example.cajet.text_contact;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;


class ContactListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Map<String, Object>> list;
    ContactListAdapter(Context context, ArrayList<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.user_image = (ImageView) view.findViewById(R.id.user_image);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_mobile = (TextView) view.findViewById(R.id.tv_mobile);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        String path = list.get(position).get("imagePath").toString();
        int imageId = (int)list.get(position).get("imageId");
        if (!path.equals("")) {
            if ((new File(path)).exists())
                viewHolder.user_image.setImageURI(Uri.parse(path));
            else
                viewHolder.user_image.setImageResource(R.drawable.icon1);
        } else {
            viewHolder.user_image.setImageResource(imageId);
        }
        viewHolder.tv_name.setText(list.get(position).get("name").toString());
        viewHolder.tv_mobile.setText(list.get(position).get("mobile").toString());
        return view;
    }

    private class ViewHolder {
        ImageView user_image;
        TextView tv_name;
        TextView tv_mobile;
    }
}
