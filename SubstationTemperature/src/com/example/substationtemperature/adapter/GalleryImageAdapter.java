package com.example.substationtemperature.adapter;

import com.example.substationtemperature.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter {
	//定义context 即Activity
	private Context context;
	//定义整形数字，即图像源
	private Integer image[]={R.drawable.liulan,R.drawable.liulan2,R.drawable.liulan3,R.drawable.liulan4,R.drawable.liulan5 };
	
	public GalleryImageAdapter(Context c) throws IllegalArgumentException,IllegalAccessException{
		context=c;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return image.length;
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageview=new ImageView(context);
		//给imgaeview 设置图像资源
		//imageview.setImageResource(image[position]);
		imageview.setImageResource(image[position%image.length]);//图片循环切换postiong与数组长度取模，与getcount的返回值配合使用。
		//设置比例类型
		imageview.setScaleType(ImageView.ScaleType.FIT_XY);
		//设置图像布局和显示大小
		imageview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		imageview.setPadding(15,0, 15, 0);
		return imageview;
	}

}
