package com.lx.checkameterclient.fragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.lx.checkameterclient.ManageDataDetailActivity;
import com.lx.checkameterclient.R;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DDErrorTestingFragment extends Fragment {
	
	static TextView qsxs,wcxs,wc1,wc2,wc3,wc4,wc5,wcpj,text_ztxs;
	String ygwc1,ygwc2,ygwc3,ygwc4,ygwc5,wgwc1,wgwc2,wgwc3,wgwc4,wgwc5;
	boolean wc_select_flag=false;
	private DecimalFormat myformat1,myformat2;
	
	private EditText cssz,qssz,dysz,dlsz,fpsz;
	private TextView spinner1,spinner2,spinner5;
	
	SQLiteDatabase sqldb;
    String sql;
    private File file = new File("/sdcard/bdlx/sxxy.db");// 创建文件
    public static Cursor cursor=null;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		myformat2= new DecimalFormat("0.000");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dd_error_testing_fragment, null);
		LinearLayout lay=(LinearLayout)view.findViewById(R.id.lay_wc1);
		lay.setVisibility(View.GONE);
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
		Spinner spinner3 = (Spinner) view.findViewById(R.id.spi_wclx);
		spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
         	@Override
         	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
         		//myformat1 = new DecimalFormat("0.0000");
                if(position==0){
                	wc_select_flag=false;
                	wc1.setText(ygwc1);
                	wc2.setText(ygwc2);
                	wc3.setText(ygwc3);
                	wc4.setText(ygwc4);
                	wc5.setText(ygwc5);
                }
                else{
                	wc_select_flag=true;
                	wc1.setText(wgwc1);
                	wc2.setText(wgwc2);
        	 		wc3.setText(wgwc3);
        	 		wc4.setText(wgwc4);
        	 		wc5.setText(wgwc5);
                }
                try {
    	 			wcpj.setText(myformat2.format((Float.parseFloat(wc1.getText().toString())+
    	 				      Float.parseFloat(wc2.getText().toString())+Float.parseFloat(wc3.getText().toString())+
    	 				      Float.parseFloat(wc4.getText().toString()))/4)); 
    		 		} catch (Exception e) {
    	  	   // TODO Auto-generated catch block
    		 			e.printStackTrace();
    		 			wcpj.setText("");
    		 		}
         	}
         	@Override
         	public void onNothingSelected(AdapterView<?> view) {
  //       		Log.i(TAG,  view.getClass().getName());
         	}
         });
		display();
		return view;
	}
	public void display(){
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
        sql="select * from sxxy_data where dbid=?";
        String[] ags={ManageDataDetailActivity.dbid};
        cursor=sqldb.rawQuery(sql, ags);
        if(cursor.getCount()>0){
			cursor.moveToFirst();
			/*if(cursor.getString(9).equals("三相三线有功") || cursor.getString(9).equals("三相四线有功")){
				wc_select_flag=false;
			}
			if(cursor.getString(9).equals("三相三线无功") || cursor.getString(9).equals("三相四线无功")){
				wc_select_flag=true;
			}*/
			cssz.setText(cursor.getString(14));
			qssz.setText(cursor.getString(15));
			dysz.setText(cursor.getString(16));
			dlsz.setText(cursor.getString(17));
			fpsz.setText(cursor.getString(18));
			
			spinner1.setText(cursor.getString(11));
			spinner2.setText(cursor.getString(12));
			spinner5.setText(cursor.getString(13));
			
			ygwc1=cursor.getString(45);
			ygwc2=cursor.getString(46);
			ygwc3=cursor.getString(47);
			ygwc4=cursor.getString(48);
			ygwc5=cursor.getString(49);
			wgwc1=cursor.getString(74);
			wgwc2=cursor.getString(75);
			wgwc3=cursor.getString(76);
			wgwc4=cursor.getString(77);
			wgwc5=cursor.getString(78);
	 		if(wc_select_flag==false){
	 			wc1.setText(ygwc1);
		 		wc2.setText(ygwc2);
		 		wc3.setText(ygwc3);
		 		wc4.setText(ygwc4);
		 		wc5.setText(ygwc5);
	 		}
	 		else{
	 			wc1.setText(wgwc1);
		 		wc2.setText(wgwc2);
		 		wc3.setText(wgwc3);
		 		wc4.setText(wgwc4);
		 		wc5.setText(wgwc5);
	 		}
	 		try {
	 			wcpj.setText(myformat2.format((Float.parseFloat(wc1.getText().toString())+
	 				      Float.parseFloat(wc2.getText().toString())+Float.parseFloat(wc3.getText().toString())+
	 				      Float.parseFloat(wc4.getText().toString()))/4)); 
		 		} catch (Exception e) {
	  	   // TODO Auto-generated catch block
		 			e.printStackTrace();
		 			wcpj.setText("");
		 		}
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
	/**
	* 
	* 由街道信息转换为经纬度
	* @param address 街道信息
	* @return 包含经纬度的一个double 数组,{longtitude,latitude}
	*/
	public static double[] getLocationInfoByGoogle(String address){
	//定义一个HttpClient，用于向指定地址发送请求
	HttpClient client = new DefaultHttpClient();
	//向指定地址发送Get请求
	HttpGet hhtpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address="+address+"ka&sensor=false");
	StringBuilder sb = new StringBuilder();
	try {
	//获取服务器响应
	HttpResponse response = client.execute(hhtpGet);
	HttpEntity entity = response.getEntity();
	//获取服务器响应的输入流
	InputStream stream = entity.getContent();
	int b;
	//循环读取服务器响应
	while((b = stream.read()) != -1){
		sb.append((char)b);
	}
	//将服务器返回的字符串转换为JSONObject  对象
	JSONObject jsonObject = new JSONObject(sb.toString());
	//从JSONObject 中取出location 属性
	JSONObject location = jsonObject.getJSONObject("results").getJSONObject("0").getJSONObject("geometry").getJSONObject("location");
	//获取经度信息
	double longtitude = location.getDouble("lng");
	double latitude = location.getDouble("lat");
	return new double[]{longtitude,latitude};
	} catch (ClientProtocolException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	} catch (JSONException e) { 
	e.printStackTrace();
	}
	return null;
	}
}
