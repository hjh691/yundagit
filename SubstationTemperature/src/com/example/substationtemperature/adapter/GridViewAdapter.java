package com.example.substationtemperature.adapter;

import com.example.substationtemperature.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
	private static final String TAG = "GridViewAdapter";   
	 private String[] names = {"实时状态","实时数据","历史数据","变化趋势","告警信息","短信日志","用户信息","退出应用"}; // "录波回放", 
	 private int[] icons = {R.drawable.wave_48x48,R.drawable.temperature_48x48,R.drawable.histray_48x48,R.drawable.quxian_48x48,
			 		R.drawable.alert_40x40,R.drawable.duanxin_40x40,R.drawable.head,R.drawable.exit_48x48};//R.drawable.liangan1,
	 private Context context;   
	 LayoutInflater infalter;
	 
	 public GridViewAdapter(Context context){
		 this.context=context;
		 infalter = LayoutInflater.from(context);
	 }

	public int getCount() {
		// TODO 自动生成的方法存根
		return names.length; 
	}

	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		Log.i(TAG,"GETVIEW "+ position);   
	     //把一个布局文件转换成视图    
	     View view = infalter.inflate(R.layout.simplegriditem, null);   
	     ImageView iv =  (ImageView) view.findViewById(R.id.ItemImage);   
	     TextView  tv = (TextView) view.findViewById(R.id.ItemText);   
	     //设置每一个item的名字和图标     
	     iv.setImageResource(icons[position]);   
	     tv.setText(names[position]);
	     tv.setTextColor(Color.BLACK);
	     return view;  
	}

}

