package com.neili.net;


import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gvvl on 2016/7/21.
 */
public class NetUtil {

    private static final String BASE_URL="http://zsoa.wfgxzs.com/index.php/public/";

    private static final long CACHE_SIZE = 5*1024*1024;
    private static final String CACHE_NAME = "HttpCache";

    private static NetUtil mInstance;
    private Retrofit retrofit;

    public static NetUtil getInstance(Context context){
        if (mInstance == null){
            synchronized (NetUtil.class){
                if (mInstance == null)
                    mInstance = new NetUtil(context);
            }
        }
        return mInstance;
    }

    private NetUtil(Context context){


        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //设置日志记录
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);



        //设置缓存目录
        File cacheDirectory = new File(context.getCacheDir()
                .getAbsolutePath(), CACHE_NAME);
        Cache cache = new Cache(cacheDirectory, CACHE_SIZE);
        builder.cache(cache);

        //设置头晕
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("User-Agent", "Retrofit-Sample-App")
                                                                    .addHeader("Content-Type","text/json")
                                                                    .build();
                return chain.proceed(newRequest);
            }
        };
        builder.interceptors().add(interceptor);

        //设置cookie缓存
        CookieHandler cookieHandler = new java.net.CookieManager(new PersistentCookieStore(context),CookiePolicy.ACCEPT_ALL);
        builder.cookieJar(new JavaNetCookieJar(cookieHandler));


        OkHttpClient okHttpClient = builder.readTimeout(7000, TimeUnit.MILLISECONDS).connectTimeout(7000, TimeUnit.MILLISECONDS).build();

        //生成buider
        retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

    }

    public  <T> T createService(Class<T> clz){
        return retrofit.create(clz);
    }




}



