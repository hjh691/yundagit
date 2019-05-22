package com.lx.checkameterclient;

import java.io.File;
import java.util.ArrayList;

import com.lx.checkameter.toolkit.BasicTool;
import com.lx.checkameterclient.adapter.CAFragmentPagerAdapter;
import com.lx.checkameterclient.bean.NavigationItem;
import com.lx.checkameterclient.fragment.CAConnectLineCorrectFragment;
import com.lx.checkameterclient.fragment.CAErrorTestingFragment;
import com.lx.checkameterclient.fragment.CASettingFragment;
import com.lx.checkameterclient.fragment.CAVoltAmphereTesting;
import com.lx.checkameterclient.fragment.DDConnectLineCorrectFragment;
import com.lx.checkameterclient.fragment.DDErrorTestingFragment;
import com.lx.checkameterclient.fragment.DDHarmonicAnalysisFragment;
import com.lx.checkameterclient.fragment.DDSettingFragment;
import com.lx.checkameterclient.fragment.DDVoltAmphereTesting;
import com.lx.checkameterclient.fragment.DDWaveviewerFragment;
import com.lx.checkameterclient.view.FileShow;
import com.lx.checkameterclient.view.NavigationBarHScrollView;
import com.lx.checkameterclient.view.confirm;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ManageDataDetailActivity extends FragmentActivity {

	private String[] itemNames = { "参数设置", "伏安测试", "误差测试", "接线判别","谐波分析","波形显示"};//
	// 导航栏视图对象
	private NavigationBarHScrollView mNavigationBarHScrollView;

	// 导航栏容器布局类对象
	LinearLayout mNavigationBarLayout;// mRadioGroup_content;

	private ViewPager mViewPager;
	// 导航栏栏目对象集合
	private ArrayList<NavigationItem> mNavigationItemList = new ArrayList<NavigationItem>();

	// 导航栏栏目对象对应的Fragment对象集合
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	// 屏幕宽度
	private int mScreenWidth = 0;
	// 导航栏栏目宽度
	private int mNavigationItemWidth = 0;
	// 被选中的导航栏栏目索引值
	private int mSelectedItemIndex = 0;
	
	public static String dbid;
	private Button but_mod;
	
	private Button btnVideoBrowse;
	private Button btnImgBrowse;
	private String videoPath;
	private String imgPath;
	private String DBID;
	
	int DO_ID=1;//确认框操作标志
	Dialog dialog,dialog1;
	

	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		this.setContentView(R.layout.activity_manage_data_detail);
		Intent i=getIntent();
		Bundle b=i.getBundleExtra("ameterno");
		dbid=b.getString("dbid");
		DBID=dbid;
		but_mod=(Button)findViewById(R.id.saveDataBtn);
        
        btnImgBrowse = (Button) findViewById(R.id.browsePictureBtn);
        btnVideoBrowse = (Button) findViewById(R.id.browseVedioBtn);
        but_mod.setOnClickListener(new But_bb_Listener());
        //but_cal.setOnClickListener(new But_bb_Listener());        
		btnImgBrowse.setOnClickListener(new But_bb_Listener());		
		btnVideoBrowse.setOnClickListener(new But_bb_Listener());

		TextView titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText("电表数据详情");

		TextView backBtn = (TextView) findViewById(R.id.back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}
		});

		// 初始化屏幕宽度和导航栏宽度
		mScreenWidth = BasicTool.getWindowsWidth(this);
		//if (mScreenWidth > 1000) {
		//	mNavigationItemWidth = mScreenWidth / itemNames.length;
		//} else {
		//	mNavigationItemWidth = mScreenWidth / 4;
		//}
		if (mScreenWidth >= 1280) {
			mNavigationItemWidth = mScreenWidth / 5;
		} else {
			mNavigationItemWidth = mScreenWidth / 4;
		}
		
		

		// 初始化导航栏以及主页内容区域视图
		initView();

	}

	private void initView() {
		mNavigationBarHScrollView = (NavigationBarHScrollView) this.findViewById(R.id.dataDetailNavBarHScrollView);
		mNavigationBarLayout = (LinearLayout) this.findViewById(R.id.dataDetailNavBarLayout);

		mViewPager = (ViewPager) this.findViewById(R.id.dataDetailViewPager);

		setChangelView();
	}

	/**
	 * 当栏目项发生变化时候调用
	 */
	private void setChangelView() {
		// 加载标签栏项目对象集合
		loadNavigationBarItemData();
		// 初始化标签栏视图
		initNavigationBar();

		// 加载各标签栏对应的fragment对象
		initFragment();
	}

	private void loadNavigationBarItemData() {
		Log.d("tag", "load item data...........");
		// TODO Auto-generated method stub
		if (mNavigationItemList != null && mNavigationItemList.size() == 0) {

			for (int i = 0; i < itemNames.length; i++) {
				NavigationItem currentItem = new NavigationItem(i, itemNames[i], i, 1);
				mNavigationItemList.add(currentItem);

				Log.d("tag", currentItem.toString());
			}
		}
	}
	private void initNavigationBar() {
		// TODO Auto-generated method stub tiger monkey panda bear rabbit cow horse sheep goat fish bird 

		mNavigationBarLayout.removeAllViews();
		int itemCount = mNavigationItemList.size();
		//
		mNavigationBarHScrollView.setParam(this, mScreenWidth, mNavigationBarLayout, null, null, null, null);
		for (int i = 0; i < itemCount; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mNavigationItemWidth,
					LayoutParams.WRAP_CONTENT);
			params.leftMargin = 5;
			params.rightMargin = 5;
			TextView itemTextView = new TextView(this);
			// 设定导航栏栏目字体尺寸、和居中显示
			itemTextView.setTextAppearance(this, R.style.navigation_bar_item_text);
			// 设定导航栏栏目的不同状态下的背景图形的尺寸、形状和字体
			itemTextView.setBackgroundResource(R.drawable.navigation_bar_item_bg);
			itemTextView.setGravity(Gravity.CENTER);
			itemTextView.setPadding(5, 5, 5, 5);
			itemTextView.setId(i);
			itemTextView.setText(mNavigationItemList.get(i).getName());
			// 设定导航栏栏目在选中和未选中两种状态下的字体颜色
			itemTextView.setTextColor(getResources().getColorStateList(R.color.navigation_bar_item_text_color));
			if (mSelectedItemIndex == i) {
				itemTextView.setSelected(true);
			}
			itemTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (int i = 0; i < mNavigationBarLayout.getChildCount(); i++) {
						View localView = mNavigationBarLayout.getChildAt(i);
						if (localView != v) {
							localView.setSelected(false);
							// Log.d("tag", "normal " + i);
						} else {
							localView.setSelected(true);
							mViewPager.setCurrentItem(i);
							// Log.d("tag", "selected " + i);
						}
					}
					Toast.makeText(getApplicationContext(), mNavigationItemList.get(v.getId()).getName(),
							Toast.LENGTH_SHORT).show();
				}
			});
			mNavigationBarLayout.addView(itemTextView, i, params);
		}
	}
	// 初始化导航栏各项目对应的Fragment对象
	private void initFragment() {
		if (this.fragments == null) {
			return;
		}
		//Log.d("tag", "intFragment ..............");
		// 清空
		this.fragments.clear();
		DDSettingFragment settingFrag = new DDSettingFragment();
		this.fragments.add(settingFrag);
		DDVoltAmphereTesting voltAmphereTestingFrag = new DDVoltAmphereTesting();
		this.fragments.add(voltAmphereTestingFrag);
		DDErrorTestingFragment errorTestingFrag = new DDErrorTestingFragment();
		this.fragments.add(errorTestingFrag);
		DDConnectLineCorrectFragment connectLineCorrectFrag = new DDConnectLineCorrectFragment();
		this.fragments.add(connectLineCorrectFrag);
		DDHarmonicAnalysisFragment harmonicanalysisFrag = new DDHarmonicAnalysisFragment();
		this.fragments.add(harmonicanalysisFrag);
		DDWaveviewerFragment waveviewerFrag = new DDWaveviewerFragment();
		this.fragments.add(waveviewerFrag);
		//Log.d("tag", "创建适配器 ..............");
		CAFragmentPagerAdapter mAdapetr = new CAFragmentPagerAdapter(getSupportFragmentManager(), fragments);
		// mViewPager.setOffscreenPageLimit(0);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(index);
				selectNavigationItem(index);
				switch(index){
				case 0:
					DO_ID=1;
					break;
				
				case 3:
					DO_ID=2;
					break;
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		//Log.d("tag", "intFragment .........end.....");
	}
	// 选中导航栏项目时执行的操作，主要是更新UI，实现视图切换
	private void selectNavigationItem(int index) {
		mSelectedItemIndex = index;
		for (int i = 0; i < mNavigationBarLayout.getChildCount(); i++) {
			View checkView = mNavigationBarLayout.getChildAt(index);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			mNavigationBarHScrollView.smoothScrollTo(i2, 0);
		}
		// 判断是否选中
		for (int j = 0; j < mNavigationBarLayout.getChildCount(); j++) {
			View checkView = mNavigationBarLayout.getChildAt(j);
			boolean ischeck;
			if (j == index) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}
	//=========报表界面查询系列按钮响应=============
    public class But_bb_Listener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
    		/* case R.id.but_que:   
    			 DO_ID=0;
    			 QueryRecord(spi1_id,spi2_id);
//    			 inti_progress();
    	     break;
    	     //=================================
    		 case R.id.but_del:
//     			 String time ="2010年12月08日11时17分00秒";
//     			String time=getStrTime(String.valueOf(time1/1000));
//     			TextView text=(TextView)findViewById(R.id.editText2);
//     			text.setText(time);
    			 dialog1 = new confirm(ManageDataDetailActivity.this,R.style.MyDialog);       	        	       	
    	         dialog1.show();
    	         DO_ID=0;
    	         dialog1.findViewById(R.id.textview2); 
    	         Button but_qr = (Button)dialog1.findViewById(R.id.but_qr);   
                 Button but_qx = (Button)dialog1.findViewById(R.id.but_qx); 
                 but_qr.setOnClickListener(new ButtonListener1());   
                 but_qx.setOnClickListener(new ButtonListener1());  
     			 
    		 break;*/
    		 //=================================
    		 case R.id.saveDataBtn:
    			 dialog1 = new confirm(ManageDataDetailActivity.this,R.style.MyDialog);       	        	       	
    	         dialog1.show();
    	         //DO_ID=1;
    	         TextView text1 = (TextView)dialog1.findViewById(R.id.textview2);
    	         text1.setText("确认修改该项记录吗?");
    	         Button but_qr1 = (Button)dialog1.findViewById(R.id.but_qr);   
                 Button but_qx1 = (Button)dialog1.findViewById(R.id.but_qx); 
                 but_qr1.setOnClickListener(new ButtonListener1());   
                 but_qx1.setOnClickListener(new ButtonListener1());  
    		 break;
    		 //=================================
    		 //case R.id.but_cal:
    		 //break;
    		 //===浏览图片
    		 case R.id.browsePictureBtn:
 				imgShow();
  				break;
 			//====浏览录像
 			case R.id.browseVedioBtn:
 				videoShow();
 				break;
    		 }
		} 
	}
	//=========确认界面按钮响应========
    public class ButtonListener1 implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
    		 case R.id.but_qr:
    			 switch(DO_ID){
    			 case 0:
    				 String path =Environment.getExternalStorageDirectory().getAbsolutePath()
						+ "/bdlx/.Gallery/"+DBID; 
    				 System.out.println(path);			 
    				 //deleteDirectory(path);
    				 //DeleteRecord();
    			 break;
    			 case 1:
    				 DDSettingFragment.UpdateRecord();
    			 break;
    			 case 2:
    				 DDConnectLineCorrectFragment.UpdateRecord();
    			 break;
    			 }
         		dialog1.cancel();
    	     break;
    	     //=================================
    		 case R.id.but_qx:
    			 dialog1.cancel();//
    		 break;
    		 }
		} 
	}
	// 浏览图片
	public void imgShow() {
		imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/bdlx/.Gallery/"+DBID+"/img/";
		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("flag", 1);
		intent.putExtra("path", imgPath);
		intent.setClass(ManageDataDetailActivity.this, FileShow.class);
		startActivityForResult(intent, 2);
		//startActivity(intent);
	}
	/**
	 * 浏览录像
	 */
	public void videoShow() {
		videoPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/bdlx/.Gallery/"+DBID+"/video/";
		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("flag", 1);
		intent.putExtra("path", videoPath);
		intent.setClass(ManageDataDetailActivity.this, FileShow.class);
		startActivityForResult(intent, 4);
		//startActivity(intent);
	}
}
