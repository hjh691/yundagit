package com.example.substationtemperature.network;

import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class Networkinfo {

	/**没有网络**/
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap网络 */
	public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;
	public static int getNetWorkType(Context context) {  
		  
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();  
  
        int mNetWorkType = 0;
		if (networkInfo != null && networkInfo.isConnected()) {  
			//ConnectivityManager.TYPE_MOBILE,ConnectivityManager.TYPE_WIFI,
			//ConnectivityManager.TYPE_WIMAX,ConnectivityManager.TYPE_ETHERNET,ConnectivityManager.TYPE_BLUETOOTH
			String type = networkInfo.getTypeName();  
  
            if (type.equalsIgnoreCase("WIFI")) {  
                mNetWorkType = NETWORKTYPE_WIFI;  
            } else if (type.equalsIgnoreCase("MOBILE")) {  
                String proxyHost = android.net.Proxy.getDefaultHost();  
  
                mNetWorkType = TextUtils.isEmpty(proxyHost)  
                        ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)  
                        : NETWORKTYPE_WAP;  
            }  
        } else {  
            mNetWorkType = NETWORKTYPE_INVALID;  
        }  
  
        return mNetWorkType;  
    }   
	private static boolean isFastMobileNetwork(Context context) {  
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
		switch (telephonyManager.getNetworkType()) {  
		       case TelephonyManager.NETWORK_TYPE_1xRTT:  
		           return false; // ~ 50-100 kbps  
		       case TelephonyManager.NETWORK_TYPE_CDMA:  
		           return false; // ~ 14-64 kbps  
		       case TelephonyManager.NETWORK_TYPE_EDGE:  
		           return false; // ~ 50-100 kbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_0:  
		           return true; // ~ 400-1000 kbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_A:  
		           return true; // ~ 600-1400 kbps  
		       case TelephonyManager.NETWORK_TYPE_GPRS:  
		           return false; // ~ 100 kbps  
		       case TelephonyManager.NETWORK_TYPE_HSDPA:  
		           return true; // ~ 2-14 Mbps  
		       case TelephonyManager.NETWORK_TYPE_HSPA:  
		           return true; // ~ 700-1700 kbps  
		       case TelephonyManager.NETWORK_TYPE_HSUPA:  
		           return true; // ~ 1-23 Mbps  
		       case TelephonyManager.NETWORK_TYPE_UMTS:  
		           return true; // ~ 400-7000 kbps  
		       case TelephonyManager.NETWORK_TYPE_EHRPD:  
		           return true; // ~ 1-2 Mbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_B:  
		           return true; // ~ 5 Mbps  
		       case TelephonyManager.NETWORK_TYPE_HSPAP:  
		           return true; // ~ 10-20 Mbps  
		       case TelephonyManager.NETWORK_TYPE_IDEN:  
		           return false; // ~25 kbps  
		       case TelephonyManager.NETWORK_TYPE_LTE:  
		           return true; // ~ 10+ Mbps  
		       case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
		           return false;  
		       default:  
		           return false;  
		    }  
		} 
	//判断网络连接是否可用
	public static boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        } else {
        	//如果仅仅是用来判断网络连接
        	//则可以使用 cm.getActiveNetworkInfo().isAvailable();  
            NetworkInfo[] info = cm.getAllNetworkInfo();   
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true;   
                    }   
                }   
            }   
        }   
        return false;   
    } 
	//判断GPS是否打开
	public static boolean isGpsEnabled(Context context) {   
        LocationManager lm = ((LocationManager) context   
                .getSystemService(Context.LOCATION_SERVICE));   
        List<String> accessibleProviders = lm.getProviders(true);   
        return accessibleProviders != null && accessibleProviders.size() > 0;   
    }
	//判断WIFI是否打开
	public static boolean isWifiEnabled(Context context) {   
        ConnectivityManager mgrConn = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        TelephonyManager mgrTel = (TelephonyManager) context   
                .getSystemService(Context.TELEPHONY_SERVICE);   
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn   
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel   
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);   
    } 
	//判断是否是3G网络
	public static boolean is3rd(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();   
        if (networkINfo != null   
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {   
            return true;   
        }   
        return false;   
    }  
	//判断是wifi还是3g网络,用户的体现性在这里了，wifi就可以建议下载或者在线播放。
	public static boolean isWifi(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();   
        if (networkINfo != null   
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {   
            return true;   
        }   
        return false;   
    }

}
