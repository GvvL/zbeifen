package com.neili.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.neili.app.CONFIG;
import com.neili.framework.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * 图片加载 使用picasso
 */
public class ImgUtil {
    public static RequestCreator getRC(String url, Context context){
        return get(context)
                .load(url)
                .config(Bitmap.Config.RGB_565)
                .centerCrop()
//                .placeholder(R.drawable.ic_launcher)
//                .error(R.drawable.ic_launcher)
                .fit();
    }
//    红色：代表从网络下载的图片
//    黄色：代表从磁盘缓存加载的图片
//    绿色：代表从内存中加载的图片
    public static Picasso get(Context context){
        Picasso p=Picasso.with(context);
        p.setIndicatorsEnabled(CONFIG.DEBUG);
        return p;
    }

    public static void into(String url, Context context,ImageView v){
        getRC(url,context).into(v);
    }
    public static void intoWithNoCache(String url, Context context,ImageView v){
        getRC(url,context)
                .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .into(v);
    }
    public static void intoWithOnlyMem(String url, Context context,ImageView v){
        getRC(url,context)
                .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE)
                .into(v);
    }
    public static void intoWithHasTag(String url, ListView lv, ImageView v){
        Picasso picasso=get(lv.getContext());
        picasso.load(url)
                .config(Bitmap.Config.RGB_565)
                .centerCrop()
//                .placeholder(R.drawable.ic_launcher)
//                .error(R.drawable.ic_launcher)
                .fit()
                .tag(lv.getContext())
                .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE)
                .into(v);
        lv.setOnScrollListener(new PcScrollListener(lv.getContext()));


    }

    static class PcScrollListener implements AbsListView.OnScrollListener {
        private final Context context;

        public PcScrollListener(Context c) {
            this.context = c;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Picasso picasso=Picasso.with(this.context);
            if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                picasso.resumeTag(context);
            } else {
                picasso.pauseTag(context);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            // Do nothing.
        }
    }
}
