package com.lx.checkameterclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.lx.checkameter.socket.Bluetoothclient;
import com.lx.checkameter.socket.mSocketClient;
import com.zxing.activity.CaptureActivity;

public class SlidingMenuControlller implements OnClickListener {

	private final Activity activity;
	SlidingMenu mSlidingMenu;
	//public static WifiAdmin mWifiAdmin=null;
	public static Button btn_con,btn_smzl;

	public SlidingMenuControlller(Activity activity) {
		this.activity = activity;
	}

	public SlidingMenu initSlidingMenu() {
		Log.d("tag", "initSlidingMenu.....");

		mSlidingMenu = new SlidingMenu(activity);
		mSlidingMenu.setMode(SlidingMenu.LEFT);// 设置左右滑菜单
		mSlidingMenu.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);// 设置要使菜单滑动，触碰屏幕的范围
		// mSlidingMenu.setTouchModeBehind(SlidingMenu.SLIDING_CONTENT);//设置了这个会获取不到菜单里面的焦点，所以先注释掉
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);// 设置阴影图片的宽度
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置阴影图片
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// SlidingMenu划出时主页面显示的剩余宽度
		mSlidingMenu.setFadeDegree(0.35F);// SlidingMenu滑动时的渐变程度
		mSlidingMenu.attachToActivity(activity, SlidingMenu.RIGHT);// 使SlidingMenu附加在Activity右边
		// mSlidingMenu.setBehindWidthRes(R.dimen.left_drawer_avatar_size);//设置SlidingMenu菜单的宽度
		mSlidingMenu.setMenu(R.layout.left_menu_fragment);// 设置menu的布局文件
		// mSlidingMenu.toggle();//动态判断自动关闭或开启SlidingMenu

		// mSlidingMenu.setSecondaryMenu(R.layout.profile_drawer_right);
		// mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
		Button btn_con=(Button)activity.findViewById(R.id.connectHotSpotBtn);
		btn_con.setText("连          接");
		Log.d("tag", "initSlidingMenu....middle.");

		mSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
			public void onOpened() {

			}
		});
		mSlidingMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
				// TODO Auto-generated method stub

			}
		});
		Log.d("tag", "initSlidingMenu....end.");

		initView();
		return mSlidingMenu;
	}

	Button connectHotSpotBtn;

	private void initView() {
		RelativeLayout testRatioItem = (RelativeLayout) activity.findViewById(R.id.testRatioItem);
		testRatioItem.setOnClickListener(this);
		
		RelativeLayout dataManageItem = (RelativeLayout) activity.findViewById(R.id.manageDataItem);
		dataManageItem.setOnClickListener(this);

		RelativeLayout debugDeviceItem = (RelativeLayout) activity.findViewById(R.id.debugDeviceItem);
		debugDeviceItem.setOnClickListener(this);
		
		RelativeLayout guidePageSwitchItem = (RelativeLayout) activity.findViewById(R.id.guidePageSwitchItem);
		guidePageSwitchItem.setOnClickListener(this);

		RelativeLayout helpItem = (RelativeLayout) activity.findViewById(R.id.helpItem);
		helpItem.setOnClickListener(this);
		
		RelativeLayout bluetoothItem = (RelativeLayout) activity.findViewById(R.id.BluetoothItem);
		bluetoothItem.setOnClickListener(this);
		
		EditText ssid=(EditText)activity.findViewById(R.id.wifi_ssid);
		ssid.setText(Declare.ssid);
		EditText wifi_password=(EditText)activity.findViewById(R.id.wifi_password);
		wifi_password.setText(Declare.ssid_pass);

		connectHotSpotBtn = (Button) this.activity.findViewById(R.id.connectHotSpotBtn);
		connectHotSpotBtn.setOnClickListener(this);
		connectHotSpotBtn.setTextColor(
				this.activity.getResources().getColorStateList(R.color.left_menu_connect_item_text_color));
		btn_smzl=(Button)activity.findViewById(R.id.btn_smzl);
		btn_smzl.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.testRatioItem:
			//mSlidingMenu.toggle(true);
			activity.startActivity(new Intent(activity, TestRatioActivity.class));
			// activity切换过场动画
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			mSlidingMenu.toggle(true);
			break;
		case R.id.manageDataItem:
			activity.startActivity(new Intent(activity, ManageDataActivity.class));
			// activity切换过场动画
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			mSlidingMenu.toggle(true);
			break;
		case R.id.debugDeviceItem:
			activity.startActivity(new Intent(activity, DebugDeviceActivity.class));
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			mSlidingMenu.toggle(true);
			break;
		case R.id.guidePageSwitchItem: //启动向导
//			this.showGuidePageFlag = !this.showGuidePageFlag;
//			if(this.showGuidePageFlag == true){
//				guidePageSwitchTextView.setText("关闭向导");
//			}else{
//				guidePageSwitchTextView.setText("启动向导");
//			}
//			helper.putGuidePageSwitchValue(showGuidePageFlag);
//			Log.d("TAG", "flag=" + String.valueOf(helper.getGuidePageSwitchValue()));
			
			Intent intent = new Intent(activity, GuideActivity.class);
			
			intent.putExtra(GuideActivity.ORIGIN_VIEW, GuideActivity.FROM_MAIN_VIEW);
			
			activity.startActivityForResult(intent,0);
			mSlidingMenu.toggle(true);
			break;
		case R.id.helpItem:
			activity.startActivity(new Intent(activity, HelpActivity.class));
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			mSlidingMenu.toggle(true);
			break;
			
		case R.id.BluetoothItem:
			activity.startActivity(new Intent(activity, Bluetoothclient.class));
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			mSlidingMenu.toggle(true);
			break;

		case R.id.connectHotSpotBtn:
			Log.d("tag", "我要连接热点。。。。。");
            btn_con=(Button)activity.findViewById(R.id.connectHotSpotBtn);
            if (btn_con.getText().toString().equals("连          接")){
				EditText txt_ssid=(EditText)activity.findViewById(R.id.wifi_ssid);
				if (txt_ssid.getText().length()!=0){
				  Declare.ssid=txt_ssid.getText().toString();
				}else{
				  Toast.makeText(activity, "热点名称不能为空",Toast.LENGTH_SHORT).show();
				  break;
				}
				EditText txt_password=(EditText)activity.findViewById(R.id.wifi_password);
				if((txt_password.getText().length()<8)||(!txt_password.getText().toString().equalsIgnoreCase("87654321"))){
					Toast.makeText(activity, "密码不正确！",Toast.LENGTH_SHORT).show();
					break;
				}else{
				  Declare.ssid_pass="87654321";//txt_password.getText().toString();
				}
				if(Declare.Clientisrun==false)
	      		{
					btn_con.setText("断          开");			
	      			Intent intent1=new Intent(activity, mSocketClient.class);
	      			activity.startService(intent1);
	      		}
				SharedPreferences.Editor mEditor = MainActivity.mPerferences.edit();
				//mEditor.putString("caiyangjiange",Caiyangjiange);
				//mEditor.putString("caiyangshichang", Caiyangshichang);
				//mEditor.putString("serverip", server_ip);
				//mEditor.putInt("port", port);
				mEditor.putString("ssid",Declare.ssid);
				mEditor.putString("ssid_pass","87654321");
				mEditor.commit();
            }else{
            	btn_con.setText("连          接");
            	if(Declare.Clientisconnect){
            	Declare.Clientisconnect=false;}
            	if(mSocketClient.check_flag){
            	mSocketClient.check_flag=false;}
            	Declare.Clientisrun=false;
            	
            	
            	
            	//Intent intent=new Intent(activity, mSocketClient.class);
      			//Service.stopService(intent);
            }
            mSlidingMenu.toggle(true);
			break;
		case R.id.btn_smzl:
			Intent openCameraIntent = new Intent(activity,CaptureActivity.class);
			openCameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
            activity.startActivityForResult(openCameraIntent, 0);
			break;
		default:
			break;
		}
	}
}
