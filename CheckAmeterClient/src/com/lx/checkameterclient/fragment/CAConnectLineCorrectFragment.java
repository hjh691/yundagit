package com.lx.checkameterclient.fragment;

import java.io.File;
import java.text.DecimalFormat;//
import java.text.SimpleDateFormat;

import com.lx.checkameter.socket.mSocketClient;
import com.lx.checkameterclient.Declare;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.view.confirm;

import android.annotation.SuppressLint;//
import android.app.Activity;//
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;//
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CAConnectLineCorrectFragment extends Fragment {
	
	private Thread mThreadread=null;
	private static boolean read_flag=false;
	
	SQLiteDatabase sqldb;
	String sql;
	static XltView xltView;
	private File file = new File("/sdcard/bdlx/sxxy.db");// 数据库文件路径
	boolean xy_flag=true;//相序标志位
    boolean ld_flag=true;//零度设置
//    boolean jxfs_flag=true;//三相四与三相三标志
    boolean xb_flag=true;//标签下标设置
    static boolean zs_flag=false;//制式标志区分false:三相三线与true:三相四线
    static boolean grx_flag=false;//感性、容性负载标志
    int lab_flag=0; //标签内容标志0为默认，1为加角度2为加幅值3为幅值加角度
    float zero_x;//零位角度
    float xl_xs=(float)0.40;
    float iu_bz=(float)0.50;//电流电压比值系数
    float radius=730;//底图半径    
    float radius_i;
	static float ua_fuzhi=0;
	static float ub_fuzhi=0;
	static float uc_fuzhi=0;
    float ua_phasic1=0;
	static float ub_phasic1=0;
	static float uc_phasic1=0;
    float ua_phasic,ub_phasic,uc_phasic;
    static float ia_fuzhi=0;
	static float ib_fuzhi=0;
	static float ic_fuzhi=05;
    static float ia_phasic1=0;
	static float ib_phasic1=0;
	static float ic_phasic1=0;  
    float ia_phasic,ib_phasic,ic_phasic; 
    float ux_text_xs,uy_text_xs,ix_text_xs,iy_text_xs;//电压电流标签显示位置补偿系数
    static boolean ua_zero_x=true;//通过ua，ub、uc、ia、ib、ic的幅值是否为零来确定是否绘制该相向量图
	static boolean ub_zero_x=true;
	static boolean uc_zero_x=true;
	static boolean ia_zero_x=true;
	static boolean ib_zero_x=true;
	static boolean ic_zero_x=true;
    
    TextView zs,ja1,ja2,jb1,jb2,jb3,jc1,jc2,jc3,ua1,ua2,ub1,ub2,ub3,uc1,uc2,uc3,ia1,ia2,ib1,ib2,ib3,ic1,ic2,ic3;
	static TextView text_xy;
	static TextView text_rxjg;
	static TextView text_gxjg;
	static TextView dypb;
	static TextView dlpb;
	TextView zbgs;
	static TextView gzxs;
	TextView tv1;
	static TextView gzl;
	static TextView zbdl;
	TextView fangxiang;
	TextView lingdu;
	static EditText dlsr;
	static EditText ctsz;
	static EditText ptsz;
	Dialog dialog,dialog1;
	public static Button but_dljs,btn_savejx;
	static ImageView image_jxt;
	LinearLayout xltlayout;
	static float gzxs_k;//更正系数
	private static int sum1=0;
	static int dyxx;//定义电压相序，电流相序，感性负载，容性负载接受值的变量
	static int dlxx;
	static int gxfz;
	static int rxfz;
	DecimalFormat myformat1;
	static DecimalFormat myformat2;
	static DecimalFormat myformat3;
	private static Activity activity;
	int zt_size=40;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity=activity;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sum1=0;
		myformat2= new DecimalFormat("0.00");
		myformat3= new DecimalFormat("0.00");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.ca_connect_line_correct_fragment, null);
		dlsr=(EditText)view.findViewById(R.id.dlsr);
		zs=(TextView)view.findViewById(R.id.zs);
		fangxiang=(TextView)view.findViewById(R.id.txt_fangxiang);
		lingdu=(TextView)view.findViewById(R.id.txt_lingdu);
		gzl=(TextView)view.findViewById(R.id.gzl);
		zbdl=(TextView)view.findViewById(R.id.zbdl);
		text_xy=(TextView)view.findViewById(R.id.text_xy);
		text_gxjg=(TextView)view.findViewById(R.id.text_gxjg);
		text_rxjg=(TextView)view.findViewById(R.id.text_rxjg);
		text_xy.setText("  ");
		text_gxjg.setText("");
		text_rxjg.setText("");
		dypb=(TextView)view.findViewById(R.id.dypb);
		dlpb=(TextView)view.findViewById(R.id.dlpb);
		//zbgs=(TextView)view.findViewById(R.id.zbgs);
		gzxs=(TextView)view.findViewById(R.id.gzxs);
		image_jxt=(ImageView)view.findViewById(R.id.image_jxt);
		int zs_index=Integer.parseInt(getdbzs());
		String zs_str=null;
		switch(zs_index){
		case 0:
			zs_str="三相四线有功";
			zs_flag=true;
			break;
		case 1:
			zs_str="三相四线无功";
			zs_flag=true;
			break;
		case 2:
			zs_str="三相三线有功";
			zs_flag=false;
			break;
		case 3:
			zs_str="三相三线无功";
			zs_flag=false;
			break;
		}
 			
 		zs.setText(zs_str);//获得电表制式
 		
 		dlsr=(EditText)view.findViewById(R.id.dlsr);//电量输入
        ctsz=(EditText)view.findViewById(R.id.ctsz);//CT值输入
        ptsz=(EditText)view.findViewById(R.id.ptsz);//PT值输入
        dlsr.setOnKeyListener(new mOnKey_Listerner());
        ctsz.setOnKeyListener(new mOnKey_Listerner());
        ptsz.setOnKeyListener(new mOnKey_Listerner());
 		
 		but_dljs=(Button)view.findViewById(R.id.but_dljs);
        but_dljs.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { 
	    		switch (v.getId()){
				
	    		case R.id.but_dljs:
	    			//======转移焦点============
	    			double dlsr_val,gzl_val,dljs_val;int ct_val,pt_val;
	    			dlsr_val=Double.parseDouble(dlsr.getText().toString());
	    			gzl_val=Double.parseDouble(gzl.getText().toString());
	    			ct_val=Integer.parseInt(ctsz.getText().toString());
	    			pt_val=Integer.parseInt(ptsz.getText().toString());
	    			dljs_val=dlsr_val*gzl_val*ct_val*pt_val;
	    			myformat1= new DecimalFormat("0.00");//格式化数据显示
	    			zbdl.setText(myformat1.format(dljs_val));
	    			
	    			Declare.save_data[34]=dlsr.getText().toString();
	    			Declare.save_data[35]=myformat3.format(gzxs_k);
	    			Declare.save_data[36]=gzl.getText().toString();
	    			Declare.save_data[37]=ctsz.getText().toString();
	    			Declare.save_data[38]=ptsz.getText().toString();
	    			Declare.save_data[39]=zbdl.getText().toString();
	  
	    									
	    			break;
	    		
	    		}
	    		
			}
		});
        btn_savejx=(Button)view.findViewById(R.id.btn_savejx);
		btn_savejx.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { 
	    		switch (v.getId()){
	    		case R.id.btn_savejx:
//	    			Toast.makeText(getApplicationContext(), "标题按钮", Toast.LENGTH_SHORT).show();  
	    			dialog1 = new confirm(activity,R.style.MyDialog);       	        	       	
	   	         	dialog1.show();
	   	         	TextView text = (TextView)dialog1.findViewById(R.id.textview2); 
	   	         	if(QueryRecord()==true){
		         		text.setText("表号："+getdbid()+"数据已存在，是否覆盖？");
		         	}
		         	else{
		         		text.setText("确认保存表号："+getdbid()+"数据吗？");
		         	}
	   	         	Button but_qr = (Button)dialog1.findViewById(R.id.but_qr);   
	                Button but_qx = (Button)dialog1.findViewById(R.id.but_qx); 
	                but_qr.setOnClickListener(new ConfirmListener());   
	                but_qx.setOnClickListener(new ConfirmListener());  
	    			
	    			break;
			
	    		}
			}
		});
		
		xltlayout = (LinearLayout)view. findViewById(R.id.xltlayout);         
        xltView = new XltView(activity);           
        xltlayout.addView(xltView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        xltView.invalidate();
		final Builder builder = new AlertDialog.Builder(this.getActivity());
		Button btn = (Button) view.findViewById(R.id.vectorgramSettingBtn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 myformat1= new DecimalFormat("0.00");
				// 设置对话框的图标
				// builder.setIcon(R.drawable.tools);
				// 设置对话框的标题
				builder.setTitle("自定义普通对话框");
				// 装载/res/layout/login.xml界面布局
				final LinearLayout loginForm = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.common_connect_line_correct_dialog, null);
				// 设置对话框显示的View对象
				builder.setView(loginForm);
				// 为对话框设置一个“确定”按钮
				builder.setPositiveButton("设置", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 此处可执行登录处理
						//RadioGroup gr1=(RadioGroup)loginForm.findViewById(R.id.positiveOritationSettingRG);
						Log.d("tag", ".....................");
						String tip=""; 
						if(xy_flag){tip="方向：顺时针";}
				    	 else{tip="方向：逆时针";}
						 fangxiang.setText(tip);
				    	 if(ld_flag){tip="零度：十二点钟";}
				    	 else{tip="零度：三点钟";}
				    	 lingdu.setText(tip);
			             xltView.invalidate();   
					}
				});
				
				// 为对话框设置一个“取消”按钮
				builder.setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 取消登录，不做任何事情。
					}
				});
				// 创建、并显示对话框
				builder.create().show();
				//==============正相序与逆相序选择====================
				RadioGroup gr1=(RadioGroup)loginForm.findViewById(R.id.positiveOritationSettingRG);
	            RadioButton radio_zx = (RadioButton)loginForm.findViewById(R.id.clockwiseRB); 
	            RadioButton radio_fx = (RadioButton)loginForm.findViewById(R.id.anticlockwiseRB); 
	            if(xy_flag==true){radio_zx.setChecked(true);radio_fx.setChecked(false);}
	            else{radio_zx.setChecked(false);radio_fx.setChecked(true);}
	            gr1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {   
	                
	                @Override  
	                public void onCheckedChanged(RadioGroup group, int checkedId) {   
	                    // TODO Auto-generated method stub   
	                	
	                	
	                	switch(checkedId){               
	                    
	                    case R.id.clockwiseRB:  
	                    	 xy_flag=true;         	                    
	                    break;
	                      //-----------------------------------------------
	                    case R.id.anticlockwiseRB:  
	                    	 xy_flag=false;                       
	                    break;
	                      //-----------------------------------------------
	                	}
	                }   
	            }); 
	          //================零度设置=====================
	            RadioGroup gr2=(RadioGroup)loginForm.findViewById(R.id.zeroSettingRG);
	            RadioButton radio_12 = (RadioButton)loginForm.findViewById(R.id.twelveClockRB); 
	            RadioButton radio_3 = (RadioButton)loginForm.findViewById(R.id.threeClockRB);  
	            if(ld_flag==true){radio_12.setChecked(true);radio_3.setChecked(false);}
	            else{radio_12.setChecked(false);radio_3.setChecked(true);}
	            gr2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {   
	                
	                @Override  
	                public void onCheckedChanged(RadioGroup group, int checkedId) {   
	                    // TODO Auto-generated method stub   
	                	                	
	                	switch(checkedId){               
	                    
	                    case R.id.twelveClockRB:  
	                    	ld_flag=true;
	                    	zero_x=0;
	                    
	                    break;
	                      //-----------------------------------------------
	                    case R.id.threeClockRB:                           
	                    	ld_flag=false;
	                    	zero_x=90;
	                    	
	                    break;
	                      //-----------------------------------------------
	                	}
	                }   
	            });
	          //================下标设置=====================
	            RadioGroup gr3=(RadioGroup)loginForm.findViewById(R.id.subscriptSettingRG);
	            RadioButton radio_xa = (RadioButton)loginForm.findViewById(R.id.abcRB); 
	            RadioButton radio_x1 = (RadioButton)loginForm.findViewById(R.id.number123RB); 
	            if(xb_flag==true){radio_xa.setChecked(true);radio_x1.setChecked(false);}
	            else{radio_xa.setChecked(false);radio_x1.setChecked(true);}
	            gr3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {   
	                
	                @Override  
	                public void onCheckedChanged(RadioGroup group, int checkedId) {   
	                    // TODO Auto-generated method stub   
	                	                	
	                	switch(checkedId){               
	                    
	                    case R.id.abcRB:                                   	
	                    	xb_flag=true;
	                    break;
	                      //-----------------------------------------------
	                    case R.id.number123RB:                           
	                    	xb_flag=false;
	                    break;
	                      //-----------------------------------------------
	                	}
	                }   
	            });  
	          //================标签内容设置=====================
	            RadioGroup gr4=(RadioGroup)loginForm.findViewById(R.id.vectorLabelRG);
	            RadioButton radio_mr = (RadioButton)loginForm.findViewById(R.id.defaultRB); 
	            RadioButton radio_jd = (RadioButton)loginForm.findViewById(R.id.angleRB); 
	            RadioButton radio_fz = (RadioButton)loginForm.findViewById(R.id.amplitudeRB); 
	            RadioButton radio_qb = (RadioButton)loginForm.findViewById(R.id.allRB);
	            switch(lab_flag){
	            
	                  case 0:
	                	  radio_mr.setChecked(true);radio_jd.setChecked(false);
	                	  radio_fz.setChecked(false);radio_qb.setChecked(false);
	            	  break;
	            	  //--------------------------
	                  case 1:
	                	  radio_mr.setChecked(false);radio_jd.setChecked(true);
	                	  radio_fz.setChecked(false);radio_qb.setChecked(false);
	            	  break;
	            	  //---------------------------
	                  case 2:
	                	  radio_mr.setChecked(false);radio_jd.setChecked(false);
	                	  radio_fz.setChecked(true);radio_qb.setChecked(false);
	            	  break;
	            	  //---------------------------
	                  case 3:
	                	  radio_mr.setChecked(false);radio_jd.setChecked(false);
	                	  radio_fz.setChecked(false);radio_qb.setChecked(true);
	            	  break;
	            
	            }
	            gr4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {   
	                
	                @Override  
	                public void onCheckedChanged(RadioGroup group, int checkedId) {   
	                    // TODO Auto-generated method stub   
	                	                	
	                	switch(checkedId){ 
	                	
	                	case R.id.defaultRB:  
	                    	lab_flag=0;         	                   	                    	                      
	                    break;
	                    //------------------------------------------------
	                    case R.id.angleRB:  
	                    	lab_flag=1;         	                   	                    	                      
	                    break;
	                      //-----------------------------------------------
	                    case R.id.amplitudeRB:  
	                    	lab_flag=2;                        
	                    break;
	                      //-----------------------------------------------
	                    case R.id.allRB:  
	                    	lab_flag=3;                        
	                    break;
	                          //-----------------------------------------------
	                	}
	                }   
	            }); 
				final  SeekBar sb = (SeekBar)loginForm.findViewById(R.id.currentRatioSeekBar);
				tv1=(TextView)loginForm.findViewById(R.id.currentRatioTV);
				sb.setProgress((int)(iu_bz*100));             
	             tv1.setText("当前比值："  + myformat1.format((float)sb.getProgress()/100));
				// 设置拖动条改变监听器  
		        OnSeekBarChangeListener osbcl = new  OnSeekBarChangeListener() { 
		 
		            @Override  
		            public   void  onProgressChanged(SeekBar seekBar,  int  progress, 
		                    boolean  fromUser) { 
		                tv1.setText("当前比值："  + myformat1.format((float)sb.getProgress()/100)); 
		                iu_bz=(float)sb.getProgress()/100;
		            } 
		 
		            @Override  
		            public   void  onStartTrackingTouch(SeekBar seekBar) { 
		                //Toast.makeText(getApplicationContext(), "onStartTrackingTouch" , 
		                       // Toast.LENGTH_SHORT).show(); 
		            } 
		 
		            @Override  
		            public   void  onStopTrackingTouch(SeekBar seekBar) { 
		                //Toast.makeText(getApplicationContext(), "onStopTrackingTouch" , 
		                      //  Toast.LENGTH_SHORT).show(); 
		            } 
		        };       
		        // 为拖动条绑定监听器  
		        sb.setOnSeekBarChangeListener(osbcl);
			}
		});
			
		
 		
 		//===============感性、容性负载选择=======================
		RadioGroup grjxt=(RadioGroup)view.findViewById(R.id.grjxt);
        RadioButton radio_gx = (RadioButton)view.findViewById(R.id.radio_gx); 
        RadioButton radio_rx = (RadioButton)view.findViewById(R.id.radio_rx); 
        radio_gx.setChecked(true);grx_flag=true;
        grjxt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {   
            
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {   
                // TODO Auto-generated method stub   
            	                	
            	switch(checkedId){               
                
                case R.id.radio_gx:                                   	
                	grx_flag=true;
                	if(zs_flag==true){
       		 		load_jxt34();//三相四线接线图装载
       		 	}
       		 	else{
       		 		load_jxt33();//三相四线接线图装载
       		 	}
                break;
                  //-----------------------------------------------
                case R.id.radio_rx:                           
                	grx_flag=false;
                	if(zs_flag==true){
       		 		load_jxt34();//三相四线接线图装载
       		 	}
       		 	else{
       		 		load_jxt33();//三相四线接线图装载
       		 	}
                break;
                  //-----------------------------------------------
            	}
            }   
        });  
        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
        
      //========开启刷新线程
        if(mThreadread==null){
        	read_flag=true;
        	mThreadread=new mThreadread();
        	mThreadread.start();
        	
        	//Declare.send_flag=1;
            Declare.Circle=true;		
            mSocketClient.SendClientmsg(Declare.Circle); 
        }
		
		return view;
	}

	//文本框输入侦听
		public class mOnKey_Listerner implements OnKeyListener{
			@Override 
			public boolean onKey(View v, int keyCode, KeyEvent event){ 
			     
				if (keyCode == KeyEvent.KEYCODE_ENTER){
					
							switch (v.getId()){				
							//电量输入
							case R.id.dlsr:
								try{ 
			        				if(!dlsr.getText().toString().equals(""))
			        				{
			        					Double dlsr_val;		        				
			        					myformat1= new DecimalFormat("0.00");//格式化数据显示
			        					dlsr_val=Double.parseDouble(dlsr.getText().toString());
			        					dlsr.setText(myformat1.format(dlsr_val));
			        					
			        				}
			        				else{
			        					ctsz.setText("0");
										Toast.makeText(activity, "输入不为空", 1).show();
			        				}
			        				
			        			 }catch(Exception e){
			        				 break;            
			        			}
								
						    
							break;
							//CT输入
							case R.id.ctsz:
								try{ 
			        				if(!ctsz.getText().toString().equals(""))
			        				{
			        					int ct_value= Integer.parseInt(ctsz.getText().toString());
			        					if(ct_value>=1 && ct_value<=99999)
			        						{
			        						  	ctsz.setText(ct_value);
			        						}
			        					else{
			        						  	ctsz.setText("1");
			        							Toast.makeText(activity, "请输入1到99999的数值", 1).show();
			        						}
			        				}
			        				else{
			        					ctsz.setText("1");
										Toast.makeText(activity, "输入不为空", 1).show();
			        					        					
			        				}
			        				
			        			 }catch(Exception e){
			        				 break;            
			        			}
								
							    
							break;
							//PT输入
							case R.id.ptsz:
								try{ 
			        				if(!ptsz.getText().toString().equals(""))
			        				{
			        					int pt_value= Integer.parseInt(ptsz.getText().toString());
			        					if(pt_value>=1 && pt_value<=99999)
			        						{
			        						  	ptsz.setText(pt_value);
			        						}
			        					else{
			        						  	ptsz.setText("1");
			        							Toast.makeText(activity, "请输入1到99999的数值", 1).show();
			        						}
			        				}
			        				else{
			        					ptsz.setText("1");
										Toast.makeText(activity, "输入不为空", 1).show();
			        					        					
			        				}
			        				
			        			 }catch(Exception e){
			        				 break;            
			        			}
								
							    
							break;
							}
											
					return true; 	
				}
				return false; 
				
		
			} 
			
		}
		
	
	//========确认界面响应==================================
			public class ConfirmListener implements  View.OnClickListener{

				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch (v.getId()) {
			    	case R.id.but_qr:
			    		SaveRecord(getdbid());
			         	dialog1.cancel();
			         	Toast.makeText(activity, "表号："+getdbid()+"的数据已保存",Toast.LENGTH_LONG).show();
			    	   break;
			    	    //=================================
			    	case R.id.but_qx:
			    		dialog1.cancel();//
			    	break;
			    		 
			    	}
						
				}

				
			}
			
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//read_flag=false;
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
			Declare.receive_flag=false;
			}
			for(int i=0;i<Declare.data_jxpb.length;i++){
				Declare.data_jxpb[i]=0;
			}
			Declare.connect_num=0;
	        Declare.connect_num1=0;
	        Declare.rec_overtime=false;
			sqldb.close();
	}
	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
	}
	
	//数据刷新线程
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
//			    	     this.interrupt();
//		        	     DisplayToast("线程关闭"); 
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
//				  @SuppressLint("HandlerLeak")
				public void handleMessage(Message msg)										
				  {											
					  super.handleMessage(msg);			
					  if(msg.what == 0)
					  {
//						 while(!Declare.receive_flag){
						while(read_flag){
							 display();
							 break;
							 
						 }
						  
					  }
				  }									
		 };
		 public static void display(){
			    /*
				int int_ua,int_ub,int_uc,int_ia,int_ib,int_ic;
				int_ua=Declare.data_jxpb[0];
				int_ia=Declare.data_jxpb[1];
				int_ub=Declare.data_jxpb[2];
				int_ib=Declare.data_jxpb[3];
				int_uc=Declare.data_jxpb[4];
				int_ic=Declare.data_jxpb[1];
				*/
				ua_fuzhi=(float)(Declare.data_jxpb[0])/32768;
				ia_fuzhi=(float)(Declare.data_jxpb[1])/32768;
				ub_fuzhi=(float)(Declare.data_jxpb[2])/32768;
				ib_fuzhi=(float)(Declare.data_jxpb[3])/32768;
				uc_fuzhi=(float)(Declare.data_jxpb[4])/32768;		    		    
				ic_fuzhi=(float)(Declare.data_jxpb[5])/32768;
				ia_phasic1=(float)(Declare.data_jxpb[6])/64;
				ub_phasic1=(float)(Declare.data_jxpb[7])/64;
				ib_phasic1=(float)(Declare.data_jxpb[8])/64;
				uc_phasic1=(float)(Declare.data_jxpb[9])/64;
				ic_phasic1=(float)(Declare.data_jxpb[10])/64;
				float uaj,ubj,ucj;
				uaj=(float)(Declare.data_jxpb[11])/8192;
				ubj=(float)(Declare.data_jxpb[12])/8192;
				ucj=(float)(Declare.data_jxpb[13])/8192;
				
	            gzxs_k=(float)(Declare.data_jxpb[14])/32768;
				dyxx=Declare.data_jxpb[15];
				dlxx=Declare.data_jxpb[16];
				gxfz=Declare.data_jxpb[17];
				rxfz=Declare.data_jxpb[18];
				
		/*		ja2.setText(myformat2.format(uaj));
			 	jb2.setText(myformat2.format(ubj));
			 	jc2.setText(myformat2.format(ucj));

//			    ja2.setText(myformat2.format((ia_phasic1+360)%360));
//			 	jb2.setText(myformat2.format((ib_phasic1-ub_phasic1+360)%360));
//			 	jc2.setText(myformat2.format((ic_phasic1-uc_phasic1+360)%360));
			 	ua2.setText(myformat2.format(ua_fuzhi));
			 	ub2.setText(myformat2.format(ub_fuzhi));
			 	uc2.setText(myformat2.format(uc_fuzhi));
			*/ 	if(gzxs_k==0)
			 	{
			 		gzxs.setText("更正系数  K≈");
			 		gzl.setText("0");
			 	}
			 	else{
			 		gzxs.setText("更正系数  K≈"+myformat3.format(gzxs_k));
			 		gzl.setText(myformat3.format(gzxs_k-1));
			 	}
			 	//通过各相幅值判断是否绘制该相向量

			 	float u_value=(float)0.0,i_value=(float)0.0;
			    if(ua_fuzhi<=u_value){ua_zero_x=false;} 
			    else{ua_zero_x=true;}
			    if(ub_fuzhi<=u_value){ub_zero_x=false;} 
			    else{ub_zero_x=true;}
			    if(uc_fuzhi<=u_value){uc_zero_x=false;} 
			    else{uc_zero_x=true;}
			    if(ia_fuzhi<=i_value){ia_zero_x=false;} 
			    else{ia_zero_x=true;}
			    if(ib_fuzhi<=i_value){ib_zero_x=false;} 
			    else{ib_zero_x=true;}
			    if(ic_fuzhi<=i_value){ic_zero_x=false;} 
			    else{ic_zero_x=true;}
			    
			 	xltView.invalidate();//刷新向量图
			 	if(zs_flag==true){
			 		load_jxt34();//三相四线接线图装载
			 	}
			 	else{
			 		load_jxt33();//三相四线接线图装载
			 	}
			 	
			 	//将数据存到数组
			 /*	Declare.save_data[0]=ua2.getText().toString();
				Declare.save_data[1]=ia2.getText().toString();
				Declare.save_data[2]=ub2.getText().toString();
				Declare.save_data[3]=ib2.getText().toString();
				Declare.save_data[4]=uc2.getText().toString();
				Declare.save_data[5]=ic2.getText().toString();
				Declare.save_data[6]=ja2.getText().toString();
				Declare.save_data[7]=jb2.getText().toString();
				Declare.save_data[8]=jc2.getText().toString();
				*/
				Declare.save_data[25]=String.valueOf(dyxx);
				Declare.save_data[26]=String.valueOf(dlxx);
				Declare.save_data[27]=String.valueOf(gxfz);
				Declare.save_data[28]=String.valueOf(rxfz);
				Declare.save_data[29]=myformat2.format(ia_phasic1);
				Declare.save_data[30]=myformat2.format(ub_phasic1);
				Declare.save_data[31]=myformat2.format(ib_phasic1);
				Declare.save_data[32]=myformat2.format(uc_phasic1);
				Declare.save_data[33]=myformat2.format(ic_phasic1);
				
				Declare.save_data[34]=dlsr.getText().toString();
				Declare.save_data[35]=myformat3.format(gzxs_k);
				Declare.save_data[36]=gzl.getText().toString();
				Declare.save_data[37]=ctsz.getText().toString();
				Declare.save_data[38]=ptsz.getText().toString();
				Declare.save_data[39]=zbdl.getText().toString();

			 	//启动循环采样后，接收超时清零
				if(Declare.rec_overtime==true){
				 	for(int i=0;i<Declare.data_jxpb.length;i++){
						Declare.data_jxpb[i]=0;
					}
				}
				//检测网络是否正常，后清零
				else if(Declare.Clientisconnect==false || Declare.rec_err==true){
					
					if(sum1>=2){
					 	for(int i=0;i<Declare.data_jxpb.length;i++){
							Declare.data_jxpb[i]=0;
						}
						sum1=0;
					}
					else{sum1++;}
					
				}
				else{sum1=0;}
			 	//清空数据
			 	

			 	

		}
		
	
	public class XltView extends View {
		  private Paint mPaint;   
	      //private Context mContext;   
	      private float x0,y0;
	      private double x_temp,y_temp,x_temp1,y_temp1,x_temp2,y_temp2;
	      String temp,temp1;


	      public XltView(Context context) {   

	    	  super(context);   
	      }   

	      public XltView(Context context,AttributeSet attr)   

	      {   

	    	  super(context,attr);   

	      }   

	      @Override  

	      protected void onDraw(Canvas canvas) {   

	    	  // TODO Auto-generated method stub   

	    	  super.onDraw(canvas);  
	    	  

	    	  mPaint = new Paint();
	    	/*mPaint.setStrokeWidth(2f);//设置线宽
	    	  mPaint.setTextSize(zt_size);//设置字号大小
	    	  mPaint.setFakeBoldText(true);
	    	  mPaint.setColor(Color.YELLOW);
	    	  //标识显示
	    	  String tip;
	    	  if(xy_flag){tip="方向：顺时针";}
	    	  else{tip="方向：逆时针";}
	    	  canvas.drawText(tip, 10, 20, mPaint); 
	    	  if(ld_flag){tip="零度：十二点钟";}
	    	  else{tip="零度：三点钟";}
	    	  canvas.drawText(tip, 10, 40, mPaint); 
	    	  */
	    	  //设置中心点	     
            x0=xltlayout.getWidth()/2;
            y0=xltlayout.getHeight()/2;
            mPaint = new Paint(); 
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStrokeWidth(4f);//设置线宽
            mPaint.setStyle(Paint.Style.STROKE);//填充模式为空心
            mPaint.setColor(Color.DKGRAY);
            //设置虚线模式
            PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);  
            mPaint.setPathEffect(effects);
            //画底图圆
            canvas.drawCircle(x0, y0, (float)(radius*xl_xs), mPaint);
            canvas.drawCircle(x0, y0, (float)((radius*xl_xs)*4/5), mPaint);
            canvas.drawCircle(x0, y0, (float)((radius*xl_xs)*3/5), mPaint);
            canvas.drawCircle(x0, y0, (float)((radius*xl_xs)*2/5), mPaint);
            //分角度画底图线
            x_temp=x0-radius*xl_xs*Math.sin(0);
            y_temp=y0-radius*xl_xs*Math.cos(0);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(Math.PI/6);
            y_temp=y0-radius*xl_xs*Math.cos(Math.PI/6);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(Math.PI/3);
            y_temp=y0-radius*xl_xs*Math.cos(Math.PI/3);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(Math.PI/2);
            y_temp=y0-radius*xl_xs*Math.cos(Math.PI/2);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(2*Math.PI/3);
            y_temp=y0-radius*xl_xs*Math.cos(2*Math.PI/3);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(5*Math.PI/6);
            y_temp=y0-radius*xl_xs*Math.cos(5*Math.PI/6);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(Math.PI);
            y_temp=y0-radius*xl_xs*Math.cos(Math.PI);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(7*Math.PI/6);
            y_temp=y0-radius*xl_xs*Math.cos(7*Math.PI/6);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(4*Math.PI/3);
            y_temp=y0-radius*xl_xs*Math.cos(4*Math.PI/3);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(3*Math.PI/2);
            y_temp=y0-radius*xl_xs*Math.cos(3*Math.PI/2);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(10*Math.PI/6);
            y_temp=y0-radius*xl_xs*Math.cos(10*Math.PI/6);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            x_temp=x0-radius*xl_xs*Math.sin(11*Math.PI/6);
            y_temp=y0-radius*xl_xs*Math.cos(11*Math.PI/6);
            canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
            if(zs_flag){
          	   ua_phasic1=0;
            }
            else{
          	   ua_phasic1=-30;
          	   float u0=0;
          	   mPaint = new Paint();
          	   mPaint.setColor(Color.rgb(107, 74, 17));
                 // mPaint.setStyle(Paint.Style.STROKE);
                 mPaint.setAntiAlias(true);//抗锯齿
                 mPaint.setStrokeWidth(2f); 
                 mPaint.setTextSize(zt_size);
                 mPaint.setFakeBoldText(true);
                 
          	   if(ld_flag){u0=0;}
          	   else{u0=90;}
          	   //=============零度起始线=================
           	  	x_temp=x0+radius*xl_xs*Math.sin(u0/180*Math.PI);
           	  	y_temp=y0-radius*xl_xs*Math.cos(u0/180*Math.PI);
           	  	canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
           	  	//绘制箭头
           	  	x_temp1=x_temp+8*Math.cos((u0+70)/180*Math.PI);
           	  	y_temp1=y_temp+8*Math.sin((u0+70)/180*Math.PI);
           	  	canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
           	  	x_temp1=x_temp-8*Math.cos((u0-70)/180*Math.PI);
           	  	y_temp1=y_temp-8*Math.sin((u0-70)/180*Math.PI);
           	  	canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            
             
           	  	if(ua_phasic>=0 && ua_phasic<=90)
           	  	{
           	  		ux_text_xs=2;uy_text_xs=-3;
           	  	}
            
           	  	x_temp=x0+radius*xl_xs*Math.sin(u0/180*Math.PI)+ux_text_xs;
           	  	y_temp=y0-radius*xl_xs*Math.cos(u0/180*Math.PI)+uy_text_xs;
           	  	x_temp2=0;y_temp2=0;
             
           	  	canvas.drawText("0°", (float)(x_temp-x_temp2),(float)y_temp, mPaint);

          	   
          	   
            }
            if(xy_flag==true){            	  
          	  ua_phasic=ua_phasic1+zero_x;ub_phasic=ua_phasic1+ub_phasic1+zero_x;uc_phasic=ua_phasic1+uc_phasic1+zero_x;
                ia_phasic=ua_phasic1+ia_phasic1+zero_x;ib_phasic=ua_phasic1+ib_phasic1+zero_x;ic_phasic=ua_phasic1+ic_phasic1+zero_x;
            }
            else{
          	  ua_phasic=ua_phasic1+zero_x;ub_phasic=-ua_phasic1+ub_phasic1-zero_x;uc_phasic=-ua_phasic1+uc_phasic1-zero_x;
                ia_phasic=-ua_phasic1+ia_phasic1-zero_x;ib_phasic=-ua_phasic1+ib_phasic1-zero_x;ic_phasic=-ua_phasic1+ic_phasic1-zero_x;
            }
            

            
//            for(int i=0;i<4;i++){                           
            //============绘制ua向量图==============================
            mPaint = new Paint(); 
            mPaint.setColor(Color.YELLOW);
           // mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStrokeWidth(2f); 
            mPaint.setTextSize(zt_size);
            mPaint.setFakeBoldText(true);
            if(ua_zero_x){
            //=============正相序=================
          	  x_temp=x0+radius*xl_xs*Math.sin(ua_phasic/180*Math.PI);
          	  y_temp=y0-radius*xl_xs*Math.cos(ua_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((ua_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((ua_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((ua_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((ua_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
           
            
            if(ua_phasic>=0 && ua_phasic<=90)
            {
          	  ux_text_xs=2;uy_text_xs=-3;
            }
            if(ua_phasic>90 && ua_phasic<=180)
            {
          	  ux_text_xs=3;uy_text_xs=16;
            }
            if(ua_phasic>180 && ua_phasic<=270)
            {
          	  ux_text_xs=-20;uy_text_xs=16;  
            }
            if(ua_phasic>270 && ua_phasic<360)
            {
          	  ux_text_xs=-24;uy_text_xs=-3; 
          	  
            }
            x_temp=x0+radius*xl_xs*Math.sin(ua_phasic/180*Math.PI)+ux_text_xs;
            y_temp=y0-radius*xl_xs*Math.cos(ua_phasic/180*Math.PI)+uy_text_xs;
            temp="";temp1="";x_temp2=0;y_temp2=0;
            myformat1= new DecimalFormat("0.00");
            String str_ua="";
            if(zs_flag){
          	  str_ua="Ua"; 
            }
            else{
          	  str_ua="Uab";
            }
            
            switch(lab_flag){
            		case 0:
            			  if(xb_flag==true){temp=str_ua;}
            			  else{temp="U1";}
          	    break;
          	    //==================================
            		case 1:
            			if(xb_flag==true){temp=str_ua+"∠"+myformat1.format((double)ua_phasic1)+"°";}
          			  else{temp="U1"+"∠"+myformat1.format((double)ua_phasic1)+"°";}
            			  x_temp2=2*zt_size;
              	break;
              	//====================================      
            		case 2:
            			 if(xb_flag==true){temp=str_ua+" "+myformat1.format((double)ua_fuzhi)+"V";}
    			         else{temp="U1"+" "+myformat1.format((double)ua_fuzhi)+"V";}
            			 x_temp2=2*zt_size;
            			
              	break;
              	//=====================================
            		case 3:
            			if(xb_flag==true){
            				temp=str_ua+"∠"+myformat1.format((double)ua_phasic1)+"° ";
            				temp1=myformat1.format((double)ua_fuzhi)+"V";}
   			        else{temp="U1"+"∠"+myformat1.format((double)ua_phasic1)+"° ";
   			             temp1=myformat1.format((double)ua_fuzhi)+"V";}
            			
            			x_temp2=2*zt_size;
              	break;
            
            }
            canvas.drawText(temp, (float)(x_temp-x_temp2),(float)y_temp, mPaint);
            if(temp1!=""){
          	  if(ua_phasic>=90 && ua_phasic<=270)
          	   {canvas.drawText(temp1,(float)(x_temp-x_temp2),(float)(y_temp+zt_size), mPaint);}
          	  else{canvas.drawText(temp1,(float)(x_temp-x_temp2),(float)(y_temp-zt_size), mPaint);}             
            }
           }
          //============绘制ub向量图==============================
            mPaint = new Paint(); 
            mPaint.setColor(Color.GREEN);
           // mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStrokeWidth(2f); 
            mPaint.setTextSize(zt_size);
            mPaint.setFakeBoldText(true);
            if(zs_flag && ub_zero_x){
          	 
            //============ub正相序=====================
            if(xy_flag==true){
          	  x_temp=x0+radius*xl_xs*Math.sin(ub_phasic/180*Math.PI);
          	  y_temp=y0-radius*xl_xs*Math.cos(ub_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((ub_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((ub_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((ub_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((ub_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            }
            //============ub逆相序========================
            else{
          	  
          	  x_temp=x0-radius*xl_xs*Math.sin(ub_phasic/180*Math.PI);
                y_temp=y0-radius*xl_xs*Math.cos(ub_phasic/180*Math.PI);
                canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
               //绘制箭头
                x_temp1=x_temp+8*Math.cos((-ub_phasic+70)/180*Math.PI);
                y_temp1=y_temp+8*Math.sin((-ub_phasic+70)/180*Math.PI);
                canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
  	    	  
                x_temp1=x_temp-8*Math.cos((-ub_phasic-70)/180*Math.PI);
                y_temp1=y_temp-8*Math.sin((-ub_phasic-70)/180*Math.PI);
                canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
          	  
            }
            
            
            if(ub_phasic>=0 && ub_phasic<=90)
            {
          	  ux_text_xs=2;uy_text_xs=-3;
            }
            if(ub_phasic>90 && ub_phasic<=180)
            {
          	  ux_text_xs=3;uy_text_xs=16;
            }
            if(ub_phasic>180 && ub_phasic<=270)
            {
          	  ux_text_xs=-20;uy_text_xs=16;  
            }
            if(ub_phasic>270 && ub_phasic<360)
            {
          	  ux_text_xs=-24;uy_text_xs=-3; 
          	  
            }
            if(xy_flag==true){
          	  x_temp=x0+radius*xl_xs*Math.sin(ub_phasic/180*Math.PI)+ux_text_xs;
          	  y_temp=y0-radius*xl_xs*Math.cos(ub_phasic/180*Math.PI)+uy_text_xs;
            }
            else{
          	  x_temp=x0-radius*xl_xs*Math.sin((ub_phasic)/180*Math.PI)+ux_text_xs;
          	  y_temp=y0-radius*xl_xs*Math.cos((ub_phasic)/180*Math.PI)+uy_text_xs;
            }
            
            temp="";temp1="";x_temp2=0;y_temp2=0;
            myformat1= new DecimalFormat("0.00");
            switch(lab_flag){
            		case 0:
            			  if(xb_flag==true){temp="Ub";}
            			  else{temp="U2";}
          	    break;
          	    //==================================
            		case 1:
            			if(xb_flag==true){temp="Ub"+"∠"+myformat1.format((double)ub_phasic1)+"°";}
          			  else{temp="U2"+"∠"+myformat1.format((double)ub_phasic1)+"°";}
            			  x_temp2=2*zt_size;
              	break;
              	//====================================      
            		case 2:
            			 if(xb_flag==true){temp="Ub"+" "+myformat1.format((double)ub_fuzhi)+"V";}
    			         else{temp="U2"+" "+myformat1.format((double)ub_fuzhi)+"V";}
            			 x_temp2=2*zt_size;
            			
              	break;
              	//=====================================
            		case 3:
            			if(xb_flag==true){
            				temp="Ub"+"∠"+myformat1.format((double)ub_phasic1)+"° ";
            				temp1=myformat1.format((double)ub_fuzhi)+"V";}
   			        else{temp="U2"+"∠"+myformat1.format((double)ub_phasic1)+"° ";
   			             temp1=myformat1.format((double)ub_fuzhi)+"V";}
            			
            			x_temp2=2*zt_size;
              	break;
            
            }
            canvas.drawText(temp, (float)(x_temp-x_temp2),(float)y_temp, mPaint);
            if(temp1!=""){
          	  if(ub_phasic>=90 && ub_phasic<=270)
          	   {canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp+zt_size), mPaint);}
          	  else{canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp-zt_size), mPaint);}
            
            }

      }
            //============绘制uc向量图==============================
            mPaint = new Paint(); 
            mPaint.setColor(Color.RED);
           // mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStrokeWidth(2f); 
            mPaint.setTextSize(zt_size);
            mPaint.setFakeBoldText(true);
            if(uc_zero_x)//判断幅值是否为零确定是否进行向量图绘制
            {
            //=============uc正相序=======================
            if(xy_flag==true){              
          	  x_temp=x0+radius*xl_xs*Math.sin(uc_phasic/180*Math.PI);
          	  y_temp=y0-radius*xl_xs*Math.cos(uc_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((uc_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((uc_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((uc_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((uc_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            }
            //==============uc逆相序=======================
            else{
          	  x_temp=x0-radius*xl_xs*Math.sin((uc_phasic)/180*Math.PI);
          	  y_temp=y0-radius*xl_xs*Math.cos((uc_phasic)/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((-uc_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((-uc_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((-uc_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((-uc_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            }
            //绘制文字   
            if(uc_phasic>=0 && uc_phasic<=90)
            {
          	  ux_text_xs=2;uy_text_xs=-3;
            }
            if(uc_phasic>90 && uc_phasic<=180)
            {
          	  ux_text_xs=3;uy_text_xs=16;
            }
            if(uc_phasic>180 && uc_phasic<=270)
            {
          	  ux_text_xs=-20;uy_text_xs=16;  
            }
            if(uc_phasic>270 && uc_phasic<360)
            {
          	  ux_text_xs=-24;uy_text_xs=-3; 
          	  
            }
            if(xy_flag==true){
          	  x_temp=x0+radius*xl_xs*Math.sin(uc_phasic/180*Math.PI)+ux_text_xs;
          	  y_temp=y0-radius*xl_xs*Math.cos(uc_phasic/180*Math.PI)+uy_text_xs;
            }
            else{
          	  x_temp=x0-radius*xl_xs*Math.sin(uc_phasic/180*Math.PI)+ux_text_xs;
          	  y_temp=y0-radius*xl_xs*Math.cos(uc_phasic/180*Math.PI)+uy_text_xs;
            }
            temp="";temp1="";x_temp2=0;y_temp2=0;
            myformat1= new DecimalFormat("0.00");
            String str_uc="";
            if(zs_flag){
          	  str_uc="Uc"; 
            }
            else{
          	  str_uc="Ucb";
            }
            switch(lab_flag){
            		case 0:
            			  if(xb_flag==true){temp=str_uc;}
            			  else{temp="U3";}
          	    break;
          	    //==================================
            		case 1:
            			if(xb_flag==true){temp=str_uc+"∠"+myformat1.format((double)uc_phasic1)+"°";}
          			  else{temp="U3"+"∠"+myformat1.format((double)uc_phasic1)+"°";}
            			  x_temp2=2*zt_size;
              	break;
              	//====================================      
            		case 2:
            			 if(xb_flag==true){temp=str_uc+" "+myformat1.format((double)uc_fuzhi)+"V";}
    			         else{temp="U3"+" "+myformat1.format((double)uc_fuzhi)+"V";}
            			 x_temp2=2*zt_size;
            			
              	break;
              	//=====================================
            		case 3:
            			if(xb_flag==true){
            				temp=str_uc+"∠"+myformat1.format((double)uc_phasic1)+"° ";
            				temp1=myformat1.format((double)uc_fuzhi)+"V";}
   			        else{temp="U3"+"∠"+myformat1.format((double)uc_phasic1)+"° ";
   			             temp1=myformat1.format((double)uc_fuzhi)+"V";}
            			
            			x_temp2=2*zt_size;
              	break;
            
            }
            canvas.drawText(temp, (float)(x_temp-x_temp2),(float)y_temp, mPaint);
            if(temp1!=""){
          	  if(uc_phasic>=90 && uc_phasic<=270)
          	   {canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp+zt_size), mPaint);}
          	  else{canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp-zt_size), mPaint);}
            
            }
            }
//            for(int i=0;i<4;i++){ 
            //============绘制ia向量图==============================
            radius_i=radius*xl_xs*iu_bz;
            mPaint = new Paint(); 
            mPaint.setColor(Color.YELLOW);
           // mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStrokeWidth(2f); 
            mPaint.setTextSize(zt_size);
            mPaint.setFakeBoldText(true);
            if(ia_zero_x){
            //==============ia正相序==========================
            if(xy_flag==true){
          	  x_temp=x0+radius_i*Math.sin(ia_phasic/180*Math.PI);
          	  y_temp=y0-radius_i*Math.cos(ia_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((ia_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((ia_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((ia_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((ia_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            }
            //==============ia逆相序============================
            else{
          	  x_temp=x0-radius_i*Math.sin(ia_phasic/180*Math.PI);
          	  y_temp=y0-radius_i*Math.cos(ia_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((-ia_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((-ia_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((-ia_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((-ia_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            }
            
            if(ia_phasic>0 && ia_phasic<=90)
            {
          	  ix_text_xs=3;iy_text_xs=16;
            }
            if(ia_phasic>90 && ia_phasic<=135)
            {
          	  ix_text_xs=-6;iy_text_xs=16;
            }
            if(ia_phasic>135 && ia_phasic<=180)
            {
          	  ix_text_xs=6;iy_text_xs=0;
            }
            if(ia_phasic>180 && ia_phasic<=225)
            {
          	  ix_text_xs=-20;iy_text_xs=0;  
            }
            if(ia_phasic>225 && ia_phasic<270)
            {
          	  ix_text_xs=-20;iy_text_xs=-3; 
          	  
            }
            if(ia_phasic>=270 && ia_phasic<360)
            {
          	  ix_text_xs=0;iy_text_xs=-3; 
          	  
            }
            if(xy_flag==true){
          	  x_temp=x0+radius_i*Math.sin(ia_phasic/180*Math.PI)+ix_text_xs;
          	  y_temp=y0-radius_i*Math.cos(ia_phasic/180*Math.PI)+iy_text_xs;
            }
            else{
          	  x_temp=x0-radius_i*Math.sin(ia_phasic/180*Math.PI)+ix_text_xs;
          	  y_temp=y0-radius_i*Math.cos(ia_phasic/180*Math.PI)+iy_text_xs;
            }
            temp="";temp1="";x_temp2=0;y_temp2=0;
            myformat1= new DecimalFormat("0.00");
            switch(lab_flag){
            		case 0:
            			  if(xb_flag==true){temp="Ia";}
            			  else{temp="I1";}
          	    break;
          	    //==================================
            		case 1:
            			if(xb_flag==true){temp="Ia"+"∠"+myformat1.format((double)ia_phasic1)+"°";}
          			  else{temp="I1"+"∠"+myformat1.format((double)ia_phasic1)+"°";}
            			  x_temp2=2*zt_size;
              	break;
              	//====================================      
            		case 2:
            			 if(xb_flag==true){temp="Ia"+" "+myformat1.format((double)ia_fuzhi)+"A";}
    			         else{temp="I1"+" "+myformat1.format((double)ia_fuzhi)+"A";}
            			 x_temp2=2*zt_size;
            			
              	break;
              	//=====================================
            		case 3:
            			if(xb_flag==true){
            				temp="Ia"+"∠"+myformat1.format((double)ia_phasic1)+"° ";
            				temp1=myformat1.format((double)ia_fuzhi)+"A";}
   			        else{temp="I1"+"∠"+myformat1.format((double)ia_phasic1)+"° ";
   			             temp1=myformat1.format((double)ia_fuzhi)+"A";}
            			
            			x_temp2=2*zt_size;
              	break;
            
            }
            canvas.drawText(temp, (float)(x_temp-x_temp2),(float)y_temp, mPaint);
            if(temp1!=""){
          	  if(ia_phasic>=90 && ia_phasic<=270)
          	   {canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp+zt_size), mPaint);}
          	  else{canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp-zt_size), mPaint);}
            
            }
           
            } 
            
          //============绘制ib向量图==============================
            mPaint = new Paint(); 
            mPaint.setColor(Color.GREEN);
           // mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStrokeWidth(2f); 
            mPaint.setTextSize(zt_size);
            mPaint.setFakeBoldText(true);

      if(zs_flag && ib_zero_x){
          	 
            

            if(xy_flag==true){             
          	  x_temp=x0+radius_i*Math.sin(ib_phasic/180*Math.PI);
          	  y_temp=y0-radius_i*Math.cos(ib_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((ib_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((ib_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((ib_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((ib_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            }
            else{
          	  x_temp=x0-radius_i*Math.sin(ib_phasic/180*Math.PI);
          	  y_temp=y0-radius_i*Math.cos(ib_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((-ib_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((-ib_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((-ib_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((-ib_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
            }
            if(ib_phasic>=0 && ib_phasic<=90)
            {
          	  ix_text_xs=3;iy_text_xs=16;
            }
            if(ib_phasic>90 && ib_phasic<=135)
            {
          	  ix_text_xs=-6;iy_text_xs=16;
            }
            if(ib_phasic>135 && ib_phasic<=180)
            {
          	  ix_text_xs=6;iy_text_xs=0;
            }
            if(ib_phasic>180 && ib_phasic<=225)
            {
          	  ix_text_xs=-20;iy_text_xs=0;  
            }
            if(ib_phasic>225 && ib_phasic<270)
            {
          	  ix_text_xs=-20;iy_text_xs=-3; 
          	  
            }
            if(ib_phasic>=270 && ib_phasic<360)
            {
          	  ix_text_xs=0;iy_text_xs=-3; 
          	  
            }
            if(xy_flag==true){
          	  x_temp=x0+radius_i*Math.sin(ib_phasic/180*Math.PI)+ix_text_xs;
          	  y_temp=y0-radius_i*Math.cos(ib_phasic/180*Math.PI)+iy_text_xs;
            }
            else{
          	  x_temp=x0-radius_i*Math.sin(ib_phasic/180*Math.PI)+ix_text_xs;
          	  y_temp=y0-radius_i*Math.cos(ib_phasic/180*Math.PI)+iy_text_xs;
            }
            temp="";temp1="";x_temp2=0;y_temp2=0;
            myformat1= new DecimalFormat("0.00");
            switch(lab_flag){
            		case 0:
            			  if(xb_flag==true){temp="Ib";}
            			  else{temp="I2";}
          	    break;
          	    //==================================
            		case 1:
            			if(xb_flag==true){temp="Ib"+"∠"+myformat1.format((double)ib_phasic1)+"°";}
          			  else{temp="I2"+"∠"+myformat1.format((double)ib_phasic1)+"°";}
            			  x_temp2=2*zt_size;
              	break;
              	//====================================      
            		case 2:
            			 if(xb_flag==true){temp="Ib"+" "+myformat1.format((double)ib_fuzhi)+"A";}
    			         else{temp="I2"+" "+myformat1.format((double)ib_fuzhi)+"A";}
            			 x_temp2=2*zt_size;
            			
              	break;
              	//=====================================
            		case 3:
            			if(xb_flag==true){
            				temp="Ib"+"∠"+myformat1.format((double)ib_phasic1)+"° ";
            				temp1=myformat1.format((double)ib_fuzhi)+"A";}
   			        else{temp="I2"+"∠"+myformat1.format((double)ib_phasic1)+"° ";
   			             temp1=myformat1.format((double)ib_fuzhi)+"A";}
            			
            			x_temp2=2*zt_size;
              	break;
            
            }
            canvas.drawText(temp, (float)(x_temp-x_temp2),(float)y_temp, mPaint);
            if(temp1!=""){
          	  if(ib_phasic>=90 && ib_phasic<=270)
          	   {canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp+zt_size), mPaint);}
          	  else{canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp-zt_size), mPaint);}
            
            }
      }
            //============绘制ic向量图==============================
            mPaint = new Paint(); 
            mPaint.setColor(Color.RED);
           // mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStrokeWidth(2f); 
            mPaint.setTextSize(zt_size);
            mPaint.setFakeBoldText(true);
            if(ic_zero_x){
            if(xy_flag==true){

          	  x_temp=x0+radius_i*Math.sin(ic_phasic/180*Math.PI);
          	  y_temp=y0-radius_i*Math.cos(ic_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((ic_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((ic_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((ic_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((ic_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);        	  
            }
            else{
          	  x_temp=x0-radius_i*Math.sin(ic_phasic/180*Math.PI);
          	  y_temp=y0-radius_i*Math.cos(ic_phasic/180*Math.PI);
          	  canvas.drawLine(x0, y0, (float)x_temp, (float)y_temp, mPaint);
          	  //绘制箭头
          	  x_temp1=x_temp+8*Math.cos((-ic_phasic+70)/180*Math.PI);
          	  y_temp1=y_temp+8*Math.sin((-ic_phasic+70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);
	    	  
          	  x_temp1=x_temp-8*Math.cos((-ic_phasic-70)/180*Math.PI);
          	  y_temp1=y_temp-8*Math.sin((-ic_phasic-70)/180*Math.PI);
          	  canvas.drawLine((float)x_temp,(float)y_temp,(float)x_temp1,(float)y_temp1,mPaint);  
            }
            
            //绘制文字   
            if(ic_phasic>=0 && ic_phasic<=90)
            {
          	  ix_text_xs=3;iy_text_xs=16;
            }
            if(ic_phasic>90 && ic_phasic<=135)
            {
          	  ix_text_xs=-6;iy_text_xs=16;
            }
            if(ic_phasic>135 && ic_phasic<=180)
            {
          	  ix_text_xs=3;iy_text_xs=0;
            }
            if(ic_phasic>180 && ic_phasic<=225)
            {
          	  ix_text_xs=-20;iy_text_xs=0;  
            }
            if(ic_phasic>225 && ic_phasic<270)
            {
          	  ix_text_xs=-20;iy_text_xs=-3; 
          	  
            }
            if(ic_phasic>=270 && ic_phasic<360)
            {
          	  ix_text_xs=0;iy_text_xs=-3; 
          	  
            }
            if(xy_flag==true){
          	  x_temp=x0+radius_i*Math.sin(ic_phasic/180*Math.PI)+ix_text_xs;
          	  y_temp=y0-radius_i*Math.cos(ic_phasic/180*Math.PI)+iy_text_xs;
            }
            else{
          	  x_temp=x0-radius_i*Math.sin(ic_phasic/180*Math.PI)+ix_text_xs;
          	  y_temp=y0-radius_i*Math.cos(ic_phasic/180*Math.PI)+iy_text_xs;
            }
            temp="";temp1="";x_temp2=0;y_temp2=0;
            myformat1= new DecimalFormat("0.00");
            switch(lab_flag){
            		case 0:
            			  if(xb_flag==true){temp="Ic";}
            			  else{temp="I3";}
          	    break;
          	    //==================================
            		case 1:
            			if(xb_flag==true){temp="Ic"+"∠"+myformat1.format((double)ic_phasic1)+"°";}
          			  else{temp="I3"+"∠"+myformat1.format((double)ic_phasic1)+"°";}
            			  x_temp2=2*zt_size;
              	break;
              	//====================================      
            		case 2:
            			 if(xb_flag==true){temp="Ic"+" "+myformat1.format((double)ic_fuzhi)+"A";}
    			         else{temp="I3"+" "+myformat1.format((double)ic_fuzhi)+"A";}
            			 x_temp2=2*zt_size;
            			
              	break;
              	//=====================================
            		case 3:
            			if(xb_flag==true){
            				temp="Ic"+"∠"+myformat1.format((double)ic_phasic1)+"° ";
            				temp1=myformat1.format((double)ic_fuzhi)+"A";}
   			        else{temp="I3"+"∠"+myformat1.format((double)ic_phasic1)+"° ";
   			             temp1=myformat1.format((double)ic_fuzhi)+"A";}
            			
            			x_temp2=2*zt_size;
              	break;
            
            }
            canvas.drawText(temp, (float)(x_temp-x_temp2),(float)y_temp, mPaint);
            if(temp1!=""){
          	  if(ic_phasic>=90 && ic_phasic<=270)
          	   {canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp+zt_size), mPaint);}
          	  else{canvas.drawText(temp1, (float)(x_temp-x_temp2),(float)(y_temp-zt_size), mPaint);}
            
            }
            
//            ua_phasic=ua_phasic+90;ub_phasic=ub_phasic+90;uc_phasic=uc_phasic+90;
//            ia_phasic=ia_phasic+90;ib_phasic=ib_phasic+90;ic_phasic=ic_phasic+90;
//            }
            }

	      }   

	}
	//三相四线电压缺相判断
		public static void open_vol_phase34(){
			text_xy.setText("");
				 			
			image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.jx01));	
			text_gxjg.setText("  ");
			text_rxjg.setText("  ");
			//缺ua***********
			if(ua_zero_x==false && ub_zero_x==true && uc_zero_x==true){
				dypb.setText("Ua相缺失，Ub、Uc相正常；");	
				
			}
			//缺ub***********
			if(ua_zero_x==true && ub_zero_x==false && uc_zero_x==true){
				dypb.setText("Ua相正常、Ub相缺失、Uc相正常；");
						
			}
			//缺ua***********
			if(ua_zero_x==true && ub_zero_x==true && uc_zero_x==false){
				dypb.setText("Ua、Ub相正常、Uc相缺失；");
						
			}
			//缺ua*ub***********
			if(ua_zero_x==false && ub_zero_x==false && uc_zero_x==true){
				dypb.setText("Ua、Ub相缺失，Uc相正常；");
						
			}
			//缺ua*uc***********
			if(ua_zero_x==false && ub_zero_x==true && uc_zero_x==false){
				dypb.setText("Ua相缺失、Ub相正常、Uc相缺失；");		
			}
			//缺ub*uc***********
			if(ua_zero_x==true && ub_zero_x==false && uc_zero_x==false){
				dypb.setText("Ua相正常，Ub、Uc相缺失；");		
			}
			//缺ua*ub*uc***********
			if(ua_zero_x==false && ub_zero_x==false && uc_zero_x==false){
				dypb.setText("Ua，Ub、Uc相全部缺失；");				
			}
			//缺ua*ub*uc***********
			if(ua_zero_x==true && ub_zero_x==true && uc_zero_x==true){
				dypb.setText("Ua，Ub、Uc相全部正常；");				
			}
			
		}
		//三相四线电流缺相判断
		public static void open_cur_phase34(){
			text_xy.setText("");
				 			
			image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.jx01));	
			text_gxjg.setText("  ");
			text_rxjg.setText("  ");
			//缺ua***********
			if(ia_zero_x==false && ib_zero_x==true && ic_zero_x==true){
				dlpb.setText("Ia相缺失，Ib、Ic相正常，无法判定结果");	
				
			}
			//缺ub***********
			if(ia_zero_x==true && ib_zero_x==false && ic_zero_x==true){
				dlpb.setText("Ia相正常、Ib相缺失、Ic相正常，无法判定结果");
						
			}
			//缺uc***********
			if(ia_zero_x==true && ib_zero_x==true && ic_zero_x==false){
				dlpb.setText("Ia、Ib相正常、Ic相缺失，无法判定结果");
						
			}
			//缺ua*ub***********
			if(ia_zero_x==false && ib_zero_x==false && ic_zero_x==true){
				dlpb.setText("Ia、Ib相缺失，Ic相正常，无法判定结果");
						
			}
			//缺ua*uc***********
			if(ia_zero_x==false && ib_zero_x==true && ic_zero_x==false){
				dlpb.setText("Ia相缺失、Ib相正常、Ic相缺失，无法判定结果");		
			}
			//缺ub*uc***********
			if(ia_zero_x==true && ib_zero_x==false && ic_zero_x==false){
				dlpb.setText("Ia相正常，Ib、Ic相缺失，无法判定结果");		
			}
			//缺ua*ub*uc***********
			if(ia_zero_x==false && ib_zero_x==false && ic_zero_x==false){
				dlpb.setText("Ia，Ib、Ic相全部缺失，无法判定结果");				
			}
			//ua*ub*uc完整***********
			if(ia_zero_x==true && ib_zero_x==true && ic_zero_x==true){
				dlpb.setText("Ia，Ib、Ic相全部正常，无法判定结果");				
			}
		}
	 //三相四线接线图装载
	 public static void load_jxt34(){
		 dlpb.setVisibility(0);//显示电流判别组件
		 if(zs_flag==true){
		 		switch(dyxx){
		 		
		 		case 0:
		 			/*
		 			text_xy.setText("");
		 			dypb.setText("因缺相无法判定结果");		 			
		 			image_jxt.setImageDrawable(getResources().getDrawable(R.drawable.jx01));	
					text_gxjg.setText("  ");
					text_rxjg.setText("  ");
					dlpb.setText("因缺相无法判定结果");
					*/
		 			open_vol_phase34();
		 			open_cur_phase34();
		 		break;
		 		//======正相序================
		 		case 1:
		 			//==单选按钮为感性负载
		 			text_xy.setText("正");
		 			dypb.setText("电压接线柱的接线为正相序，即Ua，Ub，Uc");
		 			//===当IA,IB,IC都存在进行以下判断
		 			if(ia_zero_x==true && ib_zero_x==true && ic_zero_x==true){
		 			if(grx_flag==true)
		 			{
		 				
		 				
		 				//========电流相序选择======================
		 				switch(dlxx){
		 				/*
		 					case 0:
		 						open_cur_phase34();
		 						
		 					break;
		 				*/
		 					//++++++++电流相序0x01=========================
		 							 				
		 					case 1:
		 						//========感性选择=========================
		 						switch(gxfz){
		 								 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   -Ib");
		 							dlpb.setText("A、B、C三相电流相序、极性全部正确。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x02=========================
		 					case 2:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性相反;A相电流接到B相电流接线柱,且极性相反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x03=========================
		 					case 3:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x04=========================
		 					case 4:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x05=========================
		 					case 5:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x06=========================
		 					case 6:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x07=========================
		 					case 7:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x08=========================
		 					case 8:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 				
		 				}
		 				
		 				
		 			}
		 			//***************单选按钮为容性负载
		 			else
		 			{
		 				//========电流相序选择======================
		 				switch(dlxx){
		 					//++++++++电流相序0x01=========================
		 					case 1:
		 						//========容性代码选择=========================
		 						switch(rxfz){
		 						case 0:
			 						open_cur_phase34();
			 						
			 					break;
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   +Ic");
		 							dlpb.setText("A、B、C三相电流相序、极性全部正确。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3401_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x02=========================
		 					case 2:
		 						//========容性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3402_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ib   +Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x03=========================
		 					case 3:
		 						//========容性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3403_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x04=========================
		 					case 4:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3404_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x05=========================
		 					case 5:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3405_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   +Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x06=========================
		 					case 6:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   +Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   -Ib   +Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   +Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   -Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3406_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   +Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x07=========================
		 					case 7:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3407_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x08=========================
		 					case 8:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_01));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_02));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ic   -Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_03));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ia   +Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_04));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ib   -Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_05));
		 							text_gxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Ub    Uc       +Ic   +Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.zx_3408_06));
		 							text_gxjg.setText("  Ua    Ub    Uc       +Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Ub    Uc       -Ia   -Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 				
		 				}
		 			 }
		 			}
		 			else{
		 				open_cur_phase34();
		 			}
		 			
		 			break;
		 		//======负相序=================
		 		case 2:
		 			//==单选按钮为感性负载
		 			text_xy.setText("负");
		 			dypb.setText("电压接线柱的接线为逆相序，即Ua，Uc，Ub");
		 			//===当IA,IB,IC都存在进行以下判断
		 			if(ia_zero_x==true && ib_zero_x==true && ic_zero_x==true){
		 			if(grx_flag==true)
		 			{
		 				//========电流相序选择======================
		 				switch(dlxx){
		 					//++++++++电流相序0x01=========================
		 					case 1:
		 						//========感性选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   -Ib");
		 							dlpb.setText("A、B、C三相电流相序、极性全部正确。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x02=========================
		 					case 2:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性相反;A相电流接到B相电流接线柱,且极性相反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x03=========================
		 					case 3:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x04=========================
		 					case 4:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x05=========================
		 					case 5:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x06=========================
		 					case 6:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x07=========================
		 					case 7:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x08=========================
		 					case 8:
		 						//========感性代码选择=========================
		 						switch(gxfz){
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   -Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   +Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   -Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   +Ib");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   -Ic");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   +Ia");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 				
		 				}
		 				
		 				
		 			}
		 			//***************单选按钮为容性负载
		 			else
		 			{
		 				//========电流相序选择======================
		 				switch(dlxx){
		 					//++++++++电流相序0x01=========================
		 					case 1:
		 						//========容性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   +Ic");
		 							dlpb.setText("A、B、C三相电流相序、极性全部正确。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3401_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x02=========================
		 					case 2:
		 						//========容性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3402_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ib   +Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x03=========================
		 					case 3:
		 						//========容性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3403_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x04=========================
		 					case 4:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3404_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x05=========================
		 					case 5:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3405_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   +Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x06=========================
		 					case 6:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   +Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱;C相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   +Ia   -Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   -Ib   +Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱;B相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   +Ic   -Ia");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   -Ia   +Ib");
		 							dlpb.setText("C相电流接到A相电流接线柱;A相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3406_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   -Ic   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   +Ib   -Ic");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 					
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x07=========================
		 					case 7:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3407_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱。");
		 							break;
		 					
		 						}
		 					break;
		 					//+++++++++电流相序0x08=========================
		 					case 8:
		 						//========感性代码选择=========================
		 						switch(rxfz){
		 						
		 						case 1:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_01));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   -Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   +Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱;A相电流接到B相电流接线柱;C相电流接到C相电流接线柱。");
		 							break;
		 						case 2:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_02));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   +Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ic   -Ib   -Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱,且极性接反;B相电流接到B相电流接线柱,且极性接反;A相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						case 3:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_03));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   -Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ia   +Ic   +Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱;C相电流接到B相电流接线柱;B相电流接到C相电流接线柱。");
		 							break;
		 						case 4:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_04));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   +Ia");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ib   -Ia   -Ic");
		 							dlpb.setText("B相电流接到A相电流接线柱,且极性接反;A相电流接到B相电流接线柱,且极性接反;C相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 						case 5:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_05));
		 							text_gxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   -Ib");
		 							text_rxjg.setText("  Ua    Uc    Ub       +Ic   +Ib   +Ia");
		 							dlpb.setText("C相电流接到A相电流接线柱;B相电流接到B相电流接线柱;A相电流接到C相电流接线柱。");
		 							break;
		 						case 6:
		 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.fx_3408_06));
		 							text_gxjg.setText("  Ua    Uc    Ub       +Ib   +Ia   +Ic");
		 							text_rxjg.setText("  Ua    Uc    Ub       -Ia   -Ic   -Ib");
		 							dlpb.setText("A相电流接到A相电流接线柱,且极性接反;C相电流接到B相电流接线柱,且极性接反;B相电流接到C相电流接线柱,且极性接反。");
		 							break;
		 					
		 						}
		 					break;
		 				
		 				}
		 			}
		 			}
		 			else{
		 				open_cur_phase34();
		 			}
		 			
		 			
		 			
		 			
		 			break;
		 		
		 		
		 		
		 		}
		 		
		 		
		 		
		 	}
		 
		 
		 
		 
	 }
	//三相三线电压缺相判断
		public static void open_vol_cur_phase33(){
			text_xy.setText("");
					 			
			image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.jx01));	
			text_gxjg.setText("  ");
			text_rxjg.setText("  ");
			String str_temp="";
			//缺ua*ub***********
			if(ua_zero_x==false && uc_zero_x==false){
						
				str_temp="至少两相电压缺失；";
											
			}
			//缺ua***********
			if(ua_zero_x==false && uc_zero_x==true){
				str_temp="Ua相缺失，Ub、Uc相正常；";	
					
			}

			//缺uc***********
			if(ua_zero_x==true && uc_zero_x==false){
				str_temp="Ua、Ub相正常、Uc相缺失；";
							
			}
			//缺ub***********
			if(ua_zero_x==true && uc_zero_x==true){
				if(ua_fuzhi<65 && uc_fuzhi<65){
				str_temp="Ua相正常、Ub相缺失，Uc相正常；";
				}
				else if(ua_fuzhi>75 && uc_fuzhi>75){
					str_temp="Ua相、Ub相，Uc相正常；";
				}
							
			}
			//缺Ia***********
			if(ia_zero_x==false && ic_zero_x==true){
				dypb.setText(str_temp+"Ia相缺失，Ic相正常，无法判定结果。");	
							
			}
					
			//缺ua***********
			if(ia_zero_x==true && ic_zero_x==false ){
				dypb.setText(str_temp+"Ia相正常、Ic相缺失，无法判定结果。");
									
			}
			if(ia_zero_x==false && ic_zero_x==false){
				dypb.setText(str_temp+"Ia，Ic相均缺失，无法判定结果。");	
							
			}
			if(ia_zero_x==true && ic_zero_x==true){
				dypb.setText(str_temp+"Ia，Ic相正常，无法判定结果。");	
							
			}
			

				
		}


		 //三相三线接线图装载
		 public static void load_jxt33(){
			 dlpb.setVisibility(8);//设置电流组件为gone（不占用位置）
			 if(zs_flag==false){
			 			//==单选按钮为感性负载
			 			text_xy.setText(" ");
			 			
			 			if(grx_flag==true)
			 			{
			 				//========电流相序选择======================
			 				switch(dlxx){
			 					case 0:
			 						/*
			 						text_xy.setText("");
			 						dypb.setText("因缺相无法判定结果");		 			
			 						image_jxt.setImageDrawable(getResources().getDrawable(R.drawable.jx01));	
			 						text_gxjg.setText("  ");
			 						text_rxjg.setText("  ");
			 						dlpb.setText("因缺相无法判定结果");
			 						*/
			 						open_vol_cur_phase33();
			 						//open_cur_phase33();
			 						
			 						
								break;
			 				
			 				
			 					//++++++++电流相序0x01=========================
			 					case 1:
			 						//========感性选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_01));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ia         +Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流的线序、极性全部正确。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_02));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流A相、C相的线序正确、但极性全部接反。");
			 							break;
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_03));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ia         +Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ia         -Ic");
			 							dypb.setText("电压、电流的线序、极性全部正确。");
			 							break;
			 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_04));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ia         -Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流A相、C相的线序正确、但极性全部接反。");
			 							
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_05));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流的线序、极性全部正确。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_06));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ia         -Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ia         +Ic");
			 							dypb.setText("电压相序正确；电流A相、C相的线序正确、但极性全部接反。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x02=========================
			 					case 2:
			 						//========感性代码选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_01));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ia         -Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流线序正确，A相电流极性正确，C相电流极性接反。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_02));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流线序正确，A相电流极性接反，C相电流极性正确。");
			 							break;
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_03));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ia         -Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ia         +Ic");
			 							dypb.setText("电压线序正确；电流线序正确，A相电流极性正确，C相电流极性接反。");
			 							break;
			 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_04));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ia         +Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流线序正确，A相电流极性接反，C相电流极性正确。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_05));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流线序正确，A相电流极性正确，C相电流极性接反。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_06));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ia         +Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ia         -Ic");
			 							dypb.setText("电压线序正确；电流线序正确，A相电流极性接反，C相电流极性正确。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x03=========================
			 					case 3:
			 						//========感性代码选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_01));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱，且极性接反。");
			 					
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_02));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ic         +Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ic         -Ia");
			 							dypb.setText("电压线序正确；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_03));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ic         -Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_04));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_05));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ic         -Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ic         +Ia");
			 							dypb.setText("电压线序正确；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_06));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ic         +Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x04=========================
			 					case 4:
			 						//========感性代码选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_01));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 					
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_02));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ic         -Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ic         +Ia");
			 							dypb.setText("电压线序正确；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_03));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ic         +Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;		 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_04));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_05));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ic         +Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ic         -Ia");
			 							dypb.setText("电压线序正确；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_06));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ic         -Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x05=========================
			 					case 5:
			 						//========感性代码选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_01));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ia         -Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流A相、C相的线序正确、但极性全部反了。");
			 					
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_02));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ia         +Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流线序、极性全部正确。");
			 							break;
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_03));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流A相、C相的线序正确、但极性全部反了。");
			 							break;
			 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_04));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ia         +Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流线序、极性全部正确。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_05));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ia         -Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流线序正确、但极性全部反了。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_06));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流线序、极性全部正确。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x06=========================
			 					case 6:
			 						//========感性代码选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_01));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ia         +Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流线序正确，A相电流极性接反、C相电流极性正确。");
			 					
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_02));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ia         -Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流线序正确，A相电流极性正确、C相电流极性接反。");
			 							break;
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_03));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流线序正确，A相电流极性接反、C相电流极性正确。");
			 							break;
			 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_04));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ia         -Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流线序正确，A相电流极性正确、C相电流极性接反。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_05));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ia         +Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流线序正确，A相电流极性接反、C相电流极性正确。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_06));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流线序正确，A相电流极性正确、C相电流极性接反。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x07=========================
			 					case 7:
			 						//========感性代码选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_01));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ic         +Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_02));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_03));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ic         +Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱。");
			 							break;
			 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_04));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ic         -Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_05));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_06));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ic         -Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x08=========================
			 					case 8:
			 						//========感性代码选择=========================
			 						switch(gxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_01));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ic         -Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_02));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break; 
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_03));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ic         -Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 					
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_04));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ic         +Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_05));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_06));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ic         +Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 					
			 						}
			 					break;
			 				
			 				}
			 				
			 				
			 			}
			 			//***************单选按钮为容性负载
			 			else
			 			{
			 				//========电流相序选择======================
			 				switch(dlxx){
			 					case 0:
			 						/*
			 						text_xy.setText("");
			 						dypb.setText("因缺相无法判定结果");		 			
			 						image_jxt.setImageDrawable(getResources().getDrawable(R.drawable.jx01));	
			 						text_gxjg.setText("  ");
			 						text_rxjg.setText("  ");
			 						dlpb.setText("因缺相无法判定结果");
			 						*/
			 						open_vol_cur_phase33();
			 						//open_cur_phase33();
			 						break;
			 					//++++++++电流相序0x01=========================
			 					case 1:
			 						//========感性选择=========================
			 						switch(rxfz){
			 						
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_01));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流的线序、极性全部正确。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_02));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ia         +Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流A相、C相的线序正确，但极性全部接反。");
			 							break;
			 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_03));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ia         -Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ia         +Ic");
			 							dypb.setText("电压、电流线序、极性接线全部正确。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_04));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流A相、C相的线序正确、但极性全部接反。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_05));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ia         -Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流线序、极性接线全部正确。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_01_06));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ia         +Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ia         -Ic");
			 							dypb.setText("电压相序正确；电流A相、C相的线序正确、但极性全部接反。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x02=========================
			 					case 2:
			 						//========容性代码选择=========================
			 						switch(rxfz){
			 						
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_01));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流线序正确，A相电流极性正确、C相电流极性接反。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_02));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ia         -Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流线序正确，A相电流极性接反、C相电流极性正确。");
			 							break;
			 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_03));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ia         +Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ua、Ub、Uc；电流线序正确，A相电流极性正确、C相电流极性接反。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_04));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；电流线序正确，A相电流极性接反、C相电流极性正确。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_05));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ia         +Ic");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；电流线序正确，A相电流极性正确、C相电流极性接反。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_02_06));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ia         -Ic");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ua、Ub、Uc；电流线序正确，A相电流极性接反、C相电流极性正确。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x03=========================
			 					case 3:
			 						//========容性代码选择=========================
			 						switch(rxfz){
			 						
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_01));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ic         +Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_02));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ic         -Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ua、Ub、Uc；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱。");
			 							break;
			 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_03));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_04));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ic         -Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_05));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ic         +Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ua、Ub、Uc；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_03_06));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱。");
			 					
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x04=========================
			 					case 4:
			 						//========容性代码选择=========================
			 						switch(rxfz){
			 						
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_01));
			 							text_gxjg.setText("  Ua    Ub    Uc       -Ic         -Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_02));
			 							text_gxjg.setText("  Uc    Ua    Ub       +Ic         +Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       -Ic         -Ia");
			 							dypb.setText("电压线序正确；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱、且极性接反。");
			 							break;		 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_03));
			 							text_gxjg.setText("  Ub    Uc    Ua       -Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_04));
			 							text_gxjg.setText("  Ua    Ub    Uc       +Ic         +Ia");
			 							text_rxjg.setText("  Ub    Uc    Ua       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Uc、Ua；C相电流接到A相电流接线柱、且极性反；A相电流接到C相电流接线柱、且极性反。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_05));
			 							text_gxjg.setText("  Uc    Ua    Ub       -Ic         -Ia");
			 							text_rxjg.setText("  Ua    Ub    Uc       +Ic         +Ia");
			 							dypb.setText("电压线序正确；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_04_06));
			 							text_gxjg.setText("  Ub    Uc    Ua       +Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ua    Ub       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ua、Ub；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱、且极性接反。");
			 					
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x05=========================
			 					case 5:
			 						//========容性代码选择=========================
			 						switch(rxfz){
			 						
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_01));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ia         +Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流A相、C相线序正确、但极性全部接反。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_02));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流线序、极性接线全部正确。");
			 							break;
			 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_03));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ia         +Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流A相、C相的线序正确，但极性全部接反。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_04));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ia         -Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流线序、极性接线全部正确。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_05));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流A相、C相的线序正确，但极性全部接反。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_05_06));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ia         -Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流线序、极性接线全部正确。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x06=========================
			 					case 6:
			 						//========容性代码选择=========================
			 						switch(rxfz){
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_01));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ia         -Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流的线序正确，A相电流极性接反；C相电流极性正确。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_02));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ia         +Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流的线序正确，A相电流极性正确；C相电流极性接反。");
			 							break;
			 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_03));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ia         -Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流的线序正确，A相电流极性接反；C相电流极性正确。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_04));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ia         +Ic");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；电流的线序正确，A相电流极性正确；C相电流极性接反。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_05));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ia         -Ic");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ia         +Ic");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；电流的线序正确，A相电流极性接反；C相电流极性正确。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_06_06));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ia         +Ic");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ia         -Ic");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；电流的线序正确，A相电流极性正确；C相电流极性接反。");
			 					
			 							break;

			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x07=========================
			 					case 7:
			 						//========容性代码选择=========================
			 						switch(rxfz){
			 						
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_01));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_02));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ic         +Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_03));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ic         -Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_04));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_05));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ic         -Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_07_06));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ic         +Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 					
			 						}
			 					break;
			 					//+++++++++电流相序0x08=========================
			 					case 8:
			 						//========容性代码选择=========================
			 						switch(rxfz){
			 						
			 						case 1:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_01));
			 							text_gxjg.setText("  Ub    Ua    Uc       +Ic         +Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 						case 2:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_02));
			 							text_gxjg.setText("  Ua    Uc    Ub       -Ic         -Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 					
			 						case 3:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_03));
			 							text_gxjg.setText("  Uc    Ub    Ua       +Ic         +Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 						case 4:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_04));
			 							text_gxjg.setText("  Ub    Ua    Uc       -Ic         -Ia");
			 							text_rxjg.setText("  Uc    Ub    Ua       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Uc、Ub、Ua；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 						case 5:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_05));
			 							text_gxjg.setText("  Ua    Uc    Ub       +Ic         +Ia");
			 							text_rxjg.setText("  Ub    Ua    Uc       -Ic         -Ia");
			 							dypb.setText("电压接线孔接成了Ub、Ua、Uc；C相电流接到A相电流接线柱、且极性接反；A相电流接到C相电流接线柱、且极性接反。");
			 							break;
			 						case 6:
			 							image_jxt.setImageDrawable(activity.getResources().getDrawable(R.drawable.gx_08_06));
			 							text_gxjg.setText("  Uc    Ub    Ua       -Ic         -Ia");
			 							text_rxjg.setText("  Ua    Uc    Ub       +Ic         +Ia");
			 							dypb.setText("电压接线孔接成了Ua、Uc、Ub；C相电流接到A相电流接线柱；A相电流接到C相电流接线柱。");
			 							break;
			 					
			 						}
			 					break;
			 				
			 				}
			 			}
			 
			 } 
		 }
		 

	
	//返回设置的电表ID号
		private String getdbid() {	
			String str;	
			SharedPreferences settings = getActivity().getSharedPreferences("config",  0); 
			str=settings.getString("dbid", "000000000001");
			return str;			        		 		             
		}
		//返回设置的电表制式
		private String getdbzs() {	
			String str;	
			SharedPreferences settings = getActivity().getSharedPreferences("config",  0); 
			str=settings.getString("dbzs", "0");
			return str;			        		 		             
		}
		//返回数据库记录查询结果
		private boolean QueryRecord(){
			boolean flag;
			if(!sqldb.isOpen())	 {
	 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	 	}
			Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=? and ia_cj is not null", new String[]{getdbid()});
			        
			if(cursor.getCount()==0){
				    flag=false;
			}
			else{flag=true;}
				    
			return flag;
			}

		//==========保存数据====================
		public void SaveRecord(String DBID) { 
			
		 
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
			String  date =  sDateFormat.format(new java.util.Date()); 
//			Object[] ags ={myformat.format(id),text_date1.getText().toString()+text_time1.getText().toString(),"保定朗信","竞秀街创业中心","A00001","B00001","薄谷开来","薄谷开来"};
		             //=========查询是否有相同记录再插入数据==========================
			if(!sqldb.isOpen())	 {
	 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	 	}   
			Cursor cursor = sqldb.rawQuery("select * from sxxy_data where dbid=?", new String[]{DBID});
		             
		        if(cursor.getCount()==0){
		        	
					/* 
		        	String sql = "insert into " + "sxxy_data" + " (" +   
		        			"'dbid','cs_time','ua_fz','ia_fz','ub_fz','ib_fz','uc_fz'," +
		            		"'ic_fz','ja','jb','jc','hz','cosa','cosb','cosc','cos','pa','pb','pc','qa','qb','qc','sa','sb','sc'," +
		            		"'wc1','wc2','wc3','wc4','wc5','dyxx','dlxx','jxpb_l','jxpb_c','ia_cj','ub_cj','ib_cj','uc_cj','ic_cj'+" +
		            		"'srdl','gzxs','gzl','zbct','zbpt','zbdl,'wgwc1','wgwc2','wgwc3','wgwc4','wgwc5') " +
		            		"values('" + DBID+ "','"+ date+Declare.save_data[0]+"','"+Declare.save_data[1]+"','"+Declare.save_data[2]+"','"+Declare.save_data[3]+
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
		        			"'dbid','cs_time','dyxx','dlxx','jxpb_l','jxpb_c','ia_cj','ub_cj','ib_cj','uc_cj','ic_cj'+" +
		            		"'srdl','gzxs','gzl','zbct','zbpt','zbdl') " +
		            		"values('" + DBID+ "','"+ date+"','"+Declare.save_data[25]+"','"+Declare.save_data[26]+"','"+Declare.save_data[27]+
		            		"','"+Declare.save_data[28]+"','"+Declare.save_data[29]+"','"+Declare.save_data[30]+"','"+Declare.save_data[31]+"','"+Declare.save_data[32]+
		            		"','"+Declare.save_data[33]+"','"+Declare.save_data[34]+"','"+Declare.save_data[35]+"','"+Declare.save_data[36]+"','"+Declare.save_data[37]+
		            		"','"+Declare.save_data[38]+"','"+Declare.save_data[39]+"')";
		            	 try {  
		            		 sqldb.execSQL(sql);  
		           	   } catch (SQLException e) {   } 
		           }
		         else{
		            /*String sql="update sxxy_data set cs_time=?,ua_fz=?,ia_fz=?,ub_fz=?,ib_fz=?,uc_fz=?,ic_fz=?," +
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
		        	 String sql="update sxxy_data set cs_time=?,dyxx=?,dlxx=?,jxpb_l=?,jxpb_c=?,ia_cj=?,ub_cj=?,ib_cj=?,uc_cj=?," +
			            	 	"ic_cj=?,srdl=?,gzxs=?,gzl=?,zbct=?,zbpt=?,zbdl=? where dbid=?";
			            
			         	Object[] ags={date,Declare.save_data[25],Declare.save_data[26],Declare.save_data[27],Declare.save_data[28],Declare.save_data[29],
			         				Declare.save_data[30],Declare.save_data[31],Declare.save_data[32],Declare.save_data[33],Declare.save_data[34],Declare.save_data[35],
			         				Declare.save_data[36],Declare.save_data[37],Declare.save_data[38],Declare.save_data[39],DBID};		
		         				 
		         	     try {  
		         	           sqldb.execSQL(sql,ags);  
		         	         } catch (SQLException e) {  }
		             }
		             

		}
}
