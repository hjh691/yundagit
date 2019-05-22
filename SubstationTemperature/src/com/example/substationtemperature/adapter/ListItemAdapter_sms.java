package com.example.substationtemperature.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.substationtemperature.R;
import com.example.substationtemperature.adapter.ListItemAdapter_xinxi.ViewHolder;
import com.example.substationtemperature.base.AlertInfo;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.SmsInfo;

public class ListItemAdapter_sms extends BaseAdapter{

	private Context mContext;
	private List<SmsInfo> datas; 
	
	public ListItemAdapter_sms(Context context,List<SmsInfo> datas){
		this.datas=datas;
		mContext=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public SmsInfo getItem(int position) {
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
		SmsInfo item = getItem(position);
		str_temp=item.getTelephone();
		holder.celiangdianTv.setText(str_temp);
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		holder.testtimeTv.setText((item.getMessageTime()));
		holder.detailTv.setText(item.getMessage());
		if(item.getIsSuccess()){
			holder.issuccesTv.setText("成功");
		}else{
			holder.issuccesTv.setText("失败");
			}
		return convertView;
	}
	class ViewHolder {
		TextView celiangdianTv;
		TextView testtimeTv;
		TextView detailTv;
		TextView issuccesTv;
		public ViewHolder(View view) {
			celiangdianTv = (TextView) view.findViewById(R.id.item_zhan);
			if(Declare.infotype==2){
				celiangdianTv.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.cetiao_blue), null, null, null);
			}else{
				celiangdianTv.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.cetiao), null, null, null);
			}
			testtimeTv = (TextView) view.findViewById(R.id.item_time);
			detailTv = (TextView) view.findViewById(R.id.item_detail);
			issuccesTv=(TextView)view.findViewById(R.id.txt_alert_value);
			// view.findViewById(R.id.userNameItemTv);
			view.setTag(this);
		}
	}

}
