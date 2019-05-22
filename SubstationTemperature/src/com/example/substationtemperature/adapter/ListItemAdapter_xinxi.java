package com.example.substationtemperature.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.example.substationtemperature.R;
import com.example.substationtemperature.base.AlertInfo;
import com.example.substationtemperature.base.Declare;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListItemAdapter_xinxi extends BaseAdapter{

	private Context mContext;
	private List<AlertInfo> datas; 
	
	public ListItemAdapter_xinxi(Context context,List<AlertInfo> datas){
		this.datas=datas;
		mContext=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public AlertInfo getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DecimalFormat my_float_format=new DecimalFormat("0.000");//控制浮点数显示的小数位数
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.listitem_xinxi, null);
			new ViewHolder(convertView);
		}
		if(position % 2==0){
			convertView.setBackgroundColor(Color.rgb(0,255, 255));
		}else{
			convertView.setBackgroundColor(Color.rgb(250, 250, 250));
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		String str_temp="";
		AlertInfo item = getItem(position);
		str_temp=item.getSensorName();
		holder.celiangdianTv.setText(str_temp);
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		holder.testtimeTv.setText((item.getCollectTime()));
		holder.detailTv.setText(item.getLogText());
		holder.alertValue.setText(my_float_format.format(item.getTValue()));
		return convertView;
	}
	class ViewHolder {
		TextView celiangdianTv;
		TextView testtimeTv;
		TextView alertValue;
		TextView detailTv;
		public ViewHolder(View view) {
			celiangdianTv = (TextView) view.findViewById(R.id.item_zhan);
			if(Declare.infotype==2){
				celiangdianTv.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.cetiao_blue), null, null, null);
			}else{
				celiangdianTv.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.cetiao), null, null, null);
			}
			testtimeTv = (TextView) view.findViewById(R.id.item_time);
			alertValue=(TextView)view.findViewById(R.id.txt_alert_value);
			detailTv = (TextView) view.findViewById(R.id.item_detail);
			// view.findViewById(R.id.userNameItemTv);
			view.setTag(this);
		}
	}
	

}
