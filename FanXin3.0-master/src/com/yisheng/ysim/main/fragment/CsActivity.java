package com.yisheng.ysim.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yisheng.easeui.EaseConstant;
import com.yisheng.easeui.ui.EaseChatFragment;
import com.yisheng.ysim.R;
import com.yisheng.ysim.main.FXConstant;
import com.yisheng.ysim.main.activity.ChatActivity;
import com.yisheng.ysim.main.db.ACache;
import com.yisheng.ysim.main.utils.OkHttpManager;
import com.yisheng.ysim.main.utils.Param;
import com.yisheng.ysim.runtimepermissions.PermissionsManager;
import com.yisheng.ysim.ui.BaseActivity;
import com.hyphenate.util.EasyUtils;

import java.util.ArrayList;
import java.util.List;

public class CsActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    public   String toChatUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_chat);

        toChatUsername=getIntent().getExtras().getString("userId");

        chatFragment=new ChatFragment();
        chatFragment.IS_CS=true;
        chatFragment.hideTitleBar();
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        int  chatTypeTemp = getIntent().getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        if(chatTypeTemp==EaseConstant.CHATTYPE_GROUP){
            getGroupMembersInServer(toChatUsername);
        }
    }
    private void getGroupMembersInServer(final String groupId) {

        List<Param> params = new ArrayList<>();
        params.add(new Param("groupId", groupId));
        OkHttpManager.getInstance().post(params, FXConstant.URL_GROUP_MEMBERS, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.containsKey("code")) {
                    int code = Integer.parseInt(jsonObject.getString("code"));
                    if (code == 1000) {
                        if (jsonObject.containsKey("data") && jsonObject.get("data") instanceof JSONArray) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            ACache.get(getApplicationContext()).put(groupId, jsonArray);

                        }
                    }

                }

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        super.onBackPressed();
        this.finish();
//        if (EasyUtils.isSingleActivity(this)) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
    }

    public String getToChatUsername(){
        return toChatUsername;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
