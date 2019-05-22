package com.lx.checkameterclient;

import java.text.DecimalFormat;

import com.lx.checkameter.base.BaseActivity;
import com.lx.checkameter.socket.Toasts;
import com.lx.checkameter.socket.mSocketClient;
import com.lx.checkameterclient.view.SegmentControl;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DebugDeviceActivity extends BaseActivity {
	private Thread mThreadread=null;
	private boolean read_flag=false;
	
	TextView ua,ub,uc,ia,ib,ic,ja,jb,jc,pa,pb,pc;
	RadioButton ua_rb,ub_rb,uc_rb,ia_rb,ib_rb,ic_rb,ja_rb,jb_rb,jc_rb,dw1_rb,dw2_rb,dw3_rb;
	RadioGroup rg1,rg2;
	Button but_dy,but_dl;
	LinearLayout layout_level;
	ImageButton ibut_tj4u,ibut_tj4,ibut_tj3u,ibut_tj3,ibut_tj2u,ibut_tj2,ibut_tj1u,ibut_tj1;
	int phase_sel_flag,level_sel_flag;//项目及档位选择序号
	int tjstep_sel_flag;//调节步长选择序号
	private DecimalFormat myformat1,myformat2,myformat3;
	private int sum1;
	Spinner spinner1,spinner2;
	int position1,position2;
	private SegmentControl segmentcontrol,segmentcontrol2;
	private int main_pt=-1;
	private ImageView connectionStateIV=null;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		main_pt=Declare.send_flag;
		this.setContentView(R.layout.activity_debug_device);
		TextView titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText("调试设备");
		// 显示设备连接状态图标
		connectionStateIV= (ImageView) findViewById(R.id.titleBarConnectionStateIV);
		connectionStateIV.setVisibility(View.VISIBLE);
		// 显示设备连接状态图标
		ImageView connectionStateIV = (ImageView) findViewById(R.id.titleBarConnectionStateIV);
		connectionStateIV.setVisibility(View.VISIBLE);
        spinner2 = (Spinner) findViewById(R.id.spi4);
      //===============脉冲倍率==========================
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            if(Declare.Clientisconnect==true && Declare.rec_err==false){
        		String str;
        		str=mSocketClient.BytesToHexString(mSocketClient.shorttobytes((short)(position+1)));
  				mSocketClient.send_debug_msg("12",str);
  				DisplayToast("脉冲倍率设置成功！！");
  				save_config();
  				position2=position;
        	}else{
        		DisplayToast("网络故障，请检查wifi及socket链接！！");
        		spinner2.setSelection(position2, false);
        	}
          }
          @Override
          //===============脉冲倍率==========================
          public void onNothingSelected(AdapterView<?> view) {
          }
         });
        segmentcontrol=(SegmentControl)findViewById(R.id.segmentcontrol_xm);
        segmentcontrol2=(SegmentControl)findViewById(R.id.segmentcontrol_dw);
        inti();
        if(mThreadread==null){
        	read_flag=true;
        	mThreadread=new mThreadread();
        	mThreadread.start();
        }
        Declare.send_flag=0;
        Declare.Circle=true;
	}
	
	//==初始化
	public void inti(){
		phase_sel_flag=0;
		level_sel_flag=0;
		ua=(TextView)findViewById(R.id.ua);
		ub=(TextView)findViewById(R.id.ub);
		uc=(TextView)findViewById(R.id.uc);
		ia=(TextView)findViewById(R.id.ia);
		ib=(TextView)findViewById(R.id.ib);
		ic=(TextView)findViewById(R.id.ic);
		ja=(TextView)findViewById(R.id.ja);
		jb=(TextView)findViewById(R.id.jb);
		jc=(TextView)findViewById(R.id.jc);
		but_dy=(Button)findViewById(R.id.but_dy);
		but_dl=(Button)findViewById(R.id.but_dl);
		//按钮侦听
		but_dy.setOnClickListener(new but_click());
 		but_dl.setOnClickListener(new but_click());
        
        ibut_tj4u=(ImageButton)findViewById(R.id.ibut_tj4u);
        ibut_tj4=(ImageButton)findViewById(R.id.ibut_tj4);
        ibut_tj3u=(ImageButton)findViewById(R.id.ibut_tj3u);
        ibut_tj3=(ImageButton)findViewById(R.id.ibut_tj3);
        ibut_tj2u=(ImageButton)findViewById(R.id.ibut_tj2u);
        ibut_tj2=(ImageButton)findViewById(R.id.ibut_tj2);
        ibut_tj1u=(ImageButton)findViewById(R.id.ibut_tj1u);
        ibut_tj1=(ImageButton)findViewById(R.id.ibut_tj1);
        //调节按钮侦听
        ibut_tj4u.setOnClickListener(new imbutton_click());
        ibut_tj4.setOnClickListener(new imbutton_click());
        ibut_tj3u.setOnClickListener(new imbutton_click());
        ibut_tj3.setOnClickListener(new imbutton_click());
        ibut_tj2u.setOnClickListener(new imbutton_click());
        ibut_tj2.setOnClickListener(new imbutton_click());
        ibut_tj1u.setOnClickListener(new imbutton_click());
        ibut_tj1.setOnClickListener(new imbutton_click());
        set_level_rb_unable();
        
		segmentcontrol.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		        //处理点击标签的事件
		    	switch(index){               
                 case 0:  
                	 phase_sel_flag=0;
                     //dw1_rb.setChecked(true);
                	 segmentcontrol2.setSelectedIndex(0);
                     set_level_rb_unable();
                	 break;
                  //-----------------------------------------------
                 case 1:
                	 phase_sel_flag=1;
                	 //dw1_rb.setChecked(true);
                	 segmentcontrol2.setSelectedIndex(0);
                	 set_level_rb_unable();
                	 break;
                  //-----------------------------------------------
                 case 2:  
                	 phase_sel_flag=2;
                	 //dw1_rb.setChecked(true);
                	 segmentcontrol2.setSelectedIndex(0);
                	 set_level_rb_unable();
                	 break;
                  //-----------------------------------------------                 
                 case 3:  
                	 phase_sel_flag=3;
                	 set_level_rb_enable();
                	 break;
                  //-----------------------------------------------
                 case 4:  
                	 phase_sel_flag=4;
                	 set_level_rb_enable();
                	 break;
                  //-----------------------------------------------
                 case 5:  
                	 phase_sel_flag=5;
                	 set_level_rb_enable();
                	 break;
                 //----------------------------------------- 
                 case 6:  
                	 phase_sel_flag=6;
                	 set_level_rb_enable();
                	 break;
                //-------------------------------------------------
                 case 7:  
                	 phase_sel_flag=7;
                	 set_level_rb_enable();
                	 break;
                //--------------------------------------------------
                 case 8:  
                	 phase_sel_flag=8;
                	 set_level_rb_enable();
                break;
                }
		    } 
		});
		
		segmentcontrol2.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		    	switch(index){               
                 case 0:  
                	 level_sel_flag=0;
                	 break;
                  //-----------------------------------------------
                 case 1:
                	 level_sel_flag=1;
                	 break;
                  //-----------------------------------------------
                 case 2:  
                	 level_sel_flag=2;
                	 break;
                }
		    }
		});
		myformat1= new DecimalFormat("0.000");
		myformat2= new DecimalFormat("0.0000");
		myformat3= new DecimalFormat("0.00"); 
	}
		
	public void set_level_rb_enable(){
		//dw1_rb.setEnabled(true);
		//dw2_rb.setEnabled(true);
		//dw3_rb.setEnabled(true);
		segmentcontrol2.setEnabled(true);
	}
	public void set_level_rb_unable(){
		//dw1_rb.setEnabled(false);
		//dw2_rb.setEnabled(false);
		//dw3_rb.setEnabled(false);
		segmentcontrol2.setEnabled(false);
	}
	//保存按钮监听
	public class but_click implements OnClickListener {               
    	@Override            
    	public void onClick(View v) { 
    		switch (v.getId()){
			//电压调节参数保存
    		case R.id.but_dy:
    			mSocketClient.send_debug_msg("55", "0000");
    			break;
    		//电流调节参数保存
    		case R.id.but_dl:
    			mSocketClient.send_debug_msg("66", "0000");
    			break;
    		}
    	}
	}
//调节按钮监听
	public class imbutton_click implements OnClickListener {               
    	@Override            
    	public void onClick(View v) { 
    		switch (v.getId()){
			//====+1%===========
    		case R.id.ibut_tj4u:
    			tjstep_sel_flag=0;   
    			break;
    		//=====-1%==========
    		case R.id.ibut_tj4:
    			tjstep_sel_flag=1;
    			break;
    		//====+0.1%===========
    		case R.id.ibut_tj3u:
    			tjstep_sel_flag=2;	    	       	       	 
    			break;
    		//=====-0.1%==========
    		case R.id.ibut_tj3:
    			tjstep_sel_flag=3;
    			break;
    			//====+0.01%===========
    		case R.id.ibut_tj2u:
    			tjstep_sel_flag=4;	    	       	       	 
    			break;
    		//=====-0.01%==========
    		case R.id.ibut_tj2:
    			tjstep_sel_flag=5;
    			break;
    		//====+0.001%===========
    		case R.id.ibut_tj1u:
    			tjstep_sel_flag=6;	    	       	 
    			break;
    		//=====-0.001%==========
    		case R.id.ibut_tj1:
    			
    			tjstep_sel_flag=7;
    			break;
    		}
    		order_sel(phase_sel_flag,level_sel_flag);
			tj_para_sel(tjstep_sel_flag);
			mSocketClient.send_debug_msg(Declare.str_order, Declare.str_para);
    	}
	}
	//========根据项目及档位选择所需命令字===============
	public void order_sel(int phase_flag,int level_flag){
			
		switch(phase_flag){
		//============ua============
		case 0:
			Declare.str_order="1D";
		break;
		//============ub============
		case 1:
			Declare.str_order="1E";
		break;
		//============uc============
		case 2:
			Declare.str_order="1F";
		break;
		//============ia============
		case 3:
			switch(level_flag){
			//========100%==========
			case 0:
				Declare.str_order="20";
				break;
			//========10%==========
			case 1:
				Declare.str_order="23";
				break;
			//========5%==========
			case 2:
				Declare.str_order="26";
				break;
			}
		break;
		//============ib============
		case 4:
			switch(level_flag){
			//========100%==========
			case 0:
				Declare.str_order="21";
				break;
			//========10%==========
			case 1:
				Declare.str_order="24";
				break;
			//========5%==========
			case 2:
				Declare.str_order="27";
				break;
		}
		break;
		//============ic============
		case 5:
			switch(level_flag){
			//========100%==========
			case 0:
				Declare.str_order="22";
				break;
			//========10%==========
			case 1:
				Declare.str_order="25";
				break;
			//========5%==========
			case 2:
				Declare.str_order="28";
				break;
			}
		break;
		//============ja============
		case 6:
			switch(level_flag){
			//========100%==========
			case 0:
				Declare.str_order="29";
				break;
			//========10%==========
			case 1:
				Declare.str_order="2C";
				break;
			//========5%==========
			case 2:
				Declare.str_order="2F";
				break;
			}
		break;
		//============jb============
		case 7:
			switch(level_flag){
			//========100%==========
			case 0:
				Declare.str_order="2A";
				break;
			//========10%==========
			case 1:
				Declare.str_order="2D";
				break;
			//========5%==========
			case 2:
				Declare.str_order="30";
				break;
			}
		break;
		//============jC============
		case 8:
			switch(level_flag){
			//========100%==========
				case 0:
					Declare.str_order="2B";
					break;
				//========10%==========
				case 1:
					Declare.str_order="2E";
					break;
				//========5%==========
				case 2:
					Declare.str_order="31";
					break;
			}
		break;
		}
	}
	//=======根据调节按钮选择下发的参数===============
	public void tj_para_sel(int tj_flag){
		switch(tj_flag){
		//======+10%===========
		case 0:
			Declare.str_para="0100";
			break;
			//======-10%===========
		case 1:
			Declare.str_para="F100";
			break;
			//======+1%===========
		case 2:
			Declare.str_para="0200";
			break;
			//======-1%===========
		case 3:
			Declare.str_para="F200";
			break;
			//======+0.1%===========
		case 4:
			Declare.str_para="0300";
			break;
			//======-0.1%===========
		case 5:
			Declare.str_para="F300";
			break;
			//======+0.01%===========
		case 6:
			Declare.str_para="0400";
			break;
			//======-0.01%===========
		case 7:
			Declare.str_para="F400";
			break;
		}
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
//					     this.interrupt();
//					     DisplayToast("线程关闭"); 
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
//		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg)								
		{											
			super.handleMessage(msg);			
				if(msg.what == 0)
				{
//		while(!Declare.receive_flag){
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
		 //用于测试
		float val_ia,val_ib,val_ic;
		val_ia=(float)(Declare.data_facs[1])/32768;
		val_ib=(float)(Declare.data_facs[3])/32768;
		val_ic=(float)(Declare.data_facs[5])/32768;
		ua.setText(myformat1.format((float)(Declare.data_facs[0])/32768));
//						ua.invalidate();
		ia.setText(cur_fromat(val_ia));
//						ub.invalidate();
		ub.setText(myformat1.format((float)(Declare.data_facs[2])/32768));
		ib.setText(cur_fromat(val_ib));
		uc.setText(myformat1.format((float)(Declare.data_facs[4])/32768));
		ic.setText(cur_fromat(val_ic));
		
		//pa.setText(myformat1.format((float)(Declare.data_facs[6])/1024));
		//pb.setText(myformat1.format((float)(Declare.data_facs[7])/1024));
		//pc.setText(myformat1.format((float)(Declare.data_facs[8])/1024));
		ja.setText(myformat1.format((float)(Declare.data_facs[18])/8192));
		jb.setText(myformat1.format((float)(Declare.data_facs[19])/8192));
		jc.setText(myformat1.format((float)(Declare.data_facs[20])/8192));
		switch(phase_sel_flag){
		//======ia=========
		case 3:
			break;
		//======ib=========
		case 4:
			break;
		//======ib=========
		case 5:
			break;
			//======ja=========
		case 6:
			break;
		//======jb=========
		case 7:
			break;
		//======jc=========
		case 8:
			break;
		}
		//启动循环采样后，接收超时清零
		if(Declare.rec_overtime==true){
			for(int i=0;i<Declare.data_facs.length;i++){
				Declare.data_facs[i]=0;
			}
		}
		//检测网络是否正常，后清零
		else if(Declare.Clientisconnect==false || Declare.rec_err==true)
		{
			if(sum1>=4){
			for(int i=0;i<Declare.data_facs.length;i++){
				Declare.data_facs[i]=0;
			}
			sum1=0;
			}else{sum1++;}
		}else{
			sum1=0;
		}
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
	
	//==保存电压量程和脉冲倍率设置
	public void save_config() {
		SharedPreferences settings = getSharedPreferences("config", 0);
		SharedPreferences.Editor editor = settings.edit(); 			 	
		//editor.putString("dylc",String.valueOf(spinner1.getSelectedItemPosition()));
		editor.putString("mcbl",String.valueOf(spinner2.getSelectedItemPosition()));
		editor.commit();
	}

			 		      
	//读取电压量程和脉冲倍率设置
	private void get_config() {	
		SharedPreferences settings = getSharedPreferences("config",  0);
		int P1,P2;	
		//P1=Integer.parseInt(settings.getString("dylc", "0"));
		//spinner1.setSelection(P1,true);
		//position1=P1;
		P2=Integer.parseInt(settings.getString("mcbl", "0"));
		spinner2.setSelection(P2,true);
		position2=P2;
	}
	
	public void DisplayToast(String msg){
//	    Toast toast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
	    //设置toast显示位置
//	    toast.setGravity(Gravity.TOP, 0, 220);
//	    toast.show();
		Toasts.toast(this, msg, Toast.LENGTH_SHORT,Gravity.TOP, 0, 220);
    }

	/**
	 * 这里覆盖父类Activity中的onBackPressed()方法
	 * 左侧返回按钮的响应事件已经在布局文件的onClick属性中声明了，为父类BaseActivity中声明的doBack()方法
	 * 这里覆盖的目的是增加过场动画
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Declare.send_flag=main_pt;
		read_flag=false;
		mThreadread.interrupt();
		mThreadread=null;
	}
}
