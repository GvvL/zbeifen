package com.yisheng.ysim.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yisheng.ysim.DemoApplication;
import com.yisheng.easeui.ui.EaseBaseActivity;

public class BaseActivity extends EaseBaseActivity {

    /**
     * OKHTTP3请求
     */



    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.e("-----",this.getClass().getName());
        DemoApplication.getInstance().saveActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
      }

    @Override
    protected void onStart() {
        super.onStart();
      }

    public void back(View view) {
        finish();
    }

}
