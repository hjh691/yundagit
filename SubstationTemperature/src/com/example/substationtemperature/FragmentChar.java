package com.example.substationtemperature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DateTimePickDialogUtil.DateTimePickDialogUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.MeasureData;
import com.example.substationtemperature.base.ReturnData;
import com.example.substationtemperature.base.Search;
import com.example.substationtemperature.network.ClientService;
import com.example.substationtemperature.view.MultiSelectPopupWindows;

public class FragmentChar extends Fragment implements OnClickListener{
	private Activity activity;
	private View view;
	//绘制曲线用到的变量
	private TimeSeries series; 
    private XYMultipleSeriesDataset dataset;  
    private XYMultipleSeriesRenderer renderer; 
    private XYSeriesRenderer r;
    private GraphicalView mChartView;
    int count,series_count=-1;
    int u_xs=230;
    String[] titles = new String[] { "P1","P2" ,"P3"};//,"Un"
    List<Date[]> x = new ArrayList<Date[]>();
    List<double[]> values = new ArrayList<double[]>();
    private Date[] x1,x2,x3;
    private double[] ua_Values,ub_Values,uc_Values;
    PointStyle[] styles;
    private int[] colors;
    @SuppressLint("SimpleDateFormat") private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ProgressDialog pd;
	//视图控件变量
    private Spinner spinner_histraychar_zhan,spinner_histraychar_cld,
    				spinner_histraychar_cld2,spinner_histraychar_cld3;
    private TextView txt_startDatepicker,txt_endDatepicker,txt_cld;
    private Button btn_query;
    //private TextView chb_cld2,chb_cld3;
    private boolean isvisible=false;// loop=false,
    
    private String[] zhanArray={},cldArray={};//{"Z-R-01","Z-C-02","C-02-123"};
	private ArrayAdapter<String> zhanAdapter=null,cldAdapter=null;
    
    //private ListView list_data;
    //private List<Map<String,Object>>list_histraydata=new ArrayList<Map<String,Object>>();
    //private Map<String,Object>map;//=new HashMap<String,Object>();
    //private SimpleAdapter adapter=null;
    int Temp_u1=0,Temp_u2=3;
    private static Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat") private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final static String currentDateTimeStr = sdf.format(calendar.getTime());
	//private StationInfo[]  stArray;
	//private SensorsInfo ssArray[];
	private String info;
	public static Handler mHandler;
	private String fromtime="2012-09-04 09:24:10",endtime="2012-09-04 12:12:12";
	private long sensorID=0,stationid=-1,sensorid2=0,sensorid3=0;//minutes=0,
	private MeasureData[] firstArray,secondArray,thirdArray;//mdArray,
	private String s1="",s3="",s2="",e1="",e2="",e3="";
	private LinearLayout linearLayoutProductType;
    private List<Search> products;
    private MultiSelectPopupWindows productsMultiSelectPopupWindows;
	public FragmentChar(){
		
	}

	 @SuppressLint("HandlerLeak") @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_char, container, false);
        mHandler = new Handler()
    	{										
//    		  @SuppressLint("HandlerLeak")
    		public void handleMessage(Message msg)										
    		  {											
    			  super.handleMessage(msg);			
    			  switch(msg.what)
    			  {
    			  case 20:
    				  refreshspn();
    				  Toast.makeText(activity, "测量点信息获取成功",Toast.LENGTH_SHORT).show();
    				  break;
    			  case 1:
    				  if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
    				  Declare.islogin=false;
    				  info = (String) msg.obj;
                      Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
    				  break;
    			  case 10:
    				  x1=null;ua_Values=null;
    				  if((firstArray.length>0)&&(firstArray!=null)){
      					  	x1=new Date[firstArray.length];
    						ua_Values=new double[firstArray.length];
    					  	for(int j=0;j<firstArray.length;j++){
    							try {
									x1[j]=format.parse(firstArray[j].getTime());
								} catch (ParseException e) {
									e.printStackTrace();
								};
    							ua_Values[j]=((float)Math.round(firstArray[j].getValue()*1000)/1000);
    						}
    					  	//x.remove(0);
    					  	x.add(x1);
    					  	series_count++;
    					  	//values.remove(0);
    					  	values.add(ua_Values);
    				  	
    				        series = new TimeSeries(titles[0]);
    				        Date[] xv = x.get(series_count);
    				        double[] yv = values.get(series_count);
    				        int seriesLength = xv.length;
    				        for (int k = 0; k < seriesLength; k++) {
    				            series.add(xv[k],yv[k]);
    				        }
    				        dataset.addSeries(series);
    				        //mChartView.invalidate();
    				        s1=firstArray[0].getTime();
    				        e1=firstArray[firstArray.length-1].getTime();
    					}else{
    						x1=new Date[1];//x3[0]=0;
    						try {
								x1[0]=sdf.parse(fromtime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
    						x.add(x1);
    						series_count++;
    						ua_Values=new double[1];
    						ua_Values[0]=0;
    						values.add(ua_Values);
    						series = new TimeSeries(titles[0]);
    				        Date[] xv = x.get(series_count);
    				        double[] yv = values.get(series_count);
    				        int seriesLength = xv.length;
    				        for (int k = 0; k < seriesLength; k++) {
    				            series.add(xv[k],yv[k]);
    				        }
    				        dataset.addSeries(series);
    				        //mChartView.invalidate();
    				        s1=fromtime;//firstArray[0].getTime();
    				        e1=endtime;//firstArray[firstArray.length-1].getTime();
    						Toast.makeText(activity, spinner_histraychar_cld.getSelectedItem().toString()+
    								"数据为空", Toast.LENGTH_SHORT).show();
    					}
    				  //if(chb_cld2.isChecked()){
    						getdata2(sensorid2, fromtime,endtime);
    				  //}	        
    						mChartView.invalidate();
    				  //refresh();
    				  break;
    			  case 200:
    				  x2=null;ub_Values=null;
    				  if((secondArray.length>0)&&(secondArray!=null)){//(chb_cld2.isChecked())&&
    						x2=new Date[secondArray.length];
    						ub_Values=new double[secondArray.length];
    					  	for(int j=0;j<secondArray.length;j++){
    							try {
									x2[j]=format.parse(secondArray[j].getTime());
								} catch (ParseException e) {
									e.printStackTrace();
								}
    							ub_Values[j]=((float)Math.round(secondArray[j].getValue()*1000)/1000);
    						}
    					  	//x.remove(1);
    					  	x.add(x2);
    					  	series_count++;
    					  	//values.remove(1);
    					  	values.add(ub_Values);
    					  	series = new TimeSeries(titles[1]);
    					  	Date[] xv = x.get(series_count);
    					  	double[] yv = values.get(series_count);
    					  	int seriesLength = xv.length;
    				        for (int k = 0; k < seriesLength; k++) {
    				            series.add(xv[k],yv[k]);
    				        }
    				        dataset.addSeries(series);
    				        s2=secondArray[0].getTime();
    				        e2=secondArray[secondArray.length-1].getTime();
    					}else{
    						x2=new Date[1];
    						try {
								x2[0]=sdf.parse(fromtime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
    						ub_Values=new double[1];
    						ub_Values[0]=0;
    						x.add(x2);
    					  	series_count++;
    					  	//values.remove(1);
    					  	values.add(ub_Values);
    					  	series = new TimeSeries(titles[1]);
    					  	Date[] xv = x.get(series_count);
    					  	double[] yv = values.get(series_count);
    					  	int seriesLength = xv.length;
    				        for (int k = 0; k < seriesLength; k++) {
    				            series.add(xv[k],yv[k]);
    				        }
    				        dataset.addSeries(series);
    				        s2=fromtime;//secondArray[0].getTime();
    				        e2=endtime;//secondArray[secondArray.length-1].getTime();
    						Toast.makeText(activity, spinner_histraychar_cld2.getSelectedItem().toString()+
    								"数据为空", Toast.LENGTH_SHORT).show();
    					}
    				  	//if(chb_cld3.isChecked()){
    						getdata3(sensorid3, fromtime,endtime);
    					//}
    						mChartView.invalidate();
    				  //refresh();
    				  break;
    			  case 300:
    				  x3=null;uc_Values=null;
    				  if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
    				  if((thirdArray.length>0)&&(thirdArray!=null)){//(chb_cld3.isChecked())&&
    						
    					  	x3=new Date[thirdArray.length];
    						uc_Values=new double[thirdArray.length];
    					  	for(int j=0;j<thirdArray.length;j++){
    							try {
									x3[j]=format.parse(thirdArray[j].getTime());
								} catch (ParseException e) {
									e.printStackTrace();
								}
    							uc_Values[j]=((float)Math.round(thirdArray[j].getValue()*1000)/1000);
    						}
    					  	//x.remove(2);
    					  	x.add(x3);
    					  	series_count++;
    					  	//values.remove(2);
    					  	values.add(uc_Values);
    					  	series = new TimeSeries(titles[2]);
    					  	Date[] xv = x.get(series_count);
    					  	double[] yv = values.get(series_count);
    					  	int seriesLength = xv.length;
    				        for (int k = 0; k < seriesLength; k++) {
    				            series.add(xv[k],yv[k]);
    				        }
    				        dataset.addSeries(series);
    				        s3=thirdArray[0].getTime();
    				        e3=thirdArray[thirdArray.length-1].getTime();
    					}else{
    						x3=new Date[1];
    						try {
								x3[0]=sdf.parse(fromtime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
    						uc_Values=new double[1];
    						uc_Values[0]=0;
    						x.add(x3);
    					  	series_count++;
    					  	//values.remove(2);
    					  	values.add(uc_Values);
    					  	series = new TimeSeries(titles[2]);
    					  	Date[] xv = x.get(series_count);
    					  	double[] yv = values.get(series_count);
    					  	int seriesLength = xv.length;
    				        for (int k = 0; k < seriesLength; k++) {
    				            series.add(xv[k],yv[k]);
    				        }
    				        dataset.addSeries(series);
    				        s3=fromtime;//thirdArray[0].getTime();
    				        e3=endtime;//thirdArray[thirdArray.length-1].getTime();
    						Toast.makeText(activity, spinner_histraychar_cld3.getSelectedItem().toString()+
    								"数据为空", Toast.LENGTH_SHORT).show();
    					}
    				  	try {
    				  		
    				  		String[] sArray={s1,s2,s3};
    				  		timecompare tc=new timecompare(sArray);
    				  		double mintime=format.parse(tc.min()).getTime();
    				  		String[] sArray1={e1,e2,e3};
    				  		tc=new timecompare(sArray1);
    				  		double maxtime=format.parse(tc.max()).getTime();//+TimeChart.DAY;
					    
							renderer.setXAxisMax(Math.round(maxtime));
							renderer.setXAxisMin(Math.round(mintime));
							renderer.setXLabelsAngle(-45f);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
    				  mChartView.invalidate();	
    				  //refresh();
    				  break;
    			  case 11:
    				  if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
    				  info = (String) msg.obj;
                      Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
    				  break;
    			  }
    		  }
    	};
        if((Declare.islogin)&&(!Declare.isloadstations)){
        	ClientService.getstation();
        }
        if(view!=null)
        	initchar();
	    
        return view;
        
	}
	//刷新页面
	//private void refresh() {
			// TODO Auto-generated method stub	
	//}
	private static class timecompare{
		static String min_str="";
		static String max_str="";
		//static String[] s1;
		//String s3;
		static String s2;
			//timecompare(){
		//}
		timecompare(String[] s1){
			if(s1.length>0)
			{	min_str=s1[0];
				max_str=s1[0];
			}
			for(int i=1;i<s1.length;i++){
				if(min_str.compareTo(s1[i])>0){
					min_str=s1[i];
				}
				if(max_str.compareTo(s1[i])<0){
					max_str=s1[i];
				}
			}
		}
		
		public static String min(){
			return min_str;
			
		}
		public static String max(){
			return max_str;
			
		}
	}
	private void clearchart(){
		//清除曲线数据
		if(dataset!=null){
			int ser_count=dataset.getSeriesCount();
	        for(int i=ser_count-1;i>=0;i--){
	        	dataset.removeSeries(i);
	        }
		}
		series.clear();
		x.clear();values.clear();
		series_count=-1;
		mChartView.invalidate();
	}
	private void initchar() {
		// TODO Auto-generated method stub
		//========形成曲线===========
	  	count =20;
	    x.clear();values.clear();
	  	x1=null;x2=null;x3=null;ua_Values=null;ub_Values=null;uc_Values=null;
	  	x1=new Date[count];
	    x2=new Date[count];
	    x3=new Date[count];
	  	x.add(x1);
	    x.add(x2);
	    x.add(x3);
	    //x.add(new double[count]);
	    ua_Values = new double[count];
	    ub_Values = new double[count];
	    uc_Values = new double[count];
	    //double[] un_Values = new double[count];
        values.add(ua_Values);
        values.add(ub_Values);
        values.add(uc_Values);
        //values.add(un_Values);
        long value = new Date().getTime();
	    //Random rm = new Random();
        for (int i = 0; i < count; i++) {
           // double angle = i * 2.8125;
           // try {
				x.get(0)[i] = new Date(value+i*1000);
				x.get(1)[i] = new Date(value+i*1000);//format.parse("2012-08-08 01:01:00");
				x.get(2)[i] = new Date(value+i*1000);//format.parse("2012-08-08 01:01:00");
			//} catch (ParseException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
           
            //x.get(3)[i] = angle;
           // double ua_rAngle = Math.toRadians(angle);//角度转换为弧度
	           // double ub_rAngle = Math.toRadians(angle+120);//角度转换为弧度
	           // double uc_rAngle = Math.toRadians(angle+240);//角度转换为弧度
            ua_Values[i] =0;//20 +rm.nextInt() % 10;//Math.sin(ua_rAngle)*u_xs;
            ub_Values[i] =0;//20 +rm.nextInt() % 10;// Math.sin(ub_rAngle)*u_xs;
            uc_Values[i] =0;//20 +rm.nextInt() % 10;// Math.sin(uc_rAngle)*u_xs;
            //un_Values[i] = 0;
        }
	        //
        dataset = new XYMultipleSeriesDataset();
         //series = new XYSeries(titles[0]);
	          
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	        series = new TimeSeries(titles[i]);
	        Date[] xv = x.get(i);
	        double[] yv = values.get(i);
	        int seriesLength = xv.length;
	        for (int k = 0; k < seriesLength; k++) {
	            series.add(xv[k],yv[k]);
	        }
	        dataset.addSeries(series);        
	    }

	    colors = new int[] {Color.YELLOW,Color.GREEN,Color.RED,Color.BLACK};//,Color.WHITE
	    styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.CIRCLE,PointStyle.CIRCLE,};//,PointStyle.POINT
	    renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(20);
	    renderer.setLegendTextSize(18);
	    renderer.setApplyBackgroundColor(true);
	    renderer.setBackgroundColor(colors[3]);
	    renderer.setShowGridX(true);
	    
	         
	    int length1 = colors.length;
	    for (int j = 0; j < length1-1; j++) {
	      	r = new XYSeriesRenderer();
	        r.setColor(colors[j]); 
	        r.setPointStyle(styles[j]); 
	        r.setFillPoints(true); 
	        r.setLineWidth(2);  
	        renderer.addSeriesRenderer(j,r); 
	             
	    }
	    //setChartSettings(renderer, "", "", "", 0,10, 0, 30, Color.GRAY, Color.LTGRAY);
	          
	    renderer.setXLabels(6); 
	    renderer.setYLabels(6);   
	    renderer.setXLabelsAlign(Align.RIGHT);
	    renderer.setYLabelsAlign(Align.RIGHT);
	    //renderer.setPanEnabled(true, false);
	    renderer.setZoomEnabled(true);
	    renderer.setZoomButtonsVisible(true);
	    renderer.setZoomRate(2f);
	    renderer.setPanEnabled(true, true);//平移
	    //renderer.setChartTitle(title);
        //renderer.setXTitle(xTitle);
        //renderer.setYTitle(yTitle);
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(20);
        //renderer.setYAxisMin(-2);
        //renderer.setYAxisMax(60);
        //renderer.setAxesColor(axesColor);
        //renderer.setLabelsColor(labelsColor);
	    //renderer.setFitLegend(true);
	    
	    renderer.setXLabelsAngle(-45f);
	    //
	        
	    LinearLayout barchart = (LinearLayout)view.findViewById(R.id.layout_chart); 
	    mChartView = ChartFactory.getTimeChartView(activity, dataset, renderer,"yyyy-MM-dd HH:mm:ss"); 
	    //mChartView = ChartFactory.getLineChartView(activity, dataset, renderer);           
	    barchart.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    mChartView.invalidate();
	}
	private void initViews() {
		
		// TODO Auto-generated method stub
		spinner_histraychar_zhan=(Spinner)view.findViewById(R.id.spn_histraychar_zhandian);
		spinner_histraychar_zhan.setOnItemSelectedListener(new zhanSelectedListener());
		spinner_histraychar_cld=(Spinner)view.findViewById(R.id.spn_histraychar_celiangdian);
		spinner_histraychar_cld.setOnItemSelectedListener(new cldSelectedListener());
		spinner_histraychar_cld2=(Spinner)view.findViewById(R.id.spn_histraychar_celiangdian1);
		spinner_histraychar_cld2.setOnItemSelectedListener(new cld2SelectedListener());
		spinner_histraychar_cld3=(Spinner)view.findViewById(R.id.spn_histraychar_celiangdian2);
		spinner_histraychar_cld3.setOnItemSelectedListener(new cld3SelectedListener());

        if(ClientService.zhanArray!=null){
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
        }else{
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        }
        zhanAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spinner_histraychar_zhan.setPrompt("请选择站点名称");
		spinner_histraychar_zhan.setAdapter(zhanAdapter);
		if((zhanAdapter!=null)&&(isvisible))
			spinner_histraychar_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
        
		if(ClientService.cldArray!=null){
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
		}else{
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);
		}
		cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spinner_histraychar_cld.setPrompt("请选择测量点名称");
		spinner_histraychar_cld.setAdapter(cldAdapter);
		//chb_cld2=(TextView)view.findViewById(R.id.chb_cld2);
		spinner_histraychar_cld2.setPrompt("请选择测量点名称");
		spinner_histraychar_cld2.setAdapter(cldAdapter);
		if((cldAdapter!=null)&&(isvisible)){
			spinner_histraychar_cld.setSelection(cldAdapter.getPosition(Declare.celiangdian));
		}
		if((cldAdapter!=null)&&(isvisible)){
			spinner_histraychar_cld2.setSelection(cldAdapter.getPosition(Declare.celiangdian));
		}
		/*chb_cld2.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//if(ClientService.cldArray!=null){
					//	cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
					//}else{
					//	cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);
					//}
					//cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
					spinner_histraychar_cld2.setPrompt("请选择测量点名称");
					spinner_histraychar_cld2.setAdapter(cldAdapter);
					
				}
			}
			
		});*/
		//chb_cld3=(TextView)view.findViewById(R.id.chb_cld3);
		spinner_histraychar_cld3.setPrompt("请选择测量点名称");
		spinner_histraychar_cld3.setAdapter(cldAdapter);
		if((cldAdapter!=null)&&(isvisible)){
			spinner_histraychar_cld3.setSelection(cldAdapter.getPosition(Declare.celiangdian));
		}
		/*chb_cld3.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//if(ClientService.cldArray!=null){
					//	cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
					//}else{
					//	cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);
					//}
					//cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
					spinner_histraychar_cld3.setPrompt("请选择测量点名称");
					spinner_histraychar_cld3.setAdapter(cldAdapter);
					
				}
			}
			
		});*/
		txt_startDatepicker=(TextView)view.findViewById(R.id.txt_histraychar_startdate);
		txt_startDatepicker.setText(Declare.starttime);//实际运行时用Declare.starttime;测试用"2012-09-03 08:08:08"
		txt_startDatepicker.setOnClickListener(this);
		txt_endDatepicker=(TextView)view.findViewById(R.id.txt_histraychar_enddate);
		txt_endDatepicker.setOnClickListener(this);
		txt_endDatepicker.setText(Declare.endtime);//"2012-09-05 23:40:00"
		
		linearLayoutProductType=(LinearLayout)view.findViewById(R.id.lay_char_chose);
		getdata();
		txt_cld=(TextView)view.findViewById(R.id.txt_histraychar_celiangdian);
		txt_cld.setOnClickListener(this);
		btn_query=(Button)view.findViewById(R.id.histraychar_queryDataBtn);
		btn_query.setOnClickListener(this);
		
	    //displaychar();}
		/*SegmentControl segmentcontrol=(SegmentControl)view.findViewById(R.id.xiangbie);
		segmentcontrol.setSelectedIndex(Integer.valueOf(Declare.xiangbie-1));
		
		segmentcontrol.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
			
			@Override
			public void onSegmentControlClick(int index) {
				// TODO Auto-generated method stub
				int count_u=dataset.getSeriesCount();
                for(int i=count_u-1;i>-1;i--){
            	dataset.removeSeries(i);
                }
            	series.clear(); 
		    	switch(index){               
                case 0:  
                	 Temp_u1=0;Temp_u2=1; 
                	 Declare.xiangbie=1;
                  break;
                  //-----------------------------------------------
                 case 1:  
                	 Temp_u1=1;Temp_u2=2;
                	 Declare.xiangbie=2;
                  break;
                  //-----------------------------------------------
                 case 2:  
                	 Temp_u1=2;Temp_u2=3;
                	 Declare.xiangbie=3;
                  break;
                  //-----------------------------------------------                 
                 case 3:  
                	 Temp_u1=0;Temp_u2=3;
                	 Declare.xiangbie=4;
                  //DisplayToast("请注意，切换到合相曲线"); 
                    //txv.setText("Ia谐波图");
                  break;
                  //-----------------------------------------------
                }
            	 //renderer.addSeriesRenderer(r);
            	displaychar();
		    } 
		});
		initchar();
		if(Declare.xiangbie==1){
			Temp_u1=0;Temp_u2=1;
		}else if(Declare.xiangbie==2){
			Temp_u1=1;Temp_u2=2;
		}else if(Declare.xiangbie==3){
			Temp_u1=2;Temp_u2=3;
		}else if(Declare.xiangbie==4){
			Temp_u1=0;Temp_u2=3;
		}
		*/
	}
	private void refreshspn(){
		if(ClientService.zhanArray!=null){
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.zhanArray);
        }else{
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        }
		spinner_histraychar_zhan.setAdapter(zhanAdapter);
		if(zhanAdapter!=null)
			spinner_histraychar_zhan.setSelection(zhanAdapter.getPosition(Declare.zhan));
		if(ClientService.cldArray!=null){
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
		}else{
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);
		}
		spinner_histraychar_cld.setAdapter(cldAdapter);
		spinner_histraychar_cld2.setAdapter(cldAdapter);
		spinner_histraychar_cld3.setAdapter(cldAdapter);
		if(cldAdapter!=null){
			int pt=cldAdapter.getPosition(Declare.celiangdian);
			spinner_histraychar_cld.setSelection(pt);
		}
		if(cldAdapter!=null){
			spinner_histraychar_cld2.setSelection(cldAdapter.getPosition(Declare.celiangdian));
		}
		if(cldAdapter!=null){
			spinner_histraychar_cld3.setSelection(cldAdapter.getPosition(Declare.celiangdian));
		}
		
	}
	private void getdata(){
		products = new ArrayList<>();
        products.add(new Search("日用百货", false, "0"));
        products.add(new Search("电子电器", false, "1"));
        products.add(new Search("服装鞋子", false, "2"));
        products.add(new Search("生鲜水果", false, "3"));
        products.add(new Search("食品零售", false, "4"));
        products.add(new Search("手工艺品", false, "5"));
        products.add(new Search("珠宝玉石", false, "6"));
	}
	
	
	@Override
	 public void onAttach(Activity activity){
		// TODO Auto-generated method stub
			super.onAttach(activity);
			this.activity=activity;
	 }
	 
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++   
   protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> xvalues,List<double[]> yvalues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
        	XYSeries series = new XYSeries(titles[i]);
            double[] xv = xvalues.get(i);
            double[] yv = yvalues.get(i);
            int seriesLength = xv.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xv[k],yv[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors, PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
        	XYSeriesRenderer r = new XYSeriesRenderer(); 
            r.setColor(colors[i]); 
            r.setPointStyle(styles[i]); 
            r.setFillPoints(fill); 
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
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@SuppressLint("SimpleDateFormat") @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		String currentDateTimeStr = sdf.format(calendar.getTime());
		currentDateTimeStr=(txt_startDatepicker.getText().toString().substring(0,4)+"年");
		currentDateTimeStr=currentDateTimeStr+txt_startDatepicker.getText().toString().substring(5,7)+"月";
		currentDateTimeStr=currentDateTimeStr+txt_startDatepicker.getText().toString().substring(8,10)+"日";
		currentDateTimeStr=currentDateTimeStr+txt_startDatepicker.getText().toString().substring(11,16);
		
		if(v.getId()==R.id.txt_histraychar_startdate){
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(activity,
					currentDateTimeStr);
			dateTimePicKDialog.dateTimePicKDialog(txt_startDatepicker);
		}else if(v.getId()==R.id.txt_histraychar_enddate){
			currentDateTimeStr=(txt_endDatepicker.getText().toString().substring(0,4)+"年"+
					txt_endDatepicker.getText().toString().substring(5, 7)+"月"+
					txt_endDatepicker.getText().toString().substring(8,10)+"日"+
					txt_endDatepicker.getText().toString().substring(11,16));
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(activity,
					currentDateTimeStr);
			dateTimePicKDialog.dateTimePicKDialog(txt_endDatepicker);
		}
		else if(v.getId()==R.id.histraychar_queryDataBtn){
			//刷新选定地点的趋势图数据（向服务器要数据）
			if(!txt_startDatepicker.getText().toString().equals("")){
				fromtime=txt_startDatepicker.getText().toString();
			}else{
				Toast.makeText(activity, "请选择起始时间", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!txt_endDatepicker.getText().toString().equals("")){
				endtime=txt_endDatepicker.getText().toString();
			}else{
				Toast.makeText(activity, "请选择截止时间", Toast.LENGTH_SHORT).show();
				return;
			}
			Declare.starttime=txt_startDatepicker.getText().toString();
			s1=s2=s3=Declare.starttime;
			Declare.endtime=txt_endDatepicker.getText().toString();
			e1=e2=e3=Declare.endtime;
			clearchart();
			//minutes = DateTimeControl.minutesDiff(DateTimeControl.GetItemDate(fromtime), DateTimeControl.GetItemDate(endtime));
			//Toast.makeText(activity, String.valueOf(minutes), 3000).show();
			if(Declare.islogin){
				pd=ProgressDialog.show(activity, "查询中...", "正在查询",true,false);
				getdata(sensorID, fromtime,endtime);

			}
		}else if(v.getId()==R.id.txt_histraychar_celiangdian){
			productsMultiSelectPopupWindows = new MultiSelectPopupWindows(activity, linearLayoutProductType, 110, products);
	        productsMultiSelectPopupWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
	            @Override
	            public void onDismiss() {
	                products=productsMultiSelectPopupWindows.getItemList();
	                Search sd=products.get(0);
	            	Toast.makeText(activity, "我消失了，你可以做点什么。", Toast.LENGTH_SHORT).show();
	            }
	        });
		}
	}
	//站点选择项操作
	private class zhanSelectedListener implements OnItemSelectedListener{
	
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
			if(ClientService.stArray!=null){
				stationid=ClientService.stArray[position].getStationID();
			}
			if((Declare.islogin)&&(Declare.stationid!=stationid)&&(isvisible)){
				Declare.stationid=stationid;
				ClientService.getSensors(stationid);
				Declare.zhan=spinner_histraychar_zhan.getSelectedItem().toString();
			}
		}
	
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	//测量点选择项的操作内容
	private class cldSelectedListener implements OnItemSelectedListener{
	
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if(ClientService.ssArray!=null){
				sensorID=ClientService.ssArray[position].getSensorID();
			}
			if((sensorID!=Declare.sensorid)&&(isvisible)){
				Declare.sensorid=sensorID;
				Declare.celiangdian=spinner_histraychar_cld.getSelectedItem().toString();
			}
			//getdata();
		}
	
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	//测量点选择项的操作内容
		private class cld2SelectedListener implements OnItemSelectedListener{
		
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(ClientService.ssArray!=null){
					sensorid2=ClientService.ssArray[position].getSensorID();
				}
				//getdata();
			}
		
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		}
		//测量点选择项的操作内容
		private class cld3SelectedListener implements OnItemSelectedListener{
		
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(ClientService.ssArray!=null){
					sensorid3=ClientService.ssArray[position].getSensorID();
				}
				//getdata();
			}
		
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		}
		
	private void getdata(final long sensorid,final String datetime,final String endtime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
	         @Override
	         public void run() {
	        	 try {
	        	 ReturnData iis = ClientService.sendpost("/GetHistoriesBySensor?sensorId="+sensorid+"&from="+URLEncoder.encode(datetime,"UTF-8")+"&to="+URLEncoder.encode(endtime,"UTF-8"));
	        	 if(iis.mark.equals("ok")){
		        	 // 使用BufferedReader对象读取返回的数据流
				     // 按行读取，存储在StringBuider对象response中
				     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
				     StringBuilder response = new StringBuilder();
				     String line;
				     JSONObject result = null;
				     JSONArray jsArray=null;
				     while ((line = reader.readLine()) != null) {
				    	 response.append(line);
				     }	 
				     try {
				    		 result = new JSONObject( response.toString());
				     } catch (JSONException e) {
				    			// TODO 自动生成的 catch 块
				    		 e.printStackTrace();
				     }
				     String errorcode=result.getString("Error");
				     if(result.isNull("Error")){
				    	 result=result.getJSONObject("Result");
				    	 if(result.isNull("Sensors")){
				    		 firstArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 10;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
				    	 JSONObject joSensors=result.getJSONObject("Sensors");
	    		    	 if((joSensors.isNull("Stations"))||(joSensors.isNull("Sensors"))){
	    		    		 firstArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 10;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
				    	 JSONArray jaStations=joSensors.getJSONArray("Stations");
	    		    	 JSONArray jaSensors=joSensors.getJSONArray("Sensors");
	    		    	 if(result.isNull("Datas")){
	    		    		 firstArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 10;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
	    		    	 JSONObject joDatas=result.getJSONObject("Datas");
	    		    	 if(joDatas.length()!=0){
	    		    		 jsArray=joDatas.getJSONArray("Tmp");
	    		    	 }else{
	    		    		 firstArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 10;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
				    	 
				    	 firstArray=new MeasureData[jsArray.length()];
				    	 for(int i=0;i<jsArray.length();i++){
				    		 result=(JSONObject) jsArray.get(i);
				    		 MeasureData md=new MeasureData();
				    		 md.setStationID(((JSONObject) (jaStations.get(0))).getLong("Id"));
	    		    		 md.setStationName(((JSONObject) (jaStations.get(0))).getString("Name"));
	    		    		 md.setSensorId(result.getLong("SensorId"));
	    		    		 for(int j=0;j<jaSensors.length();j++){
	    		    			 if((((JSONObject) (jaSensors.get(j))).getLong("Id"))==md.getSensorId()){
	    		    				 md.setSensorsName(((JSONObject) (jaSensors.get(j))).getString("Name"));
	    		    				 break;
	    		    			 }
	    		    		 }
	    		    		 
	    		    		 md.setDataID(result.getLong("Id"));
	    		    		 if(!result.getString("Value").equals("null")){
	    		    		 md.setValue(result.getDouble("Value"));}else{md.setValue(0.0);}
	    		    		 md.setTime(result.getString("Time"));
	    		    		 md.setType(result.getString("Type"));
	    		    		 firstArray[i]=md;
	    		    		 md=null;
				    	 }
				    	 /*
				    	 if(mdArray.length>0){
				    		 if(mdArray[0].getSensorId()==sensorID){
				    			 firstArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, firstArray, 0, mdArray.length);
				    		 }else if(mdArray[0].getSensorId()==sensorid2){
				    			 secondArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, secondArray, 0, mdArray.length);
				    		 }else if(mdArray[0].getSensorId()==sensorid3){
				    			 thirdArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, thirdArray, 0, mdArray.length);
				    		 }
				    	 }*/
				    	 Message msg = new Message();
			             msg.what=10;
			             mHandler.sendMessage(msg);
				     }else{
				    	 Message msg = new Message();
			             msg.what = 1;
			             msg.obj=errorcode;
			             mHandler.sendMessage(msg);
				     }
	        	 }else if(iis.mark.equals("outtime")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接超时";
		             mHandler.sendMessage(msg);
	        	 }else if(iis.mark.equals("error")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接错误";
		             mHandler.sendMessage(msg);
	        	 }else if(iis.mark.equals("未授权")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="操作失败，权限未认证";
		             mHandler.sendMessage(msg);
	        	 }else{
	        	    	Message msg = new Message();
	                    msg.what = 1;
	                    msg.obj=iis.mark;
	                    mHandler.sendMessage(msg);
	        	 }
			  //..........
			  // 此处省略处理数据的代码
			  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
	        } catch (Exception e){
	             e.printStackTrace();
	             Message msg = new Message();
	             msg.what = 11;
	             msg.obj=spinner_histraychar_cld.getSelectedItem().toString()+"数据获取失败！";
	             mHandler.sendMessage(msg); 
	        }// finally {}
	     }
		}).start();
	}
	private void getdata2(final long sensorid,final String datetime,final String endtime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
	         @Override
	         public void run() {
	        	 try {
	        	 ReturnData iis = ClientService.sendpost("/GetHistoriesBySensor?sensorId="+sensorid+"&from="+URLEncoder.encode(datetime,"UTF-8")+"&to="+URLEncoder.encode(endtime,"UTF-8"));
	        	 if(iis.mark.equals("ok")){
		        	 // 使用BufferedReader对象读取返回的数据流
				     // 按行读取，存储在StringBuider对象response中
				     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
				     StringBuilder response = new StringBuilder();
				     String line;
				     JSONObject result = null;
				     JSONArray jsArray=null;
				     while ((line = reader.readLine()) != null) {
				    	 response.append(line);
				     }	 
				     try {
				    		 result = new JSONObject( response.toString());
				     } catch (JSONException e) {
				    			// TODO 自动生成的 catch 块
				    		 e.printStackTrace();
				     }
				     String errorcode=result.getString("Error");
				     if(result.isNull("Error")){
				    	 result=result.getJSONObject("Result");
				    	 if(result.isNull("Sensors")){
				    		 secondArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 200;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
				    	 JSONObject joSensors=result.getJSONObject("Sensors");
				    	 if((joSensors.isNull("Stations"))||(joSensors.isNull("Sensors"))){
				    		 secondArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 200;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
				    	 JSONArray jaStations=joSensors.getJSONArray("Stations");

	    		    	 JSONArray jaSensors=joSensors.getJSONArray("Sensors");
	    		    	 if(result.isNull("Datas")){
	    		    		 secondArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 200;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
	    		    	 JSONObject joDatas=result.getJSONObject("Datas");
	    		    	 if(joDatas.length()!=0){
		    		    	 jsArray=joDatas.getJSONArray("Tmp");
		    		    	 }else{
		    		    		 secondArray=new MeasureData[0];
		    		    		 Message msg = new Message();
					             msg.what = 200;
					             msg.obj="没有符合条件的数据";
					             mHandler.sendMessage(msg);
					             return;
		    		    	 }
				    	 
				    	 secondArray=new MeasureData[jsArray.length()];
				    	 for(int i=0;i<jsArray.length();i++){
				    		 result=(JSONObject) jsArray.get(i);
				    		 MeasureData md=new MeasureData();
				    		 md.setStationID(((JSONObject) (jaStations.get(0))).getLong("Id"));
	    		    		 md.setStationName(((JSONObject) (jaStations.get(0))).getString("Name"));
	    		    		 md.setSensorId(result.getLong("SensorId"));
	    		    		 for(int j=0;j<jaSensors.length();j++){
	    		    			 if((((JSONObject) (jaSensors.get(j))).getLong("Id"))==md.getSensorId()){
	    		    				 md.setSensorsName(((JSONObject) (jaSensors.get(j))).getString("Name"));
	    		    				 break;
	    		    			 }
	    		    		 }
	    		    		 
	    		    		 md.setDataID(result.getLong("Id"));
	    		    		 if(!result.getString("Value").equals("null")){
	    		    		 md.setValue(result.getDouble("Value"));}else{md.setValue(0.0);}
	    		    		 md.setTime(result.getString("Time"));
	    		    		 md.setType(result.getString("Type"));
	    		    		 secondArray[i]=md;
	    		    		 md=null;
				    	 }
				    	 /*
				    	 if(mdArray.length>0){
				    		 if(mdArray[0].getSensorId()==sensorID){
				    			 firstArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, firstArray, 0, mdArray.length);
				    		 }else if(mdArray[0].getSensorId()==sensorid2){
				    			 secondArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, secondArray, 0, mdArray.length);
				    		 }else if(mdArray[0].getSensorId()==sensorid3){
				    			 thirdArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, thirdArray, 0, mdArray.length);
				    		 }
				    	 }*/
				    	 Message msg = new Message();
			             msg.what=200;
			             mHandler.sendMessage(msg);
				     }else{
				    	 Message msg = new Message();
			             msg.what = 1;
			             msg.obj=errorcode;
			             mHandler.sendMessage(msg);
				     }
	        	 }else if(iis.mark.equals("outtime")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接超时";
		             mHandler.sendMessage(msg);
	        	 }else if(iis.mark.equals("error")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接错误";
		             mHandler.sendMessage(msg);
	        	 }else if(iis.mark.equals("未授权")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="操作失败，权限未认证";
		             mHandler.sendMessage(msg);
	        	 }else{
	        		 Message msg = new Message();
	        		 msg.what = 1;
	        		 msg.obj=iis.mark;
	        		 FragmentHistraydata.mHandler.sendMessage(msg);
        	     }
			  //..........
			  // 此处省略处理数据的代码
			  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
	        } catch (Exception e){
	        	Message msg = new Message();
	        	msg.what = 11;
	        	msg.obj=spinner_histraychar_cld2.getSelectedItem().toString()+"数据获取失败";
	        	mHandler.sendMessage(msg); 
	        	e.printStackTrace();
	        }// finally {}
	     }
		}).start();
	}
	private void getdata3(final long sensorid,final String datetime,final String endtime) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
	         @Override
	         public void run() {
	        	 try {
	        	 ReturnData iis = ClientService.sendpost("/GetHistoriesBySensor?sensorId="+sensorid+"&from="+URLEncoder.encode(datetime,"UTF-8")+"&to="+URLEncoder.encode(endtime,"UTF-8"));
	        	 if(iis.mark.equals("ok")){
		        	 // 使用BufferedReader对象读取返回的数据流
				     // 按行读取，存储在StringBuider对象response中
				     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
				     StringBuilder response = new StringBuilder();
				     String line;
				     JSONObject result = null;
				     JSONArray jsArray=null;
				     while ((line = reader.readLine()) != null) {
				    	 response.append(line);
				     }	 
				     try {
				    		 result = new JSONObject( response.toString());
				     } catch (JSONException e) {
				    			// TODO 自动生成的 catch 块
				    		 e.printStackTrace();
				     }
				     String errorcode=result.getString("Error");
				     if(result.isNull("Error")){
				    	 result=result.getJSONObject("Result");
				    	 if(result.isNull("Sensors")){
				    		 thirdArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 300;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
				    	 JSONObject joSensors=result.getJSONObject("Sensors");
				    	 if((joSensors.isNull("Stations"))||(joSensors.isNull("Sensors"))){
				    		 thirdArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 300;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
				    	 JSONArray jaStations=joSensors.getJSONArray("Stations");

	    		    	 JSONArray jaSensors=joSensors.getJSONArray("Sensors");
	    		    	 if(result.isNull("Datas")){
	    		    		 thirdArray=new MeasureData[0];
	    		    		 Message msg = new Message();
				             msg.what = 300;
				             msg.obj="没有符合条件的数据";
				             mHandler.sendMessage(msg);
				             return;
	    		    	 }
	    		    	 JSONObject joDatas=result.getJSONObject("Datas");
	    		    	 if(joDatas.length()!=0){
		    		    	 jsArray=joDatas.getJSONArray("Tmp");
		    		    	 }else{
		    		    		 thirdArray=new MeasureData[0];
		    		    		 Message msg = new Message();
					             msg.what = 300;
					             msg.obj="没有符合条件的数据";
					             mHandler.sendMessage(msg);
					             return;
		    		    	 }
				    	 thirdArray=new MeasureData[jsArray.length()];
				    	 for(int i=0;i<jsArray.length();i++){
				    		 result=(JSONObject) jsArray.get(i);
				    		 MeasureData md=new MeasureData();
				    		 md.setStationID(((JSONObject) (jaStations.get(0))).getLong("Id"));
	    		    		 md.setStationName(((JSONObject) (jaStations.get(0))).getString("Name"));
	    		    		 md.setSensorId(result.getLong("SensorId"));
	    		    		 for(int j=0;j<jaSensors.length();j++){
	    		    			 if((((JSONObject) (jaSensors.get(j))).getLong("Id"))==md.getSensorId()){
	    		    				 md.setSensorsName(((JSONObject) (jaSensors.get(j))).getString("Name"));
	    		    				 break;
	    		    			 }
	    		    		 }
	    		    		 
	    		    		 md.setDataID(result.getLong("Id"));
	    		    		 if(!result.getString("Value").equals("null")){
	    		    		 md.setValue(result.getDouble("Value"));}else{md.setValue(0.0);}
	    		    		 md.setTime(result.getString("Time"));
	    		    		 md.setType(result.getString("Type"));
	    		    		 thirdArray[i]=md;
	    		    		 md=null;
				    	 }
				    	 /*
				    	 if(mdArray.length>0){
				    		 if(mdArray[0].getSensorId()==sensorID){
				    			 firstArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, firstArray, 0, mdArray.length);
				    		 }else if(mdArray[0].getSensorId()==sensorid2){
				    			 secondArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, secondArray, 0, mdArray.length);
				    		 }else if(mdArray[0].getSensorId()==sensorid3){
				    			 thirdArray=new MeasureData[mdArray.length];
				    			 System.arraycopy(mdArray, 0, thirdArray, 0, mdArray.length);
				    		 }
				    	 }*/
				    	 Message msg = new Message();
			             msg.what=300;
			             mHandler.sendMessage(msg);
				     }else{
				    	 Message msg = new Message();
			             msg.what = 1;
			             msg.obj=errorcode;
			             mHandler.sendMessage(msg);
				     }
	        	 }else if(iis.mark.equals("outtime")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接超时";
		             mHandler.sendMessage(msg);
	        	 }else if(iis.mark.equals("error")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接错误";
		             mHandler.sendMessage(msg);
	        	 }else if(iis.mark.equals("未授权")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj=spinner_histraychar_cld3.getSelectedItem().toString()+"操作失败，权限未认证";
		             mHandler.sendMessage(msg);
	        	 }else{
	        		 Message msg = new Message();
	        		 msg.what = 1;
	        		 msg.obj=iis.mark;
	        		 FragmentHistraydata.mHandler.sendMessage(msg);
        	     }
			  //..........
			  // 此处省略处理数据的代码
			  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
	        } catch (Exception e){
	        	e.printStackTrace();
	        	Message msg = new Message();
             	msg.what = 11;
             	msg.obj=spinner_histraychar_cld3.getSelectedItem().toString()+"数据获取失败";
             	mHandler.sendMessage(msg); 
	        }// finally {}
	     }
		}).start();
	}
	//页面显示时触发
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    	isvisible=isVisibleToUser;
        if(isVisibleToUser){
    		if (view!=null){
    			initViews();//refreshspn();
    		}//else{
    			//
    		//}
    		if(!Declare.islogin){
    			FragmentIndex.comfir_display();
    		}
    	}else{
    		//if(view!=null)
    		//clearchart();
    	}
        
    }
		
		
	public class sleep_thread extends Thread{
		String str;
		@Override
    	public void run(){
	    	while(!Thread.currentThread().isInterrupted()&& 1==1)
	    	{
				try {
					sleep_thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
	        	msg.what = 101;//read_od
	        	mHandler.sendMessage(msg);
	    	}
		}
	}
	
	//private void updata_cld2(){
		
	//}
	//private void updata_cld3(){
		
	//}
	@Override
	public void onResume(){
		super.onResume();
		//初始化视图
        if(getUserVisibleHint()){
        initViews();
        }
	}
}
