package com.example.substationtemperature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DateTimePickDialogUtil.DateTimeControl;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.substationtemperature.adapter.ListItemAdapter_measuredata;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.MeasureData;
import com.example.substationtemperature.base.ReturnData;
import com.example.substationtemperature.network.ClientService;
import com.example.substationtemperature.view.RefreshableView;
import com.example.substationtemperature.view.RefreshableView.PullToRefreshListener;

public class  FragmentRealdata extends Fragment implements OnClickListener{
	private static Activity activity;
	private ListView datalist;
	private View view;
	//private boolean isvisible=false;
	public  boolean sleeploop=true; 
	
	private Spinner spn_realdata_zhan;//,spn_realdata_cld
	private String[] zhanArray={}; //,cldArray={}
	private ArrayAdapter<String> zhanAdapter;//,cldAdapter
	
	private List<MeasureData> datas;
	
	Calendar calendar = Calendar.getInstance();
   // private List<Map<String,Object>>list_realdata=new ArrayList<Map<String,Object>>();
    //private Map<String,Object>map;//=new HashMap<String,Object>();
    //private SimpleAdapter adapter=null;
    private ListItemAdapter_measuredata mAdapter;
    private String info;
    //private MeasureData[] mdArray;
    public static long stationid,sensorid;
    private ProgressDialog pd;
    //private static Dialog dialog;
    private sleep_thread thread;
    public static Handler mHandler;	
    RefreshableView refreshableView;
	public FragmentRealdata() {
    }
	public Handler mHandler_read = new Handler()
	{										
		//@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg)										
	  	{											
		  	super.handleMessage(msg);			
		  	switch(msg.what)
		  	{
		  	case Declare.STATUS_SUCCESS:
		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
		  		int count=0;
		  		refreshableView.finishRefreshing();
		  		if(Declare.md_rl_Array.length>0)
		  			count=Declare.md_rl_Array.length;
		  		refresh();
		  		Toast.makeText(activity, "实时数据获取成功，共"+count+"条",Toast.LENGTH_LONG).show();
		  		break;
		  	case Declare.STATUS_ERROR:
		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
		  		refreshableView.finishRefreshing();
		  		Declare.islogin=false;
		  		info = (String) msg.obj;
		  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
		  		refresh();
		  		break;
		  	case 10:
		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
		  		refreshableView.finishRefreshing();
		  		info = (String) msg.obj;
		  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
		  		refresh();
		  		/*zhanAdapter=null;
				if(ClientService.zhanArray!=null){
		        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
		        }else{
		        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
		        }
				spn_realdata_zhan.setAdapter(ClientService.zhanAdapter);*/
		  		break;
		  	/*case 20:
		  		cldAdapter=null;
		  		if(ClientService.cldArray!=null){
					cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
				}else{
				cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);}
				spn_realdata_cld.setAdapter(cldAdapter);
				break;*/
		  	case Declare.STATUS_TIMEOUT:
		  		if((Declare.islogin)&&(ClientService.stArray!=null)){
		  			GetRealsByStation(stationid);
		  			/*if(pd==null){
		  				pd=ProgressDialog.show(activity, "查询中...", "正在查询",true,false);
					}else if(!pd.isShowing()){
						pd.show();
					}*/
		  		}else{
		  			Declare.islogin=false;
		  		}
		  		//refresh();
		  		break;
		  	case 11:
		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
		  		refreshableView.finishRefreshing();
		  		info = (String) msg.obj;
		  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
		  		refresh();
		  		break;
		  	}
	  	}
	};
	private void refresh() {
		// TODO Auto-generated method stub
		
		//zhanAdapter.setDropDownViewResource(R.layout.item_dropdown);
		//spn_realdata_zhan.setPrompt("请选择站点名称");
		/*if(ClientService.zhanArray!=null){
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
        }else{
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        }
		spn_realdata_zhan.setAdapter(zhanAdapter);
		if(zhanAdapter!=null)
			spn_realdata_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));*/
		loadData();
  		mAdapter=null;
  		mAdapter=new ListItemAdapter_measuredata(activity,datas);
  		datalist.setAdapter(mAdapter);
	}
	//延时定时线程
	public class sleep_thread extends Thread{
		@Override
    	public void run(){
	    	long i=1;
			while(!Thread.currentThread().isInterrupted()&& sleeploop)
	    	{
				if((i % 20)==0){
				Message msg = new Message();
	        	msg.what = 101;//read_od
	        	mHandler_read.sendMessage(msg);
	        	}
	        	try {
					sleep_thread.sleep(3000);
					i++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
		}
    }
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_realdata, container, false);
        //启动自动刷新计时延时线程
        
        //如果为登录则显示登录界面
        mHandler= new Handler()
		{										
			//@SuppressLint("HandlerLeak")
			public void handleMessage(Message msg)										
		  	{											
			  	super.handleMessage(msg);			
			  	switch(msg.what)
			  	{
			  	case 80:
			  		if(ClientService.zhanArray!=null){
			        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
			        	
			        }else{
			        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
			        }
					spn_realdata_zhan=(Spinner)view.findViewById(R.id.spn_realdata_zhandian);
					spn_realdata_zhan.setAdapter(zhanAdapter);
					if((zhanAdapter!=null)){
						spn_realdata_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
					}
			  		break;
			  	}
		  	}
		};
		if(getUserVisibleHint())
		initview();
        return view;
    }
	
	private void initview() {
		// TODO Auto-generated method stub
		spn_realdata_zhan=(Spinner)view.findViewById(R.id.spn_realdata_zhandian);
		//spn_realdata_cld=(Spinner)view.findViewById(R.id.spn_realdata_caijiqi);
		//spn_realdata_zhan.setSelection(0,true);
		spn_realdata_zhan.setOnItemSelectedListener(new zhanSelectedListener());
		//spn_realdata_cld.setSelection(0, true);
		//spn_realdata_cld.setOnItemSelectedListener(new cldSelectedListener());
		//zhanArray[2]="110kV站5";
        if(ClientService.zhanArray!=null){
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
        	
        }else{
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        }
       
        zhanAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spn_realdata_zhan.setPrompt("请选择站点名称");
		spn_realdata_zhan.setAdapter(zhanAdapter);
		if((zhanAdapter!=null)){
			spn_realdata_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));			
		}
		/*if(ClientService.cldArray!=null){
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
		}else{
		cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);}
		cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spn_realdata_cld.setPrompt("请选择测量点名称");
		spn_realdata_cld.setAdapter(cldAdapter);
		*/
		if((Declare.islogin)&&(ClientService.stArray!=null)){
  			//getReals(stationid);
  		}
		refreshableView = (RefreshableView)view.findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			   @Override
			   public void onRefresh() {
			    try {
			     if((Declare.islogin)&&(ClientService.stArray!=null)){
			    	 GetRealsByStation(stationid);
			  			/*if(pd==null){
			  				pd=ProgressDialog.show(activity, "查询中...", "正在查询",true,false);
						}else if(!pd.isShowing()){
							pd.show();
						}*/
			  		}
			     Thread.sleep(200);
			    } catch (InterruptedException e) {
			     e.printStackTrace();
			    }
			    
			   }
			  }, 0);

		datalist=(ListView)view.findViewById(R.id.listview_realdata);
        //loadData();
        //mAdapter = new ListItemAdapter_measuredata(activity,datas);
    	//adapter=new SimpleAdapter(activity,list_realdata,R.layout.list_item,
        //        new String[]{"zhandian","value","shijian"},new int[]{R.id.txt_list_zhandian,R.id.txt_list_value,R.id.txt_list_time});
    	//datalist.setAdapter(mAdapter);
    	
    	this.refresh();
    	datalist.setOnItemClickListener(new ItemClickListener());
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity=activity;
	}
	
	// 列表项点击监听器类
	private final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //需要把表号等参数传递过去，显示此表号对应的数据。2016-03-12h注。
			MeasureData item = datas.get(position);
			Declare.zhan=spn_realdata_zhan.getSelectedItem().toString();
			Declare.celiangdian=item.getSensorsName();
			//intent.putExtra("ameterno", bd);
			//startActivity(intent);
			//startActivity(new Intent(ManageDataActivity.this, ManageDataDetailActivity.class));
			// activity切换过场动画
			//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			MainActivity.selectNavigationItem(3);
    		MainActivity.mViewPager.setCurrentItem(3);
		}
	}

	private void loadData() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//final String currentDateTimeStr = sdf.format(calendar.getTime());
		datas = new ArrayList<MeasureData>();
		//Date now = new Date();
		//AmeterData currData = new AmeterData();
		//String sql="select * from sxxy_data";
		/*Cursor cursor = null;
		if (cursor.getCount()>0){
			//for(int j=0;j<300;j++){
			cursor.moveToFirst();
			//currData.setAmeterNo(cursor.getString(1));
			//currData.setTestDate(DateTimeControl.strToDateLong(cursor.getString(2)));}
			for (int i = 0; i < cursor.getCount(); i++) {
				//cursor.moveToFirst();
				MeasureData currData = new MeasureData();
				currData.setName(cursor.getString(1));
				currData.setValue(String.valueOf(cursor.getFloat(1)));
				currData.setTime(now);
				datas.add(currData);
				currData=null;
				cursor.moveToNext();
			//}
		}*/
		if(Declare.md_rl_Array!=null){
		for (int i = 0; i < Declare.md_rl_Array.length; i++) {//(int i=0;i<ClientService.mdArray.length();i++)
			MeasureData currData = new MeasureData();
			/*currData.setSensorsName(spn_realdata_cld.getSelectedItem().toString());
			currData.setTime((currentDateTimeStr));
			currData.setValue(44.0f+i);*/
			currData.setSensorsName(Declare.md_rl_Array[i].getSensorsName());
			currData.setTime(Declare.md_rl_Array[i].getTime());
			currData.setValue(Declare.md_rl_Array[i].getValue());
			
			datas.add(currData);
			currData=null;
			//cursor.moveToNext();
		}
		}
	}

	//站点选择项操作
	private class zhanSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
			if(ClientService.stArray!=null){
				stationid=ClientService.stArray[position].getStationID();
			}
			if((Declare.islogin)){//&&(Declare.stationid!=stationid)
				
				GetRealsByStation(stationid);
				Declare.md_rl_Array=null;//清空原来的数
				//Declare.stationid=stationid;
				Declare.zhan=spn_realdata_zhan.getSelectedItem().toString();
				if(pd==null){
				pd=ProgressDialog.show(activity, "查询中...", "正在查询",true,false);
				}else if(!pd.isShowing()){
					pd.show();
				}
				/*try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ClientService.getSensors(stationid);//用于刷新标签名称列表*/
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	//测量点选择项的操作内容
	@SuppressWarnings("unused")
	private class cldSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			/*if(ClientService.ssArray!=null){
				sensorid=ClientService.ssArray[position].getSensorID();
			}
			if(Declare.islogin){
				getReals(sensorid);
			}*/
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isvisible=isVisibleToUser;
    	if(isVisibleToUser){
    		if (view!=null){
    			initview();
    		}
        	if (thread==null){
	    		sleeploop=true;
	        	thread=new sleep_thread();
	        	thread.start();
        	}
        	if((!Declare.islogin)){
        		FragmentIndex.comfir_display();
            }
        }else{
        	
        	if(thread!=null){
        		thread.interrupt();
        		thread=null;
        		sleeploop=false;
        	}
        }
    }
	
	@Override
	public void onResume(){
		super.onResume();
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		//sleeploop=false;
		//thread.interrupt();
	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	//获取指定站点的实时数据：
    public void GetRealsByStation(final long sensorsid) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
         @Override
         public void run() {
        	 try{
        		 //ReturnData iis = ClientService.sendpost("GetReals?stationId="+sensorsid);//GetRealsByStation
        		 ReturnData iis = ClientService.sendpost("GetRealsByStation?stationId="+sensorsid);
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
    		    		 msg.what=10;
    		    		 msg.obj="实时数据数据为空";
    		    		 mHandler_read.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 JSONObject joSensors=result.getJSONObject("Sensors");
    		    	 if(joSensors.isNull("Stations")||(joSensors.isNull("Sensors"))){
    		    		 Message msg=new Message();
    		    		 msg.what=10;
    		    		 msg.obj="实时数据数据为空";
    		    		 mHandler_read.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 JSONArray jaStations=joSensors.getJSONArray("Stations");

    		    	 JSONArray jaSensors=joSensors.getJSONArray("Sensors");
    		    	 if(result.isNull("Datas")){
    		    		 Message msg=new Message();
    		    		 msg.what=10;
    		    		 msg.obj="实时数据数据为空";
    		    		 mHandler_read.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 JSONObject joDatas=result.getJSONObject("Datas");
    		    	 if(joDatas.length()==0){
    		    		 Message msg=new Message();
    		    		 msg.what=10;
    		    		 msg.obj="实时数据数据为空";
    		    		 mHandler_read.sendMessage(msg);
    		    		 return;
    		    	 }
    		    	 jsArray=joDatas.getJSONArray("Tmp");
    		    	 if(jsArray.length()>0){//调试用jsonArray_md,正式时使用jsArray;
    		    		 Declare.md_rl_Array=new MeasureData[jsArray.length()];
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
	    		    		 
	    		    		 Declare.md_rl_Array[i]=md;
	    		    		 md=null;
	    		    	 }
	    		    	 Message msg = new Message();
	    	             msg.what = 0;
	    	             mHandler_read.sendMessage(msg);
	    	             Declare.isloadstations=true;
    		    	 }else{
    		    		 Message msg=new Message();
    		    		 msg.what=10;
    		    		 msg.obj="数据为空";
    		    		 mHandler_read.sendMessage(msg);
    		    	 }
    		     }else{
    		    	 Message msg = new Message();
    	             msg.what = 1;
    	             msg.obj=errorcode;
    	             mHandler_read.sendMessage(msg);
    		     }
           	 }else if(iis.mark.equals("outtime")){
           		 Message msg = new Message();
                    msg.what = 1;
                    msg.obj="连接超时";
                    mHandler_read.sendMessage(msg);
           	 }else if(iis.mark.equals("error")){
           		 Message msg = new Message();
                    msg.what = 1;
                    msg.obj="连接错误";
                    mHandler_read.sendMessage(msg);
           	 }else if(iis.mark.equals("未授权")){
           		 	Message msg = new Message();
                    msg.what = 1;
                    msg.obj="操作失败，权限未认证";
                    mHandler_read.sendMessage(msg);
           	 }else{
    	    	Message msg = new Message();
                msg.what = 1;
                msg.obj=iis.mark;
                mHandler_read.sendMessage(msg);
    	     }
        	  //..........
        	  // 此处省略处理数据的代码
        	  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
         	} catch (Exception e){
                //e.printStackTrace();
                Message msg = new Message();
                msg.what = 11;
                msg.obj=e.getMessage();//"操作失败，权限未认证";
                mHandler_read.sendMessage(msg);
           }

         }
	}).start();
	}
    
  
  			
}
