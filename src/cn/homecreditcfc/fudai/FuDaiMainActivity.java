package cn.homecreditcfc.fudai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class FuDaiMainActivity extends Activity {

	private static final String DEFAULT_HOMEPAGE_URL = "http://www.homecreditcfc.cn/fudai/index.php";
	private WebView webView ;
	private ImageView wallet;
	private ImageView loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fu_dai_main);
		webView = (WebView) findViewById(R.id.webView);
		wallet = (ImageView) findViewById(R.id.wallet);
		loading = (ImageView) findViewById(R.id.loading);
		loadWebAppcation(webView);
	}

	
	@SuppressLint("SetJavaScriptEnabled")
	private void loadWebAppcation(WebView webview) {
		webView.getSettings().setJavaScriptEnabled(Boolean.TRUE);
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				webView.setVisibility(View.VISIBLE);
				wallet.destroyDrawingCache();
				wallet.setVisibility(View.GONE);
				loading.destroyDrawingCache();
				loading.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Toast.makeText(FuDaiMainActivity.this, "Page Started", Toast.LENGTH_SHORT).show();
				loading.setBackgroundResource(R.anim.loading);
				AnimationDrawable animate = (AnimationDrawable) loading.getBackground();
				animate.start();
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				Toast.makeText(FuDaiMainActivity.this, newProgress + "% is loaded", Toast.LENGTH_SHORT).show();
			}
			
		});
		webView.loadUrl(DEFAULT_HOMEPAGE_URL);
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fu_dai_main, menu);
		return true;
	}*/

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}
