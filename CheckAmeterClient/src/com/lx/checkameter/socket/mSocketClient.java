package com.lx.checkameter.socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lx.checkameterclient.Declare;
import com.lx.checkameterclient.MainApplication;
import com.lx.checkameterclient.R;
public class mSocketClient extends Service{
	
	public static Socket mClient = null;
	private WifiReceiver receiverWifi;
	private IntentFilter mIntentFilter;
//	public static BufferedReader mBufferedReaderClient	= null;
//	public static InputStream mInputReaderClient=null;
	public static DataInputStream din =null;
	public static DataOutputStream don=null;
//	public static PrintWriter mPrintWriterClient = null;
	private Thread mThreadClient = null;
	public static Thread mThreadCheck=null;
	public static Thread mThreadClient_send = null;
	
	private String IP;
	public static boolean check_flag = false;
	private int PORT;
	public static boolean send_start_flag=false;//发送线程启动标志
	private static int count,set_count;//接收超时计数
	private static int num_lj;
	private static int num_toast,num_toast1;//循环计数
	private static int num_socket;//socket连接次数
	private static int num_overtime;//超时次数
	private static int num_circ;//大循环循环计数
	private static int num_circ1;//大循环循环计数
	private static int num_overtime1;
	private static int RSSI;
	public static boolean sendover_flag=false;//发送数据完成标志
	NotificationManager notificationManager = null; 
	//	private boolean txlj_ok_flag=false,txlj_erro_flag=false;
	private static WifiAdmin mWifiAdmin=null;
	private static boolean isconnected1=false;
	private static boolean isfailover1=false;
	private static boolean isconnecting=false;
	boolean ssid_connecte=false;
	boolean connect_flag=false;
	boolean scan_ssid;//扫描指定热点标志
	boolean level_flag;//信号强度标志
	boolean toast_flag,toast_flag1;//切换toast显示时间
	public static boolean toast_flag2;
	public static int num_toast2;
	boolean hart_send_flag=false;//心跳发送标志
	byte[] buffer2=null;
//	private static int count=0;
	//有关电池电量悬浮窗
	WifiManager mWifiManager;
	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	View view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	int state;
	TextView tx_bat_value,text_ssid;
	ImageView bat_iv,connect_iv;
	private float StartX;
	private float StartY;
	
	private boolean inti_vol_value_flag;//连接后初次显示电量标志
	public static boolean toast_flag3;
	public static int num_toast3;
	boolean vol_send_flag=false;//取电量发送标志
	private int vol_value;//电压值
	private static int num_vol1;//电量值刷新循环计数
	int delaytime=2000;
	private static Listener mListener;
	
	
	
	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			dataRefresh();
			handler.postDelayed(this, delaytime);
			wm.updateViewLayout(view, wmParams);
		}
	};
	public interface Listener {
	    /**
	     * Called when new incoming data is available.
	     */
	     public void onNewData(byte[] data);
	     public void onNewData1();

	     /**
	      * Called when {@link SerialInputOutputManager#run()} aborts due to an
	      * error.
	      */
	        public void onRunError(Exception e);
	}
	public static synchronized void setListener(Listener listener) {
	        mListener = listener;
	}
	public static synchronized Listener getListener() {
        return mListener;
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void createView() {
		SharedPreferences settings = getSharedPreferences("float_position_sxxy",
				Activity.MODE_PRIVATE);		
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		// 设置LayoutParams(全局变量）相关参数
		wmParams = ((MainApplication) getApplication()).getMywmParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x=settings.getInt("float_x", 350);
		wmParams.y=settings.getInt("float_y", 0);
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		
		wm.addView(view, wmParams);

		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				y = event.getRawY(); // 25是系统状态栏的高度
				Log.i("currP", "currX" + x + "====currY" + y);// 调试信息
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);// 调试信息
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;

					updateViewPosition();
					//showImg();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});	
		
	}
	public void dataRefresh() {
		
		text_ssid.setText(Declare.ssid);
		
		if(Declare.Clientisconnect==true){
			connect_iv.setImageDrawable(getResources().getDrawable(R.drawable.lamp_l));
			if(inti_vol_value_flag==false){
				if(vol_value>0){					
					inti_vol_value_flag=true;
					vol_value=Declare.data_para[1];//取得百分比值
					tx_bat_value.setText(""+vol_value);
					bat_png_display();
				}
			}
		}
		else{
				connect_iv.setImageDrawable(getResources().getDrawable(R.drawable.lamp_g));
				inti_vol_value_flag=false;
		}
		 String str=null;
		 if(mWifiAdmin.checkState()==3){ 
		   str=mWifiAdmin.getSSID();
	    	  try {
	    		  str=str.replace("\"", "");
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			  	}
		 }
		if(str!=null && str.equals(Declare.ssid) && isconnected1==true && Declare.Clientisconnect==true){
		if(num_vol1==0){
			tx_bat_value.setText(""+vol_value);
		    if(vol_value>100){vol_value=100;}
		    if(vol_value<0){vol_value=0;}
		    bat_png_display();//电量填充显示
		}

		//=========根据电量值弹出提示信息=====================================
		if(vol_value<=20 && vol_value>0){
			if(toast_flag3==false){
				if(num_toast3<5)
				{
					if(num_circ1==0){
						DisplayToast("前端电池电量已经很低，请及时充电！！");	
						num_toast3++;
					}
				}
				else{
					toast_flag3=true;
					num_toast3=0; 
				}
			}
			else
			{
				if(++num_toast3==8){
					
					num_toast3=0;
					DisplayToast("前端电池电量已经很低，请及时充电！！");	
				}
			}
		}
		else{
			toast_flag3=false;num_toast3=0;
		}
		if(++num_circ1==2){num_circ1=0;}
		if(++num_vol1==3){
			num_vol1=0;
		}
		}
		else{
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_0));
			tx_bat_value.setText("0");
		}
//		image_suo.setImageDrawable(getResources().getDrawable(R.drawable.suo_k));
		//tx.setText("" + memInfo.getmem_UNUSED(this) + "KB");
		//tx1.setText("" + memInfo.getmem_TOLAL() + "KB");
	}

	private void bat_png_display(){
		if(vol_value>=0 && vol_value<5){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_0));	
		}
		else if(vol_value>=5 && vol_value<10){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_5));
		}
		else if(vol_value>=10 && vol_value<20){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_10));
		}
		else if(vol_value>=20 && vol_value<30){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_20));
		}
		else if(vol_value>=30 && vol_value<40){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_30));
		}
		else if(vol_value>=40 && vol_value<50){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_40));
		}
		else if(vol_value>=50 && vol_value<60){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_50));
		}
		else if(vol_value>=60 && vol_value<70){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_60));
		
		}
		else if(vol_value>=70 && vol_value<80){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_70));
		}
		else if(vol_value>=80 && vol_value<90){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_80));
		}
		else if(vol_value>=90 && vol_value<95){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_90));
		}
		else if(vol_value>=95 && vol_value<100){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_95));
		}
		else if(vol_value>=100){
			bat_iv.setImageDrawable(getResources().getDrawable(R.drawable.bat_level_100));
			//vol_value=0;
		}
   }
	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
		SharedPreferences shared = getSharedPreferences("float_position_sxxy",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float_x", wmParams.x);
		editor.putInt("float_y", wmParams.y);
		editor.commit();
	}
	
	public void onCreat()
	{
		System.out.println("客户端服务已创建");
	}
	@Override
	public  void onDestroy() {
		super.onDestroy();
		try {
			if(mThreadCheck!=null){
//				count=0;
				check_flag=false;
				mThreadCheck.interrupt();
				mThreadCheck=null;
			}	
			if(mClient!=null){
//				mPrintWriterClient.close();
//				mPrintWriterClient=null;
				don.close();
				don=null;
				din.close();
				din=null;
				mClient.close();
				mClient=null;
			 }
			if(mThreadClient!=null){			
				mThreadClient.interrupt();
				mThreadClient=null;
			}													
				Declare.recvMessageClient = "Client关闭！\n";//消息换行
//				isConnecting = false;
				Declare.infotip=true;
				Declare.Clientisconnect = false;
				Declare.Clientisrun=false;
				if(!Declare.Clientisrun && Declare.active==true){
				Intent localIntent = new Intent();
				localIntent.setClass(this, mSocketClient.class); // 销毁时重新启动Service
				this.startService(localIntent);
				}
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);  
				//notificationManager.cancel(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	
        	e.printStackTrace();
        	Declare.Clientisrun=false;
        	Declare.Clientisconnect = false;
			return;
        }
		unregisterReceiver(receiverWifi);//注销广播}
    }
	public void onStart(Intent intent, int startID)
	{
		System.out.println("开始客户端服务");
		//notificationManager = (NotificationManager) 
		//		 getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		//showNotification();
		Declare.Clientisrun=true;
		//2017-10-30 ++++++++++++++++++++++++
		ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo  = cm.getActiveNetworkInfo();
		Boolean networkinfo=Networkinfo.isNetworkAvailable(getBaseContext());
		Boolean value=networkinfo;
		//Gson gson = new Gson(); 
		Student student = new Student();  
	    student.id = 1;  
	    student.nickName = "乔晓松";  
	    student.age = 22;  
	    student.email = "965266509@qq.com";  
	    //System.out.print( gson.toJson(student));
	    String adress_Http = "http://192.168.1.180:80/";
	    HttpPost request = new HttpPost(adress_Http);
		JSONObject param = new JSONObject();
		try {
			URL my_url = new URL(adress_Http);
			request = new HttpPost(adress_Http);
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		//try {
			//StringEntity se = new StringEntity(gson.toString(),"utf-8");//防止乱码
			//request.setEntity(se);
			//} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			//}
		HttpResponse httpResponse = null;
		//try {
		//	httpResponse = new DefaultHttpClient().execute(request);
		//} catch (ClientProtocolException e) {
			// TODO 自动生成的 catch 块
		//	e.printStackTrace();
		//} catch (IOException e) {
			// TODO 自动生成的 catch 块
		//	e.printStackTrace();
		//}   
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据   
		//String retSrc = null;
		//try {
			//retSrc = EntityUtils.toString(httpResponse.getEntity());
		//} catch (org.apache.http.ParseException e) {
			// TODO 自动生成的 catch 块
		//	e.printStackTrace();
	//	} catch (IOException e) {
			// TODO 自动生成的 catch 块
		//	e.printStackTrace();
		//}   
		// 生成 JSON 对象   
		JSONObject result = null;
		//try {
			//result = new JSONObject( retSrc);
		//} catch (JSONException e) {
			// TODO 自动生成的 catch 块
		//	e.printStackTrace();
		//}   
		try {
			String token = result.get("token").toString();
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}  
		//2017-10-30-------------------------------*/
		mWifiManager=(WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiAdmin = new WifiAdmin(mSocketClient.this);
		receiverWifi = new WifiReceiver(); 
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(receiverWifi, mIntentFilter);
		count=0;
		//mThreadCheck=null;
//		mThreadClient=new Thread(new AndroidClient());
//		mThreadClient.start();
		if(mThreadCheck==null){
			check_flag=true;
			mThreadCheck=new mThreadCheck();
			mThreadCheck.start();
		}	
		/*
		view = LayoutInflater.from(this).inflate(R.layout.floatview, null);		
		tx_bat_value = (TextView) view.findViewById(R.id.bat_value);	
		text_ssid = (TextView) view.findViewById(R.id.text_ssid);
		text_ssid.setText(Declare.ssid);
		connect_iv = (ImageView) view.findViewById(R.id.connect_iv);
		bat_iv = (ImageView) view.findViewById(R.id.bat_iv);
		createView();
		handler.postDelayed(task, delaytime);*/
		
	}
	
		
	public void Handle_Buf(byte buffer[],int Pos,int len){
		 byte[] buffer_tmp=new byte[len];
		 System.arraycopy(buffer,Pos,buffer_tmp,0,len);	
		 buffer=new byte[len];
		 System.arraycopy(buffer_tmp,0,buffer,0,len);
	}
	public class Student {  
	    public int id;  
	    public String nickName;  
	    public int age; 
	    public String email;
	    public ArrayList<String> books;  
	    public HashMap<String, String> booksMap;  
	}
	public class AndroidClient implements Runnable{
    	public void run() {
    		try { 
				try {
					if(mClient!=null)
					{
						mClient.close();
						mClient = null;
						
						don.close();
						don=null;
						
						din.close();
						din=null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  	
//    				count=0;
				getClient_ip();
				//连接服务器
//    				mClient = new Socket("192.168.43.1",5000);	//portnum
				mClient = new Socket(IP,PORT);
				don=new DataOutputStream(mClient.getOutputStream());
				din=new DataInputStream(mClient.getInputStream());
				Declare.Clientisconnect = true;
				Declare.Circle=true;//2016-4-1
				SendClientmsg(Declare.Circle);//2016-4-1
				//MainActivity.netid=mWifiAdmin.getNetworkId();
				Declare.rec_overtime=false;//超时标志复位
				Declare.infotip=true;
				num_socket=0;
				Declare.ssid_connecte_flag=0;
                Declare.recvMessageClient = "已经连接server!\n";
                //DisplayToast("已经连接server!\n");
                Message msg = new Message();
                msg.what = 0;
				mHandler.sendMessage(msg);		
				//break;
    			}
    			catch (Exception e) 
    			{
    				//Declare.recvMessageClient = "����IP�쳣:" + e.toString() + e.getMessage() + "\n";//��Ϣ����
    				Declare.recvMessageClient = "连接SOCKET异常" + "\n";//消息换行
    				Declare.Clientisconnect = false;
    				Declare.ssid_connecte_flag=0;
    				Declare.infotip=true;
    				connect_flag=false;
    				Message msg = new Message();
                    msg.what = 5;
    				mHandler.sendMessage(msg); 
    				/*
    				Message msg1 = new Message();
                    msg1.what = 2;
    				mHandler.sendMessage(msg); 
					*/
    				return;
    			}
    		while(Declare.Clientisconnect)
			{
				try
				{
					//在判断发送命令完成以后再进行数据接收2013.4.12
					//if(sendover_flag==true){
						sendover_flag=false;//2013.08.08ע��
						Declare.receive_flag=false;
						Declare.rec_err=false;
						byte[] bt1=new byte[1];
					    if(din.read(bt1)!=-1)
						{
						 /*
						 int time=0;
//						 int set_count=0;
							//������ʱѡ��
							switch(rec_select)
							{
								case 0:
								case 1:
								
								case 3:
									time=0;
								break;
								case 4:
									time=0;
//									set_count=10;
								break;
								case 5:
									time=0;
//									set_count=3;
								break;
								case 6:
									time=0;
//									set_count=10;
								break;
								case 2:
									time=0;
//									set_count=10;
								break;
							}
							Thread.sleep(time, 0);
							System.out.println("��ʱ��־");
//						    Declare.rec_overtime=false;//����ѭ������ʱ�����ճ�ʱ��־
//						    count=0;
							*/
						   int h=din.available();
						   //System.out.println("h="+h);
							byte[] buffer1=new byte[h];
							byte[] buffer=null;
							if(buffer2==null){
							   buffer=new byte[h+1];
							}
							else{
								buffer=new byte[buffer2.length+din.available()+1];
							}
							din.read(buffer1); 
							//====合并字节组=====================
							if(buffer1.length>=800){
								System.arraycopy(bt1,0,buffer,0,1);
								System.arraycopy(buffer1,0,buffer,1,buffer1.length);
								buffer2=new byte[buffer.length];
								System.arraycopy(buffer,0,buffer2,0,buffer.length);
								continue;
							}
							else{
								if(buffer2==null){
									System.arraycopy(bt1,0,buffer,0,1);
									System.arraycopy(buffer1,0,buffer,1,buffer1.length);
								}
								else{
									System.arraycopy(buffer2,0,buffer,0,buffer2.length);
									System.arraycopy(bt1,0,buffer,buffer2.length,1);
									System.arraycopy(buffer1,0,buffer,buffer2.length+1,buffer1.length);
								}
							}
//							byte[] buffer = new byte[2048];
							//for(int l=0;l<100;l++){   
//							System.out.println(BytesToHexString(buffer));	
							Declare.recvMessageClient = new String(BytesToHexString(buffer)) + "\n";//消息换行
							//Message msg = new Message();
							//msg.what = 5;
							//mHandler.sendMessage(msg); 
							int i=0,m=0;
							for( i=0;i<buffer.length;i++){
								if(buffer[i]==(byte)0xEB && Declare.receive_flag==false){
									System.out.println("收到字头");
									m=i;
									break;
								}
							}
							int data_length=buffer.length-m;
							/*
							if(buffer[i]==(byte)0xEB && Declare.receive_flag==false){
								yx_flag=true;
								System.out.println("�յ���ͷ");
						//		break;
							}
							*/
							//if(yx_flag==true)
							//	{
								sendover_flag=false;
								Declare.receive_flag=true;
								Declare.rec_overtime=false;//启动循环采样时，接收超时标志
								byte jyh_read=0;
								int data_len=0;
								count=0;
								num_overtime=0;
								/*
								int len=buffer.length;
								String crc_read=jyh_receive(buffer,len-2);
								String crc_str=crcToHexString(buffer[len-2],buffer[len-1]);
								if(crc_read.equals(crc_str))
								{
									System.out.println("crczhengque");
//									int bh=buffer[2] & 0xff;
//									int data_len =lentoshort(buffer[3],buffer[4])/4;
								*/
									//伏安数据测试
									if(buffer[i+1]==(byte)0x03 && data_length>=110){
										Declare.connect_num=0;
										System.out.println("伏安数据");
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
										if(jyh_read==(byte)0xFF){
											System.out.println("crczhengque");
										data_jx(buffer,i+5,26,Declare.data_facs,0);											
									//data_jx1(buffer,i+109,1,Declare.data_facs,26);//****�����¶Ȳɼ�
										}
										/*
										if(data_length>data_len+6){
											
										}
										else{break;}
										*/
									}
									//接线判别
									else if(buffer[i+1]==(byte)0x04 && data_length>=56){
										Declare.connect_num=0;
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
										if(jyh_read==(byte)0xFF){
										data_jx(buffer,i+5,6,Declare.data_jxpb,0);
										data_jx1(buffer,i+29,5,Declare.data_jxpb,6);
										data_jx(buffer,i+39,4,Declare.data_jxpb,11);
										Declare.data_jxpb[15]=(int)buffer[i+55];
										Declare.data_jxpb[16]=(int)buffer[i+56];
										Declare.data_jxpb[17]=(int)buffer[i+57];
										Declare.data_jxpb[18]=(int)buffer[i+58];																				
										}
										/*
										if(data_length>data_len+6){
											
										}
										else{break;}
										*/
									}
									//误差测试
									else if(buffer[i+1]==(byte)0x05 && data_length>=49){
										Declare.connect_num=0;
										//data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,49);
										if(jyh_read==(byte)0xFF){
											Declare.data_wcxj[11]=(int)buffer[i+47];
											data_jx1(buffer,i+45,1,Declare.data_wcxj,10);
											data_jx(buffer,i+5,10,Declare.data_wcxj,0);
										//System.out.println("����־��"+(int)buffer[i+47]);
										}
										/*
										if(data_length>49){
											byte[] buffer_tmp=new byte[data_length-49];
											 
											 System.arraycopy(buffer,49,buffer_tmp,0,data_length-49);	
											 buffer=new byte[data_length-49];
											 System.arraycopy(buffer_tmp,0,buffer,0,data_length-49);
											System.out.println("���δ���������飺"+data_length+"��49");
											
										}
										else{break;}
										*/
									}
									//谐波测量
									else if(buffer[i+1]==(byte)0x06 && data_length>=684 ){
										Declare.connect_num=0;
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
										if(jyh_read==(byte)0xFF){
										data_jx1(buffer,i+5,54,Declare.data_xbfx,0);
										}
										for(int j=1;j<6;j++){
											int k=114*j;
											if(buffer[i+k]==(byte)0xEB && buffer[i+k+1]==(byte)0x06){
												data_len =lentoshort(buffer[i+k+3],buffer[i+k+4]);
												System.out.println("谐波长度"+data_len);
												jyh_read=jyh_receive(buffer,i+k,data_len+6);
												System.out.println("谐波数据"+jyh_read);
												if( jyh_read==(byte)0xFF){
													data_jx1(buffer,i+k+5,54,Declare.data_xbfx,54*j);
												}
											}
										}
										/*
										if(data_length>684){
										}
										else{break;}
										*/
									}
									//变比测量
									else if(buffer[i+1]==(byte)0x07){
										Declare.connect_num=0;
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
										if(jyh_read==(byte)0xFF){
										data_jx(buffer,i+5,2,Declare.data_ctcs,0);
										data_jx1(buffer,i+13,3,Declare.data_ctcs,2);
										}
										/*
										if(data_length>data_len+6){
										}
										else{break;}
										*/
									}
									//********************波形显示***********************
									else if(buffer[i+1]==(byte)0x08 && data_length==1572){
										Declare.connect_num=0;
										//System.out.println("�������");
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
//										System.out.println("0У���"+jyh_read);
										if(jyh_read==(byte)0xFF){
//											System.out.println("0У�����ȷ");
										data_jx1(buffer,i+5,128,Declare.data_bxxs,0);
										}
										for(int j=1;j<6;j++){
											int k=262*j;
											if(buffer[i+k]==(byte)0xEB && buffer[i+k+1]==(byte)0x08){
												data_len =lentoshort(buffer[i+k+3],buffer[i+k+4]);
												jyh_read=jyh_receive(buffer,i+k,data_len+6);
//												System.out.println(j+"У���"+jyh_read);
												if( jyh_read==(byte)0xFF){
//													System.out.println(j+"У�����ȷ");
													data_jx1(buffer,i+k+5,128,Declare.data_bxxs,128*j);
												}
											}
										}
										//break;
									}
									//===========读取前端参数======================
									else if(buffer[i+1]==(byte)0x09 && data_length>=30){
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
										if(jyh_read==(byte)0xFF){
											if(data_len==26){
												data_jx1(buffer,i+5,12,Declare.data_para,1);
												data_jx1(buffer,i+5,12,Declare.data_para1,1);
												//data_jx1(buffer,i+5,12,Declare.data_para2,1);
												Declare.data_para[0]=(int)buffer[i+1];																														
												Declare.data_para1[0]=(int)buffer[i+1];
												//Declare.data_para2[0]=(int)buffer[i+1];
												if(data_length>=30){
													Declare.data_para[13]=(int)buffer[i+29];
													Declare.data_para[14]=(int)buffer[i+30];
													Declare.data_para1[13]=(int)buffer[i+29];
													Declare.data_para1[14]=(int)buffer[i+30];
													//Declare.data_para1[13]=(int)buffer[i+29];
													//Declare.data_para1[14]=(int)buffer[i+30];
													Declare.devicetype=1;
												}
											}
											if(data_len==24){
												
												data_jx1(buffer,i+5,11,Declare.data_para,1);
												data_jx1(buffer,i+5,11,Declare.data_para1,1);
												Declare.data_para[0]=(int)buffer[i+1];																														
												Declare.data_para1[0]=(int)buffer[i+1];
												if(data_length>=30){
													Declare.data_para[12]=(int)buffer[i+27];
													Declare.data_para[13]=(int)buffer[i+28];
													Declare.data_para1[12]=(int)buffer[i+27];
													Declare.data_para1[13]=(int)buffer[i+28];
													Declare.devicetype=0;
												}
												Declare.devicetype=0;
											}
										}
										num_overtime1=0;
										/*
										if(data_length>data_len+6){
											Handle_Buf(buffer,data_len+6,data_length-data_len-6);
											System.out.println("二次处理参数数组：");
										}
										else{break;}
										*/
										
									}
									//===========读取前端参数======================
								/*	else if(buffer[i+1]==(byte)0x09 && data_length>=30){
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
										if(jyh_read==(byte)0xFF){
											
										data_jx1(buffer,i+5,11,Declare.data_para,1);
										data_jx1(buffer,i+5,11,Declare.data_para1,1);
										Declare.data_para[0]=(int)buffer[i+1];																														
										Declare.data_para1[0]=(int)buffer[i+1];
										if(data_length>=30){
											Declare.data_para[12]=(int)buffer[i+27];
											Declare.data_para[13]=(int)buffer[i+28];
											Declare.data_para1[12]=(int)buffer[i+27];
											Declare.data_para1[13]=(int)buffer[i+28];
										}
										}
										num_overtime1=0;
										Declare.devicetype=0;
										
										//if(data_length>data_len+6){
										//	Handle_Buf(buffer,data_len+6,data_length-data_len-6);
										//	System.out.println("���δ���������飺");
										//}
										//else{break;}
										
									}*/
									//===========读取误差测试的电参量======================
									else if(buffer[i+1]==(byte)0xF0 && data_length>=42){
										Declare.connect_num=0;
										data_len =lentoshort(buffer[i+3],buffer[i+4]);
										jyh_read=jyh_receive(buffer,i,data_len+6);
										if(jyh_read==(byte)0xFF){
										data_jx(buffer,i+5,9,Declare.data_wcxj,0);
										}
										if(data_length>data_len+6){
											Handle_Buf(buffer,data_len+6,data_length-data_len-6);
											System.out.println("二次处理误差电量数组："+data_length+"包长"+(data_len+6));
										}
										/*
										if(data_length>data_len+6){
										}
										else{break;}
										*/
									}
									buffer2=null;
									Declare.infotip=true;
									Declare.receive_flag=false;
									/*
									String datastr="";
									for(int k=0;k<Declare.data_facs.length;k++){
									datastr=datastr+Declare.data_facs[k]+",";
									}
									System.out.println("��ݽ�����"+datastr);*/
									System.out.println("收到数据长度"+buffer.length);
//									System.out.println(BytesToHexString(buffer).length());
//									System.out.println(BytesToHexString(buffer1));
//									System.out.println(BytesToHexString(buffer));
//									System.out.println(new String(buffer));
//									System.out.println(bytestoint1(buffer[0],buffer[1],buffer[2],buffer[3]));
							//}	
									Message msg1 = new Message();
									msg1.what = 5;
									mHandler.sendMessage(msg1);  
//									Declare.sendmsg_flag=true;					
//								}
						//		}
//						else{break;}
						}//din.available()>0
					 /*
					else
					{
						//�ж���ݽ����Ƿ�ʱ
						if(send_start_flag==true)
						{
							if(count>=set_count)
							{
								Declare.rec_overtime=true;
								count=0;
							}
							else{
								count++;
							}
						}
						//************2013.4.13ȥ��****ԭ���Ϊ�����뷢���໥���������պ������ʱ
						int time=0;
						switch(rec_select){
						case 0:
						case 1:
						case 2:
						case 3:
						case 4:
							time=1000;
						break;
						case 5:
							time=1500;
						break;
						}
						Thread.sleep(time, 0);
						//********************************************************************
						//Thread.sleep(30);
					//}
					}
					*/
				}
				catch (Exception e)
				{
					Declare.recvMessageClient = "接收异常:" + e.getMessage() + "\n";//消息换行
					buffer2=null;
					if(Declare.receive_flag==true)
					{
						Declare.rec_err=true;
						connect_flag=false;
						Message msg = new Message();
						msg.what = 0;
						mHandler1.sendMessage(msg);
						//====重新启动socket======
						Message msg1 = new Message();
						msg1.what = 2;
						mHandler.sendMessage(msg1);
					}
					return;
				}
			}
    	}
    }
	Handler mHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  if(msg.what == 0)
			  {
//				  recvText.append("Server: "+recvMessageServer);	// ˢ��
				  Log.d("MSG_CLIENT", Declare.recvMessageClient );
//				  Toast.makeText(getApplicationContext(), Declare.recvMessageClient, Toast.LENGTH_LONG).show();
				  DisplayToast("已连SOCKET接服务器");
				  try{
	    				Thread.sleep(200);	    			
	    				sendover_flag=false;
	    				send_msg(6,(short)0);
	    				//Thread.sleep(500);
				  }catch(Exception ex){
					  
	    		  }
			  }
			  else if(msg.what == 1)
			  {
//				  recvText.append("Client: "+recvMessageClient);	// ˢ��
				  DisplayToast("正在连接热点，请耐心等待！！");
			  }
			  else if(msg.what==2)
			  {
				  if(mWifiAdmin.checkState()==3 && isconnected1==true && connect_flag==false && Declare.Clientisconnect== false){
				  /*
				  if(++num_soket==4){
					  num_soket=0;
					  System.out.println("WIFI������");
					  mWifiAdmin.closeWifi();
					  mWifiAdmin.openWifi();
				  }*/
					  connect_flag=true;
					  try {
							Thread.sleep(100);
						 } catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					  	}
					  try {
						  if(mClient!=null)
						  {
							  mClient.close();
							  mClient = null;	
//							  mPrintWriterClient.close();
//							  mPrintWriterClient = null;
							  don.close();
							  don=null;
							  din.close();
							  din=null;
							  Declare.Clientisconnect = false;
						  }
						  if(mThreadClient!=null){			
							  mThreadClient.interrupt();
							  mThreadClient=null;
						}
					  } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					  }
					  //display=false;
					  //mThreadCheck.interrupt();
					  mThreadClient=new Thread(new AndroidClient());
					  mThreadClient.start(); 
					  
					  Log.d("MSG_CLIENT", Declare.recvMessageClient );
					  DisplayToast("正在对socket服务器进行连接，请稍等！！");
				  }
			  }
			  else if(msg.what==3)
			  {
				  try {
					  //Declare.rec_overtime=false; 
//					  System.out.println("WIFI������");
					  if(mClient!=null)
					  {
						  mClient.close();
						  mClient = null;	
//						mPrintWriterClient.close();
//						mPrintWriterClient = null;
						  don.close();
						  don=null;
						  din.close();
						  din=null;
						  Declare.Clientisconnect = false;
					  }
					  if(mWifiAdmin.checkState()==1){
						  mWifiAdmin.openWifi();
					  }
					  mWifiAdmin = new WifiAdmin(mSocketClient.this);
					  String str=null; 
				      if(mWifiAdmin.checkState()==3){
				    	  str=mWifiAdmin.getSSID();
				    	  try {
				    		  str=str.replace("\"", "");
							 } catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						  	}
				      }
						if(str!=null && isconnected1==true && str.equals(Declare.ssid)){
							 mWifiAdmin.disConnectionWifi(0);
							 try {
									Thread.sleep(1000);
								 } catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							  	}
						}
					  Declare.ssid_connecte_flag=1;
					  /*
					  for(int i=0;i<50;i++){
						  if(mWifiAdmin.checkState()==3){
							  mWifiAdmin.disConnectionWifi(0);
							  Declare.ssid_connecte_flag=1;
							break; 
						  }
						  else{
							  Thread.sleep(200);
						  }
						  }
					  Thread.sleep(300);
					  //mWifiAdmin = new WifiAdmin(mSocketClient.this);
					  /*
					  DisplayToast("������������WIFI����ȴ�");
					  mWifiAdmin = new WifiAdmin(mSocketClient.this);
					  if(mWifiAdmin.checkState()==3){
					  mWifiAdmin.closeWifi();
					  }
					  for(int i=0;i<50;i++){
					  if(mWifiAdmin.checkState()==1){
						  mWifiAdmin.openWifi();
						break; 
					  }
					  else{
						  Thread.sleep(300);
					  }
					  }
					  */
//					  System.out.println(Declare.ssid);
//					  System.out.println(Declare.ssid_pass);
					  /*
					  //Thread.sleep(1000);
					  //if(mWifiAdmin.checkState()==3){
						  try {
							  if(mClient!=null)
						  		{
								  mClient.close();
								  mClient = null;	
								  //mPrintWriterClient.close();
								  //mPrintWriterClient = null;
								  don.close();
								  don=null;
								  din.close();
								  din=null;
						  		}
						  } catch (IOException e) {
							  // TODO Auto-generated catch block
							  e.printStackTrace();
						  }
						  //display=false;
						  //mThreadCheck.interrupt();
						  mThreadClient.interrupt();
						  mThreadClient=new Thread(new AndroidClient());
						  mThreadClient.start();   
						  Log.d("MSG_CLIENT", Declare.recvMessageClient );
					  //}*/
				  
				  	} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				  	}
			  }
			  else if(msg.what==4)
			  {
				  Boolean isssid=false;
				  int level=0;
				  ScanResult scanResult=null;
				  if(Declare.ssid_connecte_flag==1)
					{  
					  mWifiAdmin.startScan();
					  System.out.println("热点扫描hh"+scan_ssid+"");
					  //---
					    mWifiManager.startScan();
				    	List<ScanResult> scanResultList = mWifiManager.getScanResults();
				    	  int i_count=scanResultList.size();
				    	  int t=i_count;
				          if (scanResultList != null && scanResultList.size() > 0) {
				              for (int i = 0; i < scanResultList.size(); i++) {
				            	  scanResult = scanResultList.get(i);
				            	  StringBuffer str = new StringBuffer()
				                       .append("SSID: " + scanResult.SSID).append("\n")
				                       .append("BSSID: " + scanResult.BSSID).append("\n")
				                       .append("capabilities: " + scanResult.capabilities).append("\n")
				                       .append("level: " + scanResult.level).append("\n")
				                       .append("frequency: " + scanResult.frequency);
				            	  //Log.i(TAG, str.toString());
				                 if (scanResult.SSID.equals(Declare.ssid)) {
				                	 level = Math.abs(scanResult.level);
				                     isssid= true;
				                     break;
				                 }//2016-03-21
				                 isssid=false;
				              }
					  //---
					  if(isssid){//2016-03-21
						  /*
						  System.out.println("�ȵ�"+mWifiAdmin.getSSID()+"");
						  System.out.println("mac��ַ"+Declare.ssid_connecte_flag+"");
						  System.out.println("����ID"+isconnected1+"");
      					*/
						  int wifi_level=level;//mWifiAdmin.level;
						  mWifiAdmin = new WifiAdmin(mSocketClient.this);
						  String str1=null; 
					      if(mWifiAdmin.checkState()==3){
					    	  str1=scanResult.SSID;//mWifiAdmin.getSSID();
					    	  try {
					    		  str1=str1.replace("\"", "");
								 } catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							  	}
					      }
							if(str1!=null && isconnected1==true && str1.equals(Declare.ssid)){
								Declare.ssid_connecte_flag=0;
								return;
							}
							else{
								/*
								try {
										Thread.sleep(500);
									 } catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
								  	}
								  	*/
//								System.out.println("ɨ��wifi�ȵ�ǿ��"+wifi_level);
								if(wifi_level<=70 && wifi_level>0 ){
									if(mWifiAdmin.targetWifiIsConfig()==true){
										mWifiAdmin.enableNetwork(mWifiAdmin.networkId);
									}
									else{
										mWifiAdmin.disconnectWifi();
										try {
											Thread.sleep(300);
										 } catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
									  	}
										mWifiAdmin.addNetWork( mWifiAdmin.CreateWifiInfo(Declare.ssid, Declare.ssid_pass, 3));
										try {
											Thread.sleep(800);
										 } catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
									  	}
									}
									//Toast.makeText(getApplicationContext(), "���ڽ�������", Toast.LENGTH_LONG).show();
									DisplayToast("正在对热点"+Declare.ssid+"进行连接,请稍等！！hh");
									Declare.ssid_connecte_flag=2;
									return;
								}
								else if(wifi_level>70 && wifi_level<90){
									if(num_circ==0){
									
									DisplayToast("指定wifi热点信号弱 ,请靠进前端设备，等待连接！！！");	
									}
								}
							}
      					}/////2016-03-21
      					else
      					{
      					//Toast.makeText(getApplicationContext(), "ָ��wifi�ȵ��ݲ�����,�����ȵ��Ƿ���", Toast.LENGTH_LONG).show();
      						if(toast_flag2==false){
	    						if(num_toast2<5)
	    						{
	    							if(num_circ==0){
	    								DisplayToast("正在扫描指定wifi热点:"+Declare.ssid+" ,请检查热点是否开启？");		
	    								num_toast2++;
	    							}
	    						}
	    						else{
	    							toast_flag2=true;
	    							num_toast2=0;
	    						}
	    					}
	    					else
	    					{
	    						if(++num_toast2==8){
	    							num_toast2=0;
	    							DisplayToast("正在扫描指定wifi热点:"+Declare.ssid+" ,请检查热点是否开启？？");	
	    						}
	    					}
      					}
				         //2016-03-21 }
			         }//2016-03-21
      				/*
				    System.out.println("�ȵ�"+mWifiAdmin.getSSID()+"");
		    		System.out.println("mac��ַ"+mWifiAdmin.getMacAddress()+"");
		    		System.out.println("����ID"+mWifiAdmin.getNetWordId()+"");
		    		System.out.println("IP��ַ"+mWifiAdmin.getIpAddress()+"");
		    		*/
					  System.out.println("连接失败状态"+isfailover1+"");
					}
			  }
			  if(msg.what == 5)
			  {
//				  recvText.append("Server: "+recvMessageServer);	// ˢ��
				  Log.d("MSG_CLIENT", Declare.recvMessageClient );
//				  Toast.makeText(getApplicationContext(), Declare.recvMessageClient, Toast.LENGTH_LONG).show();
			  }
			  if(msg.what == 6)
			  {
				  DisplayToast3("指定热点WIFI强度较弱，为了不影响使用\n请靠近前端设备！！！");
			  }
			  if(msg.what == 7)
			  {
				  DisplayToast1("指定热点WIFI强度极弱，有中断连接的可\n能请靠近前端设备！！！！");
			  }
			  if(msg.what == 8)
			  {
				    if(Declare.receive_flag==false)
				    {
				    	sendover_flag=false;
				    	send_msg(6,(short)0);	
				    	hart_send_flag=true;
				    }
			  }
		  }									
	 };
	 Handler mHandler2 = new Handler()
		{										
			  public void handleMessage(Message msg)										
			  {											
				  super.handleMessage(msg);	 
				  if(msg.what == 0)
				  {
					 // comfir_display();
					 //Toast.makeText(getApplicationContext(), "ͨѶ��������ʧ�ܣ�����WIFI��SOKETͨѶ���ӣ���", Toast.LENGTH_LONG).show();
 					  DisplayToast("通讯网络连接失败，请检查WIFI与SOKET通讯连接！！");
				  }
				  if(msg.what == 1)
				  {
					 // comfir_display();
					 //Toast.makeText(getApplicationContext(), "ͨѶ�����������", Toast.LENGTH_LONG).show();
					  DisplayToast("通讯网络连接正常！！");
				  }
				  if(msg.what == 2)
				  {
					 // comfir_display();
					 //Toast.makeText(getApplicationContext(), "ͨѶ�����������", Toast.LENGTH_LONG).show();
					  DisplayToast("正在打开WIFI网络！！");
				  }
			  }									
		 };
	/*
	 * ************自动重连检测线程****************	
	 */
	//@SuppressLint("HandlerLeak")
	public class mThreadCheck extends Thread{
		//@SuppressLint({ "HandlerLeak", "HandlerLeak" })
		@Override
		 public void run() {
		 // TODO Auto-generated method stub
			//mWifiAdmin = new WifiAdmin(mSocketClient.this);
			 while(!Thread.currentThread().isInterrupted() && check_flag){	
				 String str=null; 
				 try {
		    		if(mWifiAdmin.checkState()==1){
		    			 mWifiAdmin.openWifi();
		    			 Message msg_wifi = new Message();
						 msg_wifi.what = 2;
						 mHandler2.sendMessage(msg_wifi);
		    		}
		    		if(mWifiAdmin.checkState()==3 && Declare.ssid_connecte_flag==0){
		    			//获得当前连接信号强度
		    			  mWifiAdmin = new WifiAdmin(mSocketClient.this);
		    			  str=mWifiAdmin.getSSID();
				    	  try {
				    		  str=str.replace("\"", "");
							  
							 } catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
						  	}
		    			  if(str!=null && isconnected1==true && str.equals(Declare.ssid)){
		    				  RSSI=Math.abs(mWifiAdmin.getRssi());
		    				  System.out.println("RSSI"+RSSI);
		    				if(RSSI>=81 && RSSI<95){
		    					toast_flag1=false;
		    					num_toast1=0;
		    					level_flag=true;
		    					if(toast_flag==false){
		    						if(num_toast<5)
		    						{
		    							if(num_circ==0){
		    								Message msg_level = new Message();
		    								msg_level.what = 7;
		    								mHandler.sendMessage(msg_level);	
		    								num_toast++;
		    							}
		    						}
		    						else{
		    							toast_flag=true;
		    							num_toast=0;
		    						}
		    					}
		    					else
		    					{
		    						if(++num_toast==8){
		    							num_toast=0;
		    							Message msg_level = new Message();
		    							msg_level.what = 7;
		    							mHandler.sendMessage(msg_level);
		    						}
		    					}
		    				}
		    				else if(RSSI>=71 && RSSI<81){
			    					level_flag=true;
			    					toast_flag=false;
			    					num_toast=0;
			    					if(toast_flag1==false){
			    						if(num_toast1<5)
			    						{
			    							if(num_circ==0){
			    								Message msg_level = new Message();
			    								msg_level.what = 6;
			    								mHandler.sendMessage(msg_level);
			    								num_toast1++;
			    							}
			    						}
			    						else{
			    							toast_flag1=true;
			    							num_toast1=0;
			    						}
			    					}
			    					else
			    					{
			    						if(++num_toast1==8){
			    							num_toast1=0;
			    							Message msg_level = new Message();
			    							msg_level.what = 6;
			    							mHandler.sendMessage(msg_level);
			    						}
			    					}
			    				}
			    				else if(RSSI<71 && RSSI>0){
			    					level_flag=false;
			    					Toasts.toast1Cancle();
			    					num_toast=0;
			    					toast_flag=false;
			    					num_toast1=0;
			    					toast_flag1=false;
			    				}
						}
		    			else{
		    					level_flag=false;
		    			 }
		    		}
		    		/*
		    		 * **********连接指定wifi热点**************
		    		 */
		    		if(mWifiAdmin.checkState()==3){
		    			Message msg_con = new Message();
		    			msg_con.what = 4;
		    			mHandler.sendMessage(msg_con);
		    		}
		    		/*
		    		 * **********正在连接显示**************
		    		 */
		    		if(Declare.ssid_connecte_flag==2){
		    			
		    			Message msg = new Message();
		    			msg.what = 1;
		    			mHandler.sendMessage(msg);
		    			if(++num_lj>=3){
		    				num_lj=0;
		    				Declare.ssid_connecte_flag=0;
		    				}
		    		}
		    		/*
		    		 * **********连接指定点socket**************
		    		 */
		    		if(Declare.ssid_connecte_flag==3){
		    			try{
		    				Thread.sleep(1000);
		    			}catch(Exception ex){
		    			}
		    			connect_flag=false;
		    			Message msg_con = new Message();
		    			msg_con.what = 2;
		    			mHandler.sendMessage(msg_con);
		    			if(++num_socket>=3){
		    				num_socket=0;
		    				Declare.ssid_connecte_flag=0;
		    				}
		    		}
		    		/*
		    		 * ********前端断电重连机制****************
		    		 */
		    		if(Declare.rec_overtime==true && Declare.Circle==true && Declare.ssid_connecte_flag==0 && level_flag==false){
		    			if(++Declare.connect_num==1){
		    				Declare.Clientisconnect=false;
		    				isconnected1=false;//连接标志位置
		    				Declare.rec_overtime=false;
		    				count=0;
		    				num_overtime=0;
		    				Message msg = new Message();
		    				msg.what = 3;
		    				mHandler.sendMessage(msg);
		    			}
		    			if(++Declare.connect_num1==12){Declare.connect_num=0;Declare.connect_num1=0;}
		    		}
		    		else{Declare.connect_num=0;Declare.connect_num1=0;}
		    		/*
		    		 * ******非发送数据情况下断线重连**************
		    		 */
		    		if(Declare.Circle==false && Declare.ssid_connecte_flag==0 && level_flag==false){
		    			//isconnected1=false;//连接标志位置
		    			/*
		    			String str=null;
		    			 if(mWifiAdmin.checkState()==3){ 
		    			   str=mWifiAdmin.getSSID();
					    	  try {
					    		  str=str.replace("\"", "");
								 } catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							  	}
		    			 }
		    			 */
		    			if(str!=null && str.equals(Declare.ssid) && isconnected1==true){
		    				//发送调取参数命令
		    				if(++num_circ1==2){
		    					num_circ1=0;
		    				if(hart_send_flag==true){
		    					//回读数据判断是否短链
		    					if(Declare.data_para1[0]==9){
		    						//====清空数据===================
		    						for(int j=0;j<Declare.data_para1.length;j++){
		    							Declare.data_para1[j]=0;
		    						}
		    						num_overtime1=0;
		    					}
		    					else{
		    						if(++num_overtime1==3){
		    							num_overtime1=0;
		    							num_circ1=0;
		    							Declare.Clientisconnect=false;
		    		    				isconnected1=false;//连接标志位置
		    		    				Declare.rec_overtime=false;
		    		    				count=0;
		    							hart_send_flag=false;
		    							Message msg = new Message();
		    							msg.what = 3;
		    							mHandler.sendMessage(msg);
		    						}
		    					}
		    				}
	    					for(int j=0;j<Declare.data_para1.length;j++){
	    						Declare.data_para1[j]=0;
	    					}
	    					try {
	    						//Thread.sleep(200);
	    					 } catch (Exception e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    				  	}
	    					sendover_flag=false;
					    	send_msg(6,(short)0);	
					    	hart_send_flag=true;	
		    				}	
		    			}	
		    			else{
		    				num_overtime1=0;
		    				num_circ1=0;
		    				Declare.Clientisconnect=false;
		    				isconnected1=false;//连接标志位置
		    				Declare.rec_overtime=false;
		    				count=0;
							hart_send_flag=false;
		    				Message msg = new Message();
		    				msg.what = 3;
		    				mHandler.sendMessage(msg);
		    			}
		    		}
		    		//***********通讯结果
		    		if(Declare.ssid_connecte_flag==0){
		    		try{
						  mClient.sendUrgentData(0);
						  if(Declare.txlj_ok_flag==false && isconnected1==true){
							  Declare.txlj_ok_flag=true;
							  Declare.txlj_erro_flag=false;
			    				Message msg2 = new Message();
			    				msg2.what = 1;
			    				mHandler2.sendMessage(msg2);
							   // sum=0;
	    					}						  
					  }catch(Exception ex){
						  //comfir_flag=true;
						  if(Declare.Clientisconnect==false || Declare.rec_err==true ){//|| isconnected1==false
			    				//check_flag=false;
			    				//if(sum>=0)
								//{
			    					if(Declare.txlj_erro_flag==false){
		//	    						Declare.txlj_erro_flag=true;
			    						Declare.txlj_ok_flag=false;
//			    						Declare.ssid_connecte_flag=0;
					    				Message msg1 = new Message();
					    				msg1.what = 0;
					    				mHandler2.sendMessage(msg1);
									   // sum=0;
			    					}
								//}
								//else{
								//	sum++;
								//}
			    		}
						  Declare.recvMessageClient = "连接服务器异常\n";//消息换行
						  connect_flag=false;
						  Message msg = new Message();
						  msg.what = 2;
						  mHandler.sendMessage(msg);
					  }
		    		}
		    		if(++num_circ==2){num_circ=0;}
		    		/*
		    		//WIFI����ָ���ȵ�
		    		if(mWifiAdmin.targetWifiCanScan()==true){
		    			System.out.println(mWifiAdmin.getSSID()+"");
    					if(!mWifiAdmin.getSSID().equals(Declare.ssid) ||(mWifiAdmin.getSSID().equals(Declare.ssid) && isconnected1==false))
    						{
    						mWifiAdmin.addNetWork( mWifiAdmin.CreateWifiInfo(Declare.ssid, Declare.ssid_pass, 3));
 //   						Toast.makeText(getApplicationContext(), "���ڽ�������", Toast.LENGTH_LONG).show();
    						}
    				}
		    		*/
		    		/*
		    		 * ********线程关闭*************
		    		 */
		    		Thread.sleep(1000);
		    	    } 
		    	catch (InterruptedException e) {
		    	     System.err.println("InterruptedException！线程关闭");
//		    	     this.interrupt();
//	        	     DisplayToast("�̹߳ر�"); 
		    	     check_flag=false;
		    	     this.interrupt();
		    	     return;
		    	    }
		    	 }
			 mThreadCheck=null;
			 check_flag=false;
			 //StopClientmsg();
			 //mWifiAdmin.disconnectWifi();
			 mSocketClient.StopClientmsg();
		 }
	}
	//======消息框提示===========
	private void comfir_display() {	
		//20160303			Intent intent_facs = new Intent(this,confirm_glob.class); 
		//20160303			intent_facs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//20160303            startActivity(intent_facs); 
	}
	 /*
	 Handler mHandler_Check = new Handler()
		{										
//			  @SuppressLint("HandlerLeak")
     
			public void handleMessage(Message msg)										
			  {											
				  super.handleMessage(msg);	
				  if(msg.what == 0)
				  {
//					 if(Declare.Clientisconnect){
						  try{
//							  if(mClient!=null)
//								{
							  mClient.sendUrgentData(0);//�ж��Ƿ������������
//									  scoket1.sendUrgentData(0);//�ж��Ƿ������������
//								}
						  }catch(Exception ex){
							  /*
							  if(count==40){
								  count=0;
								  Declare.recvMessageClient = "���ӷ������쳣\n";//��Ϣ����
								  Message msg1 = new Message();
								  msg1.what = 2;
								  mHandler.sendMessage(msg1);
							  }
							  count++;
							  Declare.recvMessageClient = "���ӷ������쳣\n";//��Ϣ����
							  Message msg1 = new Message();
							  msg1.what = 2;
							  mHandler.sendMessage(msg1);
							  return;
						  }
				  }
			  }									
		 };*/
	private String getInfoBuff(char[] buff, int count)
	{
		char[] temp = new char[count];
		for(int i=0; i<count; i++)
		{
			temp[i] = buff[i];
		}
		return new String(temp);
	}
	public void getClient_ip() {
		SharedPreferences settings = getSharedPreferences("Cline_ip",  0);
	    String str_ip=settings.getString("ip1", "192")+"."+
	    	settings.getString("ip2", "168")+"."+
	    	settings.getString("ip3", "1")+"."+
	    	settings.getString("ip4", "44");
	    String str_port=settings.getString("port", "25000");
	    IP="192.168.1.44";//str_ip;
	    PORT=25000;//Integer.parseInt(str_port);
	}
	public static void test(){
		
	}
	@SuppressWarnings("deprecation")
	private void showNotification() { 
         // 创建一个NotificationManager的引用 
        // 定义Notification的各种属性 
        @SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.client, 
				"客户端服务启动", System.currentTimeMillis()); 
               PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, txxs.class), 0);
        // must set this for content view, or will throw a exception
              notification.setLatestEventInfo(this, "客户端", "客户端启动", contentIntent);
        notificationManager.notify(0, notification); 
    } 
	public static void send_msg(int send_flag,short data){
		String str=null;
    	switch(send_flag){
		case 0://
			str="EB 03 00 00";
			break;
		case 1://
			str="EB 04 00 00";
			break;
		case 2:
			str="EB 05 00 00";
			break;
		case 3://
			str="EB 06 00 00";
			break;
		case 4:
			str="EB 07 00 00";
			break;
		case 5://
			str="EB 08 00 00";
			break;
		case 6:
			str="EB 09 00 00";
			break;
		case 7:
			//设置电压量程
			str="EB 0A"+BytesToHexString(shorttobytes(data));			
			break;
		case 8:
			//设置电表常数
			str="EB 0B"+BytesToHexString(shorttobytes(data));			
			break;
		case 9:
			//设置校表圈数
			str="EB 0C"+BytesToHexString(shorttobytes(data));			
			break;
		case 10:
			//设置电流系数
			str="EB 0D"+BytesToHexString(shorttobytes(data));			
			break;
		case 11:
			//设置分频系数
			str="EB 0E"+BytesToHexString(shorttobytes(data));			
			break;
		case 12:
			//设置电表制式ʽ
			str="EB 0F"+BytesToHexString(shorttobytes(data));			
			break;
		case 13:
			//设置电流量程
			str="EB 10"+BytesToHexString(shorttobytes(data));			
			break;
		case 14:
			//设置脉冲方式
			str="EB 11"+BytesToHexString(shorttobytes(data));			
			break;
		case 15:
			//停止误差校验
			str="EB ED 00 00";			
			break;
		case 16:
			str="EB 09 00 00";
			break;
		case 17:
			str="EB F0 00 00";
			break;
		}
    	//校验更改为校验和取反
//	    	byte[] crc16=HexStringtoBytes(str.replace(" ", ""));
    	byte[] total=HexStringtoBytes(str.replace(" ", ""));
    	str=str.replace(" ", "")+jyh(total,total.length);	
//	    	System.out.println(str); 
//	    	System.out.println(HexStringtoBytes(str).length);
//	    	System.out.println(str.getBytes().length);
//			mClient.mPrintWriterClient.print(HexStringtoBytes(str));//���͸������
//			mClient.mPrintWriterClient.flush();	
    	sendover_flag=true;//�÷�����ɱ�־λ 2013.4.12
	    try 
		{					    		    				
	    	don.write(HexStringtoBytes(str));
		    don.flush();
//		    mClient.sendUrgentData(0);//2013.8.12ȥ��
		    Declare.recvMessageClient = "Client：发送数据"+str+"\n";
			Message msg = new Message();
			Declare.infotip=true;
	        msg.what = 0;
	        mHandler1.sendMessage(msg);					
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		Declare.recvMessageClient = "Client：发送异常:" + e.getMessage() + "\n";//消息换行
			Declare.infotip=true;
			Message msg = new Message();
            msg.what = 0;
			mHandler1.sendMessage(msg);
		}		
	}
	//=======发送设置命令第2种方式========================
	public static void send_setmsg(int send_flag,short data){
		if(Declare.Clientisconnect==true && Declare.rec_err==false){
			String str=null;
//			for(int i=0;i<2;i++){
			switch(send_flag){
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:						
				break;
			case 7:
				//设置电压量程
				str="EB 0A"+BytesToHexString(shorttobytes(data));			
				break;
			case 8:
				//设置电表常数
				str="EB 0B"+BytesToHexString(shorttobytes(data));			
				break;
			case 9:
				//设置校表圈数
				str="EB 0C"+BytesToHexString(shorttobytes(data));			
				break;
			case 10:
				//设置电流系数
				str="EB 0D"+BytesToHexString(shorttobytes(data));			
				break;
			case 11:
				//设置分频系数
				str="EB 0E"+BytesToHexString(shorttobytes(data));			
				break;
			case 12:
				//设置电表制式
				str="EB 0F"+BytesToHexString(shorttobytes(data));			
				break;
			case 13:
				//设置电流量程
				str="EB 10"+BytesToHexString(shorttobytes(data));			
				break;
			case 14:
				//设置脉冲方式
				str="EB 11"+BytesToHexString(shorttobytes(data));			
				break;
			case 15:
				//停止误差校验
				str="EB ED 00 00";			
				break;
			case 16:
				//读取前端设备参数
				str="EB 09 00 00";
				break;
			}
			//校验更改为校验和取反
//    		byte[] crc16=HexStringtoBytes(str.replace(" ", ""));
			byte[] total=HexStringtoBytes(str.replace(" ", ""));
			str=str.replace(" ", "")+jyh(total,total.length);	
			System.out.println(str); 
			try 
			{					    		    				
				don.write(HexStringtoBytes(str));
				don.flush();
//				mClient.sendUrgentData(0);//2013.8.12ȥ��
				Declare.recvMessageClient = "Client：发送数据"+str+"\n";
				Message msg = new Message();
				Declare.infotip=true;
				msg.what = 0;
				mHandler1.sendMessage(msg);	
				//Thread.sleep(100,0);
			}
			catch (Exception e) 
			{
				// TODO: handle exception
				Declare.recvMessageClient = "Client：发送异常:" + e.getMessage() + "\n";//消息换行
				Declare.infotip=true;
				Message msg = new Message();
				msg.what = 0;
				mHandler1.sendMessage(msg);
			}
//		}
	}
		else{
		}
}
	//=========发送调试命令=============================
	public static void send_debug_msg(String str_order,String str_para){
		String str=null;
    	str="EB"+str_order+str_para;
    	//校验更改为校验和取反
    	byte[] total=HexStringtoBytes(str.replace(" ", ""));
    	str=str.replace(" ", "")+jyh(total,total.length);	
    	System.out.println(str); 
    	if(Declare.Clientisconnect==true && Declare.rec_err==false){
    		try 
    		{					    		    				
    			don.write(HexStringtoBytes(str));
    			don.flush();
//    			mClient.sendUrgentData(0);//2013.8.12ȥ��
//    			sendover_flag=true;//�÷�����ɱ�־λ 2013.4.12
    			Declare.infotip=true;
    			Declare.recvMessageClient = "Client：发送数据"+str+"\n";
    			Message msg = new Message();    			
    			msg.what = 0;    			
    			mHandler1.sendMessage(msg);					
    		}
    		catch (Exception e) 
    		{
    			// TODO: handle exception
    			Declare.recvMessageClient = "Client：发送异常:" + e.getMessage() + "\n";//消息换行
    			Declare.infotip=true;
    			Message msg = new Message();
    			msg.what = 0;
    			mHandler1.sendMessage(msg);
    		}
    	}
}
/*	
	public static void recevie_msg(){
//		byte[] buffer = new byte[2048];
//		int count = 0;
//		boolean enable;
//		enable=true;
//	    while(enable=true){		 								    				   											
			try
				{
				for(int i=0;i<8;i++)
					{
					//if ( (recvMessageClient = mBufferedReaderClient.readLine()) != null )
					/*
					if((count = mClient.mInputReaderClient.read(buffer))>0)
					{						
						Declare.recvMessageClient = new String(buffer,0,count) + "\n";//��Ϣ����
						Message msg1 = new Message();
						msg1.what = 0;
						mHandler.sendMessage(msg1);
//						enable=false;
						break;
					}
					Declare.receive_flag=false;
					if(din.available()>0)
						{						
							byte[] buffer=new byte[din.available()];
//							byte[] buffer = new byte[2048];
						
							din.read(buffer);    						
							Declare.recvMessageClient = new String(BytesToHexString(buffer)) + "\n";//��Ϣ����
						if(buffer[0]==(byte)0xEB && Declare.receive_flag==false)
							{
								Declare.receive_flag=true;
								int len=buffer.length;
//								String crc_read=crcTable(buffer,len-2);
								String crc_read=null;
//								String crc_str=crcToHexString(buffer[len-2],buffer[len-1]);
								String crc_str=null;
								if(crc_read.equals(crc_str))
								{
									System.out.println("crczhengque");
//									int bh=buffer[2] & 0xff;
									int data_len =lentoshort(buffer[3],buffer[4])/4;
								
									if(buffer[1]==(byte)0x03){	
//										data_jx(buffer,Declare.data_xbfx);
//										data_jx(buffer,Declare.data_facs);										
//										data_jx(buffer,Declare.data_wcxj);
//										data_jx(buffer,Declare.data_ctcs);
//										data_jx(buffer,Declare.data_jxpb);
//										data_jx(buffer,Declare.data_bxxs);
									}
									else if(buffer[1]==(byte)0x04){
//										data_jx(buffer,Declare.data_jxpb);
									}
									else if(buffer[1]==(byte)0x05){
//										data_jx(buffer,Declare.data_wcxj);
									}
									else if(buffer[1]==(byte)0x06){
//										data_jx(buffer,Declare.data_xbfx);
									}
									//��Ȳ���
									else if(buffer[1]==(byte)0x07){
									}
									else if(buffer[1]==(byte)0x08){
//										data_jx(buffer,Declare.data_bxxs);
									}
									Declare.infotip=true;
									Declare.receive_flag=false;
									String datastr="";
									for(int k=0;k<Declare.data_facs.length;k++){
									datastr=datastr+Declare.data_facs[k]+",";
									}
									System.out.println("��ݽ�����"+datastr);
									System.out.println("�յ���ݳ���"+buffer.length);
//									System.out.println(BytesToHexString(buffer).length());
//									System.out.println(BytesToHexString(buffer1));
//									System.out.println(BytesToHexString(buffer));
//									System.out.println(new String(buffer));
//									System.out.println(bytestoint1(buffer[0],buffer[1],buffer[2],buffer[3]));
									Message msg1 = new Message();
									msg1.what = 0;
									mHandler1.sendMessage(msg1);  
									Declare.sendmsg_flag=true;
									break;   						
								}
							}
						else{break;}
						}//din.available()>0
						else
						{
							Thread.sleep(100, 0);
						}
					}	
				}
				catch (Exception e)
				{
					Declare.recvMessageClient = "�����쳣:" + e.getMessage() + "\n";//��Ϣ����
					Message msg = new Message();
					msg.what = 0;
					mHandler1.sendMessage(msg);
				}
	}
	*/
	//启动信息发送线程
	public static void SendClientmsg(boolean circle){
		if(mThreadClient_send==null){
			Declare.Circle=circle;
			send_start_flag=true;
			mThreadClient_send=new mThreadClient_send();
			mThreadClient_send.start();
		}
	}
	//停止发送线程
	public static void StopClientmsg(){
		if(mThreadClient_send!=null){			
			Declare.Circle=false;
			send_start_flag=false;
			mThreadClient_send.interrupt();
			mThreadClient_send=null;
		}		
	}
	public static class mThreadClient_send extends Thread{
	    public void run() {
	    	/*
	    	try {
	             Thread.sleep(100,0);
	        } catch (InterruptedException e) {
	             e.printStackTrace();
	        }*/
	    	try 
	    	{				    	
	     		if(Declare.Circle==true)
	     		{
	     			while(Declare.Circle)
	     			{
	     				Declare.sendmsg_flag=false;
	     	    		//if(mClient!=null)
//	     				if(Declare.rec_err==false )
//	     				{
	     				    //判断数据接收是否超时
							if(sendover_flag==true )//&& mWifiAdmin.checkState()==3
							{
								if(count>=set_count)
								{
									if(Declare.rec_overtime==false && Declare.ssid_connecte_flag==0){
										Message msg = new Message();
										msg.what = 1;
										mHandler1.sendMessage(msg);
									}
									Declare.rec_overtime=true;									
									count=0;
									if(++num_overtime>=2){num_overtime=2;}
								}
								else{
									count++;
								}
							}
							else{
								count=0;
							}
							System.out.println("count "+count); 
							System.out.println(Declare.ssid_connecte_flag+"");
//	     	    			for(int i=0;i<3;i++){
	     	    				if(Declare.Clientisconnect==true && Declare.sendmsg_flag==false && isconnected1==true && Declare.rec_err==false){
	     	    					sendover_flag=false;//置发送完成标志复位 2013.4.12
	     	    					
	     	    					if(RSSI>0 && RSSI<=81 && num_overtime<2){
	     	    						send_msg(Declare.send_flag,Declare.send_data);
	     	    					}
	     	    				}	
	     	    				else{sendover_flag=true;}
//	     	    				recevie_msg();
//	     	    				if(Declare.sendmsg_flag==true){
//	     	    					break;
//	     	    				}
//	     	    			}
//	     				}
	     					//根据数据接收的大小确定发送的间隔*****可以不用2013.4.12
	     				    int time=200;
	     					switch(Declare.send_flag){
	     					case 0:
	     					case 1:
	     					case 4:
	     						time=1000;
	     						set_count=3;
	     						break;
	     					case 3:
	     						time=2000;
	     						set_count=4;
	     					case 5:
	     						time=1500;
	     						set_count=6;
	     						break;
	     					case 2:
	     						time=200;
	     						set_count=20;
	     					break;
	     					case 6:
	     						time=1000;
	     						set_count=4;
	     						break;
	     					case 17:
	     						time=1000;
	     						set_count=4;
	     					break;
	     					}
	     					Thread.sleep(time,0);
	     				//Thread.sleep(1000,0);
	     			}
	     		}else{
	     			Declare.sendmsg_flag=false;
//	        		if(mClient!=null)
	     			if(Declare.Clientisconnect==true)
	    			{
	        			for(int i=0;i<1;i++){
	        				if(Declare.sendmsg_flag==false){
	        					send_msg(Declare.send_flag,Declare.send_data);
	        				}	    			
//	        				recevie_msg();//��ݽ��ո�Ϊ�����˿ں�ʼ�ռ���ע�͵�2013.1
	        				Thread.sleep(100,0);//数据接收改为建立端口后始终检测后修改2013.1.6
	 	    				if(Declare.sendmsg_flag==true){
	 	    					break;
	 	    				}
//	            			socket_client.sock_disconn();
	        			}
	    			}
	        		//
	        		StopClientmsg();
//	            		mThreadClient_send.interrupt();
	     		}
	    	}
	    	catch (Exception e) 
	    	{
	    		// TODO: handle exception
	    		Declare.recvMessageClient = "数据发送线程关闭！！\n";//消息换行
	    		Declare.infotip=true;
	    		Message msg = new Message();
	            msg.what = 0;
	    		mHandler1.sendMessage(msg);
	    	}		
	    }
	 }
	public static Handler mHandler1 = new Handler()
	{	
		public void handleMessage(Message msg)										
		{											
			super.handleMessage(msg);			
			if(msg.what == 0)
			{
//				recvText.append("Server: "+recvMessageServer);	// ˢ��
				Log.d("MSG_CLIENT", Declare.recvMessageClient );
//				Toast.makeText(context, Declare.recvMessageClient, Toast.LENGTH_SHORT).show();
//				View view = LayoutInflater.from(context).inflate(R.layout.txlj, null); 
//				TextView textbxrd1=(TextView) view.findViewById(R.id.textbxrd1);
//				textbxrd1.setText(Declare.recvMessageClient);
			}
			if(msg.what == 1)
			{
				DisplayToast2("数据接收超时！！！");
			}
		}									
	};
	public void DisplayToast(String msg){
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		Toasts.toast(this, msg, Toast.LENGTH_SHORT,Gravity.TOP, 0, 1020);
	}	
	public void DisplayToast1(String msg){
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		Toasts.toast(this, msg);
	}
	public static void DisplayToast2(String msg){
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//20160303		Toasts.toast(MainApplication.getContext(), msg, Toast.LENGTH_SHORT,Gravity.TOP, 0, 520);
	}	
	public void DisplayToast3(String msg){
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		Toasts.toast1(this, msg);
	}
	 //  32位整数数据解析
	public static void data_jx(byte[] buffer,int start,int data_len,int[] data,int start1){
//		data=new int[data_len];
		for(int j=0;j<data_len;j++){
//				Declare.data_facs[(k+1)*j]=bytestoint1(buffer[5+j*4],buffer[j*4+6],buffer[j*4+7],buffer[j*4+8]);
				data[start1]=bytestoint1(buffer[start+j*4],buffer[j*4+start+1],buffer[j*4+start+2],buffer[j*4+start+3]);
//				System.out.println(data[start1]);
				start1++;
			}
//				System.out.println(data_len);
	}
	 // 16位整数数据解析
	public static void data_jx1(byte[] buffer,int start,int data_len,int[] data,int start1){
//		data=new int[data_len];
		for(int j=0;j<data_len;j++){
//				Declare.data_facs[(k+1)*j]=bytestoint1(buffer[5+j*4],buffer[j*4+6],buffer[j*4+7],buffer[j*4+8]);
				data[start1]=(int)lentoshort(buffer[start+j*2],buffer[j*2+start+1]);
//				System.out.println(data[0]);
//				System.out.println(data[1]);
//				System.out.println(data[start1]);
				start1++;
			}
//				System.out.println(data_len);
	}
	/* 
	//CRC16校验
	public static String crcTable(byte[] bytes,int length) {
	  	int[] table = {    
	  			0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
	  		    0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440, 
	  		    0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
	  	        0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,             
	  			0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,  
	  	        0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,         
	  	        0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,   
	  	        0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,       
	  	        0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,     
	  	        0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,         
	  	        0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,   
	  	        0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,      
	  	        0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,       
	  	        0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,    
	  	        0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,        
	  	        0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,          
	  	        0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,          
	  	        0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,   
	  	        0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,       
	  	        0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,       
	  	        0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,       
	  	        0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,         
	  	        0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,      
	  	        0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,      
	  	        0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,      
	  	        0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,       
	  		    0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,   
	  		    0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841, 
	  	        0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,      
	  	        0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,    
	  	        0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,     
	  	        0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,  
	  	};           
//	  	int CRCVal=0;   
	  	short CRCVal=0;
	  	int i=0;     
	  	for(i=0; i <length; i++){    
	  		//CRCVal = table[(CRCVal ^= ((bytes[i]) & 0xFF)) & 0xFF] ^ (CRCVal>>8);   
	  		CRCVal = (short) (((CRCVal & 0xFF) << 8) ^ table[(((CRCVal & 0xFF00) >> 8) ^ bytes[i]) & 0xFF]);
	  	} 
//	  	String str=Integer.toHexString(CRCVal).toUpperCase();
	  	String str=BytesToHexString(CRCtobytes(CRCVal));
	  	System.out.println(str);
	  	return str; 
	}*/
	//校验和取反
	public static String jyh(byte[] bytes,int length){
		byte k = 0;
		for( int i=0; i <length; i++){  
			k += bytes[i]; 
		}
		k=(byte) ~k;
		byte[] K1=new byte[1];
		K1[0]=k;
		String str=BytesToHexString(K1);
//		System.out.println(str);
		return str;
	}
	public static byte jyh_receive(byte[] bytes,int start,int length){
		byte k = 0;
		for( int i=start; i <start+length; i++){  
			k += bytes[i]; 
		}
		byte[] K1=new byte[1];
		K1[0]=k;
//		String str=BytesToHexString(K1);
//		System.out.println(str);
		return k;
	}
	///整数转字节组====低字节在前
	public static byte[] inttobytes(int n) {
	  	byte[] ab = new byte[4];
	  	ab[0] = (byte) (0xff & n);	
	  	ab[1] = (byte) ((0xff00 & n) >> 8);
	  	ab[2] = (byte) ((0xff0000 & n) >> 16);
	  	ab[3] = (byte) ((0xff000000 & n) >> 24);		 		 		  		 
//	  	System.out.print(new String(ab)); 
//	  	System.out.println();
	  	return ab;
	}
	//字节组转整数=====低字节在前
	public static int bytestoint(byte b[]) {
	  	int s = 0;
	  	s = ((((b[3] & 0xff) << 8 | (b[2] & 0xff)) << 8) | (b[1] & 0xff)) << 8
	  		 | (b[0] & 0xff);
//	  	System.out.println(s); 
	  	return s;
	}	  	 
	public static int bytestoint1(byte a1,byte a2,byte a3,byte a4) {
	  	int s = 0;
	  		s = ((((a4 & 0xff) << 8 | (a3 & 0xff)) << 8) | (a2 & 0xff)) << 8
	  		 | (a1 & 0xff);
// 		System.out.println(s); 
	  	return s;
	} 
	//CRC短整数转字节组高在前============================
	public static byte[] CRCtobytes(short n) {
	  	byte[] ab = new byte[2];
	  	ab[0] = (byte) ((0xff00 & n) >> 8);
	  	ab[1] = (byte) (0xff & n);
//	  	System.out.print(new String(ab)); 
//	  	System.out.println();
	  	return ab;
	}
	//短整数转字节组============================
	public static byte[] shorttobytes(short n) {
	  	byte[] ab = new byte[2];
	  	ab[0] = (byte) (0xff & n);
	  	ab[1] = (byte) ((0xff00 & n) >> 8);
//	  	System.out.print(new String(ab)); 
//	  	System.out.println();
	  	return ab;
	}
	//字节组转短整数==========================
	public static short bytestoshort(byte b[]) {
	 	short s = 0;
	 	s = (short)((b[1] & 0xff) << 8| (b[0] & 0xff));
//	 	System.out.println(s); 
	 	return s;
	}
	//两个字节数据长度转换
	public static short lentoshort(byte a,byte b) {
	 	short s = 0;
	 	s = (short)((b & 0xff) << 8| ( a& 0xff));
//	 	System.out.println(s); 
	 	return s;
	}
	private final static byte[] hex = "0123456789ABCDEF".getBytes();
	//字节组到16进制字符串转换
	public static String BytesToHexString(byte[] b) {
	  	byte[] buff = new byte[2 * b.length];
	  	for (int i = 0; i < b.length; i++) {
	  	buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
	  	buff[2 * i + 1] = hex[b[i] & 0x0f];
	  	}
	  	return new String(buff);
	}
	private static int parse(char c) {
	  	if (c >= 'a')
	  		return (c - 'a' + 10) & 0x0f;
	  	if (c >= 'A')
	  		return (c - 'A' + 10) & 0x0f;
	  	return (c - '0') & 0x0f;
	}
	//16进制到字节组的转换
	public static byte[] HexStringtoBytes(String hexstr) {
	  	byte[] b = new byte[hexstr.length() / 2];
	  	int j = 0;
	  	for (int i = 0; i < b.length; i++) {
	  		char c0 = hexstr.charAt(j++);
	  		char c1 = hexstr.charAt(j++);
	  		b[i] = (byte) ((parse(c0) << 4) | parse(c1));
	  	}
	  	return b;
	 }
	public static byte[] sysCopy(List<byte[]> srcArrays) {
	  	int len = 0;
	  	for (byte[] srcArray:srcArrays) {
	  	   len+= srcArray.length;
	  	}
	  	byte[] destArray = new byte[len];   
	  	int destLen = 0;
	  	for (byte[] srcArray:srcArrays) {
	  	     System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);   
	  	     destLen += srcArray.length;   
	  	}   
	  	     return destArray;
	} 
/*
	public static String crcToHexString(byte a, byte b) {
	  	byte[] buff = new byte[4];
	  	buff[0] = hex[(a >> 4) & 0x0f];
	  	buff[1] = hex[a & 0x0f];
	  	buff[2] = hex[(b >> 4) & 0x0f];
	  	buff[3] = hex[b & 0x0f];
	  	System.out.println("CRC"+new String(buff)); 
	  	return new String(buff);
	}
	*/
	class WifiReceiver extends BroadcastReceiver
	{
		public void onReceive(Context context, Intent intent)
		{
			 mWifiAdmin = new WifiAdmin(mSocketClient.this);
			if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
			{
				 scan_ssid=mWifiAdmin.targetWifiCanScan();
			}
			if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
			{
				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO); 
				 if (null != parcelableExtra) { 
				      NetworkInfo networkInfo = (NetworkInfo) parcelableExtra; 
				      boolean isconnected = networkInfo.isConnected(); 
				      boolean isavailable = networkInfo.isAvailable();
				      boolean connecting = networkInfo.isConnectedOrConnecting();
				      boolean isfailover = networkInfo.isFailover();
				      isconnected1 = isconnected; 
				      isfailover1=isfailover;
				      isconnecting=connecting;
				      String str=null; 
				      if(mWifiAdmin.checkState()==3){
				    	  str=mWifiAdmin.getSSID();
				    	  try {
				    	   	  str=str.replace("\"", "");
						  } catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								str=str;
						  	}
				      }
			    	  if(str!=null && isconnected1==true && str.equals(Declare.ssid))
		    			{
			    		  //DisplayToast("指定热点"+Declare.ssid+"已成功连接！！");
			    		  System.out.println("指定热点已连接");
			    		  num_lj=0;
			    		  Declare.ssid_connecte_flag=3;
			    		  Declare.txlj_ok_flag=false;
			    		  Declare.txlj_erro_flag=false;
			    		  Declare.Clientisconnect=false;
		    			}
                     if(isconnected1==true ){
                    	 System.out.println("热点已连接");
                     }
                     System.out.println("连接状态"+connecting);
			}
			}
		}
	}
}
