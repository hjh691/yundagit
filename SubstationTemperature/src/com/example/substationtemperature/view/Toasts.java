package com.example.substationtemperature.view;
//Download by http://www.codefans.net

//import com.bdlx.Elec_Check.R;


import com.example.substationtemperature.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Toasts {
	private static View view;
	public static Toast toast;
	public static Toast toast1;
	public static void toast(Context context,String content,int duration)//,int gravity,int xoffset,int yoffset
	{
		view=LayoutInflater.from(context).inflate(R.layout.toast_item, null);
		TextView textView=(TextView) view.findViewById(R.id.toast_text);
		textView.setText(content);
		if(toast!=null){
			toast.cancel();
		}
		toast=new Toast(context);
		toast.setDuration(duration);
//		toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
		toast.setGravity(Gravity.CENTER_VERTICAL,0,0);//, xoffset, yoffset);//提示框的位置参数和对其参数
		toast.setView(view);
		toast.show();
	}
	/*
	public static void toast(Context context,int id,String content)
	{
		view=LayoutInflater.from(context).inflate(R.layout.toast_item2, null);
		TextView textView=(TextView) view.findViewById(R.id.toast_text);
		ImageView imageView=(ImageView) view.findViewById(R.id.toast_img);
		Log.i("hck", "ididi "+id);
		imageView.setBackgroundResource(id);
		textView.setText(content);
		Toast toast=new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, Gravity.CENTER, Gravity.CENTER);
		toast.setView(view);
		toast.show();
	}
	*/
	public static void toast(Context context,String content)
	{
		view=LayoutInflater.from(context).inflate(R.layout.toast_item2, null);
		TextView textView=(TextView) view.findViewById(R.id.toast_text);
		
		if(toast1!=null){
			toast1.cancel();
		}
		
		textView.setText(content);
		toast1=new Toast(context);
		toast1.setDuration(Toast.LENGTH_SHORT);
		toast1.setGravity(Gravity.TOP, 0, 220);
		toast1.setView(view);
		toast1.show();
		
	}	
	public static void toast1(Context context,String content)
	{
		view=LayoutInflater.from(context).inflate(R.layout.toast_item3, null);
		TextView textView=(TextView) view.findViewById(R.id.toast_text);
		
		if(toast1!=null){
			toast1.cancel();
		}
		
		textView.setText(content);
		toast1=new Toast(context);
		toast1.setDuration(Toast.LENGTH_SHORT);
		toast1.setGravity(Gravity.TOP, 0, 220);
		toast1.setView(view);
		toast1.show();
		
	}	
	
	public static void toast1Cancle()
	{
		
		if(toast1!=null){
			toast1.cancel();
		}


	}

}
