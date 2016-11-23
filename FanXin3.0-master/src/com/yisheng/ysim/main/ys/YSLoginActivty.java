package com.yisheng.ysim.main.ys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yisheng.easeui.EaseConstant;
import com.yisheng.easeui.domain.EaseUser;
import com.yisheng.ysim.Constant;
import com.yisheng.ysim.DemoApplication;
import com.yisheng.ysim.DemoHelper;
import com.yisheng.ysim.R;
import com.yisheng.ysim.databinding.YsActLoginBinding;
import com.yisheng.ysim.db.DemoDBManager;
import com.yisheng.ysim.db.UserDao;
import com.yisheng.ysim.main.FXConstant;
import com.yisheng.ysim.main.fragment.CsActivity;
import com.yisheng.ysim.main.fragment.MainActivity;
import com.yisheng.ysim.main.utils.JSONUtil;
import com.yisheng.ysim.main.utils.OkHttpManager;
import com.yisheng.ysim.main.utils.Param;
import com.yisheng.ysim.ui.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by giw on 16/11/21.
 */
public class YSLoginActivty extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "YSLoginActivity";
    KProgressHUD hub;
    YsActLoginBinding binding;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            binding.actBtnCode.setText("重新获取("+msg.what+")");
            if(0==msg.what){
                binding.actBtnCode.setEnabled(true);
                binding.actBtnCode.setText("获取");
            }
        }
    };
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        binding= DataBindingUtil.setContentView(this, R.layout.ys_act_login);
        hub=KProgressHUD.create(this);
        binding.actBtnLogin.setOnClickListener(this);
        binding.actBtnCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String tel=binding.tel.getText().toString().trim();
        switch (v.getId()){
            case R.id.act_btn_login:
                String code=binding.code.getText().toString().trim();
                hub.show();
                if(this.checkValue(tel,code)){
                    toLogin(tel,code);
                }else{
                    hub.dismiss();
                }
                break;
            case R.id.act_btn_code:
                if(11!=tel.length()) return;
                //获取验证码
                binding.actBtnCode.setEnabled(false);
                List<Param> params=new ArrayList<>();
                params.add(new Param("phone",tel));
                OkHttpManager.getInstance().post(params, FXConstant.URL_LOGIN_SEND_CODE, new OkHttpManager.HttpCallBack() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if(1000==jsonObject.getIntValue("code")){
                            Toast.makeText(YSLoginActivty.this,"已发送..",Toast.LENGTH_SHORT).show();
                            binding.actBtnCode.setEnabled(false);
                            YSLoginActivty.this.changeCodeText();
                        }else{
                            Toast.makeText(YSLoginActivty.this,"服务器错误",Toast.LENGTH_SHORT).show();
                            binding.actBtnCode.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Toast.makeText(YSLoginActivty.this,"发送失败",Toast.LENGTH_SHORT).show();
                        binding.actBtnCode.setEnabled(true);
                        binding.actBtnCode.setText("获取");
                    }
                });
                break;
            default:
        }
    }

    private boolean checkValue(String tel,String code){
        if(11!=tel.length()){
            return false;
        }
        if(4!=code.length()){
            return false;
        }
        return true;
    }
    private void changeCodeText(){
        binding.actBtnCode.setEnabled(false);
        binding.actBtnCode.setText("重新获取(60)");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=60;
                while (i>=0){
                    handler.sendEmptyMessage(i);
                    SystemClock.sleep(1000);
                    i--;
                }
            }
        }).start();
    }
    //dneglu
    private void toLogin(String tel , String code){
        List<Param> params=new ArrayList<>();
        params.add(new Param("phone",tel));
        params.add(new Param("code",code));
        OkHttpManager.getInstance().post(params, FXConstant.URL_LOGIN_SEND, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(1000==jsonObject.getIntValue("code")){
                    Toast.makeText(YSLoginActivty.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    JSONObject user=jsonObject.getJSONObject("data");
                    saveFriends(user);
                    loginHuanXin(user,hub);
                }else{
                    hub.dismiss();
                    Toast.makeText(YSLoginActivty.this,"登陆失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                hub.dismiss();
                Toast.makeText(YSLoginActivty.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveFriends(final JSONObject jsonObject){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray friends=jsonObject.getJSONArray("friends");
                Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                if (friends != null) {
                    for (int i = 0; i < friends.size(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        EaseUser easeUser = JSONUtil.Json2User(friend);
                        userlist.put(easeUser.getUsername(), easeUser);
                    }
                    // save the contact list to cache
                    DemoHelper.getInstance().getContactList().clear();
                    DemoHelper.getInstance().getContactList().putAll(userlist);
                    // save the contact list to database
                    UserDao dao = new UserDao(getApplicationContext());
                    List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                    dao.saveContactList(users);

                }
                sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));

            }
        }).start();

    }
    private void loginHuanXin(final JSONObject jsonObject, final KProgressHUD progressDialog) {
        final String nick = jsonObject.getString("nick");
        final String hxid = jsonObject.getString("hxid");
        final String password = jsonObject.getString("password");
        DemoDBManager.getInstance().closeDB();
        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(hxid);
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(hxid, password, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                if (!YSLoginActivty.this.isFinishing() && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                // uprogressDialogate current user's display name for APNs
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        nick);
                if (!updatenick) {
                    Log.e("ysLoginActivity", "update current user nick fail");
                }
                // get user's info (this should be get from App's server or 3rd party service)
                // DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                jsonObject.remove("friends");
                DemoApplication.getInstance().setUserJson(jsonObject);
                // enter main activity
                YSLoginActivty.this.getUserInfo();



                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);

                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void getUserInfo(){
        if (!DemoHelper.getInstance().isLoggedIn()) {
            Log.e("未登录","未登录");
            return;
        }
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("hxid", DemoHelper.getInstance().getCurrentUsernName()));
        Log.e("环信id",DemoHelper.getInstance().getCurrentUsernName());
        OkHttpManager.getInstance().post(params, FXConstant.URL_FriendList, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (code == 1000) {
                    JSONArray josnArray = jsonObject.getJSONArray("friends");
                    Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                    if (josnArray != null) {
                        for (int i = 0; i < josnArray.size(); i++) {
                            JSONObject friend = josnArray.getJSONObject(i);
                            EaseUser easeUser = JSONUtil.Json2User(friend);
                            userlist.put(easeUser.getUsername(), easeUser);
                        }
                        // save the contact list to cache
                        DemoHelper.getInstance().getContactList().putAll(userlist);
                        Log.e("------联系人数量",""+userlist.size());
                        // save the contact list to database
                        UserDao dao = new UserDao(getApplicationContext());
                        List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                        dao.saveContactList(users);

                        if(DemoHelper.getInstance().getCurrentUsernName().equals("22222")){
                            Intent intent = new Intent(YSLoginActivty.this,
                                    MainActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent=new Intent(YSLoginActivty.this, CsActivity.class);
                            intent.putExtra("userId","22222");
                            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE);
                            startActivity(intent);
                        }

                    }

                }

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }
}
