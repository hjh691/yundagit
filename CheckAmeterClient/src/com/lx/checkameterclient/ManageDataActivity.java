package com.lx.checkameterclient;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.lx.checkameter.base.BaseActivity;
import com.lx.checkameter.socket.Toasts;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import DateTimePickDialogUtil.DateTimeControl;

import com.lx.checkameterclient.bean.AmeterData;

import DateTimePickDialogUtil.DateTimePickDialogUtil;

public class ManageDataActivity extends BaseActivity {

	// private ListView mListView;
	private MyAdapter mAdapter;
	private SwipeMenuListView mListView;

	private List<AmeterData> datas;
	
	SQLiteDatabase sqldb;
    String sql;
    private File file = new File("/sdcard/bdlx/sxxy.db");// 创建文件
    private int comp_pt=1;
    private int pt=1;
    
    int spi1_id,spi2_id;
    Button but_que,but_more;
    private String time1,time2;
    private Cursor cursor;
    private Boolean first=true;
    private TextView mtxt_xm;
    
    
    private  TextView startDatePickerTV,endDatePickerTV;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_manage_data);
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
        sql="select * from sxxy_data";
		// 设置当前页面标题
		TextView titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText("数据管理");
		mtxt_xm=(TextView)findViewById(R.id.txt_xm);
		// 初始化UI
		initViews();
		loadData();
		mListView = (SwipeMenuListView) findViewById(R.id.listView);
		// loadAmeterData();
		// 创建自定义适配器对象，这里MyAdapter是一个内部类
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new ItemClickListener());
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.delete_icon);
				// add to menu
				menu.addMenuItem(deleteItem);
				// 添加打开按钮，这里不需要，因为查看详细的操作由单击实现
				// // create "open" item
				// SwipeMenuItem openItem = new
				// SwipeMenuItem(getApplicationContext());
				// // set item background
				// openItem.setBackground(new ColorDrawable(Color.rgb(0xC9,
				// 0xC9, 0xCE)));
				// // set item width
				// openItem.setWidth(dp2px(90));
				// // set item title
				// openItem.setTitle("Open");
				// // set item title fontsize
				// openItem.setTitleSize(18);
				// // set item title font color
				// openItem.setTitleColor(Color.WHITE);
				// // add to menu
				// menu.addMenuItem(openItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);
		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				AmeterData item = datas.get(position);
				switch (index) {
				case 0: // delete
					Toast.makeText(ManageDataActivity.this, "delete", Toast.LENGTH_SHORT).show();
					mAdapter.notifyDataSetChanged();
					String str_del="delete from sxxy_data where dbid=?";//item.getAmeterNo();
					String[] ags={};
					ags=new String[]{item.getAmeterNo()};
					sqldb.execSQL(str_del,ags);
					str_del="delete from xb51_data where dbid=?";
					sqldb.execSQL(str_del,ags);
					str_del="delete from bx128_data where dbid=?";
					sqldb.execSQL(str_del,ags);
					datas.remove(position);
					break;
				case 1: // open
					Intent intent=new Intent(ManageDataActivity.this, ManageDataDetailActivity.class);
					Bundle bd=new Bundle();
					bd.putString("dbid", item.getAmeterNo());
					intent.putExtra("ameterno", bd);
					startActivity(intent);
					// activity切换过场动画
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					break;
				}
			}
		});
		// set SwipeListener
		mListView.setOnSwipeListener(new OnSwipeListener() {
			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener#
			 * onSwipeEnd(int) 当滑动结束时判断是开启状态还是关闭状态, 如果开启则关闭BaseActivity中的手势操作
			 * 如果关闭，则允许BaseActivity中的手势操作
			 * 这样解决了侧滑返回MainActivity页面的操作和SwipeMenuList中列表项侧滑的冲突
			 */
			@Override
			public void onSwipeEnd(int position) {
				// 如果滑动已被打开
				if (mListView.isTouchViewOpen() == true) {
					setNeedBackGesture(false);
					// Log.d("tag", "关闭");
				}
				// swipe end
				if (mListView.isTouchViewOpen() == false) {
					setNeedBackGesture(true);
					// Timer timer = new Timer();
					// timer.schedule(new TimerTask(){
					//
					// public void run() {
					// setNeedBackGesture(true);
					// Log.d("tag", "开启");
					// }}, 1000);
				}
			}
		});
		// other setting
		// listView.setCloseInterpolator(new BounceInterpolator());
		// test item long click
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), position + " long click", 0).show();
				return false;
			}
		});
	}
	private void initViews() {
		// final LinearLayout datafilterLayout = (LinearLayout)
		// findViewById(R.id.filterDateLayout);
		Spinner spinner1 = (Spinner) findViewById(R.id.spi1);
		//为适配器设置下拉列表下拉时的菜单样式。
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
         	@Override
         	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
         		spi1_id=position;
         		switch(position){
         		default :
         			comp_pt=1;
         			break;
         		case 1:
         		case 3:
         		case 4:
         		case 5:
         		case 6:
         		case 7:
         		case 8:
         			comp_pt=position;
         			break;
         		}
         		//loadData();
         		//mAdapter.notifyDataSetChanged();
         	}
         	@Override
         	public void onNothingSelected(AdapterView<?> view) {
  //       		Log.i(TAG,  view.getClass().getName());
         	}
         });
        Spinner spinner2 = (Spinner) findViewById(R.id.spi2);
      //为适配器设置下拉列表下拉时的菜单样式。
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
         	@Override
         	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//         		adapterView.getItemAtPosition(position);
         		spi2_id=position;
         		//String equ_str=adapterView.getItemAtPosition(position).toString();
//         		myTextView.setText(itemContent);
         	}
         	@Override
         	public void onNothingSelected(AdapterView<?> view) {
  //       		Log.i(TAG,  view.getClass().getName());
         	}
         });
        but_que=(Button)findViewById(R.id.queryDataBtn);
        but_que.setOnClickListener(new But_bb_Listener());
		// 起始日期和截至日期的初始化
		startDatePickerTV = (TextView) findViewById(R.id.startDatePickerTV);
		endDatePickerTV = (TextView) findViewById(R.id.endDatePickerTV);
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		final String currentDateTimeStr = sdf.format(calendar.getTime());
		// startDatePickerTV.setText(currentDateTimeStr);
		// endDatePickerTV.setText(currentDateTimeStr);
		// 呼出日期设定对话框，设定起时日期
		startDatePickerTV.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(ManageDataActivity.this,
						currentDateTimeStr);
				dateTimePicKDialog.dateTimePicKDialog(startDatePickerTV);
			}
		});
		// 呼出日期设定对话框，设定截至日期
		endDatePickerTV.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(ManageDataActivity.this,
						currentDateTimeStr);
				dateTimePicKDialog.dateTimePicKDialog(endDatePickerTV);
			}
		});
		if (startDatePickerTV.getText()!="" && startDatePickerTV.getText()!=null){
			time1=startDatePickerTV.getText().toString();
		}
		if (endDatePickerTV.getText()!="" && endDatePickerTV.getText()!=null){
			time2=endDatePickerTV.getText().toString();
		}
		// ---------更多按钮的初始化，主要是实现popupWindow
		Button moreBtn = (Button) findViewById(R.id.moreBtn);
		moreBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showPopupWindow();
				// datafilterLayout.setVisibility(View.INVISIBLE);
			}
		});
	}
	public class But_bb_Listener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
    		 case R.id.queryDataBtn:
    			 //DO_ID=0;
    			 pt=spi1_id;
    			 QueryRecord(spi1_id,spi2_id);
//    			 inti_progress();
    	     break;
			}
		}
	}
	public void QueryRecord(int ID,int ID1) {  
//		 DecimalFormat myformat=new DecimalFormat("000000000000");		 
//		 condition= "dbid=?";  
		if (startDatePickerTV.getText()!="" && startDatePickerTV.getText()!=null){
			time1=startDatePickerTV.getText().toString();
		}
		if (endDatePickerTV.getText()!="" && endDatePickerTV.getText()!=null){
			time2=endDatePickerTV.getText().toString();
		}
		sql="select * from sxxy_data";
		TextView content=(TextView)findViewById(R.id.text_que);	
		String[] ags={};
		switch(ID){
		 case 0:
			 sql=sql+" where 1=1"+ " order by cs_time desc";
			 mtxt_xm.setText("电表编号");
		 break;
		 case 1:
			 sql=sql+" where dbid";//+ " order by cs_time desc";
			 mtxt_xm.setText("电表编号");
		 break;
		 case 2:
			 sql=sql+" where cs_time between ? and ?"+ " order by cs_time desc";
			 mtxt_xm.setText("电表编号");
		 break;
		 case 3:
			 sql=sql+" where name";//+" order by cs_time desc";
			 mtxt_xm.setText("用户名称");
	     break;
		 case 4:
			 sql=sql+" where addr";//+" order by cs_time desc";
			 mtxt_xm.setText("用户地址");
	     break;
		 case 5:
			 sql=sql+" where xianl";//+" order by cs_time desc";
			 mtxt_xm.setText("线路");
		 break;
		 case 6:
			 sql=sql+" where taiz";//+" order by cs_time desc";
			 mtxt_xm.setText("台站");
		 break;
		 case 7:
			 sql=sql+" where jyy";//+" order by cs_time desc";
			 mtxt_xm.setText("检验员");
		 break;
		 case 8:
			 sql=sql+" where jhy";//+" order by cs_time desc";
			 mtxt_xm.setText("校核员");
		 break;
		 }
		if(ID!=2 && ID!=0){
			ags=new String[]{content.getText().toString()};
			switch(ID1){
			case 0:
				sql=sql+"=?"+" order by cs_time desc";
			break;
			case 1:
				sql=sql+">?"+" order by cs_time desc";
			 break;
			case 2:
				sql=sql+">=?"+" order by cs_time desc";
			break;
			case 3:
				sql=sql+"<?"+" order by cs_time desc";
			 break;
			case 4:
				sql=sql+"<=?"+" order by cs_time desc";
			}
		}
		else{
			ags=new String[]{time1
	   		,time2};
	//		ags=new String[]{"2012-09-23 08:00:00"};
		}
//		 sql="select * from sxxy_data where "+condition;
		 //Cursor cursor;
		 if(ID==0){cursor = sqldb.rawQuery(sql,null);}
		 else{
			 if(ID==2 && (time1==""||time2==""||time1==null||time2==null)){
					DisplayToast("请输入起止时间");
					return;	
			 }else{
			 cursor = sqldb.rawQuery(sql,ags);}
		 }
		loadData();
  		mAdapter.notifyDataSetChanged();
      /*  
        String id = cursor.getColumnName(0);// 获取第1列  
//        String id = Integer.toString(cursor.getPosition());// 获取第1列  
        String bid= cursor.getColumnName(1);// 获取第2列
        String cs_time = cursor.getColumnName(2);// 获取第3列 
        String name = cursor.getColumnName(3);// 获取第4列 
        String addr = cursor.getColumnName(4);// 获取第5列  
        String xl = cursor.getColumnName(5);// 获取第6列  
        String tz = cursor.getColumnName(6);// 获取第7列
        String jyy = cursor.getColumnName(7);// 获取第8列
        String jhy = cursor.getColumnName(8);// 获取第8列
        String[] ColumnNames = { id, bid, cs_time,name,addr,xl,tz,jyy,jhy };  
        ListAdapter adapter = new MySimpleCursorAdapter(this,  
                R.layout.listviewlayout, cursor, ColumnNames, new int[] { R.id.id,  
                        R.id.bid, R.id.time, R.id.name,R.id.addr,R.id.xl,R.id.tz,R.id.jyy,R.id.jhy });  
        // layout为listView的布局文件，包括九个TextView，用来显示三个列名所对应的值  
        // ColumnNames为数据库的表的列名   
        // 最后一个参数是int[]类型的，为view类型的id，用来显示ColumnNames列名所对应的值。view的类型为TextView  
        lv.setAdapter(adapter); 
        if(cursor.getCount()>0){
       	 if(DO_ID==1){
       		 cursor.moveToPosition(position1);
       		 //DO_ID=0;
       		 lv.setSelection(position1);
       		 }
       	 else{
       		 	cursor.moveToFirst();
       	 		lv.setSelection(cursor.getPosition());
       	     }
       	 display(cursor);       	         
        }
*///20160409
//        MotionEvent me=MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN,100,100,0);

//        onTouch(View view, me);	 
	}
	// =============将时间戳转为标准时间字符串============= 
		public static String getStrTime(String cc_time) {
			String re_StrTime = null;  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			long lcc_time = Long.valueOf(cc_time);  
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));  
			return re_StrTime; 
			}
		
		//=======标准时间字符串转为时间戳======
		public static String getTime1(String user_time) {  
			String re_time = null;  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date d;  
			try {   
				d = sdf.parse(user_time);   
				long l = d.getTime();  
			    String str = String.valueOf(l);   
				re_time = str.substring(0, 10);  
				} 
		    catch (ParseException e) {
					 // TODO Auto-generated catch block   
				e.printStackTrace();  
				}  
				 return re_time; 
				}
		//=========将时间戳转为中式时间字符串 ============
		public static String getStrTime1(String cc_time) {
			String re_StrTime = null;  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");  
			long lcc_time = Long.valueOf(cc_time);  
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));  
			return re_StrTime; 
		}
		//初始化WiFi连接状态2016-3-14h------
		public void DisplayToast(String msg){
	        	/*
	    	    Toast toast=Toast.makeText(this, msg, Toast.LENGTH_SHORT);
	    	    //设置toast显示位置
	    	    toast.setGravity(Gravity.TOP, 0, 220);
	    	    toast.show();
	    	    */
	    	    Toasts.toast(this, msg, Toast.LENGTH_LONG,Gravity.TOP, 0, 520);
	    } 
	// 点击更多后，显示弹出窗口
	private void showPopupWindow() {
		// 背景透明度跳转，变暗
		setHomeWindowAlphaValue(0.5f);
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.manage_data_popup_window, null);
		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
		final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		window.setBackgroundDrawable(dw);
		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.popup_window_anim_style);
		// 在底部显示
		window.showAtLocation(ManageDataActivity.this.findViewById(R.id.manageDataRootLayout), Gravity.BOTTOM, 0, 0);
		// 删除当前筛选出的所有数据
		Button deleteAllDataBtn = (Button) view.findViewById(R.id.deleteAllDataBtn);
		deleteAllDataBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				System.out.println("我要删除全部筛选出的数据");
				AlertDialog.Builder builder = new Builder(ManageDataActivity.this);
				builder.setMessage("确认要删除当前筛选出的全部数据？");
				builder.setTitle("提示");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AmeterData item;String ameterno;
						for(int i=0;i<datas.size();i++){
							ameterno=datas.get(i).getAmeterNo();
							String str_delete="delete from sxxy_data where dbid='"+ameterno+"'";
							sqldb.execSQL(str_delete);
							str_delete="delete from xb51_data where dbid='"+ameterno+"'";
							sqldb.execSQL(str_delete);
							str_delete="delete from bx128_data where dbid='"+ameterno+"'";
							sqldb.execSQL(str_delete);
							//datas.remove(0);
						}
						QueryRecord(spi1_id,spi2_id);
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				window.dismiss();
				setHomeWindowAlphaValue(1.0f);
			}
			
		});
		Button uploaddata =(Button)view.findViewById(R.id.upLoadDataBtn);
		uploaddata.setOnClickListener(new OnClickListener(){

			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				
			    
				window.dismiss();
				setHomeWindowAlphaValue(1.0f);
			}
			
		});
		// 隐藏按钮事件
		Button hidePopupWindowBtn = (Button) view.findViewById(R.id.backManageDataBtn);
		hidePopupWindowBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				window.dismiss();
				setHomeWindowAlphaValue(1.0f);
			}
		});
		// popWindow消失监听方法，点击弹出窗口以外的地方隐藏弹出窗口
		window.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				System.out.println("popWindow消失");
				setHomeWindowAlphaValue(1.0f);
			}
		});
	}
/*	  */
	private void setHomeWindowAlphaValue(float alphaValue){
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = alphaValue; // 0.0-1.0
		getWindow().setAttributes(lp);
	}
	private void loadData() {
		// TODO Auto-generated method stub
		datas = new ArrayList<AmeterData>();
		Date now = new Date();
		//AmeterData currData = new AmeterData();
		//String sql="select * from sxxy_data";
		//Cursor cursor;
		if (first){cursor = sqldb.rawQuery(sql,null);
		first=false;}
		if (cursor.getCount()>0){
			//for(int j=0;j<300;j++){
			cursor.moveToFirst();
			//currData.setAmeterNo(cursor.getString(1));
			//currData.setTestDate(DateTimeControl.strToDateLong(cursor.getString(2)));}
			for (int i = 0; i < cursor.getCount(); i++) {
				//cursor.moveToFirst();
				AmeterData currData = new AmeterData();
				currData.setAmeterNo(cursor.getString(1));
				if(cursor.getString(2)==null || cursor.getString(2).equalsIgnoreCase("")){
					//currData.setTestDate(null);
					currData=null;
					continue;
				}else{
					currData.setTestDate(DateTimeControl.strToDateLong(cursor.getString(2)));}
					currData.setUserName(cursor.getString(3));
					currData.setUserAddress(cursor.getString(4));
					currData.setLine(cursor.getString(5));
					currData.setStation(cursor.getString(6));
					currData.setCorector(cursor.getString(8));
					currData.setInspector(cursor.getString(7));
					datas.add(currData);
					currData=null;
					cursor.moveToNext();
			}
			//}
		}
		/*for (int i = 0; i < 50; i++) {
			currData.setAmeterNo("00012334567");
			currData.setTestDate(DateTimeControl.strToDateLong("2016-04-15 10:10:10"));
			//currData.setUserName(cursor.getString(3));
			//currData.setUserAddress(cursor.getString(4));
			//currData.setLine(cursor.getString(5));
			//currData.setStation(cursor.getString(6));
			//currData.setCorector(cursor.getString(8));
			//currData.setInspector(cursor.getString(7));
			datas.add(currData);
			//cursor.moveToNext();
		}*/
	}
	// 使用SimpleAdapter时的代码，主要是建立hashmap结构，在使用侧滑列表后放弃
	// private void loadAmeterData() {
	//
	// datas = new ArrayList<AmeterData>();
	// Date now = new Date();
	// AmeterData currData = new AmeterData("0000000123", now, "Tom", "USA",
	// "666", "马甲吞", "李啊水", "孙悟空");
	//
	// for (int i = 0; i < 100; i++) {
	// datas.add(currData);
	// }
	//
	// List<HashMap<String, Object>> dataMapList = new ArrayList<HashMap<String,
	// Object>>();
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m");
	// for (AmeterData d : datas) {
	// HashMap<String, Object> newMap = new HashMap<String, Object>();
	// newMap.put("ameterNo", d.getAmeterNo());
	// newMap.put("testDate", dateFormat.format(d.getTestDate()));
	// newMap.put("userName", d.getUserName().toString());
	// newMap.put("userAddress", d.getUserAddress().toString());
	// newMap.put("line", d.getLine().toString());
	// newMap.put("station", d.getStation().toString());
	// newMap.put("inspector", d.getInspector().toString());
	// newMap.put("corector", d.getCorector().toString());
	//
	// dataMapList.add(newMap);
	// }
	//
	// // new String[] { "电表编号", "测试时间", "用户名称","用户地址","线路" },
	//
	// // 创建SimpleAdapter适配器将数据绑定到item显示控件上
	// SimpleAdapter adapter = new SimpleAdapter(this, dataMapList,
	// R.layout.manage_data_listview_item,
	// new String[] { "ameterNo", "testDate", "userName", "userAddress", "line"
	// },
	// new int[] { R.id.ameterNoItemTv, R.id.testDateItemTv,
	// R.id.userNameItemTv, R.id.userAddressItemTv,
	// R.id.lineItemTv });
	// // 实现列表的显示
	// mListView.setAdapter(adapter);
	// // 条目点击事件
	// mListView.setOnItemClickListener(new ItemClickListener());
	//
	// }
	// 列表项点击监听器类
	private final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //需要把表号等参数传递过去，显示此表号对应的数据。2016-03-12h注。
			AmeterData item = datas.get(position);
			Intent intent=new Intent(ManageDataActivity.this, ManageDataDetailActivity.class);
			Bundle bd=new Bundle();
			bd.putString("dbid", item.getAmeterNo());
			intent.putExtra("ameterno", bd);
			startActivity(intent);
			//startActivity(new Intent(ManageDataActivity.this, ManageDataDetailActivity.class));
			// activity切换过场动画
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}
	// 列表项适配器定义
	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return datas.size();
		}
		@Override
		public AmeterData getItem(int position) {
			return datas.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.manage_data_listview_item, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			String str_temp="";
			AmeterData item = getItem(position);
			switch(pt){
			case 0:
			case 1:
			case 2:
				str_temp=item.getAmeterNo();
				break;
			case 3:
				str_temp=item.getUserName();
				break;
			case 4:
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
				break;
			}
			holder.ameterNoItemTv.setText(str_temp);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			holder.testDateItemTv.setText(dateFormat.format(item.getTestDate()));
			// holder.userNameItemTv.setText(item.getUserName());
			return convertView;
		}
		class ViewHolder {
			TextView ameterNoItemTv;
			TextView testDateItemTv;
			// TextView userNameItemTv;
			public ViewHolder(View view) {
				ameterNoItemTv = (TextView) view.findViewById(R.id.ameterNoItemTv);
				testDateItemTv = (TextView) view.findViewById(R.id.testDateItemTv);
				// userNameItemTv = (TextView)
				// view.findViewById(R.id.userNameItemTv);
				view.setTag(this);
			}
		}
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
	/**
	 * 这里覆盖父类Activity中的onBackPressed()方法
	 * 左侧返回按钮的响应事件已经在布局文件的onClick属性中声明了，为父类BaseActivity中声明的doBack()方法
	 * 这里覆盖的目的是增加过场动画
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
