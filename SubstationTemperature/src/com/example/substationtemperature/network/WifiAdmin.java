package com.example.substationtemperature.network;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;


public class WifiAdmin {
    private static final String TAG = null;
    public static final int WIFI_AP_STATE_FAILED= 4;

  //定义一个WifiManager对象
  	private WifiManager mWifiManager;
  	//定义一个WifiInfo对象
  	private WifiInfo mWifiInfo;
  	//扫描出的网络连接列表
  	private List<ScanResult> mWifiList;
  	//网络连接列表
  	private List<WifiConfiguration> mWifiConfigurations;
  	WifiLock mWifiLock;
  	 /** 指定热点网络ID **/
  	public int networkId;//网络ID
  	public int level;//信号强度

  	public WifiAdmin(Context context){
  		//取得WifiManager对象
  		mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
  		//取得WifiInfo对象
  		mWifiInfo=mWifiManager.getConnectionInfo();
  	}
	//打开wifi
	public void openWifi(){
		if(!mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(true);
		}
	}
	public void openWifi1(){
		//mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		//mWifiManager.setWifiEnabled(false);
        Log.e(TAG,"getWifiState------- "+mWifiManager.getWifiState());
        Method[] wmMethods = mWifiManager.getClass().getDeclaredMethods();
        for(Method method: wmMethods){
          if(method.getName().equals("setWifiApEnabled")){
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = "BDLX";
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.preSharedKey = "bdlx123456";
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);              
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP); 
            Log.d(TAG,"ssid ---->" + netConfig.SSID);
                  try { 
              method.invoke(mWifiManager, netConfig,true);
              Log.d(TAG,"method invoke to open wifi");
            } catch (IllegalArgumentException e) {            	
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
       }
	public void closeWifi1(){
		//mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		//mWifiManager.setWifiEnabled(false);
//        Log.e(TAG,"getWifiState------- "+mWifiManager.getWifiState());
        Method[] wmMethods = mWifiManager.getClass().getDeclaredMethods();
        for(Method method: wmMethods){
          if(method.getName().equals("setWifiApEnabled")){
            WifiConfiguration netConfig = new WifiConfiguration();
            
                  try { 
              method.invoke(mWifiManager, netConfig,false);
              Log.d(TAG,"method invoke to open wifi");
            } catch (IllegalArgumentException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
       }
	 public int getWifiApState() {

         try {

             Method method = mWifiManager.getClass().getMethod( "getWifiApState");
             int tmp = ((Integer)method.invoke(mWifiManager));
    		 
				// Fix for Android 4
				if (tmp > 10) {
					tmp = tmp - 10;
				}

             return tmp;

         } catch(Exception e) {

             e.printStackTrace();

             return WIFI_AP_STATE_FAILED;

         }

     }

	private WifiManager getSystemService(String wifiService) {
		// TODO Auto-generated method stub
		return null;
	}
	//关闭wifi
	public void closeWifi(){
		if(mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
		}
	}
	 // 检查当前wifi状态  
    public int checkState() {  
        return mWifiManager.getWifiState();  
        
    } 
    //检查当前wifi状态   
    public void checkNetCardState() {  
    	if (mWifiManager.getWifiState() == 0) {  
    	   Log.i(TAG, "网卡正在关闭");  
    	 } else if (mWifiManager.getWifiState() == 1) {  
    	   Log.i(TAG, "网卡已经关闭");  
    	 } else if (mWifiManager.getWifiState() == 2) {  
           Log.i(TAG, "网卡正在打开");  
    	 } else if (mWifiManager.getWifiState() == 3) {  
           Log.i(TAG, "网卡已经打开");  
         } else {  
           Log.i(TAG, "--");  
         }  
   }  

	//锁定wifiLock
	public void acquireWifiLock(){
		mWifiLock.acquire();
	}
	//解锁wifiLock
	public void releaseWifiLock(){
		//判断是否锁定
		if(mWifiLock.isHeld()){
			mWifiLock.acquire();
		}
	}
	//创建一个wifiLock
	public void createWifiLock(){
		mWifiLock=mWifiManager.createWifiLock("test");
	}
	//得到配置好的网络
	public List<WifiConfiguration> getConfiguration(){
		return mWifiConfigurations;
	}
	//指定配置好的网络进行连接
	public void connetionConfiguration(int index){
		if(index>mWifiConfigurations.size()){
			return ;
		}
		//连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
	}
	public void startScan(){
		mWifiManager.startScan();
		//得到扫描结果
	}
	public void startScan1(){
		mWifiManager.startScan();
		//得到扫描结果
		mWifiList=mWifiManager.getScanResults();
		//得到配置好的网络连接
		mWifiConfigurations=mWifiManager.getConfiguredNetworks();
	}
	//得到网络列表
	public List<ScanResult> getWifiList(){
		return mWifiList=mWifiManager.getScanResults();
		
	}
	//查看扫描结果
	public StringBuffer lookUpScan(){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<mWifiList.size();i++){
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包  
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;	
	}
	//查看扫描结果
    public String[] getResult(){
		ArrayList<String> listStr = new ArrayList<String>();
		for(int i=0;i<mWifiList.size();i++){
			listStr.add(mWifiList.get(i).toString());
		}
		return listStr.toArray(new String[0]);	
	}
	public String getMacAddress(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();
	}
	/**得到接入点的BSSID**/
	public String getBSSID(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
	}
	 public String getSSID() {
	        return (mWifiInfo ==null)?"NULL":mWifiInfo.getSSID();
	    }
	//获得IP地址ַ 
	public int getIpAddress(){
		return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();
	}
	
	//返回当前连接的网络的ID
	public int getNetWordId(){
		return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
	}
	//得到wifiInfo的所有信息
	public String getWifiInfo(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.toString();
	}
	//得到wifi信号强度
	public int getRssi() {
		return (mWifiInfo==null)?0:mWifiInfo.getRssi();
		}
	//添加一个网络并连接
	public void addNetWork(WifiConfiguration configuration){
		int wcgId=mWifiManager.addNetwork(configuration);
		mWifiManager.enableNetwork(wcgId, true);
	}
	//添加一个网络并连接
	public int addNetWork1(WifiConfiguration configuration){
		
			int wcgId=mWifiManager.addNetwork(configuration);
			mWifiManager.enableNetwork(wcgId, true);
			return wcgId;
		}
	/**
	 * 断开当前连接的网络
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		if(netId>0){
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;}
	}
	/**
	 * 得到连接的ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}
	//断开指定ID的网络
	public void disConnectionWifi(int netId){
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
	//连接指定ID的网络
	public void enableNetwork(int netId){
		mWifiManager.enableNetwork(netId, true);
	}

	// 移除一个链接
    public void removeNetworkLink(int netId) {
    	mWifiManager.removeNetwork(netId);
    }
  //三种连接情况
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)  
    {  
          WifiConfiguration config = new WifiConfiguration();    
           config.allowedAuthAlgorithms.clear();  
           config.allowedGroupCiphers.clear();  
           config.allowedKeyManagement.clear();  
           config.allowedPairwiseCiphers.clear();  
           config.allowedProtocols.clear();  
          config.SSID = "\"" + SSID + "\"";    
           
          WifiConfiguration tempConfig = this.IsExsits(SSID);            
          if(tempConfig != null) {   
              mWifiManager.removeNetwork(tempConfig.networkId);   
          } 
           
          if(Type == 1) //WIFICIPHER_NOPASS 
          {  
               config.wepKeys[0] = "";  
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
               config.wepTxKeyIndex = 0;  
          }  
          if(Type == 2) //WIFICIPHER_WEP 
          {  
              config.hiddenSSID = true; 
              config.wepKeys[0]= "\""+Password+"\"";  
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
              config.wepTxKeyIndex = 0;  
          }  
          if(Type == 3) //WIFICIPHER_WPA 
          {  
          config.preSharedKey = "\""+Password+"\"";  
          config.hiddenSSID = true;    
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                          
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);   
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
          config.status = WifiConfiguration.Status.ENABLED;    
          } 
           return config;  
    } 
    
    private WifiConfiguration IsExsits(String SSID)   
    {   
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();   
           for (WifiConfiguration existingConfig : existingConfigs)    
           {   
             if (existingConfig.SSID.equals("\""+SSID+"\""))   
             {   
                 return existingConfig;   
             }   
           }   
        return null;    
    } 
    /**
     * Description 指定WIFI是否可以被扫描到，即是否在可用范围内
     * @return true表示可用，否则不可用
    */
    public boolean targetWifiCanScan(String ssid)
    {
    	//mWifiManager=(WifiManager) getSystemService(Context.WIFI_SERVICE);
    	mWifiManager.startScan();
    	List<ScanResult> scanResultList = mWifiManager.getScanResults();
    	  int i_count=scanResultList.size();
    	  int t=i_count;
          if (scanResultList != null && scanResultList.size() > 0) {
              for (int i = 0; i < scanResultList.size(); i++) {
            	  ScanResult scanResult = scanResultList.get(i);
                     
            	  StringBuffer str = new StringBuffer()
                           .append("SSID: " + scanResult.SSID).append("\n")
                           .append("BSSID: " + scanResult.BSSID).append("\n")
                           .append("capabilities: " + scanResult.capabilities).append("\n")
                           .append("level: " + scanResult.level).append("\n")
                           .append("frequency: " + scanResult.frequency);
            	  //Log.i(TAG, str.toString());
                     
                 if (scanResult.SSID.equals(ssid)) {
                	 level = Math.abs(scanResult.level);
                     return true;
                 }
             }
         }
//         showLongToast("指定wifi热点暂不可用，稍后可能无法操作照明设备！");
        	 return false;

    }
    /**
     * Description 指定热点是否已配置(注册) 
     * @return true表示已注册，否则未注册
     */
    public boolean targetWifiIsConfig(String ssid){
    	List<WifiConfiguration> wifiConfigurationList = mWifiManager.getConfiguredNetworks();
    		for (int i = 0; i < wifiConfigurationList.size(); i++) {
    			WifiConfiguration wifiConfiguration = wifiConfigurationList.get(i);
            
            if (wifiConfiguration.SSID.equals(ssid)) {
                networkId = wifiConfiguration.networkId;
                 return true;
             }
         }
//         showLongToast("ָ��wifi�ȵ�δע�ᣬ����wifi��������ע�ᣡ");
        return false;
     }
    




}

