package com.example.mxx62.findit;


import java.io.Serializable;

/**
 * Created by mxx62 on 2017/8/18.
 */

public class listItem implements Serializable{
    String id;
    String name;
    String phone;
    String rate;
    String url;
    String address;

    public listItem(){}

    public String getId(){return id;}

    public void setId(String id){this.id=id;}

    public String getname(){return name;}

    public void setname(String name){this.name=name;}

    public String getphone(){return phone;}

    public void setphone(String phone){this.phone=phone;}

    public String getrate(){return rate;}

    public void setrate(String rate){this.rate=rate;}

    public String geturl(){return url;}

    public void seturl(String url){this.url=url;}

    public String getaddress(){return address;}

    public void setaddress(String address){this.address=address;}

}
