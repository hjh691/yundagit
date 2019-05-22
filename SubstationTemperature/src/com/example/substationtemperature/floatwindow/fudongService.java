package com.example.substationtemperature.floatwindow;

import com.example.substationtemperature.FragmentIndex;
import com.example.substationtemperature.MainActivity;
import com.example.substationtemperature.R;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.network.ClientService;
import com.example.substationtemperature.network.ClientService.sleep_thread;
import com.example.substationtemperature.view.AutoScroll;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class fudongService extends Service{

	private View view;// 透明窗体
	private boolean viewAdded = false;// 透明窗体是否已经显示
	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;
	public static  AutoScroll auto;
	private boolean loop=false;
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
	
		view = LayoutInflater.from(this).inflate(R.layout.autoscroll, null);

         windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        
         layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
                 LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
                 LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
         //layoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM; //悬浮窗开始在右下角显示
         layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
         auto=(AutoScroll)view.findViewById(R.id.TextViewNotice);
         //auto.setText("测试测试测试测试");
         auto.setText(ClientService.str_info);
         auto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//AutoScroll.isend=true;
				//Declare.celiangdian=ClientService.alertsensorname;
				//Declare.zhan=ClientService.alertstationname;
				//Declare.starttime=ClientService.alerttime;
				//ViewPager mViewPager = (ViewPager) FragmentIndex.activity.findViewById(R.id.mViewPager);
				//MainActivity.selectNavigationItem(5);
        		//mViewPager.setCurrentItem(5);
				stopSelf();
			}
        	 
         });
     }


	private void refresh() {
         if (viewAdded) {
             windowManager.updateViewLayout(view, layoutParams);
         } else {
          //layoutParams.y = 300;
             windowManager.addView(view, layoutParams);          
             viewAdded = true;
             AutoScroll autoScrollTextView = (AutoScroll)view.findViewById(R.id.TextViewNotice);
             autoScrollTextView.init(windowManager);
             autoScrollTextView.startScroll();
         }
     }
     @SuppressWarnings("deprecation")

     public void onStart(Intent intent, int startId) {
         super.onStart(intent, startId);
         loop=true;
         onCount();
         refresh();
     }

    
     public void removeView() {
         if (viewAdded)
         {
             windowManager.removeView(view);
             viewAdded = false;
         }
     }

     @Override
     public void onDestroy()
     {
         super.onDestroy();
         removeView();
         
         Log.d("CCtext", "ONDESTORY");
     }
     private void onCount(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                while (loop){
                	try {
    					sleep_thread.sleep(300);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                     if(AutoScroll.isend){
                         stopSelf();//结束服务
                         loop=false;
                         AutoScroll.isend=false;
                     }
                 }
             }
         }).start();
     }
     private void onStop(){
    	 loop=false;
         AutoScroll.isend=false;
     }

}
