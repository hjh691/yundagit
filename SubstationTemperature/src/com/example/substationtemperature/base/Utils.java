package com.example.substationtemperature.base;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Utils {
	//根据手机的分辨率从dp的单位转换成px（像素）
	public static  int dip2px(Context context,float dpValue){
 		final float scale=context.getResources().getDisplayMetrics().density;
 		return (int)(dpValue*scale+0.5f);
 	}
	
	//根据手机的分辨率从px转化成dp
	public static  int px2dp(Context context,float pxValue){
		final float scale=context.getResources().getDisplayMetrics().density;
		return (int)(pxValue/scale+0.5f);
	}
	//获取屏幕宽度
	public static int getScreenWidth(Context context){
		WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	//获取屏幕高度
	public static int getScreenHeight(Context context){
		WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	//获取屏幕像素密度
	public static float getScreenDensity(Context context){
		WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.density;
	}
	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	/** 获取屏幕的高度 */
	public final static int getWindowsHeight(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
}
