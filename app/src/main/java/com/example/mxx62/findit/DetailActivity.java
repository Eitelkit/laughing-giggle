package com.example.mxx62.findit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.widget.ImageView.ScaleType.CENTER_CROP;


public class DetailActivity extends AppCompatActivity implements WriteReviewFrag.OnFragmentInteractionListener{

    HashMap RestDetail = new HashMap();
    private Toolbar detailBar;

    ImageView Picture;
    TextView Name;
    TextView Phone;
    TextView Address;
    TextView Rate;
    TextView ReviewNum;
    TextView prices;
    RatingBar RateBar;
    FloatingActionButton fab_plus, fab_map, fab_review;
    Animation FabOpen, FabClose, FabRotate, FabRotateback;
    boolean isopen = false;
    Double [] arr_  = new Double[2];
    List<String> photoUrl = new ArrayList<String>();
    List<Map<String, ?>> reviewlist = new ArrayList<Map<String, ?>>();

    private String ID;
    private PagerAdapter my_adapter;
    private ViewPager my_viewPager;

    private String ClientID = "g5k3vCaX2bkvTlNNEIXClA";
    private String ClientSecret = "Fxc74wLrfvr2xIEBzZZt97SMD5devqqNU6cJvpP28bYw08ozTNlaJSuxkQLPadKC";
    private String grant_type = "client_credentials";
    private String AccessToken = "";

    private FragmentManager fragmentManager;
    FrameLayout first, second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        first = (FrameLayout)findViewById(R.id.placeholder);
        second = (FrameLayout)findViewById(R.id.fragholder);


        detailBar = (Toolbar)findViewById(R.id.detail_toolbar);
        setSupportActionBar(detailBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        RestDetail = (HashMap) this.getIntent().getExtras().get("MovieDetail");

        fab_plus= (FloatingActionButton)findViewById(R.id.float_plus);
        fab_map = (FloatingActionButton)findViewById(R.id.mapfloatbtn);
        fab_review =(FloatingActionButton)findViewById(R.id.reviewfloatbtn);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate);
        FabRotateback = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_back);


    //    Picture = (ImageView)findViewById(R.id.imageViewRest);


        Name = (TextView)findViewById(R.id.nameRest);
        Phone = (TextView)findViewById(R.id.phoneRest);
        Address = (TextView)findViewById(R.id.addressRest);
        Rate = (TextView)findViewById(R.id.ratingRest);
        RateBar = (RatingBar)findViewById(R.id.ratingBarRest);
        RateBar.setIsIndicator(true);
        RateBar.setMax(5);
        RateBar.setStepSize(0.001f);
        ReviewNum = (TextView)findViewById(R.id.reviewNum);
        prices =(TextView)findViewById(R.id.price);

        ID = RestDetail.get("id").toString();



        /*String url = RestDetail.get("url").toString();
        if (url != " "){
            try {
                Picasso.with(this).load(String.valueOf(url)).into(Picture);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }*/
        Name.setText((CharSequence) RestDetail.get("name"));
        detailBar.setTitle((CharSequence) RestDetail.get("name"));
        Phone.setText((CharSequence) RestDetail.get("phone"));
        Float rate = (Float) RestDetail.get("rate");
        Rate.setText(String.valueOf(rate));
        RateBar.setRating(rate);
        ReviewNum.setText("Total Review: "+(RestDetail.get("reviewNum")).toString());
        prices.setText((CharSequence) RestDetail.get("prices"));
        Address.setText((CharSequence) RestDetail.get("address"));

        my_viewPager = (ViewPager)findViewById(R.id.ImageViewPager);
        my_adapter = new myPagerAdapter(this);
        my_viewPager.setAdapter(my_adapter);

        AuthenticatorYelpImage authenticatorYelpImage = new AuthenticatorYelpImage(my_adapter);
        authenticatorYelpImage.execute(ID);

        arr_[0]= (Double) RestDetail.get("lat");
        arr_[1]= (Double) RestDetail.get("lon");


        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isopen){
                    fab_map.startAnimation(FabClose);
                    fab_review.startAnimation(FabClose);
                    fab_plus.startAnimation(FabRotateback);
                    fab_map.setClickable(false);
                    fab_review.setClickable(false);
                    isopen=false;

                }else {
                    fab_map.startAnimation(FabOpen);
                    fab_review.startAnimation(FabOpen);
                    fab_plus.startAnimation(FabRotate);
                    fab_map.setClickable(true);
                    fab_review.setClickable(true);
                    isopen=true;
                }
            }
        });
        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(DetailActivity.this, MapsActivity.class);
                sharingIntent.putExtra("RestaurantName", arr_);
                char[] temp = ((String)RestDetail.get("name")).toCharArray();
                for(int i=0; i<temp.length;i++) {
                    if(temp[i] == ' ')
                        temp[i] = '+';
                }
                sharingIntent.putExtra("keyword",String.valueOf(temp));
                sharingIntent.putExtra("vicinity", (String)RestDetail.get("address"));
                startActivity(sharingIntent);
            }
        });

        fragmentManager =getSupportFragmentManager();
        fab_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first.setVisibility(View.GONE);
                second.setVisibility(View.VISIBLE);
                WriteReviewFrag frag = new WriteReviewFrag();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragholder, WriteReviewFrag.newInstance(RestDetail.get("id").toString(), Name.getText().toString()))
                        .addToBackStack(null).commit();

            }
        });

        /*LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container2) ;

        for (int i=0; i<reviewlist.size(); i++){
            View cardview = getLayoutInflater().inflate(R.layout.review_card_detail, linearLayout);
            ((TextView)cardview.findViewById(R.id.reviewRate2)).setText(reviewlist.get(i).get("rate").toString());
        }*/


    }

    public FrameLayout getFrameLayout(){
        return first;
    }

    public String getName(){
        return (String) Name.getText();
    }
    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        getFrameLayout().setVisibility(View.VISIBLE);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        second.setVisibility(View.GONE);
        first.setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class myPagerAdapter extends PagerAdapter{

        private final Context mContext;
        LayoutInflater mlayoutInflater;
        public myPagerAdapter(Context context){
            this.mContext = context;
           // mlayoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return photoUrl.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            Picasso.with(mContext).load(photoUrl.get(position)).into(imageView);
            container.addView(imageView, 0);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


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

    public List<String> parseJsonDataPhot (String accessToken, String Id) throws IOException {
        OkHttpClient client = new OkHttpClient();
        List<String> url = new ArrayList<String>();
        String finalUrl = "https://api.yelp.com/v3/businesses/"+Id;
        Log.i("yelp", "GET: " + finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .addHeader("authorization", "Bearer "+ accessToken)
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        String json = response.body().string();
        Log.d("!!!!!!!!", json);
        try {
            JSONObject obj = new JSONObject(json);

            if (obj.has("photos")){
                JSONArray arr = obj.getJSONArray("photos");
                for (int i=0; i<arr.length(); i++) {
                   url.add(arr.optString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url;
    }

    public List<Map<String, ?>> parseReview(String accessToken, String ID) throws IOException{
        OkHttpClient client = new OkHttpClient();
        List<Map<String,?>> list = new ArrayList<Map<String,?>>();
        String finalUrl = "https://api.yelp.com/v3/businesses/"+ID+"/reviews";
        Log.i("yelp", "GET: " + finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .addHeader("authorization", "Bearer "+ accessToken)
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        String json = response.body().string();
        Log.d("Review", json);
        try{
            JSONObject obj = new JSONObject(json);

            if (obj.has("reviews")){
                JSONArray arr = obj.getJSONArray("reviews");
                for (int i=0; i<arr.length(); i++){
                    JSONObject object = arr.getJSONObject(i);
                    HashMap temp = new HashMap();
                    temp.put("rate", object.optInt("rating"));
                    temp.put("review", object.optString("text"));
                    temp.put("time", object.optString("time_created"));

                    list.add(i, temp);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return list;
    }


    private class AuthenticatorYelpImage extends AsyncTask<String, Void, List<String>> {
        private final WeakReference<PagerAdapter> adapterWeakReference;

        public AuthenticatorYelpImage(PagerAdapter adapter){
            adapterWeakReference = new WeakReference<PagerAdapter>(adapter);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            AuthenticateYelp();
            try {
                photoUrl = parseJsonDataPhot(AccessToken, params[0]);
                reviewlist = parseReview(AccessToken, params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return photoUrl;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (adapterWeakReference != null){
                final PagerAdapter adapter = adapterWeakReference.get();
                if(adapter != null){
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

}
