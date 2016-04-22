package cn.homecreditcfc.fudai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/**
 *
 * SplashActivity
 * An full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * Apr 19, 2016 6:08:27 PM
 * 
 * @version 1.0.0
 *
 * @author Boyce Li
 */
public class SplashActivity extends Activity {

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 500;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		onStartUp();
	}
	
	/**
	 * onStartUp 
	 * @description 开场欢迎屏幕
	 * @return void
	 * @exception
	 * @since  1.0.0
	 */
	private void onStartUp(){
		new Handler().postDelayed(new Runnable(){
            public void run() {  
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(i);
                SplashActivity.this.finish();
            }  
        }, AUTO_HIDE_DELAY_MILLIS);
	}
}
