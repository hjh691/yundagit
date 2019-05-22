package com.lx.checkameterclient.fragment;

import java.io.File;
import java.text.DecimalFormat;

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
import android.widget.LinearLayout;
import android.widget.TextView;

public class DDVoltAmphereTesting extends Fragment {

	private static TextView ua,ub,uc,ia,ib,ic,ja,jb,jc,hz,sin,cos,pa,pb,pc,ph,qa,qb,qc,qh,sa,sb,sc,sh,zs,temp;
    private static TextView unit_p,unit_q,unit_s;
    private DecimalFormat myformat1,myformat2,myformat3,myformat4;
    SQLiteDatabase sqldb;
    String sql;
    private File file = new File("/sdcard/bdlx/sxxy.db");// 创建文件
    public static Cursor cursor=null;
    private static LinearLayout layout_ub,layout_ib,layout_jb,layout_pb;
    private static TextView lab_ua,lab_uc,lab_ia,lab_ic,lab_ja,lab_jc,lab_pa,lab_pc;
    boolean zs_flag=false;//制式标志区分三相三线与三相四线
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dd_volt_amphere_testing_fragment, null);
		myformat2 = new DecimalFormat("0.000");
		ua=(TextView)view.findViewById(R.id.voltATV);
		ub=(TextView)view.findViewById(R.id.voltBTV);
		uc=(TextView)view.findViewById(R.id.voltCTV);
		ia=(TextView)view.findViewById(R.id.amphereATV);
		ib=(TextView)view.findViewById(R.id.amphereBTV);
		ic=(TextView)view.findViewById(R.id.amphereCTV);
		ja=(TextView)view.findViewById(R.id.angleATV);
		jb=(TextView)view.findViewById(R.id.angleBTV);
		jc=(TextView)view.findViewById(R.id.angleCTV);
		hz=(TextView)view.findViewById(R.id.ohterHzTV);
		sin=(TextView)view.findViewById(R.id.otherSinTV);
		cos=(TextView)view.findViewById(R.id.otherCosTV);
		pa=(TextView)view.findViewById(R.id.text_gxjg);
		pb=(TextView)view.findViewById(R.id.text_rxjg);
		pc=(TextView)view.findViewById(R.id.activePowerCTV);
		ph=(TextView)view.findViewById(R.id.activePowerSumTV);
		qa=(TextView)view.findViewById(R.id.reactivePowerATV);
		qb=(TextView)view.findViewById(R.id.reactivePowerBTV);
		qc=(TextView)view.findViewById(R.id.reactivePowerCTV);
		qh=(TextView)view.findViewById(R.id.reactivePowerSumTV);
		sa=(TextView)view.findViewById(R.id.shiZaiPowerATV);
		sb=(TextView)view.findViewById(R.id.shiZaiPowerBTV);
		sc=(TextView)view.findViewById(R.id.shiZaiPowerCTV);
		sh=(TextView)view.findViewById(R.id.shiZaiPowerSumTV);
		zs=(TextView)view.findViewById(R.id.zs);//电表制式
		//功率单位
		unit_p=(TextView)view.findViewById(R.id.unit_p);
 		unit_q=(TextView)view.findViewById(R.id.unit_q);
 		unit_s=(TextView)view.findViewById(R.id.unit_s);
 		/*
		 *============ 动态调整布局====================
		 */
		layout_ub=(LinearLayout)view.findViewById(R.id.layout_ub);
		layout_ib=(LinearLayout)view.findViewById(R.id.layout_ib);
		layout_jb=(LinearLayout)view.findViewById(R.id.layout_jb);
		layout_pb=(LinearLayout)view.findViewById(R.id.layout_pb);
		//LinearLayout layout_qb=(LinearLayout)findViewById(R.id.layout_qb);
		//LinearLayout layout_sb=(LinearLayout)findViewById(R.id.layout_sb);
		lab_ua=(TextView)view.findViewById(R.id.lab_ua);
		lab_uc=(TextView)view.findViewById(R.id.lab_uc);
		lab_ia=(TextView)view.findViewById(R.id.lab_ia);
		lab_ic=(TextView)view.findViewById(R.id.lab_ic);
		lab_ja=(TextView)view.findViewById(R.id.lab_ja);
		lab_jc=(TextView)view.findViewById(R.id.lab_jc);
		lab_pa=(TextView)view.findViewById(R.id.lab_pa);
		lab_pc=(TextView)view.findViewById(R.id.lab_pc);
		//TextView lab_qa=(TextView)findViewById(R.id.lab_qa);
		//TextView lab_qc=(TextView)findViewById(R.id.lab_qc);
		//TextView lab_sa=(TextView)findViewById(R.id.lab_sa);
		//TextView lab_sc=(TextView)findViewById(R.id.lab_sc);
		
		
		
		display();

		return view;
	}
	
	public void display(){
		//打开创建一个数据库        
        sqldb = SQLiteDatabase.openOrCreateDatabase(file, null);
        sql="select * from sxxy_data where dbid=?";
        String[] ags={ManageDataDetailActivity.dbid};
        cursor=sqldb.rawQuery(sql, ags);
        int temp_pa,temp_pb,temp_pc,
		temp_qa,temp_qb,temp_qc,
		temp_sa,temp_sb,temp_sc;
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			ua.setText(cursor.getString(cursor.getColumnIndex("ua_fz")));
			ub.setText(cursor.getString(cursor.getColumnIndex("ub_fz")));
			uc.setText(cursor.getString(cursor.getColumnIndex("uc_fz")));
			ia.setText(cursor.getString(cursor.getColumnIndex("ia_fz")));
			ib.setText(cursor.getString(cursor.getColumnIndex("ib_fz")));
			ic.setText(cursor.getString(cursor.getColumnIndex("ic_fz")));
			ja.setText(cursor.getString(cursor.getColumnIndex("ja")));
			jb.setText(cursor.getString(cursor.getColumnIndex("jb")));
			jc.setText(cursor.getString(cursor.getColumnIndex("jc")));
			hz.setText(cursor.getString(cursor.getColumnIndex("hz")));
			
			double cos_value,sin_value;
	 		try {
			cos_value=Double.parseDouble(cursor.getString(35));	
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			cos_value=0;
	 		}
			sin_value=Math.sin(Math.acos(cos_value));
			if(sin_value<0.0175){
				sin_value=0;
			}
			cos.setText(cursor.getString(cursor.getColumnIndex("cos")));
			sin.setText(myformat2.format((float)(sin_value)));
			pa.setText(cursor.getString(cursor.getColumnIndex("pa")));
			pb.setText(cursor.getString(cursor.getColumnIndex("pb")));
			pc.setText(cursor.getString(cursor.getColumnIndex("pc")));
			double ph_tmp=0;
	 		try {
	 			ph_tmp=Float.parseFloat(pa.getText().toString())+
					       Float.parseFloat(pb.getText().toString())+
					       Float.parseFloat(pc.getText().toString());
	 			ph.setText(myformat2.format(ph_tmp)); 
   	 		} catch (Exception e) {
      	   // TODO Auto-generated catch block
   	 			e.printStackTrace();
   	 			ph.setText("0.000");
      	   
   	 		}
			qa.setText(cursor.getString(cursor.getColumnIndex("qa")));
			qb.setText(cursor.getString(cursor.getColumnIndex("qb")));
			qc.setText(cursor.getString(cursor.getColumnIndex("qc")));
			double qh_tmp=0;
	 		try {
	 			qh_tmp=Float.parseFloat(qa.getText().toString())+
					       Float.parseFloat(qb.getText().toString())+
					       Float.parseFloat(qc.getText().toString());
	 			qh.setText(myformat2.format(qh_tmp)); 
   	 		} catch (Exception e) {
      	   // TODO Auto-generated catch block
   	 			e.printStackTrace();
   	 			qh.setText("0.000");
      	   
   	 		}
			sa.setText(cursor.getString(cursor.getColumnIndex("sa")));
			sb.setText(cursor.getString(cursor.getColumnIndex("sb")));
			sc.setText(cursor.getString(cursor.getColumnIndex("sc")));
			double sh_tmp=0;
	 		
	 		try {
	 			sh_tmp=Math.sqrt ((Math.pow(ph_tmp,2)+Math.pow(qh_tmp,2)));
	 			sh.setText(myformat2.format(sh_tmp)); 
   	 		} catch (Exception e) {
      	   // TODO Auto-generated catch block
   	 			e.printStackTrace();
   	 			sh.setText("0.000");
      	   
   	 		}
	 		//根据读取的数值变换显示与单位
	 		//根据具体数值转换显示单位
	 		try {
	 			temp_pa=(int)Float.parseFloat(pa.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_pa=0;	       	   
	    	 }
	 		try {
	 			temp_pb=(int)Float.parseFloat(pb.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_pb=0;	       	   
	    	 }
	 		try {
	 			temp_pc=(int)Float.parseFloat(pc.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_pc=0;	       	   
	    	 }
	 		
	 		
	 		try {
	 			temp_qa=(int)Float.parseFloat(qa.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_qa=0;	       	   
	    	 }			
	 		try {
	 			temp_qb=(int)Float.parseFloat(qb.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_qb=0;	       	   
	    	 }								
	 		try {
	 			temp_qc=(int)Float.parseFloat(qc.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_qc=0;	       	   
	    	 }	
	 		
	 		try {
	 			temp_sa=(int)Float.parseFloat(sa.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_sa=0;	       	   
	    	 }
	 		try {
	 			temp_sb=(int)Float.parseFloat(sb.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_sb=0;	       	   
	    	 }					
	 		try {
	 			temp_sc=(int)Float.parseFloat(sc.getText().toString());
	 		} catch (Exception e) {
	       	   // TODO Auto-generated catch block
	    	 			e.printStackTrace();
	    	 			temp_sc=0;	       	   
	    	 }
	 		 		
			if(temp_pa>=10000 || temp_pb>=10000 || temp_pc>=10000){
				pa.setText(myformat1.format((float)(Float.parseFloat(pa.getText().toString())/1000)));
				pb.setText(myformat1.format((float)(Float.parseFloat(pb.getText().toString())/1000)));
				pc.setText(myformat1.format((float)(Float.parseFloat(pc.getText().toString())/1000)));
				ph.setText(myformat1.format((float)(Float.parseFloat(ph.getText().toString())/1000)));
				unit_p.setText("(KW)");//unit_pb.setText(" KW");unit_pc.setText(" KW");unit_ph.setText(" KW");
				
			}
			if(temp_pa<10000 && temp_pb<10000 && temp_pc<10000){

				unit_p.setText("(W)");//unit_pb.setText("  W");unit_pc.setText("  W");unit_ph.setText("  W");
				
			}
			
			if(temp_qa>=10000 || temp_qb>=10000 || temp_qc>=10000){
				qa.setText(myformat1.format((float)(Float.parseFloat(qa.getText().toString())/1000)));
				qb.setText(myformat1.format((float)(Float.parseFloat(qb.getText().toString())/1000)));
				qc.setText(myformat1.format((float)(Float.parseFloat(qc.getText().toString())/1000)));
				qh.setText(myformat1.format((float)(Float.parseFloat(qh.getText().toString())/1000)));
				unit_q.setText("(Kvar)");//unit_qb.setText(" Kvar");unit_qc.setText(" Kvar");unit_qh.setText(" Kvar");
				
			}
			if(temp_qa<10000 && temp_qb<10000 && temp_qc<10000){

				unit_q.setText("(var)");//unit_qb.setText(" var");unit_qc.setText(" var");unit_qh.setText(" var");
							
			}
			
			if(temp_sa>=10000 || temp_sb>=10000 || temp_sc>=10000){
				sa.setText(myformat1.format((float)(Float.parseFloat(sa.getText().toString())/1000)));
				sb.setText(myformat1.format((float)(Float.parseFloat(sb.getText().toString())/1000)));
				sc.setText(myformat1.format((float)(Float.parseFloat(sc.getText().toString())/1000)));
				sh.setText(myformat1.format((float)(Float.parseFloat(sh.getText().toString())/1000)));
				unit_s.setText("(KVA)");//unit_sb.setText(" KVA");unit_sc.setText(" KVA");unit_sh.setText(" KVA");
				
			}
			if(temp_sa<10000 && temp_sb<10000 && temp_sc<10000){

				unit_s.setText("(VA)");//unit_sb.setText("  VA");unit_sc.setText("  VA");unit_sh.setText("  VA");
							
			}
			zs.setText(cursor.getString(cursor.getColumnIndex("dbzs")));
			String zs_str=zs.getText().toString();
	 		int zs_index=0;
	 		if(zs_str.equals("三相四线有功") || zs_str.equals("三相四线无功"))
	 		{
	 			zs_flag=true;zs_index=0;
	 		}
	 		if(zs_str.equals("三相三线有功") || zs_str.equals("三相三线无功"))
	 		{
	 			zs_flag=false;zs_index=1;
	 		}
			if(zs_index==0){
				layout_ub.setVisibility(0);layout_ib.setVisibility(0);layout_jb.setVisibility(0);
				layout_pb.setVisibility(0);//layout_qb.setVisibility(0);layout_sb.setVisibility(0);
				lab_ua.setText("A相");lab_ia.setText("A相");lab_ja.setText("A相");//lab_ja.setTextSize(24/size_value);
				lab_pa.setText("A相");//lab_qa.setText("  A相   ");lab_sa.setText("  A相   ");
				lab_uc.setText("C相");lab_ic.setText("C相");lab_jc.setText("C相");//lab_jc.setTextSize(24/size_value);
				lab_pc.setText("C相");//lab_qc.setText("  C相   ");lab_sc.setText("  C相   ");
			}
			if( zs_index==1){
				
				layout_ub.setVisibility(4);layout_ib.setVisibility(4);layout_jb.setVisibility(4);
				layout_pb.setVisibility(4);//layout_qb.setVisibility(4);layout_sb.setVisibility(4);
				lab_ua.setText("Uab");lab_ia.setText("Ia");lab_ja.setText("UabIa");//lab_ja.setTextSize(22/size_value);
				lab_pa.setText("AB相");//lab_qa.setText("  Qab ");lab_sa.setText("  Sab ");
				lab_uc.setText("Ucb");lab_ic.setText("Ic");lab_jc.setText("UcbIc");//lab_jc.setTextSize(22/size_value);
				lab_pc.setText("CB相");//lab_qc.setText("  Qcb ");lab_sc.setText("  Scb ");
				
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

}
