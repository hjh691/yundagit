package com.lx.checkameterclient;

import com.lx.checkameter.toolkit.SharedPreferencesHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class SplashActivity extends Activity {
	private AlphaAnimation start_anima;
	View view;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		initView();
		initData();
	}

	private void initData() {
		start_anima = new AlphaAnimation(0.3f, 1.0f);
		start_anima.setDuration(2000);
//		start_anima.setDuration(50);
		view.startAnimation(start_anima);
		start_anima.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				redirectTo();
			}
		});
	}

	private void initView() {

	}

	private void redirectTo() {
		SharedPreferencesHelper helper = new SharedPreferencesHelper(this,
				SharedPreferencesHelper.CONFIGURATION_FILE_NAME);

		// Log.d("TAG", "start flag=" +
		// String.valueOf(helper.getGuidePageSwitchValue()));

		// 读取向导开关标志，如果首次运行，文件不存在，则会返回true，也就是默认为启动向导页
		boolean show_guide_page = helper.getGuidePageSwitchValue();

		// 判断是否启动向导页
		if (show_guide_page == true) {
			Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
			// 将向导页的启动源标志为Splash页面
			intent.putExtra(GuideActivity.ORIGIN_VIEW, GuideActivity.FROM_SPLASH_VIEW);

			startActivity(intent);
		} else {
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
		}
		finish();
	}
}
