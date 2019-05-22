package com.lx.checkameterclient;

import java.text.DecimalFormat;

import com.lx.checkameter.base.BaseActivity;
import com.lx.checkameter.socket.mSocketClient;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TestRatioActivity extends BaseActivity {

	private Thread mThreadread=null;
	private boolean read_flag=false;
	TextView dllc1,dlxs1,dlxs2,bbxs,bcxs,jcxs,test;
	private DecimalFormat myformat1,myformat2,myformat3;
	private int sum1=0;
	private int main_pt=-1;
	private ImageView connectionStateIV=null;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_test_ratio);
		main_pt=Declare.send_flag;

		TextView titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText("变比测试");

		// 显示设备连接状态图标
		connectionStateIV= (ImageView) findViewById(R.id.titleBarConnectionStateIV);
		connectionStateIV.setVisibility(View.VISIBLE);
		
		inti();
		
		if(mThreadread==null){
        	read_flag=true;
        	mThreadread=new mThreadread();
        	mThreadread.start();
        }
        
        Declare.send_flag=4;
        Declare.Circle=true;		
        mSocketClient.SendClientmsg(Declare.Circle);
	}
	
	public void inti(){
		sum1=0;
		for(int i=0;i<Declare.data_ctcs.length;i++){
			Declare.data_ctcs[i]=0;
			}
		Declare.txlj_ok_flag=false;Declare.txlj_erro_flag=false;//初始化标志位便于toast信息弹出
		dllc1=(TextView)findViewById(R.id.dllc1);
		dlxs1=(TextView)findViewById(R.id.dlxs1);
		dlxs2=(TextView)findViewById(R.id.dlxs2);
		bbxs=(TextView)findViewById(R.id.bbxs);
		bcxs=(TextView)findViewById(R.id.bcxs);
		jcxs=(TextView)findViewById(R.id.jcxs);

		int dllc_index=Integer.parseInt(getdllc());
		String dllc_str=null;
		switch(dllc_index){
		case 0:
			dllc_str="Q5A";
			break;
		case 1:
			dllc_str="Q50A";
			break;
		case 2:
			dllc_str="Q100A";
			break;
		case 3:
			dllc_str="Q500A";
			break;
		}
		dllc1.setText(dllc_str);
		myformat1= new DecimalFormat("0.000");
		myformat2= new DecimalFormat("0.0000");
		myformat3= new DecimalFormat("0.00");
	}
	
	public void onDestroy() {
		super.onDestroy();
		Declare.send_flag=main_pt;
		read_flag=false;
		mThreadread.interrupt();
		mThreadread=null;
		//mSocketClient.StopClientmsg();
		Declare.receive_flag=false;
		Declare.connect_num=0;
        Declare.connect_num1=0;
        Declare.rec_overtime=false;
	}
	
	public class mThreadread extends Thread{
		@SuppressLint("HandlerLeak")
		@Override
		 public void run() {
		 // TODO Auto-generated method stub
			 while(!Thread.currentThread().isInterrupted() && read_flag)
		    	{		    	
		    	try {
		    	  // 每隔1000毫秒触发
		    		Thread.sleep(1000);
		    	    } 
		    	catch (InterruptedException e) {
		    	     System.err.println("InterruptedException！线程关闭");
//		    	     this.interrupt();
//	        	     DisplayToast("线程关闭"); 
		    	     read_flag=false;
		    	     this.interrupt();
		    	     return;
		    		}
		    	Message msg = new Message();
		    	msg.what = 0;
		    	mHandler_read.sendMessage(msg);
		    	 }
		 }
	}
	Handler mHandler_read = new Handler()
		{										
//			@SuppressLint("HandlerLeak")
			public void handleMessage(Message msg)										
			{											
				super.handleMessage(msg);			
				if(msg.what == 0)
				{
//					while(!Declare.receive_flag){
					while(read_flag){
					if (Declare.Clientisconnect){
						connectionStateIV.setImageResource(R.drawable.state_connected);
					}else{
						connectionStateIV.setImageResource(R.drawable.state_connection_interrupt);
					}
					display();
					break;
				}
			}
		}									
	};
		 
	 public void display(){
//		if(Declare.data_facs!=null){
//		test.setText("1111");
			 dlxs1.setText(cur_fromat((float)(Declare.data_ctcs[0])/32768));		
			 dlxs2.setText(cur_fromat((float)(Declare.data_ctcs[1])/32768));
			 bbxs.setText(myformat1.format((float)(Declare.data_ctcs[2])));
			 bcxs.setText(myformat1.format((float)(Declare.data_ctcs[3])/1024));
			 jcxs.setText(myformat1.format((float)(Declare.data_ctcs[4])/64));
			//启动循环采样后，接收超时清零
				if(Declare.rec_overtime==true){
					for(int i=0;i<Declare.data_ctcs.length;i++){
						Declare.data_ctcs[i]=0;
						}
				}
				//检测网络是否正常，后清零
				else if(Declare.Clientisconnect==false || Declare.rec_err==true){
					if(sum1>=2){
						for(int i=0;i<Declare.data_ctcs.length;i++){
						Declare.data_ctcs[i]=0;
						}
						sum1=0;
					}
					else{sum1++;}
				}
				else{sum1=0;}
//			ua.invalidate();
	}
	//返回设置的电表制式
		private String getdllc() {	
			String str;	
			SharedPreferences settings = getSharedPreferences("config",  0); 
			str=settings.getString("dllc", "0");
			return str;			        		 		             
		}
	 //处理电流显示
	public String cur_fromat(float current){
		String str1=null;
		int cur=0;
		cur=(int)current;
		
	    if(cur >=0 && cur<10){
	        //显示4位小数
	    	str1=myformat2.format(current);
	    }
	    //显示3位小数
	    else if(cur>=10 && cur<100){
	    	 str1=myformat1.format(current);
	    }
	    else if(cur>=100){
	    	 str1=myformat3.format(current);
	    	
	    }
		return str1;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

}
