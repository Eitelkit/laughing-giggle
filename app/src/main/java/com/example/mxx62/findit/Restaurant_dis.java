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

public class Restaurant_dis {

    static List<Map<String, ?>> RestList = new ArrayList<Map<String, ?>>();
    int count = 0;

    FirebaseAuth mauth = FirebaseAuth.getInstance();

    DatabaseReference mRef;
    myFirebaseRecyclerAdapter myFirebaseRecylerAdapter;
    Context mContext;


    public List<Map<String, ?>> getRestList() {return RestList; }

    public int getSize() {return RestList.size();}

    public HashMap getItem(int i){
        if (i>=0 && i<RestList.size()){
            return (HashMap) RestList.get(i);
        }
        else return null;
    }

    public void setAdapter(myFirebaseRecyclerAdapter myFirebaseRecyclerAdapter){
        this.myFirebaseRecylerAdapter=myFirebaseRecyclerAdapter;
    }

    public Restaurant_dis(){

        RestList = new ArrayList<Map<String,?>>();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getCurrentUser().getUid()).child("Dislike").getRef();

        //child("moviedata").getRef();
        myFirebaseRecylerAdapter = null;
        mContext = null;

    }
    public void removeItemFromServer(Map<String,?> movie){
        if(movie!=null){
            String id = (String)movie.get("id");
            mRef.child(id).removeValue();
            //myFirebaseRecylerAdapter.notifyDataSetChanged();
        }
    }

    public DatabaseReference getFireBaseRef(){
        return mRef;
    }
    public void setContext(Context context){mContext = context;}

    public void onItemRemovedFromCloud(HashMap item){
        int position = -1;
        String id=(String)item.get("id");
        for(int i=0;i<RestList.size();i++){
            HashMap rest = (HashMap)RestList.get(i);
            String mid = (String)rest.get("id");
            if(mid.equals(id)){
                position= i;
                break;
            }
        }
        if(position != -1){
            RestList.remove(position);
            Toast.makeText(mContext, "Item Removed:" + id, Toast.LENGTH_SHORT).show();
            if (myFirebaseRecylerAdapter != null){
                myFirebaseRecylerAdapter.notifyItemRemoved(position);
                myFirebaseRecylerAdapter.notifyDataSetChanged();
            }

        }
    }

    public void onItemAddedToCloud(HashMap item){
        int insertPosition = 0;
        String id=(String)item.get("id");
        for(int i=0;i<RestList.size();i++){
            HashMap movie = (HashMap)RestList.get(i);
            String mid = (String)movie.get("id");
            if(mid.equals(id)){
                return;
            }
            if(mid.compareTo(id)<0){
                insertPosition=i+1;
            }else{
                break;
            }
        }
        RestList.add(insertPosition, item);
        // Toast.makeText(mContext, "Item added:" + id, Toast.LENGTH_SHORT).show();
        if (myFirebaseRecylerAdapter != null) {
            myFirebaseRecylerAdapter.notifyItemInserted(insertPosition);
            myFirebaseRecylerAdapter.notifyDataSetChanged();
        }

    }

    public void onItemUpdatedToCloud(HashMap item){
        String id=(String)item.get("id");
        for(int i=0;i<RestList.size();i++){
            HashMap rest = (HashMap)RestList.get(i);
            String mid = (String)rest.get("id");
            if(mid.equals(id)){
                RestList.remove(i);
                RestList.add(i,item);
                Log.d("My Test: NotifyChanged",id);
                if (myFirebaseRecylerAdapter != null){
                    myFirebaseRecylerAdapter.notifyItemChanged(i);
                    // myFirebaseRecylerAdapter.notifyDataSetChanged();
                }

                break;
            }
        }

    }

    public void initializeDataFromCloud() {
        RestList.clear();
        mRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("MyTest: OnChildAdded", dataSnapshot.toString());
                HashMap<String,String> rest = (HashMap<String,String>)dataSnapshot.getValue();
                onItemAddedToCloud(rest);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("MyTest: OnChildAdded", dataSnapshot.toString());
                HashMap<String,String> rest = (HashMap<String,String>)dataSnapshot.getValue();
                onItemUpdatedToCloud(rest);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("MyTest: OnChildRemoved", dataSnapshot.toString());
                HashMap<String,String> rest = (HashMap<String,String>)dataSnapshot.getValue();
                onItemRemovedFromCloud(rest);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

















    /*public RestaurantData(){

        String id;
        String name;
        String phone;
        String address;
        String url;
        Float rate;
        Double lon;
        Double lat;

    }*/

    public static HashMap createMap (String id, String name, String phone, String address, String url, Float rate,
                                     Double lon, Double lat){
        HashMap rest = new HashMap();
        rest.put("id", id);
        rest.put("name", name);
        rest.put("phone", phone);
        rest.put("address", address);
        rest.put("url", url);
        rest.put("rate", rate);
        rest.put("lon", lon);
        rest.put("lat", lat);
        return rest;
    }
}
