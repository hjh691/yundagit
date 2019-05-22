package com.example.substationtemperature.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

import com.google.gson.JsonObject;

public class Declare {

	public static boolean isnet=false;//是否有可用网络
	public static String home_url="http://192.168.10.67/_api/";//服务器地址f
	public static String username="admin";
	public static String password="admin";
	public static boolean isautologin=true;
	public static boolean isremember_pw=true;
	public static boolean islogin=false;
	public static boolean isloadstations=false;
	public static boolean isloadsensors=false;
	public static String danwei="";
	public static String phonenumber="";
	public static String email="";
	
	public static String version="1.0";
	
	public static String zhan="";
	public static String graphicname="";
	public static long stationid=-1;
	public static String celiangdian="";
	public static long sensorid=-1;
	private static Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat") private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final static String currentDateTimeStr = sdf.format(calendar.getTime());
	public static Date time=calendar.getTime();
	
	public static String starttime=currentDateTimeStr,endtime=currentDateTimeStr;
	//public static String[] zhanArray={"110kV站1","35kV站"},cldArray={"Z-R-01","Z-C-02","C-02-123"}; 
	//public static ArrayAdapter<String> zhanAdapter,cldAdapter;
	
	//public static int xiangbie=4;
	public static int infotype=2;
	//public static boolean Clientisrun;
	
	public static String _token="567894fghgjj";
	public static String request_type="Login";
	public static JsonObject js_para=null;
	
	public static String LOGIN="Login";
	public static String order="";
	
	public static MeasureData[] md_rl_Array,md_his_Array;
	//定义常量信息
	public static final int STATUS_SUCCESS=0;//200,300
	public static final int STATUS_ERROR=1;
	public static final int STATUS_TIMEOUT=101;
	public static final int STATUS_GETSTATIONS_SUCCESS=80;
	public static final int STATUS_NODATA=10;
	public static final int STATUS_OTHER_ERROR=11;
	public static final int STATUS_GETSENSORS_ERROR=21;
	public static final int STATUS_GETSENSORS_SUCCESS=20;
	public static final int STATUS_ALERT=800;
}
