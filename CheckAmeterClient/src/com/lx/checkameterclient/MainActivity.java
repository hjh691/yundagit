package com.lx.checkameterclient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lx.checkameter.socket.Toasts;
import com.lx.checkameter.socket.WifiAdmin;
import com.lx.checkameter.socket.mSocketClient;
import com.lx.checkameter.toolkit.BasicTool;
import com.lx.checkameterclient.adapter.CAFragmentPagerAdapter;
import com.lx.checkameterclient.bean.NavigationItem;
import com.lx.checkameterclient.fragment.CAConnectLineCorrectFragment;
import com.lx.checkameterclient.fragment.CAErrorTestingFragment;
import com.lx.checkameterclient.fragment.CAHarmonicAnalysisFragment;
import com.lx.checkameterclient.fragment.CASettingFragment;
import com.lx.checkameterclient.fragment.CATakePhoneFragment;
import com.lx.checkameterclient.fragment.CAVoltAmphereTesting;
import com.lx.checkameterclient.fragment.CAWaveViewerFragment;
import com.lx.checkameterclient.view.NavigationBarHScrollView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	private String[] itemNames = { "参数设置", "伏安测试", "误差测试", "接线判别", "谐波分析", "波形显示", "照相取证" };//,"文件列表"

	// 导航栏视图对象
	private NavigationBarHScrollView mNavigationBarHScrollView;

	// 导航栏容器布局类对象
	LinearLayout mNavigationBarLayout;// mRadioGroup_content;

	public static  ViewPager mViewPager;

	// 菜单开关（zhu界面左上角ImageView）
	private ImageView mCallLeftMenuImageView;

	// 保存数据的按钮和WiFi连接状态指示图标。
	private ImageView mSaveDataImageView;

	private static ImageView mConnectedState;

	// 导航栏栏目对象集合
	private ArrayList<NavigationItem> mNavigationItemList = new ArrayList<NavigationItem>();

	// 导航栏栏目对象对应的Fragment对象集合
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	// 屏幕宽度
	private int mScreenWidth = 0;
	// 导航栏栏目宽度
	private int mNavigationItemWidth = 0;
	// 被选中的导航栏栏目索引值
	private static int mSelectedItemIndex = 0;

	// 左侧侧滑菜单
	private SlidingMenu mSlidingMenu;
	
	private static WifiAdmin mWifiAdmin ;
	private boolean wifi_state;//,socket_state;
	SQLiteDatabase sqldb;
    String sql;
    EditText text_pass;
    Spinner spinner1;
    private File path = new File("/sdcard/bdlx");// 创建目录
    private File file = new File("/sdcard/bdlx/sxxy.db");// 创建文件
    
	private Thread mThreadread=null,mthreadread_wa;
	private boolean loop_flag=true;
	private int sleeptime=1000;// 每隔5000毫秒触发
	private static TextView title_ver;
	private static ImageView bat_val=null;
	private static int vol_value;
	//public static Button btn_con;
	public static int netid=-1;
	public static SharedPreferences mPerferences;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        mWifiAdmin=new WifiAdmin(this);
		Log.d("tag","mScreenWidth= " +String.valueOf(mScreenWidth));
		// 初始化屏幕宽度和导航栏宽度
		mScreenWidth = BasicTool.getWindowsWidth(this);
		if (mScreenWidth >= 1280) {
			mNavigationItemWidth = mScreenWidth / 5;
		} else {
			mNavigationItemWidth = mScreenWidth / 4;
		}
		Declare.Serverisrun=false;
		//获取首选项数据
	    mPerferences=PreferenceManager.getDefaultSharedPreferences(this);
        
	    Declare.ssid=mPerferences.getString("ssid","lxdz320001");
	    Declare.ssid_pass=mPerferences.getString("ssid_pass","87654321");
		
		// 初始化导航栏以及主页内容区域视图
		initView();

		// 初始化左侧侧滑菜单
		initSlidingMenu();
		//btn_con=(Button)findViewById(R.id.connectHotSpotBtn);
		//
		if (!path.exists()) {// 目录存在返回false
            path.mkdirs();// 创建一个目录
           }
           if (!file.exists()) {// 文件存在返回false
            try {
               file.createNewFile();//创建文件 
            } catch (IOException e) {
         	   // TODO Auto-generated catch block
         	   e.printStackTrace();
         	   Toast.makeText(this, "数据库创建失败", Toast.LENGTH_SHORT).show();  
         	   
            }
           } 
         
         //创建一个数据库        
           sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
//           sql = "CREATE  TABLE if not exists sxxy_data (_id INTEGER PRIMARY KEY , dbid VARCHAR, cs_time VARCHAR,name VARCHAR,addr VARCHAR,xianl VARCHAR,taiz VARCHAR,jyy VARCHAR,jhy VARCHAR)"; 
           //======创建基本信息表
           
           sql = "CREATE  TABLE if not exists sxxy_data (_id INTEGER PRIMARY KEY , dbid VARCHAR," +
           		 "cs_time VARCHAR,name VARCHAR,addr VARCHAR,xianl VARCHAR,taiz VARCHAR,jyy VARCHAR," +
           		 "jhy VARCHAR,dbzs VARCHAR,dbdj VARCHAR,dylc VARCHAR,dllc VARCHAR,xbfs VARCHAR," +
           		 "dbcs VARCHAR,xbqs VARCHAR,dyxs VARCHAR,dlxs VARCHAR,fpxs VARCHAR,zdy1 VARCHAR," +
           		 "zdy2 VARCHAR,zdy3 VARCHAR,ua_fz VARCHAR,ia_fz VARCHAR,ub_fz VARCHAR,ib_fz VARCHAR," +
           		 "uc_fz VARCHAR,ic_fz VARCHAR,ja VARCHAR,jb VARCHAR,jc VARCHAR,hz VARCHAR,cosa VARCHAR," +
           		 "cosb VARCHAR,cosc VARCHAR,cos VARCHAR,pa VARCHAR,pb VARCHAR,pc VARCHAR,qa VARCHAR,qb VARCHAR," +
           		 "qc VARCHAR,sa VARCHAR,sb VARCHAR,sc VARCHAR,wc1 VARCHAR,wc2 VARCHAR,wc3 VARCHAR,wc4 VARCHAR," +
           		 "wc5 VARCHAR,dyxx VARCHAR,dlxx VARCHAR,jxpb_l VARCHAR,jxpb_c VARCHAR,ia_cj VARCHAR,ub_cj VARCHAR," +
           		 "ib_cj VARCHAR,uc_cj VARCHAR,ic_cj VARCHAR,srdl VARCHAR,gzxs VARCHAR,gzl VARCHAR,zbct VARCHAR,"+
           		 "zbpt VARCHAR,zbdl VARCHAR,db_type VARCHAR,db_mfrs VARCHAR,fsdl VARCHAR,psdl VARCHAR,gsdl VARCHAR,"+
           		 "zzyg VARCHAR,zzwg VARCHAR,fxyg VARCHAR,fxwg VARCHAR,wgwc1 VARCHAR,wgwc2 VARCHAR,wgwc3 VARCHAR,"+
           		 "wgwc4 VARCHAR,wgwc5 VARCHAR)";
         sqldb.execSQL(sql);	
         //========建立复合索引===============================
//         sql="CREATE INDEX if not exists idx_dbid ON sxxy_data(dbid,cs_time,name,addr,xianl,taiz,jyy,jhy)";
//         sqldb.execSQL(sql);	
           sql="CREATE INDEX if not exists idx_dbid ON sxxy_data(dbid)";
           sqldb.execSQL(sql);	
           sql="CREATE INDEX if not exists idx_cs_time ON sxxy_data(cs_time)";
           sqldb.execSQL(sql);	
    	
         
        
         //======创建谐波数据表=============================
         sql = "CREATE  TABLE if not exists xb51_data (_id INTEGER PRIMARY KEY , dbid VARCHAR,xb_id VARCHAR," + 		 
         		 "xb_zl VARCHAR,jb VARCHAR,xb2 VARCHAR,xb3 VARCHAR,xb4 VARCHAR," +
        		 "xb5 VARCHAR,xb6 VARCHAR,xb7 VARCHAR,xb8 VARCHAR,xb9 VARCHAR,xb10 VARCHAR," +
        		 "xb11 VARCHAR,xb12 VARCHAR,xb13 VARCHAR,xb14 VARCHAR,xb15 VARCHAR,xb16 VARCHAR," +
        		 "xb17 VARCHAR,xb18 VARCHAR,xb19 VARCHAR,xb20 VARCHAR,xb21 VARCHAR,xb22 VARCHAR," +
        		 "xb23 VARCHAR,xb24 VARCHAR,xb25 VARCHAR,xb26 VARCHAR,xb27 VARCHAR,xb28 VARCHAR," +
        		 "xb29 VARCHAR,xb30 VARCHAR,xb31 VARCHAR,xb32 VARCHAR,xb33 VARCHAR,xb34 VARCHAR," +
        		 "xb35 VARCHAR,xb36 VARCHAR,xb37 VARCHAR,xb38 VARCHAR,xb39 VARCHAR,xb40 VARCHAR," +
        		 "xb41 VARCHAR,xb42 VARCHAR,xb43 VARCHAR,xb44 VARCHAR,xb45 VARCHAR,xb46 VARCHAR," +
        		 "xb47 VARCHAR,xb48 VARCHAR,xb49 VARCHAR,xb50 VARCHAR,xb51 VARCHAR,xb52 VARCHAR,xb_szd VARCHAR)";
         sqldb.execSQL(sql);
         //========建立索引加快查询速度==============================
//         sql="CREATE INDEX if not exists idx_dbid ON xb51_data(dbid,xb_id)";
//         sqldb.execSQL(sql);
         sql="CREATE INDEX if not exists idx_dbid ON xb51_data(dbid)";
         sqldb.execSQL(sql);
         sql="CREATE INDEX if not exists idx_xb_id ON xb51_data(xb_id)";
         sqldb.execSQL(sql);
         
         //======创建波形数据表=============================
         sql = "CREATE  TABLE if not exists bx128_data (_id INTEGER PRIMARY KEY , dbid VARCHAR,bx_id VARCHAR," + 		 
         		 "bx1 VARCHAR DEFAULT '',bx2 VARCHAR,bx3 VARCHAR,bx4 VARCHAR,bx5 VARCHAR,bx6 VARCHAR,bx7 VARCHAR,bx8 VARCHAR," +
         		 "bx9 VARCHAR,bx10 VARCHAR,bx11 VARCHAR,bx12 VARCHAR,bx13 VARCHAR,bx14 VARCHAR,bx15 VARCHAR,bx16 VARCHAR," +
         		 "bx17 VARCHAR,bx18 VARCHAR,bx19 VARCHAR,bx20 VARCHAR,bx21 VARCHAR,bx22 VARCHAR,bx23 VARCHAR,bx24 VARCHAR," +
         		 "bx25 VARCHAR,bx26 VARCHAR,bx27 VARCHAR,bx28 VARCHAR,bx29 VARCHAR,bx30 VARCHAR,bx31 VARCHAR,bx32 VARCHAR," +
         		 "bx33 VARCHAR,bx34 VARCHAR,bx35 VARCHAR,bx36 VARCHAR,bx37 VARCHAR,bx38 VARCHAR,bx39 VARCHAR,bx40 VARCHAR," +
         		 "bx41 VARCHAR,bx42 VARCHAR,bx43 VARCHAR,bx44 VARCHAR,bx45 VARCHAR,bx46 VARCHAR,bx47 VARCHAR,bx48 VARCHAR," +
         		 "bx49 VARCHAR,bx50 VARCHAR,bx51 VARCHAR,bx52 VARCHAR,bx53 VARCHAR,bx54 VARCHAR,bx55 VARCHAR,bx56 VARCHAR," +
         		 "bx57 VARCHAR,bx58 VARCHAR,bx59 VARCHAR,bx60 VARCHAR,bx61 VARCHAR,bx62 VARCHAR,bx63 VARCHAR,bx64 VARCHAR," +
         		 "bx65 VARCHAR,bx66 VARCHAR,bx67 VARCHAR,bx68 VARCHAR,bx69 VARCHAR,bx70 VARCHAR,bx71 VARCHAR,bx72 VARCHAR," +
         		 "bx73 VARCHAR,bx74 VARCHAR,bx75 VARCHAR,bx76 VARCHAR,bx77 VARCHAR,bx78 VARCHAR,bx79 VARCHAR,bx80 VARCHAR," +
         		 "bx81 VARCHAR,bx82 VARCHAR,bx83 VARCHAR,bx84 VARCHAR,bx85 VARCHAR,bx86 VARCHAR,bx87 VARCHAR,bx88 VARCHAR," +
         		 "bx89 VARCHAR,bx90 VARCHAR,bx91 VARCHAR,bx92 VARCHAR,bx93 VARCHAR,bx94 VARCHAR,bx95 VARCHAR,bx96 VARCHAR," +
         		 "bx97 VARCHAR,bx98 VARCHAR,bx99 VARCHAR,bx100 VARCHAR,bx101 VARCHAR,bx102 VARCHAR,bx103 VARCHAR,bx104 VARCHAR," +
         		 "bx105 VARCHAR,bx106 VARCHAR,bx107 VARCHAR,bx108 VARCHAR,bx109 VARCHAR,bx110 VARCHAR,bx111 VARCHAR,bx112 VARCHAR," +
         		 "bx113 VARCHAR,bx114 VARCHAR,bx115 VARCHAR,bx116 VARCHAR,bx117 VARCHAR,bx118 VARCHAR,bx119 VARCHAR,bx120 VARCHAR," +
         		 "bx121 VARCHAR,bx122 VARCHAR,bx123 VARCHAR,bx124 VARCHAR,bx125 VARCHAR,bx126 VARCHAR,bx127 VARCHAR,bx128 VARCHAR)";
        		 
         sqldb.execSQL(sql);
         //========建立索引加快查询速度==============================
         sql="CREATE INDEX if not exists idx_dbid ON bx128_data(dbid)";
         sqldb.execSQL(sql);
         sql="CREATE INDEX if not exists idx_xb_id ON bx128_data(bx_id)";
         sqldb.execSQL(sql);		   
        
        sqldb.close();
		//初始化WiFi连接状态2016-3-14h------
		mWifiAdmin = new WifiAdmin(MainActivity.this);
        if(wifi_state==false){
        	if(mWifiAdmin.getWifiApState()==3){
       		 mWifiAdmin.closeWifi1(); 
            }
         	if(mWifiAdmin.checkState()==3){
        		Declare.ssid_connecte_flag=1;
        		/*
        		if(!mWifiAdmin.getSSID().equals(Declare.ssid) && !mWifiAdmin.getSSID().equals("") && mWifiAdmin.getSSID()!=null){
        			mWifiAdmin.closeWifi();
        			try
                	{
                		for(int i=0;i<30;i++)
                		{
                			if(mWifiAdmin.checkState()==1){
                				mWifiAdmin.openWifi();
                				//        	 mWifiAdmin = new WifiAdmin(MainActivity.this);
                				Declare.ssid_connecte_flag=1;
                				Toast.makeText(getApplicationContext(), "正在打开WIFI,请稍等", Toast.LENGTH_LONG).show();
                				break;
                			}
                			else{
                				Thread.sleep(500, 0);	
                			}
                		}
                	}catch (Exception e)
                    {
                    }
    			}*/
        	}
        	else if(mWifiAdmin.checkState()==1){
    			mWifiAdmin.openWifi();
    			//        	 mWifiAdmin = new WifiAdmin(MainActivity.this);
    			//Toast.makeText(getApplicationContext(), "正在打开WIFI开关,请稍等", Toast.LENGTH_LONG).show();
    		   DisplayToast("正在打开WIFI开关,请稍等");
    		   Declare.ssid_connecte_flag=1;
    		}
        }
      //初始化WiFi连接状态2016-3-14h------
        //CATakePhoneFragment.display();
        if(mThreadread==null){
        	loop_flag=true;
        	mThreadread=new mThreadread();
        	mThreadread.start();          
        }
	}
	public class mThreadread extends Thread{
		 @SuppressLint("HandlerLeak")
		@Override
		 public void run() {
		 // TODO Auto-generated method stub
			 int bat_refresh=0;
			 while(!Thread.currentThread().isInterrupted() && loop_flag)
		    	{		    	
		    	try {
			    	  // 每隔3000毫秒触发
			    		Thread.sleep(sleeptime);
			    	    } 
			    	catch (InterruptedException e) {
			    	     System.err.println("主界面刷新线程关闭");
	//		    	     this.interrupt();
	//	        	     DisplayToast("线程关闭"); 
			    	     loop_flag=false;
			    	     this.interrupt();
			    	     return;
			    		}
			    	Message msg = new Message();
			    	msg.what = 0;
			    	mHandler.sendMessage(msg);
			    	if((bat_refresh % 5)==0){
			    		Message msg1 = new Message();
			    		msg1.what=1;
			    		mHandler.sendMessage(msg1);
			    		bat_refresh=1;
			    	}
			    	bat_refresh++;
		    	 }
		 }
	}
	//初始化WiFi连接状态2016-3-14h------
	public void DisplayToast(String msg){
        	/*
    	    Toast toast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    	    //设置toast显示位置
    	    toast.setGravity(Gravity.TOP, 0, 220);
    	    toast.show();
    	    */
    	    Toasts.toast(this, msg, Toast.LENGTH_LONG,Gravity.TOP, 0, 720);
    } 
	//初始化WiFi连接状态2016-3-14h------
	private void initSlidingMenu() {
		mSlidingMenu = new SlidingMenuControlller(this).initSlidingMenu();
	}

	private void initView() {
		title_ver=(TextView)this.findViewById(R.id.txt_ver);
		bat_val=(ImageView)this.findViewById(R.id.bat_state);
		
		mNavigationBarHScrollView = (NavigationBarHScrollView) this.findViewById(R.id.mNavigationBarHScrollView);
		mNavigationBarLayout = (LinearLayout) this.findViewById(R.id.mNavigationBarLayout);

		mViewPager = (ViewPager) this.findViewById(R.id.mViewPager);

		mCallLeftMenuImageView = (ImageView) this.findViewById(R.id.callLeftMenuBtn);
		mCallLeftMenuImageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mSlidingMenu != null) {
					if (mSlidingMenu.isMenuShowing()) {
						mSlidingMenu.showContent();
					} else {
						mSlidingMenu.showMenu();
					}
				}
			}
		});

		mSaveDataImageView = (ImageView) this.findViewById(R.id.saveDataBtn);
		mSaveDataImageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				//Toast.makeText(MainActivity.this, "这里保存数据!"+String.valueOf(mSelectedItemIndex), Toast.LENGTH_LONG).show();
				if (mSelectedItemIndex==0){
					CASettingFragment.save_configAll();
				}
				if(mSelectedItemIndex==1){
					CAVoltAmphereTesting.btn_save.callOnClick();
				}
				if(mSelectedItemIndex==2){
					CAErrorTestingFragment.btn_savewc.callOnClick();
				}
				if(mSelectedItemIndex==3){
					CAConnectLineCorrectFragment.btn_savejx.callOnClick();
				}
				if(mSelectedItemIndex==4){
					CAHarmonicAnalysisFragment.btn_savexb.callOnClick();
				}
				if(mSelectedItemIndex==5){
					CAWaveViewerFragment.btn_savebx.callOnClick();
				}
			}
		});
		mConnectedState=(ImageView)this.findViewById(R.id.connectionStateIcon);
		mConnectedState.setImageResource(R.drawable.state_connection_interrupt);
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
	// 初始化导航栏各项目对应的Fragment对象
	private void initFragment() {
		if (this.fragments == null) {
			return;
		}
		//Log.d("tag", "intFragment ..............");
		// 清空
		this.fragments.clear(); 
		CASettingFragment settingFrag = new CASettingFragment();
		this.fragments.add(settingFrag);
		CAVoltAmphereTesting voltAmphereTestingFrag = new CAVoltAmphereTesting();
		this.fragments.add(voltAmphereTestingFrag);
		CAErrorTestingFragment errorTestingFrag = new CAErrorTestingFragment();
		this.fragments.add(errorTestingFrag);
		CAConnectLineCorrectFragment connectLineCorrectFrag = new CAConnectLineCorrectFragment();
		this.fragments.add(connectLineCorrectFrag);
		CAHarmonicAnalysisFragment harmonicAnalysisFrag = new CAHarmonicAnalysisFragment();
		this.fragments.add(harmonicAnalysisFrag);
		CAWaveViewerFragment waveViewerFrag = new CAWaveViewerFragment();
		this.fragments.add(waveViewerFrag);
		CATakePhoneFragment takePhoneFrag = new CATakePhoneFragment();
		this.fragments.add(takePhoneFrag);
		//CAFileshow cafileshow= new CAFileshow();
		//this.fragments.add(cafileshow);
		//Log.d("tag", "创建适配器 ..............");
		CAFragmentPagerAdapter mAdapetr = new CAFragmentPagerAdapter(getSupportFragmentManager(), fragments);
		// mViewPager.setOffscreenPageLimit(0);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setCurrentItem(0);
		selectNavigationItem(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
		@Override
		public void onPageSelected(int index) {
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(index);
			selectNavigationItem(index);
			switch(index){
			case 0:
				Declare.send_flag=6;
				break;
			case 1:
				Declare.send_flag=0;
				break;
			case 2:
				if (Declare.iserrortest){
					Declare.send_flag=2;
				}else{
					Declare.send_flag=17;
				}
				break;
			case 3:
				Declare.send_flag=1;
				break;
			case 4:
				Declare.send_flag=3;
				break;
			case 5:
				Declare.send_flag=5;
				break;	
			case 6:
				Declare.send_flag=6;
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
	//选中导航栏项目时执行的操作，主要是更新UI，实现视图切换
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
		// TODO Auto-generated method stub
		mNavigationBarLayout.removeAllViews();
		int itemCount = mNavigationItemList.size();
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
					//Toast.makeText(getApplicationContext(), mNavigationItemList.get(v.getId()).getName(),
					//		Toast.LENGTH_SHORT).show();
				}
			});
			mNavigationBarLayout.addView(itemTextView, i, params);
		}
	}
	private long mExitTime;
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(mSlidingMenu.isMenuShowing() ||mSlidingMenu.isSecondaryMenuShowing()){
				mSlidingMenu.showContent();
			}else {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(this, "在按一次退出",
							Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					mSocketClient.check_flag=false;
					Declare.Clientisconnect=false;
					loop_flag=false;
					Declare.Clientisconnect=false;
					Declare.Circle=false;
			     	//dialog.cancel();
			     	Declare.active=false;
			    	//Declare.Clientisrun=false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					if(Declare.Clientisrun==true)
					{						
						Intent intent=new Intent(MainActivity.this, mSocketClient.class);
						stopService(intent);
					}
					if (mSocketClient.mThreadCheck!=null){
						mSocketClient.mThreadCheck.interrupt();
					}
			    	//this.onDestroy();
					//onDestroy();
					this.finish();
				}
			}
			return true;
		}
		//拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static Handler mHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);
			  //DecimalFormat myformat_v= new DecimalFormat("00");
			  if(msg.what == 0)
			  {
				  //显示前端版本号
				  int v1=Declare.data_para[12+Declare.devicetype];
				  int v2=Declare.data_para[13+Declare.devicetype];
				  if((v1!=0)||(v2!=0)){
					  title_ver.setText(String.valueOf(v1)+"."+String.valueOf(v2));
				  }
				  //刷新网络连接状态，根据界面控制发送命令标志
				  if(Declare.Clientisconnect){
					  mConnectedState.setImageResource(R.drawable.state_connected);
					  mWifiAdmin.startScan();
					  netid=mWifiAdmin.getNetworkId();
					  Message msg2=new Message();
					  switch(mSelectedItemIndex){ 
					  case 1:
							msg2.what=0;
							//CAVoltAmphereTesting.mHandler_read.sendMessage(msg2);
						  break;
					  case 2:
						  msg2.what=0;
						  //CAErrorTestingFragment.mHandler_read.sendMessage(msg2);
						  break;
					  case 3:
						  msg2.what=0;
						  Declare.send_flag=1;
						  //CAConnectLineCorrectFragment.mHandler_read.sendMessage(msg2);
						  break;
					  case 4:
						msg2=new Message();
						msg2.what=0;
						//CAHarmonicAnalysisFragment.mHandler_read.sendMessage(msg2);
						break;
					  case 5:
						Message msg1 = new Message();
		                msg1.what = 0;
		                //CAWaveViewerFragment.mHandler_read.sendMessage(msg1);
		                break;
					  case 6:
						  //CATakePhoneFragment.display();
						  break;
					  }
					  //btn_con.setText("断          开");
					  //DisplayToast("通讯网络连接失败，请检查WIFI与SOKET通讯连接！！");
				  }else{
					  mConnectedState.setImageResource(R.drawable.state_connection_interrupt);
					  //btn_con.setText("连          接");
				  }
			  }
			  if(msg.what == 1)
			  {
				 
				 if(Declare.Clientisconnect){ 
				 if(Declare.devicetype==1){
				     vol_value=Declare.data_para[1];
				     bat_val.setVisibility(View.VISIBLE);
				     if(vol_value<0){
						 vol_value=0;
					 }
					 if(vol_value>100){
						 vol_value=100;
					 }
					 bat_png_display();
				 }else{
					 bat_val.setVisibility(View.GONE);//vol_value=100;
				 }
				  //DisplayToast("通讯网络连接正常！！");
			  }else{
				  bat_val.setVisibility(View.GONE);
				  //bat_val.setVisibility(View.VISIBLE);
				  //bat_png_display();
			  }
				 
			  }
				 
			  if(msg.what == 2)
			  {
				  //DisplayToast("正在打开WIFI网络！！");
			  }
		  }									
	 };
	@Override
	public void onDestroy() {
		super.onDestroy();
		mSocketClient.check_flag=false;
		loop_flag=false;
		Declare.Clientisconnect=false;
		Declare.Circle=false;
		//	 
     	//dialog.cancel();
		mSocketClient.check_flag=false;
     	Declare.active=false;
    	Declare.Clientisrun=false;
    	//notificationManager.cancelAll();
		//Declare.active=false;
		//finish();
    	//android.os.Process.killProcess(android.os.Process.myPid());//System.exit(0);
	}
	public void onStop(){
		super.onStop();
		//if(netid>0){
		//mWifiAdmin.disConnectionWifi(netid);
		//}
		//mSocketClient.check_flag=false;
		//Declare.Clientisconnect=false;
		//Declare.Circle=false;
		//if (Declare.Clientisconnect){
		//    SlidingMenuControlller.btn_con.callOnClick();
		//}
	}
	public void onStart(){
	super.onStart();
		//if(mSelectedItemIndex==6){
			//CATakePhoneFragment.display();
		//}
	}
	//用来回收由侧滑菜单启动的向导页返回的数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
//	        Log.d("TAG", str + " requestCode="+requestCode + " resultCode="+resultCode);
		
		if(resultCode == GuideActivity.FROM_MAIN_VIEW){
			//添加代码：由参数data中获取回传的数据，并且并入到当前参数设置的数据模型对象中
			//...
			CASettingFragment.get_config();
		}
		if(requestCode==0){
			if(resultCode==-1){
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			EditText txt_ssid=(EditText)findViewById(R.id.wifi_ssid);
			txt_ssid.setText(scanResult);
			Declare.ssid=scanResult;
			txt_ssid.setFocusable(true);
			Button btn_con=(Button)findViewById(R.id.connectHotSpotBtn);
			btn_con.callOnClick();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
    }
	
	//根据电池电量值来更新电量指示图片
	private  static void bat_png_display(){
		if(vol_value>0 && vol_value<10){
			bat_val.setImageResource(R.drawable.ima_bat_empty);	
		}
		else if(vol_value>=10 && vol_value<25){
			bat_val.setImageResource(R.drawable.ima_bat_10);
		}
		else if(vol_value>=25 && vol_value<40){
			bat_val.setImageResource(R.drawable.ima_bat_25);
		}
		else if(vol_value>=40 && vol_value<55){
			bat_val.setImageResource(R.drawable.ima_bat_40);
		}
		else if(vol_value>=55&& vol_value<70){
			bat_val.setImageResource(R.drawable.ima_bat_55);
		}
		else if(vol_value>=70 && vol_value<85){
			bat_val.setImageResource(R.drawable.ima_bat_70);
		}
		else if(vol_value>=85 && vol_value<96){
			bat_val.setImageResource(R.drawable.ima_bat_85);
		}
		else if(vol_value>=96){
			bat_val.setImageResource(R.drawable.ima_bat_full);
			//vol_value=0;
		}
   }
}
