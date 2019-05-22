package com.lx.checkameterclient.fragment;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.lx.checkameter.socket.mSocketClient;
import com.lx.checkameterclient.Declare;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.view.confirm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lx.checkameterclient.ManageDataDetailActivity;

public class CAVoltAmphereTesting extends Fragment {
    //private View view=null;
    private static TextView ua,ub,uc,ia,ib,ic,ja,jb,jc,hz,sin,cos,pa,pb,pc,ph,qa,qb,qc,qh,sa,sb,sc,sh,zs,temp;
    private static TextView unit_p,unit_q,unit_s;
    private static TextView lab_ua,lab_uc,lab_ia,lab_ic,lab_ja,lab_jc,lab_pa,lab_pc;
    private static DecimalFormat myformat1,myformat2,myformat3;
    private static int sum1;
    private Thread mThreadread=null;
	private static boolean read_flag=false;
	private int loop_time=1000;//界面刷新时间间隔
	static SQLiteDatabase sqldb;
	private static Dialog dialog;
	private static Activity activity;
	public static Button btn_save;
	private static File file = new File("/sdcard/bdlx/sxxy.db");// 数据库文件路径
	private static LinearLayout layout_ub,layout_ib,layout_jb,layout_pb;
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity=activity;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sum1=0;
		myformat1= new DecimalFormat("0.000");
		myformat2= new DecimalFormat("0.0000");
		myformat3= new DecimalFormat("0.00");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ca_volt_amphere_testing_fragment, null);
		//打开创建一个数据库        
        
		sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
		ua=(TextView)view.findViewById(R.id.voltATV);
		ub=(TextView)view.findViewById(R.id.voltBTV);
		uc=(TextView)view.findViewById(R.id.voltCTV);
		ia=(TextView)view.findViewById(R.id.amphereATV);
		ib=(TextView)view.findViewById(R.id.amphereBTV);
		ic=(TextView)view.findViewById(R.id.amphereCTV);
		ja=(TextView)view.findViewById(R.id.angleATV);
		jb=(TextView)view.findViewById(R.id.angleBTV);
		jc=(TextView)view.findViewById(R.id.angleCTV);
		hz=(TextView)view.findViewById(R.id.ohterHzTV);
		sin=(TextView)view.findViewById(R.id.otherSinTV);
		cos=(TextView)view.findViewById(R.id.otherCosTV);
		pa=(TextView)view.findViewById(R.id.text_gxjg);
		pb=(TextView)view.findViewById(R.id.text_rxjg);
		pc=(TextView)view.findViewById(R.id.activePowerCTV);
		ph=(TextView)view.findViewById(R.id.activePowerSumTV);
		qa=(TextView)view.findViewById(R.id.reactivePowerATV);
		qb=(TextView)view.findViewById(R.id.reactivePowerBTV);
		qc=(TextView)view.findViewById(R.id.reactivePowerCTV);
		qh=(TextView)view.findViewById(R.id.reactivePowerSumTV);
		sa=(TextView)view.findViewById(R.id.shiZaiPowerATV);
		sb=(TextView)view.findViewById(R.id.shiZaiPowerBTV);
		sc=(TextView)view.findViewById(R.id.shiZaiPowerCTV);
		sh=(TextView)view.findViewById(R.id.shiZaiPowerSumTV);
		zs=(TextView)view.findViewById(R.id.zs);//电表制式
		//temp=(TextView)view.findViewById(R.id.temp);
		int zs_index=Integer.parseInt(getdbzs());
		String zs_str=null;
		switch(zs_index){
		case 0:
			zs_str="三相四线有功";
			break;
		case 1:
			zs_str="三相四线无功";
			break;
		case 2:
			zs_str="三相三线有功";
			break;
		case 3:
			zs_str="三相三线无功";
			break;
		}
 		zs.setText(zs_str);//获得电表制式
 		btn_save=(Button)view.findViewById(R.id.btn_savefa);
 		btn_save.setOnClickListener(new btn_click());
 		unit_p=(TextView)view.findViewById(R.id.unit_p);
 		unit_q=(TextView)view.findViewById(R.id.unit_q);
 		unit_s=(TextView)view.findViewById(R.id.unit_s);
		if(mThreadread==null){
        	read_flag=true;
        	mThreadread=new mThreadread();
        	mThreadread.start();                
        	//Declare.send_flag=0;
        	Declare.Circle=true;
        	System.out.println("开启");		
        	mSocketClient.SendClientmsg(Declare.Circle);
        }
		/*
		 *============ 动态调整布局====================
		 */
		layout_ub=(LinearLayout)view.findViewById(R.id.layout_ub);
		layout_ib=(LinearLayout)view.findViewById(R.id.layout_ib);
		layout_jb=(LinearLayout)view.findViewById(R.id.layout_jb);
		layout_pb=(LinearLayout)view.findViewById(R.id.layout_pb);
		//LinearLayout layout_qb=(LinearLayout)findViewById(R.id.layout_qb);
		//LinearLayout layout_sb=(LinearLayout)findViewById(R.id.layout_sb);
		lab_ua=(TextView)view.findViewById(R.id.lab_ua);
		lab_uc=(TextView)view.findViewById(R.id.lab_uc);
		lab_ia=(TextView)view.findViewById(R.id.lab_ia);
		lab_ic=(TextView)view.findViewById(R.id.lab_ic);
		lab_ja=(TextView)view.findViewById(R.id.lab_ja);
		lab_jc=(TextView)view.findViewById(R.id.lab_jc);
		lab_pa=(TextView)view.findViewById(R.id.lab_pa);
		lab_pc=(TextView)view.findViewById(R.id.lab_pc);
		//TextView lab_qa=(TextView)findViewById(R.id.lab_qa);
		//TextView lab_qc=(TextView)findViewById(R.id.lab_qc);
		//TextView lab_sa=(TextView)findViewById(R.id.lab_sa);
		//TextView lab_sc=(TextView)findViewById(R.id.lab_sc);
		if(zs_index==0 || zs_index==1){
			layout_ub.setVisibility(0);layout_ib.setVisibility(0);layout_jb.setVisibility(0);
			layout_pb.setVisibility(0);//layout_qb.setVisibility(0);layout_sb.setVisibility(0);
			lab_ua.setText("A相");lab_ia.setText("A相");lab_ja.setText("A相");//lab_ja.setTextSize(24/size_value);
			lab_pa.setText("A相");//lab_qa.setText("  A相   ");lab_sa.setText("  A相   ");
			lab_uc.setText("C相");lab_ic.setText("C相");lab_jc.setText("C相");//lab_jc.setTextSize(24/size_value);
			lab_pc.setText("C相");//lab_qc.setText("  C相   ");lab_sc.setText("  C相   ");
		}
		if(zs_index==2 || zs_index==3){
			layout_ub.setVisibility(4);layout_ib.setVisibility(4);layout_jb.setVisibility(4);
			layout_pb.setVisibility(4);//layout_qb.setVisibility(4);layout_sb.setVisibility(4);
			lab_ua.setText("Uab");lab_ia.setText("Ia");lab_ja.setText("UabIa");//lab_ja.setTextSize(22/size_value);
			lab_pa.setText("AB相");//lab_qa.setText("  Qab ");lab_sa.setText("  Sab ");
			lab_uc.setText("Ucb");lab_ic.setText("Ic");lab_jc.setText("UcbIc");//lab_jc.setTextSize(22/size_value);
			lab_pc.setText("CB相");//lab_qc.setText("  Qcb ");lab_sc.setText("  Scb ");
		}
		return view;
	}
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//mAdapter = null;
		//read_flag=false;//Activity销毁时终止刷新线程
		if(mThreadread!=null){
			read_flag=false;//Activity销毁时终止刷新线程
			mThreadread.interrupt();
			mThreadread=null;
			//mSocketClient.StopClientmsg();//
			Declare.receive_flag=false;
			System.out.println("关闭");
			}
				Declare.connect_num=0;
		        Declare.connect_num1=0;
		        Declare.rec_overtime=false;
			
			sqldb.close();
			for(int i=0;i<Declare.data_facs.length;i++){
				Declare.data_facs[i]=0;
			}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
	}
	//2016-03-10拷贝于原320.用于刷新显示界面数据。
	public class mThreadread extends Thread{
		 @SuppressLint("HandlerLeak")
		@Override
		 public void run() {
		 // TODO Auto-generated method stub
			 while(!Thread.currentThread().isInterrupted() && read_flag)
		    	{		    	
		    	try {
		    	  // 每隔1000毫秒触发
		    		Thread.sleep(loop_time);
		    	    } 
		    	catch (InterruptedException e) {
		    	     System.err.println("InterruptedException！线程关闭");
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
	public static Handler mHandler_read = new Handler()
	{										
//		  @SuppressLint("HandlerLeak")
		public void handleMessage(Message msg)								
		  {											
			  super.handleMessage(msg);			
			  if(msg.what == 0)
			  {
//				 while(!Declare.receive_flag){
				while(read_flag){
					 display();
					 break;
				 }
			  }
		  }									
	};
	public static void display(){
//		if(Declare.data_facs!=null){
		 //用于测试
		int zs_index=Integer.parseInt(getdbzs());
		String zs_str=null;
		switch(zs_index){
		case 0:
			zs_str="三相四线有功";
			layout_ub.setVisibility(0);layout_ib.setVisibility(0);layout_jb.setVisibility(0);
			layout_pb.setVisibility(0);//layout_qb.setVisibility(0);layout_sb.setVisibility(0);
			lab_ua.setText("A相");lab_ia.setText("A相");lab_ja.setText("A相");//lab_ja.setTextSize(24/size_value);
			lab_pa.setText("A相");//lab_qa.setText("  A相   ");lab_sa.setText("  A相   ");
			lab_uc.setText("C相");lab_ic.setText("C相");lab_jc.setText("C相");//lab_jc.setTextSize(24/size_value);
			lab_pc.setText("C相");//lab_qc.setText("  C相   ");lab_sc.setText("  C相   ");
			break;
		case 1:
			layout_ub.setVisibility(0);layout_ib.setVisibility(0);layout_jb.setVisibility(0);
			layout_pb.setVisibility(0);//layout_qb.setVisibility(0);layout_sb.setVisibility(0);
			lab_ua.setText("A相");lab_ia.setText("A相");lab_ja.setText("A相");//lab_ja.setTextSize(24/size_value);
			lab_pa.setText("A相");//lab_qa.setText("  A相   ");lab_sa.setText("  A相   ");
			lab_uc.setText("C相");lab_ic.setText("C相");lab_jc.setText("C相");//lab_jc.setTextSize(24/size_value);
			lab_pc.setText("C相");//lab_qc.setText("  C相   ");lab_sc.setText("  C相   ");
			zs_str="三相四线无功";
			break;
		case 2:
			zs_str="三相三线有功";
			layout_ub.setVisibility(4);layout_ib.setVisibility(4);layout_jb.setVisibility(4);
			layout_pb.setVisibility(4);//layout_qb.setVisibility(4);layout_sb.setVisibility(4);
			lab_ua.setText("Uab");lab_ia.setText("Ia");lab_ja.setText("UabIa");//lab_ja.setTextSize(22/size_value);
			lab_pa.setText("AB相");//lab_qa.setText("  Qab ");lab_sa.setText("  Sab ");
			lab_uc.setText("Ucb");lab_ic.setText("Ic");lab_jc.setText("UcbIc");//lab_jc.setTextSize(22/size_value);
			lab_pc.setText("CB相");//lab_qc.setText("  Qcb ");lab_sc.setText("  Scb ");
			break;
		case 3:
			zs_str="三相三线无功";
			layout_ub.setVisibility(4);layout_ib.setVisibility(4);layout_jb.setVisibility(4);
			layout_pb.setVisibility(4);//layout_qb.setVisibility(4);layout_sb.setVisibility(4);
			lab_ua.setText("Uab");lab_ia.setText("Ia");lab_ja.setText("UabIa");//lab_ja.setTextSize(22/size_value);
			lab_pa.setText("AB相");//lab_qa.setText("  Qab ");lab_sa.setText("  Sab ");
			lab_uc.setText("Ucb");lab_ic.setText("Ic");lab_jc.setText("UcbIc");//lab_jc.setTextSize(22/size_value);
			lab_pc.setText("CB相");//lab_qc.setText("  Qcb ");lab_sc.setText("  Scb ");
			break;
		}
 		zs.setText(zs_str);//获得电表制式
	    sum1++;
		int temp_pa,temp_pb,temp_pc,temp_ph,
			temp_qa,temp_qb,temp_qc,temp_qh,
			temp_sa,temp_sb,temp_sc,temp_sh;	 
		//单位
		//TextView unit_pa=(TextView)view.findViewById(R.id.activePowerATV);
		//TextView unit_pb=(TextView)view.findViewById(R.id.activePowerBTV);
		//TextView unit_pc=(TextView)view.findViewById(R.id.activePowerCTV);
		//TextView unit_ph=(TextView)view.findViewById(R.id.activePowerSumTV);
		//TextView unit_qa=(TextView)view.findViewById(R.id.reactivePowerATV);
		//TextView unit_qb=(TextView)view.findViewById(R.id.reactivePowerBTV);
		//TextView unit_qc=(TextView)view.findViewById(R.id.reactivePowerCTV);
		//TextView unit_qh=(TextView)view.findViewById(R.id.reactivePowerSumTV);
		//TextView unit_sa=(TextView)view.findViewById(R.id.shiZaiPowerATV);
		//TextView unit_sb=(TextView)view.findViewById(R.id.shiZaiPowerBTV);
		//TextView unit_sc=(TextView)view.findViewById(R.id.shiZaiPowerCTV);
		//TextView unit_sh=(TextView)view.findViewById(R.id.shiZaiPowerSumTV);
		ua.setText(myformat1.format((float)(Declare.data_facs[0])/32768));
//		ua.invalidate();
		ia.setText(cur_fromat((float)(Declare.data_facs[1])/32768));
//		ub.invalidate();
		ub.setText(myformat1.format((float)(Declare.data_facs[2])/32768));
		ib.setText(cur_fromat((float)(Declare.data_facs[3])/32768));
		uc.setText(myformat1.format((float)(Declare.data_facs[4])/32768));
		ic.setText(cur_fromat((float)(Declare.data_facs[5])/32768));
		pa.setText(myformat1.format((float)(Declare.data_facs[6])/1024));
		pb.setText(myformat1.format((float)(Declare.data_facs[7])/1024));
		pc.setText(myformat1.format((float)(Declare.data_facs[8])/1024));
		ph.setText(myformat1.format((float)(Declare.data_facs[9])/1024));
		qa.setText(myformat1.format((float)(Declare.data_facs[10])/1024));
		qb.setText(myformat1.format((float)(Declare.data_facs[11])/1024));
		qc.setText(myformat1.format((float)(Declare.data_facs[12])/1024));
		qh.setText(myformat1.format((float)(Declare.data_facs[13])/1024));
		sa.setText(myformat1.format((float)(Declare.data_facs[14])/1024));
		sb.setText(myformat1.format((float)(Declare.data_facs[15])/1024));
		sc.setText(myformat1.format((float)(Declare.data_facs[16])/1024));
		sh.setText(myformat1.format((float)(Declare.data_facs[17])/1024));
		ja.setText(myformat1.format((float)(Declare.data_facs[18])/8192));
		jb.setText(myformat1.format((float)(Declare.data_facs[19])/8192));
		jc.setText(myformat1.format((float)(Declare.data_facs[20])/8192));
		double cos_value,sin_value;
		cos_value=(float)(Declare.data_facs[24])/8192;
		cos.setText(myformat1.format((float)(Declare.data_facs[24])/8192));
		hz.setText(myformat1.format((float)(Declare.data_facs[25])/8192));
		//temp.setText(myformat1.format((float)(Declare.data_facs[26])*0.125));//*******温度显示
		sin_value=Math.sin(Math.acos(cos_value));
//		System.out.println("sin值："+sin_value);
		if(sin_value<0.0175){
			sin_value=0;
		}
		sin.setText(myformat1.format((float)(sin_value)));
//			zs=(TextView)findViewById(R.id.zs);
//			Declare.data_facs=null;
//			}
		//存储到零时数组
		String temp=myformat1.format((float)(Declare.data_facs[0])/32768);
		Declare.save_data[0]=temp;//ua.getText().toString();
		Declare.save_data[1]=ia.getText().toString();
		Declare.save_data[2]=ub.getText().toString();
		Declare.save_data[3]=ib.getText().toString();
		Declare.save_data[4]=uc.getText().toString();
		Declare.save_data[5]=ic.getText().toString();
		Declare.save_data[6]=ja.getText().toString();
		Declare.save_data[7]=jb.getText().toString();
		Declare.save_data[8]=jc.getText().toString();
		Declare.save_data[9]=hz.getText().toString();
		Declare.save_data[10]=cos.getText().toString();
		Declare.save_data[11]=pa.getText().toString();
		Declare.save_data[12]=pb.getText().toString();
		Declare.save_data[13]=pc.getText().toString();
		Declare.save_data[14]=qa.getText().toString();
		Declare.save_data[15]=qb.getText().toString();
		Declare.save_data[16]=qc.getText().toString();
		Declare.save_data[17]=sa.getText().toString();
		Declare.save_data[18]=sb.getText().toString();
		Declare.save_data[19]=sc.getText().toString();
		//根据具体数值转换显示单位
		temp_pa=Math.abs((int)Float.parseFloat(pa.getText().toString()));
		temp_pb=Math.abs((int)Float.parseFloat(pb.getText().toString()));
		temp_pc=Math.abs((int)Float.parseFloat(pc.getText().toString()));
		temp_ph=Math.abs((int)Float.parseFloat(ph.getText().toString()));
		temp_qa=Math.abs((int)Float.parseFloat(qa.getText().toString()));
		temp_qb=Math.abs((int)Float.parseFloat(qb.getText().toString()));
		temp_qc=Math.abs((int)Float.parseFloat(qc.getText().toString()));
		temp_qh=Math.abs((int)Float.parseFloat(qh.getText().toString()));
		temp_sa=Math.abs((int)Float.parseFloat(sa.getText().toString()));
		temp_sb=Math.abs((int)Float.parseFloat(sb.getText().toString()));
		temp_sc=Math.abs((int)Float.parseFloat(sc.getText().toString()));
		temp_sh=Math.abs((int)Float.parseFloat(sh.getText().toString()));	
		//根据读取的数值变换显示与单位
		if(temp_pa>=10000 || temp_pb>=10000 || temp_pc>=10000){
			pa.setText(myformat1.format((float)(Float.parseFloat(pa.getText().toString())/1000)));
			pb.setText(myformat1.format((float)(Float.parseFloat(pb.getText().toString())/1000)));
			pc.setText(myformat1.format((float)(Float.parseFloat(pc.getText().toString())/1000)));
			ph.setText(myformat1.format((float)(Float.parseFloat(ph.getText().toString())/1000)));
			unit_p.setText("(KW)");//unit_pb.setText(" KW");unit_pc.setText(" KW");unit_ph.setText(" KW");
		}
		if(temp_pa<10000 && temp_pb<10000 && temp_pc<10000){
			unit_p.setText("(W)");//unit_pb.setText("  W");unit_pc.setText("  W");unit_ph.setText("  W");
		}
		if(temp_qa>=10000 || temp_qb>=10000 || temp_qc>=10000){
			qa.setText(myformat1.format((float)(Float.parseFloat(qa.getText().toString())/1000)));
			qb.setText(myformat1.format((float)(Float.parseFloat(qb.getText().toString())/1000)));
			qc.setText(myformat1.format((float)(Float.parseFloat(qc.getText().toString())/1000)));
			qh.setText(myformat1.format((float)(Float.parseFloat(qh.getText().toString())/1000)));
			unit_q.setText("(Kvar)");//unit_qb.setText(" Kvar");unit_qc.setText(" Kvar");unit_qh.setText(" Kvar");
		}
		if(temp_qa<10000 && temp_qb<10000 && temp_qc<10000){
			unit_q.setText("(var)");//unit_qb.setText(" var");unit_qc.setText(" var");unit_qh.setText(" var");
		}
		if(temp_sa>=10000 || temp_sb>=10000 || temp_sc>=10000){
			sa.setText(myformat1.format((float)(Float.parseFloat(sa.getText().toString())/1000)));
			sb.setText(myformat1.format((float)(Float.parseFloat(sb.getText().toString())/1000)));
			sc.setText(myformat1.format((float)(Float.parseFloat(sc.getText().toString())/1000)));
			sh.setText(myformat1.format((float)(Float.parseFloat(sh.getText().toString())/1000)));
			unit_s.setText("(KVA)");//unit_sb.setText(" KVA");unit_sc.setText(" KVA");unit_sh.setText(" KVA");
		}
		if(temp_sa<10000 && temp_sb<10000 && temp_sc<10000){
			unit_s.setText("(VA)");//unit_sb.setText("  VA");unit_sc.setText("  VA");unit_sh.setText("  VA");
		}
		//启动循环采样后，接收超时清零
		if(Declare.rec_overtime==true){
			for(int i=0;i<Declare.data_facs.length;i++){
				Declare.data_facs[i]=0;
				}
		}
		//检测网络是否正常，后清零
		else if(Declare.Clientisconnect==false || Declare.rec_err==true){
			if(sum1>=2){
				for(int i=0;i<Declare.data_facs.length;i++){
				Declare.data_facs[i]=0;
				}
				sum1=0;
			}
			else{sum1++;}
		}
		else{sum1=0;}
	}
	//处理电流显示
	public static String cur_fromat(float current){
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
	//返回设置的电表ID号
	private static String getdbid() {	
		String str;	
		SharedPreferences settings = activity.getSharedPreferences("config",  0); 
		str=settings.getString("dbid", "000000000001");
		return str;			        		 		             
	}
	//返回设置的电表制式
	private static String getdbzs() {	
		String str;	
		SharedPreferences settings = activity.getSharedPreferences("config",  0); 
		str=settings.getString("dbzs", "0");
		return str;			        		 		             
	}
	//返回数据库记录查询结果
	private static boolean QueryRecord(){
		boolean flag=false;
		if(!sqldb.isOpen())	 {
	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	}
		Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=? and pa is not null", new String[]{getdbid()});
		try{
				if(cursor.getCount()==0){
					flag=false;
				}
				else{flag=true;}
		} catch (Exception e) {
		}finally{
			if(cursor!=null){cursor.close();}
		}
		return flag;
	}
//==========保存数据====================
	public class btn_click implements OnClickListener {               
    	@Override            
    	public void onClick(View v) { 
    		switch (v.getId()){
    		case R.id.btn_savefa:
//		    			Toast.makeText(getApplicationContext(), "标题按钮", Toast.LENGTH_SHORT).show();  
    			dialog = new confirm(activity,R.style.MyDialog);       	        	       	
   	         	dialog.show();
   	         	TextView text = (TextView)dialog.findViewById(R.id.textview2); 
   	         	if(QueryRecord()==true){
	         		text.setText("表号："+getdbid()+"数据已存在，是否覆盖？");
	         	}
	         	else{
	         		text.setText("确认保存表号："+getdbid()+"数据吗？");
	         	}
   	         	Button but_qr = (Button)dialog.findViewById(R.id.but_qr);   
                Button but_qx = (Button)dialog.findViewById(R.id.but_qx); 
                but_qr.setOnClickListener(new ConfirmListener());   
                but_qx.setOnClickListener(new ConfirmListener());  
    			break;
    		}
    	}
	}
	//========确认界面响应==================================
		public class ConfirmListener implements  OnClickListener{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
		    	case R.id.but_qr:
		    		SaveRecord(getdbid());
		         	dialog.cancel();
		         	Toast.makeText(activity, "表号："+getdbid()+"的数据已保存",Toast.LENGTH_LONG).show();
		    	   break;
		    	    //=================================
		    	case R.id.but_qx:
		    		dialog.cancel();//
		    	break;
		    	}
			} 
		}
	public void SaveRecord(String DBID) { 
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
		String  date =  sDateFormat.format(new java.util.Date());  
//					   Object[] ags ={myformat.format(id),text_date1.getText().toString()+text_time1.getText().toString(),"保定朗信","竞秀街创业中心","A00001","B00001","薄谷开来","薄谷开来"};
	             //=========查询是否有相同记录再插入数据==========================
		if(!sqldb.isOpen())	 {
	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	}
			Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=?", new String[]{DBID});
	         try {   
	         if(cursor.getCount()==0){
	        	/*String sql = "insert into " + "sxxy_data" + " (" +   
	        			"'dbid','cs_time','ua_fz','ia_fz','ub_fz','ib_fz','uc_fz'," +
	            		"'ic_fz','ja','jb','jc','hz','cosa','cosb','cosc','cos','pa','pb','pc','qa','qb','qc','sa','sb','sc'," +
	            		"'wc1','wc2','wc3','wc4','wc5','dyxx','dlxx','jxpb_l','jxpb_c','ia_cj','ub_cj','ib_cj','uc_cj','ic_cj'+" +
	            		"'srdl','gzxs','gzl','zbct','zbpt','zbdl','wgwc1','wgwc2','wgwc3','wgwc4','wgwc5') " +
	            		"values('" + DBID+ "','"+ date+"','"+Declare.save_data[0]+"','"+Declare.save_data[1]+"','"+Declare.save_data[2]+"','"+Declare.save_data[3]+
	            		"','"+Declare.save_data[4]+"','"+Declare.save_data[5]+"','"+Declare.save_data[6]+"','"+Declare.save_data[7]+"','"+Declare.save_data[8]+
	            		"','"+Declare.save_data[9]+"',null,null,null,'" +Declare.save_data[10]+"','"+Declare.save_data[11]+"','"+Declare.save_data[12]+
	            		"','"+Declare.save_data[13]+"','"+Declare.save_data[14]+"','"+Declare.save_data[15]+"','"+Declare.save_data[16]+"','"+Declare.save_data[17]+
	            		"','"+Declare.save_data[18]+"','"+Declare.save_data[19]+"','"+Declare.save_data[20]+"','"+Declare.save_data[21]+"','"+Declare.save_data[22]+
	            		"','"+Declare.save_data[23]+"','"+Declare.save_data[24]+"','"+Declare.save_data[25]+"','"+Declare.save_data[26]+"','"+Declare.save_data[27]+
	            		"','"+Declare.save_data[28]+"','"+Declare.save_data[29]+"','"+Declare.save_data[30]+"','"+Declare.save_data[31]+"','"+Declare.save_data[32]+
	            		"','"+Declare.save_data[33]+"','"+Declare.save_data[34]+"','"+Declare.save_data[35]+"','"+Declare.save_data[36]+"','"+Declare.save_data[37]+
	            		"','"+Declare.save_data[38]+"','"+Declare.save_data[39]+"','"+Declare.save_data[40]+"','"+Declare.save_data[41]+"','"+Declare.save_data[42]+
	            		"','"+Declare.save_data[43]+"','"+Declare.save_data[44]+"')"; */
	        	String sql = "insert into " + "sxxy_data" + " (" +   
	        			"'dbid','cs_time','ua_fz','ia_fz','ub_fz','ib_fz','uc_fz'," +
	            		"'ic_fz','ja','jb','jc','hz','cosa','cosb','cosc','cos','pa','pb','pc','qa','qb','qc','sa','sb','sc') " +
	            		"values('" + DBID+ "','"+ date+"','"+Declare.save_data[0]+"','"+Declare.save_data[1]+"','"+Declare.save_data[2]+"','"+Declare.save_data[3]+
	            		"','"+Declare.save_data[4]+"','"+Declare.save_data[5]+"','"+Declare.save_data[6]+"','"+Declare.save_data[7]+"','"+Declare.save_data[8]+
	            		"','"+Declare.save_data[9]+"',null,null,null,'" +Declare.save_data[10]+"','"+Declare.save_data[11]+"','"+Declare.save_data[12]+
	            		"','"+Declare.save_data[13]+"','"+Declare.save_data[14]+"','"+Declare.save_data[15]+"','"+Declare.save_data[16]+"','"+Declare.save_data[17]+
	            		"','"+Declare.save_data[18]+"','"+Declare.save_data[19]+"')";
	            	 try {  
	            		 sqldb.execSQL(sql);  
	           	   } catch (SQLException e) {   } 
	           }
	         else{
	           /* String sql="update sxxy_data set cs_time=?,ua_fz=?,ia_fz=?,ub_fz=?,ib_fz=?,uc_fz=?,ic_fz=?," +
	            		"ja=?,jb=?,jc=?,hz=?,cos=?,pa=?,pb=?,pc=?,qa=?,qb=?,qc=?,sa=?,sb=?,sc=?," +
	            	 	"wc1=?,wc2=?,wc3=?,wc4=?,wc5=?,dyxx=?,dlxx=?,jxpb_l=?,jxpb_c=?,ia_cj=?,ub_cj=?,ib_cj=?,uc_cj=?," +
	            	 	"ic_cj=?,srdl=?,gzxs=?,gzl=?,zbct=?,zbpt=?,zbdl=?,wgwc1=?,wgwc2=?,wgwc3=?,wgwc4=?,wgwc5=? where dbid=?";
	         	Object[] ags={date,Declare.save_data[0],Declare.save_data[1],Declare.save_data[2],Declare.save_data[3],Declare.save_data[4],Declare.save_data[5],
	         				Declare.save_data[6],Declare.save_data[7],Declare.save_data[8],Declare.save_data[9],Declare.save_data[10],Declare.save_data[11],
	         				Declare.save_data[12],Declare.save_data[13],Declare.save_data[14],Declare.save_data[15],Declare.save_data[16],Declare.save_data[17],
	         				Declare.save_data[18],Declare.save_data[19],Declare.save_data[20],Declare.save_data[21],Declare.save_data[22],Declare.save_data[23],
	         				Declare.save_data[24],Declare.save_data[25],Declare.save_data[26],Declare.save_data[27],Declare.save_data[28],Declare.save_data[29],
	         				Declare.save_data[30],Declare.save_data[31],Declare.save_data[32],Declare.save_data[33],Declare.save_data[34],Declare.save_data[35],
	         				Declare.save_data[36],Declare.save_data[37],Declare.save_data[38],Declare.save_data[39],Declare.save_data[40],Declare.save_data[41],
	         				Declare.save_data[42],Declare.save_data[43],Declare.save_data[44],DBID};*/
	         	String sql="update sxxy_data set cs_time=?,ua_fz=?,ia_fz=?,ub_fz=?,ib_fz=?,uc_fz=?,ic_fz=?," +
	            		"ja=?,jb=?,jc=?,hz=?,cos=?,pa=?,pb=?,pc=?,qa=?,qb=?,qc=?,sa=?,sb=?,sc=? where dbid=?";
	         	Object[] ags={date,Declare.save_data[0],Declare.save_data[1],Declare.save_data[2],Declare.save_data[3],Declare.save_data[4],Declare.save_data[5],
	         				Declare.save_data[6],Declare.save_data[7],Declare.save_data[8],Declare.save_data[9],Declare.save_data[10],Declare.save_data[11],
	         				Declare.save_data[12],Declare.save_data[13],Declare.save_data[14],Declare.save_data[15],Declare.save_data[16],Declare.save_data[17],
	         				Declare.save_data[18],Declare.save_data[19],DBID};
	         	     try {  
	         	           sqldb.execSQL(sql,ags);  
	         	         } catch (SQLException e) {  }
	             }
	         }catch (Exception e) {  }
	         finally{
	        	 if(cursor!=null){cursor.close();}
	         }
	}
}