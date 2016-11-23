package com.neili.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.neili.net.NetUtil;
import com.neili.net.entry.ApiCallBack;
import com.neili.net.entry.ResponseDataItem;
import com.neili.service.DownLoadService;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.http.GET;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/26.
 */

public class UpdateManager {

    private Context mContext;

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    class VersionBean{
        private String[] version_info;

        public String[] getVersion_info() {
            return version_info;
        }

        public void setVersion_info(String[] version_info) {
            this.version_info = version_info;
        }

        private int version_code;
        private float version_size;
        private String version_name;
        private String version_url;

        public String getVersion_url() {
            return version_url;
        }

        public void setVersion_url(String version_url) {
            this.version_url = version_url;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public float getVersion_size() {
            return version_size;
        }

        public void setVersion_size(float version_size) {
            this.version_size = version_size;
        }
    }

    public interface VersionServeice{
        @GET("version")
        Observable<ResponseDataItem<VersionBean>> getVersion();
    }



    /**
     * 检测软件更新
     */
    public void checkUpdate(final boolean isToast) {
        /**
         * 在这里请求后台接口，获取更新的内容和最新的版本号
         */
        // 版本的更新信息
        NetUtil.getInstance(mContext).createService(VersionServeice.class)
                .getVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiCallBack<ResponseDataItem<VersionBean>>(){
                    @Override
                    protected void success(ResponseDataItem<VersionBean> resultData) {
                        int nVersion_code=resultData.getData().getVersion_code();
                        DownLoadService.downUrl=resultData.getData().getVersion_url();
                        int mVersion_code = DeviceUtils.getVersionCode(mContext);// 当前的版本号
                        if (mVersion_code < nVersion_code) {
                            // 显示提示对话
                            showNoticeDialog(resultData.getData());
                        } else {
                            if (isToast) {
                                Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    protected void error(int code, String str) {

                    }
                });
    }

    /**
     * 显示更新对话框
     *
     */
    private void showNoticeDialog(VersionBean vb) {
        // 构造对话框
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("更新提示");
//        builder.setMessage(version_info);
//        // 更新
//        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                mContext.startService(new Intent(mContext, DownLoadService.class));
//            }
//        });
//        // 稍后更新
//        builder.setNegativeButton("以后更新", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        Dialog noticeDialog = builder.create();
//        noticeDialog.show();
        String content=vb.getVersion_name()+"   "+vb.getVersion_size()+"m"+"\n";
        for (String s : vb.getVersion_info()) {
            content+=s+"\n";
        }

        new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("发现新版本")
                .setContentText(content)
                .setConfirmText("更新")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        mContext.startService(new Intent(mContext, DownLoadService.class));
//                        下载中对话框
//                        SweetAlertDialog sad=new SweetAlertDialog(mContext,SweetAlertDialog.PROGRESS_TYPE)
//                                .setTitleText("Downloading");
//                        sad.setCancelable(false);
//                        sad.show();

                    }
                })
                .setCancelText("稍后")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}

