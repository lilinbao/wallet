package cn.homecreditcfc.fudai;

import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 *
 * MainActivity
 *
 * Apr 19, 2016 6:09:27 PM
 * 
 * @version 1.0.0
 * 
 * @author Boyce Li
 */
public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static Boolean isExit = false;
	private static final String URL_PREFIX_HTTP = "http://";
	private static final String URL_PREFIX_HTTPS = "https://";
	private WebView webView = null;
	private ImageButton btnNavLeft = null;
	private ImageButton btnNavRight = null;
	private ImageView loadingIndicator = null;
	private LinearLayout navigationController = null;
	private String cachePath = null;

	/**
	 * onCreate 应用启动执行的第一个方法
	 * 
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cachePath = getFilesDir().getAbsolutePath() + File.pathSeparator + BuildConfig.APP_CACAHE_DIRNAME;
		loadingIndicator = (ImageView) findViewById(R.id.imgViewLoading);
		loadWebApplication(BuildConfig.END_POINT_URL);
		bindNavigationButtonEvent();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "activity startup completed!");
		}
	}

	/**
	 * loadWebApplication 加载web页面的主方法
	 * 
	 * @param url
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void loadWebApplication(String url) {
		webView = (WebView) findViewById(R.id.webview);
		webView.setBackgroundColor(BuildConfig.WEB_VIEW_BACKGROUND_COLOR);
		webView.getSettings().setJavaScriptEnabled(BuildConfig.JAVASCRIPT_ENABLED);
		webView.getSettings().setCacheMode(MODE_PRIVATE);
		webView.getSettings().setAppCacheEnabled(BuildConfig.CACHE_ENABLED);
		webView.getSettings().setAppCachePath(cachePath);
		webView.getSettings().setDatabaseEnabled(BuildConfig.DATABASE_ENABLED);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setWebViewClient(new WebViewClient() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.webkit.WebViewClient#onReceivedError(android.webkit.
			 * WebView, int, java.lang.String, java.lang.String)
			 */
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				String errorMsg = null;
				ImageView errorView = (ImageView) findViewById(R.id.errorView);
				super.onReceivedError(view, errorCode, description, failingUrl);
				switch (errorCode) {
				case 401: // Unauthorized
					if (BuildConfig.DEBUG) {
						Log.e(TAG, "您访问的页面需要登录，请先登录!");
					}
					errorMsg = "您访问的页面需要登录，请先登录!";
					onScreenDebugMessage(errorMsg + "(" + description + ")");
					break;
				case 403: // Forbidden
					if (BuildConfig.DEBUG) {
						Log.e(TAG, "您访问的页面被禁止!");
					}
					errorMsg = "您访问的页面被禁止";
					onScreenDebugMessage(errorMsg + "(" + description + ")");
					break;
				case 404: // Not Found
					if (BuildConfig.DEBUG) {
						Log.e(TAG, "页面不存在!");
					}
					errorMsg = "您访问的页面不存在";
					onScreenDebugMessage(errorMsg + "(" + description + ")");
					break;
				case 500: // Internal Server Error
					if (BuildConfig.DEBUG) {
						Log.e(TAG, "服务器内部发生错误!");
					}
					errorMsg = "服务器错误，请稍后再试";
					onScreenDebugMessage(errorMsg + "(" + description + ")");
					break;
				default:
					if (BuildConfig.DEBUG) {
						Log.e(TAG, "发生未知错误!");
					}
					errorMsg = "发生未知错误！" + errorCode;
					onScreenDebugMessage(errorMsg + "(" + description + ")");
					break;
				}
				errorView.setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebViewClient#onReceivedSslError(android.webkit.
			 * WebView, android.webkit.SslErrorHandler,
			 * android.net.http.SslError)
			 */
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, "SSL error occur!");
				}
				super.onReceivedSslError(view, handler, error);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.webkit.WebViewClient#onPageStarted(android.webkit.
			 * WebView, java.lang.String, android.graphics.Bitmap)
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				onScreenDebugMessage("On Page Start Event");
				showLoadingIndicator(view);
				super.onPageStarted(view, url, favicon);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.
			 * WebView, java.lang.String)
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Page has been loaded!");
				}
				view.setVisibility(View.VISIBLE);
				onScreenDebugMessage("Page Load Finished Event Trigger");
				resetPageStateWhenPageFinished();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebViewClient#shouldOverrideUrlLoading(android.
			 * webkit.WebView, java.lang.String)
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Locale locale = new Locale(Locale.US.getDisplayCountry());
				if (!((url.toLowerCase(locale).startsWith(URL_PREFIX_HTTP))
						|| url.toLowerCase(locale).startsWith(URL_PREFIX_HTTPS))) {
					url = URL_PREFIX_HTTP + url;
				}
				view.loadUrl(url);
				return true;
			}

		});
		webView.setWebChromeClient(new WebChromeClient() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebChromeClient#onProgressChanged(android.webkit.
			 * WebView, int)
			 */
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				onScreenDebugMessage(newProgress + "% Loaded");
			}
		});
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "loading web page");
		}
		webView.loadUrl(url);
	}

	/**
	 * resetPageStateWhenPageFinished(页面加载后处理页面可视元素)
	 * 
	 * @description
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	protected void resetPageStateWhenPageFinished() {
		onScreenDebugMessage("start handling page finished event");

		ImageView walletLogo = (ImageView) findViewById(R.id.imgViewWallet);
		navigationController.setVisibility(View.VISIBLE);
		loadingIndicator.destroyDrawingCache();
		loadingIndicator.setVisibility(View.GONE);
		walletLogo.destroyDrawingCache();
		walletLogo.setVisibility(View.GONE);
		if (webView.canGoBack()) {
			onScreenDebugMessage("Web View Can Go Back");
			btnNavLeft.setEnabled(Boolean.TRUE);
			btnNavLeft.setAlpha(1.0f);
		} else {
			btnNavLeft.setAlpha(0.5f);
			btnNavLeft.setEnabled(Boolean.FALSE);
		}
		if (webView.canGoForward()) {
			onScreenDebugMessage("Web View Can Go Forward");
			btnNavRight.setAlpha(1.0f);
			btnNavRight.setEnabled(Boolean.TRUE);
		} else {
			btnNavRight.setAlpha(0.5f);
			btnNavRight.setEnabled(Boolean.FALSE);
		}
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "page reset completed");
		}
	}

	/**
	 * bindNavigationButtonEvent (为导航栏上的按钮绑定点击事件)
	 * 
	 * @description - home, refresh按钮常亮 - 当webView可以返回上一页时，返回按钮亮 -
	 *              当webView可以前进时，前进按钮亮
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	private void bindNavigationButtonEvent() {
		navigationController = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
		btnNavLeft = (ImageButton) findViewById(R.id.btn_nav_left);
		btnNavRight = (ImageButton) findViewById(R.id.btn_nav_right);
		ImageButton btnNavHome = (ImageButton) findViewById(R.id.btn_nav_home);
		ImageButton btnNavRefresh = (ImageButton) findViewById(R.id.btn_nav_refresh);
		btnNavLeft.setEnabled(Boolean.FALSE);
		btnNavRight.setEnabled(Boolean.FALSE);

		btnNavLeft.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					exitByClickingBackButtonTwice();
				}
			}
		});

		btnNavRight.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				if (webView.canGoForward()) {
					webView.goForward();
				}
			}
		});

		btnNavHome.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				loadWebApplication(BuildConfig.END_POINT_URL);
			}
		});

		btnNavRefresh.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				webView.reload();
			}
		});
	}

	/**
	 * 
	 * showLoadingIndicator 加载等待动画
	 * 
	 * @param view
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	private void showLoadingIndicator(WebView view) {
		loadingIndicator.setBackgroundResource(R.anim.loading);
		AnimationDrawable animate = (AnimationDrawable) loadingIndicator.getBackground();
		animate.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		processBackEvent(keyCode);
		return true;
	}

	/**
	 * processBackEvent 处理返回按键事件
	 * 
	 * @description 如果有浏览历史，则导航到上一页面； 否则提示退出
	 * @param keyCode
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	private void processBackEvent(int keyCode) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
		} else {
			exitByClickingBackButtonTwice();
		}
	}

	/**
	 * 
	 * exitByClickingBackButtonTwice 用户双击退出方法
	 * 
	 * @description 用户执行双击Back按键且连续间隔不大于2秒钟则自动退出程序
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	private void exitByClickingBackButtonTwice() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);

		} else {
			finish();
			System.exit(0);
		}
	}

	/**
	 * debugProcessMessage 开发模式显示调试信息
	 * 
	 * @description
	 * @param msg
	 * @return void
	 * @exception @since
	 *                1.0.0
	 */
	public void onScreenDebugMessage(String msg) {
		if (BuildConfig.DEBUG) {
			Toast toast = Toast.makeText(getApplicationContext(), "Processing Message", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.setText(msg);
			toast.show();
		}
	}
}
