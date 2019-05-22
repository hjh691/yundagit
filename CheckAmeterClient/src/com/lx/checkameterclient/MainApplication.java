package com.lx.checkameterclient;


import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

public class MainApplication extends Application {

	/**
	 * ȫ�ֵ�������.
	 */
	private static Context mContext;
	/**
	 * ����ȫ�ֱ���
	 * ȫ�ֱ���һ�㶼�Ƚ������ڴ���һ��������������ļ�����ʹ��static��̬����
	 * 
	 * ����ʹ������Application�������ݵķ���ʵ��ȫ�ֱ���
	 * ע����AndroidManifest.xml�е�Application�ڵ����android:name=".MyApplication"����
	 * 
	 */
	private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
	public WindowManager.LayoutParams getMywmParams(){
		return wmParams;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		
		mContext = getApplicationContext();
		
	}	
	
	/**��ȡContext.
	 * @return
	 */
	public static Context getContext(){
		return mContext;
	}
	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	
}

