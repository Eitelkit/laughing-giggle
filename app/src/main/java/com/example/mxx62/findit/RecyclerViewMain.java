package com.example.mxx62.findit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.support.design.widget.CoordinatorLayout.LayoutParams;

import static android.R.id.list;


public class  RecyclerViewMain extends Fragment {

    private OnFragmentInteractionListener mListener;


    ////////////////////////////////////////////////////////////
    private String ClientID = "g5k3vCaX2bkvTlNNEIXClA";
    private String ClientSecret = "Fxc74wLrfvr2xIEBzZZt97SMD5devqqNU6cJvpP28bYw08ozTNlaJSuxkQLPadKC";
    private String grant_type = "client_credentials";
    private String AccessToken = "";
    private String LastLocation;
    private String Category;
    private String lon, lat, name, olocation, phone, jsonAddress, img, json;


    public RestaurantDataJson restaurantData1,restaurantDataSearch;
    RestaurantDataJson restaurantDataJson1 = new RestaurantDataJson();
    RestaurantDataJson restaurantDataJson2 = new RestaurantDataJson();
    private RecyclerView main_view;
    private RecyclerAdapter main_adapter;


    int mCurCheckPostion=0;

    private DatabaseReference fragRef;
    private FirebaseAuth fragAuth;

    public voiceinferface vi;
    SearchView my_search;

    ////////////////////////////////////////////////////////////

    public RecyclerViewMain() {
        // Required empty public constructor
    }

    public interface voiceinferface{
        public void startvoice(SearchView s);
    }

    private static  final String Rest_argument1 = "location";
    private static final String Rest_argument2 = "categories";

    public static RecyclerViewMain newInstance(String Location, String category) {
        Bundle args = new Bundle();
        RecyclerViewMain fragment = new RecyclerViewMain();
        args.putString(Rest_argument1, Location);
        args.putString(Rest_argument2, category);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        restaurantDataJson1 = new RestaurantDataJson();
        restaurantDataSearch = new RestaurantDataJson();
        vi = (voiceinferface)getContext();

        restaurantData1 = new RestaurantDataJson();
        if (getArguments() != null){
            LastLocation = (String)getArguments().getString(Rest_argument1);
            Category = (String) getArguments().getString(Rest_argument2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.recycler_view_main, container, false);

        fragRef = FirebaseDatabase.getInstance().getReference();
        fragAuth = FirebaseAuth.getInstance();

        main_view = (RecyclerView) view.findViewById(R.id.main_recycler);
        final LinearLayoutManager layoutManager_main = new LinearLayoutManager(view.getContext());
        layoutManager_main.scrollToPosition(0);
        main_view.setLayoutManager(layoutManager_main);
        main_view.setHasFixedSize(true);

        main_adapter = new RecyclerAdapter(restaurantData1.getRestList(), getActivity()); //restaurantDataJson1
        main_view.setAdapter(main_adapter);

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT , ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // callback for swipe to dismiss, removing item from data and adapter
                int position = (viewHolder.getAdapterPosition());
                final String id = (String) restaurantData1.getItem(position).get("id");
                final String name = (String) restaurantData1.getItem(position).get("name");
                final String phone = (String) restaurantData1.getItem(position).get("phone");
                final String address = (String) restaurantData1.getItem(position).get("address");
                final String rate = String.valueOf(restaurantData1.getItem(position).get("rate"));
                final String url = (String) restaurantData1.getItem(position).get("url");

                if (fragAuth.getCurrentUser() != null) {
                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                            .child("Dislike").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(id).exists()) {
                                fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                        .child("Dislike").child(id).child("id").setValue(id);

                                fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                        .child("Dislike").child(id).child("name").setValue(name);

                                fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                        .child("Dislike").child(id).child("phone").setValue(phone);

                                fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                        .child("Dislike").child(id).child("address").setValue(address);

                                fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                        .child("Dislike").child(id).child("rate").setValue(rate);

                                fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                        .child("Dislike").child(id).child("url").setValue(url);

                            } else {
                                Toast.makeText(getContext(), "Data Exits", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                main_adapter.notifyDataSetChanged();

            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(main_view);



            AuthenticatorYelp task = new AuthenticatorYelp(main_adapter);
            task.execute();

            main_adapter.setOnItemClickListener(new RecyclerAdapter.OnRecyclerViewItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {
                    HashMap map = (HashMap) restaurantData1.getItem(position);
                    mListener.onFragmentInteraction(map);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }

                @Override
                public void onDisClick(View view, int position) {
                    final String id = (String) restaurantData1.getItem(position).get("id");
                    final String name = (String) restaurantData1.getItem(position).get("name");
                    final String phone = (String) restaurantData1.getItem(position).get("phone");
                    final String address = (String) restaurantData1.getItem(position).get("address");
                    final String rate = String.valueOf(restaurantData1.getItem(position).get("rate"));
                    final String url = (String) restaurantData1.getItem(position).get("url");

                    if (fragAuth.getCurrentUser() != null) {
                        fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                .child("Dislike").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.child(id).exists()) {
                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Dislike").child(id).child("id").setValue(id);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Dislike").child(id).child("name").setValue(name);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Dislike").child(id).child("phone").setValue(phone);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Dislike").child(id).child("address").setValue(address);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Dislike").child(id).child("rate").setValue(rate);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Dislike").child(id).child("url").setValue(url);

                                } else {
                                    Toast.makeText(getContext(), "Data Exits", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onFavorClick(View view, final int position) {
                    final String id = (String) restaurantData1.getItem(position).get("id");
                    final String name = (String) restaurantData1.getItem(position).get("name");
                    final String phone = (String) restaurantData1.getItem(position).get("phone");
                    final String address = (String) restaurantData1.getItem(position).get("address");
                    final String rate = String.valueOf(restaurantData1.getItem(position).get("rate"));
                    final String url = (String) restaurantData1.getItem(position).get("url");

                    if (fragAuth.getCurrentUser() != null){
                        fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                .child("Favorite").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.child(id).exists()){
                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Favorite").child(id).child("id").setValue(id);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Favorite").child(id).child("name").setValue(name);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Favorite").child(id).child("phone").setValue(phone);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Favorite").child(id).child("address").setValue(address);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Favorite").child(id).child("rate").setValue(rate);

                                    fragRef.child("users").child(fragAuth.getCurrentUser().getUid())
                                            .child("Favorite").child(id).child("url").setValue(url);

                                }
                                else {
                                    Toast.makeText(getContext(), "Data Exits", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onOverflowMenuClick(View v, int position) {

                }


            });
        return view;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void AuthenticateYelp(){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType,
                "client_id="+ ClientID+ "&client_secret=" + ClientSecret
                        + "&grant_type=" +grant_type);
        Request request = new Request.Builder()
                .url("https://api.yelp.com/oauth2/token")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            try {

                JSONObject obj = new JSONObject(json);
                AccessToken = obj.getString("access_token");
                Log.i("Yelp", "token: " + AccessToken);

            } catch (Throwable t) {
                Log.i("Yelp", "Could not parse malformed JSON: \"" + json + "\"");
            }
        } catch(Exception e) {
            Log.i("Yelp", "Error " + e.toString());
        }
    }

    private class AuthenticatorYelp extends AsyncTask<Void, Void, RestaurantDataJson>{
        private final WeakReference<RecyclerAdapter> recyclerAdapterWeakReference;

        public AuthenticatorYelp(RecyclerAdapter adapter){
            recyclerAdapterWeakReference = new WeakReference<RecyclerAdapter>(adapter);
        }

        @Override
        protected RestaurantDataJson doInBackground(Void... params) {
            AuthenticateYelp();
            try {
                restaurantDataJson1.parseJsonData(AccessToken, LastLocation, Category);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return restaurantDataJson1;
        }

        @Override
        protected void onPostExecute(RestaurantDataJson restaurantDataJson) {


            for (int i=0; i<restaurantDataJson.getSize(); i++){
                restaurantData1.RestList.add((HashMap<String, ?>)((HashMap) restaurantDataJson.RestList.get(i)).clone());
            }

            if(recyclerAdapterWeakReference != null){
                final RecyclerAdapter adapter = recyclerAdapterWeakReference.get();
                if (adapter != null){
                    main_adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class SearchRestaurantYelp extends AsyncTask<Void, Void, RestaurantDataJson> {
        private final WeakReference<RecyclerAdapter> recyclerAdapterWeakReference;
        String keywords;
        public SearchRestaurantYelp(String query, RecyclerAdapter adapter){
            recyclerAdapterWeakReference = new WeakReference<RecyclerAdapter>(adapter);
            keywords = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected RestaurantDataJson doInBackground(Void... params) {
            AuthenticateYelp();
            try {
                restaurantDataJson2.parseSearchRestaurantJsonData(keywords, AccessToken, LastLocation, Category);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return restaurantDataJson2;
        }
        @Override
        protected void onPostExecute(RestaurantDataJson restaurantDataJson) {
            restaurantData1 = new RestaurantDataJson();
            for (int i=0; i<restaurantDataJson.getSize(); i++){
                restaurantData1.RestList.add((HashMap<String, ?>)((HashMap) restaurantDataJson.RestList.get(i)).clone());
            }

            if(recyclerAdapterWeakReference != null){
                final RecyclerAdapter adapter = recyclerAdapterWeakReference.get();
                if (adapter != null){
                    main_adapter.notifyDataSetChanged();
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu.findItem(R.id.RatingSort) == null)
            inflater.inflate(R.menu.recycler_frag_menu, menu);

        my_search = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        //my_search.setSubmitButtonEnabled(true);
        //my_search.setIconifiedByDefault(false);

        if (my_search != null) {
            my_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    main_adapter = new RecyclerAdapter(restaurantData1.getRestList(), getActivity());
                    main_view.setAdapter(main_adapter);
                    //restaurantData1.getRestList().clear();
                    SearchRestaurantYelp task = new SearchRestaurantYelp(query, main_adapter);
                    task.execute();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.RatingSort){
            restaurantData1.sort_rating();
            main_adapter = new RecyclerAdapter(restaurantData1.getRestList(), getActivity());
            main_view.setAdapter(main_adapter);
            return true;
        }
        else if (i == R.id.DistSort){
            restaurantData1.sort_dist();
            main_adapter = new RecyclerAdapter(restaurantData1.getRestList(), getActivity());
            main_view.setAdapter(main_adapter);
            return true;
        } else if (i == R.id.app_bar_voicesearch) {
            vi.startvoice(my_search);
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(HashMap map);
    }
}
