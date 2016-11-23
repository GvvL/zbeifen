package com.neili.net.entry;

import android.util.Log;
import android.widget.Toast;


import com.neili.app.App;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by gvvl on 2016/7/21.
 * 回调  服务端约定
 * 2++正常
 * 4++错误
 */

public abstract class ApiCallBack<T extends ResponseDataBase> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Toast.makeText(App.getInstance().getApplicationContext(), "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
            Toast.makeText(App.getInstance().getApplicationContext(), "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(App.getInstance().getApplicationContext(), "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        e.printStackTrace();
        error(500, e.getMessage());
    }
    @Override
    public void onNext(T data) {
        int code=data.getCode();
        if(code>=200 && code<300){
            success(data);
        }else{
            Log.e("数据返回错误:","错误信息:"+data.getCode()+data.getMsg());
            error(code, data.getMsg());
        }
    }
    protected abstract void success(T resultData);
    protected abstract void error(int code,String str);
}
