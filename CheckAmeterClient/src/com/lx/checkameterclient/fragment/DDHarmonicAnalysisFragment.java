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

import com.lx.checkameterclient.ManageDataDetailActivity;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.fragment.CAHarmonicAnalysisFragment.ConfirmListener;
import com.lx.checkameterclient.fragment.CAHarmonicAnalysisFragment.btn_click;
import com.lx.checkameterclient.view.SegmentControl;
import com.lx.checkameterclient.view.confirm;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
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

public class DDHarmonicAnalysisFragment extends Fragment{
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
    private LinearLayout barchart;
    private ImageButton imbtn_left,imbtn_right;
    private int min=0,max=9;
    
	public static Cursor cursor=null;
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
		View view = inflater.inflate(R.layout.dd_harmonic_analysis_fragment, null);
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
        txv=(TextView)view.findViewById(R.id.txt_ver);
        text_sz=(TextView)view.findViewById(R.id.text_sz);
        barchart = (LinearLayout)view.findViewById(R.id.barchart1);
        
        imbtn_left=(ImageButton)view.findViewById(R.id.imageButton2);
		imbtn_left.setOnClickListener(new btn_click());
		imbtn_right=(ImageButton)view.findViewById(R.id.imageButton1);
		imbtn_right.setOnClickListener(new btn_click());
		
        SegmentControl segmentcontrol=(SegmentControl)view.findViewById(R.id.control_xiangmu);
		segmentcontrol.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
		    @Override 
		    public void onSegmentControlClick(int index) {
		        //处理点击标签的事件
		    	//int f=index;
		    	//int g=f;
		    	renderer.removeSeriesRenderer(r);
            	switch(index){               
                
                case 0:  
                              	
                	 //renderer.removeSeriesRenderer(r);
                     xb_sel=0;
                     r.setColor(colors[0]);
                     
//                    DisplayToast("请注意，切换到Ua谐波显示");  
                     txv.setText("Ua");
                     text_sz.setText(ua_Values[53]+"");
                 
                  break;
                  //-----------------------------------------------
                 case 1:

                	 xb_sel=1;
                     r.setColor(colors[1]);
                     txv.setText("Ub");
                     text_sz.setText(ub_Values[53]+"");
                   
                  break;
                  //-----------------------------------------------
                 case 2:  
                    
                  	 //renderer.removeSeriesRenderer(r);
                	 xb_sel=2; 
                     r.setColor(colors[2]);                       
                     txv.setText("Uc");
                     text_sz.setText(uc_Values[53]+"");
                   
                  break;
                  //-----------------------------------------------                 
                 case 3:  

                	 xb_sel=3;
                     r.setColor(colors[0]);
                     txv.setText("Ia");
                     text_sz.setText(ia_Values[53]+"");
                 
                  break;
                  //-----------------------------------------------
                 case 4:  
                     
                	 xb_sel=4; 
                     r.setColor(colors[1]);
                     txv.setText("Ib");
                     text_sz.setText(ib_Values[53]+"");
                   
                  break;
                  //-----------------------------------------------
                 case 5:  
                	 xb_sel=5;                                           
                     r.setColor(colors[2]);
                     txv.setText("Ic");
                     text_sz.setText(ic_Values[53]+"");
                   
                  break;
                }
            	 renderer.addSeriesRenderer(r); 
            	 //mChartView.invalidate();
            	 xb_data_sel(xb_sel);
                 mChartView.invalidate();
		    } 
		}); 
		count =54;
		values.clear();//一定要加入此语句。
        new DecimalFormat("0.00");
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
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(colors[3]);
        renderer.setShowGridY(true);
        r = new SimpleSeriesRenderer();
        r.setColor(colors[0]);
        renderer.addSeriesRenderer(r);
        renderer.setMargins(new int[] { 20, 40, 20, 20 });//设置图表的外边框(上/左/下/右)
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
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(true);
        renderer.setZoomButtonsVisible(true);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        renderer.setFitLegend(true);
        renderer.setClickEnabled(false);
        mChartView = ChartFactory.getBarChartView(getActivity(), dataset, renderer, Type.DEFAULT);           
        barchart.addView(mChartView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
      //==========谐波图形刷新======================
 		dis_xb(ManageDataDetailActivity.dbid);//读取库中谐波数据
 	   	xb_data_sel(xb_sel);
        mChartView.invalidate();
        
		return view;
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
	
	
	//================读取库中谐波数据====================================
		public void dis_xb(String str){
			Cursor cursor=null;
			cursor = sqldb.rawQuery("select * from xb51_data where dbid=?", new String[]{str});
			
			if(cursor.getCount()>0){
				
				cursor = sqldb.rawQuery("select * from xb51_data where dbid=? and xb_id=?", new String[]{str,"1"});
	           
				if(cursor.getCount()>0){
					cursor.moveToFirst();

					for (int i = 0; i < count; i++) {

						ua_Values[i] = Double.parseDouble(cursor.getString(i+3));
	  	         
					}
				}
//				System.out.println("UA"+String.valueOf(ua_Values[53]));
				cursor = sqldb.rawQuery("select * from xb51_data where dbid=? and xb_id=?", new String[]{str,"2"});
	            
				if(cursor.getCount()>0){
					cursor.moveToFirst();
					for (int i = 0; i < count; i++) {

						ia_Values[i] = Double.parseDouble(cursor.getString(i+3));
	      	         
					}        	        	
	        	
				}
//				System.out.println("IA"+String.valueOf(ia_Values[52]));
				cursor = sqldb.rawQuery("select * from xb51_data where dbid=? and xb_id=?", new String[]{str,"3"});
	        
				if(cursor.getCount()>0){
					cursor.moveToFirst();
					for (int i = 0; i < count; i++) {

						ub_Values[i] = Double.parseDouble(cursor.getString(i+3));
	      	         
					}        	        	
	        	
				}
//				System.out.println("UB"+String.valueOf(ub_Values[52]));
				cursor = sqldb.rawQuery("select * from xb51_data where dbid=? and xb_id=?", new String[]{str,"4"});
	        
				if(cursor.getCount()>0){
					cursor.moveToFirst();
					for (int i = 0; i < count; i++) {

						ib_Values[i] = Double.parseDouble(cursor.getString(i+3));
	      	         
					}        	        	
	        	
				}
//				System.out.println("IB"+String.valueOf(ib_Values[52]));
				cursor = sqldb.rawQuery("select * from xb51_data where dbid=? and xb_id=?", new String[]{str,"5"});
	        
				if(cursor.getCount()>0){
					cursor.moveToFirst();
					for (int i = 0; i < count; i++) {

						uc_Values[i] = Double.parseDouble(cursor.getString(i+3));
	      	         
					}        	        	
	        	
				}
//				System.out.println("UC"+String.valueOf(uc_Values[52]));
				cursor = sqldb.rawQuery("select * from xb51_data where dbid=? and xb_id=?", new String[]{str,"6"});
	        
				if(cursor.getCount()>0){
					cursor.moveToFirst();
					for (int i = 0; i < count; i++) {

						ic_Values[i] = Double.parseDouble(cursor.getString(i+3));
	      	         
					}        	        	
	        	
				}
//				System.out.println("IC"+String.valueOf(ic_Values[52]));
			}
			else
			{
				for (int i = 0; i < count; i++) {

					 ua_Values[i] = 0;
			         ub_Values[i] = 0;
			         uc_Values[i] = 0;
			         ia_Values[i] = 0;
			         ib_Values[i] = 0;
			         ic_Values[i] = 0;
	  	         
				}       	
				
			}
	        
			
		}
		
		/*
		 * ==========谐波显示根据选择装载相应数据组============================
		 */
		public void xb_data_sel(int i){
			
			dataset.removeSeries(0);
	     	series.clear(); 
			double[] v = null;
	     	
	     	switch(i){
	     	case 0:
	     		v = values.get(0);
	     		break;
	     	case 1:
	     		v = values.get(1);
	     		break;
	     	case 2:
	     		v = values.get(2);
	     		break;
	     	case 3:
	     		v = values.get(3);
	     		break;
	     	case 4:
	     		v = values.get(4);
	     		break;
	     	case 5:
	     		v = values.get(5);
	     		break;
	     		
	     	}
	     	
	     	 
	        int seriesLength = v.length;
	        for (int k = 0; k < seriesLength; k++) {
	            series.add(v[k]);
	        }
	        dataset.addSeries(series.toXYSeries());
			
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
	    public class btn_click implements OnClickListener {               
	    	@Override            
	    	public void onClick(View v) { 
	    		switch (v.getId()){
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

}
