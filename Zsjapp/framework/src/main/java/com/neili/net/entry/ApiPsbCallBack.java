package com.neili.net.entry;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.neili.net.progress.ProgressCancelListener;
import com.neili.net.progress.ProgressDialogHandler;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import rx.Subscriber;

/**
 * 带进度条
 * @param <T>
 */

public abstract class ApiPsbCallBack<T extends ResponseDataBase> extends Subscriber<T> implements ProgressCancelListener {

    private ProgressDialogHandler mProgressDialogHandler;

    private Context context;

    public ApiPsbCallBack( Context context) {
        this.context = context;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    private void showProgressDialog(){
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog(){
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }
    private void errorProgressDialog(String str){
        if (mProgressDialogHandler != null) {
            Message message = mProgressDialogHandler.obtainMessage(ProgressDialogHandler.ERROR_PROGRESS_DIALOG);
            if(str.trim().length()>0) message.obj=str;
            message.sendToTarget();
        }
    }
    private void errorProgressDialog(){
        errorProgressDialog("");
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
//        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            errorProgressDialog("网络状态异常");
        } else if (e instanceof ConnectException) {
            errorProgressDialog("网络状态异常");
        } else {
            errorProgressDialog();
        }
        e.printStackTrace();
        error(500, e.getMessage());
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        int code=t.getCode();
        if(code>=200 && code<300){
            dismissProgressDialog();
            success(t);
        }else{
            errorProgressDialog(t.getMsg());
            Log.e("数据返回错误:","错误信息:"+t.getCode()+t.getMsg());
            error(code, t.getMsg());
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            Toast.makeText(context,"取消..",Toast.LENGTH_SHORT).show();
            this.unsubscribe();
        }
    }

    protected abstract void success(T resultData);
    protected abstract void error(int code,String str);


}
