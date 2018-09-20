package com.example.mxx62.findit;

/**
 * Created by mxx62 on 2017/8/16.
 */

import com.example.mxx62.findit.POJO.Example;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by navneet on 17/7/16.
 */
public interface RetrofitMaps {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/place/radarsearch/json?key=AIzaSyDN7RJFmImYAca96elyZlE5s_fhX-MMuhk")
    Call<Example> getNearbyPlaces(@Query("location") String location,@Query("keyword") String keyword, @Query("radius") int radius);

}
