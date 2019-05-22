package com.lx.checkameterclient.fragment;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.lx.checkameterclient.ManageDataDetailActivity;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.view.SegmentControl;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class DDWaveviewerFragment extends Fragment{
	
	//***********波形显示*****************************
    private XYSeries series_u=null,series_i=null; 
    private XYMultipleSeriesDataset dataset_u=null,dataset_i=null;  
    private XYMultipleSeriesRenderer renderer_u=null,renderer_i=null; 
    private XYSeriesRenderer r_u=null,r_i=null;
    private GraphicalView mChartView_u=null,mChartView_i=null; 
	String[] titles_u = new String[] { "Ua曲线","Ub曲线" ,"Uc曲线"};
    String[] titles_i = new String[] { "Ia曲线","Ib曲线" ,"Ic曲线"};
    List<double[]> x_u = new ArrayList<double[]>();
    List<double[]> x_i = new ArrayList<double[]>();
    List<double[]> values_u = new ArrayList<double[]>();
    List<double[]> values_i = new ArrayList<double[]>();
    double[] bx_ua_Values = null;double[] bx_ub_Values = null;double[] bx_uc_Values = null;    
    double[] bx_ia_Values = null;double[] bx_ib_Values = null;double[] bx_ic_Values = null;
    PointStyle[] styles_u,styles_i;
    private int count_bx=0;
    private int[] colors_u,colors_i;
    double u_xs=300,i_xs=4;
    static int Temp_u1=0,Temp_u2=3,Temp_i1=0,Temp_i2=3;

    double[] ua_Values = null; double[] ub_Values = null; double[] uc_Values = null;    
    double[] ia_Values = null; double[] ib_Values = null; double[] ic_Values = null;
    boolean ui_flag=false;
    private  int sum1=0;
    private LinearLayout chart_u,chart_i;
    private SegmentControl segmentcontrol,segmentcontrol1;
    private DecimalFormat myformat1,myformat2,myformat3;
    int count =0;
    SQLiteDatabase sqldb;
	String sql;
	private File file = new File("/sdcard/bdlx/sxxy.db");// 数据库文件路径 
	public static Cursor cursor=null;
	
	private Activity activity;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity=activity;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
       
      //打开创建一个数据库        
      //  sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dd_wave_viewer_fragment, null);
		
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null); 
        
        segmentcontrol=(SegmentControl)view.findViewById(R.id.dianyaxiangbie);
		segmentcontrol.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		        //处理点击标签的事件
		    	int count_u=dataset_u.getSeriesCount();
                for(int i=count_u-1;i>-1;i--){
            	dataset_u.removeSeries(i);
                }
            	series_u.clear(); 
		    	switch(index){               
                case 0:  
                	 Temp_u1=0;Temp_u2=1; 
                  break;
                  //-----------------------------------------------
                 case 1:  
                	 Temp_u1=1;Temp_u2=2;
                  break;
                  //-----------------------------------------------
                 case 2:  
                	 Temp_u1=2;Temp_u2=3;
                  break;
                  //-----------------------------------------------                 
                 case 3:  
                	 Temp_u1=0;Temp_u2=3; 
//                    DisplayToast("请注意，切换到合相曲线"); 
                    //txv.setText("Ia谐波图");
                  break;
                  //-----------------------------------------------
                }
            	 //renderer.addSeriesRenderer(r);
            	 for (int i = Temp_u1; i < Temp_u2; i++) {
                     series_u = new XYSeries(titles_u[i]);
                      double[] xv_u = x_u.get(i);
                      double[] yv = values_u.get(i);
                      int seriesLength = xv_u.length;
                      for (int k = 0; k < seriesLength; k++) {
                          series_u.add(xv_u[k],yv[k]);
                      }
                      dataset_u.addSeries(series_u);
                     //renderer.addSeriesRenderer(i,r); 
            	 }
            	 renderer_u.getSeriesRendererAt(0).setColor(colors_u[Temp_u1]);
            	 mChartView_u.invalidate();
            	 dis_bx_data(ManageDataDetailActivity.dbid);
            	 bx_refresh();
		    } 
		}); 
        segmentcontrol1=(SegmentControl)view.findViewById(R.id.dianliuxiangbie);
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
//                    DisplayToast("请注意，切换到Ia曲线");  
                  break;
                  //-----------------------------------------------
                 case 1:  
                	 Temp_i1=1;Temp_i2=2;
//                      DisplayToast("请注意，切换到Ib曲线");  
                  break;
                  //-----------------------------------------------
                 case 2:  
                	 Temp_i1=2;Temp_i2=3;
//                     DisplayToast("请注意，切换到Ic曲线");
                  break;
                  //-----------------------------------------------                 
                 case 3:  
                	Temp_i1=0;Temp_i2=3; 
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
            	 dis_bx_data(ManageDataDetailActivity.dbid);
            	 bx_refresh();
		    } 
		});
		chart_u=(LinearLayout)view.findViewById(R.id.chart_u);
        chart_i=(LinearLayout)view.findViewById(R.id.chart_i);
		myformat1= new DecimalFormat("0.0000");
		
		segmentcontrol.setSelectedIndex(3);
		segmentcontrol1.setSelectedIndex(3);
		segmentcontrol.callOnClick();
		segmentcontrol1.callOnClick();
		Spinner spinner = (Spinner) view.findViewById(R.id.spi1);
		
	    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
	    	@Override
	    	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
	    		 switch(position){
	    		 case 0:
	    			 count=128;
	    			 //inti_bx();
	    			 chart_u.setVisibility(0);
	    			 chart_i.setVisibility(8);
	    			 renderer_u.setXLabels(4);
//	    			 barchart.invalidate();
	    			 mChartView_u.invalidate();
	    			 break;
	    		 case 1:
	    			 count=128;
	    			 //inti_bx();
	    			 chart_u.setVisibility(8);
	    			 chart_i.setVisibility(0);
	    			 renderer_i.setXLabels(4);
//	    			 barchart_i.invalidate();
	    			 mChartView_i.invalidate();
	    			 break;
	    		 case 2:
	    			 count=128;
	    			 
	    			 //he_u_rb.setChecked(true);
	    			 //he_i_rb.setChecked(true);
	    			 //inti_bx();

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
	    
	    count_bx =128;
        x_u.add(new double[count_bx]);
        x_u.add(new double[count_bx]);
        x_u.add(new double[count_bx]);
        x_i.add(new double[count_bx]);
        x_i.add(new double[count_bx]);
        x_i.add(new double[count_bx]);
        bx_ua_Values = new double[count_bx];
        bx_ub_Values = new double[count_bx];
        bx_uc_Values = new double[count_bx];
        
        bx_ia_Values = new double[count_bx];
        bx_ib_Values = new double[count_bx];
        bx_ic_Values = new double[count_bx];
        values_u.add(bx_ua_Values);
        values_u.add(bx_ub_Values);
        values_u.add(bx_uc_Values);
        values_i.add(bx_ia_Values);
        values_i.add(bx_ib_Values);
        values_i.add(bx_ic_Values);
        /*
        for (int i = 0; i < count_bx; i++) {
          double angle = i * 2.8125;
          x_u.get(0)[i] = angle;
          x_u.get(1)[i] = angle;
          x_u.get(2)[i] = angle;
          x_i.get(0)[i] = angle;
          x_i.get(1)[i] = angle;
          x_i.get(2)[i] = angle;
          double ua_rAngle = Math.toRadians(angle);//角度转换为弧度
          double ub_rAngle = Math.toRadians(angle+120);//角度转换为弧度
          double uc_rAngle = Math.toRadians(angle+240);//角度转换为弧度
          bx_ua_Values[i] = Math.sin(ua_rAngle)*u_xs;
          bx_ub_Values[i] = Math.sin(ub_rAngle)*u_xs;
          bx_uc_Values[i] = Math.sin(uc_rAngle)*u_xs;
          bx_ia_Values[i] = Math.sin(ua_rAngle)*i_xs;
          bx_ib_Values[i] = Math.sin(ub_rAngle)*i_xs;
          bx_ic_Values[i] = Math.sin(uc_rAngle)*i_xs;
        }
        
	  */
        
        dataset_u = new XYMultipleSeriesDataset();
        //series = new XYSeries(titles[0]);
       //***电压曲线
        int length_u = titles_u.length;
        for (int i = 0; i < length_u; i++) {
           series_u = new XYSeries(titles_u[i]);
            double[] xv_u = x_u.get(i);
            double[] yv_u = values_u.get(i);
            int seriesLength = xv_u.length;
            for (int k = 0; k < seriesLength; k++) {
                series_u.add(xv_u[k],yv_u[k]);
            }
            dataset_u.addSeries(series_u);
        
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
        
        
        colors_u = new int[] {Color.YELLOW,Color.GREEN,Color.RED,Color.BLACK};
        styles_u = new PointStyle[] { PointStyle.POINT, PointStyle.POINT,PointStyle.POINT,};
        renderer_u = new XYMultipleSeriesRenderer();
        renderer_u.setAxisTitleTextSize(16);
        renderer_u.setChartTitleTextSize(25);
        renderer_u.setLabelsTextSize(25);
        renderer_u.setLegendTextSize(25);
        renderer_u.setApplyBackgroundColor(true);
        renderer_u.setBackgroundColor(colors_u[3]);
        renderer_u.setShowGridX(true);
        renderer_u.setMargins(new int[] { 20, 40, 20, 20 });//设置图表的外边框(上/左/下/右)
        
        int length1 = colors_u.length;
        for (int j = 0; j < length1-1; j++) {
        	r_u = new XYSeriesRenderer();
            r_u.setColor(colors_u[j]); 
            r_u.setPointStyle(styles_u[j]); 
            r_u.setFillPoints(true); 
            r_u.setLineWidth(3);  
            renderer_u.addSeriesRenderer(j,r_u); 
            
        }
        renderer_u.setXLabels(6); 
        renderer_u.setYLabels(6);   
        renderer_u.setXLabelsAlign(Align.RIGHT);
        renderer_u.setYLabelsAlign(Align.RIGHT);
        renderer_u.setPanEnabled(true, false);
        renderer_u.setZoomEnabled(true);
        renderer_u.setZoomButtonsVisible(true);
        renderer_u.setZoomRate(1.1f);
        //renderer.setsetBarSpacing(0.5f);
        renderer_u.setFitLegend(true);
        
        //电流曲线
        colors_i = new int[] {Color.YELLOW,Color.GREEN,Color.RED,Color.BLACK};
        styles_i = new PointStyle[] { PointStyle.POINT, PointStyle.POINT,PointStyle.POINT,};
        renderer_i = new XYMultipleSeriesRenderer();
        renderer_i.setAxisTitleTextSize(16);
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
        renderer_i.setYLabels(6);   
        renderer_i.setXLabelsAlign(Align.RIGHT);
        renderer_i.setYLabelsAlign(Align.RIGHT);
        renderer_i.setPanEnabled(true, false);
        renderer_i.setZoomEnabled(true);
        renderer_i.setZoomButtonsVisible(true);
        renderer_i.setZoomRate(1.1f);
        //renderer.setsetBarSpacing(0.5f);
        renderer_i.setFitLegend(true);

        //在指定linearlayout中显示电压曲线
        LinearLayout chart_u = (LinearLayout) view.findViewById(R.id.barchart); 
        mChartView_u = ChartFactory.getLineChartView(activity, dataset_u, renderer_u);           
        chart_u.addView(mChartView_u, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); 
        //在指定linearlayout中显示电流曲线
        LinearLayout barchart_i = (LinearLayout) view.findViewById(R.id.barchart_i); 
        mChartView_i = ChartFactory.getLineChartView(activity, dataset_i, renderer_i);           
         barchart_i.addView(mChartView_i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); 
        Temp_i1=0;Temp_i2=3;
		Temp_u1=0;Temp_u2=3;
        dis_bx_data(ManageDataDetailActivity.dbid);
        bx_refresh();
		return view;
	
	}
	/*
	 * 切换波形图后初始化
	 */
	public void inti_bx(){
		 int count_i=x_u.size();
         for(int i=count_i-1;i>-1;i--){
     	    x_u.remove(i);
     	    x_i.remove(i);
     	    values_u.remove(i);
     	    values_i.remove(i);
         }
         x_u.clear();
         x_i.clear();
         values_u.clear();
         values_i.clear();
		 x_u.add(new double[count]);
         x_u.add(new double[count]);
         x_u.add(new double[count]);
         x_i.add(new double[count]);
         x_i.add(new double[count]);
         x_i.add(new double[count]);
         ua_Values = new double[count];
         ub_Values = new double[count];
         uc_Values = new double[count];
        
         ia_Values = new double[count];
         ib_Values = new double[count];
         ic_Values = new double[count];
         values_u.add(ua_Values);
         values_u.add(ub_Values);
         values_u.add(uc_Values);
         values_i.add(ia_Values);
         values_i.add(ib_Values);
         values_i.add(ic_Values);
	}
	/*
	 * ================读取数据库中波形数据====================================
	 */
	public void dis_bx_data(String str){
		Cursor cursor=null;
		cursor = sqldb.rawQuery("select * from bx128_data where dbid=?", new String[]{str});
		
		if(cursor.getCount()>0){
			
			cursor = sqldb.rawQuery("select * from bx128_data where dbid=? and bx_id=?", new String[]{str,"1"});
           
			if(cursor.getCount()>0){
				cursor.moveToFirst();

				for (int i = 0; i < count_bx; i++) {

					bx_ua_Values[i] = Double.parseDouble(cursor.getString(i+3));
  	         
				}
			}
//			System.out.println("UA"+String.valueOf(ua_Values[53]));
			cursor = sqldb.rawQuery("select * from bx128_data where dbid=? and bx_id=?", new String[]{str,"2"});
            
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				for (int i = 0; i < count_bx; i++) {

					bx_ia_Values[i] = Double.parseDouble(cursor.getString(i+3));
      	         
				}        	        	
        	
			}
//			System.out.println("IA"+String.valueOf(ia_Values[52]));
			cursor = sqldb.rawQuery("select * from bx128_data where dbid=? and bx_id=?", new String[]{str,"3"});
        
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				for (int i = 0; i < count_bx; i++) {

					bx_ub_Values[i] = Double.parseDouble(cursor.getString(i+3));
      	         
				}        	        	
        	
			}
//			System.out.println("UB"+String.valueOf(ub_Values[52]));
			cursor = sqldb.rawQuery("select * from bx128_data where dbid=? and bx_id=?", new String[]{str,"4"});
        
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				for (int i = 0; i < count_bx; i++) {

					bx_ib_Values[i] = Double.parseDouble(cursor.getString(i+3));
      	         
				}        	        	
        	
			}
//			System.out.println("IB"+String.valueOf(ib_Values[52]));
			cursor = sqldb.rawQuery("select * from bx128_data where dbid=? and bx_id=?", new String[]{str,"5"});
        
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				for (int i = 0; i < count_bx; i++) {

					bx_uc_Values[i] = Double.parseDouble(cursor.getString(i+3));
      	         
				}        	        	
        	
			}
//			System.out.println("UC"+String.valueOf(uc_Values[52]));
			cursor = sqldb.rawQuery("select * from bx128_data where dbid=? and bx_id=?", new String[]{str,"6"});
        
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				for (int i = 0; i < count_bx; i++) {

					bx_ic_Values[i] = Double.parseDouble(cursor.getString(i+3));
      	         
				}        	        	
        	
			}
//			System.out.println("IC"+String.valueOf(ic_Values[52]));
		}
		else
		{
			for (int i = 0; i < count_bx; i++) {

				 bx_ua_Values[i] = 0;
				 bx_ub_Values[i] = 0;
				 bx_uc_Values[i] = 0;
				 bx_ia_Values[i] = 0;
				 bx_ib_Values[i] = 0;
				 bx_ic_Values[i] = 0;
  	         
			}       	
			
		}
        
		
	}
	/***************************************
	 **************波形刷新显示
	 ***************************************/
	 public void bx_refresh(){
		 //=======取得数据==============
	
		 
			 for (int i = 0; i < count_bx; i++) {
		          double angle = i * 2.8125;
		          x_u.get(0)[i] = i;//angle;
		          x_u.get(1)[i] = i;//angle;
		          x_u.get(2)[i] = i;//angle;
		          x_i.get(0)[i] = i;//angle;
		          x_i.get(1)[i] = i;//angle;
		          x_i.get(2)[i] = i;//angle;

		        }
			 //========清空电压曲线============
			 int count_u=dataset_u.getSeriesCount();
	         for(int i=count_u-1;i>-1;i--){
	        	 dataset_u.removeSeries(i);
	         }
	     	 series_u.clear();
	     	//========绘制电压曲线============
	     	 for (int i = Temp_u1; i < Temp_u2; i++) {
	             series_u = new XYSeries(titles_u[i]);
	              double[] xv = x_u.get(i);
	              double[] yv = values_u.get(i);
	              int seriesLength = xv.length;
	              for (int k = 0; k < seriesLength; k++) {
	                  series_u.add(xv[k],yv[k]);
	              }
	              dataset_u.addSeries(series_u);    
	             // renderer.addSeriesRenderer(i,r); 
	             
	    	 }
	    	 
	      //==========电压曲线更新==================================
	     renderer_u.getSeriesRendererAt(0).setColor(colors_u[Temp_u1]);
	     mChartView_u.invalidate(); 
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

}
