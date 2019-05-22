package com.lx.checkameterclient.fragment;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.lx.checkameter.socket.Toasts;
import com.lx.checkameter.socket.mSocketClient;
import com.lx.checkameterclient.Declare;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.view.SegmentControl;
import com.lx.checkameterclient.view.confirm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class CAErrorTestingFragment extends Fragment {
	static TextView qsxs,wcxs,wc1,wc2,wc3,wc4,wc5,wcpj,text_ztxs;
	static int Error_flag;//误差标志
	int val_mcbl;//脉冲倍率
	private static DecimalFormat myformat1,myformat2,myformat3;
	private static float[] wc_array=new float[4];
	private int position1,position2,position3,position4;
	private static int position5;
	public static boolean wg_check_flag=false;//无功勾选
	private static boolean read_flag=false; 
	private Thread mThreadread=null;
	private static int set_sum=0;//sum1=0
	private static int sum=0;
	private Activity activity;
	private File file = new File("/sdcard/bdlx/sxxy.db");// 数据库文件路径
	public static Button btn_savewc,btn_qdjy;
	private Dialog dialog;
	SQLiteDatabase sqldb;
	String sql;
	private boolean auto_flag,manu_flag;//自动、手动按钮启动标志
	private boolean qs_set_flag=false;//手动时置圈标志
	private static String temp_bh,temp_hm,temp_cs,temp_qs,temp_dyxs,temp_dlxs,temp_fpxs,temp_dbdj,temp_dbzs,temp_dylc,temp_dllc,temp_mcfs,
    temp_addr,temp_xl,temp_tz,temp_jyy,temp_xhy,temp_zdy1,temp_zdy2,temp_zdy3,temp_ssid,temp_ssid_pass,
    temp_db_type,temp_db_mfrs,temp_fsdl,temp_psdl,temp_gsdl,temp_zzyg,temp_zzwg,temp_fxwg;
	private EditText cssz,qssz,dysz,dlsz,fpsz;
	private TextView spinner1,spinner2,spinner5; 
	
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
		myformat1= new DecimalFormat("0.000");
		myformat2= new DecimalFormat("0.0000");
		myformat3= new DecimalFormat("0.00");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ca_error_testing_fragment, null);
        SegmentControl segmentcontrol=(SegmentControl)view.findViewById(R.id.errorTestingSegment);
		segmentcontrol.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		        //处理点击标签的事件
		    	position5=index;
		    } 
		});   
		//清空数据
		for(int i=0;i<Declare.data_wcxj.length;i++){
			Declare.data_wcxj[i]=0;
		}
		//int sum1=0;
		//Declare.txlj_ok_flag=false;
		Declare.txlj_erro_flag=false;//初始化标志位便于toast信息弹出
		spinner1=(TextView)view.findViewById(R.id.dylc);
		spinner2=(TextView)view.findViewById(R.id.dllc);
		spinner5=(TextView)view.findViewById(R.id.mcfs1);
		cssz=(EditText)view.findViewById(R.id.ameterConstantValueET1);
		qssz=(EditText)view.findViewById(R.id.coilsNumberET1);
		dysz=(EditText)view.findViewById(R.id.voltRatioET1);
		dlsz=(EditText)view.findViewById(R.id.amphereRatioET1);
		fpsz=(EditText)view.findViewById(R.id.frequencyDivideRatioET);
		
		qsxs=(TextView)view.findViewById(R.id.coilsNumberTV);
		wcxs=(TextView)view.findViewById(R.id.errorValueTV);
		wc1=(TextView)view.findViewById(R.id.errorValue1TV);
		wc2=(TextView)view.findViewById(R.id.errorValue2TV);
		wc3=(TextView)view.findViewById(R.id.errorValue3TV);
		wc4=(TextView)view.findViewById(R.id.errorValue4TV);
		wc5=(TextView)view.findViewById(R.id.standardErrorValueTV);
		wcpj=(TextView)view.findViewById(R.id.averageErrorValueTV);
		//wcpj.setText("456");
		btn_savewc=(Button)view.findViewById(R.id.btn_savewc);
		btn_qdjy=(Button)view.findViewById(R.id.softSwitchBtn);
		btn_qdjy.setOnClickListener(new btn_click());
		 //		btn_xytz.setOnClickListener(new btn_click());
		btn_savewc.setOnClickListener(new btn_click());
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
        text_ztxs=(TextView)view.findViewById(R.id.stateInfoTV);
        LinearLayout lay_sdkg=(LinearLayout)view.findViewById(R.id.lay_sdkg);
        lay_sdkg.setVisibility(View.GONE);
        get_config();
        Switch sw=(Switch)view.findViewById(R.id.checkSwitch);
        sw.setChecked(true);
        text_ztxs.setText("状态：    自动校验");
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
              @Override  
            public void onCheckedChanged(CompoundButton buttonView,  
                    boolean isChecked) {  
                // TODO Auto-generated method stub  
                if (isChecked) {  
                	//if(auto_flag==false){
        				auto_flag=true;
        				for(int i=0;i<Declare.data_wcxj.length;i++){
        				Declare.data_wcxj[i]=0;
        				}
        				for(int i=0;i<wc_array.length;i++){
        					wc_array[i]=0;
            			}
        				clear_error();
        				/*
        				if(mThreadread!=null){
            				read_flag=false;
            				mThreadread.interrupt();
            				mThreadread=null;
            			}
            			*/
        				mSocketClient.StopClientmsg();
        				Declare.iserrortest=true;	
        				Declare.send_flag=2;
        					Declare.Circle=true;          
        					set_sum=5;
        					sum=0;
        					mSocketClient.SendClientmsg(Declare.Circle);
        					//set_disenable();
        					 switch(position5){
               		   				//======自动=========
        					 case 0:
        						//hjh//image_lamp.setImageDrawable(getResources().getDrawable(R.drawable.lamp_r));
               			   			text_ztxs.setText("状态：    自动校验");
               			   			text_ztxs.setTextColor(Color.RED);
               			   			//btn_xyqd.setText("停止校验");
               			   			//btn_xyqd.setTextColor(Color.RED);
               			   			break;
        					 case 1:
        						   /*********=======软自动==========
        						 	ibut_manu.setEnabled(true);
        						 	//ibut_manu.setImageResource(R.drawable.manu_btn);
        						 	ibut_manu.setBackgroundResource(R.drawable.manu_btn);
        						 	//ibut_manu.setImageDrawable(getResources().getDrawable(R.drawable.manu_btn));
        						 	image_lamp.setImageDrawable(getResources().getDrawable(R.drawable.lamp_l));
                			   		text_ztxs.setText("软自动运行");
                			   		text_ztxs.setTextColor(0xFF00A900);
                			   		btn_xyqd.setText("停止校验");
                			   		btn_xyqd.setTextColor(0xFF00A900);
               			   			******/
      						   		//=======手动==========
      						 		//ibut_manu.setEnabled(true);
      						 		//ibut_manu.setImageResource(R.drawable.manu_btn);
      						 		//ibut_manu.setBackgroundResource(R.drawable.manu_btn);
      						 		//ibut_manu.setImageDrawable(getResources().getDrawable(R.drawable.manu_btn));
        						//hjh//image_lamp.setImageDrawable(getResources().getDrawable(R.drawable.lamp_l));
      						 		text_ztxs.setText("手动运行");
      						 		text_ztxs.setTextColor(0xFF00A900);
      						 	//hjh//btn_xyqd.setText("停止校验");
      						  //hjh//btn_xyqd.setTextColor(0xFF00A900);
                			   		break;
                			   		//=======软手动==========
        					 case 2:
        						//hjh//ibut_manu.setEnabled(true);
                			   		//ibut_manu.setImageResource(R.drawable.manu_btn);
        						//hjh//ibut_manu.setBackgroundResource(R.drawable.manu_btn);
        						//hjh//qs_set_flag=false;
                			   		//ibut_manu.setImageDrawable(getResources().getDrawable(R.drawable.manu_btn));
                			   	//hjh//image_lamp.setImageDrawable(getResources().getDrawable(R.drawable.lamp_b));
                			   		text_ztxs.setText("软手动运行");
                			   		text_ztxs.setTextColor(0xFF0000FF);
                			   	//hjh//btn_xyqd.setText("停止校验");
                			   	//hjh//btn_xyqd.setTextColor(0xFF0000FF);
        						 break;
               		   }
                } else {  
                	auto_flag=false;
    				mSocketClient.StopClientmsg();
    				Declare.send_set_flag=15;
    				mSocketClient.send_setmsg(Declare.send_set_flag,(short)0);        				
   	        		Declare.receive_flag=false;
   	        		
   	               	Declare.send_flag=17;
   	               	Declare.iserrortest=false;
   	               	set_sum=1;
	               	sum=0;
   	               	Declare.Circle=true;
   	               	System.out.println("开启");		
   	               	mSocketClient.SendClientmsg(Declare.Circle);
   	              // }
   	           //hjh//set_enable();
   	           //hjh//image_lamp.setImageDrawable(getResources().getDrawable(R.drawable.lamp_g));
					text_ztxs.setText("状态：    校验停止");
					text_ztxs.setTextColor(Color.WHITE);
					//hjh//btn_xyqd.setText("启动校验");
					//hjh//btn_xyqd.setTextColor(Color.BLACK);
					//hjh//ibut_manu.setEnabled(false);
   			   		//ibut_manu.setImageResource(R.drawable.btn_manu3);
					//hjh//ibut_manu.setBackgroundResource(R.drawable.btn_manu3);
                }  
            }  
        });  
        
		if(mThreadread==null){
          	read_flag=true;
          	mThreadread=new mThreadread();//开启计时线程，进行界面更新
          	mThreadread.start();                
          	//Declare.send_flag=17;
          	Declare.Circle=true;
          	set_sum=1;
          	sum=0;
          	System.out.println("开启");		
          	mSocketClient.SendClientmsg(Declare.Circle);
          }
		return view;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//mAdapter = null;
		//read_flag=false;//Activity销毁时终止刷新线程
		if(mThreadread!=null){
			read_flag=false;
			mThreadread.interrupt();
			mThreadread=null;
		
			//mSocketClient.StopClientmsg();
			//if(auto_flag==true || manu_flag==true){
			
        	//Declare.send_flag=15;         
       	    //mSocketClient.send_setmsg(Declare.send_flag,(short)0);
			//}
			//Declare.receive_flag=false;
		}
		Declare.connect_num=0;
        Declare.connect_num1=0;
        Declare.rec_overtime=false;
		sqldb.close();
		//清空数据
		for(int i=0;i<Declare.data_wcxj.length;i++){
			Declare.data_wcxj[i]=0;
		}
	}
	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
	}
	public void clear_error(){
		for(int i=0;i<wc_array.length;i++){
			wc_array[i]=0;
			}
		wc1.setText("0.000");
		wc2.setText("0.000");
		wc3.setText("0.000");
		wc4.setText("0.000");
		wc5.setText("0.000");
		wcpj.setText("0.000");
		wcxs.setText("0.000");
	}
	public class btn_click implements OnClickListener {               
    	@Override            
    	public void onClick(View v) { 
    		switch (v.getId()){
			//自动校验
   	        case R.id.softSwitchBtn:
   	       //==========手动脉冲按钮=====================
    	    		if(Declare.Clientisconnect==true && Declare.rec_err==false){
           			//软手动
            		if(position5==2){
            			mSocketClient.send_debug_msg("14","0000");
            			String str=null;
            			int val=0;
            			//str=qssz.getText().toString();
            			val=Integer.parseInt(str);
            			
            			if(qs_set_flag==false){
            				
            				qs_set_flag=true;
            				if(val>1){
            					qsxs.setText(String.valueOf(val-1));
            				}
            			}
            			else{
            				qs_set_flag=false;
            				qsxs.setText("0");
            			}
            		}
            		//软自动
            		else if(position5==1){
            			mSocketClient.send_debug_msg("15","0000");	
            		}
      				//DisplayToast("脉冲下发成功！！");
            	  }
            	  else{DisplayToast("网络故障，请检查wifi及socket链接！！");}
    			break;
    			//====保存按钮=======================
    		case R.id.btn_savewc:
//    			Toast.makeText(getApplicationContext(), "标题按钮", Toast.LENGTH_SHORT).show();  
    			dialog = new confirm(getActivity(),R.style.MyDialog);       	        	       	
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
public void DisplayToast(String msg){
//	    Toast toast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    //设置toast显示位置
//	    toast.setGravity(Gravity.TOP, 0, 220);
//	    toast.show();
	Toasts.toast(activity, msg, Toast.LENGTH_SHORT,Gravity.TOP, 0, 520);
    }
	//读取参数基本设置
	private void get_config() {	
	 	String str1,str2,str3,str4,str5,str6,str7,str8,
	 	str9,str10,str11,str12,str13,str14,str15,str16,str17;
		SharedPreferences settings = activity.getSharedPreferences("config",  0);
		str1=settings.getString("dbid", "000000000001");
		//bhsz.setText(str1);
		temp_bh=str1;
		str2=settings.getString("hmsz", "");	 	
	 	//hmsz.setText(str2);
	 	temp_hm=str2;
	 	int P1,P2,P3,P4,P5,P6;
	 	temp_dbzs=settings.getString("dbzs", "");
	 	//spinner.setSelection(P1,true);
	 	//position1=P1;
	 	temp_dbdj=settings.getString("dbdj", "");
	 	//P2=Integer.parseInt(settings.getString("dylc", "0"));
	 	spinner1.setText(Declare.dylc);
	 	//position2=P2;
	 	temp_dylc=settings.getString("dylc", "0");
	 	//P3=Integer.parseInt(settings.getString("dllc", "0"));
	 	spinner2.setText(Declare.dllc);
	 	//position3=P3;
	 	temp_dllc=settings.getString("dllc", "0");
	 	//P4=Integer.parseInt(settings.getString("mcfs", "0"));
	 	spinner5.setText(Declare.mcfs);
	 	//position4=P4;
	 	////P5=Integer.parseInt(settings.getString("xbfs", "0"));
	 	
	 	////spinner4.setSelection(P5,true);
	 	////position5=P5;	 	
	 	temp_mcfs=settings.getString("mcfs", "0");
	 	//spinner5.setSelection(P6,true);
	 	//position6=P6;
	 	
	 	str3=settings.getString("dbcs", "2000");
	 	cssz.setText(str3);
	 	temp_cs=str3;
	 	str4=settings.getString("xbqs", "5");
	 	qssz.setText(str4);
	 	temp_qs=str4;
	 	str5=settings.getString("dyxs", "1");
	 	dysz.setText(str5);
	 	temp_dyxs=str5;
	 	str6=settings.getString("dlxs", "1");
	 	dlsz.setText(str6);
	 	temp_dlxs=str6;
	 	str7=settings.getString("fpxs", "1");
	 	fpsz.setText(str7);
	 	temp_fpxs=str7;
	 	str8=settings.getString("addr", "");
	 	//dzsz.setText(str8);
	 	temp_addr=str8;
	 	str9=settings.getString("xianl", "");
	 	//xlsz.setText(str9);
	 	temp_xl=str9;
	 	str10=settings.getString("taiz", "");
	 	//tzsz.setText(str10);
	 	temp_tz=str10;
	 	str11=settings.getString("jyy", "");
	 	//jyy.setText(str11);
	 	temp_jyy=str11;
	 	str12=settings.getString("jhy", "");
	 	//xhy.setText(str12);
	 	temp_xhy=str12;
	 	//str13=settings.getString("zdy1", "");
	 	//zdy1.setText(str13);
	 	//temp_zdy1=str13;
	 	//str14=settings.getString("zdy2", "");
	 	//zdy2.setText(str14);
	 	//temp_zdy2=str14;
	 	//str15=settings.getString("zdy3", "");
	 	//zdy3.setText(str15);
	 	//temp_zdy3=str15;
	 	//str16=settings.getString("ssid", "lxdz");
	 	//ssid.setText(str16);
	 	//temp_ssid=str16;
	 	//str17=settings.getString("ssid_pass", "87654321");
	 	//ssid_pass.setText(str17);
	 	//temp_ssid_pass=str17;
	 	//=========电表信息==================
	 	str17=settings.getString("db_type", "");
	 	//db_type.setText(str17);
	 	temp_db_type=str17;
	 	str17=settings.getString("db_mfrs", "");
	 	//db_mfrs.setText(str17);
	 	temp_db_mfrs=str17;
	 	str17=settings.getString("fsdl", "0.00");
	 	//fsdl.setText(str17);
	 	temp_fsdl=str17;
	 	str17=settings.getString("psdl", "0.00");
	 	//psdl.setText(str17);
	 	temp_psdl=str17;
	 	str17=settings.getString("gsdl", "0.00");
	 	//gsdl.setText(str17);
	 	temp_gsdl=str17;
	 	str17=settings.getString("zzyg", "0.00");
	 	//zzyg.setText(str17);
	 	temp_zzyg=str17;
	 	str17=settings.getString("zzwg", "0.00");
	 	//zzwg.setText(str17);
	 	temp_zzwg=str17;
	 	str17=settings.getString("fxwg", "0.00");
	 	//fxwg.setText(str17);
	 	temp_fxwg=str17;
	}
//返回设置的电表ID号
	private String getdbid() {	
		String str;	
		SharedPreferences settings = getActivity().getSharedPreferences("config",  0); 
		str=settings.getString("dbid", "000000000001");
		return str;			        		 		             
	}
	//返回数据库记录查询结果
	private boolean QueryRecord(){
		boolean flag;
		if(!sqldb.isOpen())	 {
	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	}
		Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=? and wc1 is not null", new String[]{getdbid()});
		if(cursor.getCount()==0){
			    flag=false;
		}
		else{flag=true;}
		return flag;
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
         		Toast.makeText(getActivity(),"表号："+ getdbid()+"误差数据记录已保存", Toast.LENGTH_LONG).show();
    	     break;
    	     //=================================
    		 case R.id.but_qx:
    			 dialog.cancel();//
    		 break;
    		 }
		} 
	}
	//==========保存数据====================
	public void SaveRecord(String DBID) { 
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
				String  date =  sDateFormat.format(new java.util.Date());  
	//				   Object[] ags ={myformat.format(id),text_date1.getText().toString()+text_time1.getText().toString(),"保定朗信","竞秀街创业中心","A00001","B00001","薄谷开来","薄谷开来"};
	         //=========查询是否有相同记录再插入数据==========================
		if(!sqldb.isOpen())	 {
 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
 	 	}
		Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=?", new String[]{DBID});
	    if(cursor.getCount()==0){
	    	/*String sql = "insert into " + "sxxy_data" + " (" +   
	    			"'dbid','cs_time','name','addr','xianl','taiz','jyy','jhy','dbzs','dbdj','dylc','dllc','xbfs'," +
	        		"'dbcs','xbqs','dyxs','dlxs','fpxs','zdy1','zdy2','zdy3','ua_fz','ia_fz','ub_fz','ib_fz','uc_fz'," +
	        		"'ic_fz','ja','jb','jc','hz','cosa','cosb','cosc','cos','pa','pb','pc','qa','qb','qc','sa','sb','sc'," +
	        		"'wc1','wc2','wc3','wc4','wc5','dyxx','dlxx','jxpb_l','jxpb_c','ia_cj','ub_cj','ib_cj','uc_cj','ic_cj'+" +
	        		"'srdl','gzxs','gzl','zbct','zbpt','zbdl','wgwc1','wgwc2','wgwc3','wgwc4','wgwc5') " +
	        		"values('" + DBID+ "','"+ date+"','"+temp_hm+"','"+temp_addr+"','"+temp_xl+"','"+temp_tz+"','"+temp_jyy+
	        		"','"+temp_xhy+"','"+Declare.dbzs+"',null,'"+Declare.dylc+
	        		"','"+Declare.dllc+"',null,'"+temp_cs+"','"+
	        		temp_qs+"','"+temp_dyxs+"','"+temp_dlxs+"','"+temp_fpxs+
	        		"',null,null,null,'"+Declare.save_data[0]+"','"+Declare.save_data[1]+"','"+Declare.save_data[2]+"','"+Declare.save_data[3]+
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
	    			"'dbid','cs_time','name','addr','xianl','taiz','jyy','jhy','dbzs','dbdj','dylc','dllc'," +
	        		"'dbcs','xbqs','dyxs','dlxs','fpxs','zdy1','zdy2','zdy3',"+
	        		"'wc1','wc2','wc3','wc4','wc5','wgwc1','wgwc2','wgwc3','wgwc4','wgwc5') " +
	        		"values('" + DBID+ "','"+ date+"','"+temp_hm+"','"+temp_addr+"','"+temp_xl+"','"+temp_tz+"','"+temp_jyy+
	        		"','"+temp_xhy+"','"+Declare.dbzs+"',null,'"+Declare.dylc+
	        		"','"+Declare.dllc+"','"+temp_cs+"','"+
	        		temp_qs+"','"+temp_dyxs+"','"+temp_dlxs+"','"+temp_fpxs+
	        		"',null,null,null,'"+Declare.save_data[20]+"','"+Declare.save_data[21]+"','"+Declare.save_data[22]+
	        		"','"+Declare.save_data[23]+"','"+Declare.save_data[24]+"','"+Declare.save_data[40]+"','"+Declare.save_data[41]+"','"+Declare.save_data[42]+
	        		"','"+Declare.save_data[43]+"','"+Declare.save_data[44]+"')";
	        	 try {  
	        		 sqldb.execSQL(sql);  
	       	   } catch (SQLException e) {   } 
	       }
	     else{
	        /*String sql="update sxxy_data set cs_time=?,name=?,jyy=?,jhy=?,dbzs=?,dylc=?,dllc=?," +
	        		"xbfs=?,dbcs=?,xbqs=?,dyxs=?,dlxs=?,fpxs=?,ua_fz=?,ia_fz=?,ub_fz=?,ib_fz=?,uc_fz=?,ic_fz=?," +
	        		"ja=?,jb=?,jc=?,hz=?,cos=?,pa=?,pb=?,pc=?,qa=?,qb=?,qc=?,sa=?,sb=?,sc=?," +
	        	 	"wc1=?,wc2=?,wc3=?,wc4=?,wc5=?,dyxx=?,dlxx=?,jxpb_l=?,jxpb_c=?,ia_cj=?,ub_cj=?,ib_cj=?,uc_cj=?," +
	        	 	"ic_cj=?,srdl=?,gzxs=?,gzl=?,zbct=?,zbpt=?,zbdl=?,wgwc1=?,wgwc2=?,wgwc3=?,wgwc4=?,wgwc5=? where dbid=?";
	     	Object[] ags={date,temp_hm,temp_jyy,temp_xhy,Declare.dbzs,
	     				  Declare.dylc,Declare.dllc,"",
	     				  temp_cs,temp_qs,temp_dyxs,temp_dlxs,temp_fpxs,
	     				Declare.save_data[0],Declare.save_data[1],Declare.save_data[2],Declare.save_data[3],Declare.save_data[4],Declare.save_data[5],
	     				Declare.save_data[6],Declare.save_data[7],Declare.save_data[8],Declare.save_data[9],Declare.save_data[10],Declare.save_data[11],
	     				Declare.save_data[12],Declare.save_data[13],Declare.save_data[14],Declare.save_data[15],Declare.save_data[16],Declare.save_data[17],
	     				Declare.save_data[18],Declare.save_data[19],Declare.save_data[20],Declare.save_data[21],Declare.save_data[22],Declare.save_data[23],
	     				Declare.save_data[24],Declare.save_data[25],Declare.save_data[26],Declare.save_data[27],Declare.save_data[28],Declare.save_data[29],
	     				Declare.save_data[30],Declare.save_data[31],Declare.save_data[32],Declare.save_data[33],Declare.save_data[34],Declare.save_data[35],
	     				Declare.save_data[36],Declare.save_data[37],Declare.save_data[38],Declare.save_data[39],Declare.save_data[40],Declare.save_data[41],
	     				Declare.save_data[42],Declare.save_data[43],Declare.save_data[44],DBID};*/
	    	 String sql="update sxxy_data set cs_time=?,name=?,jyy=?,jhy=?,dbzs=?,dylc=?,dllc=?," +
		        		"dbcs=?,xbqs=?,dyxs=?,dlxs=?,fpxs=?," +
		        	 	"wc1=?,wc2=?,wc3=?,wc4=?,wc5=?,wgwc1=?,wgwc2=?,wgwc3=?,wgwc4=?,wgwc5=? where dbid=?";
		     	Object[] ags={date,temp_hm,temp_jyy,temp_xhy,Declare.dbzs,
		     				  Declare.dylc,Declare.dllc,
		     				  temp_cs,temp_qs,temp_dyxs,temp_dlxs,temp_fpxs,
		     				Declare.save_data[20],Declare.save_data[21],Declare.save_data[22],Declare.save_data[23],
		     				Declare.save_data[24],Declare.save_data[40],Declare.save_data[41],
		     				Declare.save_data[42],Declare.save_data[43],Declare.save_data[44],DBID};
	     	     try {  
	     	           sqldb.execSQL(sql,ags);  
	     	         } catch (SQLException e) {  }
	         }
	}
//2016-03-14 h	
	private static void display(){
		//myformat1=new DecimalFormat("0.0");
		float wc_pjz=0,bzpc=0;
		if(++sum==set_sum){
	 		sum=0;
	 		//ua.setText(myformat1.format((float)(Declare.data_wcxj[0])/32768));
			//ub.setText(myformat1.format((float)(Declare.data_wcxj[2])/32768));
			//uc.setText(myformat1.format((float)(Declare.data_wcxj[4])/32768));
			//ia.setText(cur_fromat((float)(Declare.data_wcxj[1])/32768));
			//ib.setText(cur_fromat((float)(Declare.data_wcxj[3])/32768));
			//ic.setText(cur_fromat((float)(Declare.data_wcxj[5])/32768));
			//ja.setText(myformat1.format((float)(Declare.data_wcxj[6])/8192));
			//jb.setText(myformat1.format((float)(Declare.data_wcxj[7])/8192));
			//jc.setText(myformat1.format((float)(Declare.data_wcxj[8])/8192));
		/*	Declare.save_data[0]=myformat1.format((float)(Declare.data_wcxj[0])/32768);
			Declare.save_data[1]=cur_fromat((float)(Declare.data_wcxj[1])/32768);
			Declare.save_data[2]=myformat1.format((float)(Declare.data_wcxj[2])/32768);
			Declare.save_data[3]=cur_fromat((float)(Declare.data_wcxj[3])/32768);
			Declare.save_data[4]=myformat1.format((float)(Declare.data_wcxj[4])/32768);
			Declare.save_data[5]=cur_fromat((float)(Declare.data_wcxj[5])/32768);
			Declare.save_data[6]=myformat1.format((float)(Declare.data_wcxj[6])/8192);
			Declare.save_data[7]=myformat1.format((float)(Declare.data_wcxj[7])/8192);
			Declare.save_data[8]=myformat1.format((float)(Declare.data_wcxj[8])/8192);*/
	 	}	;
		if(position5!=2){
			qsxs.setText(Declare.data_wcxj[10]+"");
		}
		Error_flag=Declare.data_wcxj[11];//=1;
		if(Error_flag==1){
			Error_flag=0;Declare.data_wcxj[11]=0;
		wcxs.setText(myformat1.format((float)(Declare.data_wcxj[9])/1024));
		data_processing(wc_array,Float.parseFloat(wcxs.getText().toString()));
		wc1.setText(myformat1.format((float)(wc_array[3])));
		wc2.setText(myformat1.format((float)(wc_array[2])));
		wc3.setText(myformat1.format((float)(wc_array[1])));
		wc4.setText(myformat1.format((float)(wc_array[0])));
		wc_pjz=(wc_array[3]+wc_array[2]+wc_array[1]+wc_array[0])/4;
//		System.out.println("误差标志："+Error_flag);
		bzpc=(float)Math.sqrt((Math.pow(wc_array[3]-wc_pjz,2)+Math.pow(wc_array[2]-wc_pjz,2)+
				Math.pow(wc_array[1]-wc_pjz,2)+Math.pow(wc_array[0]-wc_pjz,2))/4);
		wc5.setText(myformat1.format(bzpc)+"");
		wcpj.setText(myformat1.format(wc_pjz)+"");
		if(wg_check_flag==true){
			Declare.save_data[40]=wc1.getText().toString();
			Declare.save_data[41]=wc2.getText().toString();
			Declare.save_data[42]=wc3.getText().toString();
			Declare.save_data[43]=wc4.getText().toString();
			Declare.save_data[44]=wc5.getText().toString();
			
		}
		else{
			Declare.save_data[20]=wc1.getText().toString();
			Declare.save_data[21]=wc2.getText().toString();
			Declare.save_data[22]=wc3.getText().toString();
			Declare.save_data[23]=wc4.getText().toString();
			Declare.save_data[24]=wc5.getText().toString();
		}
		}
	}
	public static void data_processing(float Array[],float Para)//数据先进先出处理
	 {
        Array[0]= Array[1];
        Array[1]= Array[2];
        Array[2]= Array[3];
//	    Array[3]= Array[4];
        Array[3]= Para;
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
	public class mThreadread extends Thread{
		 @SuppressLint("HandlerLeak")
		@Override
		 public void run() {
		 // TODO Auto-generated method stub
			 while(!Thread.currentThread().isInterrupted() && read_flag)
		    	{		    	
		    	try {
		    	  // 每隔1000毫秒触发
		    		int time=0;
		    		if(Declare.send_flag==17){time=1000;}
		    		else{time=200;}
		    		Thread.sleep(time);
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
	 public static Handler mHandler_read = new Handler()
		{										
//			  @SuppressLint("HandlerLeak")
			public void handleMessage(Message msg)										
			  {											
				  super.handleMessage(msg);			
				  if(msg.what == 0)
				  {
//					 while(!Declare.receive_flag){
					while(read_flag){
						 display();
						 break;
					 }
				  }
			  }									
	 };
	 
}
