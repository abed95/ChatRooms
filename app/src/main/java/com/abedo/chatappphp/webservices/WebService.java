package com.abedo.chatappphp.webservices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * created by Abedo95 on 12/2/2019
 */
public class WebService {
    private static WebService instance;
    private API api;

    public WebService()
    {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder().client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Urls.MAIN_URL)
                .build();

        api = retrofit.create(API.class);
    }

    public static WebService getInstance(){
        if (instance==null){
            instance=new WebService();
        }
        return instance;
    }

    public API getApi(){
        return api;
    }
}
