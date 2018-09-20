package com.example.mxx62.findit.POJO;

/**
 * Created by mxx62 on 2017/8/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Result {

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("id")
    @Expose
    private String id;


    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("reference")
    @Expose
    private String reference;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}

