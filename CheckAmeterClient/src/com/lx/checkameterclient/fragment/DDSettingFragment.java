package com.lx.checkameterclient.fragment;

import java.io.File;

import com.lx.checkameterclient.ManageDataDetailActivity;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.adapter.CAFragmentPagerAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class DDSettingFragment extends Fragment {

	// fragment所属的activity对象
	private Activity activity;

	private CAFragmentPagerAdapter mAdapter;
	private static EditText hmsz,bhsz,cssz,qssz,fpsz,dysz,dlsz,dzsz,xlsz,tzsz,jyy,xhy,zdy1,zdy2,zdy3,ssid,ssid_pass;
	private static EditText db_type,db_mfrs,fsdl,psdl,gsdl,zzyg,zzwg,fxwg;
	private static Spinner spinner,spinner1,spinner2,spinner3,spinner4,spinner5;
	
	static SQLiteDatabase sqldb;
    String sql;
    private File file = new File("/sdcard/bdlx/sxxy.db");// 创建文件
    public static Cursor cursor=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 用于读取fragment销毁时保存的数据，以便重建
		Bundle bundle = getArguments();

		// 加载校验数据
		initCASettingData();

		super.onCreate(savedInstanceState);
	}

	// 加载校验数据，读取通信数据、或者之前保存在bundle中的数据，将其显示在UI上
	private void initCASettingData() {
		// TODO Auto-generated method stub

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.dd_setting_fragment, null);
		Log.d("tag", "inflate  setting fragment ..............");
		
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
        sql="select * from sxxy_data where dbid=?";
        String[] ags={ManageDataDetailActivity.dbid};
        
		//数据详细界面，无需扫码二维码，所以隐藏之
		Button scanBarCodeBtn = (Button)view.findViewById(R.id.scanBarCodeBtn);
		scanBarCodeBtn.setVisibility(View.INVISIBLE);
		//=======调取参数按钮========================
        Button btn_read=(Button)view.findViewById(R.id.btn_read);
		btn_read.setVisibility(View.GONE);
		
		bhsz=(EditText)view.findViewById(R.id.ameterNumberET);
		bhsz.setEnabled(false);
		hmsz=(EditText)view.findViewById(R.id.userNameET);
		hmsz.setImeOptions(EditorInfo.IME_ACTION_DONE);
		cssz=(EditText)view.findViewById(R.id.ameterConstantValueET);
		cssz.setEnabled(false);
		qssz=(EditText)view.findViewById(R.id.coilsNumberET);
		qssz.setEnabled(false);
		fpsz=(EditText)view.findViewById(R.id.frequencyDivideRatioET);
		fpsz.setEnabled(false);
		dysz=(EditText)view.findViewById(R.id.voltRatioET);
		dysz.setEnabled(false);
		dlsz=(EditText)view.findViewById(R.id.amphereRatioET);
		dlsz.setEnabled(false);
		dzsz=(EditText)view.findViewById(R.id.userAddressET);
		dzsz.setImeOptions(EditorInfo.IME_ACTION_DONE);
		xlsz=(EditText)view.findViewById(R.id.lineET);
		xlsz.setImeOptions(EditorInfo.IME_ACTION_DONE);
		tzsz=(EditText)view.findViewById(R.id.stationET);
		tzsz.setImeOptions(EditorInfo.IME_ACTION_DONE);
		jyy=(EditText)view.findViewById(R.id.inspectorET);
		jyy.setImeOptions(EditorInfo.IME_ACTION_DONE);
		xhy=(EditText)view.findViewById(R.id.corectorET);
		xhy.setImeOptions(EditorInfo.IME_ACTION_DONE);

		//=====电表信息录入==================================
		db_type=(EditText)view.findViewById(R.id.ameterTypeET);
		db_type.setImeOptions(EditorInfo.IME_ACTION_DONE);
		db_mfrs=(EditText)view.findViewById(R.id.ameterFactoryET);
		db_mfrs.setImeOptions(EditorInfo.IME_ACTION_DONE);
		fsdl=(EditText)view.findViewById(R.id.topQuantityET);
		fsdl.setImeOptions(EditorInfo.IME_ACTION_DONE);
		psdl=(EditText)view.findViewById(R.id.normalQuantityET);
		psdl.setImeOptions(EditorInfo.IME_ACTION_DONE);
		gsdl=(EditText)view.findViewById(R.id.bottomQuantityET);
		gsdl.setImeOptions(EditorInfo.IME_ACTION_DONE);
		zzyg=(EditText)view.findViewById(R.id.positiveActivePowerET);
		zzyg.setImeOptions(EditorInfo.IME_ACTION_DONE);
		zzwg=(EditText)view.findViewById(R.id.positiveReactivePowerET);
		zzwg.setImeOptions(EditorInfo.IME_ACTION_DONE);
		fxwg=(EditText)view.findViewById(R.id.negativeReactivePowerET);
		fxwg.setImeOptions(EditorInfo.IME_ACTION_DONE);
		spinner = (Spinner) view.findViewById(R.id.ameterStandardSpinner);
		spinner.setEnabled(false);
        spinner1 = (Spinner) view.findViewById(R.id.ameterGradeSpinner);
        spinner2 = (Spinner) view.findViewById(R.id.voltRangeSpinner);
        spinner2.setEnabled(false);
        spinner3 = (Spinner) view.findViewById(R.id.amphereRangeSpinner);
        spinner.setEnabled(false);
        spinner5 = (Spinner) view.findViewById(R.id.pulseModeSpinner);
        spinner5.setEnabled(false);
        TextView txt_mcfs=(TextView)view.findViewById(R.id.txt_mcfs);
        //txt_mcfs.setVisibility(View.INVISIBLE);
        //spinner5.setVisibility(View.INVISIBLE);
		cursor=sqldb.rawQuery(sql, ags);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			bhsz.setText(cursor.getString(1));
			hmsz.setText(cursor.getString(3));
			cssz.setText(cursor.getString(14));
			qssz.setText(cursor.getString(15));
			fpsz.setText(cursor.getString(18));
			dysz.setText(cursor.getString(16));
			dlsz.setText(cursor.getString(17));
			dzsz.setText(cursor.getString(4));
			xlsz.setText(cursor.getString(5));
			tzsz.setText(cursor.getString(6));
			jyy.setText(cursor.getString(7));
			xhy.setText(cursor.getString(cursor.getColumnIndex("jhy")));
			db_type.setText(cursor.getString(cursor.getColumnIndex("db_type")));
			db_mfrs.setText(cursor.getString(cursor.getColumnIndex("db_mfrs")));
			fsdl.setText(cursor.getString(cursor.getColumnIndex("fsdl")));
			psdl.setText(cursor.getString(cursor.getColumnIndex("psdl")));
			gsdl.setText(cursor.getString(cursor.getColumnIndex("gsdl")));
			zzyg.setText(cursor.getString(cursor.getColumnIndex("zzyg")));
			zzwg.setText(cursor.getString(cursor.getColumnIndex("zzwg")));
			fxwg.setText(cursor.getString(cursor.getColumnIndex("fxwg")));
			String[] dbzs = getResources().getStringArray(R.array.ameterStandardArray);
			ArrayAdapter<String> a_adapter=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dbzs);
			spinner.setSelection(a_adapter.getPosition(cursor.getString(9)));
			
			String[] dbdj = getResources().getStringArray(R.array.ameterGradeArray);
			ArrayAdapter<String> a_adapter1=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dbdj);
			spinner1.setSelection(a_adapter1.getPosition(cursor.getString(10)));
			
			String[] dylc = getResources().getStringArray(R.array.voltRangeArray);
			ArrayAdapter<String> a_adapter2=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dylc);
			spinner2.setSelection(a_adapter2.getPosition(cursor.getString(11)));
			
			String[] dllc = getResources().getStringArray(R.array.amphereRangeArray);
			ArrayAdapter<String> a_adapter3=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,dllc);
			spinner3.setSelection(a_adapter3.getPosition(cursor.getString(12)));
			
			String[] mcfs = getResources().getStringArray(R.array.pulseModeAsrray);
			ArrayAdapter<String> a_adapter5=new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_item,mcfs);
			spinner5.setSelection(a_adapter5.getPosition(cursor.getString(13)));
			
			
		}
		return view;
	}

	// fragment对象声明周期方法，建立和activity关联关系时的回调
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	// 覆盖父类的方法，控制fragment显示和隐藏
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据

		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAdapter = null;
	}

	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	//========更新记录========================	
	public static void UpdateRecord() {  
		
		String sql="update sxxy_data set name=?,addr=?,taiz=?" +
				",xianl=?,jyy=?,jhy=?,dbdj=?,dylc=?,dllc=?,db_type=?" +
				",db_mfrs=?,fsdl=?,psdl=?,gsdl=?,zzyg=?,zzwg=?,fxwg=? where dbid=?";
		//EditText text1=(EditText)findViewById(R.id.editText1);
		//TextView text2=(TextView)findViewById(R.id.editText2);
		//EditText text3=(EditText)findViewById(R.id.editText3);
		//EditText text4=(EditText)findViewById(R.id.editText4);
		//EditText text5=(EditText)findViewById(R.id.editText5);
		//EditText text6=(EditText)findViewById(R.id.editText6);
		//EditText text7=(EditText)findViewById(R.id.editText7);
		//EditText text10=(EditText)findViewById(R.id.editText10);
		//EditText text11=(EditText)findViewById(R.id.editText11);
		//EditText text12=(EditText)findViewById(R.id.editText12);
		
		
		Object[] ags={hmsz.getText().toString(),dzsz.getText().toString()
					,tzsz.getText().toString(),xlsz.getText().toString()
					,jyy.getText().toString(),xhy.getText().toString()
					,spinner1.getSelectedItem().toString(),spinner2.getSelectedItem().toString()
					,spinner3.getSelectedItem().toString(),db_type.getText().toString()
					,db_mfrs.getText().toString(),fsdl.getText().toString()
					,psdl.getText().toString(),gsdl.getText().toString()
					,zzyg.getText().toString(),zzwg.getText().toString()
					,fxwg.getText().toString()
					,ManageDataDetailActivity.dbid};	    
			
				 
	     try {  
	           sqldb.execSQL(sql,ags);  
	         } catch (SQLException e) {  
	         }
	     
	        
	    //QueryRecord(spi1_id,spi2_id);//查询记录刷新表格   
	     //display(cursor_temp);//显示数据
	}
}
