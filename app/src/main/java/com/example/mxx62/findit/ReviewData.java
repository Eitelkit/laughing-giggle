package com.example.mxx62.findit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mxx62 on 2017/8/18.
 */

public class ReviewData {

    static List<Map<String, ?>> ReviewList = new ArrayList<Map<String, ?>>();
    int count = 0;

    FirebaseAuth mauth = FirebaseAuth.getInstance();

    DatabaseReference mRef;
    ReviewRecyclerAdapter reviewRecyclerAdapter;
    Context mContext;

    public List<Map<String, ?>> getRestList() {return ReviewList; }

    public int getSize() {return ReviewList.size();}

    public HashMap getItem(int i){
        if (i>=0 && i<ReviewList.size()){
            return (HashMap) ReviewList.get(i);
        }
        else return null;
    }

    public void setAdapter(ReviewRecyclerAdapter reviewRecyclerAdapter){
        this.reviewRecyclerAdapter = reviewRecyclerAdapter;
    }

    public ReviewData(){

        ReviewList = new ArrayList<Map<String,?>>();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid()).child("Review").getRef();




        //child("moviedata").getRef();
        reviewRecyclerAdapter = null;
        mContext = null;

    }





    public void removeItemFromServer(Map<String,?> review){
        if(review!=null){
            String name = (String)review.get("name");
            String key = (String)review.get("key");
            mRef.child(key).removeValue();
            //myFirebaseRecylerAdapter.notifyDataSetChanged();
        }
    }

    public DatabaseReference getFireBaseRef(){
        return mRef;
    }
    public void setContext(Context context){mContext = context;}

    public void onItemRemovedFromCloud(HashMap item){
        int position = -1;
        String name=(String)item.get("name");
        for(int i=0;i<ReviewList.size();i++){
            HashMap review = (HashMap)ReviewList.get(i);
            String mid = (String)review.get("name");
            if(mid.equals(name)){
                position= i;
                break;
            }
        }
        if(position != -1){
            ReviewList.remove(position);
            Toast.makeText(mContext, "Item Removed:" + name, Toast.LENGTH_SHORT).show();
            if (reviewRecyclerAdapter != null){
                reviewRecyclerAdapter.notifyItemRemoved(position);
                reviewRecyclerAdapter.notifyDataSetChanged();
            }

        }
    }

    public void onItemAddedToCloud(HashMap item){
        int insertPosition = 0;
        String name=(String)item.get("name");
        for(int i=0;i<ReviewList.size();i++){
            /*HashMap review = (HashMap)ReviewList.get(i);
            String mid = (String)review.get("name");
            if(mid.equals(name)){
                return;
            }
            if(mid.compareTo(name)<0){*/
                insertPosition=i+1;
           /* }else{
                break;
            }*/
        }
        ReviewList.add(insertPosition, item);
        // Toast.makeText(mContext, "Item added:" + id, Toast.LENGTH_SHORT).show();
        if (reviewRecyclerAdapter != null) {
            reviewRecyclerAdapter.notifyItemInserted(insertPosition);
            reviewRecyclerAdapter.notifyDataSetChanged();
        }

    }

    public void onItemUpdatedToCloud(HashMap item){
        String name=(String)item.get("name");
        for(int i=0;i<ReviewList.size();i++){
            HashMap review = (HashMap)ReviewList.get(i);
            String mid = (String)review.get("name");
            if(mid.equals(name)){
                ReviewList.remove(i);
                ReviewList.add(i,item);
                Log.d("My Test: NotifyChanged",name);
                if (reviewRecyclerAdapter != null){
                    reviewRecyclerAdapter.notifyItemChanged(i);
                    // myFirebaseRecylerAdapter.notifyDataSetChanged();
                }

                break;
            }
        }

    }

    public void initializeDataFromCloud() {
        ReviewList.clear();
        mRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("MyTest: OnChildAdded", dataSnapshot.toString());
                        HashMap<String, String> review = (HashMap<String, String>) dataSnapshot.getValue();
                        review.put("key", dataSnapshot.getKey());
                        onItemAddedToCloud(review);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d("MyTest: OnChildAdded", dataSnapshot.toString());
                        HashMap<String, String> review = (HashMap<String, String>) dataSnapshot.getValue();
                        review.put("key", dataSnapshot.getKey());
                        onItemUpdatedToCloud(review);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d("MyTest: OnChildRemoved", dataSnapshot.toString());
                        HashMap<String, String> review = (HashMap<String, String>) dataSnapshot.getValue();
                        review.put("key", dataSnapshot.getKey());
                        onItemRemovedFromCloud(review);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



}
