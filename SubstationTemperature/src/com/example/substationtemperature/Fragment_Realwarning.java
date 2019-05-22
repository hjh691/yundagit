package com.example.substationtemperature;

import java.util.ArrayList;

import com.example.substationtemperature.adapter.ListItemAdapter_xinxi;
import com.example.substationtemperature.adapter.ListitemAdapter_realalert;
import com.example.substationtemperature.base.AlertInfo;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.network.ClientService;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment_Realwarning extends Fragment implements OnClickListener{
	
	private Activity activity;
	private View view;
	public static Handler mhandler;
	private ListView list_realalert;
	private ListitemAdapter_realalert mAdapter=null;
	private ArrayList<AlertInfo> realalert_xinxis;
	public static  AlertInfo[] alerArray;
	private boolean sleeploop=false;
	private sleep_thread thread;
	private TextView txt_count;
	public  Fragment_Realwarning(){
		
	}
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity=activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle saveInstanceState){
		view=inflater.inflate(R.layout.fragment_realwarning,container, false);
		mhandler = new Handler()
		{										
			  public void handleMessage(Message msg)										
			  {								
				switch(msg.what){
				case 0:
					initview();
					break;
				}
			}
		};
		initview();
		return view;
	}
	private void initview() {
		// TODO Auto-generated method stub
		txt_count=(TextView)view.findViewById(R.id.txt_realwarning_count);
		loadData();
		mAdapter=new ListitemAdapter_realalert(activity,realalert_xinxis);
		list_realalert=(ListView)view.findViewById(R.id.listview_realalert);
		list_realalert.setAdapter(mAdapter);
	}
	private void loadData() {
		// TODO Auto-generated method stub
		realalert_xinxis = new ArrayList<AlertInfo>();
		//Date time=new Date();
		if(ClientService.alerArray!=null)
		for(int i=0;i<ClientService.alerArray.size();i++){
			AlertInfo currdata=new AlertInfo();
			/*currdata.setStationName("0001");
			currdata.setCollectTime(currentDateTimeStr);
			currdata.setTValue(Float.valueOf("23."+i));
			currdata.setLogText("这里显示告警详情");*/
			currdata.setStationName(ClientService.alerArray.get(i).getStationName());
			currdata.setSensorName(ClientService.alerArray.get(i).getSensorName());
			currdata.setCollectTime(ClientService.alerArray.get(i).getCollectTime());
			currdata.setTValue(ClientService.alerArray.get(i).getTValue());
			currdata.setLogText(ClientService.alerArray.get(i).getLogText());
			realalert_xinxis.add(currdata);
			currdata=null;
		}
		txt_count.setText(ClientService.alerArray.size()+"条");
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
	    		//sleeploop=true;
	        	//thread=new sleep_thread();
	        	//thread.start();
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	//延时定时线程
		public class sleep_thread extends Thread{
			@Override
	    	public void run(){
		    	long i=1;
				while(!Thread.currentThread().isInterrupted()&& sleeploop)
		    	{
					try {
						sleep_thread.sleep(10000);
						i++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//if((i % 20)==0){
					Message msg = new Message();
		        	msg.what = 0;//read_od
		        	mhandler.sendMessage(msg);
		        	//}
		        	
		    	}
			}
	    }
}
