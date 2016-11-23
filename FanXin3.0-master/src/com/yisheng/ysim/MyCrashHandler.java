package com.yisheng.ysim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义异常处理类
 */
public class MyCrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/crash_ay/log/";
    private static final String FILE_NAME = "crash";

    //log文件的后缀名
    private static final String FILE_NAME_SUFFIX = ".log";

    private static MyCrashHandler sInstance = new MyCrashHandler();

    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    @SuppressWarnings("unused")
	private UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

    //构造方法私有，防止外部构造多个实例，即采用单例模式
    private MyCrashHandler() {
    }

    public static MyCrashHandler getInstance() {
        return sInstance;
    }

    //这里主要完成初始化工作
    public void init(Context context) {
    	Log.e("----------", "初始化");
        //获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            //导出异常信息到SD卡中
            dumpExceptionToSDCard(ex);
            //这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
            uploadExceptionToServer(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //打印出当前调用栈信息
        ex.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
//        if (mDefaultCrashHandler != null) {
//            mDefaultCrashHandler.uncaughtException(thread, ex);
//        } else {
//        	android.os.Process.killProcess(android.os.Process.myPid());
//        }
        new Thread() {//必须在线程中显示提示信息
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "程序崩溃啦)_(",Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			
//				安全退出
				if(DemoApplication.getApp()==null){
					android.os.Process.killProcess(android.os.Process.myPid());
				}else{
                    DemoApplication.getApp().onTerminate();
				}
			}
		}.start();

    }

    @SuppressLint("SimpleDateFormat")
	private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "sdcard不存在，跳过");
                return;
            }
        }
        Log.w(TAG, "sdcard存在");
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        //以当前时间创建log文件
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

        
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出发生异常的时间
            pw.println(time);

            //导出手机信息
            try {
				dumpPhoneInfo(pw);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		        Log.e(TAG, "导出异常信息出错");   
			}

            pw.println();
            //导出异常的调用栈信息
            ex.printStackTrace(pw);

            pw.close();
            Log.e("异常写入成功：", time);     
    }

    @SuppressWarnings("deprecation")
	private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
        //应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

    private void uploadExceptionToServer(Throwable ex) {
//        MobclickAgent.reportError(mContext, ex);
    }

}
