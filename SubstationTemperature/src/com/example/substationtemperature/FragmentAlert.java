package com.example.substationtemperature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DateTimePickDialogUtil.DateTimePickDialogUtil;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.substationtemperature.adapter.ListItemAdapter_xinxi;
import com.example.substationtemperature.base.AlertInfo;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.ReturnData;
import com.example.substationtemperature.network.ClientService;
import com.example.substationtemperature.view.RefreshableView;
import com.example.substationtemperature.view.RefreshableView.PullToRefreshListener;

public class FragmentAlert extends Fragment implements OnClickListener {
	private Activity activity;
	private View view;
	
	private Spinner spn_alert_zhan,spn_alert_cld;//
	private String[] zhanArray={},cldArray={}; //
	private ArrayAdapter<String> zhanAdapter,cldAdapter;//
	private TextView txt_startdate,txt_enddate;
	private Button btn_query_alert,btn_more_alert;
	private ListView list_xinxi;
	
	private ArrayList<AlertInfo> xinxis;
	private Calendar calendar = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final String currentDateTimeStr = sdf.format(calendar.getTime());
	private ListItemAdapter_xinxi mAdapter=null;
	private AlertInfo[] alerArray;
	private String fromtime,endtime;
	//private int minutes;
	private int stationid=0,sensorid=0,j=0;
	private boolean isvisible=false;
	private LinearLayout layou;
	private ProgressDialog pd;
	public static Handler mhandler;
	
	private RefreshableView swipeRefreshLayout;
	
	public FragmentAlert(){
		
	}
	
	private void refresh(){
		if((zhanAdapter!=null)&&(isvisible))
		{	
			spn_alert_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
		}
		loadData();
		mAdapter=null;
		mAdapter=new ListItemAdapter_xinxi(activity,xinxis);
		list_xinxi.setAdapter(mAdapter);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle saveInstanceState){
		view=inflater.inflate(R.layout.fragment_alert, container,false);
		mhandler = new Handler()
		{										
			  public void handleMessage(Message msg)										
			  {								
				switch(msg.what){
				case 0:
					if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
					//swipeRefreshLayout.finishRefreshing();
					refresh();
					int count=0;
					if(alerArray.length>0){
						count=alerArray.length;}
					else{
						count=0;
					}
					Toast.makeText(activity, "告警信息请求成功,共计："+count+"条", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
					Declare.islogin=false;
					String info=(String) msg.obj;
					Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
					refresh();
					break;
				case 10:
					if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
					//Declare.islogin=false;
					info="没有符合条件的记录";
					Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
					refresh();
					break;
				case 11:
					if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
					info=(String) msg.obj;
					Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
					refresh();
				case 20:
			  		cldAdapter=null;
			  		cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
			  		spn_alert_cld.setAdapter(cldAdapter);
			  		if((cldAdapter!=null)&&(isvisible))
			  			spn_alert_cld.setSelection(zhanAdapter.getPosition(Declare.celiangdian));
			  		//Toast.makeText(activity, "测量点信息获取成功",Toast.LENGTH_SHORT).show();
			  		break;
			  	case 21:
			  		info = (String) msg.obj;
			  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
			  		break;
				case 80:
					if(ClientService.zhanArray!=null){
			        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
			        }else{
			        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
			        }
					spn_alert_zhan.setAdapter(zhanAdapter);
					if((zhanAdapter!=null)&&(isvisible))
					{	
						spn_alert_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
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
		
		spn_alert_zhan=(Spinner)view.findViewById(R.id.spn_alert_zhandian);
		spn_alert_zhan.setOnItemSelectedListener(new zhanSelectedListener());
		layou=(LinearLayout)view.findViewById(R.id.lay_alert_zhan);
		layou.setOnClickListener(new spnselected());
		spn_alert_cld=(Spinner)view.findViewById(R.id.spn_alert_celiangdian);
		//zhanArray[2]="110kV站5";
        if(ClientService.zhanArray!=null){
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
        }else{
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        }
		zhanAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spn_alert_zhan.setPrompt("请选择站点名称");
		spn_alert_zhan.setAdapter(zhanAdapter);
		if((zhanAdapter!=null)&&(isvisible))
		{	
			spn_alert_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
		}
		if(ClientService.cldArray!=null){
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
		}else{
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);
		}
		cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spn_alert_cld.setPrompt("请选择测量点名称");
		spn_alert_cld.setAdapter(cldAdapter);
		if((cldAdapter!=null)&&(isvisible)){
			spn_alert_cld.setSelection(cldAdapter.getPosition(Declare.celiangdian));
		}
		spn_alert_cld.setOnItemSelectedListener(new cldSelectedListener());
		txt_startdate=(TextView)view.findViewById(R.id.alert_startDatePickerTV);
		txt_startdate.setOnClickListener(this);
		txt_startdate.setText(Declare.starttime);
		txt_enddate=(TextView)view.findViewById(R.id.alert_endDatePickerTV);
		txt_enddate.setOnClickListener(this);
		txt_enddate.setText(Declare.endtime);
		btn_query_alert=(Button)view.findViewById(R.id.alert_queryDataBtn);
		btn_query_alert.setOnClickListener(this);
		btn_more_alert=(Button)view.findViewById(R.id.moreBtn);
		btn_more_alert.setOnClickListener(this);
		//RefreshableView rv=new RefreshableView(activity,null);
		//swipeRefreshLayout = (RefreshableView)view. findViewById(R.id.main_srl); 
		//swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,    android.R.color.holo_orange_light, android.R.color.holo_red_light);
		/*swipeRefreshLayout.setOnRefreshListener(new PullToRefreshListener() {
			@Override   
			public void onRefresh() {
				//new LoadDataThread().start();
				try {
					getWarnLog(stationid,fromtime,endtime);
					alerArray=null;
				     Thread.sleep(200);
				    } catch (InterruptedException e) {
				     e.printStackTrace();
				    }
				
			   }
			  }, 0);
		*/
		loadData();
		mAdapter=new ListItemAdapter_xinxi(activity,xinxis);
		list_xinxi=(ListView)view.findViewById(R.id.listview_alert);
		list_xinxi.setOnItemClickListener(new ItemClickListener());
		list_xinxi.setAdapter(mAdapter);
		
		
		//TextView txt_zhan=(TextView)view.findViewById(R.id.item_zhan);
		//txt_zhan.setCompoundDrawables(activity.getResources().getDrawable(R.drawable.cetiao), null, null, null);
		
	}
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity=activity;
	}
	private void loadData() {
		// TODO Auto-generated method stub
		xinxis = new ArrayList<AlertInfo>();
		//Date time=new Date();
		if(alerArray!=null)
		
		try{
		for(int i=0;i<alerArray.length;i++){
			AlertInfo currdata=new AlertInfo();
			/*currdata.setStationName("0001");
			currdata.setCollectTime(currentDateTimeStr);
			currdata.setTValue(Float.valueOf("23."+i));
			currdata.setLogText("这里显示告警详情");*/
			currdata.setSensorName(alerArray[i].getSensorName());
			currdata.setCollectTime(alerArray[i].getCollectTime());
			currdata.setTValue(alerArray[i].getTValue());
			currdata.setLogText(alerArray[i].getLogText());
			xinxis.add(currdata);
			currdata=null;
			j=i;
		}
		}catch(Exception e){
			Toast.makeText(activity, "告警信息请求成功,共计："+j+"条", Toast.LENGTH_SHORT).show();
		}
	}
	private class spnselected implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//isseleceted=true;
		}
		
	}
	private final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //需要把表号等参数传递过去，显示此表号对应的数据。2016-03-12h注。
			//AlertInfo item = xinxis.get(position);
			//Intent intent=new Intent(activity, ManageDataDetailActivity.class);
			//Bundle bd=new Bundle();
			Declare.zhan=spn_alert_zhan.getSelectedItem().toString();
			//Declare.celiangdian=item.getName();
			//Declare.time=item.getAlerttime();
			//intent.putExtra("ameterno", bd);
			//startActivity(intent);
			//startActivity(new Intent(ManageDataActivity.this, ManageDataDetailActivity.class));
			// activity切换过场动画
			//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			//MainActivity.selectNavigationItem(4);
    		//MainActivity.mViewPager.setCurrentItem(4);
		}
	}
	//站点选择项操作
	private class zhanSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			//if (isseleceted)
			
			if((ClientService.stArray!=null)){
				stationid=ClientService.stArray[position].getStationID();
				//Declare.zhan=spn_alert_zhan.getSelectedItem().toString();
			}
			if((Declare.islogin)&&(Declare.stationid!=stationid)&&(isvisible)){
			//	getWarnLog(stationid,fromtime,endtime);
				Declare.zhan=spn_alert_zhan.getSelectedItem().toString();
				ClientService.getSensors(stationid);
				Declare.stationid=stationid;
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	}
	//测量点选择项的操作内容
	private class cldSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if(ClientService.ssArray!=null){
				sensorid=ClientService.ssArray[position].getSensorID();
			}
			if((sensorid!=Declare.sensorid)&&(Declare.islogin)&&(isvisible)){
				//ClientService.getHistories(sensorid, fromtime, endtime);//自动获取数据
				Declare.sensorid=sensorid;
				Declare.celiangdian=spn_alert_cld.getSelectedItem().toString();
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	}/**/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		String currentDateTimeStr = sdf.format(calendar.getTime());
		switch(v.getId()){
		case R.id.alert_startDatePickerTV:
			currentDateTimeStr=(txt_startdate.getText().toString().substring(0,4)+"年"+
					txt_startdate.getText().toString().substring(5, 7)+"月"+
					txt_startdate.getText().toString().substring(8,10)+"日"+
					txt_startdate.getText().toString().substring(11,16));
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(activity,
					currentDateTimeStr);
			dateTimePicKDialog.dateTimePicKDialog(txt_startdate);
			break;
		case R.id.alert_endDatePickerTV:
			currentDateTimeStr=(txt_enddate.getText().toString().substring(0,4)+"年"+
					txt_enddate.getText().toString().substring(5, 7)+"月"+
					txt_enddate.getText().toString().substring(8,10)+"日"+
					txt_enddate.getText().toString().substring(11,16));
			dateTimePicKDialog = new DateTimePickDialogUtil(activity,
					currentDateTimeStr);
			dateTimePicKDialog.dateTimePicKDialog(txt_enddate);
			break;
		case R.id.alert_queryDataBtn:
			if(!txt_startdate.getText().toString().equals("")){
				fromtime=txt_startdate.getText().toString();
			}else{
				Toast.makeText(activity, "请选择起始时间", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!txt_enddate.getText().toString().equals("")){
				endtime=txt_enddate.getText().toString();
			}else{
				Toast.makeText(activity, "请选择截止时间", Toast.LENGTH_SHORT).show();
				return;
			}
			Declare.starttime=txt_startdate.getText().toString();
			Declare.endtime=txt_enddate.getText().toString();
			//minutes = DateTimeControl.minutesDiff(DateTimeControl.GetItemDate(fromtime), DateTimeControl.GetItemDate(endtime));
			//Toast.makeText(activity, String.valueOf(minutes), 3000).show();
			if(Declare.islogin){
				getWarnLogBySensorId(sensorid,fromtime,endtime);
				alerArray=null;
				pd=ProgressDialog.show(activity, "查询中...", "正在查询",true,false);
			}
			break;
		case R.id.moreBtn:
			if(!txt_startdate.getText().toString().equals("")){
				fromtime=txt_startdate.getText().toString();
			}else{
				Toast.makeText(activity, "请选择起始时间", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!txt_enddate.getText().toString().equals("")){
				endtime=txt_enddate.getText().toString();
			}else{
				Toast.makeText(activity, "请选择截止时间", Toast.LENGTH_SHORT).show();
				return;
			}
			Declare.starttime=txt_startdate.getText().toString();
			Declare.endtime=txt_enddate.getText().toString();
			//minutes = DateTimeControl.minutesDiff(DateTimeControl.GetItemDate(fromtime), DateTimeControl.GetItemDate(endtime));
			//Toast.makeText(activity, String.valueOf(minutes), 3000).show();
			if(Declare.islogin){
				getWarnLog(stationid,fromtime,endtime);
				alerArray=null;
				pd=ProgressDialog.show(activity, "查询中...", "正在查询",true,false);
			}
			break;
		}
	}
	public void getWarnLogBySensorId(final long asensorid, final String fromtime,final String endtime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
	        	ReturnData iis = ClientService.sendpost("GetWarnLogsBySensor?sensorId="+asensorid+"&from="+URLEncoder.encode(fromtime,"utf-8")+"&to="+URLEncoder.encode(endtime,"utf-8"));
	        	if(iis.mark.equals("ok")){
	        		// 使用BufferedReader对象读取返回的数据流
				    // 按行读取，存储在StringBuider对象response中
	        		BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
	        		StringBuilder response=new StringBuilder();
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
	    		    if(result.isNull("Error")){//正常
	    		    	result=result.getJSONObject("Result");
				    	if(result.isNull("Sensors")){
				    		Message msg = new Message();
	    		     		msg.what = 10;
	    		     		msg.obj="没有符合条件的数据";
	    		     		mhandler.sendMessage(msg);
	    		     		return;
				    	} 
				    	JSONObject joSensors=result.getJSONObject("Sensors");
				    	if((joSensors.isNull("Stations"))||(joSensors.isNull("Sensors"))){
				    		Message msg = new Message();
	    		     		msg.what = 10;
	    		     		msg.obj="没有符合条件的数据";
	    		     		mhandler.sendMessage(msg);
	    		     		return;
				    	} 
				    	JSONArray jaStations=joSensors.getJSONArray("Stations");

	    		    	JSONArray jaSensors=joSensors.getJSONArray("Sensors");
	    		    	JSONObject joDatas=result.getJSONObject("Datas");
	    		    	if(result.isNull("Datas")){
	    		    		Message msg = new Message();
	    		     		msg.what = 10;
	    		     		msg.obj="没有符合条件的数据";
	    		     		mhandler.sendMessage(msg);
	    		     		return;
	    		    	}
	    		    	jsArray=joDatas.getJSONArray("Err");
	    		    	
	    		    	if(jsArray.length()>0){
	    		    		alerArray=new AlertInfo[jsArray.length()];
	    		    		for(int i=0;i<jsArray.length();i++){
	    		    			AlertInfo ad=new AlertInfo();
	    		    			result=(JSONObject) jsArray.get(i);
	    		    			ad.setStationId(((JSONObject) (jaStations.get(0))).getLong("Id"));
	    		    			ad.setStationName(((JSONObject) (jaStations.get(0))).getString("Name"));
	    		    			ad.setSensorID(result.getLong("SensorId"));
	    		    			for(int j=0;j<jaSensors.length();j++){
		    		    			 if((((JSONObject) (jaSensors.get(j))).getLong("Id"))==ad.getSensorID()){
		    		    				 ad.setSensorName(((JSONObject) (jaSensors.get(j))).getString("Name"));
		    		    				 break;
		    		    			 }
		    		    		 }
	    		    			//ad.setSensorName(result.getString("SensorName"));
	    		    			ad.setDataId(result.getLong("Id"));
	    		    			if(!result.isNull("TmpValue")){
	    		    			ad.setTValue(result.getDouble("TmpValue"));}
	    		    			ad.setCollectTime(result.getString("Time"));
	    		    			ad.setLogText(result.getString("Value"));
	    		    			alerArray[i]=ad;//(AlertInfo) jsArray.get(i);
	    		    			ad=null;
	    		    		}
	    		    		Message msg = new Message();
	    		    		msg.what = 0;
	    		    		mhandler.sendMessage(msg);
    		    	 	}else{
    		    	 		Message msg=new Message();
    		    	 		msg.what=10;
    		    	 		mhandler.sendMessage(msg);
    		    	 	}
    		     	}else{//服务器返回错误信息
    		     		Message msg = new Message();
    		     		msg.what = 1;
    		     		msg.obj=errorcode;
    		     		mhandler.sendMessage(msg);
    		     	}
	        	}else if(iis.mark.equals("outtime")){//超时
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj="连接超时";
	        		mhandler.sendMessage(msg);
	        	}else if(iis.mark.equals("error")){//连接异常
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj="连接错误";
	        		mhandler.sendMessage(msg);
	        	}else if(iis.mark.equals("未授权")){//未授权认证
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj="操作失败，权限未认证";
	        		mhandler.sendMessage(msg);
	        	}else{//其他异常
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj=iis.mark;
	        		mhandler.sendMessage(msg);
	        	}
	         	} catch (Exception e){
	         		//e.printStackTrace();
	         		Message msg = new Message();
	        		msg.what = 11;
	        		msg.obj="告警信息获取失败！";
	        		mhandler.sendMessage(msg);
	         	}
			}
		}).start();
	}
		
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isvisible=isVisibleToUser;
        if(isVisibleToUser){
        	//onCreateView()
        	Declare.infotype=0;
        	if(view!=null){
        		initview();
        	}
        	if(!Declare.islogin){
        		FragmentIndex.comfir_display();}
        }
    }
	
	public void getWarnLog(final int sensorsid,final String datetime,final String endtime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
	        	ReturnData iis = ClientService.sendpost("GetWarnLogsByStation?stationId="+sensorsid+"&from="+URLEncoder.encode(datetime,"utf-8")+"&to="+URLEncoder.encode(endtime,"utf-8"));
	        	if(iis.mark.equals("ok")){
	        		// 使用BufferedReader对象读取返回的数据流
				    // 按行读取，存储在StringBuider对象response中
	        		BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
	        		StringBuilder response=new StringBuilder();
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
	    		    if(result.isNull("Error")){//正常
	    		    	result=result.getJSONObject("Result");
				    	if(result.isNull("Sensors")){
				    		Message msg = new Message();
	    		     		msg.what = 10;
	    		     		msg.obj="没有符合条件的数据";
	    		     		mhandler.sendMessage(msg);
	    		     		return;
				    	} 
				    	JSONObject joSensors=result.getJSONObject("Sensors");
				    	if((joSensors.isNull("Stations"))||(joSensors.isNull("Sensors"))){
				    		Message msg = new Message();
	    		     		msg.what = 10;
	    		     		msg.obj="没有符合条件的数据";
	    		     		mhandler.sendMessage(msg);
	    		     		return;
				    	} 
				    	JSONArray jaStations=joSensors.getJSONArray("Stations");

	    		    	JSONArray jaSensors=joSensors.getJSONArray("Sensors");
	    		    	JSONObject joDatas=result.getJSONObject("Datas");
	    		    	if(result.isNull("Datas")){
	    		    		Message msg = new Message();
	    		     		msg.what = 10;
	    		     		msg.obj="没有符合条件的数据";
	    		     		mhandler.sendMessage(msg);
	    		     		return;
	    		    	}
	    		    	jsArray=joDatas.getJSONArray("Err");
	    		    	
	    		    	if(jsArray.length()>0){
	    		    		alerArray=new AlertInfo[jsArray.length()];
	    		    		for(int i=0;i<jsArray.length();i++){
	    		    			AlertInfo ad=new AlertInfo();
	    		    			result=(JSONObject) jsArray.get(i);
	    		    			ad.setStationId(((JSONObject) (jaStations.get(0))).getLong("Id"));
	    		    			ad.setStationName(((JSONObject) (jaStations.get(0))).getString("Name"));
	    		    			ad.setSensorID(result.getLong("SensorId"));
	    		    			for(int j=0;j<jaSensors.length();j++){
		    		    			 if((((JSONObject) (jaSensors.get(j))).getLong("Id"))==ad.getSensorID()){
		    		    				 ad.setSensorName(((JSONObject) (jaSensors.get(j))).getString("Name"));
		    		    				 break;
		    		    			 }
		    		    		 }
	    		    			//ad.setSensorName(result.getString("SensorName"));
	    		    			ad.setDataId(result.getLong("Id"));
	    		    			if(!result.isNull("TmpValue")){
	    		    			ad.setTValue(result.getDouble("TmpValue"));}
	    		    			ad.setCollectTime(result.getString("Time"));
	    		    			ad.setLogText(result.getString("Value"));
	    		    			alerArray[i]=ad;//(AlertInfo) jsArray.get(i);
	    		    			ad=null;
	    		    		}
	    		    		Message msg = new Message();
	    		    		msg.what = 0;
	    		    		mhandler.sendMessage(msg);
    		    	 	}else{
    		    	 		Message msg=new Message();
    		    	 		msg.what=10;
    		    	 		mhandler.sendMessage(msg);
    		    	 	}
    		     	}else{//服务器返回错误信息
    		     		Message msg = new Message();
    		     		msg.what = 1;
    		     		msg.obj=errorcode;
    		     		mhandler.sendMessage(msg);
    		     	}
	        	}else if(iis.mark.equals("outtime")){//超时
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj="连接超时";
	        		mhandler.sendMessage(msg);
	        	}else if(iis.mark.equals("error")){//连接异常
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj="连接错误";
	        		mhandler.sendMessage(msg);
	        	}else if(iis.mark.equals("未授权")){//未授权认证
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj="操作失败，权限未认证";
	        		mhandler.sendMessage(msg);
	        	}else{//其他异常
	        		Message msg = new Message();
	        		msg.what = 1;
	        		msg.obj=iis.mark;
	        		mhandler.sendMessage(msg);
	        	}
	         	} catch (Exception e){
	         		//e.printStackTrace();
	         		Message msg = new Message();
	        		msg.what = 11;
	        		msg.obj="告警信息获取失败！";
	        		mhandler.sendMessage(msg);
	         	}
			}
		}).start();
	}
}
