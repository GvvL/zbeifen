package com.neili.app;



import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.thefinestartist.Base;


/**
 * 主程序
 */
public class App extends Application implements ActivityLifecycleCallbacks{
	private final static String AYPREFERENCES="aydcRP";

	private static App instance;
	


//	public WeakReference<Activity> currActivity;


	public static boolean islogin=false;
	public static boolean isfirst=false;
	public static boolean issafe=false;
	

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Base.initialize(this);//util
		TypefaceProvider.registerDefaultIconSets();//bootstrap
		instance = this;
		MyCrashHandler handler = MyCrashHandler.getInstance();
		handler.init(getApplicationContext());
		registerActivityLifecycleCallbacks(this);
	}




	/**
	 * 单例
	 * @return 
	 */
	public static App getInstance() {
		 return instance;
	}
	


	/*
	 * 初始
	 */
	/*
	public static void initImageLoader(Context context) {
        //缓存文件的目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache"); 
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽 
                .threadPoolSize(3) //线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) //你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(30 * 1024 * 1024)  // 50 Mb sd卡(本地)缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径  
                .imageDownloader(new BaseImageDownloader(context, 1 * 1000, 1 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
                .writeDebugLogs() // Remove for release app
                .build();
        //全局初始化此配置  
        ImageLoader.getInstance().init(config);
    }
    */




	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void onActivityStarted(Activity activity) {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void onActivityResumed(Activity activity) {

	}



	@Override
	public void onActivityPaused(Activity activity) {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void onActivityStopped(Activity activity) {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void onActivityDestroyed(Activity activity) {
		// TODO 自动生成的方法存根
		
	}
	


	


}
