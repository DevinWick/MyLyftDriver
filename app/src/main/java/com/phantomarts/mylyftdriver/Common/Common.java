package com.phantomarts.mylyftdriver.Common;

import com.phantomarts.mylyftdriver.remote.IGoogleAPI;
import com.phantomarts.mylyftdriver.remote.RetrofitClient;

public class Common {
    public static final String baseUrl="https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseUrl).create(IGoogleAPI.class);
    }
}
