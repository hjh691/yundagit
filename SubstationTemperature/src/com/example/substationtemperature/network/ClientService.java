package com.example.substationtemperature.network;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DateTimePickDialogUtil.DateTimeControl;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.substationtemperature.FragmentAlert;
import com.example.substationtemperature.FragmentChar;
import com.example.substationtemperature.FragmentDailyrecord;
import com.example.substationtemperature.FragmentHistraydata;
import com.example.substationtemperature.FragmentIndex;
import com.example.substationtemperature.FragmentPicture;
import com.example.substationtemperature.FragmentRealdata;
import com.example.substationtemperature.Fragment_Realwarning;
import com.example.substationtemperature.MainActivity;
import com.example.substationtemperature.base.AlertInfo;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.MeasureData;
import com.example.substationtemperature.base.PropertyForDrawPicture;
import com.example.substationtemperature.base.ReturnData;
import com.example.substationtemperature.base.SensorsInfo;
import com.example.substationtemperature.base.StationInfo;
import com.example.substationtemperature.floatwindow.WindowService;
import com.example.substationtemperature.floatwindow.fudongService;
import com.example.substationtemperature.view.Toasts;

public class ClientService extends Service {
	//private static ClientService clentservice=new ClientService();
	//private static IncomingHandler mhandler= new IncomingHandler(clentservice);
	public static StationInfo[]  stArray;
	public static SensorsInfo ssArray[]={};
	public static String[] cldArray={};
	public static String zhanArray[]={};//=new String[1];
	public static ReturnData iis;
	public static Handler mhandler=null;
	public static MeasureData[] mdArray;
	public static ArrayAdapter<String> zhanAdapter=null,cldAdapter=null;
	
	private ConnectivityManager mConnectivityManager;  
	private NetworkInfo netInfo; 
	public static long DataId=0;//最新实时数据ID。用于获取实时数据。为零为全部测量点的实时数据。
	//private static JSONArray jsStationArray;
	private static JSONArray jsCollectorArray;
	private static JSONArray jsSensorArray;
	//private static AlertInfo[] alerArray;
	private static long mCollectorid;
	private List<AlertInfo> alertlist=new ArrayList<AlertInfo>();
	public static List<AlertInfo> alerArray=new ArrayList<AlertInfo>();
	public static String alertstationname;
	public static String alertsensorname;
	public static String alerttime;
	public static String alertvalue;
	public static String alertinfo;
	public static String str_info;

	@SuppressLint("HandlerLeak") public ClientService() {
		super();
		// TODO Auto-generated constructor stub
	}
	Handler mHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  switch(msg.what){
			  case Declare.STATUS_SUCCESS:
				  Declare.islogin=true;
				  //Toast.makeText(ClientService.this, "登录成功", Toast.LENGTH_SHORT).show();
				  DataId=0;
				  getstation();
				  break;
			  case 1:
                  Declare.islogin=false;
				  String info = (String) msg.obj;
				  Toast.makeText(ClientService.this, info,Toast.LENGTH_SHORT).show();
                  break;
              case 2://登录失败
            	  Declare.islogin=false;
            	  info = (String) msg.obj;
                  Toast.makeText(ClientService.this, info,Toast.LENGTH_SHORT).show();
                  break;
              case 3://无可用的网络连接
            	  info = (String) msg.obj;
                  FragmentIndex.comfir1_display();
            	  //Toast.makeText(ClientService.this, info,3000).show();
                  break;
              case 10:
            	  //new Alert_thread().start();
            	  if(Declare.islogin){
            		  //sendbeat();
            		  GetRealsByDataId();
            	  }else{
            		  if ((Declare.isautologin)&&(!Declare.username.equals(""))&&(!Declare.password.equals(""))){
            			  login();
            		  }
            	  }
            	  break;
              case 11:
            	  Declare.islogin=true;
            	  //Toast.makeText(ClientService.this, "登录成功", 3000).show();
            	  break;
              case 800:
            	  //FragmentPicture.alert_display(alertstationname,alertsensorname, alerttime, alertvalue, alertinfo);
            	  //Intent intent1=new Intent(ClientService.this,fudongService.class);
            	  //startService(intent1);
            	  break;
			  }
		  }

	};
	
	@SuppressLint("HandlerLeak") Handler handler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {								
			switch(msg.what){
			case 1:
				String info=(String) msg.obj;
				Toast.makeText(ClientService.this, info,Toast.LENGTH_SHORT).show();
				break;
			}  
		  }
	};
	/*public static class IncomingHandler extends Handler {  
	    private final WeakReference<ClientService> mService;  
	   
	    IncomingHandler(ClientService service) {  
	        mService = new WeakReference<ClientService>(service);  
	    }  
	    @Override  
	    public void handleMessage(Message msg)  
	    {  
	    	ClientService service = mService.get();
	         //if (service != null) {  
	            MainActivity.handler.handleMessage(msg);  
	         //}  
	    }  
	}*/

	@SuppressLint("HandlerLeak") @Override
	public int onStartCommand(Intent intent,int flags,int startid){
        

		new sleep_thread().start();
		IntentFilter mFilter = new IntentFilter();  
	    mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);  
	    registerReceiver(myNetReceiver, mFilter);
		//给json数组和对象赋值，用于调试，正式运行应去掉。
		/*for(int i=0;i<20;i++){
			stationdata=new JSONObject();
			sensordata=new JSONObject();
			tempdata=new JSONObject();
			try {
				stationdata.put("StationID", i);
				stationdata.put("StationName","zhan"+i);
				sensordata.put("StationID", i);
				sensordata.put("StationName", "zhan"+i);
				sensordata.put("SensorID", i+1);
				sensordata.put("SensorName", "dian"+(i+1));
				sensordata.put("SensorNum", i);
				sensordata.put("VoiceID", i);
				tempdata.put("StationID", i);
				tempdata.put("StationName", "zhan"+i);
				tempdata.put("SensorID", i+1);
				tempdata.put("SensorName", "dian"+(i+1));
				tempdata.put("DataID", i);
				tempdata.put("TValue", 32+i/10);
				tempdata.put("CollectTime", "2019-01-08 12:23:45");
				jsonArray_st.put(i, stationdata);
				jsonArray_ss.put(i,sensordata);
				jsonArray_md.put(i,tempdata);
				stationdata=null;
				sensordata=null;
				tempdata=null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		if(Networkutils.isNetworkConnected(this)){
			Declare.isnet=true;
			if ((Declare.isautologin)&&(!Declare.username.equals(""))&&(!Declare.password.equals(""))){
				//login();
			}else{
				Message msg = new Message();
		        msg.what = 2;
		        msg.obj="用户未登录";
		        mHandler.sendMessage(msg);
			}
		}else{
			Declare.isnet=false;
			Message msg = new Message();
	        msg.what = 3;
	        msg.obj="用户未登录，网络连接不可用";
	        mHandler.sendMessage(msg);
		}
		mhandler = new Handler()
		{										
			public void handleMessage(Message msg)										
			{								
				switch(msg.what){
				case Declare.STATUS_SUCCESS:
					Declare.islogin=true;
					Toast.makeText(ClientService.this, "命令请求成功", Toast.LENGTH_SHORT).show();
					getSensors(stArray[0].getStationID());
					break;
				case Declare.STATUS_ERROR:
					Declare.islogin=false;
					String info=(String) msg.obj;
					Toast.makeText(ClientService.this, info,Toast.LENGTH_SHORT).show();
					break;
				case 10:
					Toast.makeText(ClientService.this, "站点数据为空", Toast.LENGTH_SHORT).show();
					break;
				case 20:
					//Toast.makeText(ClientService.this, "获取首站测量点信息成功", Toast.LENGTH_SHORT).show();
					break;
				case 21:
					Toast.makeText(ClientService.this, "指定站点的测量点信息为空", Toast.LENGTH_SHORT).show();
					break;
				case 80://站点信息提取
					Declare.isloadstations=true;
					//zhanArray=new String[1];
					//zhanArray[0]="110kV站4";
					//if(ClientService.zhanArray!=null){
			        //	zhanAdapter=new ArrayAdapter<String>(ClientService.this,R.layout.item_select,ClientService.zhanArray);
			        //}else{
			        //	zhanAdapter=new ArrayAdapter<String>(ClientService.this,R.layout.item_select,zhanArray);
			        //}
					Toast.makeText(ClientService.this, "站点请求成功", Toast.LENGTH_SHORT).show();
					if(stArray!=null){
						getSensors(stArray[0].getStationID());
					}
					break;
				case 81:
					Declare.isloadstations=false;
					info=(String) msg.obj;
					Toast.makeText(ClientService.this, info,Toast.LENGTH_SHORT).show();
					break;
				case Declare.STATUS_ALERT://实时数据有告警
					new Alert_thread().start();
					if(MainActivity.mSelectedItemIndex==7){
						Message msg_2 = new Message();
			        	msg.what = 0;//read_od
			        	Fragment_Realwarning.mhandler.sendMessage(msg_2);
   	             	}
					
	            	break;
				case 201://实时数据提取为空时
					
					break;
				}  
			  }
		};
		
		
		return START_REDELIVER_INTENT;

	}
	
	

	/*@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		//启动超时计数线程
		
	}*/
private void login(){
	new Thread(new Runnable() {
	@Override
	public void run() {
		try {
			Declare.order="用户登录";
			ReturnData iis = ClientService.sendpost(Declare.LOGIN+"?name="+URLEncoder.encode(Declare.username,"UTF-8")+"&pass="+URLEncoder.encode(Declare.password,"UTF-8"));
			if(iis.mark.equals("ok")){
	        	 // 使用BufferedReader对象读取返回的数据流
			     // 按行读取，存储在StringBuider对象response中
			     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
			     StringBuilder response = new StringBuilder();
			     String line;
			     JSONObject result = null;
			     //JSONArray jsArray=null;
			     while ((line = reader.readLine()) != null) {
			    	 response.append(line);
			     }	 
			     try {
			    		 result = new JSONObject( response.toString());
			     } catch (JSONException e) {
			    			// TODO 自动生成的 catch 块
			    		 e.printStackTrace();
			     }
			      String token="";
			      String errorcode=result.getString("Error");
			      if(result.isNull("Error")){
			    	  result=result.getJSONObject("Result");
			    	  try {
				    	  token = result.getString("Token");
				    	  Declare.isnet=true;
				    	  Declare.islogin=true;
				    	  Message msg = new Message();
			              msg.what = Declare.STATUS_SUCCESS;
			              mHandler.sendMessage(msg);
				      } catch (JSONException e) {
								// TODO 自动生成的 catch 块
				    	  e.printStackTrace();
				      }  
				      if(token!=null){
				    	  Declare._token=token;
				      }
				      //
			      }else{
			    	  Message msg = new Message();
		              msg.what = Declare.STATUS_ERROR;
		              msg.obj=errorcode;
		              mHandler.sendMessage(msg);
			      }
        	 }else if(iis.mark.equals("outtime")){
        		  Message msg = new Message();
	              msg.what = Declare.STATUS_ERROR;
	              msg.obj="请求超时，请确认服务器地址！";
	              mHandler.sendMessage(msg);
        	 }else if(iis.mark.equals("error")){
        		 Message msg = new Message();
	              msg.what = Declare.STATUS_ERROR;
	              msg.obj="连接错误";
	              mHandler.sendMessage(msg);
        	 }else{
		    	  Message msg = new Message();
	              msg.what = Declare.STATUS_ERROR;
	              msg.obj=iis.mark;
	              mHandler.sendMessage(msg);
		      }
        } catch (Exception e){
             e.printStackTrace();
        }// finally {}
	     }
	}).start();
	}
private void sendbeat() {
	// TODO Auto-generated method stub
	new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				ReturnData iis = ClientService.sendpost("");
				if(iis.mark.equals("ok")){
		        	 // 使用BufferedReader对象读取返回的数据流
				     // 按行读取，存储在StringBuider对象response中
				     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
				     StringBuilder response = new StringBuilder();
				     String line;
				     JSONObject result = null;
				     //JSONArray jsArray=null;
				     while ((line = reader.readLine()) != null) {
				    	 response.append(line);
				     }	 
				     try {
				    		 result = new JSONObject( response.toString());
				     } catch (JSONException e) {
				    			// TODO 自动生成的 catch 块
				    		 e.printStackTrace();
				     }
				      //String token="";
				      String errorcode=result.getString("Error");
				      if(result.isNull("Error")){
				    	  Declare.islogin=true;
				    	  Message msg = new Message();
			              msg.what = 11;
			              mHandler.sendMessage(msg);
				      }else{
				    	  Message msg = new Message();
			              msg.what = Declare.STATUS_ERROR;
			              msg.obj=errorcode;
			              mHandler.sendMessage(msg);
				      }
	        	 }else if(iis.mark.equals("outtime")){
	        		  Message msg = new Message();
		              msg.what = Declare.STATUS_ERROR;
		              msg.obj="连接超时";
		              mHandler.sendMessage(msg);
	        	 }else if(iis.mark.equals("error")){
	        		 Message msg = new Message();
		              msg.what = Declare.STATUS_ERROR;
		              msg.obj="连接错误";
		              mHandler.sendMessage(msg);
	        	 }else{
			    	  Message msg = new Message();
		              msg.what = Declare.STATUS_ERROR;
		              msg.obj=iis.mark;
		              mHandler.sendMessage(msg);
			      }
	        } catch (Exception e){
	             e.printStackTrace();
	        }// finally {}
		     }
		}).start();
}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void DisplayToast(String msg){
		Toasts.toast(this, msg, Toast.LENGTH_SHORT);//,Gravity.TOP, 0, 1020
	}
	public void DisplayToast1(String msg){
		Toasts.toast(this, msg);
	}
	public static void DisplayToast2(String msg){
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//20160303		Toasts.toast(MainApplication.getContext(), msg, Toast.LENGTH_SHORT,Gravity.TOP, 0, 520);
	}	
	public void DisplayToast3(String msg){
		Toasts.toast1(this, msg);
	}
/*	public void onStart(Intent intent, int startID)
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
		Gson gson = new Gson(); 
		Student student = new Student();  
	    student.id = 1;  
	    student.nickName = "乔晓松";  
	    student.age = 22;  
	    student.email = "965266509@qq.com";  
	    System.out.print( gson.toJson(student));
	    String url_Http = "http://192.168.10.67:80/_main/Login?name=admin&pass=admin";
	    HttpPost request = new HttpPost(url_Http);
		JSONObject param = new JSONObject();
		try {
			URL my_url = new URL(url_Http);
			request = new HttpPost(url_Http);
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		try {
			StringEntity se = new StringEntity(gson.toString(),"utf-8");//防止乱码
			request.setEntity(se);
			} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
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

		
	}*/
	
	public static ReturnData sendpost( String request) {
	    final String mrequest=request;
        HttpURLConnection connection = null;
        //DataOutputStream out=null;
        ReturnData returndata = new ReturnData();
        //for(int i=0;i<1;i++){
        try {
            // 调用URL对象的openConnection方法获取HttpURLConnection的实例
            URL url = new URL(Declare.home_url+mrequest);
             connection = (HttpURLConnection) url.openConnection();
            // 设置请求方式，GET或POST
            //connection.setRequestMethod("GET");
            // 设置连接超时、读取超时的时间，单位为毫秒（ms）
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(6000);
            connection.setRequestProperty("_Token",Declare._token);
            //Post 方法
            //connection.setDoOutput(true);
            //connection.setDoInput(true);
			//设置以POST方式
            connection.setRequestMethod("GET");
			//POST请求不能使用缓存
            //connection.setUseCaches(false);
            //connection.setInstanceFollowRedirects(true);
			
			//配置本次连接的Content_type,配置为application/x-www-form-urlencoded
            connection.setRequestProperty("Content-Type", "application/json");
			//连接，从postUrl.OpenConnection()至此的配置必须要在connect之前完成。
/*			//要注意的是connection.getOutputStream会隐含地进行connect.
//********************************Post方式不同的地方*************************************/
            
            connection.connect();
			//DataOutputStream流。
			//out = new DataOutputStream(connection.getOutputStream());
			//要上传的参数
//			String content = URLEncoder.encode("name=admin&pass=admin","utf-8");
			//将要上传的内容写入流中
//			out.writeBytes("123");
			//刷新、关闭
//			out.flush();
//			out.close();
			//InputStream in = null;
			//String responseHeader=null;
			int responseCode = connection.getResponseCode();
			if(responseCode==HttpURLConnection.HTTP_OK){
			// getInputStream方法获取服务器返回的输入流
				returndata.mark="ok";
				returndata.is = connection.getInputStream();
				//responseHeader = getResponseHeader(connection);
				//break;
			}else if(responseCode==HttpURLConnection.HTTP_UNAUTHORIZED){
				returndata.is=null;
				returndata.mark="未授权";
				//break;
			}else{
				/*if(i>=2){
					returndata.is=null;
					returndata.mark="请求失败";
					break;
				}else{
					Thread.sleep(5000);
				}*/
			}
        } catch (Exception e){
            /*if(i>=2){
	        	e.printStackTrace();
	            returndata.is=null;
	            returndata.mark="outtime";//String.valueOf(e.getMessage());
	            break;
            }else{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}*/
        } finally {
            //if (connection != null){
                // 结束后，关闭连接
              //  connection.disconnect();
            	
            //}
        }
        //}
	    return returndata;   
	}
	
	//读取请求头
    @SuppressWarnings("unused")
	private String getReqeustHeader(HttpURLConnection conn) {
        //https://github.com/square/okhttp/blob/master/okhttp-urlconnection/src/main/java/okhttp3/internal/huc/HttpURLConnectionImpl.java#L236
        Map<String, List<String>> requestHeaderMap = conn.getRequestProperties();
        Iterator<String> requestHeaderIterator = requestHeaderMap.keySet().iterator();
        StringBuilder sbRequestHeader = new StringBuilder();
        while (requestHeaderIterator.hasNext()) {
            String requestHeaderKey = requestHeaderIterator.next();
            String requestHeaderValue = conn.getRequestProperty(requestHeaderKey);
            sbRequestHeader.append(requestHeaderKey);
            sbRequestHeader.append(":");
            sbRequestHeader.append(requestHeaderValue);
            sbRequestHeader.append("\n");
        }
        return sbRequestHeader.toString();
    }

    //读取响应头
    @SuppressWarnings("unused")
	private static String getResponseHeader(HttpURLConnection conn) {
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        StringBuilder sbResponseHeader = new StringBuilder();
        for(int i = 0; i < size; i++){
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            String responseHeaderValue = conn.getHeaderField(i);
            sbResponseHeader.append(responseHeaderKey);
            sbResponseHeader.append(":");
            sbResponseHeader.append(responseHeaderValue);
            sbResponseHeader.append("\n");
        }
        return sbResponseHeader.toString();
    }
    
  //将表示xml的字节数组进行解析
    @SuppressWarnings("unused")
	private String parseXmlResultByBytes(byte[] bytes) {
       /* InputStream is = new ByteArrayInputStream(bytes);
        StringBuilder sb = new StringBuilder();
        List<Person> persons = XmlParser.parse(is);
        for (Person person : persons) {
            sb.append(person.toString()).append("\n");
        }*/
        return null;//sb.toString();
    }

    //将表示json的字节数组进行解析
    @SuppressWarnings("unused")
	private String parseJsonResultByBytes(byte[] bytes){
       /* String jsonString = getStringByBytes(bytes);
        List<Person> persons = JsonParser.parse(jsonString);
        StringBuilder sb = new StringBuilder();
        for (Person person : persons) {
            sb.append(person.toString()).append("\n");
        }*/
        return null;// sb.toString();
    }
    //获取指定站点的标签信息列表
    public static void getSensors(final long stationid){
		new Thread(new Runnable() {
	         @Override
	         public void run() {
	        	 try {
	        		 Declare.order="获取测量点信息";//GetSensorsByStation
	        		 //ReturnData iis = ClientService.sendpost("GetSensors?stationId="+stationid);
	        		 ReturnData iis = ClientService.sendpost("GetSensorsByStation?stationId="+stationid);
	        		 if(iis.mark.equals("ok")){
			        	 // 使用BufferedReader对象读取返回的数据流
					     // 按行读取，存储在StringBuider对象response中
					     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
					     StringBuilder response = new StringBuilder();
					     String line;
					     JSONObject result = null;
					     JSONArray jsArray=null;
					     while ((line = reader.readLine()) != null) {
					         response.append(line);
					     }
					     try {
					    		 result = new JSONObject( response.toString());
					    	 } catch (JSONException e) {
					    			// TODO 自动生成的 catch 块
					    		 e.printStackTrace();
					    	 }
					     
					     String errorcode=result.getString("Error");
					     if(result.isNull("Error")){
					    	 result=result.getJSONObject("Result");
					    	 if(result.isNull("Stations")){
					    		 Message msg = new Message();
					             msg.what = 21;
					             switch(MainActivity.mSelectedItemIndex){
					             case 3:
					            	 FragmentHistraydata.mHandler.sendMessage(msg);
					            	 break;
					             case 4:
					            	 FragmentChar.mHandler.sendMessage(msg);
					            	 break;
					             case 5:
					            	 FragmentAlert.mhandler.sendMessage(msg);
					            	 break;
					             case 1:
					            	 
					            	 break;
					             default :
					            	 mhandler.sendMessage(msg);
					             }
					             return;
					    	 }
					    	 JSONArray jaStation=result.getJSONArray("Stations");
					    	 ((JSONObject) jaStation.get(0)).getInt("Id");
					    	 //JSONArray jaCollector=result.getJSONArray("Collectors");
					    	 if(result.isNull("Sensors")){
					    		 Message msg = new Message();
					             msg.what = 21;
					             switch(MainActivity.mSelectedItemIndex){
					             case 3:
					            	 FragmentHistraydata.mHandler.sendMessage(msg);
					            	 break;
					             case 4:
					            	 FragmentChar.mHandler.sendMessage(msg);
					            	 break;
					             case 5:
					            	 FragmentAlert.mhandler.sendMessage(msg);
					            	 break;
					             case 1:
					            	 
					            	 break;
					             default :
					            	 mhandler.sendMessage(msg);
					             }
					             return;
					    	 }
					    	 jsArray=result.getJSONArray("Sensors");
					    	 if(jsArray.length()>0){//正常时为jsArray，jsonArray_ss用于调试
						    	 ssArray=null;cldArray=null;
						    	 ssArray=new SensorsInfo[jsArray.length()];
						    	 cldArray=new String[jsArray.length()];
					    		 for(int i=0;i<jsArray.length();i++){
						    		 SensorsInfo ssi=new SensorsInfo();
					    			 result=(JSONObject) jsArray.get(i);
						    		 ssi.setStationID(((JSONObject) jaStation.get(0)).getInt("Id"));
						    		 ssi.setStationName(((JSONObject) jaStation.get(0)).getString("Name"));
						    		 ssi.setSensorID(result.getInt("Id"));
						    		 ssi.setSensorName(result.getString("Name"));
						    		 ssi.setSensorNum(result.getInt("Addr"));
						    		 //ssi.setVoiceID(result.getInt("VoiceID"));
					    			 ssArray[i]=ssi;//(SensorsInfo) jsonArray_ss.get(i);
						    		 cldArray[i]=ssArray[i].getSensorName();//result.getString("StationName");
						    		 ssi=null;
					    		 }
						    	 Message msg = new Message();
					             msg.what = 20;
					             switch(MainActivity.mSelectedItemIndex){
					             case 3:
					            	 FragmentHistraydata.mHandler.sendMessage(msg);
					            	 break;
					             case 4:
					            	 FragmentChar.mHandler.sendMessage(msg);
					            	 break;
					             case 5:
					            	 FragmentAlert.mhandler.sendMessage(msg);
					            	 break;
					             case 1:
					            	 
					            	 break;
					             default :
					            	 mhandler.sendMessage(msg);
					             }
					    	 }else{
					    		 Message msg = new Message();
					             msg.what = 21;
					             switch(MainActivity.mSelectedItemIndex){
					             case 3:
					            	 FragmentHistraydata.mHandler.sendMessage(msg);
					            	 break;
					             case 4:
					            	 FragmentChar.mHandler.sendMessage(msg);
					            	 break;
					             case 5:
					            	 FragmentAlert.mhandler.sendMessage(msg);
					            	 break;
					             case 1:
					            	 
					            	 break;
					             default :
					            	 mhandler.sendMessage(msg);
					             }

					    	 }
					     }else{
					    	 Message msg = new Message();
				             msg.what = Declare.STATUS_ERROR;
				             msg.obj=errorcode;
				             mhandler.sendMessage(msg);
					     }
		        	 }else if(iis.mark.equals("outtime")){
		        		 Message msg = new Message();
			             msg.what = Declare.STATUS_ERROR;
			             msg.obj="连接超时";
			             mhandler.sendMessage(msg);
		        	 }else if(iis.mark.equals("error")){
		        		 Message msg = new Message();
			             msg.what = Declare.STATUS_ERROR;
			             msg.obj="连接错误";
			             mhandler.sendMessage(msg);
		        	 }else if(iis.mark.equals("未授权")){
		        		 Message msg = new Message();
			             msg.what = Declare.STATUS_ERROR;
			             msg.obj="操作失败，权限未认证";
			             mhandler.sendMessage(msg);
		        	 }else{
		        	    	Message msg = new Message();
		                    msg.what = Declare.STATUS_ERROR;
		                    msg.obj=iis.mark;
		                    FragmentHistraydata.mHandler.sendMessage(msg);
		        	     }
			  //..........
			  // 此处省略处理数据的代码
			  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
	        	 } catch (Exception e){
	        		 e.printStackTrace();
	        	 }// finally {}
	         }
		}).start();
	}
    
    //获取站点信息列表
    public static void getstation() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
         @Override
         public void run() {
        	 try{
        		 Declare.order="获取站信息";
        		 iis = ClientService.sendpost("GetStations");
        	 if(iis.mark.equals("ok")){
               	 // 使用BufferedReader对象读取返回的数据流
    		     // 按行读取，存储在StringBuider对象response中
    		     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
    		     StringBuilder response = new StringBuilder();
    		     String line;
    		     JSONObject result = null;
    		     JSONArray jsArray=null;
    		     while ((line = reader.readLine()) != null) {
    		    	 response.append(line);
			     }	 
			     try {
			    		 result = new JSONObject( response.toString());
			     } catch (JSONException e) {
			    			// TODO 自动生成的 catch 块
			    		 e.printStackTrace();
			     }
    		     String errorcode=result.getString("Error");
    		     if(result.isNull("Error")){
    		    	 result=result.getJSONObject("Result");
    		    	 if(result.isNull("Stations")){
    		    		 Declare.isloadstations=false;
    		    		 zhanArray=null;
    		    		 stArray=null;
    		    		 Message msg=new Message();
    		    		 msg.what=10;
    		    		 mhandler.sendMessage(msg);
    		    	 }
    		    	 jsArray=result.getJSONArray("Stations");
    		    	 if(jsArray.length()>0){//jsonArray_st用于调试，正常时为jsArray;
	    		    	 stArray=null;zhanArray=null;
    		    		 stArray=new StationInfo[jsArray.length()];
	    		    	 zhanArray=new String[jsArray.length()];
    		    		 
	    		    	 for(int i=0;i<jsArray.length();i++){
	    		    		 
	    		    		 StationInfo si=new StationInfo();
	    		    		 result=(JSONObject) jsArray.getJSONObject(i);
	    		    		 si.setStationID(result.getInt("Id"));
	    		    		 si.setStationName(result.getString("Name"));
	    		    		 stArray[i]=si;
	    		    		 zhanArray[i]=stArray[i].getStationName();//result.getString("StationName");
	    		    		 
	    		    		 si=null;
	    		    	 }
	    		    	 //Declare.zhan=zhanArray[0];
	    		    	 Message msg = new Message();
	    	             msg.what = 80;
	    	             //mhandler.sendMessage(msg);
	    	             Declare.isloadstations=true;
	    	             switch(MainActivity.mSelectedItemIndex){
			             case 3:
			            	 FragmentHistraydata.mHandler.sendMessage(msg);
			            	 break;
			             case 2:
			            	 FragmentRealdata.mHandler.sendMessage(msg);
			            	 break;
			             /*case 1:
			            	 FragmentPicture.mHandler.sendMessage(msg);
			            	 break;*/
			             case 5:
			            	 FragmentAlert.mhandler.sendMessage(msg);
			            	 break;
			             case 6:
			            	 FragmentDailyrecord.mHandler_sms.sendMessage(msg);
			            	 break;
			             default :
			            	 mhandler.sendMessage(msg);
			             }
    		    	 }else{
    		    		 Declare.isloadstations=false;
    		    		 zhanArray=null;
    		    		 stArray=null;
    		    		 Message msg=new Message();
    		    		 msg.what=10;
    		    		 mhandler.sendMessage(msg);
    		    	 }
    		     }else{
    		    	 Message msg = new Message();
    	             msg.what = 81;
    	             msg.obj=errorcode;
    	             mhandler.sendMessage(msg);
    		     }
           	 }else if(iis.mark.equals("outtime")){
           		 Message msg = new Message();
                    msg.what = 81;
                    msg.obj="连接超时";
                    mhandler.sendMessage(msg);
           	 }else if(iis.mark.equals("error")){
           		 Message msg = new Message();
                    msg.what = 81;
                    msg.obj="连接错误";
                    mhandler.sendMessage(msg);
           	 }else if(iis.mark.equals("未授权")){
           		 Message msg = new Message();
                    msg.what = 81;
                    msg.obj="操作失败，权限未认证";
                    mhandler.sendMessage(msg);
           	 }else{
        	    	Message msg = new Message();
                    msg.what = 81;
                    msg.obj=iis.mark;
                    mhandler.sendMessage(msg);
        	     }
        	  //..........
        	  // 此处省略处理数据的代码
        	  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
         	} catch (Exception e){
                e.printStackTrace();
           }

         }
	}).start();
	}
    
    
    
  //获取指定标签的历史数据：
    public static void getHistories(final long sensorsid,final String datetime,final String endtime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
         @Override
         public void run() {
        	 try{
        		 Declare.order="获取历史数据";
        		 iis = ClientService.sendpost("GetHistoriesBySensor?sensorId="+sensorsid+"&from="+URLEncoder.encode(datetime,"UTF-8")+"&to="+URLEncoder.encode(endtime,"UTF-8"));
        	 if(iis.mark.equals("ok")){
               	 // 使用BufferedReader对象读取返回的数据流
    		     // 按行读取，存储在StringBuider对象response中
    		     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
    		     StringBuilder response = new StringBuilder();
    		     String line;
    		     JSONObject result = null;
    		     JSONArray jsArray=null;
    		     while ((line = reader.readLine()) != null) {
    		    	 response.append(line);
			     }	 
			     try {
			    		 result = new JSONObject( response.toString());
			     } catch (JSONException e) {
			    			// TODO 自动生成的 catch 块
			    		 e.printStackTrace();
			     }
    		     String errorcode=result.getString("Error");
    		     JSONObject joSensors=new JSONObject();
    		     JSONArray jaStations=new JSONArray(),jaSensors=new JSONArray();
    		     if(result.isNull("Error")){
    		    	 result=result.getJSONObject("Result");
    		    	 if(result.isNull("Sensors")){
    		    		 mdArray=new MeasureData[0];
    		    		 Message msg = new Message();
			             msg.what = 10;
			             msg.obj="没有符合条件的数据";
			             FragmentHistraydata.mHandler.sendMessage(msg);
			             return;
    		    	 }
    		    	 joSensors=result.getJSONObject("Sensors");
    		    	 if((joSensors.isNull("Stations"))||(joSensors.isNull("Sensors"))){
    		    		 mdArray=new MeasureData[0];
    		    		 Message msg = new Message();
			             msg.what = 10;
			             msg.obj="没有符合条件的数据";
			             FragmentHistraydata.mHandler.sendMessage(msg);
			             return;
    		    	 }
    		    	 jaStations=joSensors.getJSONArray("Stations");
    		    	 jaSensors=joSensors.getJSONArray("Sensors");
    		    	 if(result.isNull("Datas")){
    		    		 mdArray=new MeasureData[0];
    		    		 Message msg = new Message();
			             msg.what = 10;
			             msg.obj="没有符合条件的数据";
			             FragmentHistraydata.mHandler.sendMessage(msg);
			             return;
    		    	 }
    		    	 JSONObject joDatas=result.getJSONObject("Datas");
    		    	 //jsArray=joDatas.getJSONArray("Tmp");
    		    	 if(joDatas.length()!=0){
    		    		 jsArray=joDatas.getJSONArray("Tmp");
    		    	 }else{
    		    		 mdArray=new MeasureData[0];
    		    		 Message msg = new Message();
			             msg.what = 10;
			             msg.obj="没有符合条件的数据";
			             FragmentHistraydata.mHandler.sendMessage(msg);
			             return;
    		    	 }
    		    	 mdArray=new MeasureData[jsArray.length()];
    		    	 if(jsArray.length()>0){
	    		    	 for(int i=0;i<jsArray.length();i++){
	    		    		 result=(JSONObject) jsArray.get(i);
	    		    		 MeasureData md=new MeasureData();
	    		    		 md.setStationID(((JSONObject) (jaStations.get(0))).getLong("Id"));
	    		    		 md.setStationName(((JSONObject) (jaStations.get(0))).getString("Name"));
	    		    		 md.setSensorId(result.getLong("SensorId"));
	    		    		 for(int j=0;j<jaSensors.length();j++){
	    		    			 if((((JSONObject) (jaSensors.get(j))).getLong("Id"))==md.getSensorId()){
	    		    				 md.setSensorsName(((JSONObject) (jaSensors.get(j))).getString("Name"));
	    		    				 break;
	    		    			 }
	    		    		 }
	    		    		 
	    		    		 md.setDataID(result.getLong("Id"));
	    		    		 if(!result.getString("Value").equals("null")){
	    		    		 md.setValue(result.getDouble("Value"));}else{md.setValue(0.0);}
	    		    		 md.setTime(result.getString("Time"));
	    		    		 md.setType(result.getString("Type"));
	    		    		 
	    		    		 mdArray[i]=md;
	    		    		 md=null;
	    		    		 //mdArray[i]=(MeasureData) jsArray.get(i);
	    		    	 }
	    		    	 Message msg = new Message();
	    	             msg.what = 10;
	    	             FragmentHistraydata.mHandler.sendMessage(msg);
    		    	 }else{
    		    		 mdArray=null;
    		    		 Message msg=new Message();
    		    		 msg.obj="请求数据为空";
    		    		 msg.what=11;
    		    		 FragmentHistraydata.mHandler.sendMessage(msg);
    		    	 }
    		     }else{
    		    	 Message msg = new Message();
    	             msg.what = Declare.STATUS_ERROR;
    	             msg.obj=errorcode;
    	             FragmentHistraydata.mHandler.sendMessage(msg);
    		     }
           	 }else if(iis.mark.equals("outtime")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接超时";
                    FragmentHistraydata.mHandler.sendMessage(msg);
           	 }else if(iis.mark.equals("error")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接错误";
                    FragmentHistraydata.mHandler.sendMessage(msg);
           	 }else if(iis.mark.equals("未授权")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="操作失败，权限未认证";
                    FragmentHistraydata.mHandler.sendMessage(msg);
           	 }else{
        	    	Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj=iis.mark;
                    FragmentHistraydata.mHandler.sendMessage(msg);
        	     }
        	  //..........
        	  // 此处省略处理数据的代码
        	  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
         	} catch (Exception e){
         		Message msg = new Message();
                msg.what =11;// Declare.STATUS_ERROR;
                msg.obj=iis.mark;
                FragmentHistraydata.mHandler.sendMessage(msg);
                e.printStackTrace();
           }

         }
	}).start();
	}
    
	//获取实时数据：
    public static void GetRealsByDataId() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
         @Override
         public void run() {
        	 try{
        		 //ReturnData iis = ClientService.sendpost("GetReals?stationId="+sensorsid);//GetRealsByStation
        		 ReturnData iis = ClientService.sendpost("GetRealsByDataId?dataId="+String.valueOf(DataId));
        	 if(iis.mark.equals("ok")){
               	 // 使用BufferedReader对象读取返回的数据流
    		     // 按行读取，存储在StringBuider对象response中
    		     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
    		     StringBuilder response = new StringBuilder();
    		     String line;
    		     JSONObject result = null;
    		     JSONArray jsArray=null;
    		     while ((line = reader.readLine()) != null) {
    		    	 response.append(line);
			     }	 
			     try {
			    		 result = new JSONObject( response.toString());
			     } catch (JSONException e) {
			    			// TODO 自动生成的 catch 块
			    		 e.printStackTrace();
			     }
    		     String errorcode=result.getString("Error");
    		     if(result.isNull("Error")){
    		    	 result=result.getJSONObject("Result");
    		    	 if(result.isNull("Sensors")){
    		    		 Message msg=new Message();
    		    		 msg.what=201;
    		    		 msg.obj="数据为空";
    		    		 mhandler.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 JSONObject joSensors=result.getJSONObject("Sensors");
    		    	 if(joSensors.isNull("Stations")){
    		    		 Message msg=new Message();
    		    		 msg.what=201;
    		    		 msg.obj="数据为空";
    		    		 mhandler.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 //jsStationArray=joSensors.getJSONArray("Stations");
    		    	 if(joSensors.isNull("Collectors")){
    		    		 Message msg=new Message();
    		    		 msg.what=201;
    		    		 msg.obj="数据为空";
    		    		 mhandler.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 jsCollectorArray=joSensors.getJSONArray("Collectors");
    		    	 if(joSensors.isNull("Sensors")){
    		    		 Message msg=new Message();
    		    		 msg.what=201;
    		    		 msg.obj="数据为空";
    		    		 mhandler.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 jsSensorArray=joSensors.getJSONArray("Sensors");
    		    	 
    		    	 JSONObject joDatas=result.getJSONObject("Datas");
    		    	 if(joDatas.length()==0){
    		    		 Message msg=new Message();
    		    		 msg.what=201;
    		    		 msg.obj="数据为空";
    		    		 mhandler.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 //遍历接收的数据所包含的项目
    		    	 /*if(joDatas.names().length()>0){
    		    		 for(int i=0;i<joDatas.names().length();i++){
    		    			 String name=joDatas.names().get(i).toString();
    		    			 System.out.println(name);
    		    		 }
    		    	 };
    		    	 Iterator keys = joDatas.keys();
    		    	 while(keys.hasNext()){
    		    	    String key = (String) keys.next();
    		    	    System.out.println(key);
    		    	 } */
    		    	 //获取最大的数据编号，为下次提取最新数据
    		    	 String str_binding="";
    		    	 if(joDatas.has("Tmp")){
    		    		 JSONArray temp=joDatas.getJSONArray("Tmp");
    		    		 //如果是实时状态页面，则更新图形对应的标签数值，否则抛弃；
    		    		 if(MainActivity.mSelectedItemIndex==1){
    		    			 for(int k=0;k<temp.length();k++){
    		    				 long sensorid=((JSONObject)temp.get(k)).getLong("SensorId");
    		    				 for(int j=0;j<jsSensorArray.length();j++){
		    		    			 if((((JSONObject) (jsSensorArray.get(j))).getLong("Id"))==sensorid){
		    		    				 str_binding=String.valueOf(((JSONObject)jsSensorArray.get(j)).getLong("Addr"));
		    		    				 mCollectorid=((JSONObject) (jsSensorArray.get(j))).getLong("CollectorId");//获取标签的采集器ID，从而获得站点的信息
		    		    				 for(int i=0;i<jsCollectorArray.length();i++){
		    		    					 if(((JSONObject)jsCollectorArray.get(i)).getLong("Id")==mCollectorid){
		    		    						 str_binding=String.valueOf(((JSONObject)jsCollectorArray.get(i)).getLong("Channel"))+'_'+str_binding;
		    		    						 break;
		    		    					 }
		    		    				 }
		    		    				 break;
		    		    			 }
		    		    		 }
    		    				 for(int l=0;l<FragmentPicture.drawslist.size();l++){
    		    					 if(FragmentPicture.drawslist.get(l).getBinding().equals(str_binding)){
    		    						 if(FragmentPicture.drawslist.get(l).getType().equals("Title")||FragmentPicture.drawslist.get(l).getType().equals("Monitor")){
    		    							 FragmentPicture.drawslist.get(l).setText(String.valueOf(((JSONObject)temp.get(k)).getDouble("Value")));
    		    						 }
    		    						 break;
    		    					 }
    		    				 }
    		    			 }
    		    		 }
    		    		 JSONObject jo=(JSONObject) temp.get(temp.length()-1);
    		    		 if(DataId<jo.getLong("Id"))
    		    			 DataId=jo.getLong("Id"); 
    		    	 }
    		    	 if(joDatas.has("Vol")){
    		    		 JSONArray temp=joDatas.getJSONArray("Vol");
    		    		 JSONObject jo=(JSONObject) temp.get(temp.length()-1);
    		    		 //获取最后的实时数据的编号，其他值暂时不用。
    		    		 if(DataId<jo.getLong("Id")){
    		    			 DataId=jo.getLong("Id");
    		    		 }; 
    		    	}
    		    	 
    		    	 if(joDatas.has("Err")){//判断是否有告警数据，有取出告警数据
    		    		 jsArray=joDatas.getJSONArray("Err");
	    		    	 if(jsArray.length()>0){//调试用jsonArray_md,正式时使用jsArray;
	    		    		 alerArray.clear();
	    		    		 for(int i=0;i<jsArray.length();i++){
		    		    		AlertInfo ad=new AlertInfo();
	    		    			result=(JSONObject) jsArray.get(i);
	    		    			//ad.setStationId(((JSONObject) (jaStations.get(0))).getLong("Id"));
	    		    			//ad.setStationName(((JSONObject) (jaStations.get(0))).getString("Name"));
	    		    			if(result.isNull("Value")){
	    		    				ad.setSensorID(result.getLong("SensorId"));//获取报警数据的标签Id，名称
		    		    			for(int j=0;j<jsSensorArray.length();j++){
			    		    			 if((((JSONObject) (jsSensorArray.get(j))).getLong("Id"))==ad.getSensorID()){
			    		    				 ad.setSensorName(((JSONObject) (jsSensorArray.get(j))).getString("Name"));
			    		    				 mCollectorid=((JSONObject) (jsSensorArray.get(j))).getLong("CollectorId");//获取标签的采集器ID，从而获得站点的信息
			    		    				 str_binding=String.valueOf(((JSONObject)jsSensorArray.get(j)).getLong("Addr"));
			    		    				 //str_binding=String.valueOf(mCollectorid)+"_"+String.valueOf(ad.getSensorID());
			    		    				 break;
			    		    			 }
			    		    		 }
		    		    			for(int j=0;j<jsCollectorArray.length();j++){
		    		    				if(((JSONObject)(jsCollectorArray.get(j))).getLong("Id")==mCollectorid){//获取对应的站点信息
		    		    					ad.setStationId(((JSONObject)(jsCollectorArray.get(j))).getLong("StationId"));
		    		    					ad.setStationName(((JSONObject)(jsCollectorArray.get(j))).getString("Name"));
		    		    					str_binding=String.valueOf(((JSONObject)jsCollectorArray.get(j)).getLong("Channel"))+'_'+str_binding;
		    		    					break;
		    		    				}
		    		    			}
		    		    			for(int l=0;l<FragmentPicture.drawslist.size();l++){
	    		    					 if(FragmentPicture.drawslist.get(l).getBinding().equals(str_binding)){
	    		    						 if(FragmentPicture.drawslist.get(l).getType().equals("Title")||FragmentPicture.drawslist.get(l).getType().equals("Monitor")){
	    		    							 FragmentPicture.drawslist.get(l).setText(String.valueOf(result.getDouble("TmpValue")));
	    		    							 FragmentPicture.drawslist.get(l).setIsError(false);
	    		    						 }else{
	    		    							 FragmentPicture.drawslist.get(l).setIsError(false);
	    		    						 }
	    		    						 break;
	    		    					 }
	    		    				 }
	    		    				continue;
	    		    			}
	    		    			ad.setSensorID(result.getLong("SensorId"));//获取报警数据的标签Id，名称
	    		    			for(int j=0;j<jsSensorArray.length();j++){
		    		    			 if((((JSONObject) (jsSensorArray.get(j))).getLong("Id"))==ad.getSensorID()){
		    		    				 ad.setSensorName(((JSONObject) (jsSensorArray.get(j))).getString("Name"));
		    		    				 mCollectorid=((JSONObject) (jsSensorArray.get(j))).getLong("CollectorId");//获取标签的采集器ID，从而获得站点的信息
		    		    				 str_binding=String.valueOf(((JSONObject)jsSensorArray.get(j)).getLong("Addr"));
		    		    				 //str_binding=String.valueOf(mCollectorid)+"_"+String.valueOf(ad.getSensorID());
		    		    				 break;
		    		    			 }
		    		    		 }
	    		    			for(int j=0;j<jsCollectorArray.length();j++){
	    		    				if(((JSONObject)(jsCollectorArray.get(j))).getLong("Id")==mCollectorid){//获取对应的站点信息
	    		    					ad.setStationId(((JSONObject)(jsCollectorArray.get(j))).getLong("StationId"));
	    		    					ad.setStationName(((JSONObject)(jsCollectorArray.get(j))).getString("Name"));
	    		    					str_binding=String.valueOf(((JSONObject)jsCollectorArray.get(j)).getLong("Channel"))+'_'+str_binding;
	    		    					break;
	    		    				}
	    		    			}
	    		    			for(int l=0;l<FragmentPicture.drawslist.size();l++){
    		    					 if(FragmentPicture.drawslist.get(l).getBinding().equals(str_binding)){
    		    						 if(FragmentPicture.drawslist.get(l).getType().equals("Title")||FragmentPicture.drawslist.get(l).getType().equals("Monitor")){
    		    							 FragmentPicture.drawslist.get(l).setText(String.valueOf(result.getDouble("TmpValue")));
    		    							 FragmentPicture.drawslist.get(l).setIsError(true);
    		    							 FragmentPicture.drawslist.get(l).setErrorColor(Color.YELLOW);
    		    						 }else{
    		    							 FragmentPicture.drawslist.get(l).setIsError(true);
    		    							 //FragmentPicture.drawslist.get(l).setErrorColor(0xFF4500);
    		    						 }
    		    						 break;
    		    					 }
    		    				 }
	    		    			//ad.setSensorName(result.getString("SensorName"));
	    		    			ad.setDataId(result.getLong("Id"));
	    		    			if(DataId<ad.getDataId()){
	    		    				DataId=ad.getDataId();
	    		    			}
	    		    			ad.setTValue(result.getDouble("TmpValue"));
	    		    			ad.setCollectTime(result.getString("Time"));
	    		    			ad.setLogText(result.getString("Value"));
	    		    			alerArray.add(ad);//
	    		    			ad=null;
		    		    	 }
	    		    	 }
    		    	 
	    		    	 Message msg = new Message();
	    	             msg.what = Declare.STATUS_ALERT;
	    	             mhandler.sendMessage(msg);
	    	             Declare.isloadstations=true;
    		    	 }/*else{
    		    		 Message msg=new Message();
    		    		 msg.what=201;
    		    		 msg.obj="数据为空";
    		    		 mhandler.sendMessage(msg);
    		    	 }*/
    		    	 Message msg = new Message();
    	             msg.what = Declare.STATUS_SUCCESS;
    		    	 FragmentPicture.mHandler.sendMessage(msg);
    		     }else{
    		    	 Message msg = new Message();
    	             msg.what = Declare.STATUS_ERROR;
    	             msg.obj=errorcode;
    	             mhandler.sendMessage(msg);
    		     }
           	 }else if(iis.mark.equals("outtime")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接超时";
                    mhandler.sendMessage(msg);
           	 }else if(iis.mark.equals("error")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接错误";
                    mhandler.sendMessage(msg);
           	 }else if(iis.mark.equals("未授权")){
           		 	Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="操作失败，权限未认证";
                    mhandler.sendMessage(msg);
           	 }else{
    	    	Message msg = new Message();
                msg.what = Declare.STATUS_ERROR;
                msg.obj=iis.mark;
                mhandler.sendMessage(msg);
    	     }
         	} catch (Exception e){
                //e.printStackTrace();
                Message msg = new Message();
                msg.what =10;// Declare.STATUS_ERROR;
                msg.obj=e.getMessage();//"操作失败，权限未认证";
                mhandler.sendMessage(msg);
           }

         }
	}).start();
	}
    
    //网络状态监听
    private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {  
    	  
    	 @Override  
    	 public void onReceive(Context context, Intent intent) {  
    	    
    	  String action = intent.getAction();  
    	        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {  
    	             
    	            mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);  
    	            netInfo = mConnectivityManager.getActiveNetworkInfo();    
    	            if(netInfo != null && netInfo.isAvailable()) {  
    	                 /////////////网络连接
    	            	if(Declare.islogin){
    	            		  sendbeat();
    	            	  }else{
    	            		  login();
    	            	  }
    	                /*String name = netInfo.getTypeName();  
    	                  
    	                if(netInfo.getType()==ConnectivityManager.TYPE_WIFI){  
    	                 /////WiFi网络  
    	  
    	                }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET){  
    	                /////有线网络  
    	  
    	                }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){  
    	               /////////3g网络  
    	  
    	                }  */
    	              } else {  
    	             ////////网络断开  
    	            	  Message msg = new Message();
    	            	  msg.what = Declare.STATUS_ERROR;
    	            	  msg.obj="网络连接中断";
    	            	  mHandler.sendMessage(msg);
    	            }  
    	        }  
    	  
    	   }   
    	   };  
 //延时线程
    public class Alert_thread extends Thread{
		@Override
    	public void run(){
			try{
			int j=0;
			str_info=new String();
			str_info="";
			for(int i=0;i<alerArray.size();i++){
      		  for(j=0;j<alertlist.size();j++){
      			  long t1=alerArray.get(i).getSensorID();
      			  long t2=alertlist.get(j).getSensorID();
      			  if((t1==t2)){
      				  if( DateTimeControl.minutesDiff(DateTimeControl.GetItemDate(alertlist.get(j).getCollectTime()), DateTimeControl.GetItemDate(alerArray.get(i).getCollectTime()))>5){
          				  alertsensorname=alerArray.get(i).getSensorName();
      					  alertstationname=alerArray.get(i).getStationName();
          				  alerttime=alerArray.get(i).getCollectTime();
          				  alertvalue=String.valueOf(alerArray.get(i).getTValue());
          				  alertinfo=alerArray.get(i).getLogText();
          				  //FragmentPicture.alert_display(alertstationname, alerttime, alertvalue, alertinfo);
          				  str_info=str_info+" "+alertstationname+" "+alertsensorname+" "+alerttime+" "+alertinfo+"当前温度为："+alertvalue+" ";
          				 /* Message msg = new Message();
          				  msg.what = Declare.STATUS_ALERT;
          				  msg.obj="告警";
          				  mHandler.sendMessage(msg);
          				  alertlist.get(j).setCollectTime(alerttime);
	          			  try {
	          				  sleep_thread.sleep(1500);
	        			  } catch (InterruptedException e) {
	        				  // TODO Auto-generated catch block
	        				  e.printStackTrace();
	        			  }*/
      			  	  }
      				  break;
      			  }
      		  }
      		  if(j>=alertlist.size()){
      			  alertsensorname=alerArray.get(i).getSensorName();
				  alertstationname=alerArray.get(i).getStationName();
  				  alerttime=alerArray.get(i).getCollectTime();
  				  alertvalue=String.valueOf(alerArray.get(i).getTValue());
  				  alertinfo=alerArray.get(i).getLogText();
  				//FragmentPicture.alert_display(alertstationname, alerttime, alertvalue, alertinfo);
  				  str_info=str_info+" "+alertstationname+" "+alertsensorname+" "+alerttime+" "+alertinfo+" 当前温度为："+alertvalue+' ';
  				  /*Message msg = new Message();
  				  msg.what = Declare.STATUS_ALERT;
  				  msg.obj="告警";
  				  mHandler.sendMessage(msg);
  				  alerArray.get(i).isAlert=true;
  				  alertlist.add(alerArray.get(i));
  				  try {
    				  sleep(1500);
  				  } catch (InterruptedException e) {
  					  // TODO Auto-generated catch block
  					  e.printStackTrace();
  				  }*/
      		  }
      			  
      		  
      	  }
			 // Message msg = new Message();
			 // msg.what = Declare.STATUS_ALERT;
			  //msg.obj="告警";
			  //mHandler.sendMessage(msg);
			  Intent intent1=new Intent(ClientService.this,fudongService.class);
			  	
		  	if(isServiceWork(ClientService.this,"com.example.substationtemperature.floatwindow.fudongService"))
		  	{	stopService(intent1);
		  	}
			  startService(intent1);
		}catch(Exception e){
			
		}
      }
    }
  //延时线程
    public class sleep_thread extends Thread{
		String str;
		@Override
    	public void run(){
    	while(!Thread.currentThread().isInterrupted())
    	{
				try {
					sleep_thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
	        	msg.what = 10;//read_od
	        	mHandler.sendMessage(msg);
	        	
    	}
      }
    }
@SuppressWarnings("unused")
private void onDestory(){
	if(myNetReceiver!=null){  
        unregisterReceiver(myNetReceiver);  
	}
}
public boolean isServiceWork(Context mContext, String serviceName) {
    boolean isWork = false;
    ActivityManager myAM = (ActivityManager) mContext
            .getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningServiceInfo> myList = myAM.getRunningServices(40);
    if (myList.size() <= 0) {
        return false;
    }
    for (int i = 0; i < myList.size(); i++) {
        String mName = myList.get(i).service.getClassName().toString();
        if (mName.equals(serviceName)) {
            isWork = true;
            break;
        }
    }
    return isWork;
}
}