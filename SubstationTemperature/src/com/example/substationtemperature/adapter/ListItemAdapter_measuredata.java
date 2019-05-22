package com.example.substationtemperature.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.example.substationtemperature.R;
import com.example.substationtemperature.R.color;
import com.example.substationtemperature.base.MeasureData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListItemAdapter_measuredata extends BaseAdapter{

	private Context mContext;
	private List<MeasureData> datas; 
	
	public ListItemAdapter_measuredata(Context context,List<MeasureData> datas){
		this.datas=datas;
		mContext=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public MeasureData getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("ResourceAsColor") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DecimalFormat my_float_format=new DecimalFormat("0.0000");//控制浮点数显示的小数位数
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.list_item, null);
			new ViewHolder(convertView);
		}
		if(position % 2==0){
			convertView.setBackgroundColor(Color.rgb(0,255, 255));
		}else{
			convertView.setBackgroundColor(Color.rgb(250, 250, 250));
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		String str_temp="";
		MeasureData item = getItem(position);
		//switch(pt){
		//case 0:
		//case 1:
		//case 2:
		//	break;
		//case 3:
		//	str_temp=item.getValue();
		//	break;
		/*case 4:
			str_temp=item.getUserAddress();
			break;
		case 5:
			str_temp=item.getLine();
			break;
		case 6:
			str_temp=item.getStation();
			break;
		case 7:
			str_temp=item.getInspector();
			break;
		case 8:
			str_temp=item.getCorector();
			break;*/
		//}
		str_temp=item.getSensorsName();
		holder.celiangdianTv.setText(str_temp);
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		holder.testtimeTv.setText(item.getTime());
		holder.valueTv.setText(my_float_format.format(item.getValue()));
		return convertView;
	}
	class ViewHolder {
		TextView celiangdianTv;
		TextView testtimeTv;
		TextView valueTv;
		public ViewHolder(View view) {
			celiangdianTv = (TextView) view.findViewById(R.id.txt_list_zhandian);
			testtimeTv = (TextView) view.findViewById(R.id.txt_list_time);
			valueTv = (TextView) view.findViewById(R.id.txt_list_value);
			// view.findViewById(R.id.userNameItemTv);
			view.setTag(this);
		}
	}
	

}
