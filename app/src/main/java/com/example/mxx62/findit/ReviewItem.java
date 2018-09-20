package com.example.mxx62.findit;

/**
 * Created by mxx62 on 2017/8/17.
 */

public class ReviewItem {
    String id;
    String name;
    String review;
    String rate;
    String time;
    String key;

    public  ReviewItem (){}

    public ReviewItem(String review, String rate, String time, String name){
        this.review=review;
        this.rate = rate;
        this.time = time;
        this.name = name;

    }

    public String getId(){return id;}

    public void setId(String id){this.id=id;}

    public String getname(){return name;}

    public void setname(String name){this.name=name;}

    public String getrate(){return rate;}

    public void setrate(String rate){this.rate=rate;}

    public String getreview(){return review;}

    public void setreview(String review){this.review=review;}

    public String gettime(){return time;}

    public void settime(String time){this.time=time;}

    public String getkey(){return key;}

    public void setkey(String key){this.key=key;}


}
