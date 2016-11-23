package com.zsj.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.neili.utils.UpdateManager;
import com.thefinestartist.utils.preferences.PreferencesUtil;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.zsj.app.ProgressWebView;
import com.zsj.app.R;

public class MainActivity extends AppCompatActivity {

    private ProgressWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        mPushAgent.setDebugMode(false);
        mPushAgent.onAppStart();
        String device_token = UmengRegistrar.getRegistrationId(this);
//        L.showDivider(true).i("+++"+device_token);
        webView= (ProgressWebView) findViewById(R.id.webview);
        webView.loadUrl("http://zsoa.wfgxzs.com/index.php?m=&c=public&a=tokenLogin&token="+PreferencesUtil.get("token","a"));
        new UpdateManager(this).checkUpdate(false);

    }
}
