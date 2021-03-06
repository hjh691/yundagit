package com.example.substationtemperature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.substationtemperature.adapter.STFragmentPagerAdapter;
import com.example.substationtemperature.adapter.UserDBHelper;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.NavigationItem;
import com.example.substationtemperature.base.UserInfo;
import com.example.substationtemperature.base.Utils;
import com.example.substationtemperature.floatwindow.fudongService;

import com.example.substationtemperature.network.ClientService;
import com.example.substationtemperature.view.NavigationBarHScrollView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;



public class MainActivity extends FragmentActivity {
	
	private String[] itemNames = { "首页","实时状态", "实时数据", "历史数据", "趋势图", "告警信息","短信日志", "当前告警" };//, "照相取证","文件列表"
	// 导航栏视图对象
	private static NavigationBarHScrollView mNavigationBarHScrollView;

	// 导航栏容器布局类对象
	static LinearLayout mNavigationBarLayout;// mRadioGroup_content;
	// 导航栏栏目对象集合
	private ArrayList<NavigationItem> mNavigationItemList = new ArrayList<NavigationItem>();

	public static  ViewPager mViewPager;
	// 左侧侧滑菜单
	private SlidingMenu mSlidingMenu;
	// 菜单开关（zhu界面左上角ImageView）
	private ImageView mCallLeftMenuImageView;
	
	// 导航栏栏目对象对应的Fragment对象集合
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	public static SharedPreferences mPerferences;
	//private static FragmentRealdata fragmentrealdata=new FragmentRealdata();
	
	// 屏幕宽度
	private static int mScreenWidth = 0;
	// 导航栏栏目宽度
	private int mNavigationItemWidth = 0;
	// 被选中的导航栏栏目索引值
	public static int mSelectedItemIndex = 0;
	
	private static Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//private static TextView title_ver;
	public static  Handler handler=null;
	public ScaleAnimation scale;
	public static UserInfo userinfo=new UserInfo();
	public  static UserDBHelper mUserHelper=null;//new UserDBHelper(dbcontext);
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,   WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED); 

        setContentView(R.layout.activity_main);
        Log.d("tag","mScreenWidth= " +String.valueOf(mScreenWidth));
		// 初始化屏幕宽度和导航栏宽度
		mScreenWidth = Utils.getWindowsWidth(this);
		if (mScreenWidth >= 1280) {
			mNavigationItemWidth = mScreenWidth / 5;
		} else {
			mNavigationItemWidth = mScreenWidth / 4;
		}
		handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case 0:
                	Declare.islogin=false;
                	//Declare.username="未登录";
                	if(!Declare.isremember_pw)
                		Declare.password="";
                	Toast.makeText(MainActivity.this, "安全退出",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    String info = (String) msg.obj;
                    Toast.makeText(MainActivity.this, info,Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                	info = (String) msg.obj;
                    Toast.makeText(MainActivity.this, info,Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        };
        //初始化时间系统变量：时间
        Declare.endtime=sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, -1);
        Declare.starttime=sdf.format(calendar.getTime());
        
        //读取首选项
        mPerferences=PreferenceManager.getDefaultSharedPreferences(this);
	    Declare.username=mPerferences.getString("username","未登录");
	    Declare.password=mPerferences.getString("password","");
	    Declare.isremember_pw=mPerferences.getBoolean("isremember",false);
	    Declare.isautologin=mPerferences.getBoolean("isautologin", false);
	    Declare.home_url=mPerferences.getString("homeurl", "http://192.168.10.67/_api/");
        
		/*if (savedInstanceState == null) {
        	getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,new PlaceholderFragment())
                    .commit();
        }else{
        	getFragmentManager().beginTransaction()
        	.replace(R.id.container, new FragmentHistraydata())
        	.commit();
        }*/
		// 初始化导航栏以及主页内容区域视图
		initView();
		
		// 初始化左侧侧滑菜单
		initSlidingMenu();
		
		//Networkutils.isNetworkConnected(this);
		Intent intent=new Intent(MainActivity.this,ClientService.class);
        startService(intent);
        
    }
	 
	 private void initView() {
		// TODO Auto-generated method stub
		 //title_ver=(TextView)this.findViewById(R.id.txt_titlebar_username);
		 //title_ver.setText("--"+Declare.username);
			//bat_val=(ImageView)this.findViewById(R.id.bat_state);
			
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
			setChangelView();
	}

	private void setChangelView() {
		// TODO Auto-generated method stub
		// 加载标签栏项目对象集合
		loadNavigationBarItemData();
		// 初始化标签栏视图
		initNavigationBar();
		// 加载各标签栏对应的fragment对象
		initFragment();
	}

	private void initFragment() {
		// TODO Auto-generated method stub
		if(this.fragments==null){
			return;
		}
		//清空
		fragments.clear();
		FragmentIndex mindex=new FragmentIndex();//主页
		fragments.add(mindex);
		FragmentPicture mrealstate=new FragmentPicture();
		fragments.add(mrealstate);
		FragmentRealdata mrealdata=new FragmentRealdata();
		fragments.add(mrealdata);
		FragmentHistraydata mhistraydata=new FragmentHistraydata();
		fragments.add(mhistraydata);
		FragmentChar mhistraychar=new FragmentChar();
		fragments.add(mhistraychar);
		FragmentAlert malert=new FragmentAlert();
		fragments.add(malert);
		FragmentDailyrecord mdailyrecord=new FragmentDailyrecord();
		fragments.add(mdailyrecord);
		Fragment_Realwarning mrealwarning=new Fragment_Realwarning();
		fragments.add(mrealwarning);
		//log.d("tag","创建适配器"
		STFragmentPagerAdapter mAdapter=new STFragmentPagerAdapter(getSupportFragmentManager(),fragments);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(0);
		selectNavigationItem(0);
		//ui切换监听事件
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int index){
				mViewPager.setCurrentItem(index);
				selectNavigationItem(index);
				switch(index){
				
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		});

	}
	//选中导航栏项目时执行的操作，主要是更新UI，实现视图切换
	public static void selectNavigationItem(int index) {
		// TODO Auto-generated method stub
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

	private void loadNavigationBarItemData() {
		// TODO Auto-generated method stub
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
   
	private void initSlidingMenu() {
		mSlidingMenu = new SlidingMenuControlller(this).initSlidingMenu();
	}
	
	//系统菜单栏菜单项加载
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_realdata, container, false);
            return rootView;
        }
    }
    
  //用来回收由侧滑菜单启动的向导页返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
//        Log.d("TAG", str + " requestCode="+requestCode + " resultCode="+resultCode);
	
	/*if(resultCode == GuideActivity.FROM_MAIN_VIEW){
		//添加代码：由参数data中获取回传的数据，并且并入到当前参数设置的数据模型对象中
		//...
		CASettingFragment.get_config();
	}*/
	if(requestCode==0){
		if(resultCode==-1){
		Bundle bundle = data.getExtras();
		String scanResult = bundle.getString("result");
		EditText txt_homeurl=(EditText)findViewById(R.id.home_url);
		txt_homeurl.setText(scanResult);
		Declare.home_url=scanResult;
		txt_homeurl.setFocusable(true);
		//Button btn_con=(Button)findViewById(R.id.connectHotSpotBtn);
		//btn_con.callOnClick();
		}
	}
	if(requestCode==1){
		TextView txt_user=(TextView)findViewById(R.id.txt_user);
		txt_user.setText(Declare.username);
	}
	super.onActivityResult(requestCode, resultCode, data);
}

 /*   
  //延时线程--延时3秒刷新界面一次；
    private class thread_delay implements Runnable{
		public void run() {
		boolean refresh_loop = false;
			// TODO Auto-generated method stub
			while (refresh_loop) {
				try {
					Thread.sleep(3000);//线程暂停10秒，单位毫秒
					//Mainmenu.this.setTitle(Declare.name_app+"：通讯设置"+"   "+Declare.wifi_tishixinxi);
					Message message=new Message();
					message.what=1;
					//handler_main.sendMessage(message);//发送消息
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
	}*/
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
				/*	mSocketClient.check_flag=false;
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
					//onDestroy();*/
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

    @Override
	protected void onDestroy(){
    	
    	SharedPreferences.Editor mEditor = mPerferences.edit();
		mEditor.putString("username",Declare.username);
		mEditor.putString("password", Declare.password);
		mEditor.putBoolean("isremmember",Declare.isremember_pw);
		mEditor.putBoolean("isautologin", Declare.isautologin);
		mEditor.putString("homeurl",Declare.home_url);
		mEditor.commit();
		SlidingMenuControlller.Logout();
		super.onDestroy();
		System.exit(0);
	}

}
