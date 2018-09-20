package com.example.mxx62.findit;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mxx62 on 2017/8/14.
 */

public class RestaurantDataJson implements Serializable {

    List<Map<String, ?>> RestList = new ArrayList<Map<String, ?>>();
    int count = 0;

    public List<Map<String, ?>> getRestList() {return RestList; }

    public int getSize() {return RestList.size();}

    public HashMap getItem(int i){
        if (i>=0 && i<RestList.size()){
            return (HashMap) RestList.get(i);
        }
        else return null;
    }

    public RestaurantDataJson(){

        String id;
        String name;
        String phone;
        String address;
        String url;
        Float rate;
        String reviewNum;
        Double lon;
        Double lat;
        String prices;
        Double distance;

    }

    public static HashMap createMap (String id, String name, String phone, String address, String url, Float rate,
                                     String reviewNum, Double lon, Double lat, String prices, Double distance){
        HashMap rest = new HashMap();
        rest.put("id", id);
        rest.put("name", name);
        rest.put("phone", phone);
        rest.put("address", address);
        rest.put("url", url);
        rest.put("rate", rate);
        rest.put("reviewNum", reviewNum);
        rest.put("lon", lon);
        rest.put("lat", lat);
        rest.put("prices", prices);
        rest.put("distance", distance);
        return rest;
    }



    public void sort_rating(){
        Collections.sort(RestList, new Comparator<Map<String, ?>>() {
            @Override
            public int compare(Map<String, ?> o1, Map<String, ?> o2) {
                if (o2.get("rate").toString().compareTo(o1.get("rate").toString()) != 0){
                    return o2.get("rate").toString().compareTo(o1.get("rate").toString());
                }
                else if ((o2.get("rate").toString().compareTo(o1.get("rate").toString())) == 0){
                    return o2.get("name").toString().compareTo(o1.get("name").toString());
                }
                return 0;
            }
        });
    }

    public void sort_dist(){
        Collections.sort(RestList, new Comparator<Map<String, ?>>() {
            @Override
            public int compare(Map<String, ?> o1, Map<String, ?> o2) {
                if (o2.get("distance").toString().compareTo(o1.get("distance").toString()) != 0){
                    return o1.get("distance").toString().compareTo(o2.get("distance").toString());
                }
                else if ((o2.get("distance").toString().compareTo(o1.get("distance").toString())) == 0){
                    return o2.get("name").toString().compareTo(o1.get("name").toString());
                }
                return 0;
            }
        });
    }


    public void parseJsonData (String accessToken, String Location, String Category) throws IOException {
        OkHttpClient client = new OkHttpClient();
        //String location = "&latitude=42.9978746"+"&longitude=-76.1310136";
      //      location = "&latitude=42.9978746"+"&longitude=-76.1310136";
       // String location = Location;

        String default_cate = "restaurants";
        if (Category != null){
            default_cate = Category;
        }

        String finalUrl = "https://api.yelp.com/v3/businesses/search?"+"categories="+default_cate+"&open_now=false"+Location;
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

            if (obj.has("businesses")){
                JSONArray arr = obj.getJSONArray("businesses");
                for (int i=0; i<arr.length(); i++) {

                    JSONObject object = arr.getJSONObject(i);
                    String id = object.optString("id");
                    String name = object.optString("name");
                    String phone = object.optString("display_phone");
                    JSONObject jsonLocation = object.getJSONObject("location");
                    String address = jsonLocation.getJSONArray("display_address").toString();
                    JSONObject coordinates = object.getJSONObject("coordinates");
                    Double lon = Double.valueOf(coordinates.optString("longitude"));
                    Double lat = Double.valueOf(coordinates.optString("latitude"));
                    String url = object.optString("image_url");
                    Float rate = Float.valueOf(object.optString("rating"));
                    String review_count = String.valueOf(object.optInt("review_count"));
                    String prices = object.optString("price");
                    Double distance = Double.valueOf(object.optDouble("distance"));

                    getRestList().add(i, createMap(id, name, phone, address, url, rate, review_count, lon, lat, prices, distance));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseSearchRestaurantJsonData (String keyword, String accessToken, String Location, String Category) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String location = "&latitude=42.9978746"+"&longitude=-76.1310136";
        String default_cate = "restaurants";
        if (Category != null){
            default_cate = Category;
        }
        //  String a = keyword;
        //"https://api.yelp.com/v3/businesses/matches/lookup?name=Tang+Flavor&city=Syracuse&state=US-NY&country=US";
        //String finalUrl = "https://api.yelp.com/v3/businesses/matches/lookup?name=Tang+Flavor&city=Syracuse&state=US-NY&country=US";
        // String finalUrl = "https://api.yelp.com/v3/businesses/Mcdonald-s-syracuse";

        //GET https://api.yelp.com/v3/autocomplete?text=del&latitude=37.786882&longitude=-122.399972
        String finalUrl = "https://api.yelp.com/v3/businesses/search?term=" + keyword +Location;
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

            if (obj.has("businesses")){
                JSONArray arr = obj.getJSONArray("businesses");
                for (int i=0; i<arr.length(); i++) {

                    JSONObject object = arr.getJSONObject(i);
                    String id = object.optString("id");
                    String name = object.optString("name");
                    String phone = object.optString("display_phone");
                    JSONObject jsonLocation = object.getJSONObject("location");
                    String address = jsonLocation.getJSONArray("display_address").toString();
                    JSONObject coordinates = object.getJSONObject("coordinates");
                    Double lon = Double.valueOf(coordinates.optString("longitude"));
                    Double lat = Double.valueOf(coordinates.optString("latitude"));
                    String url = object.optString("image_url");
                    Float rate = Float.valueOf(object.optString("rating"));
                    String review_count = String.valueOf(object.optInt("review_count"));
                    String prices = object.optString("price");
                    Double distance = Double.valueOf(object.optDouble("distance"));

                    getRestList().add(i, createMap(id, name, phone, address, url, rate, review_count, lon, lat, prices, distance));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
