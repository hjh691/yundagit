package com.lx.checkameterclient.fragment;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

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
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CAHarmonicAnalysisFragment extends Fragment {
	private Activity activity;
	private  CategorySeries series; 
    private  XYMultipleSeriesDataset dataset;  
    private  XYMultipleSeriesRenderer renderer; 
    private  SimpleSeriesRenderer r;
    private  GraphicalView mChartView;
    private File file = new File("/sdcard/bdlx/sxxy.db");// 数据库文件路径
    SQLiteDatabase sqldb;
	String sql;
	public static Button btn_savexb;
	private  DecimalFormat myformat1;
	private DecimalFormat myformat2;
	private DecimalFormat myformat3;
     int count =0;
    String[] titles = new String[] { "谐波" };
     List<double[]> values = new ArrayList<double[]>();
     double[] ua_Values = null;static double[] ub_Values = null;static double[] uc_Values = null;    
     double[] ia_Values = null;static double[] ib_Values = null;static double[] ic_Values = null;
    RadioGroup rg;
    RadioButton ua_rb,ub_rb,uc_rb,ia_rb,ib_rb,ic_rb;
    TextView txv;
	static TextView text_sz;
    private int[] colors;
    private static int xb_sel=0;
    private static int sum1=0;
    private static boolean read_flag=false;
    private Thread mThreadread=null;
    private Dialog dialog;
    private LinearLayout barchart;
    
    private ImageButton imbtn_left,imbtn_right;
    private int min=0,max=9;
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
      //打开创建一个数据库        
      //  sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
	}
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ca_harmonic_analysis_fragment, null);
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
		//清空数据库
        for(int i=0;i<Declare.data_xbfx.length;i++){
			Declare.data_xbfx[i]=0;
		}
		txv=(TextView)view.findViewById(R.id.txt_ver);
        text_sz=(TextView)view.findViewById(R.id.text_sz);
        barchart = (LinearLayout)view.findViewById(R.id.barchart1);
        SegmentControl segmentcontrol=(SegmentControl)view.findViewById(R.id.control_xiangmu);
		segmentcontrol.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		        //处理点击标签的事件
		    	//int f=index;
		    	//int g=f;
		    	renderer.removeSeriesRenderer(r);
		    	Declare.flag_harmonic=index;
            	switch(index){               
                case 0:  
                	 //renderer.removeSeriesRenderer(r);
                     xb_sel=0;
                     //r.setColor(colors[0]);
//                    DisplayToast("请注意，切换到Ua谐波显示");  
                     txv.setText("Ua");
                     text_sz.setText(ua_Values[53]+"");
                  break;
                  //-----------------------------------------------
                 case 1:
                	 xb_sel=1;
                     //r.setColor(colors[1]);
                     txv.setText("Ub");
                     text_sz.setText(ub_Values[53]+"");
                  break;
                  //-----------------------------------------------
                 case 2:  
                  	 //renderer.removeSeriesRenderer(r);
                	 xb_sel=2; 
                     //r.setColor(colors[2]);                       
                     txv.setText("Uc");
                     text_sz.setText(uc_Values[53]+"");
                  break;
                  //-----------------------------------------------                 
                 case 3:  
                	 xb_sel=3;
                     //r.setColor(colors[0]);
                     txv.setText("Ia");
                     text_sz.setText(ia_Values[53]+"");
                  break;
                  //-----------------------------------------------
                 case 4:  
                	 xb_sel=4; 
                     //r.setColor(colors[1]);
                     txv.setText("Ib");
                     text_sz.setText(ib_Values[53]+"");
                  break;
                  //-----------------------------------------------
                 case 5:  
                	 xb_sel=5;                                           
                     //r.setColor(colors[2]);
                     txv.setText("Ic");
                     text_sz.setText(ic_Values[53]+"");
                  break;
                }
            	r.setColor(colors[Declare.flag_harmonic % 3]);
            	 renderer.addSeriesRenderer(r); 
            	 mChartView.invalidate();
		    } 
		}); 
		segmentcontrol.setSelectedIndex(Declare.flag_harmonic);
		btn_savexb=(Button)view.findViewById(R.id.btn_savexb);
		btn_savexb.setOnClickListener(new btn_click());
		imbtn_left=(ImageButton)view.findViewById(R.id.imageButton2);
		imbtn_left.setOnClickListener(new btn_click());
		imbtn_right=(ImageButton)view.findViewById(R.id.imageButton1);
		imbtn_right.setOnClickListener(new btn_click());
		count =54;
		values.clear();//一定要加入此语句。
        myformat1= new DecimalFormat("0.00");
        ua_Values = new double[count];
        ub_Values = new double[count];
        uc_Values = new double[count];
        ia_Values = new double[count];
        ib_Values = new double[count];
        ic_Values = new double[count];
        for (int i = 0; i < count; i++) {
        	ua_Values = new double[count];
            ub_Values = new double[count];
            uc_Values = new double[count];
            ia_Values = new double[count];
            ib_Values = new double[count];
            ic_Values = new double[count];
        }
//        values.add(new double[] { 12.5, 25, 30, 40, 50, 80, 65, 45, 70, 55, 65, 78 });
        values.add(ua_Values);
        values.add(ub_Values);
        values.add(uc_Values);
        values.add(ia_Values);
        values.add(ib_Values);
        values.add(ic_Values);
        dataset = new XYMultipleSeriesDataset();
        series = new CategorySeries(titles[0]);
        double[] v = values.get(0);
        int seriesLength = v.length;
        for (int k = 0; k < seriesLength; k++) {
            series.add(v[k]);
        }
        dataset.addSeries(series.toXYSeries());
        colors = new int[] {Color.YELLOW, Color.GREEN, Color.RED, Color.BLACK};
        renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(25);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(25);
        renderer.setLegendTextSize(25);
        renderer.setMargins(new int[] { 20, 40, 20, 20 });//设置图表的外边框(上/左/下/右)
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(colors[3]);
        renderer.setShowGridY(true);
        r = new SimpleSeriesRenderer();
        r.setColor(colors[Declare.flag_harmonic % 3]);
        renderer.addSeriesRenderer(r);
        //renderer = buildBarRenderer(colors);百分比
        setChartSettings(renderer, "", "谐波次数", "", 0.5, 10, 0, 110, Color.GRAY, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.getSeriesRendererAt(0).setChartValuesTextSize(30);
        //renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
        renderer.setXLabels(0);        
        renderer.setYLabels(5);
        for (int i = 0; i < count; i++) {
        	renderer.addXTextLabel(i, String.valueOf(i-1));        	
        }
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.RIGHT);
        //renderer.setPanEnabled(true, false);
        //renderer.setZoomEnabled(true);
        //renderer.setZoomButtonsVisible(true);
        //renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        renderer.setFitLegend(true);
        renderer.setClickEnabled(false);
        renderer.setXAxisMin(min);
        renderer.setXAxisMax(max);
        mChartView = ChartFactory.getBarChartView(getActivity(), dataset, renderer, Type.DEFAULT);           
        barchart.addView(mChartView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//=======启动刷新线程==============
        if(mThreadread==null){
        	read_flag=true;
        	mThreadread=new mThreadread();
        	mThreadread.start();        
        	//Declare.send_flag=3;
        	Declare.Circle=true;		
        	mSocketClient.SendClientmsg(Declare.Circle);
        }
        mChartView.invalidate();		
		return view;
	}
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if(mThreadread!=null){
			read_flag=false;
			mThreadread.interrupt();
			mThreadread=null;
			//mSocketClient.StopClientmsg();
			Declare.receive_flag=false;
			}
			Declare.connect_num=0;
	        Declare.connect_num1=0;
	        Declare.rec_overtime=false;
			sqldb.close();
			for(int i=0;i<Declare.data_xbfx.length;i++){
				Declare.data_xbfx[i]=0;
			}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
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
	public Handler mHandler_read = new Handler()
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
	public class btn_click implements OnClickListener {               
    	@Override            
    	public void onClick(View v) { 
    		switch (v.getId()){
    		case R.id.btn_savexb:
//    			Toast.makeText(getApplicationContext(), "标题按钮", Toast.LENGTH_SHORT).show();  
    			dialog = new confirm(activity,R.style.MyDialog);       	        	       	
   	         	dialog.show();
   	         	TextView text = (TextView)dialog.findViewById(R.id.textview2);
   	         	if(QueryRecord()==true){
   	         		text.setText("表号："+getdbid()+"谐波数据已存在，是否覆盖？");
   	         	}
   	         	else{
   	         		text.setText("确认保存表号："+getdbid()+"的谐波数据吗？");
   	         	}
   	         	Button but_qr = (Button)dialog.findViewById(R.id.but_qr);   
                Button but_qx = (Button)dialog.findViewById(R.id.but_qx); 
                but_qr.setOnClickListener(new ConfirmListener());   
                but_qx.setOnClickListener(new ConfirmListener());  
    			break;
    		case R.id.imageButton1:
    			min=max-1;max=max+9;
    			if (max>=54){
    				imbtn_right.setEnabled(false);
    			} 
    			if(!imbtn_left.isEnabled())imbtn_left.setEnabled(true);
    			renderer.setXAxisMin(min);
    			renderer.setXAxisMax(max);
    			mChartView.repaint();
    	        mChartView.invalidate();
    			break;
    		case R.id.imageButton2:
    			max=min+1;min=min-9;
    			if (min<=0){
    				min=0;
    				imbtn_left.setEnabled(false);
    			} 
    			if(!imbtn_right.isEnabled())imbtn_right.setEnabled(true);
    			renderer.setXAxisMin(min);
    			renderer.setXAxisMax(max);
    			mChartView.repaint();
    	        mChartView.invalidate();
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
         		Toast.makeText(activity, "表号："+getdbid()+"的谐波数据已保存", Toast.LENGTH_LONG).show();
    	     break;
    	     //=================================
    		 case R.id.but_qx:
    			 dialog.cancel();//
    		 break;
    		 }
		} 
	}
	protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	        CategorySeries series = new CategorySeries(titles[i]);
	            double[] v = values.get(i);
	            int seriesLength = v.length;
	            for (int k = 0; k < seriesLength; k++) {
	                series.add(v[k]);
	            }
	            dataset.addSeries(series.toXYSeries());
	        }
	        return dataset;
	    }
    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }
    public  void display(){
    	//========读取数据=================
		 for (int i = 0; i < count; i++) {
	          ua_Values[i] = Double.parseDouble(myformat1.format((float)(Declare.data_xbfx[i])/256));
	          ub_Values[i] = Double.parseDouble(myformat1.format((float)(Declare.data_xbfx[i+2*count])/256));
	          uc_Values[i] = Double.parseDouble(myformat1.format((float)(Declare.data_xbfx[i+4*count])/256));
	          ia_Values[i] = Double.parseDouble(myformat1.format((float)(Declare.data_xbfx[i+count])/256));
	          ib_Values[i] = Double.parseDouble(myformat1.format((float)(Declare.data_xbfx[i+3*count])/256));
	          ic_Values[i] = Double.parseDouble(myformat1.format((float)(Declare.data_xbfx[i+5*count])/256));
	    }
		dataset.removeSeries(0);
     	series.clear();  
     	double[] v = null;
     	switch(xb_sel){
     	case 0:
     		v = values.get(0);
     		text_sz.setText(ua_Values[53]+"");
     		break;
     	case 1:
     		v = values.get(1);
     		text_sz.setText(ub_Values[53]+"");
     		break;
     	case 2:
     		v = values.get(2);
     		text_sz.setText(uc_Values[53]+"");
     		break;
     	case 3:
     		v = values.get(3);
     		text_sz.setText(ia_Values[53]+"");
     		break;
     	case 4:
     		v = values.get(4);
     		text_sz.setText(ib_Values[53]+"");
     		break;
     	case 5:
     		v = values.get(5);
     		text_sz.setText(ic_Values[53]+"");
     		break;
     	}
     	text_sz.invalidate();
        int seriesLength = v.length;
        for (int k = 0; k < seriesLength; k++) {
            series.add(v[k]);
        }
        dataset.addSeries(series.toXYSeries());
        //mChartView = ChartFactory.getBarChartView(activity, dataset, renderer, Type.DEFAULT);           
        //barchart.addView(mChartView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mChartView.repaint();
        mChartView.invalidate();
        //barchart.removeAllViews();
        //barchart.addView(mChartView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      //启动循环采样后，接收超时清零
      		if(Declare.rec_overtime==true){
      			for(int i=0;i<Declare.data_xbfx.length;i++){
      				Declare.data_xbfx[i]=0;
      			}
      		}
      		//检测网络是否正常，后清零
      		else if(Declare.Clientisconnect==false || Declare.rec_err==true){
      			if(sum1>=2){
      				for(int i=0;i<Declare.data_xbfx.length;i++){
      					Declare.data_xbfx[i]=0;
      				}
      				sum1=0;
      			}
      			else{sum1++;}
      		}
      		else{sum1=0;}
        //清空谐波数组数据
	}
  //返回设置的电表ID号
	private String getdbid() {	
		String str;	
		SharedPreferences settings = activity.getSharedPreferences("config",  0); 
		str=settings.getString("dbid", "000000000001");
		return str;			        		 		             
	}
	//返回数据库记录查询结果
	private boolean QueryRecord(){
		boolean flag;
		if(!sqldb.isOpen())	 {
	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	}
		Cursor cursor = sqldb.rawQuery("select * from xb51_data where dbid=?", new String[]{getdbid()});
	    if(cursor.getCount()==0){
	    	flag=false;
	    }
	    else{flag=true;}
	    return flag;
	}
	//==========保存数据====================
	public void SaveRecord(String DBID) { 
		String sql=null;
		for(int i=0;i<6;i++)
		{
//					   Object[] ags ={myformat.format(id),text_date1.getText().toString()+text_time1.getText().toString(),"保定朗信","竞秀街创业中心","A00001","B00001","薄谷开来","薄谷开来"};
		             //=========查询是否有相同记录再插入数据==========================
			if(!sqldb.isOpen())	 {
	 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	 	}   
			Cursor cursor = sqldb.rawQuery("select * from xb51_data where dbid=? and xb_id=?", new String[]{DBID,String.valueOf(i+1)});
		       if(cursor.getCount()==0){
					switch(i)
					{
					//===============UA谐波数据==============
					case 0:					
						sql = "insert into " + "xb51_data" + " (" +   
		        			"'dbid','xb_id','xb_zl','jb','xb2','xb3','xb4','xb5','xb6','xb7','xb8','xb9','xb10'," +
		        			"'xb11','xb12','xb13','xb14','xb15','xb16','xb17','xb18','xb19','xb20','xb21','xb22'," +
		        			"'xb23','xb24','xb25','xb26','xb27','xb28','xb29','xb30','xb31','xb32','xb33','xb34'," +
		        			"'xb35','xb36','xb37','xb38','xb39','xb40','xb41','xb42','xb43','xb44','xb45','xb46'," +
		        			"'xb47','xb48','xb49','xb50','xb51','xb52','xb_szd') " +
		            		"values('" + DBID+ "','"+String.valueOf(i+1)+"','"+String.valueOf(ua_Values[0])+"','"+String.valueOf(ua_Values[1])+"','"+String.valueOf(ua_Values[2])+"','"+String.valueOf(ua_Values[3])+
		            		"','"+String.valueOf(ua_Values[4])+"','"+String.valueOf(ua_Values[5])+"','"+String.valueOf(ua_Values[6])+"','"+String.valueOf(ua_Values[7])+"','"+String.valueOf(ua_Values[8])+
		            		"','"+String.valueOf(ua_Values[9])+"','"+String.valueOf(ua_Values[10])+"','"+String.valueOf(ua_Values[11])+"','"+String.valueOf(ua_Values[12])+"','"+String.valueOf(ua_Values[13])+
		            		"','"+String.valueOf(ua_Values[14])+"','"+String.valueOf(ua_Values[15])+"','"+String.valueOf(ua_Values[16])+"','"+String.valueOf(ua_Values[17])+"','"+String.valueOf(ua_Values[18])+
		            		"','"+String.valueOf(ua_Values[19])+"','"+String.valueOf(ua_Values[20])+"','"+String.valueOf(ua_Values[21])+"','"+String.valueOf(ua_Values[22])+"','"+String.valueOf(ua_Values[23])+
		            		"','"+String.valueOf(ua_Values[24])+"','"+String.valueOf(ua_Values[25])+"','"+String.valueOf(ua_Values[26])+"','"+String.valueOf(ua_Values[27])+"','"+String.valueOf(ua_Values[28])+
		            		"','"+String.valueOf(ua_Values[29])+"','"+String.valueOf(ua_Values[30])+"','"+String.valueOf(ua_Values[31])+"','"+String.valueOf(ua_Values[32])+"','"+String.valueOf(ua_Values[33])+
		            		"','"+String.valueOf(ua_Values[34])+"','"+String.valueOf(ua_Values[35])+"','"+String.valueOf(ua_Values[36])+"','"+String.valueOf(ua_Values[37])+"','"+String.valueOf(ua_Values[38])+
		            		"','"+String.valueOf(ua_Values[39])+"','"+String.valueOf(ua_Values[40])+"','"+String.valueOf(ua_Values[41])+"','"+String.valueOf(ua_Values[42])+"','"+String.valueOf(ua_Values[43])+
		            		"','"+String.valueOf(ua_Values[44])+"','"+String.valueOf(ua_Values[45])+"','"+String.valueOf(ua_Values[46])+"','"+String.valueOf(ua_Values[47])+"','"+String.valueOf(ua_Values[48])+
		            		"','"+String.valueOf(ua_Values[49])+"','"+String.valueOf(ua_Values[50])+"','"+String.valueOf(ua_Values[51])+"','"+String.valueOf(ua_Values[52])+"','"+String.valueOf(ua_Values[53])+"')";  
						System.out.println(sql);
						break;
					//===============IA谐波数据==============
					case 1:					
			        	 sql = "insert into " + "xb51_data" + " (" +   
			        			"'dbid','xb_id','xb_zl','jb','xb2','xb3','xb4','xb5','xb6','xb7','xb8','xb9','xb10'," +
			        			"'xb11','xb12','xb13','xb14','xb15','xb16','xb17','xb18','xb19','xb20','xb21','xb22'," +
			        			"'xb23','xb24','xb25','xb26','xb27','xb28','xb29','xb30','xb31','xb32','xb33','xb34'," +
			        			"'xb35','xb36','xb37','xb38','xb39','xb40','xb41','xb42','xb43','xb44','xb45','xb46'," +
			        			"'xb47','xb48','xb49','xb50','xb51','xb52','xb_szd') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1)+"','"+String.valueOf(ia_Values[0])+"','"+String.valueOf(ia_Values[1])+"','"+String.valueOf(ia_Values[2])+"','"+String.valueOf(ia_Values[3])+
			            		"','"+String.valueOf(ia_Values[4])+"','"+String.valueOf(ia_Values[5])+"','"+String.valueOf(ia_Values[6])+"','"+String.valueOf(ia_Values[7])+"','"+String.valueOf(ia_Values[8])+
			            		"','"+String.valueOf(ia_Values[9])+"','"+String.valueOf(ia_Values[10])+"','"+String.valueOf(ia_Values[11])+"','"+String.valueOf(ia_Values[12])+"','"+String.valueOf(ia_Values[13])+
			            		"','"+String.valueOf(ia_Values[14])+"','"+String.valueOf(ia_Values[15])+"','"+String.valueOf(ia_Values[16])+"','"+String.valueOf(ia_Values[17])+"','"+String.valueOf(ia_Values[18])+
			            		"','"+String.valueOf(ia_Values[19])+"','"+String.valueOf(ia_Values[20])+"','"+String.valueOf(ia_Values[21])+"','"+String.valueOf(ia_Values[22])+"','"+String.valueOf(ia_Values[23])+
			            		"','"+String.valueOf(ia_Values[24])+"','"+String.valueOf(ia_Values[25])+"','"+String.valueOf(ia_Values[26])+"','"+String.valueOf(ia_Values[27])+"','"+String.valueOf(ia_Values[28])+
			            		"','"+String.valueOf(ia_Values[29])+"','"+String.valueOf(ia_Values[30])+"','"+String.valueOf(ia_Values[31])+"','"+String.valueOf(ia_Values[32])+"','"+String.valueOf(ia_Values[33])+
			            		"','"+String.valueOf(ia_Values[34])+"','"+String.valueOf(ia_Values[35])+"','"+String.valueOf(ia_Values[36])+"','"+String.valueOf(ia_Values[37])+"','"+String.valueOf(ia_Values[38])+
			            		"','"+String.valueOf(ia_Values[39])+"','"+String.valueOf(ia_Values[40])+"','"+String.valueOf(ia_Values[41])+"','"+String.valueOf(ia_Values[42])+"','"+String.valueOf(ia_Values[43])+
			            		"','"+String.valueOf(ia_Values[44])+"','"+String.valueOf(ia_Values[45])+"','"+String.valueOf(ia_Values[46])+"','"+String.valueOf(ia_Values[47])+"','"+String.valueOf(ia_Values[48])+
			            		"','"+String.valueOf(ia_Values[49])+"','"+String.valueOf(ia_Values[50])+"','"+String.valueOf(ia_Values[51])+"','"+String.valueOf(ia_Values[52])+"','"+String.valueOf(ia_Values[53])+"')";  
							break;
					//===============UB谐波数据==============
					case 2:					
			        	sql = "insert into " + "xb51_data" + " (" +   
			        			"'dbid','xb_id','xb_zl','jb','xb2','xb3','xb4','xb5','xb6','xb7','xb8','xb9','xb10'," +
			        			"'xb11','xb12','xb13','xb14','xb15','xb16','xb17','xb18','xb19','xb20','xb21','xb22'," +
			        			"'xb23','xb24','xb25','xb26','xb27','xb28','xb29','xb30','xb31','xb32','xb33','xb34'," +
			        			"'xb35','xb36','xb37','xb38','xb39','xb40','xb41','xb42','xb43','xb44','xb45','xb46'," +
			        			"'xb47','xb48','xb49','xb50','xb51','xb52','xb_szd') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1)+"','"+String.valueOf(ub_Values[0])+"','"+String.valueOf(ub_Values[1])+"','"+String.valueOf(ub_Values[2])+"','"+String.valueOf(ub_Values[3])+
			            		"','"+String.valueOf(ub_Values[4])+"','"+String.valueOf(ub_Values[5])+"','"+String.valueOf(ub_Values[6])+"','"+String.valueOf(ub_Values[7])+"','"+String.valueOf(ub_Values[8])+
			            		"','"+String.valueOf(ub_Values[9])+"','"+String.valueOf(ub_Values[10])+"','"+String.valueOf(ub_Values[11])+"','"+String.valueOf(ub_Values[12])+"','"+String.valueOf(ub_Values[13])+
			            		"','"+String.valueOf(ub_Values[14])+"','"+String.valueOf(ub_Values[15])+"','"+String.valueOf(ub_Values[16])+"','"+String.valueOf(ub_Values[17])+"','"+String.valueOf(ub_Values[18])+
			            		"','"+String.valueOf(ub_Values[19])+"','"+String.valueOf(ub_Values[20])+"','"+String.valueOf(ub_Values[21])+"','"+String.valueOf(ub_Values[22])+"','"+String.valueOf(ub_Values[23])+
			            		"','"+String.valueOf(ub_Values[24])+"','"+String.valueOf(ub_Values[25])+"','"+String.valueOf(ub_Values[26])+"','"+String.valueOf(ub_Values[27])+"','"+String.valueOf(ub_Values[28])+
			            		"','"+String.valueOf(ub_Values[29])+"','"+String.valueOf(ub_Values[30])+"','"+String.valueOf(ub_Values[31])+"','"+String.valueOf(ub_Values[32])+"','"+String.valueOf(ub_Values[33])+
			            		"','"+String.valueOf(ub_Values[34])+"','"+String.valueOf(ub_Values[35])+"','"+String.valueOf(ub_Values[36])+"','"+String.valueOf(ub_Values[37])+"','"+String.valueOf(ub_Values[38])+
			            		"','"+String.valueOf(ub_Values[39])+"','"+String.valueOf(ub_Values[40])+"','"+String.valueOf(ub_Values[41])+"','"+String.valueOf(ub_Values[42])+"','"+String.valueOf(ub_Values[43])+
			            		"','"+String.valueOf(ub_Values[44])+"','"+String.valueOf(ub_Values[45])+"','"+String.valueOf(ub_Values[46])+"','"+String.valueOf(ub_Values[47])+"','"+String.valueOf(ub_Values[48])+
			            		"','"+String.valueOf(ub_Values[49])+"','"+String.valueOf(ub_Values[50])+"','"+String.valueOf(ub_Values[51])+"','"+String.valueOf(ub_Values[52])+"','"+String.valueOf(ua_Values[53])+"')";  
							break;
					//===============IB谐波数据==============
					case 3:					
			        	sql = "insert into " + "xb51_data" + " (" +   
			        			"'dbid','xb_id','xb_zl','jb','xb2','xb3','xb4','xb5','xb6','xb7','xb8','xb9','xb10'," +
			        			"'xb11','xb12','xb13','xb14','xb15','xb16','xb17','xb18','xb19','xb20','xb21','xb22'," +
			        			"'xb23','xb24','xb25','xb26','xb27','xb28','xb29','xb30','xb31','xb32','xb33','xb34'," +
			        			"'xb35','xb36','xb37','xb38','xb39','xb40','xb41','xb42','xb43','xb44','xb45','xb46'," +
			        			"'xb47','xb48','xb49','xb50','xb51','xb52','xb_szd') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1)+"','"+String.valueOf(ib_Values[0])+"','"+String.valueOf(ib_Values[1])+"','"+String.valueOf(ib_Values[2])+"','"+String.valueOf(ib_Values[3])+
			            		"','"+String.valueOf(ib_Values[4])+"','"+String.valueOf(ib_Values[5])+"','"+String.valueOf(ib_Values[6])+"','"+String.valueOf(ib_Values[7])+"','"+String.valueOf(ib_Values[8])+
			            		"','"+String.valueOf(ib_Values[9])+"','"+String.valueOf(ib_Values[10])+"','"+String.valueOf(ib_Values[11])+"','"+String.valueOf(ib_Values[12])+"','"+String.valueOf(ib_Values[13])+
			            		"','"+String.valueOf(ib_Values[14])+"','"+String.valueOf(ib_Values[15])+"','"+String.valueOf(ib_Values[16])+"','"+String.valueOf(ib_Values[17])+"','"+String.valueOf(ib_Values[18])+
			            		"','"+String.valueOf(ib_Values[19])+"','"+String.valueOf(ib_Values[20])+"','"+String.valueOf(ib_Values[21])+"','"+String.valueOf(ib_Values[22])+"','"+String.valueOf(ib_Values[23])+
			            		"','"+String.valueOf(ib_Values[24])+"','"+String.valueOf(ib_Values[25])+"','"+String.valueOf(ib_Values[26])+"','"+String.valueOf(ib_Values[27])+"','"+String.valueOf(ib_Values[28])+
			            		"','"+String.valueOf(ib_Values[29])+"','"+String.valueOf(ib_Values[30])+"','"+String.valueOf(ib_Values[31])+"','"+String.valueOf(ib_Values[32])+"','"+String.valueOf(ib_Values[33])+
			            		"','"+String.valueOf(ib_Values[34])+"','"+String.valueOf(ib_Values[35])+"','"+String.valueOf(ib_Values[36])+"','"+String.valueOf(ib_Values[37])+"','"+String.valueOf(ib_Values[38])+
			            		"','"+String.valueOf(ib_Values[39])+"','"+String.valueOf(ib_Values[40])+"','"+String.valueOf(ib_Values[41])+"','"+String.valueOf(ib_Values[42])+"','"+String.valueOf(ib_Values[43])+
			            		"','"+String.valueOf(ib_Values[44])+"','"+String.valueOf(ib_Values[45])+"','"+String.valueOf(ib_Values[46])+"','"+String.valueOf(ib_Values[47])+"','"+String.valueOf(ib_Values[48])+
			            		"','"+String.valueOf(ib_Values[49])+"','"+String.valueOf(ib_Values[50])+"','"+String.valueOf(ib_Values[51])+"','"+String.valueOf(ib_Values[52])+"','"+String.valueOf(ia_Values[53])+"')";  
							break;
							//===============UC谐波数据==============
					 case 4:					
			        	sql = "insert into " + "xb51_data" + " (" +   
			        			"'dbid','xb_id','xb_zl','jb','xb2','xb3','xb4','xb5','xb6','xb7','xb8','xb9','xb10'," +
			        			"'xb11','xb12','xb13','xb14','xb15','xb16','xb17','xb18','xb19','xb20','xb21','xb22'," +
			        			"'xb23','xb24','xb25','xb26','xb27','xb28','xb29','xb30','xb31','xb32','xb33','xb34'," +
			        			"'xb35','xb36','xb37','xb38','xb39','xb40','xb41','xb42','xb43','xb44','xb45','xb46'," +
			        			"'xb47','xb48','xb49','xb50','xb51','xb52','xb_szd') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1)+"','"+String.valueOf(uc_Values[0])+"','"+String.valueOf(uc_Values[1])+"','"+String.valueOf(uc_Values[2])+"','"+String.valueOf(uc_Values[3])+
			            		"','"+String.valueOf(uc_Values[4])+"','"+String.valueOf(uc_Values[5])+"','"+String.valueOf(uc_Values[6])+"','"+String.valueOf(uc_Values[7])+"','"+String.valueOf(uc_Values[8])+
			            		"','"+String.valueOf(uc_Values[9])+"','"+String.valueOf(uc_Values[10])+"','"+String.valueOf(uc_Values[11])+"','"+String.valueOf(uc_Values[12])+"','"+String.valueOf(uc_Values[13])+
			            		"','"+String.valueOf(uc_Values[14])+"','"+String.valueOf(uc_Values[15])+"','"+String.valueOf(uc_Values[16])+"','"+String.valueOf(uc_Values[17])+"','"+String.valueOf(uc_Values[18])+
			            		"','"+String.valueOf(uc_Values[19])+"','"+String.valueOf(uc_Values[20])+"','"+String.valueOf(uc_Values[21])+"','"+String.valueOf(uc_Values[22])+"','"+String.valueOf(uc_Values[23])+
			            		"','"+String.valueOf(uc_Values[24])+"','"+String.valueOf(uc_Values[25])+"','"+String.valueOf(uc_Values[26])+"','"+String.valueOf(uc_Values[27])+"','"+String.valueOf(uc_Values[28])+
			            		"','"+String.valueOf(uc_Values[29])+"','"+String.valueOf(uc_Values[30])+"','"+String.valueOf(uc_Values[31])+"','"+String.valueOf(uc_Values[32])+"','"+String.valueOf(uc_Values[33])+
			            		"','"+String.valueOf(uc_Values[34])+"','"+String.valueOf(uc_Values[35])+"','"+String.valueOf(uc_Values[36])+"','"+String.valueOf(uc_Values[37])+"','"+String.valueOf(uc_Values[38])+
			            		"','"+String.valueOf(uc_Values[39])+"','"+String.valueOf(uc_Values[40])+"','"+String.valueOf(uc_Values[41])+"','"+String.valueOf(uc_Values[42])+"','"+String.valueOf(uc_Values[43])+
			            		"','"+String.valueOf(uc_Values[44])+"','"+String.valueOf(uc_Values[45])+"','"+String.valueOf(uc_Values[46])+"','"+String.valueOf(uc_Values[47])+"','"+String.valueOf(uc_Values[48])+
			            		"','"+String.valueOf(uc_Values[49])+"','"+String.valueOf(uc_Values[50])+"','"+String.valueOf(uc_Values[51])+"','"+String.valueOf(uc_Values[52])+"','"+String.valueOf(ua_Values[53])+"')";  
							break;
							//===============IC谐波数据==============
					 case 5:					
			        	sql = "insert into " + "xb51_data" + " (" +   
			        			"'dbid','xb_id','xb_zl','jb','xb2','xb3','xb4','xb5','xb6','xb7','xb8','xb9','xb10'," +
			        			"'xb11','xb12','xb13','xb14','xb15','xb16','xb17','xb18','xb19','xb20','xb21','xb22'," +
			        			"'xb23','xb24','xb25','xb26','xb27','xb28','xb29','xb30','xb31','xb32','xb33','xb34'," +
			        			"'xb35','xb36','xb37','xb38','xb39','xb40','xb41','xb42','xb43','xb44','xb45','xb46'," +
			        			"'xb47','xb48','xb49','xb50','xb51','xb52','xb_szd') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1)+"','"+String.valueOf(ic_Values[0])+"','"+String.valueOf(ic_Values[1])+"','"+String.valueOf(ic_Values[2])+"','"+String.valueOf(ic_Values[3])+
			            		"','"+String.valueOf(ic_Values[4])+"','"+String.valueOf(ic_Values[5])+"','"+String.valueOf(ic_Values[6])+"','"+String.valueOf(ic_Values[7])+"','"+String.valueOf(ic_Values[8])+
			            		"','"+String.valueOf(ic_Values[9])+"','"+String.valueOf(ic_Values[10])+"','"+String.valueOf(ic_Values[11])+"','"+String.valueOf(ic_Values[12])+"','"+String.valueOf(ic_Values[13])+
			            		"','"+String.valueOf(ic_Values[14])+"','"+String.valueOf(ic_Values[15])+"','"+String.valueOf(ic_Values[16])+"','"+String.valueOf(ic_Values[17])+"','"+String.valueOf(ic_Values[18])+
			            		"','"+String.valueOf(ic_Values[19])+"','"+String.valueOf(ic_Values[20])+"','"+String.valueOf(ic_Values[21])+"','"+String.valueOf(ic_Values[22])+"','"+String.valueOf(ic_Values[23])+
			            		"','"+String.valueOf(ic_Values[24])+"','"+String.valueOf(ic_Values[25])+"','"+String.valueOf(ic_Values[26])+"','"+String.valueOf(ic_Values[27])+"','"+String.valueOf(ic_Values[28])+
			            		"','"+String.valueOf(ic_Values[29])+"','"+String.valueOf(ic_Values[30])+"','"+String.valueOf(ic_Values[31])+"','"+String.valueOf(ic_Values[32])+"','"+String.valueOf(ic_Values[33])+
			            		"','"+String.valueOf(ic_Values[34])+"','"+String.valueOf(ic_Values[35])+"','"+String.valueOf(ic_Values[36])+"','"+String.valueOf(ic_Values[37])+"','"+String.valueOf(ic_Values[38])+
			            		"','"+String.valueOf(ic_Values[39])+"','"+String.valueOf(ic_Values[40])+"','"+String.valueOf(ic_Values[41])+"','"+String.valueOf(ic_Values[42])+"','"+String.valueOf(ic_Values[43])+
			            		"','"+String.valueOf(ic_Values[44])+"','"+String.valueOf(ic_Values[45])+"','"+String.valueOf(ic_Values[46])+"','"+String.valueOf(ic_Values[47])+"','"+String.valueOf(ic_Values[48])+
			            		"','"+String.valueOf(ic_Values[49])+"','"+String.valueOf(ic_Values[50])+"','"+String.valueOf(ic_Values[51])+"','"+String.valueOf(ic_Values[52])+"','"+String.valueOf(ia_Values[53])+"')";  
							break;
					}	 
		        	try {  
		            		 sqldb.execSQL(sql);  
		           	   } catch (SQLException e) {   } 
		           }
		       else{
		    	   Object[] ags=null; 
		    	   sql="update xb51_data set xb_zl=?,jb=?,xb2=?,xb3=?,xb4=?,xb5=?,xb6=?,xb7=?,xb8=?,xb9=?,xb10=?,xb11=?,xb12=?,xb13=?,xb14=?,xb15=?,xb16=?," +
		            		"xb17=?,xb18=?,xb19=?,xb20=?,xb21=?,xb22=?,xb23=?,xb24=?,xb25=?,xb26=?,xb27=?,xb28=?,xb29=?,xb30=?,xb31=?,xb32=?,xb33=?,xb34=?," +
		            		"xb35=?,xb36=?,xb37=?,xb38=?,xb39=?,xb40=?,xb41=?,xb42=?,xb43=?,xb44=?,xb45=?,xb46=?,xb47=?,xb48=?,xb49=?,xb50=?,xb51=?,xb52=?," +
		            		"xb_szd=? where dbid=? and xb_id=?";
		         	switch(i){
		         		//===========UA数据更新============================
		         	case 0:
		         		ags=new Object[]{String.valueOf(ua_Values[0]),String.valueOf(ua_Values[1]),String.valueOf(ua_Values[2]),String.valueOf(ua_Values[3])
		            		,String.valueOf(ua_Values[4]),String.valueOf(ua_Values[5]),String.valueOf(ua_Values[6]),String.valueOf(ua_Values[7]),String.valueOf(ua_Values[8])
		            		,String.valueOf(ua_Values[9]),String.valueOf(ua_Values[10]),String.valueOf(ua_Values[11]),String.valueOf(ua_Values[12]),String.valueOf(ua_Values[13])
		            		,String.valueOf(ua_Values[14]),String.valueOf(ua_Values[15]),String.valueOf(ua_Values[16]),String.valueOf(ua_Values[17]),String.valueOf(ua_Values[18])
		            		,String.valueOf(ua_Values[19]),String.valueOf(ua_Values[20]),String.valueOf(ua_Values[21]),String.valueOf(ua_Values[22]),String.valueOf(ua_Values[23])
		            		,String.valueOf(ua_Values[24]),String.valueOf(ua_Values[25]),String.valueOf(ua_Values[26]),String.valueOf(ua_Values[27]),String.valueOf(ua_Values[28])
		            		,String.valueOf(ua_Values[29]),String.valueOf(ua_Values[30]),String.valueOf(ua_Values[31]),String.valueOf(ua_Values[32]),String.valueOf(ua_Values[33])
		            		,String.valueOf(ua_Values[34]),String.valueOf(ua_Values[35]),String.valueOf(ua_Values[36]),String.valueOf(ua_Values[37]),String.valueOf(ua_Values[38])
		            		,String.valueOf(ua_Values[39]),String.valueOf(ua_Values[40]),String.valueOf(ua_Values[41]),String.valueOf(ua_Values[42]),String.valueOf(ua_Values[43])
		            		,String.valueOf(ua_Values[44]),String.valueOf(ua_Values[45]),String.valueOf(ua_Values[46]),String.valueOf(ua_Values[47]),String.valueOf(ua_Values[48])
		            		,String.valueOf(ua_Values[49]),String.valueOf(ua_Values[50]),String.valueOf(ua_Values[51]),String.valueOf(ua_Values[52]),String.valueOf(ua_Values[53]),DBID,String.valueOf(i+1)};	    
		         		break;
		         		//===========IA数据更新============================
		         	case 1:
		         		ags=new Object[]{String.valueOf(ia_Values[0]),String.valueOf(ia_Values[1]),String.valueOf(ia_Values[2]),String.valueOf(ia_Values[3])
		            		,String.valueOf(ia_Values[4]),String.valueOf(ia_Values[5]),String.valueOf(ia_Values[6]),String.valueOf(ia_Values[7]),String.valueOf(ia_Values[8])
		            		,String.valueOf(ia_Values[9]),String.valueOf(ia_Values[10]),String.valueOf(ia_Values[11]),String.valueOf(ia_Values[12]),String.valueOf(ia_Values[13])
		            		,String.valueOf(ia_Values[14]),String.valueOf(ia_Values[15]),String.valueOf(ia_Values[16]),String.valueOf(ia_Values[17]),String.valueOf(ia_Values[18])
		            		,String.valueOf(ia_Values[19]),String.valueOf(ia_Values[20]),String.valueOf(ia_Values[21]),String.valueOf(ia_Values[22]),String.valueOf(ia_Values[23])
		            		,String.valueOf(ia_Values[24]),String.valueOf(ia_Values[25]),String.valueOf(ia_Values[26]),String.valueOf(ia_Values[27]),String.valueOf(ia_Values[28])
		            		,String.valueOf(ia_Values[29]),String.valueOf(ia_Values[30]),String.valueOf(ia_Values[31]),String.valueOf(ia_Values[32]),String.valueOf(ia_Values[33])
		            		,String.valueOf(ia_Values[34]),String.valueOf(ia_Values[35]),String.valueOf(ia_Values[36]),String.valueOf(ia_Values[37]),String.valueOf(ia_Values[38])
		            		,String.valueOf(ia_Values[39]),String.valueOf(ia_Values[40]),String.valueOf(ia_Values[41]),String.valueOf(ia_Values[42]),String.valueOf(ia_Values[43])
		            		,String.valueOf(ia_Values[44]),String.valueOf(ia_Values[45]),String.valueOf(ia_Values[46]),String.valueOf(ia_Values[47]),String.valueOf(ia_Values[48])
		            		,String.valueOf(ia_Values[49]),String.valueOf(ia_Values[50]),String.valueOf(ia_Values[51]),String.valueOf(ia_Values[52]),String.valueOf(ia_Values[53]),DBID,String.valueOf(i+1)};	    
		         		break;
		         		//===========UB数据更新============================
		         	case 2:
			         	ags=new Object[]{String.valueOf(ub_Values[0]),String.valueOf(ub_Values[1]),String.valueOf(ub_Values[2]),String.valueOf(ub_Values[3])
			            		,String.valueOf(ub_Values[4]),String.valueOf(ub_Values[5]),String.valueOf(ub_Values[6]),String.valueOf(ub_Values[7]),String.valueOf(ub_Values[8])
			            		,String.valueOf(ub_Values[9]),String.valueOf(ub_Values[10]),String.valueOf(ub_Values[11]),String.valueOf(ub_Values[12]),String.valueOf(ub_Values[13])
			            		,String.valueOf(ub_Values[14]),String.valueOf(ub_Values[15]),String.valueOf(ub_Values[16]),String.valueOf(ub_Values[17]),String.valueOf(ub_Values[18])
			            		,String.valueOf(ub_Values[19]),String.valueOf(ub_Values[20]),String.valueOf(ub_Values[21]),String.valueOf(ub_Values[22]),String.valueOf(ub_Values[23])
			            		,String.valueOf(ub_Values[24]),String.valueOf(ub_Values[25]),String.valueOf(ub_Values[26]),String.valueOf(ub_Values[27]),String.valueOf(ub_Values[28])
			            		,String.valueOf(ub_Values[29]),String.valueOf(ub_Values[30]),String.valueOf(ub_Values[31]),String.valueOf(ub_Values[32]),String.valueOf(ub_Values[33])
			            		,String.valueOf(ub_Values[34]),String.valueOf(ub_Values[35]),String.valueOf(ub_Values[36]),String.valueOf(ub_Values[37]),String.valueOf(ub_Values[38])
			            		,String.valueOf(ub_Values[39]),String.valueOf(ub_Values[40]),String.valueOf(ub_Values[41]),String.valueOf(ub_Values[42]),String.valueOf(ub_Values[43])
			            		,String.valueOf(ub_Values[44]),String.valueOf(ub_Values[45]),String.valueOf(ub_Values[46]),String.valueOf(ub_Values[47]),String.valueOf(ub_Values[48])
			            		,String.valueOf(ub_Values[49]),String.valueOf(ub_Values[50]),String.valueOf(ub_Values[51]),String.valueOf(ub_Values[52]),String.valueOf(ua_Values[53]),DBID,String.valueOf(i+1)};	    
			         		break;
			         	//===========IB数据更新============================
			         case 3:
			         	ags=new Object[]{String.valueOf(ib_Values[0]),String.valueOf(ib_Values[1]),String.valueOf(ib_Values[2]),String.valueOf(ib_Values[3])
			            		,String.valueOf(ib_Values[4]),String.valueOf(ib_Values[5]),String.valueOf(ib_Values[6]),String.valueOf(ib_Values[7]),String.valueOf(ib_Values[8])
			            		,String.valueOf(ib_Values[9]),String.valueOf(ib_Values[10]),String.valueOf(ib_Values[11]),String.valueOf(ib_Values[12]),String.valueOf(ib_Values[13])
			            		,String.valueOf(ib_Values[14]),String.valueOf(ib_Values[15]),String.valueOf(ib_Values[16]),String.valueOf(ib_Values[17]),String.valueOf(ib_Values[18])
			            		,String.valueOf(ib_Values[19]),String.valueOf(ib_Values[20]),String.valueOf(ib_Values[21]),String.valueOf(ib_Values[22]),String.valueOf(ib_Values[23])
			            		,String.valueOf(ib_Values[24]),String.valueOf(ib_Values[25]),String.valueOf(ib_Values[26]),String.valueOf(ib_Values[27]),String.valueOf(ib_Values[28])
			            		,String.valueOf(ib_Values[29]),String.valueOf(ib_Values[30]),String.valueOf(ib_Values[31]),String.valueOf(ib_Values[32]),String.valueOf(ib_Values[33])
			            		,String.valueOf(ib_Values[34]),String.valueOf(ib_Values[35]),String.valueOf(ib_Values[36]),String.valueOf(ib_Values[37]),String.valueOf(ib_Values[38])
			            		,String.valueOf(ib_Values[39]),String.valueOf(ib_Values[40]),String.valueOf(ib_Values[41]),String.valueOf(ib_Values[42]),String.valueOf(ib_Values[43])
			            		,String.valueOf(ib_Values[44]),String.valueOf(ib_Values[45]),String.valueOf(ib_Values[46]),String.valueOf(ib_Values[47]),String.valueOf(ib_Values[48])
			            		,String.valueOf(ib_Values[49]),String.valueOf(ib_Values[50]),String.valueOf(ib_Values[51]),String.valueOf(ib_Values[52]),String.valueOf(ia_Values[53]),DBID,String.valueOf(i+1)};	    
			         		break;
			         	//===========UC数据更新============================	
			         case 4:
				         ags=new Object[]{String.valueOf(uc_Values[0]),String.valueOf(uc_Values[1]),String.valueOf(uc_Values[2]),String.valueOf(uc_Values[3])
				            	,String.valueOf(uc_Values[4]),String.valueOf(uc_Values[5]),String.valueOf(uc_Values[6]),String.valueOf(uc_Values[7]),String.valueOf(uc_Values[8])
				            	,String.valueOf(uc_Values[9]),String.valueOf(uc_Values[10]),String.valueOf(uc_Values[11]),String.valueOf(uc_Values[12]),String.valueOf(uc_Values[13])
				            	,String.valueOf(uc_Values[14]),String.valueOf(uc_Values[15]),String.valueOf(uc_Values[16]),String.valueOf(uc_Values[17]),String.valueOf(uc_Values[18])
				            	,String.valueOf(uc_Values[19]),String.valueOf(uc_Values[20]),String.valueOf(uc_Values[21]),String.valueOf(uc_Values[22]),String.valueOf(uc_Values[23])
				            	,String.valueOf(uc_Values[24]),String.valueOf(uc_Values[25]),String.valueOf(uc_Values[26]),String.valueOf(uc_Values[27]),String.valueOf(uc_Values[28])
				            	,String.valueOf(uc_Values[29]),String.valueOf(uc_Values[30]),String.valueOf(uc_Values[31]),String.valueOf(uc_Values[32]),String.valueOf(uc_Values[33])
				            	,String.valueOf(uc_Values[34]),String.valueOf(uc_Values[35]),String.valueOf(uc_Values[36]),String.valueOf(uc_Values[37]),String.valueOf(uc_Values[38])
				            	,String.valueOf(uc_Values[39]),String.valueOf(uc_Values[40]),String.valueOf(uc_Values[41]),String.valueOf(uc_Values[42]),String.valueOf(uc_Values[43])
				            	,String.valueOf(uc_Values[44]),String.valueOf(uc_Values[45]),String.valueOf(uc_Values[46]),String.valueOf(uc_Values[47]),String.valueOf(uc_Values[48])
				            	,String.valueOf(uc_Values[49]),String.valueOf(uc_Values[50]),String.valueOf(uc_Values[51]),String.valueOf(uc_Values[52]),String.valueOf(ua_Values[53]),DBID,String.valueOf(i+1)};	    
				         	break;
				         //===========IC数据更新============================
				     case 5:
				         ags=new Object[]{String.valueOf(ic_Values[0]),String.valueOf(ic_Values[1]),String.valueOf(ic_Values[2]),String.valueOf(ic_Values[3])
				            	,String.valueOf(ic_Values[4]),String.valueOf(ic_Values[5]),String.valueOf(ic_Values[6]),String.valueOf(ic_Values[7]),String.valueOf(ic_Values[8])
				            	,String.valueOf(ic_Values[9]),String.valueOf(ic_Values[10]),String.valueOf(ic_Values[11]),String.valueOf(ic_Values[12]),String.valueOf(ic_Values[13])
				            	,String.valueOf(ic_Values[14]),String.valueOf(ic_Values[15]),String.valueOf(ic_Values[16]),String.valueOf(ic_Values[17]),String.valueOf(ic_Values[18])
				            	,String.valueOf(ic_Values[19]),String.valueOf(ic_Values[20]),String.valueOf(ic_Values[21]),String.valueOf(ic_Values[22]),String.valueOf(ic_Values[23])
				            	,String.valueOf(ic_Values[24]),String.valueOf(ic_Values[25]),String.valueOf(ic_Values[26]),String.valueOf(ic_Values[27]),String.valueOf(ic_Values[28])
				            	,String.valueOf(ic_Values[29]),String.valueOf(ic_Values[30]),String.valueOf(ic_Values[31]),String.valueOf(ic_Values[32]),String.valueOf(ic_Values[33])
				            	,String.valueOf(ic_Values[34]),String.valueOf(ic_Values[35]),String.valueOf(ic_Values[36]),String.valueOf(ic_Values[37]),String.valueOf(ic_Values[38])
				            	,String.valueOf(ic_Values[39]),String.valueOf(ic_Values[40]),String.valueOf(ic_Values[41]),String.valueOf(ic_Values[42]),String.valueOf(ic_Values[43])
				            	,String.valueOf(ic_Values[44]),String.valueOf(ic_Values[45]),String.valueOf(ic_Values[46]),String.valueOf(ic_Values[47]),String.valueOf(ic_Values[48])
				            	,String.valueOf(ic_Values[49]),String.valueOf(ic_Values[50]),String.valueOf(ic_Values[51]),String.valueOf(ic_Values[52]),String.valueOf(ia_Values[53]),DBID,String.valueOf(i+1)};	    
				         	break;
		         	}		
		         	     try {  
		         	           sqldb.execSQL(sql,ags);  
		         	         } catch (SQLException e) {  }
		            }
		}
	}
}
