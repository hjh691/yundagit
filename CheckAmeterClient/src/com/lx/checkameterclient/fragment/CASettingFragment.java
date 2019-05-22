package com.lx.checkameterclient.fragment;


import java.io.File;
import java.text.DecimalFormat;//
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;//
import android.app.Activity;//
import android.app.Dialog;
import android.content.Context;//
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;


import com.lx.checkameter.socket.Toasts;
import com.lx.checkameter.socket.mSocketClient;
import com.lx.checkameterclient.Declare;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.adapter.CAFragmentPagerAdapter;
import com.lx.checkameterclient.view.confirm1;
import com.zxing.activity.CaptureActivity;

public class CASettingFragment extends Fragment {
	// fragment所属的activity对象
	private static Activity activity;
	
	private static SQLiteDatabase sqldb;
	private String sql;
	@SuppressLint("SdCardPath")
	private static File file = new File("/sdcard/bdlx/sxxy.db");// 数据库文件路径
	
	private int object_flag,zthy_flag;
	private Dialog dialog;
	//private CAFragmentPagerAdapter mAdapter;
	
	private static EditText hmsz,bhsz,cssz,qssz,fpsz,dysz,dlsz,dzsz,xlsz,tzsz,jyy,xhy,zdy1,zdy2,zdy3,ssid,ssid_pass;
	private static EditText db_type,db_mfrs,fsdl,psdl,gsdl,zzyg,zzwg,fxwg;
	private static Spinner spinner,spinner1,spinner2,spinner3,spinner4,spinner5;
	
	private  static String temp_bh,temp_hm,temp_cs,temp_qs,temp_dyxs,temp_dlxs,temp_fpxs,
    temp_addr,temp_xl,temp_tz,temp_jyy,temp_xhy,temp_zdy1,temp_zdy2,temp_zdy3,temp_ssid,temp_ssid_pass,
    temp_db_type,temp_db_mfrs,temp_fsdl,temp_psdl,temp_gsdl,temp_zzyg,temp_zzwg,temp_fxwg;
     private int erro_sel;//区别弹出确认框错误
     private DecimalFormat myformat1;
     int val_mcbl;//脉冲倍率
     boolean lock_flag=true;
     int val_auto_manu;//手自动
     private Button btn_read;
     private boolean view_flag=false;
     private static int position1,position2,position3,position4,position5,position6;//记录spinner选相点以便在锁定状态下还原

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 用于读取fragment销毁时保存的数据，以便重建
		Bundle bundle = getArguments();
		// 加载校验数据
		//initCASettingData();
		super.onCreate(savedInstanceState);
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
	}
	// 加载校验数据，读取通信数据、或者之前保存在bundle中的数据，将其显示在UI上
	//private void initCASettingData() {
		// TODO Auto-generated method stub
	//}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.ca_setting_fragment, null);
		Log.d("tag", "inflate  setting fragment ..............");
		Button scanbarcodebtn=(Button)view.findViewById(R.id.scanBarCodeBtn);
		scanbarcodebtn.setOnClickListener(new btn_scan_click());

		bhsz=(EditText)view.findViewById(R.id.ameterNumberET);
		bhsz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		hmsz=(EditText)view.findViewById(R.id.userNameET);
		hmsz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		cssz=(EditText)view.findViewById(R.id.ameterConstantValueET);
		cssz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		qssz=(EditText)view.findViewById(R.id.coilsNumberET);
		qssz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		fpsz=(EditText)view.findViewById(R.id.frequencyDivideRatioET);
		fpsz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		dysz=(EditText)view.findViewById(R.id.voltRatioET);
		dysz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		dlsz=(EditText)view.findViewById(R.id.amphereRatioET);
		dlsz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		dzsz=(EditText)view.findViewById(R.id.userAddressET);
		dzsz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		xlsz=(EditText)view.findViewById(R.id.lineET);
		xlsz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		tzsz=(EditText)view.findViewById(R.id.stationET);
		tzsz.setImeOptions(EditorInfo.IME_ACTION_SEND);
		jyy=(EditText)view.findViewById(R.id.inspectorET);
		jyy.setImeOptions(EditorInfo.IME_ACTION_SEND);
		xhy=(EditText)view.findViewById(R.id.corectorET);
		xhy.setImeOptions(EditorInfo.IME_ACTION_SEND);
		//zdy1=(EditText)view.findViewById(R.id.zdy1);
		//zdy2=(EditText)view.findViewById(R.id.zdy2);
		//zdy3=(EditText)view.findViewById(R.id.zdy3);
		//ssid=(EditText)view.findViewById(R.id.ssid);
		//ssid_pass=(EditText)view.findViewById(R.id.ssid_pass);
		//csh=(TextView)view.findViewById(R.id.zdy3);
		//ver_num=(TextView)view.findViewById(R.id.ver_num);
		//=====电表信息录入==================================
		db_type=(EditText)view.findViewById(R.id.ameterTypeET);
		db_type.setImeOptions(EditorInfo.IME_ACTION_SEND);
		db_mfrs=(EditText)view.findViewById(R.id.ameterFactoryET);
		db_mfrs.setImeOptions(EditorInfo.IME_ACTION_SEND);
		fsdl=(EditText)view.findViewById(R.id.topQuantityET);
		fsdl.setImeOptions(EditorInfo.IME_ACTION_SEND);
		psdl=(EditText)view.findViewById(R.id.normalQuantityET);
		psdl.setImeOptions(EditorInfo.IME_ACTION_SEND);
		gsdl=(EditText)view.findViewById(R.id.bottomQuantityET);
		gsdl.setImeOptions(EditorInfo.IME_ACTION_SEND);
		zzyg=(EditText)view.findViewById(R.id.positiveActivePowerET);
		zzyg.setImeOptions(EditorInfo.IME_ACTION_SEND);
		zzwg=(EditText)view.findViewById(R.id.positiveReactivePowerET);
		zzwg.setImeOptions(EditorInfo.IME_ACTION_SEND);
		fxwg=(EditText)view.findViewById(R.id.negativeReactivePowerET);
		fxwg.setImeOptions(EditorInfo.IME_ACTION_SEND);
		        
		hmsz.setOnFocusChangeListener(new mOnFocus_Listener());
        bhsz.setOnFocusChangeListener(new mOnFocus_Listener());
        cssz.setOnFocusChangeListener(new mOnFocus_Listener());
        qssz.setOnFocusChangeListener(new mOnFocus_Listener());
        fpsz.setOnFocusChangeListener(new mOnFocus_Listener());
        dysz.setOnFocusChangeListener(new mOnFocus_Listener());
        dlsz.setOnFocusChangeListener(new mOnFocus_Listener());
        dzsz.setOnFocusChangeListener(new mOnFocus_Listener());
        xlsz.setOnFocusChangeListener(new mOnFocus_Listener());
        tzsz.setOnFocusChangeListener(new mOnFocus_Listener());
        jyy.setOnFocusChangeListener(new mOnFocus_Listener());
        xhy.setOnFocusChangeListener(new mOnFocus_Listener());
        //zdy1.setOnFocusChangeListener(new mOnFocus_Listener());
        //zdy2.setOnFocusChangeListener(new mOnFocus_Listener());
        //zdy3.setOnFocusChangeListener(new mOnFocus_Listener());
        //ssid.setOnFocusChangeListener(new mOnFocus_Listener());
        //ssid_pass.setOnFocusChangeListener(new mOnFocus_Listener());
        //==========电表信息================================
        db_type.setOnFocusChangeListener(new mOnFocus_Listener());
        db_mfrs.setOnFocusChangeListener(new mOnFocus_Listener());
        fsdl.setOnFocusChangeListener(new mOnFocus_Listener());
        psdl.setOnFocusChangeListener(new mOnFocus_Listener());
        gsdl.setOnFocusChangeListener(new mOnFocus_Listener());
        zzyg.setOnFocusChangeListener(new mOnFocus_Listener());
        zzwg.setOnFocusChangeListener(new mOnFocus_Listener());
        fxwg.setOnFocusChangeListener(new mOnFocus_Listener());

		hmsz.setOnEditorActionListener(new mOnKey_Listerner());
        bhsz.setOnEditorActionListener(new mOnKey_Listerner());
        cssz.setOnEditorActionListener(new mOnKey_Listerner());
        qssz.setOnEditorActionListener(new mOnKey_Listerner());
        fpsz.setOnEditorActionListener(new mOnKey_Listerner());
        dysz.setOnEditorActionListener(new mOnKey_Listerner());
        dlsz.setOnEditorActionListener(new mOnKey_Listerner());
        dzsz.setOnEditorActionListener(new mOnKey_Listerner());
        xlsz.setOnEditorActionListener(new mOnKey_Listerner());
        tzsz.setOnEditorActionListener(new mOnKey_Listerner());
        jyy.setOnEditorActionListener(new mOnKey_Listerner());
        xhy.setOnEditorActionListener(new mOnKey_Listerner());
        //zdy1.setOnEditorActionListener(new mOnKey_Listerner());
        //zdy2.setOnEditorActionListener(new mOnKey_Listerner());
        //zdy3.setOnEditorActionListener(new mOnKey_Listerner());
       //ssid.setOnEditorActionListener(new mOnKey_Listerner());
        //ssid_pass.setOnEditorActionListener(new mOnKey_Listerner());
        //====================电表信息================================
        db_type.setOnEditorActionListener(new mOnKey_Listerner());
        db_mfrs.setOnEditorActionListener(new mOnKey_Listerner());
        fsdl.setOnEditorActionListener(new mOnKey_Listerner());
        psdl.setOnEditorActionListener(new mOnKey_Listerner());
        gsdl.setOnEditorActionListener(new mOnKey_Listerner());
        zzyg.setOnEditorActionListener(new mOnKey_Listerner());
        zzwg.setOnEditorActionListener(new mOnKey_Listerner());
        fxwg.setOnEditorActionListener(new mOnKey_Listerner());
        myformat1= new DecimalFormat("0.00");
        spinner = (Spinner) view.findViewById(R.id.ameterStandardSpinner);
        spinner1 = (Spinner) view.findViewById(R.id.ameterGradeSpinner);
        spinner2 = (Spinner) view.findViewById(R.id.voltRangeSpinner);
        spinner2.setEnabled(false);
        spinner3 = (Spinner) view.findViewById(R.id.amphereRangeSpinner);
        spinner5 = (Spinner) view.findViewById(R.id.pulseModeSpinner);
        get_config();//获取保存的偏好项参数；
      //=======调取参数按钮========================
        btn_read=(Button)view.findViewById(R.id.btn_read);
		btn_read.setOnClickListener(new btn_click());
      //========================制式=============================
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
        	@Override
        	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//        		Spinner spinner = (Spinner)adapterView;
//        		String itemContent = (String)adapterView.getItemAtPosition(position);
        		if(zthy_flag==0){
        			if(lock_flag==true){
        				if(Declare.Clientisconnect==true && Declare.rec_err==false){
        					Declare.send_set_flag=12;
        					Declare.send_set_data=(short)(position+1);
        					mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
        					try {
    							Thread.sleep(100);
    						 } catch (Exception e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    					  	}
        					if(Integer.parseInt(set_para(9))==(position+1)){
        						save_config(2);
        						SaveRecord(bhsz.getText().toString(),2);
//        						SaveRecordAll(bhsz.getText().toString());
        						position1=position;
        						if(position==0 ||position==2){
        							CAErrorTestingFragment.wg_check_flag=false;
        						}
        						if(position==1 || position==3){
        							CAErrorTestingFragment.wg_check_flag=true;
        						}
        						DisplayToast("电表制式设置成功！！");
        					}
        					else{
        						DisplayToast("电表制式设置失败！！");
        						spinner.setSelection(position1,true);
        						//spinner.setOnItemSelectedListener(this);
        						//zthy_flag=1;
        					}
        				}
        				else{erro_sel=2;comfir_display();}//
        			}
        			else{erro_sel=1;comfir_display();}//
        			object_flag=1;
        		}
        		else{zthy_flag=0;}
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> view) {
        		 //       		Log.i(TAG,  view.getClass().getName());
        	}
        });
      //==================电表等级====================
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
         	@Override
         	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
         		if(zthy_flag==0){
         			if(lock_flag==true){
         				if(Declare.Clientisconnect==true && Declare.rec_err==false){
         				SaveRecord(bhsz.getText().toString(),3);
//         				SaveRecordAll(bhsz.getText().toString());
         				position2=position;
         				save_config(3);
         				DisplayToast("电表等级设置成功！！");
         				}
         				else{erro_sel=2;comfir_display();}//
         			}
         			else{erro_sel=1;comfir_display();}//
         			object_flag=2;
         		}
        		else{zthy_flag=0;}
         	}
         	@Override
         	public void onNothingSelected(AdapterView<?> view) {
  //       		Log.i(TAG,  view.getClass().getName());
         	}
         });
      //===============电压量程===========================
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
          	@Override
          	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
          		if(zthy_flag==0){
          			if(lock_flag==true){
          				if(Declare.Clientisconnect==true && Declare.rec_err==false){
          					Declare.send_set_flag=7;
          					Declare.send_set_data=(short)(position+1);
          					mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
          					try {
    							Thread.sleep(100);
    						 } catch (Exception e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    					  	}
          					if(Integer.parseInt(set_para(2))==(position+1)){
          						SaveRecord(bhsz.getText().toString(),4);
//          					SaveRecordAll(bhsz.getText().toString());
          						save_config(4);
          						position3=position;
          						DisplayToast("电压量程设置成功！！");
          					}
          					else{
          						DisplayToast("电压量程设置失败！！");
        						spinner2.setSelection(position3,true);
        						//spinner2.setOnItemSelectedListener(this);
        						//zthy_flag=1;
          					}
          				}
          				else{erro_sel=2;comfir_display();}//
          			}
          			else{erro_sel=1;comfir_display();}//
          			object_flag=3;
          		}
        		else{zthy_flag=0;}
          	}
          	@Override
          	public void onNothingSelected(AdapterView<?> view) {
   //       		Log.i(TAG,  view.getClass().getName());
          	}
          });
      //===================电流量程=============================
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
           	@Override
           	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
           		if(zthy_flag==0){
           			if(lock_flag==true){
           				int new_position;
           				if(position>0){
           					new_position=position+4;
           				}
           				else{
           					new_position=position+1;
           				}
           				if(Declare.Clientisconnect==true && Declare.rec_err==false){
           					Declare.send_set_flag=13;
           					Declare.send_set_data=(short)(new_position);
           					mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
           					try {
           						Thread.sleep(100);
           					 } catch (Exception e) {
           						// TODO Auto-generated catch block
           						e.printStackTrace();
           				  	}	
           					if(Integer.parseInt(set_para(3))==(new_position)){
           						SaveRecord(bhsz.getText().toString(),5);
 //          					SaveRecordAll(bhsz.getText().toString());
           						save_config(5);
           						position4=position;
           						DisplayToast("电流量程设置成功！！");
           					}
           					else{
           						DisplayToast("电流量程设置失败！！");
           						spinner3.setSelection(position4,true);
        						//spinner3.setOnItemSelectedListener(this);
           						//zthy_flag=1;
           					}
           				 }
           				else{erro_sel=2;comfir_display();}//
           			}
           			else{erro_sel=1;comfir_display();}//
           			object_flag=4;
           		}
        		else{zthy_flag=0;}
           	}
           	@Override
           	public void onNothingSelected(AdapterView<?> view) {
    //       		Log.i(TAG,  view.getClass().getName());
           	}
           });
      //===================脉冲方式=============================
        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
        	@Override
        	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        		if(zthy_flag==0){
        			if(lock_flag==true){
        				if(Declare.Clientisconnect==true && Declare.rec_err==false){
        				   Declare.send_set_flag=14;
       						Declare.send_set_data=(short)(position+1);
       						mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
       						try {
       							Thread.sleep(100);
       						 } catch (Exception e) {
       							// TODO Auto-generated catch block
       							e.printStackTrace();
       					  	}	
       						if(Integer.parseInt(set_para(4))==(position+1)){
       							SaveRecord(bhsz.getText().toString(),6);
       							save_config(20);
       							position6=position;
       							DisplayToast("脉冲方式设置成功！！");
       						}
       						else{
       							DisplayToast("脉冲方式设置失败！！");
       							spinner5.setSelection(position6,true);
       							//zthy_flag=1;
       						}
        				}
        				else{erro_sel=2;comfir_display();}//
        			}
        			else{erro_sel=1;comfir_display();}//
        			object_flag=21;
        		}
        		else{zthy_flag=0;}
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> view) {
 //       		Log.i(TAG,  view.getClass().getName());
        	}
        });
/*20160323
        image_suo=(ImageView)findViewById(R.id.image_suo);
        slip_btn1 = (SlipButton) findViewById(R.id.slip_btn1);// 获得指定控件 
        slip_btn1.SetOnChangedListener(new mslip_listener());
        //=======调取参数按钮========================
        btn_read=(ImageButton)findViewById(R.id.btn_read);
		btn_read.setOnClickListener(new btn_click());
        btn_read.setEnabled(false);
        btn_read.setImageResource(R.drawable.btn_read2);
        
		*/
       // get_check();//取得初始化wifi及socket设置
        //sub_para_read();
        view_flag=true;
		return view;
	}
	//========侦听参数调取按钮==================
	 public class btn_click implements OnClickListener {               
	    	@Override            
	    	public void onClick(View v) { 
	    		switch (v.getId()){
	    		case R.id.btn_read:
	    			sub_para_read();//调用前端参数
	    			break;
	    		}
	    	}
		}
	 class Call_Para implements Runnable {
		 @Override
	     public void run() {
            synchronized(this){
    			for(int i=0;i<1500;i++){
    				try {
    					Thread.sleep(10);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				if(Declare.data_para[0]==9){
    					Message msg = new Message();
						msg.what = 0;
						mHandler.sendMessage(msg);	
    					break;
    				}
    				if(i>=1499)
    				   {
    						Message msg = new Message();
    						msg.what = 1;
    						mHandler.sendMessage(msg);
    					}
    			}
            }
	        }
	 }	 
	 //========调取参数子程序=========================
	 public void sub_para_read(){
		 if(Declare.Clientisconnect==true && Declare.rec_err==false){
			//====清空数据===================
				for(int j=0;j<Declare.data_para.length;j++){
					Declare.data_para[j]=0;
				}
				mSocketClient.sendover_flag=false;
				mSocketClient.send_msg(6,(short)0);
				try {
					Thread.sleep(100);
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			  	}	
		    new Thread(new Call_Para()).start();
		 }
		 else{
			 erro_sel=2;
			 comfir_display();
		 }
	 }
	//侦听焦点
	private class mOnFocus_Listener implements OnFocusChangeListener{
		@Override 
			public void onFocusChange(View v, boolean hasFocus) {
			String str=bhsz.getText().toString();
			if (v.hasFocus() == false) {
				if(zthy_flag==0){//2016032309
					if(lock_flag==true ){
						switch (v.getId()){				
						//表号设置
						case R.id.ameterNumberET:
							if(!bhsz.getText().toString().equals("")){
								if(!bhsz.getText().toString().equals(temp_bh)){
									temp_bh=bhsz.getText().toString().trim();
									SaveRecord(str,0);
	//									SaveRecordAll(str);
									save_config(0);						
									DisplayToast("表号设置成功！！");
									loadsettings(str);
									for(int i=0;i<Declare.save_data.length;i++){
										Declare.save_data[i]="";
									}
								}
							}
							else{
								DisplayToast("表号值不能为空！！");
								bhsz.setText(temp_bh);
							}
							break;								
							//户名设置
						case R.id.userNameET:
							temp_hm=hmsz.getText().toString().trim();
							SaveRecord(str,1);
//							SaveRecordAll(str);
							save_config(1);	
							DisplayToast("用户名称设置成功！！");	
							break;
							//常数设置
						case R.id.ameterConstantValueET:
							if(Declare.Clientisconnect==true && Declare.rec_err==false){
								if(!cssz.getText().toString().equals("")){
									int constant= Integer.parseInt(cssz.getText().toString().trim());
		        					if(constant>=1 && constant<=32767)
	        						{
	        						    String temp_cs1="";
	        							cssz.setText(String.valueOf(constant));		        					
	        							temp_cs1=cssz.getText().toString().trim();
	        							Declare.send_set_flag=8;
	        							Declare.send_set_data=Short.parseShort(temp_cs1);
	        							mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
	        							try {
	        								Thread.sleep(500);
	        							 } catch (Exception e) {
	        								// TODO Auto-generated catch block
	        								e.printStackTrace();
	        						  	}	
	        							int value=Integer.parseInt(set_para(1));
	        							System.out.println(value);
	        							if(value==constant){
	        								temp_cs=temp_cs1;
	        								SaveRecord(str,7);
//			        						SaveRecordAll(str);
	        								save_config(7);
	        								DisplayToast("常数设置成功！！");
	        							}
	        							else{
	        								DisplayToast("常数设置失败！！");
	        								cssz.setText(temp_cs);
	        							}
	        						}
		        					else{
	        							cssz.setText(temp_cs);
	        							DisplayToast("请输入1到32767的数值"); 
		        					}
								}
								else{
									DisplayToast("常数值不能为空！！");
									cssz.setText(temp_cs);
								}
							}
							else{erro_sel=2;comfir_display();}//未解锁进行消息框提示
							break;
							//圈数设置
						 case R.id.coilsNumberET:
							 if(Declare.Clientisconnect==true && Declare.rec_err==false){ 
								 if(!qssz.getText().toString().equals("")){
									 int constant= Integer.parseInt(qssz.getText().toString().trim());
		        					 if(constant>1 && constant<=9999)
		        					 {
	        							 String temp_qs1="";
	        							 qssz.setText(String.valueOf(constant));		        					
	        							 temp_qs1=qssz.getText().toString().trim();
	        							 Declare.send_set_flag=9;
										 Declare.send_set_data=Short.parseShort(temp_qs1);
										 mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
										 try {
										     Thread.sleep(100);
										 } catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
									  	 }	
										 int value=Integer.parseInt(set_para(5));			        							
	        							 if(value==constant){
	        								 temp_qs=temp_qs1;
	        								 SaveRecord(str,8);
	        								 save_config(8);
	        								 DisplayToast("圈数设置成功！！");
	        							 }
	        							 else{
	        								 qssz.setText(temp_qs);
	        								 DisplayToast("圈数设置失败！！");
	        							 }
	        						 }
		        					 else{
	        					 		 qssz.setText(temp_qs);
	        					 		 DisplayToast("请输入1到9999的数值"); 
		        				     }
								 }
								 else{
									 DisplayToast("圈数值不能为空！！");
									 qssz.setText(temp_qs);
								 }
							 }
							 else{erro_sel=2;comfir_display();}//未解锁进行消息框提示
							 break;
						//电压系数设置
						 case R.id.voltRatioET:
							 if(Declare.Clientisconnect==true && Declare.rec_err==false){  
								 if(!dysz.getText().toString().equals("")){																
									 temp_dyxs=dysz.getText().toString().trim();
									 SaveRecord(str,9);
									save_config(9);
									DisplayToast("电压系数设置成功！！");	
						
								 }
								 else{
						
									 DisplayToast("电压系数值不能为空！！");
									 dysz.setText(temp_dyxs);
								 }
							 }
							 else{erro_sel=2;comfir_display();}//未解锁进行消息框提示 
							break;
							//电流系数设置
						case R.id.amphereRatioET:
							if(Declare.Clientisconnect==true && Declare.rec_err==false){  
								if(!dlsz.getText().toString().equals("")){
									String temp_dlxs1="";
									temp_dlxs1=dlsz.getText().toString().trim();									
									Declare.send_set_flag=10;
									Declare.send_set_data=Short.parseShort(temp_dlxs1);
//										Declare.Circle=false;				
//										mSocketClient.SendClientmsg(Declare.Circle);	
									mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
									try {
										Thread.sleep(100);
									 } catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
								  	}	
									if(set_para(7).equals(temp_dlxs1)){
										temp_dlxs=temp_dlxs1;
										SaveRecord(str,10);
//											SaveRecordAll(str);
										save_config(10);
										DisplayToast("电流系数设置成功！！");	
									}
									else{
										dlsz.setText(temp_dlxs); 
										DisplayToast("电流系数设置失败！！");	
									}
								}
								else{
						
									DisplayToast("电流系数值不能为空！！");
									dlsz.setText(temp_dlxs);
								}
							}
							else{erro_sel=2;comfir_display();}//未解锁进行消息框提示 
							break;
							//分频系数设置
						case R.id.frequencyDivideRatioET:
							if(Declare.Clientisconnect==true && Declare.rec_err==false){  
								if(!fpsz.getText().toString().equals("")){	
									String temp_fpxs1="";
									temp_fpxs1=fpsz.getText().toString().trim();									
									Declare.send_set_flag=11;
									Declare.send_set_data=Short.parseShort(temp_fpxs1);
									mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
									try {
										Thread.sleep(100);
									 } catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
								  	}	
									if(set_para(6).equals(temp_fpxs1)){
										temp_fpxs=temp_fpxs1;
										SaveRecord(str,11);
//											SaveRecordAll(str);
										DisplayToast("分频系数设置成功！！");	
									}
									else{
										fpsz.setText(temp_fpxs);
										DisplayToast("分频系数设置失败！！");	
									}
								}
								else{
									DisplayToast("分频系数值不能为空！！");
									fpsz.setText(temp_fpxs);
								}
							}
							else{erro_sel=2;comfir_display();}//未解锁进行消息框提示 
							break;
						//地址设置
						case R.id.userAddressET:
							temp_addr=dzsz.getText().toString().trim();
							SaveRecord(str,12);
//							SaveRecordAll(str);
							DisplayToast("地址设置成功！！");	
							break;
						//线路设置
						case R.id.lineET:
							temp_xl=xlsz.getText().toString().trim();
							SaveRecord(str,13);
//								SaveRecordAll(str);
							DisplayToast("线路设置成功！！");							
							break;
							//台站设置
						case R.id.stationET:
							temp_tz=tzsz.getText().toString().trim();
							SaveRecord(str,14);
//								SaveRecordAll(str);
							DisplayToast("台站设置成功！！");	
							break;
							//检验员设置
						case R.id.inspectorET:
							temp_jyy=jyy.getText().toString().trim();
							SaveRecord(str,15);
//							SaveRecordAll(str);
							DisplayToast("检验员设置成功！！");	
							break;
							//校核员设置
						case R.id.corectorET:
							temp_xhy=xhy.getText().toString().trim();
							SaveRecord(str,16);
//							SaveRecordAll(str);
							DisplayToast("校核员设置成功！！");	
							break;
					/*		//自定义1设置
							case R.id.zdy1:
								temp_zdy1=zdy1.getText().toString().trim();
								SaveRecord(str,17);
//								SaveRecordAll(str);
								break;
								//自定义2设置
							case R.id.zdy2:
								temp_zdy2=zdy2.getText().toString().trim();
								SaveRecord(str,18);
//								SaveRecordAll(str);
								break;
								//自定义3设置
							case R.id.zdy3:
								temp_zdy3=zdy3.getText().toString().trim();
								SaveRecord(str,19);
//								SaveRecordAll(str);
								break;
								//配置热点名称
							case R.id.ssid:
								save_config(21);
								DisplayToast("热点名称设置成功！！");	
								break;
								//配置热点名称
							case R.id.ssid_pass:
								save_config(22);
								DisplayToast("密码设置成功！！");	
								break;
							*/	//电表型号
						case R.id.ameterTypeET:
							DisplayToast("电表型号设置成功！！");	
							break;
							//电表厂家
						case R.id.ameterFactoryET:
							DisplayToast("电表厂家设置成功！！");	
							break;
							//峰时电量
						case R.id.topQuantityET:
							if(!fsdl.getText().toString().equals("")){
								String temp_fsdl1="";
								temp_fsdl1=myformat1.format(Float.parseFloat(fsdl.getText().toString().trim()));
								fsdl.setText(temp_fsdl1);
								DisplayToast("峰时电量设置成功！！");	
							}
							else{
								DisplayToast("峰时电量不能为空！！");
								fsdl.setText("0.00");
							}
							break;
							//平时电量
						case R.id.normalQuantityET:
							if(!psdl.getText().toString().equals("")){
								String temp_psdl1="";
								temp_psdl1=myformat1.format(Float.parseFloat(psdl.getText().toString().trim()));
								psdl.setText(temp_psdl1);
								DisplayToast("平时电量设置成功！！");	
							}
							else{
								DisplayToast("平时电量不能为空！！");
								psdl.setText("0.00");
							}
							break;
							//谷时电量
						case R.id.bottomQuantityET:
							if(!gsdl.getText().toString().equals("")){
								String temp_gsdl1="";
								temp_gsdl1=myformat1.format(Float.parseFloat(gsdl.getText().toString().trim()));
								gsdl.setText(temp_gsdl1);
								DisplayToast("谷时电量设置成功！！");	
							}
							else{
								DisplayToast("谷时电量不能为空！！");
								gsdl.setText("0.00");
							}
							break;
							//正总有功
						case R.id.positiveActivePowerET:
							if(!zzyg.getText().toString().equals("")){
								String temp_zzyg1="";
								temp_zzyg1=myformat1.format(Float.parseFloat(zzyg.getText().toString().trim()));
								zzyg.setText(temp_zzyg1);
								DisplayToast("正向总有功设置成功！！");	
							}
							else{
								DisplayToast("正向总有功不能为空！！");
								zzyg.setText("0.00");
							}
							break;
							//正总无功
						case R.id.positiveReactivePowerET:
							if(!zzwg.getText().toString().equals("")){
								String temp_zzwg1="";
								temp_zzwg1=myformat1.format(Float.parseFloat(zzwg.getText().toString().trim()));
								zzwg.setText(temp_zzwg1);
								DisplayToast("正向总无功设置成功！！");	
							}
							else{
								DisplayToast("正向总无功不能为空！！");
								zzwg.setText("0.00");
							}
							break;
							//负向无功
						case R.id.negativeReactivePowerET:
							if(!fxwg.getText().toString().equals("")){
								String temp_fxwg1="";
								temp_fxwg1=myformat1.format(Float.parseFloat(fxwg.getText().toString().trim()));
								fxwg.setText(temp_fxwg1);
								DisplayToast("负向无功设置成功！！");	
							}
							else{
								DisplayToast("负向有功不能为空！！");
								fxwg.setText("0.00");
							}
							break;
						}
					}
					else{erro_sel=1;comfir_display();}//未解锁进行消息框提示
					switch (v.getId()){				
					//表号设置
					case R.id.ameterNumberET:
						object_flag=6;
						break;												
						//户名设置
					case R.id.userNameET:
						object_flag=7;
						break;											
						//常数设置
					case R.id.ameterConstantValueET:
						object_flag=8;		        									
					break;
					//圈数设置
					case R.id.coilsNumberET:
						object_flag=9;
					break;
					//电压系数设置
					case R.id.voltRatioET:
						object_flag=10;
					break;
					//电流系数设置
					case R.id.amphereRatioET:
						object_flag=11;
					break;
					//分频系数设置
					case R.id.frequencyDivideRatioET:
						object_flag=12;
					break;				
					//地址设置
					case R.id.userAddressET:
						object_flag=13;	        									
					break;
					//线路设置
					case R.id.lineET:
						object_flag=14;	        									
						break;
						//台站设置
					case R.id.stationET:
						object_flag=15;	        									
						break;
						//检验员设置
					case R.id.inspectorET:
						object_flag=16;	        									
					break;
					//校核员设置
					case R.id.corectorET:
						object_flag=17;					
					break;
				/*	//自定义1设置
					case R.id.zdy1:
						object_flag=18;					
					break;
					//自定义2设置
					case R.id.zdy2:
						object_flag=19;					
					break;
					//自定义3设置
					case R.id.zdy3:
						object_flag=20;					
					break;
					//配置热点名称
					case R.id.ssid:
						object_flag=22;					
					break;
					//配置接入密码
					case R.id.ssid_pass:
						object_flag=23;					
					break;
				*/	
					}
				}//20160323
			else{zthy_flag=0;}
			}
		//	if (v.hasFocus() == true){
			//}
		} 
	}
		public void DisplayToast(String msg){
	    	
//		    Toast toast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		    //设置toast显示位置
//		    toast.setGravity(Gravity.TOP, 0, 220);
//		    toast.show();
			Toasts.toast(activity, msg, Toast.LENGTH_SHORT,Gravity.TOP, 0, 520);
		    }
		public static void SaveRecord(String DBID,int i) {
			String sql=null;
			if(!sqldb.isOpen())	 {
	 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	 	}
			
//			save_data_flag=true;
//	 		Object[] ags ={myformat.format(id),text_date1.getText().toString()+text_time1.getText().toString(),"保定朗信","竞秀街创业中心","A00001","B00001","薄谷开来","薄谷开来"};
	 		//=========查询是否有相同记录再插入数据==========================
	 		Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=?", new String[]{DBID});
	 		try { 	             
	 		if(cursor.getCount()==0){
	 			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
					String  date =  sDateFormat.format(new java.util.Date());	  
	 			switch(i)
	 				  {	
	 				  	case 0:
	 					    sql = "insert into sxxy_data ('dbid','cs_time') values('" + DBID+ "','"+date+"')";  
	 					  	break;
	 				  	case 1:
	 	 					sql = "insert into sxxy_data ('dbid','cs_time','name') values('" + DBID+ "','"+date+ "','" +hmsz.getText().toString()+"')";  
	 	 					break;
	 				  	case 2:
	 				  		Declare.dbzs=spinner.getSelectedItem().toString();
		 					sql = "insert into sxxy_data ('dbid','cs_time','dbzs') values('" + DBID+ "','"+date+ "','" + spinner.getSelectedItem().toString()+"')";  
		 					break;
	 				  	case 3:
	 				  		Declare.dbdj=spinner1.getSelectedItem().toString();
		 					sql = "insert into sxxy_data ('dbid','cs_time','dbdj') values('" + DBID+ "','"+date+ "','" + spinner1.getSelectedItem().toString()+"')";  
		 					break;
	 				  	case 4:
	 				  		Declare.dylc=spinner2.getSelectedItem().toString();
		 					sql = "insert into sxxy_data ('dbid','cs_time','dylc') values('" + DBID+ "','"+date+ "','" + spinner2.getSelectedItem().toString()+"')";  
		 					break;
	 				  	case 5:
	 				  		Declare.dllc=spinner3.getSelectedItem().toString();
		 					  sql = "insert into sxxy_data ('dbid','cs_time','dllc') values('" + DBID+ "','"+date+ "','" + spinner3.getSelectedItem().toString()+"')";  
		 					break;
	 				  	case 6:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','xbfs') values('" + DBID+ "','"+date+ "','" + spinner5.getSelectedItem().toString()+"')";  
		 					break;
	 				  	case 7:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','dbcs') values('" + DBID+ "','"+date+ "','" + cssz.getText().toString()+"')";  
		 					break;
	 				  	case 8:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','xbqs') values('" + DBID+ "','"+date+ "','" + qssz.getText().toString()+"')";  
		 					break;
	 				  	case 9:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','dyxs') values('" + DBID+ "','"+date+ "','" + dysz.getText().toString()+"')";  
		 					break;
	 				  	case 10:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','dlxs') values('" + DBID+ "','"+date+ "','" + dlsz.getText().toString()+"')";  
		 					break;
	 				  	case 11:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','fpxs') values('" + DBID+ "','"+date+ "','" + fpsz.getText().toString()+"')";  
		 					break;
	 				  	case 12:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','addr') values('" + DBID+ "','"+date+ "','" + dzsz.getText().toString()+"')";  
		 					break;
	 				  	case 13:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','xianl') values('" + DBID+ "','"+date+ "','" + xlsz.getText().toString()+"')";  
		 					break;
	 				  	case 14:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','taiz') values('" + DBID+ "','"+date+ "','" + tzsz.getText().toString()+"')";  
		 					break;
	 				  	case 15:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','jyy') values('" + DBID+ "','"+date+ "','" + jyy.getText().toString()+"')";  
		 					break;
	 				  	case 16:
		 					  sql = "insert into sxxy_data ('dbid','cs_time','jhy') values('" + DBID+ "','"+date+ "','" + xhy.getText().toString()+"')";  
		 					break;/*
	 				  	case 17:
		 					  sql = "insert into sxxy_data ('dbid','zdy1') values('" + DBID+ "','" + zdy1.getText().toString()+"')";  
		 					break;
	 				  	case 18:
		 					  sql = "insert into sxxy_data ('dbid','zdy2') values('" + DBID+ "','" + zdy2.getText().toString()+"')";  
		 					break;
	 				  	case 19:
		 					  sql = "insert into sxxy_data ('dbid','zdy3') values('" + DBID+ "','" + zdy3.getText().toString()+"')";  
		 					break;*/
	 				  }      	 
	 			      try { 
	 			            sqldb.execSQL(sql);  
	 			          } catch (SQLException e) {   } 
	 		}
	 		else
	 		{
	 				Object[] ags=null;  
	 				   switch(i){
	 				   case 0:
	 					  break;
	 				   case 1:
	 					   sql="update sxxy_data set name=? where dbid=?";
	 					   ags=new Object[]{hmsz.getText().toString(),DBID};	    
	 			          break;
	 				   case 2:
	 					  Declare.dbzs=spinner.getSelectedItem().toString(); 
						  sql="update sxxy_data set dbzs=? where dbid=?";
				          ags=new Object[]{spinner.getSelectedItem().toString(),DBID};	    
				          break;
	 				   case 3:
	 					  Declare.dbdj=spinner1.getSelectedItem().toString(); 
						  sql="update sxxy_data set dbdj=? where dbid=?";
				          ags=new Object[]{spinner1.getSelectedItem().toString(),DBID};	    
				          break;
	 				   case 4:
	 					  Declare.dylc=spinner2.getSelectedItem().toString(); 
						  sql="update sxxy_data set dylc=? where dbid=?";
				          ags=new Object[]{spinner2.getSelectedItem().toString(),DBID};	    
				          break;   
	 				   case 5:
	 					  Declare.dllc=spinner3.getSelectedItem().toString(); 
						  sql="update sxxy_data set dllc=? where dbid=?";
				          ags=new Object[]{spinner3.getSelectedItem().toString(),DBID};	    
				          break;
	 				   case 6:
						  sql="update sxxy_data set xbfs=? where dbid=?";
				          ags=new Object[]{spinner5.getSelectedItem().toString(),DBID};	    
				          break;
	 				   case 7:
						  sql="update sxxy_data set dbcs=? where dbid=?";
				          ags=new Object[]{cssz.getText().toString(),DBID};	    
				          break;
	 				   case 8:
						  sql="update sxxy_data set xbqs=? where dbid=?";
				          ags=new Object[]{qssz.getText().toString(),DBID};	    
				          break;
	 				   case 9:
						  sql="update sxxy_data set dyxs=? where dbid=?";
				          ags=new Object[]{dysz.getText().toString(),DBID};	    
				          break;
	 				   case 10:
						  sql="update sxxy_data set dlxs=? where dbid=?";
				          ags=new Object[]{dlsz.getText().toString(),DBID};	    
				          break;
	 				   case 11:
						  sql="update sxxy_data set fpxs=? where dbid=?";
				          ags=new Object[]{fpsz.getText().toString(),DBID};	    
				          break;
	 				   case 12:
						  sql="update sxxy_data set addr=? where dbid=?";
				          ags=new Object[]{dzsz.getText().toString(),DBID};	    
				          break;
	 				   case 13:
						  sql="update sxxy_data set xianl=? where dbid=?";
				          ags=new Object[]{xlsz.getText().toString(),DBID};	    
				          break;
	 				   case 14:
						  sql="update sxxy_data set taiz=? where dbid=?";
				          ags=new Object[]{tzsz.getText().toString(),DBID};	    
				          break;
	 				   case 15:
						  sql="update sxxy_data set jyy=? where dbid=?";
				          ags=new Object[]{jyy.getText().toString(),DBID};	    
				          break;
	 				   case 16:
						  sql="update sxxy_data set jhy=? where dbid=?";
				          ags=new Object[]{xhy.getText().toString(),DBID};	    
				          break;/*
	 				   case 17:
						  sql="update sxxy_data set zdy1=? where dbid=?";
				          ags=new Object[]{zdy1.getText().toString(),DBID};	    
				          break;
	 				   case 18:
						  sql="update sxxy_data set zdy2=? where dbid=?";
				          ags=new Object[]{zdy2.getText().toString(),DBID};	    
				          break;
	 				   case 19:
						  sql="update sxxy_data set zdy3=? where dbid=?";
				          ags=new Object[]{zdy3.getText().toString(),DBID};	    
				          break;*/
	 				  }  				 
	 			      try {  
	 			         	if(i!=0){  
	 			    	  	sqldb.execSQL(sql,ags); 
	 			         	}
	 			         } catch (SQLException e) {  }
	 		}
	 		 }catch (Exception e) {  }
	        finally{
	       	 if(cursor!=null){cursor.close();}
	        }
//	 		save_data_flag=false;
	 	}
	 	//==========保存所有设置数据====================
 	 	public static  void SaveRecordAll(String DBID) {
 	 	if(!sqldb.isOpen())	 {
 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
 	 	}
 	 	//=========查询是否有相同记录再插入数据==========================
 	 		Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=?", new String[]{DBID});
 	 		try{	             
 	 		if(cursor.getCount()==0){
 	 			String sql = "insert into " + "sxxy_data" + " (" +  
 	 			        "'dbid','name','addr','xianl','taiz','jyy','jhy','dbzs','dbdj','dylc','dllc','xbfs'," +
 	 			        "'dbcs','xbqs','dyxs','dlxs','fpxs','zdy1','zdy2','zdy3','db_type','db_mfrs','fsdl',"+
 	 			        "'psdl','gsdl','zzyg','zzwg','fxwg') " +
 	 			        "values('" + DBID+ "','"+hmsz.getText().toString()+"','"+dzsz.getText().toString()+"','"+xlsz.getText().toString()+
 	 			        "','"+tzsz.getText().toString()+"','"+jyy.getText().toString()+"','"+xhy.getText().toString()+"','"+spinner.getSelectedItem().toString()+
 	 			        "','"+spinner1.getSelectedItem().toString()+"','"+spinner2.getSelectedItem().toString()+"','"+spinner3.getSelectedItem().toString()+
 	 			        "','"+spinner5.getSelectedItem().toString()+"','"+cssz.getText().toString()+"','"+qssz.getText().toString()+"','"+dysz.getText().toString()+
 	 			        "','"+dlsz.getText().toString()+"','"+fpsz.getText().toString()+"','"+""+"','"+""+"','"+""+
 	 			        "','"+db_type.getText().toString()+"','"+db_mfrs.getText().toString()+"','"+fsdl.getText().toString()+"','"+psdl.getText().toString()+"','"+gsdl.getText().toString()+
 	 			        "','"+zzyg.getText().toString()+"','"+zzwg.getText().toString()+"','"+fxwg.getText().toString()+"')";  
 	 			 try {  
 	 			         sqldb.execSQL(sql);  
 	 			     } catch (SQLException e) {   } 
 	 		}
 	 		else
 	 		{
 	 			 String sql="update sxxy_data set name=?,addr=?,xianl=?,taiz=?,jyy=?,jhy=?,dbzs=?,dbdj=?,dylc=?,dllc=?," +
 	 			         "xbfs=?,dbcs=?,xbqs=?,dyxs=?,dlxs=?,fpxs=?,zdy1=?,zdy2=?,zdy3=?,db_type=?,db_mfrs=?,"+
 	 					 "fsdl=?,psdl=?,gsdl=?,zzyg=?,zzwg=?,fxwg=? where dbid=?";
 	 			         		
 	 			 Object[] ags={hmsz.getText().toString(),dzsz.getText().toString(),xlsz.getText().toString(),tzsz.getText().toString()
 	 			         	,jyy.getText().toString(),xhy.getText().toString(),spinner.getSelectedItem().toString(),spinner1.getSelectedItem().toString()
 	 			         	,spinner2.getSelectedItem().toString(),spinner3.getSelectedItem().toString(),spinner5.getSelectedItem().toString(),cssz.getText().toString()
 	 			         	,qssz.getText().toString(),dysz.getText().toString(),dlsz.getText().toString(),fpsz.getText().toString(),""
 	 			         	,"","",db_type.getText().toString(),db_mfrs.getText().toString(),fsdl.getText().toString()
 	 			         	,psdl.getText().toString(),gsdl.getText().toString(),zzyg.getText().toString(),zzwg.getText().toString(),fxwg.getText().toString(),DBID};	    
 	 			         					         				 
 	 			  try {  
 	 			         sqldb.execSQL(sql,ags);  
 	 			      } catch (SQLException e) {  }
 	 		}
 	 		 }catch (Exception e) {  }
 	        finally{
 	       	 
 	       	 if(cursor!=null){cursor.close();}
 	        }
 	 		if(sqldb.isOpen()){
 	 			sqldb.close();
 	 		}
 	 		

 	 	}	
	
		//侦听软键盘
		public class mOnKey_Listerner implements OnEditorActionListener{
			@Override 
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event){ 
				String str=bhsz.getText().toString();
				if (actionId == EditorInfo.IME_ACTION_SEND){
					if(zthy_flag==0){
						if(lock_flag==true ){
							//****************关闭软件盘***********************************
							InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
							if(imm.isActive()){  
							imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );  
							}
							switch (v.getId()){				
							//表号设置
							case R.id.ameterNumberET:
								if(!bhsz.getText().toString().equals("")){
									if(!bhsz.getText().toString().equals(temp_bh)){
										temp_bh=bhsz.getText().toString().trim();
										SaveRecord(str,0);
	//									SaveRecordAll(str);
										save_config(0);						
										DisplayToast("表号设置成功！！");
										loadsettings(str);
										for(int i=0;i<Declare.save_data.length;i++){
											Declare.save_data[i]="";
										}
									}
								}
								else{
									DisplayToast("表号值不能为空！！");
									bhsz.setText(temp_bh);
								}
								break;								
								//户名设置
							case R.id.userNameET:
								temp_hm=hmsz.getText().toString().trim();
								SaveRecord(str,1);
//								SaveRecordAll(str);
								save_config(1);	
								DisplayToast("用户名称设置成功！！");	
								break;
								//常数设置
							case R.id.ameterConstantValueET:
								if(Declare.Clientisconnect==true && Declare.rec_err==false){
									if(!cssz.getText().toString().equals("")){
										int constant= Integer.parseInt(cssz.getText().toString().trim());
			        					if(constant>=1 && constant<=32767)
			        						{
			        						    String temp_cs1="";
			        							cssz.setText(String.valueOf(constant));		        					
			        							temp_cs1=cssz.getText().toString().trim();
			        							Declare.send_set_flag=8;
			        							Declare.send_set_data=Short.parseShort(temp_cs1);
			        							mSocketClient.send_msg(Declare.send_set_flag,Declare.send_set_data);
			        							try {
			        								Thread.sleep(100);
			        							  
			        							 } catch (Exception e) {
			        								// TODO Auto-generated catch block
			        								e.printStackTrace();
			        						  	}
			        							int value=Integer.parseInt(set_para(1));
			        							System.out.println(value);
			        							if(value==constant){
			        								temp_cs=temp_cs1;
			        								SaveRecord(str,7);
//			        								SaveRecordAll(str);
			        								save_config(7);
			        								DisplayToast("常数设置成功！！");
			        							}
			        							else{
			        								DisplayToast("常数设置失败！！");
			        								cssz.setText(temp_cs);
			        							}
			        						}
			        					else{
			        							cssz.setText(temp_cs);
			        							DisplayToast("请输入1到32767的数值"); 
			        						}
									}
									else{
										DisplayToast("常数值不能为空！！");
										cssz.setText(temp_cs);
									}
								}
								else{erro_sel=2;comfir_display();}//未解锁进行消息框提示
								break;
								//圈数设置
							 case R.id.coilsNumberET:
								 if(Declare.Clientisconnect==true && Declare.rec_err==false){ 
									 if(!qssz.getText().toString().equals("")){
										 int constant= Integer.parseInt(qssz.getText().toString().trim());
				        					if(constant>1 && constant<=9999)
				        						{
				        							String temp_qs1="";
				        							qssz.setText(String.valueOf(constant));		        					
				        							temp_qs1=qssz.getText().toString().trim();
				        							Declare.send_set_flag=9;
													Declare.send_set_data=Short.parseShort(temp_qs1);
													mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
													try {
														Thread.sleep(100);
													 } catch (Exception e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
												  	}	
													int value=Integer.parseInt(set_para(5));			        							
				        							if(value==constant){
				        								temp_qs=temp_qs1;
				        								SaveRecord(str,8);
				        								save_config(8);
				        								DisplayToast("圈数设置成功！！");
				        							}
				        							else{
				        								qssz.setText(temp_qs);
				        								DisplayToast("圈数设置失败！！");
				        							}
				        						}
				        					else{
				        							qssz.setText(temp_qs);
				        							DisplayToast("请输入1到9999的数值"); 
				        						}
									 }
									 else{
										 DisplayToast("圈数值不能为空！！");
										 qssz.setText(temp_qs);
									 }
								 }
								 else{erro_sel=2;comfir_display();}//未解锁进行消息框提示
								break;
								//电压系数设置
							 case R.id.voltRatioET:
								 if(Declare.Clientisconnect==true && Declare.rec_err==false){  
									 if(!dysz.getText().toString().equals("")){	
										 temp_dyxs=dysz.getText().toString().trim();
										 SaveRecord(str,9);
//										 SaveRecordAll(str);
										save_config(9);
										DisplayToast("电压系数设置成功！！");	
										//dlsz.requestFocus();
									 }
									 else{
										 DisplayToast("电压系数值不能为空！！");
										 dysz.setText(temp_dyxs);
									 }
								 }
								 else{erro_sel=2;comfir_display();}//未解锁进行消息框提示 
								break;
								//电流系数设置
								case R.id.amphereRatioET:
									if(Declare.Clientisconnect==true && Declare.rec_err==false){  
										if(!dlsz.getText().toString().equals("")){
											String temp_dlxs1="";
											temp_dlxs1=dlsz.getText().toString().trim();										
											Declare.send_set_flag=10;
											Declare.send_set_data=Short.parseShort(temp_dlxs1);
											mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
											try {
												Thread.sleep(100);
											  
											 } catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
										  	}	
											if(set_para(7).equals(temp_dlxs1)){
												temp_dlxs=temp_dlxs1;
												SaveRecord(str,10);
//												SaveRecordAll(str);
												save_config(10);
												DisplayToast("电流系数设置成功！！");	
											}
											else{
												dlsz.setText(temp_dlxs); 
												DisplayToast("电流系数设置失败！！");	
											}
										}
										else{
											DisplayToast("电流系数值不能为空！！");
											dlsz.setText(temp_dlxs);
											}
									}
									else{erro_sel=2;comfir_display();}//未解锁进行消息框提示 
									break;
									//分频系数设置
								case R.id.frequencyDivideRatioET:
									if(Declare.Clientisconnect==true && Declare.rec_err==false){  
										if(!fpsz.getText().toString().equals("")){	
											String temp_fpxs1="";
											temp_fpxs1=fpsz.getText().toString().trim();										
											Declare.send_set_flag=11;
											Declare.send_set_data=Short.parseShort(temp_fpxs1);
											mSocketClient.send_setmsg(Declare.send_set_flag,Declare.send_set_data);
											try {
												Thread.sleep(100);
											 } catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
										  	}	
											if(set_para(6).equals(temp_fpxs1)){
												temp_fpxs=temp_fpxs1;
												SaveRecord(str,11);
//												SaveRecordAll(str);
												DisplayToast("分频系数设置成功！！");	
											}
											else{
												fpsz.setText(temp_fpxs);
												DisplayToast("分频系数设置失败！！");	
											}
										}
										else{
											DisplayToast("分频系数值不能为空！！");
											fpsz.setText(temp_fpxs);
										}
									}
									else{erro_sel=2;comfir_display();}//未解锁进行消息框提示 
									break;
								//地址设置
							case R.id.userAddressET:
								temp_addr=dzsz.getText().toString().trim();
								SaveRecord(str,12);
//								SaveRecordAll(str);
								DisplayToast("地址设置成功！！");
								//xlsz.requestFocus();
								break;
								//线路设置
							case R.id.lineET:
								temp_xl=xlsz.getText().toString().trim();
								SaveRecord(str,13);
//								SaveRecordAll(str);
								DisplayToast("线路设置成功！！");
								//tzsz.requestFocus();
								break;
								//台站设置
							case R.id.stationET:
								temp_tz=tzsz.getText().toString().trim();
								SaveRecord(str,14);
//								SaveRecordAll(str);
								DisplayToast("台站设置成功！！");	
								//jyy.requestFocus();
								break;
								//检验员设置
							case R.id.inspectorET:
								temp_jyy=jyy.getText().toString().trim();
								SaveRecord(str,15);
//								SaveRecordAll(str);
								DisplayToast("检验员设置成功！！");	
								//xhy.requestFocus();
								break;
								//校核员设置
							case R.id.corectorET:
								temp_xhy=xhy.getText().toString().trim();
								SaveRecord(str,16);
//								SaveRecordAll(str);
								DisplayToast("校核员设置成功！！");	
								//zdy1.requestFocus();
								break;
					/*			//自定义1设置
							case R.id.zdy1:
								temp_zdy1=zdy1.getText().toString().trim();
								
								SaveRecord(str,17);
//								SaveRecordAll(str);
								//zdy2.requestFocus();
								break;
								//自定义2设置
							case R.id.zdy2:
								temp_zdy2=zdy2.getText().toString().trim();
								SaveRecord(str,18);
//								SaveRecordAll(str);
								//zdy3.requestFocus();
								break;
								//自定义3设置
							case R.id.zdy3:
								temp_zdy3=zdy3.getText().toString().trim();
								SaveRecord(str,19);
//								SaveRecordAll(str);
								//bhsz.requestFocus();
								break;
								//配置热点名称
							case R.id.ssid:
								save_config(21);
								DisplayToast("热点名称设置成功！！");	
								break;
								//配置热点名称
							case R.id.ssid_pass:
								save_config(22);
								DisplayToast("密码设置成功！！");	
								break;
						*/		//电表型号
							case R.id.ameterTypeET:
								DisplayToast("电表型号设置成功！！");	
								break;
								//电表厂家
							case R.id.ameterFactoryET:
								DisplayToast("电表厂家设置成功！！");	
								break;
								//峰时电量
							case R.id.topQuantityET:
								if(!fsdl.getText().toString().equals("")){
									String temp_fsdl1="";
									temp_fsdl1=myformat1.format(Float.parseFloat(fsdl.getText().toString().trim()));
									fsdl.setText(temp_fsdl1);
									DisplayToast("峰时电量设置成功！！");	
								}
								else{
									DisplayToast("峰时电量不能为空！！");
									fsdl.setText("0.00");
								}
								break;
								//平时电量
							case R.id.normalQuantityET:
								if(!psdl.getText().toString().equals("")){
									String temp_psdl1="";
									temp_psdl1=myformat1.format(Float.parseFloat(psdl.getText().toString().trim()));
									psdl.setText(temp_psdl1);
									DisplayToast("平时电量设置成功！！");	
								}
								else{
									DisplayToast("平时电量不能为空！！");
									psdl.setText("0.00");
								}
								break;
								//谷时电量
							case R.id.bottomQuantityET:
								if(!gsdl.getText().toString().equals("")){
									String temp_gsdl1="";
									temp_gsdl1=myformat1.format(Float.parseFloat(gsdl.getText().toString().trim()));
									gsdl.setText(temp_gsdl1);
									DisplayToast("谷时电量设置成功！！");	
								}
								else{
									DisplayToast("谷时电量不能为空！！");
									gsdl.setText("0.00");
								}
								break;
								//正总有功
							case R.id.positiveActivePowerET:
								if(!zzyg.getText().toString().equals("")){
									String temp_zzyg1="";
									temp_zzyg1=myformat1.format(Float.parseFloat(zzyg.getText().toString().trim()));
									zzyg.setText(temp_zzyg1);
									DisplayToast("正向总有功设置成功！！");	
								}
								else{
									DisplayToast("正向总有功不能为空！！");
									zzyg.setText("0.00");
								}
								break;
								//正总无功
							case R.id.positiveReactivePowerET:
								if(!zzwg.getText().toString().equals("")){
									String temp_zzwg1="";
									temp_zzwg1=myformat1.format(Float.parseFloat(zzwg.getText().toString().trim()));
									zzwg.setText(temp_zzwg1);
									DisplayToast("正向总无功设置成功！！");	
								}
								else{
									DisplayToast("正向总无功不能为空！！");
									zzwg.setText("0.00");
								}
								break;
								//负向无功
							case R.id.negativeReactivePowerET:
								if(!fxwg.getText().toString().equals("")){
									String temp_fxwg1="";
									temp_fxwg1=myformat1.format(Float.parseFloat(fxwg.getText().toString().trim()));
									fxwg.setText(temp_fxwg1);
									DisplayToast("负向无功设置成功！！");	
								}
								else{
									DisplayToast("负向有功不能为空！！");
									fxwg.setText("0.00");
								}
								break;
							}
						}
						else{erro_sel=1;comfir_display();}//未解锁进行消息框提示
						switch (v.getId()){				
						//表号设置
						case R.id.ameterNumberET:
							object_flag=6;
							break;												
							//户名设置
						case R.id.userNameET:
							object_flag=7;
							break;											
							//常数设置
						case R.id.ameterConstantValueET:
							object_flag=8;		        									
						break;
						//圈数设置
						case R.id.coilsNumberET:
							object_flag=9;
						break;
						//电压系数设置
						case R.id.voltRatioET:
							object_flag=10;
						break;
						//电流系数设置
						case R.id.amphereRatioET:
							object_flag=11;
						break;
						//分频系数设置
						case R.id.frequencyDivideRatioET:
							object_flag=12;
						break;				
						//地址设置
						case R.id.userAddressET:
							object_flag=13;	        									
						break;
						//线路设置
						case R.id.lineET:
							object_flag=14;	        									
							break;
							//台站设置
						case R.id.stationET:
							object_flag=15;	        									
							break;
							//检验员设置
						case R.id.inspectorET:
							object_flag=16;	        									
						break;
						//校核员设置
						case R.id.corectorET:
							object_flag=17;					
						break;
				/*		//自定义1设置
						case R.id.zdy1:
							object_flag=18;					
						break;
						//自定义2设置
						case R.id.zdy2:
							object_flag=19;					
						break;
						//自定义3设置
						case R.id.zdy3:
							object_flag=20;					
						break;
						//配置热点名称
						case R.id.ssid:
							object_flag=22;					
						break;
						//配置接入密码
						case R.id.ssid_pass:
							object_flag=23;					
						break;*/
						}
					}
					else{zthy_flag=0;}
					return true; 	
				}
				return false; 
			} 
		}
		private void loadsettings(String dbid){
			if(!sqldb.isOpen())	 {
	 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	 	}
			Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=?", new String[]{dbid});
			try { 	             
		 		if(cursor.getCount()>0){
		 			cursor.moveToFirst();
	 				bhsz.setText(cursor.getString(1));
	 				hmsz.setText(cursor.getString(3));
	 				cssz.setText(cursor.getString(14));
	 				qssz.setText(cursor.getString(15));
	 				fpsz.setText(cursor.getString(18));
	 				dysz.setText(cursor.getString(16));
	 				dlsz.setText(cursor.getString(17));
	 				dzsz.setText(cursor.getString(4));
	 				xlsz.setText(cursor.getString(5));
	 				tzsz.setText(cursor.getString(6));
	 				jyy.setText(cursor.getString(7));
	 				xhy.setText(cursor.getString(cursor.getColumnIndex("jhy")));
	 				db_type.setText(cursor.getString(cursor.getColumnIndex("db_type")));
	 				db_mfrs.setText(cursor.getString(cursor.getColumnIndex("db_mfrs")));
	 				
	 				String[] dbzs = getResources().getStringArray(R.array.ameterStandardArray);
	 				ArrayAdapter<String> a_adapter=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dbzs);
	 				spinner.setSelection(a_adapter.getPosition(cursor.getString(9)));
	 				
	 				String[] dbdj = getResources().getStringArray(R.array.ameterGradeArray);
	 				ArrayAdapter<String> a_adapter1=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dbdj);
	 				spinner1.setSelection(a_adapter1.getPosition(cursor.getString(10)));
	 				
	 				String[] dylc = getResources().getStringArray(R.array.voltRangeArray);
	 				ArrayAdapter<String> a_adapter2=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dylc);
	 				spinner2.setSelection(a_adapter2.getPosition(cursor.getString(11)));
		 				
	 				String[] dllc = getResources().getStringArray(R.array.amphereRangeArray);
	 				ArrayAdapter<String> a_adapter3=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dllc);
	 				spinner3.setSelection(a_adapter3.getPosition(cursor.getString(9)));
	 				
	 				String[] mcfs = getResources().getStringArray(R.array.pulseModeAsrray);
	 				ArrayAdapter<String> a_adapter4=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,mcfs);
	 				spinner3.setSelection(a_adapter4.getPosition(cursor.getString(13)));
		 		}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  	}	
		}
		//======消息框提示===========
		private void comfir_display() {	
			
			if(dialog==null){
			dialog = new confirm1(activity,R.style.MyDialog);       	        	       	
	        dialog.show(); 
	        TextView text_info=(TextView)dialog.findViewById(R.id.text_info); 
	        String str="";
	        if(erro_sel==1){
	        	str="操作被锁定，请先解锁！\n";
	        }
	        if(erro_sel==2){
//	        	str="操作被锁定，请先解锁！\n";
	        	str=str+"通讯故障、请检测网络！";
	        }
	        text_info.setText(str);
	        Button but_qr = (Button)dialog.findViewById(R.id.but_qr);   
	        but_qr.setOnClickListener(new ConfirmListener());  
			}
		}
		 //========调取参数子程序=========================
		 public String set_para(int ID){
			 String value=null;
			 if(Declare.Clientisconnect==true && Declare.rec_err==false){
				 //Declare.Set_Para_Flag=false;
				//====清空数据===================
					for(int j=0;j<Declare.data_para.length;j++){
						Declare.data_para[j]=0;
					}
					mSocketClient.sendover_flag=false;
					mSocketClient.send_msg(6,(short)0);
					try {
						Thread.sleep(100);
					 } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				  	}
				for(int i=0;i<1500;i++){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(Declare.data_para[0]==9){
						 value=String.valueOf(Declare.data_para[ID+Declare.devicetype]);					 
						 System.out.println(value);
						//====清空数据===================
						for(int j=0;j<Declare.data_para.length;j++){
							Declare.data_para[j]=0;
						}
						break;
					}
				}
				//Declare.Set_Para_Flag=true;
			 }
			 return (value==null)?"0":value;
		 }
		//==分单相保存基本参数
		public void save_config(int i) {
		 	String content=null;
		 	String str=null;
		 	SharedPreferences settings = activity.getSharedPreferences("config", 0);
		 	SharedPreferences.Editor editor = settings.edit(); 
		 	switch(i){
		 		case 0:
		 			content="dbid";
		 			str=bhsz.getText().toString();
		 			break;
		 		case 1:
		 			content="hmsz";
		 			str=hmsz.getText().toString();
		 			break;
		 		case 2:
		 			content="dbzs";
//		 			str=spinner.getSelectedItem().toString();
		 			Declare.dbzs=spinner.getSelectedItem().toString();
		 			str=String.valueOf(spinner.getSelectedItemPosition());
		 			break;
		 		case 3:
		 			content="dbdj";
//		 			str=spinner1.getSelectedItem().toString();
		 			Declare.dbdj=spinner1.getSelectedItem().toString();
		 			str=String.valueOf(spinner1.getSelectedItemPosition());
		 			break;
		 		case 4:
		 			content="dylc";
//		 			str=spinner2.getSelectedItem().toString();
		 			Declare.dylc=spinner2.getSelectedItem().toString();
		 			str=String.valueOf(spinner2.getSelectedItemPosition());
		 			break;
		 		case 5:
		 			content="dllc";
//		 			str=spinner3.getSelectedItem().toString();
		 			Declare.dllc=spinner3.getSelectedItem().toString();
		 			str=String.valueOf(spinner3.getSelectedItemPosition());
		 			break;/*
		 		case 6:
		 			content="xbfs";
//		 			str=spinner4.getSelectedItem().toString();
		 			str=String.valueOf(spinner4.getSelectedItemPosition());
		 			break;*/
		 		case 7:
		 			content="dbcs";
		 			str=cssz.getText().toString();
		 			break;
		 		case 8:
		 			content="xbqs";
		 			str=qssz.getText().toString();
		 			break;
		 		case 9:
		 			content="dyxs";
		 			str=dysz.getText().toString();
		 			break;
		 		case 10:
		 			content="dlxs";
		 			str=dlsz.getText().toString();
		 			break;
		 		case 11:
		 			content="fpxs";
		 			str=fpsz.getText().toString();
		 			break;
		 		case 12:
		 			content="addr";
		 			str=dzsz.getText().toString();
		 			break;
		 		case 13:
		 			content="xianl";
		 			str=xlsz.getText().toString();
		 			break;
		 		case 14:
		 			content="taiz";
		 			str=tzsz.getText().toString();
		 			break;
		 		case 15:
		 			content="jyy";
		 			str=jyy.getText().toString();
		 			break;
		 		case 16:
		 			content="jhy";
		 			str=xhy.getText().toString();
		 			break;/*
		 		case 17:
		 			content="zdy1";
		 			str=zdy1.getText().toString();
		 			break;
		 		case 18:
		 			content="zdy2";
		 			str=zdy2.getText().toString();
		 			break;
		 		case 19:
		 			content="zdy3";
		 			str=zdy3.getText().toString();
		 			break;*/
		 		case 20:
		 			content="mcfs";
		 			Declare.mcfs=spinner5.getSelectedItem().toString();
		 			str=String.valueOf(spinner5.getSelectedItemPosition());
		 			break;
		 		case 21:
		 			content="ssid";
		 			str=ssid.getText().toString().trim();
		 			Declare.ssid=str;
		 			break;
		 		case 22:
		 			content="ssid_pass";
		 			str=ssid_pass.getText().toString().trim();
		 			Declare.ssid_pass=str;
		 			break;
		 		case 23:
		 			content="db_type";
		 			str=db_type.getText().toString().trim();
		 			break;
		 		case 24:
		 			content="db_mfrs";
		 			str=db_mfrs.getText().toString().trim();
		 			break;
		 		case 25:
		 			content="fsdl";
		 			str=fsdl.getText().toString().trim();
		 			break;
		 		case 26:
		 			content="psdl";
		 			str=psdl.getText().toString().trim();
		 			break;
		 		case 27:
		 			content="gsdl";
		 			str=gsdl.getText().toString().trim();
		 			break;
		 		case 28:
		 			content="zzyg";
		 			str=zzyg.getText().toString().trim();
		 			break;
		 		case 29:
		 			content="zzwg";
		 			str=zzwg.getText().toString().trim();
		 			break;
		 		case 30:
		 			content="fxwg";
		 			str=fxwg.getText().toString().trim();
		 			break;
		 	}
		 		editor.putString(content, str);
		 		editor.commit();
		}
		//==保存所有基本参数
		public static void save_configAll() {
			if(!bhsz.getText().toString().equals("")){
				String s_dbbh=bhsz.getText().toString();
				//for(int i=0;i<17;i++){
				//	SaveRecord(s_dbbh,i);
			//}
				SaveRecordAll(s_dbbh);
			}
			SharedPreferences settings = activity.getSharedPreferences("config", 0);
			SharedPreferences.Editor editor = settings.edit(); 
			editor.putString("dbid",bhsz.getText().toString());
			editor.putString("hmsz",hmsz.getText().toString());
			editor.putString("dbzs",String.valueOf(spinner.getSelectedItemPosition()));
			editor.putString("dbdj",String.valueOf(spinner1.getSelectedItemPosition()));
			editor.putString("dylc",String.valueOf(spinner2.getSelectedItemPosition()));
			editor.putString("dllc",String.valueOf(spinner3.getSelectedItemPosition()));
			//editor.putString("xbfs",String.valueOf(spinner4.getSelectedItemPosition()));
			editor.putString("mcfs",String.valueOf(spinner5.getSelectedItemPosition()));
			editor.putString("dbcs",cssz.getText().toString());
			editor.putString("xbqs",qssz.getText().toString());
			editor.putString("dyxs",dysz.getText().toString());
			editor.putString("dlxs",dlsz.getText().toString());
			editor.putString("fpxs",fpsz.getText().toString());
			editor.putString("addr",dzsz.getText().toString());
			editor.putString("xianl",xlsz.getText().toString());
			editor.putString("taiz",tzsz.getText().toString());
			editor.putString("jyy",jyy.getText().toString());
			editor.putString("jhy",xhy.getText().toString());
			//editor.putString("zdy1",zdy1.getText().toString());
			//editor.putString("zdy2",zdy2.getText().toString());
			//editor.putString("zdy3",zdy3.getText().toString());
			//editor.putString("ssid",ssid.getText().toString());
			//editor.putString("ssid_pass",ssid_pass.getText().toString());
			//===========电表信息====================================
			editor.putString("db_type",db_type.getText().toString());
			editor.putString("db_mfrs",db_mfrs.getText().toString());
			editor.putString("fsdl",fsdl.getText().toString());
			editor.putString("psdl",psdl.getText().toString());
			editor.putString("gsdl",gsdl.getText().toString());
			editor.putString("zzyg",zzyg.getText().toString());
			editor.putString("zzwg",zzwg.getText().toString());
			editor.putString("fxwg",fxwg.getText().toString());
			
			/*
			if(val_mcbl>0){
				editor.putString("mcbl",String.valueOf(val_mcbl-1));
			}
			if(val_auto_manu>0){
				editor.putString("a/m",String.valueOf(val_auto_manu-1));	
			}*/
			editor.commit();
		}
	 	//读取参数基本设置
		public static void get_config() {	
		 	String str1,str2,str3,str4,str5,str6,str7,str8,
		 	str9,str10,str11,str12,str13,str14,str15,str16,str17;
			SharedPreferences settings = activity.getSharedPreferences("config",  0);
			str1=settings.getString("dbid", "000000000001");
			bhsz.setText(str1);
			temp_bh=str1;
			str2=settings.getString("hmsz", "");	 	
		 	hmsz.setText(str2);
		 	temp_hm=str2;
		 	int P1,P2,P3,P4,P5,P6;
		 	P1=Integer.parseInt(settings.getString("dbzs", "0"));
		 	spinner.setSelection(P1,true);
		 	Declare.dbzs=spinner.getSelectedItem().toString();
		 	position1=P1;
		 	P2=Integer.parseInt(settings.getString("dbdj", "0"));
		 	spinner1.setSelection(P2,true);
		 	Declare.dbdj=spinner1.getSelectedItem().toString();
		 	position2=P2;
		 	P3=Integer.parseInt(settings.getString("dylc", "0"));
		 	spinner2.setSelection(P3,true);
		 	Declare.dylc=spinner2.getSelectedItem().toString();
		 	position3=P3;
		 	P4=Integer.parseInt(settings.getString("dllc", "0"));
		 	spinner3.setSelection(P4,true);
		 	Declare.dllc=spinner3.getSelectedItem().toString();
		 	position4=P4;
		 	//P5=Integer.parseInt(settings.getString("xbfs", "0"));
		 	//spinner4.setSelection(P5,true);
		 	//position5=P5;	 	
		 	P6=Integer.parseInt(settings.getString("mcfs", "0"));
		 	spinner5.setSelection(P6,true);
		 	Declare.mcfs=spinner5.getSelectedItem().toString();
		 	position6=P6;
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
		 	dzsz.setText(str8);
		 	temp_addr=str8;
		 	str9=settings.getString("xianl", "");
		 	xlsz.setText(str9);
		 	temp_xl=str9;
		 	str10=settings.getString("taiz", "");
		 	tzsz.setText(str10);
		 	temp_tz=str10;
		 	str11=settings.getString("jyy", "");
		 	jyy.setText(str11);
		 	temp_jyy=str11;
		 	str12=settings.getString("jhy", "");
		 	xhy.setText(str12);
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
		 	db_type.setText(str17);
		 	temp_db_type=str17;
		 	str17=settings.getString("db_mfrs", "");
		 	db_mfrs.setText(str17);
		 	temp_db_mfrs=str17;
		 	str17=settings.getString("fsdl", "0.00");
		 	fsdl.setText(str17);
		 	temp_fsdl=str17;
		 	str17=settings.getString("psdl", "0.00");
		 	psdl.setText(str17);
		 	temp_psdl=str17;
		 	str17=settings.getString("gsdl", "0.00");
		 	gsdl.setText(str17);
		 	temp_gsdl=str17;
		 	str17=settings.getString("zzyg", "0.00");
		 	zzyg.setText(str17);
		 	temp_zzyg=str17;
		 	str17=settings.getString("zzwg", "0.00");
		 	zzwg.setText(str17);
		 	temp_zzwg=str17;
		 	str17=settings.getString("fxwg", "0.00");
		 	fxwg.setText(str17);
		 	temp_fxwg=str17;
		}
	
		//========消息确认界面按钮响应==================================
		public class ConfirmListener implements  OnClickListener{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
			    	case R.id.but_qr:		    				    			 
			    		switch(object_flag){
			    		case 1:
			    			object_flag=0;
			    			zthy_flag=1;
			    			spinner.setSelection(position1,true);
			    			break;
			    		case 2:
			    			object_flag=0;
			    			zthy_flag=1;
			    			spinner1.setSelection(position2,true);		    			
			    			break;
			    		case 3:
			    			object_flag=0;
			    			zthy_flag=1;
			    			spinner2.setSelection(position3,true);
			    			break;
			    		case 4:
			    			object_flag=0;
			    			zthy_flag=1;
			    			spinner3.setSelection(position4,true);
			    			break;
			    		case 5:
			    			object_flag=0;
			    			zthy_flag=1;
			    			spinner4.setSelection(position5,true);
			    			break;
			    			//表号设置
			    		case 6:
			    			object_flag=0;
			    			zthy_flag=1;
			    			bhsz.setText(temp_bh);
			    			bhsz.requestFocus();
			    			break;
			    			//名称设置
			    		case 7:
			    			object_flag=0;
			    			zthy_flag=1;
			    			hmsz.setText(temp_hm);
			    			hmsz.requestFocus();
			    			break;
			    		case 8:
			    			object_flag=0;
			    			zthy_flag=1;
			    			cssz.setText(temp_cs);
			    			cssz.requestFocus();
			    			break;
			    		case 9:
			    			object_flag=0;
			    			zthy_flag=1;
			    			qssz.setText(temp_qs);
			    			qssz.requestFocus();
			    			break;
			    		case 10:
			    			object_flag=0;
			    			zthy_flag=1;
			    			dysz.setText(temp_dyxs);
			    			dysz.requestFocus();
			    			break;
			    		case 11:
			    			object_flag=0;
			    			zthy_flag=1;
			    			dlsz.setText(temp_dlxs);
			    			dlsz.requestFocus();
			    			break;
			    		case 12:
			    			object_flag=0;
			    			zthy_flag=1;
			    			fpsz.setText(temp_fpxs);
			    			fpsz.requestFocus();
			    			break;
			    		case 13:
			    			object_flag=0;
			    			zthy_flag=1;
			    			dzsz.setText(temp_addr);
			    			dzsz.requestFocus();
			    			break;
			    		case 14:
			    			object_flag=0;
			    			zthy_flag=1;
			    			xlsz.setText(temp_xl);
			    			xlsz.requestFocus();
			    			break;
			    		case 15:
			    			object_flag=0;
			    			zthy_flag=1;
			    			tzsz.setText(temp_tz);
			    			tzsz.requestFocus();
			    			break;
			    		case 16:
			    			object_flag=0;
			    			zthy_flag=1;
			    			jyy.setText(temp_jyy);
			    			jyy.requestFocus();
			    			break;
			    		case 17:
			    			object_flag=0;
			    			zthy_flag=1;
			    			xhy.setText(temp_xhy);
			    			xhy.requestFocus();
			    			break;
			    		case 18:
			    			object_flag=0;
			    			zthy_flag=1;
			    			zdy1.setText(temp_zdy1);
			    			zdy1.requestFocus();
			    			break;
			    		case 19:
			    			object_flag=0;
			    			zthy_flag=1;
			    			zdy2.setText(temp_zdy2);
			    			zdy2.requestFocus();
			    			break;
			    		case 20:
			    			object_flag=0;
			    			zthy_flag=1;
			    			zdy3.setText(temp_zdy3);
			    			zdy3.requestFocus();
			    		break;
			    		case 21:
			    			object_flag=0;
			    			zthy_flag=1;
			    			spinner5.setSelection(position6,false);
			    		break;
			    		case 22:
			    			object_flag=0;
			    			zthy_flag=1;
			    			ssid.setText(temp_ssid);
			    			ssid.requestFocus();
			    		break;
			    		case 23:
			    			object_flag=0;
			    			zthy_flag=1;
			    			ssid_pass.setText(temp_ssid_pass);
			    			ssid_pass.requestFocus();
			    		break;		    		
			    		}
			    		dialog.cancel();
			    		dialog=null;
			    	    break;
			    }
			} 
		}
	// fragment对象声明周期方法，建立和activity关联关系时的回调
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}
	// 覆盖父类的方法，控制fragment显示和隐藏
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据
			if(view_flag){
			sub_para_read();}
		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
/*
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
    @Override
    public void onPause(){
    	super.onPause();
    	//mAdapter=null;
    }
	/* 摧毁视图 */
	/*@Override
	public void onDestroyView() {
		super.onDestroyView();
		//mAdapter = null;
	}
	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		SaveRecordAll(bhsz.getText().toString());
	}
	
	//===侦听二维码扫描======================
		private class btn_scan_click implements OnClickListener {               
	    	@Override            
	    	public void onClick(View v) { 
	    		switch (v.getId()){
	    		case R.id.scanBarCodeBtn:
	                //Activity activity=getActivity();
	    			Intent openCameraIntent = new Intent(activity,CaptureActivity.class);
	    			openCameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
	                startActivityForResult(openCameraIntent, 0);
	    			//startActivityForResult(openCameraIntent, 0);	
	    			/*
						 mSocketClient.sendMessage("保定朗信电子有限公司\n");
					*/
	    			break;
	    		}
	    	}
		}
		//取得扫描结果
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			//
			if (resultCode == -1) {
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("result");
				bhsz.setText(scanResult);
				bhsz.setFocusable(true);
				//DisplayToast(scanResult);
			}
		}
		Handler mHandler = new Handler()
		{										
			  public void handleMessage(Message msg)										
			  {											
				  super.handleMessage(msg);	 
				  if(msg.what == 0)
				  {
					  	display();
  						DisplayToast("参数调取成功！！");
  						//====清空数据===================
    					for(int j=0;j<Declare.data_para.length;j++){
    						Declare.data_para[j]=0;
    					}
				  }
				  if(msg.what == 1)
				  {
					  DisplayToast("参数调取超时...！！");
				  }
				  if(msg.what == 2)
				  {
					  erro_sel=2;
					  comfir_display();
				  }
			  }									
		 };
		//==========显示调取参数=========================
		 public void display(){
			 int val_cs,val_dylc,val_dllc,val_mcfs,val_qs,val_fpxs,val_ct,val_pt,val_zs;
			 val_cs=Declare.data_para[1+Declare.devicetype];
			 val_dylc=Declare.data_para[2+Declare.devicetype];
			 val_dllc=Declare.data_para[3+Declare.devicetype];
			 val_mcfs=Declare.data_para[4+Declare.devicetype];
			 val_qs=Declare.data_para[5+Declare.devicetype];
			 val_fpxs=Declare.data_para[6+Declare.devicetype];
			 val_ct=Declare.data_para[7+Declare.devicetype];
			 val_pt=Declare.data_para[8+Declare.devicetype];
			 val_zs=Declare.data_para[9+Declare.devicetype];
			 val_mcbl=Declare.data_para[10+Declare.devicetype];
			 val_auto_manu=Declare.data_para[11+Declare.devicetype];
			 int v1=Declare.data_para[12+Declare.devicetype];
			 int v2=Declare.data_para[13+Declare.devicetype];
			 DecimalFormat myformat_v= new DecimalFormat("000");
			 cssz.setText(String.valueOf(val_cs));
			 qssz.setText(String.valueOf(val_qs));
			 fpsz.setText(String.valueOf(val_fpxs));
			 dlsz.setText(String.valueOf(val_ct));
			 dysz.setText(String.valueOf(val_pt));
			 //ver_num.setText("Ver "+myformat_v.format(v1)+"."+myformat_v.format(v2));
			 //=======制式======================
			 if(val_zs>0){
			 spinner.setSelection(val_zs-1,true);
			 position1=val_zs-1;
			 }
			 //=======电压量程===================
			 if(val_dylc>0){
			 spinner2.setSelection(val_dylc-1,true);
			 position3=val_dylc-1;
			 }
			//=======电流量程===================
			 if(val_dllc>0){
				 int dl_temp=0;
				 if(val_dllc>1){
					 dl_temp=val_dllc-4;
				 }
				 else{
					 dl_temp=val_dllc-1;
				 }
			 spinner3.setSelection(dl_temp,true);
			 position4=dl_temp;
			 }
			//=======脉冲方式===================
			 if(val_mcfs>0){
			 spinner5.setSelection(val_mcfs-1,true);
			 position6=val_mcfs-1;
			 }		 
		 }
}
