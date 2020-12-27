package com.example.appchat.Retrofit2;

public class APIUtils {
    public static final String baseURL = "http://13.229.207.6:3000/";

    public static final String baseURLLocal = "http://172.16.1.99:4000";   //localhost

    public static DataClient getData(){
        return RetrofitClient.getClient(baseURL).create(DataClient.class);
    }

    public static DataClient getDataLocal(){
        return RetrofitClient.getClient(baseURLLocal).create(DataClient.class);
    }
}
