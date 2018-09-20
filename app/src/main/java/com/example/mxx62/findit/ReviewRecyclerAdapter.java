package com.example.mxx62.findit;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mxx62 on 2017/8/18.
 */

public class ReviewRecyclerAdapter extends FirebaseRecyclerAdapter<ReviewItem, ReviewRecyclerAdapter.reViewHolder> {

    private Context mContext;
    static OnReviewListItemClickListener itemClickListener;

    public ReviewRecyclerAdapter(Class<ReviewItem> modelClass, int modelLayout,
                                     Class<reViewHolder> holder, DatabaseReference ref, Context context) {
        super(modelClass, modelLayout, holder, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(reViewHolder viewHolder, ReviewItem model, int position) {
        viewHolder.cardname.setText(model.getname());
        viewHolder.cardrate.setText(model.getrate());
        viewHolder.cardreview.setText(model.getreview());
        viewHolder.carddate.setText(model.gettime());
        viewHolder.cardname.setText(model.getname());
        if (model.getrate() != null) {
            try {
                viewHolder.cardbar.setRating(Float.parseFloat(model.getrate()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnItemClickListener (final OnReviewListItemClickListener listener){
        this.itemClickListener = listener;
    }

    public interface OnReviewListItemClickListener{
        public void onImageClick (View v, int position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public static class reViewHolder extends RecyclerView.ViewHolder{

        public TextView cardname;
        public RatingBar cardbar;
        public TextView cardrate;
        public TextView cardreview;
        public TextView carddate;
        public ImageView carddelete;

        public reViewHolder(View itemView) {
            super(itemView);

            cardname = (TextView)itemView.findViewById(R.id.RestaurantName);
            cardrate = (TextView)itemView.findViewById(R.id.reviewCardRate);
            cardreview = (TextView)itemView.findViewById(R.id.reviewCardReview);
            cardbar = (RatingBar) itemView.findViewById(R.id.reviewCardratingBar);
            carddate = (TextView)itemView.findViewById(R.id.reviewCardDate);
            carddelete = (ImageView) itemView.findViewById(R.id.deletecard);

            carddelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        itemClickListener.onImageClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }

}
