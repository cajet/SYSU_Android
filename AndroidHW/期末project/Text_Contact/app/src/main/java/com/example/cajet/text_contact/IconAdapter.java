package com.example.cajet.text_contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {
    private ArrayList<Bitmap> iconList;
    private LayoutInflater mInflater;
    interface OnItemClickListener {
        void onItemClick(View view, int position, Bitmap item);
    }
    private OnItemClickListener mOnItemClickListener;
    void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    IconAdapter(Context context, ArrayList<Bitmap> items) {
        super();
        iconList = items;
        mInflater = LayoutInflater.from(context);
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
        ImageView icon;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =  mInflater.inflate(R.layout.gallery_item, viewGroup, false);
        ViewHolder holder =  new ViewHolder(view);
        holder.icon = (ImageView) view.findViewById(R.id.image);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int pos = i % iconList.size();
        viewHolder.icon.setImageBitmap(iconList.get(pos));
        if (mOnItemClickListener !=  null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView, pos, iconList.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
