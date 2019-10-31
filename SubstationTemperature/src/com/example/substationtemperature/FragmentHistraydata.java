package com.example.substationtemperature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DateTimePickDialogUtil.DateTimeControl;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.substationtemperature.adapter.ListItemAdapter_measuredata;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.MeasureData;
import com.example.substationtemperature.network.ClientService;

public class FragmentHistraydata extends Fragment implements OnClickListener{
	private Activity activity;
	private View view;
	private Spinner spn_histray_zhan,spn_histray_cld;
    private TextView txt_startDatepicker,txt_endDatepicker;
    private Calendar calendar = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final String currentDateTimeStr = sdf.format(calendar.getTime());
    private Button btn_query;
    private boolean isvisible=false;
    private ProgressDialog pd;
    private ListView list_data;
    //private List<Map<String,Object>>list_histraydata=new ArrayList<Map<String,Object>>();
    //private Map<String,Object>map;//=new HashMap<String,Object>();
    private ListItemAdapter_measuredata adapter=null;
    private List<MeasureData> datas; 
    private String[] zhanArray={},cldArray={}; 
	private ArrayAdapter<String> zhanAdapter,cldAdapter;
	private String info;
	private long stationid=0,sensorid=0;
	private String fromtime,endtime;
	//private int minutes;
	public static Handler mHandler;	//刷新列表数据
	private void refresh() {
		// TODO Auto-generated method stub
		loadData();
		adapter=null;
		adapter=new ListItemAdapter_measuredata(activity,datas);
		list_data.setAdapter(adapter);
	}
	//延时线程
	public class sleep_thread extends Thread{
		@Override
    	public void run(){
	    	while(!Thread.currentThread().isInterrupted()&& 1==1)
	    	{
				try {
					sleep_thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
	        	msg.what = Declare.STATUS_TIMEOUT;//read_od
	        	mHandler.sendMessage(msg);
	    	}
		}
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState){
		
		view = inflater.inflate(R.layout.fragment_histraydata, container, false);
		 mHandler= new Handler()
			{										
				//@SuppressLint("HandlerLeak")
				public void handleMessage(Message msg)										
			  	{											
				  	super.handleMessage(msg);			
				  	switch(msg.what)
				  	{
				  	case Declare.STATUS_ERROR:
				  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
				  		Declare.islogin=false;
				  		info=(String)msg.obj;
				  		Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
				  		break;
				  	case 20:
				  		cldAdapter=null;
				  		cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
				  		spn_histray_cld.setAdapter(cldAdapter);
				  		if((cldAdapter!=null)&&(isvisible))
							spn_histray_cld.setSelection(zhanAdapter.getPosition(Declare.celiangdian));
				  		//Toast.makeText(activity, "测量点信息获取成功",Toast.LENGTH_SHORT).show();
				  		break;
				  	case 21:
				  		info = (String) msg.obj;
				  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
				  		break;
				  	case 10:
				  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
				  		int count=0;
				  		if(ClientService.mdArray.length>0)
				  			count=ClientService.mdArray.length;
				  		refresh();
				  		Toast.makeText(activity, "数据获取成功，共"+count+"条",Toast.LENGTH_LONG).show();
				  		break;
				  	case 11:
				  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
				  		info = (String) msg.obj;
				  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
				  		break;
				  	case 80:
				  		if(ClientService.zhanArray!=null){
				        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
				        }else{
				        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
				        }
				  		spn_histray_zhan.setAdapter(zhanAdapter);
				  		if((zhanAdapter!=null)&&(isvisible))
							spn_histray_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
				  		break;
				  	}
			  	}
			};

		//if(getUserVisibleHint())
		//initViews();//不提前初始化，只在显示时才初始化，这样操作界面有一定的延时，读取一些控制参数而后刷新界面。尝试一下，看效果。
		
		return view;
	}
	
	private void loadData() {
		// TODO Auto-generated method stub
		datas=new ArrayList<MeasureData>();
		//Date time=new Date();
		if(ClientService.mdArray!=null){
			for(int i=0;i<ClientService.mdArray.length;i++){
				MeasureData currdata=new MeasureData();
				/*currdata.setSensorsName(spn_histray_cld.getSelectedItem().toString());//调试
				currdata.setTime((currentDateTimeStr));
				currdata.setValue(23.0f+i);*/
				currdata.setSensorsName(ClientService.mdArray[i].getSensorsName());//真正运行时在修改
				currdata.setTime(ClientService.mdArray[i].getTime());
				currdata.setValue(ClientService.mdArray[i].getValue());
				datas.add(currdata);
				currdata=null;
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity=activity;
	}
	private void initViews() {
		// TODO Auto-generated method stub
		spn_histray_zhan=(Spinner)view.findViewById(R.id.spn_histrayldata_zhandian);
		//spn_histray_zhan.setSelection(0, true);
		spn_histray_zhan.setOnItemSelectedListener(new zhanSelectedListener());
		//spn_histray_zhan.setSelection(0);
		if(ClientService.zhanArray!=null){
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
        }else{
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        }
		spn_histray_zhan.setPrompt("请选择站点名称");
		spn_histray_zhan.setAdapter(zhanAdapter);
		zhanAdapter.setDropDownViewResource(R.layout.item_dropdown);
		if((zhanAdapter!=null)&&(isvisible))
			spn_histray_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
		
		if(ClientService.cldArray!=null){
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
		}else{
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);
		}
		spn_histray_cld=(Spinner)view.findViewById(R.id.spn_histraydata_celiangdian);
		spn_histray_cld.setOnItemSelectedListener(new cldSelectedListener());
		spn_histray_cld.setPrompt("请选择测量点名称");
		spn_histray_cld.setAdapter(cldAdapter);
		cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
		if((cldAdapter!=null)&&(isvisible)){
			spn_histray_cld.setSelection(cldAdapter.getPosition(Declare.celiangdian));
		}
		
		txt_startDatepicker=(TextView)view.findViewById(R.id.histraydata_startDatePickerTV);
		txt_startDatepicker.setOnClickListener(this);
		txt_startDatepicker.setText(Declare.starttime);
		txt_endDatepicker=(TextView)view.findViewById(R.id.histraydata_endDatePickerTV);
		txt_endDatepicker.setOnClickListener(this);
		txt_endDatepicker.setText(Declare.endtime);
		btn_query=(Button)view.findViewById(R.id.histraydata_queryDataBtn);
		btn_query.setOnClickListener(this);
		//数据列表项
		list_data=(ListView)view.findViewById(R.id.listview_histraydata);
		list_data.setOnItemClickListener(new ItemClickListener());
		refresh();
		//list_histraydata.clear();
		//loadData();
    	//adapter=new SimpleAdapter(activity,list_histraydata,R.layout.list_item,
        //        new String[]{"zhandian","value","shijian"},new int[]{R.id.txt_list_zhandian,R.id.txt_list_value,R.id.txt_list_time});
    	//adapter=new ListItemAdapter_measuredata(activity,datas);
    	//list_data.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Calendar calendar = Calendar.getInstance();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateTimeStr = "";//sdf.format(calendar.getTime());
		switch(v.getId()){
		
		case R.id.histraydata_startDatePickerTV:
			currentDateTimeStr=(txt_startDatepicker.getText().toString().substring(0,4)+"年"+
					txt_startDatepicker.getText().toString().substring(5, 7)+"月"+
					txt_startDatepicker.getText().toString().substring(8,10)+"日"+
					txt_startDatepicker.getText().toString().substring(11,16));
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(activity,
					currentDateTimeStr);
			dateTimePicKDialog.dateTimePicKDialog(txt_startDatepicker);
			break;
		case R.id.histraydata_endDatePickerTV:
			currentDateTimeStr=(txt_endDatepicker.getText().toString().substring(0,4)+"年"+
					txt_endDatepicker.getText().toString().substring(5, 7)+"月"+
					txt_endDatepicker.getText().toString().substring(8,10)+"日"+
					txt_endDatepicker.getText().toString().substring(11,16));
			dateTimePicKDialog = new DateTimePickDialogUtil(activity,
					currentDateTimeStr);
			dateTimePicKDialog.dateTimePicKDialog(txt_endDatepicker);
			break;
		case R.id.histraydata_queryDataBtn:
			if(!txt_startDatepicker.getText().toString().equals("")){
				fromtime=txt_startDatepicker.getText().toString();
			}else{
				Toast.makeText(activity, "请选择起始时间", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!txt_endDatepicker.getText().toString().equals("")){
				endtime=txt_endDatepicker.getText().toString();
			}else{
				Toast.makeText(activity, "请选择截止时间", Toast.LENGTH_SHORT).show();
				return;
			}
			Declare.starttime=txt_startDatepicker.getText().toString();
			Declare.endtime=txt_endDatepicker.getText().toString();
			//minutes = DateTimeControl.minutesDiff(DateTimeControl.GetItemDate(fromtime), DateTimeControl.GetItemDate(endtime));
			//Toast.makeText(activity, String.valueOf(minutes), 3000).show();
			if(Declare.islogin){
				ClientService.getHistories(sensorid, fromtime,endtime);
				pd=ProgressDialog.show(activity, "查询中...", "正在查询",true,false);
			}else{
				Toast.makeText(activity, "用户未注册",Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	//站点下拉框选择项
	private class zhanSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
			if(ClientService.stArray!=null){
				stationid=ClientService.stArray[position].getStationID();
			}
			if((stationid!=Declare.stationid)&&(Declare.islogin)&&(isvisible)){
				ClientService.getSensors(stationid);
				Declare.stationid=stationid;
				Declare.zhan=spn_histray_zhan.getSelectedItem().toString();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	//测量点选择项操作
	private class cldSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			//Declare.celiangdian=spn_histray_cld.getSelectedItem().toString();
			if(ClientService.ssArray!=null){
				sensorid=ClientService.ssArray[position].getSensorID();
			}
			if((sensorid!=Declare.sensorid)&&(Declare.islogin)&&(isvisible)){
				//ClientService.getHistories(sensorid, fromtime, endtime);//自动获取数据
				Declare.sensorid=sensorid;
				Declare.celiangdian=spn_histray_cld.getSelectedItem().toString();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	private final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //需要把表号等参数传递过去，显示此表号对应的数据。2016-03-12h注。
			MeasureData item = datas.get(position);
			//Intent intent=new Intent(activity, ManageDataDetailActivity.class);
			//Bundle bd=new Bundle();
			Declare.zhan=spn_histray_zhan.getSelectedItem().toString();
			Declare.celiangdian=item.getSensorsName();
			Declare.time=(Date) DateTimeControl.strToDateLong(item.getTime());
			//intent.putExtra("ameterno", bd);
			//startActivity(intent);
			//startActivity(new Intent(ManageDataActivity.this, ManageDataDetailActivity.class));
			// activity切换过场动画
			//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			MainActivity.selectNavigationItem(4);
    		MainActivity.mViewPager.setCurrentItem(4);
		}
	}
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    	isvisible=isVisibleToUser;
        if(isVisibleToUser){
        	if (view!=null){
	        	initViews();
	        }
	        if(!Declare.islogin){
	        	FragmentIndex.comfir_display();
			}
        }
    }
	@Override
	public void onResume(){
		super.onResume();
		//初始化视图
        if(getUserVisibleHint()){
        initViews();
        }
	}
	
	


}
