package com.example.mxx62.findit;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mxx62 on 2017/8/14.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder1>{
    private final List<Map<String, ?>> restaurantList;
    private final Context my_context;
    static OnRecyclerViewItemClickListener itemClickListener;

    public RecyclerAdapter(List<Map<String, ?>> list, Context my_context) {
        this.my_context = my_context;
        this.restaurantList = list;
    }

    @Override
    public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);//
        return new ViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder1 holder, int position) {
        if (restaurantList.get(position).get("url").toString() != " ") {
            try{
                Picasso.with(my_context).load(String.valueOf(restaurantList.get(position).get("url"))).into(holder.card_pic);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        holder.card_title.setText((String) restaurantList.get(position).get("name"));
        holder.card_bar.setRating(Float.parseFloat(restaurantList.get(position).get("rate").toString()));
        //holder.card_rate.setText(restaurantList.get(position).get("rate").toString());
        holder.card_phone.setText(restaurantList.get(position).get("phone").toString());
        holder.card_address.setText(restaurantList.get(position).get("address").toString());
        holder.card_url.setText(restaurantList.get(position).get("url").toString());


    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public interface OnRecyclerViewItemClickListener{
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
        public void onFavorClick(View view, int position);
        public void onOverflowMenuClick (View v, int position);
        public void onDisClick (View view, int position);
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        public ImageView card_pic;
        public TextView card_title;
        public RatingBar card_bar;
        public TextView card_rate;
        public TextView card_phone;
        public TextView card_address;
        public TextView card_url;
        public ImageView card_favor;
        public ImageView card_dis;
        public ViewHolder1(final View itemView) {
            super(itemView);

            card_pic = (ImageView) itemView.findViewById(R.id.cardViewImg);
            card_title = (TextView) itemView.findViewById(R.id.cardViewTitle);
            card_bar = (RatingBar)itemView.findViewById(R.id.cardViewBar);
            card_rate = (TextView) itemView.findViewById(R.id.cardViewRate);
            card_phone = (TextView) itemView.findViewById(R.id.cardViewPhone);
            card_address = (TextView) itemView.findViewById(R.id.cardViewAddress);
            card_url = (TextView)itemView.findViewById(R.id.cardViewUrl);
            card_favor = (ImageView)itemView.findViewById(R.id.Favorite);
            card_dis = (ImageView)itemView.findViewById(R.id.Dislike);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

            if (card_favor != null){
                card_favor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null){
                            itemClickListener.onFavorClick(v,getAdapterPosition());
                        }
                    }
                });
            }

            if (card_dis != null){
                card_dis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null){
                            itemClickListener.onDisClick(v,getAdapterPosition());
                        }
                    }
                });
            }


        }
    }

    public void setOnItemClickListener (final OnRecyclerViewItemClickListener listener){
        this.itemClickListener = listener;
    }

}
