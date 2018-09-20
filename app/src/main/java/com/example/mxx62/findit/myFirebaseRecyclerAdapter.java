package com.example.mxx62.findit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mxx62 on 2017/8/18.
 */

public class myFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<listItem, myFirebaseRecyclerAdapter.myViewHolder>{

    private Context mContext;
    static OnRecyclerViewItemClickListener itemClickListener;

    public myFirebaseRecyclerAdapter(Class<listItem> modelClass, int modelLayout,
                                     Class<myViewHolder> holder, DatabaseReference ref, Context context) {
        super(modelClass, modelLayout, holder, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(myViewHolder viewHolder, listItem model, int position) {
        viewHolder.card_title.setText(model.getname());
        viewHolder.card_address.setText(model.getaddress());
        viewHolder.card_phone.setText(model.getphone());
        viewHolder.card_rate.setText(model.getrate());
        viewHolder.card_url.setText(model.geturl());
     //   viewHolder.card_bar.setRating((float) Double.parseDouble(model.getrate()));
        if (model.geturl() != null) {
            try {
                Picasso.with(mContext).load(model.geturl()).into(viewHolder.card_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else return;
    }

    public interface OnRecyclerViewItemClickListener{
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
        public void onOverflowMenuClick (View v, int position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {

        public ImageView card_pic;
        public TextView card_title;
        public RatingBar card_bar;
        public TextView card_rate;
        public TextView card_phone;
        public TextView card_address;
        public TextView card_url;
      //  public ImageView card_favor;

        public myViewHolder(View itemView) {
            super(itemView);
            card_pic = (ImageView) itemView.findViewById(R.id.cardViewImg);
            card_title = (TextView) itemView.findViewById(R.id.cardViewTitle);
            card_bar = (RatingBar)itemView.findViewById(R.id.cardViewBar);
            card_rate = (TextView) itemView.findViewById(R.id.cardViewRate);
            card_phone = (TextView) itemView.findViewById(R.id.cardViewPhone);
            card_address = (TextView) itemView.findViewById(R.id.cardViewAddress);
            card_url = (TextView)itemView.findViewById(R.id.cardViewUrl);
       //     card_favor = (ImageView)itemView.findViewById(R.id.Favorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }
}
