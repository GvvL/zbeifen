package com.zsj.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.zsj.app.R;

public class WelcomeActivity extends AppCompatActivity {
    private boolean isDelay=false;
    private boolean isLogin=false;
    private boolean isLoginTest=false;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==100){
                isDelay=true;
            }
            if(msg.what==200){
                isLoginTest=true;
            }
            if(isDelay && isLoginTest){
                Intent intent=new Intent();
                //跳转
                if(isLogin){
                    intent.setClass(WelcomeActivity.this,MainActivity.class);
                }else{
                    intent.setClass(WelcomeActivity.this,LoginActivity.class);
                }
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final Message message = handler.obtainMessage(100);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(4000);
                message.sendToTarget();
            }
        }).start();
        final Message msg=handler.obtainMessage(200);
        isLogin=true;
        msg.sendToTarget();
//        LoginService loginService = NetUtil.getInstance(this).createService(LoginService.class);
//        loginService.testLogin()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new ApiCallBack<ResponseDataItem<String>>() {
//                    @Override
//                    protected void success(ResponseDataItem<String> resultData) {
//                        isLogin=true;
//                        msg.sendToTarget();
//                    }
//
//                    @Override
//                    protected void error(int code, String str) {
//                        isLogin=false;
//                        msg.sendToTarget();
//                    }
//                });

    }

}
