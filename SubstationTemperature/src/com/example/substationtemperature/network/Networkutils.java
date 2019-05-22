package com.example.substationtemperature.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Networkutils {
	
	
    
    /**
     * 检测网络是否可用
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if(context!=null){
	    	ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo ni = cm.getActiveNetworkInfo();
	        return ni != null && ni.isConnectedOrConnecting();
        }
        return false;
    }
    //判断WiFi网络是否可用
    public static boolean isWifiConnected(Context context) {  
    	if (context != null) {  
    		ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
    				.getSystemService(Context.CONNECTIVITY_SERVICE);  
    		NetworkInfo mWiFiNetworkInfo = mConnectivityManager  
    				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
    		if (mWiFiNetworkInfo != null) {  
    			return mWiFiNetworkInfo.isAvailable();  
    		}  
    	}  
    	return false;  
	}  
    //判断移动网络是否可用
    public static boolean isMobileConnected(Context context){
    	if(context!=null){
    		ConnectivityManager mConnectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo mMobileNetworkInfo=mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    		if(mMobileNetworkInfo!=null){
    			return mMobileNetworkInfo.isAvailable();
    		}
    	}
    	return false;
    }
    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */
    
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    public int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager)context. getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }        
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if(!(extraInfo).isEmpty()){
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }
}
