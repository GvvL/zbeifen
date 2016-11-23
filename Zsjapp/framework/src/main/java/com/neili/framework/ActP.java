package com.neili.framework;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class ActP<T extends ViewDelegate> extends AppCompatActivity{
    protected T viewDelegate;
    protected boolean transbar=false;


    public ActP() {

        try {
            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            Class<T> entityClass = (Class<T>) params[0];
            viewDelegate =entityClass.newInstance();
//            viewDelegate = getDelegateClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("create IDelegate error");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("create IDelegate error");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(transbar){
            makeBarTranslucent();
        }
        viewDelegate.create(getLayoutInflater(), null, savedInstanceState);
        setContentView(viewDelegate.getRootView());
        viewDelegate.initWidget();
        preliminary();
        bindEvenListener();
    }
    /**
     * 事件绑定
     */
    protected void bindEvenListener() {
    }
    /**
     * 数据准备
     */
    protected void preliminary(){

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewDelegate == null) {
            try {
                Type genType = getClass().getGenericSuperclass();
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                Class<T> entityClass = (Class<T>) params[0];
                viewDelegate =entityClass.newInstance();
//                viewDelegate = getDelegateClass().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("create IView error");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("create IView error");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewDelegate.getOptionsMenuId() != 0) {
            getMenuInflater().inflate(viewDelegate.getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        viewDelegate.deinitWidget();
        viewDelegate = null;
        super.onDestroy();
    }
    //    protected abstract Class<T> getDelegateClass();
    public void makeBarTranslucent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // 透明上部状态栏
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明下部导航栏
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
