package com.lx.checkameterclient.fragment;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class CAWaveViewerFragment extends Fragment {
	
	private  XYSeries series=null;
	private  XYSeries series_i=null; 
    private  XYMultipleSeriesDataset dataset=null;
	private  XYMultipleSeriesDataset dataset_i=null;  
    private  XYMultipleSeriesRenderer renderer=null;
	private  XYMultipleSeriesRenderer renderer_i=null; 
    private XYSeriesRenderer r=null,r_i=null;
    private  GraphicalView mChartView=null;
	private  GraphicalView mChartView_i=null;
	private DecimalFormat myformat1,myformat2,myformat3;
	 int count =128;
     int Temp_u1=0,Temp_u2=3,Temp_i1=0,Temp_i2=3;
     String[] titles = new String[] { "Ua曲线","Ub曲线" ,"Uc曲线"};
     String[] titles_i = new String[] { "Ia曲线","Ib曲线" ,"Ic曲线"};
     List<double[]> x = new ArrayList<double[]>();
     List<double[]> x_i = new ArrayList<double[]>();
     List<double[]> values = new ArrayList<double[]>();
     List<double[]> values_i = new ArrayList<double[]>();
     double[] ua_Values = null; double[] ub_Values = null; double[] uc_Values = null;    
     double[] ia_Values = null; double[] ib_Values = null; double[] ic_Values = null;
    PointStyle[] styles,styles_i;
    double u_xs=300,i_xs=4;
    boolean ui_flag=false;
    private  int sum1=0;
    private  int[] colors;
	private  int[] colors_i;
    private LinearLayout barchart,barchart_i,chart_u,chart_i;
    public static Button btn_savebx;
    private Dialog dialog;
    SQLiteDatabase sqldb;
	String sql;
	private File file = new File("/sdcard/bdlx/sxxy.db");// 数据库文件路径
    
    private Activity activity;
    private SegmentControl segmentcontrol,segmentcontrol1;
    private Thread mThreadread=null;
	private  boolean read_flag=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ca_wave_viewer_fragment, null);
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
        
        segmentcontrol=(SegmentControl)view.findViewById(R.id.dianyaxiangbie);
        //segmentcontrol.setSelectedIndex(Declare.wave_flag_for_volt);
		segmentcontrol.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		        //处理点击标签的事件
		    	int count_u=dataset.getSeriesCount();
                for(int i=count_u-1;i>-1;i--){
            	dataset.removeSeries(i);
                }
            	series.clear(); 
		    	switch(index){               
                case 0:  
                	 Temp_u1=0;Temp_u2=1; 
                	 Declare.wave_flag_for_volt=0;
                  break;
                  //-----------------------------------------------
                 case 1:  
                	 Temp_u1=1;Temp_u2=2;
                	 Declare.wave_flag_for_volt=1;
                  break;
                  //-----------------------------------------------
                 case 2:  
                	 Temp_u1=2;Temp_u2=3;
                	 Declare.wave_flag_for_volt=2;
                  break;
                  //-----------------------------------------------                 
                 case 3:  
                	 Temp_u1=0;Temp_u2=3; 
                	 Declare.wave_flag_for_volt=3;
//                    DisplayToast("请注意，切换到合相曲线"); 
                    //txv.setText("Ia谐波图");
                  break;
                  //-----------------------------------------------
                }
            	 //renderer.addSeriesRenderer(r);
            	 for (int i = Temp_u1; i < Temp_u2; i++) {
                     series = new XYSeries(titles[i]);
                      double[] xv = x.get(i);
                      double[] yv = values.get(i);
                      int seriesLength = xv.length;
                      for (int k = 0; k < seriesLength; k++) {
                          series.add(xv[k],yv[k]);
                      }
                      dataset.addSeries(series);
                     //renderer.addSeriesRenderer(i,r); 
            	 }
            	 renderer.getSeriesRendererAt(0).setColor(colors[Temp_u1]);
            	 mChartView.invalidate();
		    } 
		}); 
        segmentcontrol1=(SegmentControl)view.findViewById(R.id.dianliuxiangbie);
        //segmentcontrol1.setSelectedIndex(Declare.wave_flag_for_amphere);
		segmentcontrol1.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		        //处理点击标签的事件
		    	int count_i=dataset_i.getSeriesCount();
                for(int i=count_i-1;i>-1;i--){
            	dataset_i.removeSeries(i);
                }
            	series_i.clear(); 
 //           	ui_flag=true;
            	switch(index){               
                case 0:  
                	Temp_i1=0;Temp_i2=1; 
                	Declare.wave_flag_for_amphere=0;
//                    DisplayToast("请注意，切换到Ia曲线");  
                  break;
                  //-----------------------------------------------
                 case 1:  
                	 Temp_i1=1;Temp_i2=2;
                	 Declare.wave_flag_for_amphere=1;
//                      DisplayToast("请注意，切换到Ib曲线");  
                  break;
                  //-----------------------------------------------
                 case 2:  
                	 Temp_i1=2;Temp_i2=3;
                	 Declare.wave_flag_for_amphere=2;
//                     DisplayToast("请注意，切换到Ic曲线");
                  break;
                  //-----------------------------------------------                 
                 case 3:  
                	Temp_i1=0;Temp_i2=3; 
                	Declare.wave_flag_for_amphere=3;
//                    DisplayToast("请注意，切换到合相曲线"); 
                  break;
                  //-----------------------------------------------
                }
            	 //renderer.addSeriesRenderer(r);
            	 for (int i = Temp_i1; i < Temp_i2; i++) {
                     series_i = new XYSeries(titles_i[i]);
                      double[] xv_i = x_i.get(i);
                      double[] yv_i = values_i.get(i);
                      int seriesLength = xv_i.length;
                      for (int k = 0; k < seriesLength-1; k++) {
                          series_i.add(xv_i[k+1],yv_i[k]);
                      }
                      dataset_i.addSeries(series_i);
//                      renderer_i.addSeriesRenderer(i,r_i); 
            	 }
            	 renderer_i.getSeriesRendererAt(0).setColor(colors_i[Temp_i1]);
            	 mChartView_i.invalidate();
		    } 
		});
		chart_u=(LinearLayout)view.findViewById(R.id.chart_u);
        chart_i=(LinearLayout)view.findViewById(R.id.chart_i);
		myformat1= new DecimalFormat("0.0000");
		
		btn_savebx=(Button)view.findViewById(R.id.btn_savebx);
		btn_savebx.setOnClickListener(new btn_click());
		segmentcontrol.setSelectedIndex(Declare.wave_flag_for_volt);
		segmentcontrol1.setSelectedIndex(Declare.wave_flag_for_amphere);
		Spinner spinner = (Spinner) view.findViewById(R.id.spi1);
		
	    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
	    	@Override
	    	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
	    		 switch(position){
	    		 case 0:
	    			 //count=128;
	    			 inti_bx();
	    			 chart_u.setVisibility(0);
	    			 chart_i.setVisibility(8);
	    			 renderer.setXLabels(4);
//	    			 barchart.invalidate();
	    			 mChartView.invalidate();
	    			 break;
	    		 case 1:
	    			 //count=128;
	    			 inti_bx();
	    			 chart_u.setVisibility(8);
	    			 chart_i.setVisibility(0);
	    			 renderer_i.setXLabels(4);
//	    			 barchart_i.invalidate();
	    			 mChartView_i.invalidate();
	    			 break;
	    		 case 2:
	    			 //count=128;
	    			 
	    			 //he_u_rb.setChecked(true);
	    			 //he_i_rb.setChecked(true);
	    			 inti_bx();

	    			 chart_u.setVisibility(0);
	    			 chart_i.setVisibility(0);
	    		/*	 renderer.setXLabels(12); 
	    			 renderer_i.setXLabels(12); 
	    			 barchart.removeAllViews();
	    			 barchart_i.removeAllViews();
	    			 mChartView = ChartFactory.getLineChartView(activity, dataset, renderer);           
	    		     barchart.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
	    		     //在指定linearlayout中显示电流曲线
	    		     mChartView_i = ChartFactory.getLineChartView(activity, dataset_i, renderer_i);           
	    		     barchart_i.addView(mChartView_i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
	    		     mChartView.invalidate();
	    		     mChartView_i.invalidate();
	    			 //barchart.invalidate();
	    			 //barchart_i.invalidate();*/
	    			 break;
	    		 }
	    	}
	    	@Override
	    	public void onNothingSelected(AdapterView<?> view) {
//	       		Log.i(TAG,  view.getClass().getName());
	    	}
	    });
	    spinner.setSelection(2, false);
        //初始化波形数据为零
        /*
        for(int i=0;i<Declare.data_bxxs.length;i++){
			Declare.data_bxxs[i]=0;
		}
        */
        count =128;
        x.clear();
        x_i.clear();
        x.add(new double[count]);
        x.add(new double[count]);
        x.add(new double[count]);
        x_i.add(new double[count]);
        x_i.add(new double[count]);
        x_i.add(new double[count]);
        ua_Values = new double[count];
        ub_Values = new double[count];
        uc_Values = new double[count];
        
        ia_Values = new double[count];
        ib_Values = new double[count];
        ic_Values = new double[count];
        values.add(ua_Values);
        values.add(ub_Values);
        values.add(uc_Values);
        values_i.add(ia_Values);
        values_i.add(ib_Values);
        values_i.add(ic_Values);
        
        for (int i = 0; i < count; i++) {
          double angle = i * 2.8125;
          x.get(0)[i] = i;//angle;
          x.get(1)[i] = i;//angle;
          x.get(2)[i] = i;//angle;
          x_i.get(0)[i] = i;//angle;
          x_i.get(1)[i] = i;//angle;
          x_i.get(2)[i] = i;//angle;
          double ua_rAngle = Math.toRadians(angle);//角度转换为弧度
          double ub_rAngle = Math.toRadians(angle+120);//角度转换为弧度
          double uc_rAngle = Math.toRadians(angle+240);//角度转换为弧度
          ua_Values[i] = Math.sin(ua_rAngle)*u_xs;
          ub_Values[i] = Math.sin(ub_rAngle)*u_xs;
          uc_Values[i] = Math.sin(uc_rAngle)*u_xs;
          ia_Values[i] = Math.sin(ua_rAngle)*i_xs;
          ib_Values[i] = Math.sin(ub_rAngle)*i_xs;
          ic_Values[i] = Math.sin(uc_rAngle)*i_xs;
        }

        dataset = new XYMultipleSeriesDataset();
        //series = new XYSeries(titles[0]);
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            series = new XYSeries(titles[i]);
            series.clear();
            double[] xv = x.get(i);
            double[] yv = values.get(i);
            int seriesLength = xv.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xv[k],yv[k]);
            }
            dataset.addSeries(series);
        }
        //电流曲线
        dataset_i = new XYMultipleSeriesDataset();
        int length_i = titles_i.length;
        for (int i = 0; i < length_i; i++) {
           series_i = new XYSeries(titles_i[i]);
            double[] xv_i = x_i.get(i);
            double[] yv_i = values_i.get(i);
            int seriesLength = xv_i.length;
            for (int k = 0; k < seriesLength-1; k++) {
                series_i.add(xv_i[k+1],yv_i[k]);
            }
            dataset_i.addSeries(series_i);
        }

        colors = new int[] {Color.YELLOW,Color.GREEN,Color.RED,Color.BLACK};
        styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT,PointStyle.POINT,};
        renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(25);
        renderer.setChartTitleTextSize(25);
        renderer.setLabelsTextSize(25);
        renderer.setLegendTextSize(25);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(colors[3]);
        renderer.setShowGridX(true);
        renderer.setMargins(new int[] { 20, 40, 20, 20 });//设置图表的外边框(上/左/下/右)  
        int length1 = colors.length;
        for (int j = 0; j < length1-1; j++) {
        	r = new XYSeriesRenderer();
            r.setColor(colors[j]); 
            r.setPointStyle(styles[j]); 
            r.setFillPoints(true); 
            r.setLineWidth(3);  
            renderer.addSeriesRenderer(j,r); 
        }
        renderer.setXLabels(3); 
        renderer.setYLabels(6);   
        renderer.setXLabelsAlign(Align.RIGHT);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(true);
        renderer.setZoomButtonsVisible(true);
        renderer.setZoomRate(1.1f);
        //renderer.setsetBarSpacing(0.5f);
        renderer.setFitLegend(true);
        
        //电流曲线
        colors_i = new int[] {Color.YELLOW,Color.GREEN,Color.RED,Color.BLACK};
        styles_i = new PointStyle[] { PointStyle.POINT, PointStyle.POINT,PointStyle.POINT,};
        renderer_i = new XYMultipleSeriesRenderer();
        renderer_i.setAxisTitleTextSize(25);
        renderer_i.setChartTitleTextSize(25);
        renderer_i.setLabelsTextSize(25);
        renderer_i.setLegendTextSize(25);
        renderer_i.setApplyBackgroundColor(true);
        renderer_i.setBackgroundColor(colors_i[3]);
        renderer_i.setShowGridX(true);
        renderer_i.setMargins(new int[] { 20, 40, 20, 20 });//设置图表的外边框(上/左/下/右)

        int length1_i = colors_i.length;
        for (int j = 0; j < length1_i-1; j++) {
        	r_i = new XYSeriesRenderer();
            r_i.setColor(colors_i[j]); 
            r_i.setPointStyle(styles_i[j]); 
            r_i.setFillPoints(true); 
            r_i.setLineWidth(3);  
            renderer_i.addSeriesRenderer(j,r_i); 
        }
        renderer_i.setXLabels(6); 
        //renderer_i.setYLabels(7);   
        renderer_i.setXLabelsAlign(Align.RIGHT);
        renderer_i.setYLabelsAlign(Align.RIGHT);
        renderer_i.setPanEnabled(true, false);
        renderer_i.setZoomEnabled(true);
        renderer_i.setZoomButtonsVisible(true);
        renderer_i.setZoomRate(1.1f);
        //renderer.setsetBarSpacing(0.5f);
        renderer_i.setFitLegend(true);
      //renderer = buildBarRenderer(colors);
        //setChartSettings(renderer, "", "电压曲线图", "电压值", 0,400,-150 ,150 , Color.GRAY, Color.LTGRAY);
        //setChartSettings(renderer_i, "", "电流曲线图", "电流值", 0,130, -5, 5, Color.GRAY, Color.LTGRAY);
        //renderer.getSeriesRendererAt(0).setDisplayChartValues(false);
        //renderer.getSeriesRendererAt(0).setChartValuesTextSize(15);
//        renderer_i.getSeriesRendererAt(1).setDisplayChartValues(true);
        //在指定linearlayout中显示电压曲线
        barchart = (LinearLayout) view.findViewById(R.id.barchart); 
        mChartView = ChartFactory.getLineChartView(activity, dataset, renderer);           
        barchart.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
        //在指定linearlayout中显示电流曲线
        barchart_i = (LinearLayout) view.findViewById(R.id.barchart_i); 
        mChartView_i = ChartFactory.getLineChartView(activity, dataset_i, renderer_i);           
        barchart_i.addView(mChartView_i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
        
        /*for (int i = Temp_u1; i < Temp_u2; i++) {
            series = new XYSeries(titles[i]);
             double[] xv = x.get(i);
             double[] yv = values.get(i);
             int seriesLength = xv.length;
             for (int k = 0; k < seriesLength; k++) {
                 series.add(xv[k],yv[k]);
             }
             dataset.addSeries(series);
            
             
            //renderer.addSeriesRenderer(i,r); 
   	 }*/ 

      //=======启动刷新线程==============
        if(mThreadread==null){
        	//Declare.send_flag=5;
        	Declare.Circle=true;		
        	mSocketClient.SendClientmsg(Declare.Circle);
        	read_flag=true;
        	mThreadread=new mThreadread();
        	mThreadread.start();        
        	
        }
		return view;
	}
	// fragment对象声明周期方法，建立和activity关联关系时的回调
		@Override
		public void onAttach(Activity activity) {
			this.activity = activity;
			super.onAttach(activity);
		}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if(mThreadread!=null){
			//mSocketClient.StopClientmsg();
			Declare.receive_flag=false;
			read_flag=false;
			mThreadread.interrupt();
			mThreadread=null;
		
		}
		Declare.connect_num=0;
        Declare.connect_num1=0;
        Declare.rec_overtime=false;
        sqldb.close();
		for(int i=0;i<Declare.data_bxxs.length;i++){
			Declare.data_bxxs[i]=0;
		}
	}
	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
	}
	/*
	 * 切换波形图后初始化
	 */
	public void inti_bx(){
		 int count_i=x.size();
         for(int i=count_i-1;i>-1;i--){
     	    x.remove(i);
     	    x_i.remove(i);
     	    values.remove(i);
     	    values_i.remove(i);
         }
         x.clear();
         x_i.clear();
         values.clear();
         values_i.clear();
		 x.add(new double[count]);
         x.add(new double[count]);
         x.add(new double[count]);
         x_i.add(new double[count]);
         x_i.add(new double[count]);
         x_i.add(new double[count]);
         ua_Values = new double[count];
         ub_Values = new double[count];
         uc_Values = new double[count];
        
         ia_Values = new double[count];
         ib_Values = new double[count];
         ic_Values = new double[count];
         values.add(ua_Values);
         values.add(ub_Values);
         values.add(uc_Values);
         values_i.add(ia_Values);
         values_i.add(ib_Values);
         values_i.add(ic_Values);
	}
	public class btn_click implements OnClickListener {               
    	@Override            
    	public void onClick(View v) { 
    		switch (v.getId()){
    		case R.id.btn_savebx:
//    			Toast.makeText(getApplicationContext(), "标题按钮", Toast.LENGTH_SHORT).show();  
    			dialog = new confirm(activity,R.style.MyDialog);       	        	       	
   	         	dialog.show();
   	         	TextView text = (TextView)dialog.findViewById(R.id.textview2); 
   	         	if(QueryRecord()==true){
	         		text.setText("表号："+getdbid()+"波形数据已存在，是否覆盖？");
	         	}
	         	else{
	         		text.setText("确认保存表号："+getdbid()+"波形数据吗？");
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
	         	Toast.makeText(activity.getApplicationContext(), "表号："+getdbid()+"的波形数据已保存",Toast.LENGTH_LONG).show();
	    	   break;
	    	    //=================================
	    	case R.id.but_qx:
	    		dialog.cancel();//
	    	break;
	    	}
		} 
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
		Cursor cursor = sqldb.rawQuery("select * from bx128_data where dbid=?", new String[]{getdbid()});
	    if(cursor.getCount()==0){
	    	flag=false;
	    }
	    else{flag=true;}
	    return flag;
	}
	//==========保存数据====================
	public void SaveRecord(String DBID) { 
		String sql=null;
		String temp=null;
		for(int i=0;i<6;i++)
		{
//			   Object[] ags ={myformat.format(id),text_date1.getText().toString()+text_time1.getText().toString(),"保定朗信","竞秀街创业中心","A00001","B00001","薄谷开来","薄谷开来"};
		             //=========查询是否有相同记录再插入数据==========================
			if(!sqldb.isOpen())	 {
	 	 		 sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
	 	 	}   
			Cursor cursor = sqldb.rawQuery("select * from bx128_data where dbid=? and bx_id=?", new String[]{DBID,String.valueOf(i+1)});
		             
		       if(cursor.getCount()==0){
					switch(i)
					{
					//===============UA波形数据==============
					case 0:	
						temp="";
						sql = "insert into " + "bx128_data" + " (" +   
		        			"'dbid','bx_id','bx1','bx2','bx3','bx4','bx5','bx6','bx7','bx8','bx9','bx10'," +
								
		        			"'bx11','bx12','bx13','bx14','bx15','bx16','bx17','bx18','bx19','bx20','bx21','bx22'," +
		        			"'bx23','bx24','bx25','bx26','bx27','bx28','bx29','bx30','bx31','bx32','bx33','bx34'," +
		        			"'bx35','bx36','bx37','bx38','bx39','bx40','bx41','bx42','bx43','bx44','bx45','bx46'," +
		        			"'bx47','bx48','bx49','bx50','bx51','bx52','bx53','bx54','bx55','bx56','bx57','bx58'," +
		        			"'bx59','bx60','bx61','bx62','bx63','bx64','bx65','bx66','bx67','bx68','bx69','bx70'," +
		        			"'bx71','bx72','bx73','bx74','bx75','bx76','bx77','bx78','bx79','bx80','bx81','bx82'," +
		        			"'bx83','bx84','bx85','bx86','bx87','bx88','bx89','bx90','bx91','bx92','bx93','bx94'," +
		        			"'bx95','bx96','bx97','bx98','bx99','bx100','bx101','bx102','bx103','bx104','bx105','bx106'," +
		        			"'bx107','bx108','bx109','bx110','bx111','bx112','bx113','bx114','bx115','bx116','bx117','bx118'," +
		        			"'bx119','bx120','bx121','bx122','bx123','bx124','bx125','bx126','bx127','bx128') " +
		            		"values('" + DBID+ "','"+String.valueOf(i+1);
						   for(int j=0;j<128;j++)
						   {
							   temp+="','"+String.valueOf(myformat1.format(ua_Values[j]));
						   }
						   sql=sql+temp+"')";
						   System.out.println(sql);
						   break;
					//===============IA谐波数据==============
					case 1:	
						temp="";
						sql = "insert into " + "bx128_data" + " (" +   
			        			"'dbid','bx_id','bx1','bx2','bx3','bx4','bx5','bx6','bx7','bx8','bx9','bx10'," +
			        			"'bx11','bx12','bx13','bx14','bx15','bx16','bx17','bx18','bx19','bx20','bx21','bx22'," +
			        			"'bx23','bx24','bx25','bx26','bx27','bx28','bx29','bx30','bx31','bx32','bx33','bx34'," +
			        			"'bx35','bx36','bx37','bx38','bx39','bx40','bx41','bx42','bx43','bx44','bx45','bx46'," +
			        			"'bx47','bx48','bx49','bx50','bx51','bx52','bx53','bx54','bx55','bx56','bx57','bx58'," +
			        			"'bx59','bx60','bx61','bx62','bx63','bx64','bx65','bx66','bx67','bx68','bx69','bx70'," +
			        			"'bx71','bx72','bx73','bx74','bx75','bx76','bx77','bx78','bx79','bx80','bx81','bx82'," +
			        			"'bx83','bx84','bx85','bx86','bx87','bx88','bx89','bx90','bx91','bx92','bx93','bx94'," +
			        			"'bx95','bx96','bx97','bx98','bx99','bx100','bx101','bx102','bx103','bx104','bx105','bx106'," +
			        			"'bx107','bx108','bx109','bx110','bx111','bx112','bx113','bx114','bx115','bx116','bx117','bx118'," +
			        			"'bx119','bx120','bx121','bx122','bx123','bx124','bx125','bx126','bx127','bx128') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1);
							   for(int j=0;j<128;j++)
							   {
								   temp+="','"+String.valueOf(myformat1.format(ia_Values[j]));
							   }
							   sql=sql+temp+"')";
							break;
					//===============UB波形数据==============
					case 2:	
						temp="";
						sql = "insert into " + "bx128_data" + " (" +   
			        			"'dbid','bx_id','bx1','bx2','bx3','bx4','bx5','bx6','bx7','bx8','bx9','bx10'," +
			        			"'bx11','bx12','bx13','bx14','bx15','bx16','bx17','bx18','bx19','bx20','bx21','bx22'," +
			        			"'bx23','bx24','bx25','bx26','bx27','bx28','bx29','bx30','bx31','bx32','bx33','bx34'," +
			        			"'bx35','bx36','bx37','bx38','bx39','bx40','bx41','bx42','bx43','bx44','bx45','bx46'," +
			        			"'bx47','bx48','bx49','bx50','bx51','bx52','bx53','bx54','bx55','bx56','bx57','bx58'," +
			        			"'bx59','bx60','bx61','bx62','bx63','bx64','bx65','bx66','bx67','bx68','bx69','bx70'," +
			        			"'bx71','bx72','bx73','bx74','bx75','bx76','bx77','bx78','bx79','bx80','bx81','bx82'," +
			        			"'bx83','bx84','bx85','bx86','bx87','bx88','bx89','bx90','bx91','bx92','bx93','bx94'," +
			        			"'bx95','bx96','bx97','bx98','bx99','bx100','bx101','bx102','bx103','bx104','bx105','bx106'," +
			        			"'bx107','bx108','bx109','bx110','bx111','bx112','bx113','bx114','bx115','bx116','bx117','bx118'," +
			        			"'bx119','bx120','bx121','bx122','bx123','bx124','bx125','bx126','bx127','bx128') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1);
							   for(int j=0;j<128;j++)
							   {
								   temp+="','"+String.valueOf(myformat1.format(ub_Values[j]));
							   }
							   sql=sql+temp+"')";
							break;
					//===============IB波形数据==============
					case 3:		
						temp="";
						sql = "insert into " + "bx128_data" + " (" +   
			        			"'dbid','bx_id','bx1','bx2','bx3','bx4','bx5','bx6','bx7','bx8','bx9','bx10'," +
			        			"'bx11','bx12','bx13','bx14','bx15','bx16','bx17','bx18','bx19','bx20','bx21','bx22'," +
			        			"'bx23','bx24','bx25','bx26','bx27','bx28','bx29','bx30','bx31','bx32','bx33','bx34'," +
			        			"'bx35','bx36','bx37','bx38','bx39','bx40','bx41','bx42','bx43','bx44','bx45','bx46'," +
			        			"'bx47','bx48','bx49','bx50','bx51','bx52','bx53','bx54','bx55','bx56','bx57','bx58'," +
			        			"'bx59','bx60','bx61','bx62','bx63','bx64','bx65','bx66','bx67','bx68','bx69','bx70'," +
			        			"'bx71','bx72','bx73','bx74','bx75','bx76','bx77','bx78','bx79','bx80','bx81','bx82'," +
			        			"'bx83','bx84','bx85','bx86','bx87','bx88','bx89','bx90','bx91','bx92','bx93','bx94'," +
			        			"'bx95','bx96','bx97','bx98','bx99','bx100','bx101','bx102','bx103','bx104','bx105','bx106'," +
			        			"'bx107','bx108','bx109','bx110','bx111','bx112','bx113','bx114','bx115','bx116','bx117','bx118'," +
			        			"'bx119','bx120','bx121','bx122','bx123','bx124','bx125','bx126','bx127','bx128') " +
			            		"values('" + DBID+ "','"+String.valueOf(i+1)+"";
							   for(int j=0;j<128;j++)
							   {
								   temp+="','"+String.valueOf(myformat1.format(ib_Values[j]));
							   }
							   sql=sql+temp+"')";
							break;
							//===============UC谐波数据==============
					 case 4:
						 temp="";
						 sql = "insert into " + "bx128_data" + " (" +   
				        			"'dbid','bx_id','bx1','bx2','bx3','bx4','bx5','bx6','bx7','bx8','bx9','bx10'," +
				        			"'bx11','bx12','bx13','bx14','bx15','bx16','bx17','bx18','bx19','bx20','bx21','bx22'," +
				        			"'bx23','bx24','bx25','bx26','bx27','bx28','bx29','bx30','bx31','bx32','bx33','bx34'," +
				        			"'bx35','bx36','bx37','bx38','bx39','bx40','bx41','bx42','bx43','bx44','bx45','bx46'," +
				        			"'bx47','bx48','bx49','bx50','bx51','bx52','bx53','bx54','bx55','bx56','bx57','bx58'," +
				        			"'bx59','bx60','bx61','bx62','bx63','bx64','bx65','bx66','bx67','bx68','bx69','bx70'," +
				        			"'bx71','bx72','bx73','bx74','bx75','bx76','bx77','bx78','bx79','bx80','bx81','bx82'," +
				        			"'bx83','bx84','bx85','bx86','bx87','bx88','bx89','bx90','bx91','bx92','bx93','bx94'," +
				        			"'bx95','bx96','bx97','bx98','bx99','bx100','bx101','bx102','bx103','bx104','bx105','bx106'," +
				        			"'bx107','bx108','bx109','bx110','bx111','bx112','bx113','bx114','bx115','bx116','bx117','bx118'," +
				        			"'bx119','bx120','bx121','bx122','bx123','bx124','bx125','bx126','bx127','bx128') " +
				            		"values('" + DBID+ "','"+String.valueOf(i+1);
								   for(int j=0;j<128;j++)
								   {
									   temp+="','"+String.valueOf(myformat1.format(uc_Values[j]));
								   }
								   sql=sql+temp+"')";
							break;
							//===============IC谐波数据==============
					 case 5:	
						 temp="";
						 sql = "insert into " + "bx128_data" + " (" +   
				        			"'dbid','bx_id','bx1','bx2','bx3','bx4','bx5','bx6','bx7','bx8','bx9','bx10'," +
				        			"'bx11','bx12','bx13','bx14','bx15','bx16','bx17','bx18','bx19','bx20','bx21','bx22'," +
				        			"'bx23','bx24','bx25','bx26','bx27','bx28','bx29','bx30','bx31','bx32','bx33','bx34'," +
				        			"'bx35','bx36','bx37','bx38','bx39','bx40','bx41','bx42','bx43','bx44','bx45','bx46'," +
				        			"'bx47','bx48','bx49','bx50','bx51','bx52','bx53','bx54','bx55','bx56','bx57','bx58'," +
				        			"'bx59','bx60','bx61','bx62','bx63','bx64','bx65','bx66','bx67','bx68','bx69','bx70'," +
				        			"'bx71','bx72','bx73','bx74','bx75','bx76','bx77','bx78','bx79','bx80','bx81','bx82'," +
				        			"'bx83','bx84','bx85','bx86','bx87','bx88','bx89','bx90','bx91','bx92','bx93','bx94'," +
				        			"'bx95','bx96','bx97','bx98','bx99','bx100','bx101','bx102','bx103','bx104','bx105','bx106'," +
				        			"'bx107','bx108','bx109','bx110','bx111','bx112','bx113','bx114','bx115','bx116','bx117','bx118'," +
				        			"'bx119','bx120','bx121','bx122','bx123','bx124','bx125','bx126','bx127','bx128') " +
				            		"values('" + DBID+ "','"+String.valueOf(i+1);
								   for(int j=0;j<128;j++)
								   {
									   temp+="','"+String.valueOf(myformat1.format(ic_Values[j]));
								   }
								   sql=sql+temp+"')";
							break;
					
					}	 
		        	try {  
		            		 sqldb.execSQL(sql);  
		           	   } catch (SQLException e) {   } 
		           }
		       else{
		    	   Object[] ags=null; 
		    	   sql="update bx128_data set bx1=?,bx2=?,bx3=?,bx4=?,bx5=?,bx6=?,bx7=?,bx8=?,bx9=?,bx10=?,bx11=?,bx12=?,bx13=?,bx14=?,bx15=?,bx16=?," +
		            		"bx17=?,bx18=?,bx19=?,bx20=?,bx21=?,bx22=?,bx23=?,bx24=?,bx25=?,bx26=?,bx27=?,bx28=?,bx29=?,bx30=?,bx31=?,bx32=?,bx33=?,bx34=?," +
		            		"bx35=?,bx36=?,bx37=?,bx38=?,bx39=?,bx40=?,bx41=?,bx42=?,bx43=?,bx44=?,bx45=?,bx46=?,bx47=?,bx48=?,bx49=?,bx50=?,bx51=?,bx52=?," +
		            		"bx53=?,bx54=?,bx55=?,bx56=?,bx57=?,bx58=?,bx59=?,bx60=?,bx61=?,bx62=?,bx63=?,bx64=?,bx65=?,bx66=?,bx67=?,bx68=?,bx69=?,bx70=?," +
		            		"bx71=?,bx72=?,bx73=?,bx74=?,bx75=?,bx76=?,bx77=?,bx78=?,bx79=?,bx80=?,bx81=?,bx82=?,bx83=?,bx84=?,bx85=?,bx86=?,bx87=?,bx88=?," +
		            		"bx89=?,bx90=?,bx91=?,bx92=?,bx93=?,bx94=?,bx95=?,bx96=?,bx97=?,bx98=?,bx99=?,bx100=?,bx101=?,bx102=?,bx103=?,bx104=?,bx105=?,bx106=?," +
		            		"bx107=?,bx108=?,bx109=?,bx110=?,bx111=?,bx112=?,bx113=?,bx114=?,bx115=?,bx116=?,bx117=?,bx118=?,bx119=?,bx120=?,bx121=?,bx122=?,bx123=?,bx124=?," +
		            		"bx125=?,bx126=?,bx127=?,bx128=? where dbid=? and bx_id=?";
		         	switch(i){
		         		//===========UA数据更新============================
		         	case 0:
		         		ags=new Object[]{String.valueOf(myformat1.format(ua_Values[0])),String.valueOf(myformat1.format(ua_Values[1])),String.valueOf(myformat1.format(ua_Values[2])),String.valueOf(myformat1.format(ua_Values[3]))
		            		,String.valueOf(myformat1.format(ua_Values[4])),String.valueOf(myformat1.format(ua_Values[5])),String.valueOf(myformat1.format(ua_Values[6])),String.valueOf(myformat1.format(ua_Values[7])),String.valueOf(myformat1.format(ua_Values[8]))
		            		,String.valueOf(myformat1.format(ua_Values[9])),String.valueOf(myformat1.format(ua_Values[10])),String.valueOf(myformat1.format(ua_Values[11])),String.valueOf(myformat1.format(ua_Values[12])),String.valueOf(myformat1.format(ua_Values[13]))
		            		,String.valueOf(myformat1.format(ua_Values[14])),String.valueOf(myformat1.format(ua_Values[15])),String.valueOf(myformat1.format(ua_Values[16])),String.valueOf(myformat1.format(ua_Values[17])),String.valueOf(myformat1.format(ua_Values[18]))
		            		,String.valueOf(myformat1.format(ua_Values[19])),String.valueOf(myformat1.format(ua_Values[20])),String.valueOf(myformat1.format(ua_Values[21])),String.valueOf(myformat1.format(ua_Values[22])),String.valueOf(myformat1.format(ua_Values[23]))
		            		,String.valueOf(myformat1.format(ua_Values[24])),String.valueOf(myformat1.format(ua_Values[25])),String.valueOf(myformat1.format(ua_Values[26])),String.valueOf(myformat1.format(ua_Values[27])),String.valueOf(myformat1.format(ua_Values[28]))
		            		,String.valueOf(myformat1.format(ua_Values[29])),String.valueOf(myformat1.format(ua_Values[30])),String.valueOf(myformat1.format(ua_Values[31])),String.valueOf(myformat1.format(ua_Values[32])),String.valueOf(myformat1.format(ua_Values[33]))
		            		,String.valueOf(myformat1.format(ua_Values[34])),String.valueOf(myformat1.format(ua_Values[35])),String.valueOf(myformat1.format(ua_Values[36])),String.valueOf(myformat1.format(ua_Values[37])),String.valueOf(myformat1.format(ua_Values[38]))
		            		,String.valueOf(myformat1.format(ua_Values[39])),String.valueOf(myformat1.format(ua_Values[40])),String.valueOf(myformat1.format(ua_Values[41])),String.valueOf(myformat1.format(ua_Values[42])),String.valueOf(myformat1.format(ua_Values[43]))
		            		,String.valueOf(myformat1.format(ua_Values[44])),String.valueOf(myformat1.format(ua_Values[45])),String.valueOf(myformat1.format(ua_Values[46])),String.valueOf(myformat1.format(ua_Values[47])),String.valueOf(myformat1.format(ua_Values[48]))
		            		,String.valueOf(myformat1.format(ua_Values[49])),String.valueOf(myformat1.format(ua_Values[50])),String.valueOf(myformat1.format(ua_Values[51])),String.valueOf(myformat1.format(ua_Values[52])),String.valueOf(myformat1.format(ua_Values[53]))
		            		,String.valueOf(myformat1.format(ua_Values[54])),String.valueOf(myformat1.format(ua_Values[55])),String.valueOf(myformat1.format(ua_Values[56])),String.valueOf(myformat1.format(ua_Values[57])),String.valueOf(myformat1.format(ua_Values[58]))
		            		,String.valueOf(myformat1.format(ua_Values[59])),String.valueOf(myformat1.format(ua_Values[60])),String.valueOf(myformat1.format(ua_Values[61])),String.valueOf(myformat1.format(ua_Values[62])),String.valueOf(myformat1.format(ua_Values[63]))
		            		,String.valueOf(myformat1.format(ua_Values[64])),String.valueOf(myformat1.format(ua_Values[65])),String.valueOf(myformat1.format(ua_Values[66])),String.valueOf(myformat1.format(ua_Values[67])),String.valueOf(myformat1.format(ua_Values[68]))
		            		,String.valueOf(myformat1.format(ua_Values[69])),String.valueOf(myformat1.format(ua_Values[70])),String.valueOf(myformat1.format(ua_Values[71])),String.valueOf(myformat1.format(ua_Values[72])),String.valueOf(myformat1.format(ua_Values[73]))
		            		,String.valueOf(myformat1.format(ua_Values[74])),String.valueOf(myformat1.format(ua_Values[75])),String.valueOf(myformat1.format(ua_Values[76])),String.valueOf(myformat1.format(ua_Values[77])),String.valueOf(myformat1.format(ua_Values[78]))
		            		,String.valueOf(myformat1.format(ua_Values[79])),String.valueOf(myformat1.format(ua_Values[80])),String.valueOf(myformat1.format(ua_Values[81])),String.valueOf(myformat1.format(ua_Values[82])),String.valueOf(myformat1.format(ua_Values[83]))
		            		,String.valueOf(myformat1.format(ua_Values[84])),String.valueOf(myformat1.format(ua_Values[85])),String.valueOf(myformat1.format(ua_Values[86])),String.valueOf(myformat1.format(ua_Values[87])),String.valueOf(myformat1.format(ua_Values[88]))
		            		,String.valueOf(myformat1.format(ua_Values[89])),String.valueOf(myformat1.format(ua_Values[90])),String.valueOf(myformat1.format(ua_Values[91])),String.valueOf(myformat1.format(ua_Values[92])),String.valueOf(myformat1.format(ua_Values[93]))
		            		,String.valueOf(myformat1.format(ua_Values[94])),String.valueOf(myformat1.format(ua_Values[95])),String.valueOf(myformat1.format(ua_Values[96])),String.valueOf(myformat1.format(ua_Values[97])),String.valueOf(myformat1.format(ua_Values[98]))
		            		,String.valueOf(myformat1.format(ua_Values[99])),String.valueOf(myformat1.format(ua_Values[100])),String.valueOf(myformat1.format(ua_Values[101])),String.valueOf(myformat1.format(ua_Values[102])),String.valueOf(myformat1.format(ua_Values[103]))
		            		,String.valueOf(myformat1.format(ua_Values[104])),String.valueOf(myformat1.format(ua_Values[105])),String.valueOf(myformat1.format(ua_Values[106])),String.valueOf(myformat1.format(ua_Values[107])),String.valueOf(myformat1.format(ua_Values[108]))
		            		,String.valueOf(myformat1.format(ua_Values[109])),String.valueOf(myformat1.format(ua_Values[110])),String.valueOf(myformat1.format(ua_Values[111])),String.valueOf(myformat1.format(ua_Values[112])),String.valueOf(myformat1.format(ua_Values[113]))
		            		,String.valueOf(myformat1.format(ua_Values[114])),String.valueOf(myformat1.format(ua_Values[115])),String.valueOf(myformat1.format(ua_Values[116])),String.valueOf(myformat1.format(ua_Values[117])),String.valueOf(myformat1.format(ua_Values[118]))
		            		,String.valueOf(myformat1.format(ua_Values[119])),String.valueOf(myformat1.format(ua_Values[120])),String.valueOf(myformat1.format(ua_Values[121])),String.valueOf(myformat1.format(ua_Values[122])),String.valueOf(myformat1.format(ua_Values[123]))
		            		,String.valueOf(myformat1.format(ua_Values[124])),String.valueOf(myformat1.format(ua_Values[125])),String.valueOf(myformat1.format(ua_Values[126])),String.valueOf(myformat1.format(ua_Values[127])),DBID,String.valueOf(i+1)};	    
		         		break;
		         		//===========IA数据更新============================
		         	case 1:
		         		ags=new Object[]{String.valueOf(myformat1.format(ia_Values[0])),String.valueOf(myformat1.format(ia_Values[1])),String.valueOf(myformat1.format(ia_Values[2])),String.valueOf(myformat1.format(ia_Values[3]))
			            		,String.valueOf(myformat1.format(ia_Values[4])),String.valueOf(myformat1.format(ia_Values[5])),String.valueOf(myformat1.format(ia_Values[6])),String.valueOf(myformat1.format(ia_Values[7])),String.valueOf(myformat1.format(ia_Values[8]))
			            		,String.valueOf(myformat1.format(ia_Values[9])),String.valueOf(myformat1.format(ia_Values[10])),String.valueOf(myformat1.format(ia_Values[11])),String.valueOf(myformat1.format(ia_Values[12])),String.valueOf(myformat1.format(ia_Values[13]))
			            		,String.valueOf(myformat1.format(ia_Values[14])),String.valueOf(myformat1.format(ia_Values[15])),String.valueOf(myformat1.format(ia_Values[16])),String.valueOf(myformat1.format(ia_Values[17])),String.valueOf(myformat1.format(ia_Values[18]))
			            		,String.valueOf(myformat1.format(ia_Values[19])),String.valueOf(myformat1.format(ia_Values[20])),String.valueOf(myformat1.format(ia_Values[21])),String.valueOf(myformat1.format(ia_Values[22])),String.valueOf(myformat1.format(ia_Values[23]))
			            		,String.valueOf(myformat1.format(ia_Values[24])),String.valueOf(myformat1.format(ia_Values[25])),String.valueOf(myformat1.format(ia_Values[26])),String.valueOf(myformat1.format(ia_Values[27])),String.valueOf(myformat1.format(ia_Values[28]))
			            		,String.valueOf(myformat1.format(ia_Values[29])),String.valueOf(myformat1.format(ia_Values[30])),String.valueOf(myformat1.format(ia_Values[31])),String.valueOf(myformat1.format(ia_Values[32])),String.valueOf(myformat1.format(ia_Values[33]))
			            		,String.valueOf(myformat1.format(ia_Values[34])),String.valueOf(myformat1.format(ia_Values[35])),String.valueOf(myformat1.format(ia_Values[36])),String.valueOf(myformat1.format(ia_Values[37])),String.valueOf(myformat1.format(ia_Values[38]))
			            		,String.valueOf(myformat1.format(ia_Values[39])),String.valueOf(myformat1.format(ia_Values[40])),String.valueOf(myformat1.format(ia_Values[41])),String.valueOf(myformat1.format(ia_Values[42])),String.valueOf(myformat1.format(ia_Values[43]))
			            		,String.valueOf(myformat1.format(ia_Values[44])),String.valueOf(myformat1.format(ia_Values[45])),String.valueOf(myformat1.format(ia_Values[46])),String.valueOf(myformat1.format(ia_Values[47])),String.valueOf(myformat1.format(ia_Values[48]))
			            		,String.valueOf(myformat1.format(ia_Values[49])),String.valueOf(myformat1.format(ia_Values[50])),String.valueOf(myformat1.format(ia_Values[51])),String.valueOf(myformat1.format(ia_Values[52])),String.valueOf(myformat1.format(ia_Values[53]))
			            		,String.valueOf(myformat1.format(ia_Values[54])),String.valueOf(myformat1.format(ia_Values[55])),String.valueOf(myformat1.format(ia_Values[56])),String.valueOf(myformat1.format(ia_Values[57])),String.valueOf(myformat1.format(ia_Values[58]))
			            		,String.valueOf(myformat1.format(ia_Values[59])),String.valueOf(myformat1.format(ia_Values[60])),String.valueOf(myformat1.format(ia_Values[61])),String.valueOf(myformat1.format(ia_Values[62])),String.valueOf(myformat1.format(ia_Values[63]))
			            		,String.valueOf(myformat1.format(ia_Values[64])),String.valueOf(myformat1.format(ia_Values[65])),String.valueOf(myformat1.format(ia_Values[66])),String.valueOf(myformat1.format(ia_Values[67])),String.valueOf(myformat1.format(ia_Values[68]))
			            		,String.valueOf(myformat1.format(ia_Values[69])),String.valueOf(myformat1.format(ia_Values[70])),String.valueOf(myformat1.format(ia_Values[71])),String.valueOf(myformat1.format(ia_Values[72])),String.valueOf(myformat1.format(ia_Values[73]))
			            		,String.valueOf(myformat1.format(ia_Values[74])),String.valueOf(myformat1.format(ia_Values[75])),String.valueOf(myformat1.format(ia_Values[76])),String.valueOf(myformat1.format(ia_Values[77])),String.valueOf(myformat1.format(ia_Values[78]))
			            		,String.valueOf(myformat1.format(ia_Values[79])),String.valueOf(myformat1.format(ia_Values[80])),String.valueOf(myformat1.format(ia_Values[81])),String.valueOf(myformat1.format(ia_Values[82])),String.valueOf(myformat1.format(ia_Values[83]))
			            		,String.valueOf(myformat1.format(ia_Values[84])),String.valueOf(myformat1.format(ia_Values[85])),String.valueOf(myformat1.format(ia_Values[86])),String.valueOf(myformat1.format(ia_Values[87])),String.valueOf(myformat1.format(ia_Values[88]))
			            		,String.valueOf(myformat1.format(ia_Values[89])),String.valueOf(myformat1.format(ia_Values[90])),String.valueOf(myformat1.format(ia_Values[91])),String.valueOf(myformat1.format(ia_Values[92])),String.valueOf(myformat1.format(ia_Values[93]))
			            		,String.valueOf(myformat1.format(ia_Values[94])),String.valueOf(myformat1.format(ia_Values[95])),String.valueOf(myformat1.format(ia_Values[96])),String.valueOf(myformat1.format(ia_Values[97])),String.valueOf(myformat1.format(ia_Values[98]))
			            		,String.valueOf(myformat1.format(ia_Values[99])),String.valueOf(myformat1.format(ia_Values[100])),String.valueOf(myformat1.format(ia_Values[101])),String.valueOf(myformat1.format(ia_Values[102])),String.valueOf(myformat1.format(ia_Values[103]))
			            		,String.valueOf(myformat1.format(ia_Values[104])),String.valueOf(myformat1.format(ia_Values[105])),String.valueOf(myformat1.format(ia_Values[106])),String.valueOf(myformat1.format(ia_Values[107])),String.valueOf(myformat1.format(ia_Values[108]))
			            		,String.valueOf(myformat1.format(ia_Values[109])),String.valueOf(myformat1.format(ia_Values[110])),String.valueOf(myformat1.format(ia_Values[111])),String.valueOf(myformat1.format(ia_Values[112])),String.valueOf(myformat1.format(ia_Values[113]))
			            		,String.valueOf(myformat1.format(ia_Values[114])),String.valueOf(myformat1.format(ia_Values[115])),String.valueOf(myformat1.format(ia_Values[116])),String.valueOf(myformat1.format(ia_Values[117])),String.valueOf(myformat1.format(ia_Values[118]))
			            		,String.valueOf(myformat1.format(ia_Values[119])),String.valueOf(myformat1.format(ia_Values[120])),String.valueOf(myformat1.format(ia_Values[121])),String.valueOf(myformat1.format(ia_Values[122])),String.valueOf(myformat1.format(ia_Values[123]))
			            		,String.valueOf(myformat1.format(ia_Values[124])),String.valueOf(myformat1.format(ia_Values[125])),String.valueOf(myformat1.format(ia_Values[126])),String.valueOf(myformat1.format(ia_Values[127])),DBID,String.valueOf(i+1)};	    
		         		break;
		         		//===========UB数据更新============================
		         	case 2:
		         		ags=new Object[]{String.valueOf(myformat1.format(ub_Values[0])),String.valueOf(myformat1.format(ub_Values[1])),String.valueOf(myformat1.format(ub_Values[2])),String.valueOf(myformat1.format(ub_Values[3]))
			            		,String.valueOf(myformat1.format(ub_Values[4])),String.valueOf(myformat1.format(ub_Values[5])),String.valueOf(myformat1.format(ub_Values[6])),String.valueOf(myformat1.format(ub_Values[7])),String.valueOf(myformat1.format(ub_Values[8]))
			            		,String.valueOf(myformat1.format(ub_Values[9])),String.valueOf(myformat1.format(ub_Values[10])),String.valueOf(myformat1.format(ub_Values[11])),String.valueOf(myformat1.format(ub_Values[12])),String.valueOf(myformat1.format(ub_Values[13]))
			            		,String.valueOf(myformat1.format(ub_Values[14])),String.valueOf(myformat1.format(ub_Values[15])),String.valueOf(myformat1.format(ub_Values[16])),String.valueOf(myformat1.format(ub_Values[17])),String.valueOf(myformat1.format(ub_Values[18]))
			            		,String.valueOf(myformat1.format(ub_Values[19])),String.valueOf(myformat1.format(ub_Values[20])),String.valueOf(myformat1.format(ub_Values[21])),String.valueOf(myformat1.format(ub_Values[22])),String.valueOf(myformat1.format(ub_Values[23]))
			            		,String.valueOf(myformat1.format(ub_Values[24])),String.valueOf(myformat1.format(ub_Values[25])),String.valueOf(myformat1.format(ub_Values[26])),String.valueOf(myformat1.format(ub_Values[27])),String.valueOf(myformat1.format(ub_Values[28]))
			            		,String.valueOf(myformat1.format(ub_Values[29])),String.valueOf(myformat1.format(ub_Values[30])),String.valueOf(myformat1.format(ub_Values[31])),String.valueOf(myformat1.format(ub_Values[32])),String.valueOf(myformat1.format(ub_Values[33]))
			            		,String.valueOf(myformat1.format(ub_Values[34])),String.valueOf(myformat1.format(ub_Values[35])),String.valueOf(myformat1.format(ub_Values[36])),String.valueOf(myformat1.format(ub_Values[37])),String.valueOf(myformat1.format(ub_Values[38]))
			            		,String.valueOf(myformat1.format(ub_Values[39])),String.valueOf(myformat1.format(ub_Values[40])),String.valueOf(myformat1.format(ub_Values[41])),String.valueOf(myformat1.format(ub_Values[42])),String.valueOf(myformat1.format(ub_Values[43]))
			            		,String.valueOf(myformat1.format(ub_Values[44])),String.valueOf(myformat1.format(ub_Values[45])),String.valueOf(myformat1.format(ub_Values[46])),String.valueOf(myformat1.format(ub_Values[47])),String.valueOf(myformat1.format(ub_Values[48]))
			            		,String.valueOf(myformat1.format(ub_Values[49])),String.valueOf(myformat1.format(ub_Values[50])),String.valueOf(myformat1.format(ub_Values[51])),String.valueOf(myformat1.format(ub_Values[52])),String.valueOf(myformat1.format(ub_Values[53]))
			            		,String.valueOf(myformat1.format(ub_Values[54])),String.valueOf(myformat1.format(ub_Values[55])),String.valueOf(myformat1.format(ub_Values[56])),String.valueOf(myformat1.format(ub_Values[57])),String.valueOf(myformat1.format(ub_Values[58]))
			            		,String.valueOf(myformat1.format(ub_Values[59])),String.valueOf(myformat1.format(ub_Values[60])),String.valueOf(myformat1.format(ub_Values[61])),String.valueOf(myformat1.format(ub_Values[62])),String.valueOf(myformat1.format(ub_Values[63]))
			            		,String.valueOf(myformat1.format(ub_Values[64])),String.valueOf(myformat1.format(ub_Values[65])),String.valueOf(myformat1.format(ub_Values[66])),String.valueOf(myformat1.format(ub_Values[67])),String.valueOf(myformat1.format(ub_Values[68]))
			            		,String.valueOf(myformat1.format(ub_Values[69])),String.valueOf(myformat1.format(ub_Values[70])),String.valueOf(myformat1.format(ub_Values[71])),String.valueOf(myformat1.format(ub_Values[72])),String.valueOf(myformat1.format(ub_Values[73]))
			            		,String.valueOf(myformat1.format(ub_Values[74])),String.valueOf(myformat1.format(ub_Values[75])),String.valueOf(myformat1.format(ub_Values[76])),String.valueOf(myformat1.format(ub_Values[77])),String.valueOf(myformat1.format(ub_Values[78]))
			            		,String.valueOf(myformat1.format(ub_Values[79])),String.valueOf(myformat1.format(ub_Values[80])),String.valueOf(myformat1.format(ub_Values[81])),String.valueOf(myformat1.format(ub_Values[82])),String.valueOf(myformat1.format(ub_Values[83]))
			            		,String.valueOf(myformat1.format(ub_Values[84])),String.valueOf(myformat1.format(ub_Values[85])),String.valueOf(myformat1.format(ub_Values[86])),String.valueOf(myformat1.format(ub_Values[87])),String.valueOf(myformat1.format(ub_Values[88]))
			            		,String.valueOf(myformat1.format(ub_Values[89])),String.valueOf(myformat1.format(ub_Values[90])),String.valueOf(myformat1.format(ub_Values[91])),String.valueOf(myformat1.format(ub_Values[92])),String.valueOf(myformat1.format(ub_Values[93]))
			            		,String.valueOf(myformat1.format(ub_Values[94])),String.valueOf(myformat1.format(ub_Values[95])),String.valueOf(myformat1.format(ub_Values[96])),String.valueOf(myformat1.format(ub_Values[97])),String.valueOf(myformat1.format(ub_Values[98]))
			            		,String.valueOf(myformat1.format(ub_Values[99])),String.valueOf(myformat1.format(ub_Values[100])),String.valueOf(myformat1.format(ub_Values[101])),String.valueOf(myformat1.format(ub_Values[102])),String.valueOf(myformat1.format(ub_Values[103]))
			            		,String.valueOf(myformat1.format(ub_Values[104])),String.valueOf(myformat1.format(ub_Values[105])),String.valueOf(myformat1.format(ub_Values[106])),String.valueOf(myformat1.format(ub_Values[107])),String.valueOf(myformat1.format(ub_Values[108]))
			            		,String.valueOf(myformat1.format(ub_Values[109])),String.valueOf(myformat1.format(ub_Values[110])),String.valueOf(myformat1.format(ub_Values[111])),String.valueOf(myformat1.format(ub_Values[112])),String.valueOf(myformat1.format(ub_Values[113]))
			            		,String.valueOf(myformat1.format(ub_Values[114])),String.valueOf(myformat1.format(ub_Values[115])),String.valueOf(myformat1.format(ub_Values[116])),String.valueOf(myformat1.format(ub_Values[117])),String.valueOf(myformat1.format(ub_Values[118]))
			            		,String.valueOf(myformat1.format(ub_Values[119])),String.valueOf(myformat1.format(ub_Values[120])),String.valueOf(myformat1.format(ub_Values[121])),String.valueOf(myformat1.format(ub_Values[122])),String.valueOf(myformat1.format(ub_Values[123]))
			            		,String.valueOf(myformat1.format(ub_Values[124])),String.valueOf(myformat1.format(ub_Values[125])),String.valueOf(myformat1.format(ub_Values[126])),String.valueOf(myformat1.format(ub_Values[127])),DBID,String.valueOf(i+1)};	    
			         		break;
			         	//===========IB数据更新============================
			         case 3:
			         		ags=new Object[]{String.valueOf(myformat1.format(ib_Values[0])),String.valueOf(myformat1.format(ib_Values[1])),String.valueOf(myformat1.format(ib_Values[2])),String.valueOf(myformat1.format(ib_Values[3]))
				            		,String.valueOf(myformat1.format(ib_Values[4])),String.valueOf(myformat1.format(ib_Values[5])),String.valueOf(myformat1.format(ib_Values[6])),String.valueOf(myformat1.format(ib_Values[7])),String.valueOf(myformat1.format(ib_Values[8]))
				            		,String.valueOf(myformat1.format(ib_Values[9])),String.valueOf(myformat1.format(ib_Values[10])),String.valueOf(myformat1.format(ib_Values[11])),String.valueOf(myformat1.format(ib_Values[12])),String.valueOf(myformat1.format(ib_Values[13]))
				            		,String.valueOf(myformat1.format(ib_Values[14])),String.valueOf(myformat1.format(ib_Values[15])),String.valueOf(myformat1.format(ib_Values[16])),String.valueOf(myformat1.format(ib_Values[17])),String.valueOf(myformat1.format(ib_Values[18]))
				            		,String.valueOf(myformat1.format(ib_Values[19])),String.valueOf(myformat1.format(ib_Values[20])),String.valueOf(myformat1.format(ib_Values[21])),String.valueOf(myformat1.format(ib_Values[22])),String.valueOf(myformat1.format(ib_Values[23]))
				            		,String.valueOf(myformat1.format(ib_Values[24])),String.valueOf(myformat1.format(ib_Values[25])),String.valueOf(myformat1.format(ib_Values[26])),String.valueOf(myformat1.format(ib_Values[27])),String.valueOf(myformat1.format(ib_Values[28]))
				            		,String.valueOf(myformat1.format(ib_Values[29])),String.valueOf(myformat1.format(ib_Values[30])),String.valueOf(myformat1.format(ib_Values[31])),String.valueOf(myformat1.format(ib_Values[32])),String.valueOf(myformat1.format(ib_Values[33]))
				            		,String.valueOf(myformat1.format(ib_Values[34])),String.valueOf(myformat1.format(ib_Values[35])),String.valueOf(myformat1.format(ib_Values[36])),String.valueOf(myformat1.format(ib_Values[37])),String.valueOf(myformat1.format(ib_Values[38]))
				            		,String.valueOf(myformat1.format(ib_Values[39])),String.valueOf(myformat1.format(ib_Values[40])),String.valueOf(myformat1.format(ib_Values[41])),String.valueOf(myformat1.format(ib_Values[42])),String.valueOf(myformat1.format(ib_Values[43]))
				            		,String.valueOf(myformat1.format(ib_Values[44])),String.valueOf(myformat1.format(ib_Values[45])),String.valueOf(myformat1.format(ib_Values[46])),String.valueOf(myformat1.format(ib_Values[47])),String.valueOf(myformat1.format(ib_Values[48]))
				            		,String.valueOf(myformat1.format(ib_Values[49])),String.valueOf(myformat1.format(ib_Values[50])),String.valueOf(myformat1.format(ib_Values[51])),String.valueOf(myformat1.format(ib_Values[52])),String.valueOf(myformat1.format(ib_Values[53]))
				            		,String.valueOf(myformat1.format(ib_Values[54])),String.valueOf(myformat1.format(ib_Values[55])),String.valueOf(myformat1.format(ib_Values[56])),String.valueOf(myformat1.format(ib_Values[57])),String.valueOf(myformat1.format(ib_Values[58]))
				            		,String.valueOf(myformat1.format(ib_Values[59])),String.valueOf(myformat1.format(ib_Values[60])),String.valueOf(myformat1.format(ib_Values[61])),String.valueOf(myformat1.format(ib_Values[62])),String.valueOf(myformat1.format(ib_Values[63]))
				            		,String.valueOf(myformat1.format(ib_Values[64])),String.valueOf(myformat1.format(ib_Values[65])),String.valueOf(myformat1.format(ib_Values[66])),String.valueOf(myformat1.format(ib_Values[67])),String.valueOf(myformat1.format(ib_Values[68]))
				            		,String.valueOf(myformat1.format(ib_Values[69])),String.valueOf(myformat1.format(ib_Values[70])),String.valueOf(myformat1.format(ib_Values[71])),String.valueOf(myformat1.format(ib_Values[72])),String.valueOf(myformat1.format(ib_Values[73]))
				            		,String.valueOf(myformat1.format(ib_Values[74])),String.valueOf(myformat1.format(ib_Values[75])),String.valueOf(myformat1.format(ib_Values[76])),String.valueOf(myformat1.format(ib_Values[77])),String.valueOf(myformat1.format(ib_Values[78]))
				            		,String.valueOf(myformat1.format(ib_Values[79])),String.valueOf(myformat1.format(ib_Values[80])),String.valueOf(myformat1.format(ib_Values[81])),String.valueOf(myformat1.format(ib_Values[82])),String.valueOf(myformat1.format(ib_Values[83]))
				            		,String.valueOf(myformat1.format(ib_Values[84])),String.valueOf(myformat1.format(ib_Values[85])),String.valueOf(myformat1.format(ib_Values[86])),String.valueOf(myformat1.format(ib_Values[87])),String.valueOf(myformat1.format(ib_Values[88]))
				            		,String.valueOf(myformat1.format(ib_Values[89])),String.valueOf(myformat1.format(ib_Values[90])),String.valueOf(myformat1.format(ib_Values[91])),String.valueOf(myformat1.format(ib_Values[92])),String.valueOf(myformat1.format(ib_Values[93]))
				            		,String.valueOf(myformat1.format(ib_Values[94])),String.valueOf(myformat1.format(ib_Values[95])),String.valueOf(myformat1.format(ib_Values[96])),String.valueOf(myformat1.format(ib_Values[97])),String.valueOf(myformat1.format(ib_Values[98]))
				            		,String.valueOf(myformat1.format(ib_Values[99])),String.valueOf(myformat1.format(ib_Values[100])),String.valueOf(myformat1.format(ib_Values[101])),String.valueOf(myformat1.format(ib_Values[102])),String.valueOf(myformat1.format(ib_Values[103]))
				            		,String.valueOf(myformat1.format(ib_Values[104])),String.valueOf(myformat1.format(ib_Values[105])),String.valueOf(myformat1.format(ib_Values[106])),String.valueOf(myformat1.format(ib_Values[107])),String.valueOf(myformat1.format(ib_Values[108]))
				            		,String.valueOf(myformat1.format(ib_Values[109])),String.valueOf(myformat1.format(ib_Values[110])),String.valueOf(myformat1.format(ib_Values[111])),String.valueOf(myformat1.format(ib_Values[112])),String.valueOf(myformat1.format(ib_Values[113]))
				            		,String.valueOf(myformat1.format(ib_Values[114])),String.valueOf(myformat1.format(ib_Values[115])),String.valueOf(myformat1.format(ib_Values[116])),String.valueOf(myformat1.format(ib_Values[117])),String.valueOf(myformat1.format(ib_Values[118]))
				            		,String.valueOf(myformat1.format(ib_Values[119])),String.valueOf(myformat1.format(ib_Values[120])),String.valueOf(myformat1.format(ib_Values[121])),String.valueOf(myformat1.format(ib_Values[122])),String.valueOf(myformat1.format(ib_Values[123]))
				            		,String.valueOf(myformat1.format(ib_Values[124])),String.valueOf(myformat1.format(ib_Values[125])),String.valueOf(myformat1.format(ib_Values[126])),String.valueOf(myformat1.format(ib_Values[127])),DBID,String.valueOf(i+1)};	    
			         		break;
			         	//===========UC数据更新============================	
			         case 4:
			         		ags=new Object[]{String.valueOf(myformat1.format(uc_Values[0])),String.valueOf(myformat1.format(uc_Values[1])),String.valueOf(myformat1.format(uc_Values[2])),String.valueOf(myformat1.format(uc_Values[3]))
				            		,String.valueOf(myformat1.format(uc_Values[4])),String.valueOf(myformat1.format(uc_Values[5])),String.valueOf(myformat1.format(uc_Values[6])),String.valueOf(myformat1.format(uc_Values[7])),String.valueOf(myformat1.format(uc_Values[8]))
				            		,String.valueOf(myformat1.format(uc_Values[9])),String.valueOf(myformat1.format(uc_Values[10])),String.valueOf(myformat1.format(uc_Values[11])),String.valueOf(myformat1.format(uc_Values[12])),String.valueOf(myformat1.format(uc_Values[13]))
				            		,String.valueOf(myformat1.format(uc_Values[14])),String.valueOf(myformat1.format(uc_Values[15])),String.valueOf(myformat1.format(uc_Values[16])),String.valueOf(myformat1.format(uc_Values[17])),String.valueOf(myformat1.format(uc_Values[18]))
				            		,String.valueOf(myformat1.format(uc_Values[19])),String.valueOf(myformat1.format(uc_Values[20])),String.valueOf(myformat1.format(uc_Values[21])),String.valueOf(myformat1.format(uc_Values[22])),String.valueOf(myformat1.format(uc_Values[23]))
				            		,String.valueOf(myformat1.format(uc_Values[24])),String.valueOf(myformat1.format(uc_Values[25])),String.valueOf(myformat1.format(uc_Values[26])),String.valueOf(myformat1.format(uc_Values[27])),String.valueOf(myformat1.format(uc_Values[28]))
				            		,String.valueOf(myformat1.format(uc_Values[29])),String.valueOf(myformat1.format(uc_Values[30])),String.valueOf(myformat1.format(uc_Values[31])),String.valueOf(myformat1.format(uc_Values[32])),String.valueOf(myformat1.format(uc_Values[33]))
				            		,String.valueOf(myformat1.format(uc_Values[34])),String.valueOf(myformat1.format(uc_Values[35])),String.valueOf(myformat1.format(uc_Values[36])),String.valueOf(myformat1.format(uc_Values[37])),String.valueOf(myformat1.format(uc_Values[38]))
				            		,String.valueOf(myformat1.format(uc_Values[39])),String.valueOf(myformat1.format(uc_Values[40])),String.valueOf(myformat1.format(uc_Values[41])),String.valueOf(myformat1.format(uc_Values[42])),String.valueOf(myformat1.format(uc_Values[43]))
				            		,String.valueOf(myformat1.format(uc_Values[44])),String.valueOf(myformat1.format(uc_Values[45])),String.valueOf(myformat1.format(uc_Values[46])),String.valueOf(myformat1.format(uc_Values[47])),String.valueOf(myformat1.format(uc_Values[48]))
				            		,String.valueOf(myformat1.format(uc_Values[49])),String.valueOf(myformat1.format(uc_Values[50])),String.valueOf(myformat1.format(uc_Values[51])),String.valueOf(myformat1.format(uc_Values[52])),String.valueOf(myformat1.format(uc_Values[53]))
				            		,String.valueOf(myformat1.format(uc_Values[54])),String.valueOf(myformat1.format(uc_Values[55])),String.valueOf(myformat1.format(uc_Values[56])),String.valueOf(myformat1.format(uc_Values[57])),String.valueOf(myformat1.format(uc_Values[58]))
				            		,String.valueOf(myformat1.format(uc_Values[59])),String.valueOf(myformat1.format(uc_Values[60])),String.valueOf(myformat1.format(uc_Values[61])),String.valueOf(myformat1.format(uc_Values[62])),String.valueOf(myformat1.format(uc_Values[63]))
				            		,String.valueOf(myformat1.format(uc_Values[64])),String.valueOf(myformat1.format(uc_Values[65])),String.valueOf(myformat1.format(uc_Values[66])),String.valueOf(myformat1.format(uc_Values[67])),String.valueOf(myformat1.format(uc_Values[68]))
				            		,String.valueOf(myformat1.format(uc_Values[69])),String.valueOf(myformat1.format(uc_Values[70])),String.valueOf(myformat1.format(uc_Values[71])),String.valueOf(myformat1.format(uc_Values[72])),String.valueOf(myformat1.format(uc_Values[73]))
				            		,String.valueOf(myformat1.format(uc_Values[74])),String.valueOf(myformat1.format(uc_Values[75])),String.valueOf(myformat1.format(uc_Values[76])),String.valueOf(myformat1.format(uc_Values[77])),String.valueOf(myformat1.format(uc_Values[78]))
				            		,String.valueOf(myformat1.format(uc_Values[79])),String.valueOf(myformat1.format(uc_Values[80])),String.valueOf(myformat1.format(uc_Values[81])),String.valueOf(myformat1.format(uc_Values[82])),String.valueOf(myformat1.format(uc_Values[83]))
				            		,String.valueOf(myformat1.format(uc_Values[84])),String.valueOf(myformat1.format(uc_Values[85])),String.valueOf(myformat1.format(uc_Values[86])),String.valueOf(myformat1.format(uc_Values[87])),String.valueOf(myformat1.format(uc_Values[88]))
				            		,String.valueOf(myformat1.format(uc_Values[89])),String.valueOf(myformat1.format(uc_Values[90])),String.valueOf(myformat1.format(uc_Values[91])),String.valueOf(myformat1.format(uc_Values[92])),String.valueOf(myformat1.format(uc_Values[93]))
				            		,String.valueOf(myformat1.format(uc_Values[94])),String.valueOf(myformat1.format(uc_Values[95])),String.valueOf(myformat1.format(uc_Values[96])),String.valueOf(myformat1.format(uc_Values[97])),String.valueOf(myformat1.format(uc_Values[98]))
				            		,String.valueOf(myformat1.format(uc_Values[99])),String.valueOf(myformat1.format(uc_Values[100])),String.valueOf(myformat1.format(uc_Values[101])),String.valueOf(myformat1.format(uc_Values[102])),String.valueOf(myformat1.format(uc_Values[103]))
				            		,String.valueOf(myformat1.format(uc_Values[104])),String.valueOf(myformat1.format(uc_Values[105])),String.valueOf(myformat1.format(uc_Values[106])),String.valueOf(myformat1.format(uc_Values[107])),String.valueOf(myformat1.format(uc_Values[108]))
				            		,String.valueOf(myformat1.format(uc_Values[109])),String.valueOf(myformat1.format(uc_Values[110])),String.valueOf(myformat1.format(uc_Values[111])),String.valueOf(myformat1.format(uc_Values[112])),String.valueOf(myformat1.format(uc_Values[113]))
				            		,String.valueOf(myformat1.format(uc_Values[114])),String.valueOf(myformat1.format(uc_Values[115])),String.valueOf(myformat1.format(uc_Values[116])),String.valueOf(myformat1.format(uc_Values[117])),String.valueOf(myformat1.format(uc_Values[118]))
				            		,String.valueOf(myformat1.format(uc_Values[119])),String.valueOf(myformat1.format(uc_Values[120])),String.valueOf(myformat1.format(uc_Values[121])),String.valueOf(myformat1.format(uc_Values[122])),String.valueOf(myformat1.format(uc_Values[123]))
				            		,String.valueOf(myformat1.format(uc_Values[124])),String.valueOf(myformat1.format(uc_Values[125])),String.valueOf(myformat1.format(uc_Values[126])),String.valueOf(myformat1.format(uc_Values[127])),DBID,String.valueOf(i+1)};	    
				         	break;
				         //===========IC数据更新============================
				     case 5:
			         		ags=new Object[]{String.valueOf(myformat1.format(ic_Values[0])),String.valueOf(myformat1.format(ic_Values[1])),String.valueOf(myformat1.format(ic_Values[2])),String.valueOf(myformat1.format(ic_Values[3]))
				            		,String.valueOf(myformat1.format(ic_Values[4])),String.valueOf(myformat1.format(ic_Values[5])),String.valueOf(myformat1.format(ic_Values[6])),String.valueOf(myformat1.format(ic_Values[7])),String.valueOf(myformat1.format(ic_Values[8]))
				            		,String.valueOf(myformat1.format(ic_Values[9])),String.valueOf(myformat1.format(ic_Values[10])),String.valueOf(myformat1.format(ic_Values[11])),String.valueOf(myformat1.format(ic_Values[12])),String.valueOf(myformat1.format(ic_Values[13]))
				            		,String.valueOf(myformat1.format(ic_Values[14])),String.valueOf(myformat1.format(ic_Values[15])),String.valueOf(myformat1.format(ic_Values[16])),String.valueOf(myformat1.format(ic_Values[17])),String.valueOf(myformat1.format(ic_Values[18]))
				            		,String.valueOf(myformat1.format(ic_Values[19])),String.valueOf(myformat1.format(ic_Values[20])),String.valueOf(myformat1.format(ic_Values[21])),String.valueOf(myformat1.format(ic_Values[22])),String.valueOf(myformat1.format(ic_Values[23]))
				            		,String.valueOf(myformat1.format(ic_Values[24])),String.valueOf(myformat1.format(ic_Values[25])),String.valueOf(myformat1.format(ic_Values[26])),String.valueOf(myformat1.format(ic_Values[27])),String.valueOf(myformat1.format(ic_Values[28]))
				            		,String.valueOf(myformat1.format(ic_Values[29])),String.valueOf(myformat1.format(ic_Values[30])),String.valueOf(myformat1.format(ic_Values[31])),String.valueOf(myformat1.format(ic_Values[32])),String.valueOf(myformat1.format(ic_Values[33]))
				            		,String.valueOf(myformat1.format(ic_Values[34])),String.valueOf(myformat1.format(ic_Values[35])),String.valueOf(myformat1.format(ic_Values[36])),String.valueOf(myformat1.format(ic_Values[37])),String.valueOf(myformat1.format(ic_Values[38]))
				            		,String.valueOf(myformat1.format(ic_Values[39])),String.valueOf(myformat1.format(ic_Values[40])),String.valueOf(myformat1.format(ic_Values[41])),String.valueOf(myformat1.format(ic_Values[42])),String.valueOf(myformat1.format(ic_Values[43]))
				            		,String.valueOf(myformat1.format(ic_Values[44])),String.valueOf(myformat1.format(ic_Values[45])),String.valueOf(myformat1.format(ic_Values[46])),String.valueOf(myformat1.format(ic_Values[47])),String.valueOf(myformat1.format(ic_Values[48]))
				            		,String.valueOf(myformat1.format(ic_Values[49])),String.valueOf(myformat1.format(ic_Values[50])),String.valueOf(myformat1.format(ic_Values[51])),String.valueOf(myformat1.format(ic_Values[52])),String.valueOf(myformat1.format(ic_Values[53]))
				            		,String.valueOf(myformat1.format(ic_Values[54])),String.valueOf(myformat1.format(ic_Values[55])),String.valueOf(myformat1.format(ic_Values[56])),String.valueOf(myformat1.format(ic_Values[57])),String.valueOf(myformat1.format(ic_Values[58]))
				            		,String.valueOf(myformat1.format(ic_Values[59])),String.valueOf(myformat1.format(ic_Values[60])),String.valueOf(myformat1.format(ic_Values[61])),String.valueOf(myformat1.format(ic_Values[62])),String.valueOf(myformat1.format(ic_Values[63]))
				            		,String.valueOf(myformat1.format(ic_Values[64])),String.valueOf(myformat1.format(ic_Values[65])),String.valueOf(myformat1.format(ic_Values[66])),String.valueOf(myformat1.format(ic_Values[67])),String.valueOf(myformat1.format(ic_Values[68]))
				            		,String.valueOf(myformat1.format(ic_Values[69])),String.valueOf(myformat1.format(ic_Values[70])),String.valueOf(myformat1.format(ic_Values[71])),String.valueOf(myformat1.format(ic_Values[72])),String.valueOf(myformat1.format(ic_Values[73]))
				            		,String.valueOf(myformat1.format(ic_Values[74])),String.valueOf(myformat1.format(ic_Values[75])),String.valueOf(myformat1.format(ic_Values[76])),String.valueOf(myformat1.format(ic_Values[77])),String.valueOf(myformat1.format(ic_Values[78]))
				            		,String.valueOf(myformat1.format(ic_Values[79])),String.valueOf(myformat1.format(ic_Values[80])),String.valueOf(myformat1.format(ic_Values[81])),String.valueOf(myformat1.format(ic_Values[82])),String.valueOf(myformat1.format(ic_Values[83]))
				            		,String.valueOf(myformat1.format(ic_Values[84])),String.valueOf(myformat1.format(ic_Values[85])),String.valueOf(myformat1.format(ic_Values[86])),String.valueOf(myformat1.format(ic_Values[87])),String.valueOf(myformat1.format(ic_Values[88]))
				            		,String.valueOf(myformat1.format(ic_Values[89])),String.valueOf(myformat1.format(ic_Values[90])),String.valueOf(myformat1.format(ic_Values[91])),String.valueOf(myformat1.format(ic_Values[92])),String.valueOf(myformat1.format(ic_Values[93]))
				            		,String.valueOf(myformat1.format(ic_Values[94])),String.valueOf(myformat1.format(ic_Values[95])),String.valueOf(myformat1.format(ic_Values[96])),String.valueOf(myformat1.format(ic_Values[97])),String.valueOf(myformat1.format(ic_Values[98]))
				            		,String.valueOf(myformat1.format(ic_Values[99])),String.valueOf(myformat1.format(ic_Values[100])),String.valueOf(myformat1.format(ic_Values[101])),String.valueOf(myformat1.format(ic_Values[102])),String.valueOf(myformat1.format(ic_Values[103]))
				            		,String.valueOf(myformat1.format(ic_Values[104])),String.valueOf(myformat1.format(ic_Values[105])),String.valueOf(myformat1.format(ic_Values[106])),String.valueOf(myformat1.format(ic_Values[107])),String.valueOf(myformat1.format(ic_Values[108]))
				            		,String.valueOf(myformat1.format(ic_Values[109])),String.valueOf(myformat1.format(ic_Values[110])),String.valueOf(myformat1.format(ic_Values[111])),String.valueOf(myformat1.format(ic_Values[112])),String.valueOf(myformat1.format(ic_Values[113]))
				            		,String.valueOf(myformat1.format(ic_Values[114])),String.valueOf(myformat1.format(ic_Values[115])),String.valueOf(myformat1.format(ic_Values[116])),String.valueOf(myformat1.format(ic_Values[117])),String.valueOf(myformat1.format(ic_Values[118]))
				            		,String.valueOf(myformat1.format(ic_Values[119])),String.valueOf(myformat1.format(ic_Values[120])),String.valueOf(myformat1.format(ic_Values[121])),String.valueOf(myformat1.format(ic_Values[122])),String.valueOf(myformat1.format(ic_Values[123]))
				            		,String.valueOf(myformat1.format(ic_Values[124])),String.valueOf(myformat1.format(ic_Values[125])),String.valueOf(myformat1.format(ic_Values[126])),String.valueOf(myformat1.format(ic_Values[127])),DBID,String.valueOf(i+1)};	    
				         	break;
		         	}		
         	     try {  
       	             sqldb.execSQL(sql,ags);  
		         } catch (SQLException e) {  }
		         }
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
		 
	 public void display(){
		 //=======取得数据==============
		 int j=0;
			 for (int i = 0; i < count; i++) {
		          double angle = i * 2.8125;
		          x.get(0)[i] = i;//angle;
		          x.get(1)[i] = i;//angle;
		          x.get(2)[i] = i;//angle;
		          x_i.get(0)[i] = i;//angle;
		          x_i.get(1)[i] = i;//angle;
		          x_i.get(2)[i] = i;//angle;
	              if(j>127){j=0;}//128个数据循环使用
		          ua_Values[i] = (double)(Declare.data_bxxs[j]*600)/32768;
		          ia_Values[i] = (double)(Declare.data_bxxs[j+128]*6)/32768;
		          ub_Values[i] = (double)(Declare.data_bxxs[j+256]*600)/32768;
		          ib_Values[i] = (double)(Declare.data_bxxs[j+384]*6)/32768;
		          uc_Values[i] = (double)(Declare.data_bxxs[j+512]*600)/32768;		          		          
		          ic_Values[i] = (double)(Declare.data_bxxs[j+640]*6)/32768;
		          j++;
		        }
			 //========清空电压曲线============
			 int count_u=dataset.getSeriesCount();
	         for(int i=count_u-1;i>-1;i--){
	        	 dataset.removeSeries(i);
	         }
	     	 series.clear();
	     	//========绘制电压曲线============
	     	 for (int i = Temp_u1; i < Temp_u2; i++) {
	             series = new XYSeries(titles[i]);
	              double[] xv = x.get(i);
	              double[] yv = values.get(i);
	              int seriesLength = xv.length;
	              for (int k = 0; k < seriesLength; k++) {
	                  series.add(xv[k],yv[k]);
	              }
	              dataset.addSeries(series);    
	             // renderer.addSeriesRenderer(i,r); 
	    	 }
	      //==========电压曲线更新==================================
	     renderer.getSeriesRendererAt(0).setColor(colors[Temp_u1]);
	     mChartView.invalidate(); 
//	     	Message msg = new Message();
//	    	msg.what = 0;
//	    	mHandler_qx.sendMessage(msg);
   	 
   	 //******************电流曲线刷新**************************
   	 //==========清空电流曲线===================================
    	 int count_i=dataset_i.getSeriesCount();
        for(int i=count_i-1;i>-1;i--){
       	 dataset_i.removeSeries(i);
        }
   	 series_i.clear();
   	 //==========绘制电流曲线===================================
   	 for (int i = Temp_i1; i < Temp_i2; i++) {
            series_i = new XYSeries(titles_i[i]);
             double[] xv_i = x_i.get(i);
             double[] yv_i = values_i.get(i);
             int seriesLength = xv_i.length;
             for (int k = 0; k < seriesLength-1; k++) {
                 series_i.add(xv_i[k+1],yv_i[k]);
             }
             dataset_i.addSeries(series_i);
             //renderer_i.addSeriesRenderer(i,r_i); 
   	 }
   	 renderer_i.getSeriesRendererAt(0).setColor(colors_i[Temp_i1]);
   	 //===========电流曲线更新===================================
   	 mChartView_i.invalidate();

//	     ua_Values = null;
//	     ia_Values = null;
//	     ub_Values =null;
//	     ib_Values = null;
//	     uc_Values = null;		          		          
//	     ic_Values = null;
   	//启动循环采样后，接收超时清零
		if(Declare.rec_overtime==true){
			 for(int i=0;i<Declare.data_bxxs.length;i++){
	 			Declare.data_bxxs[i]=0;
	 		}
		}
		//检测网络是否正常，后清零
		else if(Declare.Clientisconnect==false || Declare.rec_err==true){
			if(sum1>=2){
				 for(int i=0;i<Declare.data_bxxs.length;i++){
		 			Declare.data_bxxs[i]=0;
		 		}
				sum1=0;
			}
			else{sum1++;}
		}
		else{sum1=0;}
		 //	清空波形数组数据
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
	
}
