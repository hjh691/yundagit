package com.example.substationtemperature;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.substationtemperature.FragmentIndex.ConfirmListener;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.GraphicInfo;
import com.example.substationtemperature.base.PropertyForDrawPicture;
import com.example.substationtemperature.base.ReturnData;
import com.example.substationtemperature.floatwindow.Alertdialog;
import com.example.substationtemperature.network.ClientService;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.substationtemperature.view.CustomView;
import com.example.substationtemperature.view.confirm;

public class FragmentPicture extends Fragment{
	private static Activity activity;
	private Spinner spn_zhan;//,spn_celiangdian
	private static String[] zhanArray={};//{"Z-R-01","Z-C-02","C-02-123"}; 
	private ArrayAdapter<String> zhanAdapter;//,cldAdapter
	private View view;
	private String info;
	private boolean loop=false;
	private long graphicid=-1;//stationid=-1,
	private boolean isvisible;
	private static GraphicInfo[] graphicArray;
	private GraphicInfo graphicinfo=new GraphicInfo();
	private ImageView mImage;
	private TextView txt_zhan;
	private sleep_thread thread;
	//private Canvas canvas;
	//public static PropertyForDrawPicture[] drawsArray=new PropertyForDrawPicture[521];
	public static List<PropertyForDrawPicture> drawslist=new ArrayList<PropertyForDrawPicture>();
	private CustomView cView;
	private int temp=50;
	public static Handler mHandler;
	private ProgressDialog pd;
	
	public static Dialog dialog;
	public FragmentPicture(){
		
	}
	
	 	
		//@SuppressLint("HandlerLeak")
		
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){
		view = inflater.inflate(R.layout.fragment_picture, container, false);
		mHandler= new Handler()
		{										
			//@SuppressLint("HandlerLeak")
        	public void handleMessage(Message msg)										
    	  	{											
    		  	super.handleMessage(msg);			
    		  	switch(msg.what)
    		  	{
    		  	case Declare.STATUS_SUCCESS:
    		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
    		  		//Toast.makeText(activity, "测量点信息获取成功",3000).show();
    		  		
    		  		refresh();
    		  		break;
    		  	case Declare.STATUS_ERROR:
    		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
    		  		Declare.islogin=false;
    		  		info = (String) msg.obj;
    		  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
    		  		refresh();
    		  		break;
    		  	case 10:
    		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
    		  		info = (String) msg.obj;
    		  		refresh();
    		  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
    		  		break;
    		  	case 11:
    		  		if((pd!=null)&&(pd.isShowing())){pd.dismiss();}
    		  		info = (String) msg.obj;
    		  		Toast.makeText(activity, info,Toast.LENGTH_SHORT).show();
    		  		refresh();
    		  		break;
    		  	case Declare.STATUS_TIMEOUT:
    		  		if((Declare.islogin)&&(graphicid!=-1)){
    		  			//drawslist.clear();
    		  			//getGraphic(graphicid);
    		  			ClientService.GetRealsByDataId();
    		  		}
    		  		//refresh();//调试刷新
    		  		break;

    		  	case 80:
    		  		if(zhanArray!=null){
    		        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
    		        	graphicid=graphicArray[0].getGraphicID();
    		  		}//else{
			        //	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
			        //}
    		  		spn_zhan.setAdapter(zhanAdapter);
    		  		if((zhanAdapter!=null)){
    		  			spn_zhan.setSelection(zhanAdapter.getPosition(Declare.graphicname));
    		  			int pt=spn_zhan.getSelectedItemPosition();
    		  			if(pt>=0)
    		  				graphicid=graphicArray[pt].getGraphicID();
					}
    		  		break;
    		  	case 800:
    		  		
    		  		break;
    		  	}
    	  	}
    	};
		initview();
        
		return view;
	}
	protected void refresh() {
		// TODO Auto-generated method stub
		if(!graphicinfo.getGraphicContent().equals("")){
			byte[] byteArray=graphicinfo.getGraphicContent().getBytes();
			//Size previewSize = camera.getParameters().getPreviewSize();
			YuvImage yuvimage=new YuvImage(byteArray, ImageFormat.NV21, 700, 200, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		       yuvimage.compressToJpeg(new Rect(0, 0,700, 200), 80, baos);//80--JPG图片的质量[0-100],100最高
			byte[] jdata = baos.toByteArray();//这时候 bmp 就不为 null 了
			
			Bitmap bm=getBitmapFromByte(jdata);
			mImage.setImageBitmap(bm);
			//mImage.setImageDrawable(getResources().getDrawable(R.drawable.liulan));
		}
		//opentext("/sdcard/jka/aaa.vis");
		//Bitmap bm = null;
		//canvas = new Canvas();
		
		LinearLayout layout_test=(LinearLayout)view.findViewById(R.id.layout_test);
		/*for(int i=0;i<drawsArray.length;i++){
			if(drawsArray[i]==null){return;}
			if(drawsArray[i].getType().equals("")){break;}
			drawsArray[i].setIsClosed(!drawsArray[i].getIsClosed());
			
		}*/
		//layout_test.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		if(cView==null){
			cView=new CustomView(activity);
			
			//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1150, 800);
	        //cView.setLayoutParams(params);
			layout_test.removeAllViews();
	        layout_test.addView(cView);
		}else{
			cView=new CustomView(activity);
			//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1150, 800);
	        //cView.setLayoutParams(params);
			layout_test.removeAllViews();
	        layout_test.addView(cView);
			//cView.invalidate();
		}
		
//		CustomView cView=new CustomView(activity,drawsArray);
		//layout_test.addView(cView);
	}

    //byte[]转换成图片
	public Bitmap getBitmapFromByte(byte[] temp){  
	    if(temp != null){  
	    	BitmapFactory.Options opts =new BitmapFactory.Options();
	    	opts.inJustDecodeBounds=false;//inJustDecodeBounds 需要设置为false，如果设置为true，那么将返回null
	    	//opts.inSampleSize=size;
	    	
	    	Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length,opts);  
	        return bitmap;  
	    }else{  
	        return null;  
	    }  
	} 
	//延时定时线程
	public class sleep_thread extends Thread{
		@Override
    	public void run(){
	    	long i=0;
			while(!Thread.currentThread().isInterrupted()&& loop)
	    	{
				try {
					sleep_thread.sleep(3000);
					i++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(i % 20==0){
					Message msg = new Message();
		        	msg.what = 101;//read_od
		        	mHandler.sendMessage(msg);
	    		}
	    	}
		}
    }
	private void initview(){
		
		spn_zhan=(Spinner)view.findViewById(R.id.spn_picture_zhandian);
		spn_zhan.setSelection(0,true);
        spn_zhan.setOnItemSelectedListener(new zhanSelectedListener());
		//spn_celiangdian=(Spinner)view.findViewById(R.id.spn_picture_caijiqi);
        //spn_celiangdian.setSelection(0,true);
        //spn_celiangdian.setOnItemSelectedListener(new cldSelectedListener());
		//zhanArray[2]="110kV站5";
        if(zhanArray!=null){
        	zhanAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        	zhanAdapter.setDropDownViewResource(R.layout.item_dropdown);
        }else{
        	//zhanAdapter=null;//new ArrayAdapter<String>(activity,R.layout.item_select,zhanArray);
        }
        //zhanAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spn_zhan.setPrompt("请选择站点名称");
		spn_zhan.setAdapter(zhanAdapter);
		if((zhanAdapter!=null)&&(isvisible))
			spn_zhan.setSelection(zhanAdapter.getPosition(Declare.graphicname));
		/*if(ClientService.cldArray!=null){
			cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,ClientService.cldArray);
			cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
		}else{
			//cldAdapter=new ArrayAdapter<String>(activity,R.layout.item_select,cldArray);
		}
		//cldAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spn_celiangdian.setPrompt("请选择测量点名称");
		spn_celiangdian.setAdapter(cldAdapter);
		*/
		mImage=(ImageView)view.findViewById(R.id.ima_picture);
		txt_zhan=(TextView)view.findViewById(R.id.txt_picture_zhandian);
		/*txt_zhan.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//opentext("/sdcard/jka/aaa.vis");
				//Toast.makeText(activity,str_read, 3000).show();
				LinearLayout layout_test=(LinearLayout)view.findViewById(R.id.layout_test);
				if(cView==null){
					cView=new CustomView(activity);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1150, 800);
			        cView.setLayoutParams(params);
			        layout_test.removeAllViews();
					layout_test.addView(cView);
				}else{cView.invalidate();}
			}
		});*/
		TextView txt_cld=(TextView)view.findViewById(R.id.txt_picture_caijiqi);
		txt_cld.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//String str_read=opentext("/sdcard/jka/aaa.vis");
				//Toast.makeText(activity,str_read, 3000).show();
				//LinearLayout layout_test=(LinearLayout)view.findViewById(R.id.layout_test);
				temp=temp+50;
				Point point=new Point();point.x=400+temp;point.y=400;
				//drawsArray[0].setEndPoint(point);
				//drawsArray[0].setStrokeColor(Color.RED+temp);
				//cView=null;
				//cView=new CustomView(activity,drawsArray);
				cView.invalidate();
				//layout_test.addView(cView);
			}

			
			
		});
		}
	/*public static void saveText(String path,String text){
		try{
			FileOutputStream fos=new FileOutputStream(path);
			fos.write(text.getBytes());
			fos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	//调试绘图用
	/***
	public static String opentext(String path) {
		// TODO Auto-generated method stub
		String readStr="";//,content="";
		try{
			 //String encoding = getCharSet(path);
			File file=new File(path);
			//FileInputStream fis=(new FileInputStream(file));
			 //if (fis != null) 
             {
				 //InputStreamReader isr = 
				 InputStreamReader inputreader =new InputStreamReader(new FileInputStream(file), "GBK");
                 BufferedReader buffreader = new BufferedReader(inputreader);
                 String line;
                 
                // String pen="\",\"";
                 
                 int i=0;
                 //分行读取
                 while (( line = buffreader.readLine()) != null) {
                     //content = line + "\n";
                     //String[] temp=content.split(pen);
                     //drawsArray[i]=DecodeString(line);
                     i++;
                 }
                 PropertyForDrawPicture pfdp_temp=new PropertyForDrawPicture();
                 pfdp_temp.setType("");
                 //drawsArray[i]=pfdp_temp;
             }
			//byte[] b=new byte[fis.available()];
			//fis.read(b);
			//readStr=new String(b,"GBK");
			//fis.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		return readStr;
	}
	
	//解析绘图数据，ver=1.2；
	public static PropertyForDrawPicture DecodeString(String str){
		PropertyForDrawPicture pfdp=new PropertyForDrawPicture();
		String key="";
		int pt=str.indexOf("_type");
		if(pt>=0){
       	 key=str.substring(pt+8,str.indexOf("\"", pt+8));
       	 if(key.length()>0){pfdp.setType(key);}
        }
		if((pt=str.indexOf("StrokeColor"))>=0){
			key=str.substring(pt+14,str.indexOf("\"", pt+14));
			if(key.length()>0&&key.length()==9){
				int color=StringToColor(key);
				pfdp.setStrokeColor(color);
			}
		}
		if((pt=str.indexOf("StrokeThinkness"))>=0){
			key=str.substring(pt+17,str.indexOf(",", pt+17));
			if(key.length()>0){
				pfdp.setStrokeThinkness(Float.valueOf(key));
			}
		}
		if((pt=str.indexOf("StartPoint"))>=0){
			int j=str.indexOf(",", pt+13);
			int x=0,y=0;
			key=str.substring(pt+13,str.indexOf(",", pt+13));
			if(key.length()>0){
				x=Math.round(Float.valueOf(key));
			}
			key=str.substring(j+1,str.indexOf("\"",j));
			if(key.length()>0){
				y=Math.round(Float.valueOf(key));
			}
			Point point=new Point(x,y);
			pfdp.setStartPoint(point);
		}
		if((pt=str.indexOf("EndPoint"))>=0){
			int j=str.indexOf(",", pt+11);
			int x=0,y=0;
			key=str.substring(pt+11,str.indexOf(",", pt+11));
			if(key.length()>0){
				x=Math.round(Float.valueOf(key));
			}
			key=str.substring(j+1,str.indexOf("\"",j));
			if(key.length()>0){
				y=Math.round(Float.valueOf(key));
			}
			Point point=new Point(x,y);
			pfdp.setEndPoint(point);
		}
		if((pt=str.indexOf("ErrorColor"))>=0){
			key=str.substring(pt+13,str.indexOf("\"", pt+13));
			if(key.length()>0&&key.length()==9){
				int color=StringToColor(key);
				pfdp.setErrorColor(color);
			}
		}
		if((pt=str.indexOf("_matrix"))>=0){
			key=str.substring(pt+10,str.indexOf("\"", pt+10));
			if(key.length()>0){
				String fs[]=key.split(",");
				float[] a=new float[fs.length+3];
				for (int i=0;i<fs.length;i++){
					a[i]=Float.valueOf(fs[i]);
				}
				float temp=a[1];
				a[1]=a[2];a[2]=a[4];a[4]=a[3];a[3]=temp;
				a[fs.length]=0;a[fs.length+1]=0;a[fs.length+2]=1;
				Matrix mt=new Matrix();
				mt.setValues(a);
				pfdp.setMatrix(mt);
			}
		}
		if((pt=str.indexOf("Binding"))>=0){
			if(str.indexOf(",",pt+9)>=0)
			key=str.substring(pt+9,str.indexOf(",", pt+9));
			if(str.indexOf("}",pt+9)>=0)
				key=str.substring(pt+9,str.indexOf("}", pt+9));
			pfdp.setBinding(key);
		}
		if((pt=str.indexOf("NodeType"))>=0){
			key=str.substring(pt+11,str.indexOf("\"", pt+11));
			pfdp.setNodeType(key);
		}
		if((pt=str.indexOf("\"Size"))>=0){
			key=str.substring(pt+7,str.indexOf(",", pt+7));
			if(key.length()>0){
			pfdp.setSize(Float.valueOf(key));
			}
		}
		if((pt=str.indexOf("Text\""))>=0){
			key=str.substring(pt+7,str.indexOf("\"", pt+7));
			pfdp.setText(key);
		}
		if((pt=str.indexOf("FontFamily"))>=0){
			key=str.substring(pt+13,str.indexOf("\"", pt+13));
			pfdp.setFontFamily(key);
		}
		if((pt=str.indexOf("FontStyle"))>=0){
			key=str.substring(pt+12,str.indexOf("\"", pt+12));
			pfdp.setFontStyle(key);
		}
		if((pt=str.indexOf("FontWeight"))>=0){
			key=str.substring(pt+13,str.indexOf("\"", pt+13));
			pfdp.setFontWeight(key);
		}
		if((pt=str.indexOf("FontStretch"))>=0){
			key=str.substring(pt+14,str.indexOf("\"", pt+14));
			pfdp.setFontStretch(key);
		}
		if((pt=str.indexOf("FontSize"))>=0){
			key=str.substring(pt+10,str.indexOf(",", pt+10));
			if(key.length()>0)
			pfdp.setSize(Float.valueOf(key));
		}
		if((pt=str.indexOf("TextSpace"))>=0){
			key=str.substring(pt+11,str.indexOf(",", pt+11));
			if(key.length()>0)
			pfdp.setTextSpace(Float.valueOf(key));
		}
		if((pt=str.indexOf("Vertical"))>=0){
			key=str.substring(pt+10,str.indexOf(",", pt+10));
			pfdp.setVertical(Boolean.valueOf(key));
		}
		if((pt=str.indexOf("TitleType"))>=0){
			key=str.substring(pt+11,str.indexOf("\"", pt+11));
			pfdp.setTitleType(key);
		}
		if((pt=str.indexOf("Threephase"))>=0){
			key=str.substring(pt+12,str.indexOf("}", pt+12));
			pfdp.setIsThreephase(Boolean.valueOf(key));
		}
		if((pt=str.indexOf("Points"))>=0){
			key=str.substring(pt+10,str.indexOf("]",pt+10)-1);
			if(key.length()>0){
				//String sp="\\";
				//key=key.replaceAll("\\\"","");
				//key=key.replaceAll("\\\\", "");
				String ps[]=key.split("\",\"");
				Point[] points=new Point[ps.length];
				for (int i=0;i<ps.length;i++){
					String fs[]=ps[i].split(",");
					Point point=new Point();
					point.x=Integer.valueOf(fs[0]);
					point.y=Integer.valueOf(fs[1]);
					points[i]=point;
				}
				
				pfdp.setPoints(points);
			}
		}
		if((pt=str.indexOf("IsFill"))>=0){
			key=str.substring(pt+8,str.indexOf(",",pt+8));
			if(key.length()>0){
				pfdp.setIsFill(Boolean.valueOf(key));
			}
		}
		if((pt=str.indexOf("FillColor"))>=0){
			key=str.substring(pt+12,str.indexOf("\"",pt+12));
			if(key.length()>0&&key.length()==9){
				int color=StringToColor(key);
				pfdp.setFillColor(color);
			}
		}
		if((pt=str.indexOf("IsClosed"))>=0){
			key=str.substring(pt+10,str.indexOf("}",pt+10));
			if(key.length()>0){
				pfdp.setIsClosed(Boolean.valueOf(key));
			}
		}
		return pfdp;
	}**/
	public static int StringToColor(String str_color){
		return Color.parseColor(str_color);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity=activity;
	}
	
	private class zhanSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if(graphicArray!=null){
				graphicid=graphicArray[position].getGraphicID();
			}
			if((Declare.islogin)&&(isvisible)){//(stationid!=Declare.stationid)&&
				Declare.graphicname=spn_zhan.getSelectedItem().toString();
				drawslist.clear();
				if(pd==null){
				    pd=ProgressDialog.show(activity, "查询中...", "正在获取图形信息",true,false);
				}else{
				    pd.show();
				}
				getGraphic(graphicid);
				//Declare.stationid=stationid;
				
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	/*
	private class cldSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}*/
	
	 public void setUserVisibleHint(boolean isVisibleToUser) {
	        super.setUserVisibleHint(isVisibleToUser);
	        isvisible=isVisibleToUser;
	    	if(isVisibleToUser){
	    		if (view!=null){
	    			getGraphics();
	    			initview();
	    		}
	    		//if (thread==null){
		    		//loop=true;
		        	//thread=new sleep_thread();
		        	//thread.start();
	        	//}/**/
	    		if(!Declare.isnet){
	    			FragmentIndex.comfir1_display();

	    		}else if(!Declare.islogin){
	    			FragmentIndex.comfir_display();
	    		}
	    	}else{
	        	if(thread!=null){
	        		thread.interrupt();
	        		thread=null;
	        		loop=false;
	        		if(cView!=null){
	        			cView=null;
	        		}
	        	}
	        }
	    }
	 
	//获取指定站的图形：
    public void getGraphic(final long graphicid) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
         @Override
         public void run() {
        	 try{
        	 ReturnData iis = ClientService.sendpost("GetGraphic?graphicId="+graphicid);
        	 if(iis.mark.equals("ok")){
               	 // 使用BufferedReader对象读取返回的数据流
    		     // 按行读取，存储在StringBuider对象response中
    		     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
    		     StringBuilder response = new StringBuilder();
    		     String line;
    		     JSONObject obj = null,result=null;
    		     //JSONArray jsArray=null;
    		     while ((line = reader.readLine()) != null) {
    		         response.append(line);
    		     }
    		    	 try {
    		    		 obj = new JSONObject( response.toString());
    		    	 } catch (JSONException e) {
    		    			// TODO 自动生成的 catch 块
    		    		 e.printStackTrace();
    		    	 }
    		     String errorcode=obj.getString("Error");
    		     if(obj.isNull("Error")){
    		    	 
    		    	 result=obj.optJSONObject("Result");
    		    	 if(result!=null){
	    		    	 graphicinfo.setGraphicID(result.getLong("Id"));
	    		    	 graphicinfo.setGraphicName(result.getString("Name"));
	    		    	 String[] strContents=result.getString("Content").split("\r\n");
	    		    	 int j=0;
	    		    	 for(String strContent :strContents){
	    		    		 PropertyForDrawPicture p1=new PropertyForDrawPicture();
	    		    		 //drawsArray[j]=DecodeString(strContent);
	    		    		 //drawslist.add(DecodeString(strContent));
	    		    		 //j++;
	    		    		 JSONObject content =new JSONObject(strContent);
	    		    		 JSONObject shape=new JSONObject();
	    		    		 p1.setType(content.getString("_type"));
	    		    		 shape=content.getJSONObject("_shape");
	    		    		 if(content.has("_matrix")){
	    		    			 String key=content.getString("_matrix");
	    		    			 if(key.length()>0){
	    		    					String fs[]=key.split(",");
	    		    					float[] a=new float[fs.length+3];
	    		    					for (int i=0;i<fs.length;i++){
	    		    						a[i]=Float.valueOf(fs[i]);
	    		    					}
	    		    					float temp=a[1];
	    		    					a[1]=a[2];a[2]=a[4];a[4]=a[3];a[3]=temp;
	    		    					a[fs.length]=0;a[fs.length+1]=0;a[fs.length+2]=1;
	    		    					Matrix mt=new Matrix();
	    		    					mt.setValues(a);
	    		    					p1.setMatrix(mt);
	    		    				}
	    		    		 }
	    		    		 Iterator<?> keys=shape.keys();
	    		    		 String pName=null;
	    		    		 while(keys.hasNext()){
	    		    			 pName=keys.next().toString();
	    		    			 p1.setProperty(pName, shape.get(pName));
	    		    			 /*
	    		    			 Object pValue=content.get(pName);
	    		    			 shape.put(pName, pValue);*/
	    		    		 }
	    		    		 drawslist.add(p1);
	    		    	 }
	    		    	 Declare.isloadstations=true;
	    	             ClientService.DataId=0;
	    	             ClientService.GetRealsByDataId();
	    		    	 //saveText("/sdcard/jka/aaa.vis",graphicinfo.getGraphicContent());
	    		    	 Message msg = new Message();
	    	             msg.what = 0;
	    	             mHandler.sendMessage(msg);
	    	             
    		    	 }else{
    		    		 Message msg=new Message();
    		    		 msg.what=10;
    		    		 mHandler.sendMessage(msg);
    		    	 }
    		     }else{
    		    	 Message msg = new Message();
    	             msg.what = Declare.STATUS_ERROR;
    	             msg.obj=errorcode;
    	             mHandler.sendMessage(msg);
    		     }
           	 }else if(iis.mark.equals("outtime")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接超时";
                    mHandler.sendMessage(msg);
           	 }else if(iis.mark.equals("error")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接错误";
                    mHandler.sendMessage(msg);
           	 }else if(iis.mark.equals("未授权")){
           		 	Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="操作失败，权限未认证";
                    mHandler.sendMessage(msg);
           	 }else{
        	    	Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj=iis.mark;
                    mHandler.sendMessage(msg);
        	     }
        	  //..........
        	  // 此处省略处理数据的代码
        	  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
         	} catch (Exception e){
                //e.printStackTrace();
                Message msg = new Message();
                msg.what =11;// Declare.STATUS_ERROR;
                msg.obj="图形获取未成功！"+e.getMessage();
                mHandler.sendMessage(msg);
           }

         }
	}).start();
	}
  //获取指定站的图形的数据：
    public static void getGraphics() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
         @Override
         public void run() {
        	 try{
        	 ReturnData iis = ClientService.sendpost("GetGraphics");
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
    		     //if(errorcode.equals("null")){
    		     if(result.isNull("Error")){
    		    	 result=result.getJSONObject("Result");
    		    	 if(result.length()!=0){
    		    	 jsArray=result.getJSONArray("Graphics");}
    		    	 //if(jsArray.length()>0){
    		    		 if(jsArray.length()>0){//jsonArray_st用于调试，正常时为jsArray;
    	    		    	 graphicArray=null;zhanArray=null;
    	    		    	 graphicArray=new GraphicInfo[jsArray.length()];
    	    		    	 zhanArray=new String[jsArray.length()];
        		    		 
    	    		    	 for(int i=0;i<jsArray.length();i++){
    	    		    		 
    	    		    		 GraphicInfo gi=new GraphicInfo();
    	    		    		 result=(JSONObject) jsArray.getJSONObject(i);
    	    		    		 gi.setGraphicID(result.getInt("Id"));
    	    		    		 gi.setGraphicName(result.getString("Name"));
    	    		    		 gi.setStationid(result.getLong("StationId"));
    	    		    		 graphicArray[i]=gi;
    	    		    		 zhanArray[i]=graphicArray[i].getGraphicName();//result.getString("StationName");
    	    		    		 gi=null;
    	    		    	 }
	    		    	 
    	    		    	 Message msg = new Message();
    	    	             msg.what = 80;
    	    	             mHandler.sendMessage(msg);
        		    	 }else{
        		    		 zhanArray=null;
        		    		 graphicArray=null;
        		    		 Message msg=new Message();
        		    		 msg.what=10;
        		    		 mHandler.sendMessage(msg);
        		    	 }
    		    	 //}else{
    		    	//	 Message msg=new Message();
    		    		// msg.what=10;
    		    	//	 msg.obj="数据为空";
    		    	//	 mHandler.sendMessage(msg);
    		    	 //}
    		     }else{
    		    	 Message msg = new Message();
    	             msg.what = Declare.STATUS_ERROR;
    	             msg.obj=errorcode;
    	             mHandler.sendMessage(msg);
    		     }
           	 }else if(iis.mark.equals("outtime")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接超时";
                    mHandler.sendMessage(msg);
           	 }else if(iis.mark.equals("error")){
           		 Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="连接错误";
                    mHandler.sendMessage(msg);
           	 }else if(iis.mark.equals("未授权")){
           		 	Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj="操作失败，权限未认证";
                    mHandler.sendMessage(msg);
           	 }else{
        	    	Message msg = new Message();
                    msg.what = Declare.STATUS_ERROR;
                    msg.obj=iis.mark;
                    mHandler.sendMessage(msg);
        	     }
        	  //..........
        	  // 此处省略处理数据的代码
        	  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
         	} catch (Exception e){
                //e.printStackTrace();
                Message msg = new Message();
                msg.what =11;// Declare.STATUS_ERROR;
                msg.obj=e.getMessage();
                mHandler.sendMessage(msg);
           }

         }
	}).start();
	}
    
  //======消息框提示===========
  	public static void alert_display(String stationname,String sensorname,String time,String value,String content) {	
  		if(dialog==null){
  			dialog = new Alertdialog(activity,R.style.AlertDialog);       	        	       	
  	        dialog.show(); 
  	        TextView text_name=(TextView)dialog.findViewById(R.id.txt_stationname); 
  	        text_name.setText(stationname);
  	        TextView text_type=(TextView)dialog.findViewById(R.id.txt_sensorname); 
  	        text_type.setText(sensorname);
  	        TextView text_time=(TextView)dialog.findViewById(R.id.txt_alt_time); 
	        text_time.setText(time);
	        TextView text_value=(TextView)dialog.findViewById(R.id.txt_alt_value); 
  	        text_value.setText(value);
  	        TextView text_info=(TextView)dialog.findViewById(R.id.txt_alt_content); 
	        text_info.setText(content);
  	        //Button but_qr = (Button)dialog.findViewById(R.id.but_qr);   
  	        //but_qr.setOnClickListener(new ConfirmListener());
  	        Button but_q = (Button)dialog.findViewById(R.id.btn_close);   
  	        but_q.setOnClickListener(new ConfirmListener()); 
  		}else{dialog.show();}
  		new Thread(){ public void run(){ try {
  			sleep(2000);
  			if(dialog!=null)
  			{dialog.cancel();
  			dialog=null;}
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  		//	e.printStackTrace();
  		} } }.start(); 
  	}
    
  //========消息确认界面按钮响应==================================
  	public static class ConfirmListener implements  OnClickListener{
  		@Override
  		public void onClick(View v) {
  			// TODO Auto-generated method stub
  			switch (v.getId()) {
  		    	case R.id.but_qr:		    				    			 
  		    		//Intent intent=new Intent(activity,Login.class);
  					//activity.startActivityForResult(intent,1);
  					//activity.overridePendingTransition(R.anim.fade, R.anim.hold);
  		    	    break;
  		    	case R.id.btn_close:
  		    		
  		    		break;
  			}
  			dialog.cancel();
      		dialog=null;
  	    }
  	} 
}
