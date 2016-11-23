package com.zsj.ui;


import com.neili.net.entry.ResponseDataItem;

import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface LoginService{
    @GET("testLogin")
    Observable<ResponseDataItem<String>> testLogin();
    @Multipart
    @POST("appLogin")
    Observable<ResponseDataItem<String>> login(@Part("user") String user, @Part("pwd") String pwd,@Part("device_token") String device_token);
}
