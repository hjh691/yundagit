package com.lx.checkameterclient;

import java.util.ArrayList;

import com.lx.checkameter.toolkit.SharedPreferencesHelper;
import com.lx.checkameterclient.view.MyRadioGroup;
import com.lx.checkameterclient.view.MyRadioGroup.OnCheckedChangeListener;
import com.zxing.activity.CaptureActivity;

import android.widget.CheckBox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GuideActivity extends Activity implements OnPageChangeListener {

	private ArrayList<View> mPageViews;
	private LayoutInflater mInflater;
	private LinearLayout mGroups;
	private FrameLayout mMain;
	private ViewPager mViewPager;
	private ImageView[] mImageViews;

	private ImageView mDiagramImageView;
	private MyRadioGroup mAmeterStandardRadioGroup;

	// 标志：用于获取向导页面是有splash页面启动的还是由主页面的侧滑菜单项启动的
	public static final int FROM_SPLASH_VIEW = 100;
	public static final int FROM_MAIN_VIEW = 101;

	// 标志：向导页向主页面回传参数是使用的resultCode码常量
	public static final int BACK_RESULT_CODE = 200;

	public static final String ORIGIN_VIEW = "origin_view";
	private int originViewFlag;
	//控件变量
	private TextView bhsz,cssz,qssz,fpsz,dlsz;
	private static Spinner spinner;
	private int position1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取来源页面的的标志
		Intent intent = this.getIntent();
		this.originViewFlag = intent.getIntExtra(ORIGIN_VIEW, FROM_SPLASH_VIEW);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mInflater = LayoutInflater.from(this);
		mPageViews = new ArrayList<View>();
		mPageViews.add(mInflater.inflate(R.layout.guide_page_0_setting, null));
		mPageViews.add(mInflater.inflate(R.layout.guide_page_1_wiring_diagram, null));
		// mPageViews.add(mInflater.inflate(R.layout.item_2, null));
		// mPageViews.add(mInflater.inflate(R.layout.item_3, null));
		mMain = (FrameLayout) mInflater.inflate(R.layout.activity_guide, null);
		mGroups = (LinearLayout) mMain.findViewById(R.id.viewGroup);
		mViewPager = (ViewPager) mMain.findViewById(R.id.guidePages);
		mImageViews = new ImageView[mPageViews.size()];
		for (int i = 0; i < mPageViews.size(); i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(new LayoutParams(20, 20));
			mImageViews[i] = iv;
			if (i == 0) {
				mImageViews[i].setBackgroundResource(R.drawable.guide_page_indicator_focused);
			} else {
				mImageViews[i].setBackgroundResource(R.drawable.guide_page_indicator);
			}
			mGroups.addView(mImageViews[i]);
		}
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(this);
		setContentView(mMain);
		//提取控件并进行相关操作
		Button scanbarcodebtn=(Button)mPageViews.get(0).findViewById(R.id.ameterNoBtn);
		scanbarcodebtn.setOnClickListener(new btn_scan_click());
		bhsz=(EditText)mPageViews.get(0).findViewById(R.id.guideameterNumberET);
		cssz=(EditText)mPageViews.get(0).findViewById(R.id.guideameterConstantValueET);
		qssz=(EditText)mPageViews.get(0).findViewById(R.id.guidecoilsNumberET);
		fpsz=(EditText)mPageViews.get(0).findViewById(R.id.guidevoltRatioET);
		dlsz=(EditText)mPageViews.get(0).findViewById(R.id.guideamphereRatioET);
		spinner = (Spinner) mPageViews.get(0).findViewById(R.id.guideamphereRangeSpinner);
		// 电表制式选项改变事件
		mAmeterStandardRadioGroup = (MyRadioGroup) mPageViews.get(0).findViewById(R.id.ameterStandRadioGroup);
		/*处理向导页的向导开关复选框*/
		CheckBox guideSwitchCheckBox = (CheckBox) mPageViews.get(0).findViewById(R.id.guidePageSwitchCheckBox);
		// 根据系统配置变量值修改启动向导栏目显示
		final SharedPreferencesHelper helper = new SharedPreferencesHelper(this,
				SharedPreferencesHelper.CONFIGURATION_FILE_NAME);
		boolean showGuidePageFlag = helper.getGuidePageSwitchValue();
		Log.d("TAG", "switch = "+ showGuidePageFlag);
		if (showGuidePageFlag == true) {
			guideSwitchCheckBox.setChecked(true);
		} else {
			guideSwitchCheckBox.setChecked(false);
		}
		guideSwitchCheckBox.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				helper.putGuidePageSwitchValue(isChecked);
				Log.d("TAG", "flag=" + String.valueOf(helper.getGuidePageSwitchValue()));
			}
		});
		/*--向导页中的跳转按钮处理---*/
		Button goToMainBtn = (Button) mPageViews.get(1).findViewById(R.id.goToMainBtn);
		goToMainBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bhsz.getText().toString().equalsIgnoreCase("")){
					
				}else{
				SharedPreferences settings = getSharedPreferences("config", 0);
				SharedPreferences.Editor editor = settings.edit();
				String str=bhsz.getText().toString();
				editor.putString("dbid",bhsz.getText().toString());
				editor.putString("dbcs",cssz.getText().toString());
				editor.putString("xbqs",qssz.getText().toString());
				editor.putString("fpxs",fpsz.getText().toString());
				editor.putString("dlxs",dlsz.getText().toString());
				mAmeterStandardRadioGroup.getCheckedRadioButtonId();
				switch(mAmeterStandardRadioGroup.getCheckedRadioButtonId()){
				case R.id.threeWireActivePowerRadio:
					position1=2;
					break;
				case R.id.threWireReactivePowerRadio:
					position1=3;
					break;
				case R.id.fourWireActivePowerRadio:
					position1=0;
					break;
				case R.id.fourWireReactivePowerRadio:
					position1=1;
					break;
				}
				editor.putString("dbzs",String.valueOf(position1));
				editor.putString("dllc",String.valueOf(spinner.getSelectedItemPosition()));
				editor.commit();
				}
				if (originViewFlag == FROM_SPLASH_VIEW) {
					Intent intent = new Intent(GuideActivity.this, MainActivity.class);
					// 此处添加参数传递的Bundle赋值
					startActivity(intent);
				} else if (originViewFlag == FROM_MAIN_VIEW) {
					// 此处实现参数的回传
					Intent intent = new Intent();
					// 此处添加代码： 将页面收集的参数写入intent
					Bundle bd=new Bundle();
					intent.putExtra("settings", bd);
					// 回传参数
					setResult(FROM_MAIN_VIEW, intent);
				}
				finish();
			}
		});
		/*向导页 第二页中的图示处理代码*/
		mDiagramImageView = (ImageView) mPageViews.get(1).findViewById(R.id.diagramImageView);
		mAmeterStandardRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(MyRadioGroup group, int checkedId) {
				// group.check(checkedId);
				Log.d("TAG", "" + checkedId);
				// 如果是三相三线
				if (checkedId == R.id.threeWireActivePowerRadio || checkedId == R.id.threWireReactivePowerRadio) {
					mDiagramImageView.setImageResource(R.drawable.there_phase_there_wire);
				} else { // 如果是三相四线
					mDiagramImageView.setImageResource(R.drawable.there_phase_four_wire);
				}
			}
		});
	}
	private PagerAdapter mPageAdapter = new PagerAdapter() {
		@Override
		public void startUpdate(View arg0) {
		}
		@Override
		public Parcelable saveState() {
			return null;
		}
		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}
		@Override
		public int getCount() {
			return mPageViews.size();
		}
		@Override
		public void finishUpdate(View arg0) {
		}
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}
	};
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// Log.d("TAG", String.valueOf(position));
		// 假如是向导页的第二页（图示页）
		if (position == 1) {
			// mViewPager.setCurrentItem(0);
			// Toast.makeText(this, "信息不完整", Toast.LENGTH_LONG).show();
			int selectedRadioButtonId = mAmeterStandardRadioGroup.getCheckedRadioButtonId();
			Log.d("TAG", "checkedId=" + selectedRadioButtonId);
			// 如果是三相三线
			// if(selectedRadioButtonId == R.id.threeWireActivePowerRadio ||
			// selectedRadioButtonId == R.id.threWireReactivePowerRadio){
			// mDiagramImageView.setImageResource(R.drawable.there_phase_there_wire);
			// }else{ //如果是三相四线
			// mDiagramImageView.setImageResource(R.drawable.there_phase_four_wire);
			// }
		}
	}
	@Override
	public void onPageSelected(int position) {
		for (int i = 0; i < mPageViews.size(); i++) {
			mImageViews[position].setBackgroundResource(R.drawable.guide_page_indicator_focused);
			if (position != i) {
				mImageViews[i].setBackgroundResource(R.drawable.guide_page_indicator);
			}
		}
	}
	@Override
	public void onPageScrollStateChanged(int state) {
	}
	//===侦听二维码扫描======================
			private class btn_scan_click implements OnClickListener {               
		    	@Override            
		    	public void onClick(View v) { 
		    		switch (v.getId()){
		    		case R.id.ameterNoBtn:
		    			Activity activity=GuideActivity.this;
		    			Intent openCameraIntent = new Intent(activity,CaptureActivity.class);
		    			openCameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		                startActivityForResult(openCameraIntent, 0);
		    			//startActivityForResult(openCameraIntent, 0);	
		    			break;
		    		}
		    	}
			}
			//取得扫描结果
			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				super.onActivityResult(requestCode, resultCode, data);
				//
				if (resultCode == -1) {
					Bundle bundle = data.getExtras();
					String scanResult = bundle.getString("result");
					bhsz.setText(scanResult);
					//DisplayToast(scanResult);
				}
			}
}