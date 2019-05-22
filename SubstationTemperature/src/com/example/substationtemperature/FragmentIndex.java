package com.example.substationtemperature;

import java.util.Timer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.TextView;

import com.example.substationtemperature.adapter.GalleryImageAdapter;
import com.example.substationtemperature.adapter.GridViewAdapter;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.floatwindow.WindowService;
import com.example.substationtemperature.view.confirm;

public class FragmentIndex extends Fragment{
	public static Activity activity;
	private Gallery gallery;
	private GridView gridview;
	
	private final Timer timer = new Timer();   
	//private TimerTask task;
	
	//private static final int timerAnimation = 1;
	private ViewPager mViewPager;
	public static Dialog dialog;
	public void FragmentIndex(){
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.activity_index, container, false);
		//初始化Gallery
        gallery=(Gallery)view.findViewById(R.id.gallery1);
        try{
        	gallery.setAdapter(new GalleryImageAdapter(activity));
        }catch(IllegalArgumentException e){
        	e.printStackTrace();
        }catch(IllegalAccessException e){
        	e.printStackTrace();
        }

       
        //单击事件
        gallery.setOnItemClickListener(new OnItemClickListener(){
        	
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//setTitle("您单击了第"+String.valueOf(position+1+"项"));
			}
        });
        //初始化主功能列表选项，并设置旗单击事件
        gridview=(GridView)view.findViewById(R.id.gridView1);
        gridview.setAdapter(new GridViewAdapter(activity));
        gridview.setOnItemClickListener(new MainItemClickListener());
		
       
		return view;	
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		FragmentIndex.activity=activity;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		//SaveRecordAll(bhsz.getText().toString());
	}
	private class MainItemClickListener implements OnItemClickListener{   
        /**  
         * @param parent 代表当前的gridview   
         * @param view 代表点击的item  
         * @param position 当前点击的item在适配中的位置   
         * @param id 当前点击的item在哪一行  
         */   
        public void onItemClick(AdapterView<?> parent, View view, int position,   
                long id) {  
        	mViewPager = (ViewPager) activity.findViewById(R.id.mViewPager);
        	
        	switch (position){
        	case 0:
        	case 1:
        	case 2:
        	case 3:
        	case 4:
        	case 5:
        		//if(!Declare.islogin){
            	//	comfir_display();
    				//return;
            	//}
        		MainActivity.selectNavigationItem(position+1);
        		mViewPager.setCurrentItem(position+1);
        		break;
        	case 6:
        		Intent intent = new Intent(activity,Regist.class);   
                startActivity(intent);
                if(!Declare.islogin){
        			comfir_display();
    				//return;
            	}
        		break;
        	case 7:
        		SlidingMenuControlller.Logout();
        		activity.finish();
        		//intent=new Intent(activity,WindowService.class);
                //activity.startService(intent);
        		break;
        	}
	        	
        }
 }
	
	//======消息框提示===========
	public static void comfir_display() {	
		if(dialog==null){
			dialog = new confirm(activity,R.style.MyDialog);       	        	       	
	        dialog.show(); 
	        TextView text_info=(TextView)dialog.findViewById(R.id.text_info); 
	        String str="";
	       	str=str+"您还未登录，现在要登录吗";
	        text_info.setText(str);
	        Button but_qr = (Button)dialog.findViewById(R.id.but_qr);   
	        but_qr.setOnClickListener(new ConfirmListener());
	        Button but_q = (Button)dialog.findViewById(R.id.but_qx);   
	        but_q.setOnClickListener(new ConfirmListener()); 
		}else{dialog.show();}
		new Thread(){ public void run(){ try {
			sleep(3000);
			if(dialog!=null)
			{dialog.cancel();dialog=null;}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} } }.start(); 
	}
	public static void comfir1_display() {	
		if(dialog==null){
			dialog = new confirm(activity,R.style.MyDialog);       	        	       	
	        dialog.show(); 
	        TextView text_info=(TextView)dialog.findViewById(R.id.text_info); 
	        String str="";
	       	str=str+"网络不可用，现在打开网络设置吗？";
	        text_info.setText(str);
	        Button but_qr = (Button)dialog.findViewById(R.id.but_qr);
	        but_qr.setText("设置");
	        but_qr.setOnClickListener(new Confirm1Listener());
	        Button but_qx = (Button)dialog.findViewById(R.id.but_qx);   
	        but_qx.setOnClickListener(new ConfirmListener()); 
		}else{dialog.show();}
		new Thread(){ public void run(){ try {
			sleep(3000);
			if(dialog!=null)
			{dialog.cancel();dialog=null;}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} } }.start(); 

	}
	  		
  	//========消息确认界面按钮响应==================================
	public static class ConfirmListener implements  OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
		    	case R.id.but_qr:		    				    			 
		    		Intent intent=new Intent(activity,Login.class);
					activity.startActivityForResult(intent,1);
					activity.overridePendingTransition(R.anim.fade, R.anim.hold);
		    	    break;
		    	case R.id.but_qx:
		    		
		    		break;
			}
			dialog.cancel();
    		dialog=null;
	    }
	} 
	public static class Confirm1Listener implements  OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
		    	case R.id.but_qr:		    				    			 
		    		//activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
		    		//startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //进入手机中的wifi网络设置界面
		    		activity.startActivity(new Intent(Settings.ACTION_SETTINGS));//调用系统设置
		    	    break;

			}
			dialog.cancel();
    		dialog=null;
	    }
	} 
	//延时线程
    public class sleep_thread extends Thread{
		String str;
		@Override
		
    	public void run(){
				try {
					sleep_thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.cancel();
      }
    }

}
