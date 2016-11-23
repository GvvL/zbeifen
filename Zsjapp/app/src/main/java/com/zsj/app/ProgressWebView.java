package com.zsj.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neili.net.NetUtil;
import com.neili.net.entry.ApiCallBack;
import com.neili.net.entry.ResponseDataItem;
import com.neili.utils.Md5Util;
import com.thefinestartist.utils.preferences.PreferencesUtil;
import com.umeng.message.UmengRegistrar;
import com.zsj.ui.LoginService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ProgressWebView extends LinearLayout {

	@BindView(R.id.web_view)
	WebView mWebView;

	@BindView(R.id.progress_bar)
	ProgressBar mProgressBar;

	private Context mContext;
	
	private String url;

	public WebView getmWebView(){
		return mWebView;
	}
	
//	private String errorHtml = "<html><head><meta charset='UTF-8'></head><body><br><br><br><br><br><br><br><div align='center' style='font-size: smaller'  onclick='window.android.refresh()' ><a href='http://www.baidu.com' style='text-decoration: none'>暂无数据 <br/> 点击调用android方法 </a></div></body></html>";

//	@JavascriptInterface
//	public void refresh() {
//		Toast.makeText(mContext, "js 调用方法", Toast.LENGTH_SHORT).show();
//	}


	public ProgressWebView(Context context) {
		this(context, null);
	}


	public ProgressWebView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ProgressWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.view_web_progress, this);
		ButterKnife.bind(this);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void loadUrl(String url) {
		if(url == null) {
			url = "http://baidu.com";
		}
		initWebview(url);
	}

	@JavascriptInterface
	private void initWebview(String url) {

		mWebView.addJavascriptInterface(this, "android");

		WebSettings webSettings = mWebView.getSettings();

		webSettings.setJavaScriptEnabled(true);
		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		// 设置默认缩放方式尺寸是far
		webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(false);
		webSettings.setDefaultFontSize(16);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setSaveFormData(true);
		webSettings.setLoadWithOverviewMode(true);
		mWebView.requestFocus();
		mWebView.loadUrl(url);
		
		// 设置WebViewClient
		mWebView.setWebViewClient(new WebViewClient() {
			// url拦截
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
				view.loadUrl(url);
				// 相应完成返回true
				return true;
				// return super.shouldOverrideUrlLoading(view, url);
			}

			// 页面开始加载
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
//				mProgressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			// 页面加载完成
			@Override
			public void onPageFinished(WebView view, String url) {
//				mProgressBar.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			// WebView加载的所有资源url
			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				view.loadData(errorHtml, "text/html; charset=UTF-8", null);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});
		
		// 设置WebChromeClient
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			// 处理javascript中的alert
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				Toast.makeText(mContext,"----------",Toast.LENGTH_SHORT).show();
				Log.e("~~~~~~~~~~~~~",message);
				return super.onJsAlert(view, url, message, result);
			};

			@Override
			// 处理javascript中的confirm
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
				return super.onJsConfirm(view, url, message, result);
			};

			@Override
			// 处理javascript中的prompt
			public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
				return super.onJsPrompt(view, url, message, defaultValue, result);
			};

			// 设置网页加载的进度条
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				mProgressBar.setProgress(newProgress);
				super.onProgressChanged(view, newProgress);
			}

			// 设置程序的Title
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		});
		mWebView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { // 表示按返回键

						mWebView.goBack(); // 后退

						// webview.goForward();//前进
						return true; // 已处理
					}
				}
				return false;
			}
		});
		//下载相关
		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//				Log.e("----",url);
//				Uri uri = Uri.parse(url);
//				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//				mContext.startActivity(intent);
//				Log.e("---",contentDisposition);
				String[] sts=contentDisposition.split("\"");
				String filename="新文件下载";
				if(sts.length>=2){
					filename=sts[1];
				}
				startDownload(url,filename,mimetype);
			}
		});
	}
	private void startDownload(String url,String filename,String mimetype) {
		DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		request.setMimeType(mimetype);
		request.setVisibleInDownloadsUi(true);
		request.setDestinationInExternalFilesDir(mContext,
				Environment.DIRECTORY_DOWNLOADS, filename);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		dm.enqueue(request);
	}

	public boolean canBack() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return false;
		}
		return true;
	}
	@JavascriptInterface
	public void fun2(String emp_no,String password) {
		String device_token = UmengRegistrar.getRegistrationId(mContext);
		toLogin(emp_no,password,device_token);
	}
	public void toLogin(String username, String password,String device_token){
		LoginService service = NetUtil.getInstance(mContext).createService(LoginService.class);
		service.login(username, new Md5Util().getMD5ofStr(password),device_token)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new ApiCallBack<ResponseDataItem<String>>() {
					@Override
					protected void success(ResponseDataItem<String> resultData) {
						PreferencesUtil.put("token",resultData.getData());
//						Toast.makeText(mContext,resultData.getData()+"--",Toast.LENGTH_SHORT).show();
					}

					@Override
					protected void error(int code, String str) {
						Toast.makeText(mContext,"登录失败",Toast.LENGTH_SHORT).show();
					}
				});
	}


}
