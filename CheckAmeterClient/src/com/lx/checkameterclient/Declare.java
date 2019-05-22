package com.lx.checkameterclient;

import android.app.ProgressDialog;

public class Declare {
	public static  String recvMessageClient = "";
	public static  String recvMessageServer = "";
	public static int send_flag=6;
	public static int send_set_flag=0;
	public static boolean sendmsg_flag=false;
	public static boolean Circle=false;
	public static boolean Clientisrun = false;
	public static boolean Clientisconnect = false;
	public static boolean Serverisrun = false;
	public static boolean Serverisconnect = false;
	public static boolean infotip=false;//Client刷新标志
	public static boolean infotip1=false;//Server刷新标志
	public static boolean receive_flag=false;//允许接收标志
	public static boolean rec_err=false;//接收错误标志
	public static short send_data=0;
	public static short send_set_data=0;
	public static boolean rec_overtime=false;//接收超时标志
	public static boolean confirm_flag=false;//对话框确认标志
//	public static boolean test_flag=false;
	public static int[] data_facs=new int [128];//伏安测试数据
	public static int[] data_jxpb=new int [128];//接线判别数据
	public static int[] data_wcxj=new int [128];//误差校验数据
	public static int[] data_xbfx=new int [512];//谐波分析数据
	public static int[] data_ctcs=new int [10];//ct测试数据
	public static int[] data_bxxs=new int [1024];//波形显示数据
	public static int[] data_para=new int [15];//参数显示
	public static int[] data_para1=new int [15];//参数显示
	public static String[] save_data=new String[64];
	public static String str_order="";
	public static String str_para="";
	public static String pass_word="";
	public static ProgressDialog progress=null;
	public static boolean txlj_ok_flag=false,txlj_erro_flag=false;//弹出网络连接成功及失败toast标志
	public static int connect_num=0;
	public static int connect_num1=0;
	public static String ssid="";
	public static String ssid_pass="";
	public static int ssid_connecte_flag=0;
	public static boolean hart_enable_flag=false;
	public static boolean active = false;
	public static boolean set_flag=false;
	public static int Mwidth;
	public static int Mheight;
	public static int mwidth=0;
	public static int mheight=0;
//	public static String localip="";
//	public static boolean getip_flag=false;
	public static String dbzs,dbdj,dylc,dllc,mcfs,xbfs;
	public static byte devicetype;
	
	public static String file_path="";
	public static boolean iserrortest=true;
	public static int wave_flag_for_volt=3,wave_flag_for_amphere=3,flag_harmonic=0;

}
