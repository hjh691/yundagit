package com.lx.checkameterclient;

import com.lx.checkameter.base.BaseActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {

	private WebView mHelpWebView;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_help);
		TextView titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText("帮助");
		mHelpWebView = (WebView) this.findViewById(R.id.helpWebView);
		// 设置WebView属性，能够执行Javascript脚本
		mHelpWebView.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		mHelpWebView.loadUrl("file:///android_asset/html/lx_help.html");
		// 设置Web视图
		mHelpWebView.setWebViewClient(new HelloWebViewClient());
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	@Override
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mHelpWebView.canGoBack()) {
			mHelpWebView.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return false;
	}
	// Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
