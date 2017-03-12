package com.example.cajet.text_contact.news;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cajet.text_contact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;



class NewsAdapter extends RecyclerView.Adapter {
    private final int TYPE_NORMAL = 0;
    private final int TYPE_FOOT = 1;
    private Context mContext;
    private List<NewsItem> mData  = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private boolean isLoading;
    private String mError = null;


    NewsAdapter(Context context) {
        this.mContext = context;
    }

    void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }
    void addData(List<NewsItem> data){
        mData.addAll(data);
    }

    void setData(List<NewsItem> data){
        mData.clear();
        mData.addAll(data);
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT;
        }
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_listview_item, parent, false);
            return new ItemViewHolder(view);
        }
        if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_footer, parent, false);
            return new FootHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).title.setText(mData.get(position).getTitle());
            ((ItemViewHolder) holder).content.setText(mData.get(position).getContent());
            ((ItemViewHolder) holder).date.setText(mData.get(position).getDate());
            if (mData.get(position).getImgLink() != null) {
                ((ItemViewHolder) holder).icon.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(mData.get(position).getImgLink()).placeholder(R.mipmap.news_default_icon)
                        .error(R.mipmap.news_default_icon).into(((ItemViewHolder) holder).icon);
            }else{
                ((ItemViewHolder) holder).icon.setVisibility(View.GONE);
            }
            // 如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }

        }
        if (holder instanceof FootHolder) {
            ((FootHolder) holder).foot.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (mError != null) {
                //((FootHolder) holder).progressBar.setVisibility(View.GONE);
                ((FootHolder) holder).message.setText(mError);
            } else {
                //((FootHolder) holder).progressBar.setVisibility(View.VISIBLE);
                ((FootHolder) holder).message.setText("加载中....");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size()+1;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView content;
        TextView date;

        ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.id_newsItem_icon);
            title = (TextView) itemView.findViewById(R.id.id_newsItem_title);
            content = (TextView) itemView.findViewById(R.id.id_newItem_content);
            date = (TextView) itemView.findViewById(R.id.id_newsItem_date);
        }
    }

    private class FootHolder extends RecyclerView.ViewHolder {
        LinearLayout foot;
        //ProgressWheel progressBar;
        TextView message;

        FootHolder(View itemView) {
            super(itemView);
            foot = (LinearLayout) itemView.findViewById(R.id.item_news_foot);
            //progressBar = (ProgressWheel) itemView.findViewById(R.id.item_news_progressbar);
            message = (TextView) itemView.findViewById(R.id.item_news_message);
        }
    }


    void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

    }

    List<NewsItem> getData() {
        return mData;
    }

    void setMError(String mError) {
        this.mError = mError;
    }
}
