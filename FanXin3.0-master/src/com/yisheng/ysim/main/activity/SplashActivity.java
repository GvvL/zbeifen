package com.yisheng.ysim.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yisheng.easeui.EaseConstant;
import com.yisheng.easeui.domain.EaseUser;
import com.yisheng.ysim.Constant;
import com.yisheng.ysim.DemoHelper;
import com.yisheng.ysim.R;
import com.yisheng.ysim.db.UserDao;
import com.yisheng.ysim.main.FXConstant;
import com.yisheng.ysim.main.fragment.CsActivity;
import com.yisheng.ysim.main.fragment.MainActivity;
import com.yisheng.ysim.main.service.ContactsService;
import com.yisheng.ysim.main.service.GroupService;
import com.yisheng.ysim.main.utils.JSONUtil;
import com.yisheng.ysim.main.utils.OkHttpManager;
import com.yisheng.ysim.main.utils.Param;
import com.yisheng.ysim.ui.BaseActivity;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity{
	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 2000;



	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.fx_activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);

	}


	@Override
	protected void onStart() {
		super.onStart();
		new Thread(new Runnable() {
			public void run() {
				if (DemoHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();

					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					startService(new Intent(SplashActivity.this, ContactsService.class));
					//enter main screen
//					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					if(!DemoHelper.getInstance().getCurrentUsernName().equals("22222")){
						Intent intent=new Intent(SplashActivity.this, CsActivity.class);
						intent.putExtra("userId","22222");
						intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE);
						startActivity(intent);
					}else{
						startActivity(new Intent(SplashActivity.this, MainActivity.class));
					}
					//获取下群组信息
					startService(new Intent(SplashActivity.this, GroupService.class));

					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}


}
