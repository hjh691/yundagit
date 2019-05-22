package com.example.substationtemperature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.ReturnData;
import com.example.substationtemperature.network.ClientService;
import com.example.substationtemperature.view.Toasts;

public class Regist extends Activity implements OnClickListener{
	private EditText edt_password,edt_password2,edt_phonenumber,edt_danwei,edt_email,edt_username;
	private Button btn_saveinfo,btn_concel;
	private TextView txt_editpassword,txt_password;
	private LinearLayout layout_password2;
	private Handler handler;
	private String newpass="",usergrade="100";
	@SuppressLint("HandlerLeak") @Override
	public void onCreate(Bundle savedInstanceState){
		super. onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		handler = new Handler(){
            @SuppressLint({ "HandlerLeak", "ShowToast" }) @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case 0://更新用户信息
                	edt_username.setText(Declare.username);
                	edt_phonenumber.setText(usergrade);
                	edt_danwei.setText(Declare.danwei);
                    break;
                case 1://错误信息
                    String info = (String) msg.obj;
                    Toast.makeText(Regist.this, info,Toast.LENGTH_SHORT).show();
                    break;
                case 2://密码修改成功
                	info = (String) msg.obj;
                    Toast.makeText(Regist.this, info+" 请重新登录",Toast.LENGTH_SHORT).show();
                    Declare.islogin=false;
                    Declare.username="未登录";
                    Declare.password="";
                    Declare.isremember_pw=false;
                    Declare.isautologin=false;
                    btn_saveinfo.setVisibility(View.GONE);
                    Intent intent=new Intent(Regist.this,Login.class);
    				startActivityForResult(intent,1);
    				overridePendingTransition(R.anim.fade, R.anim.hold);
                    break;
                }
            }
        };
		//初始化页面信息
		initview();
		//从后台获取用户信息
		initinfo();
	}
	private void initview() {
		// TODO Auto-generated method stub
		edt_username=(EditText)findViewById(R.id.edt_username);
		edt_username.setText(Declare.username);
		edt_username.setEnabled(false);
		edt_password=(EditText)findViewById(R.id.edt_password);
		edt_password.setText(Declare.password);
		edt_password.setEnabled(false);
		edt_password2=(EditText)findViewById(R.id.edt_password2);
		txt_password=(TextView)findViewById(R.id.txt_password);
		txt_editpassword=(TextView)findViewById(R.id.txt_editpassword);
		txt_editpassword.setOnClickListener(this);
		layout_password2=(LinearLayout)findViewById(R.id.lay_password2);
		edt_phonenumber=(EditText)findViewById(R.id.edt_phonenumber);
		edt_phonenumber.setText(Declare.phonenumber);
		edt_danwei=(EditText)findViewById(R.id.edt_danwei);
		edt_danwei.setText(Declare.danwei);
		edt_email=(EditText)findViewById(R.id.edt_email);
		edt_email.setText(Declare.email);
		
		btn_saveinfo=(Button)findViewById(R.id.btn_save_userinfo);
		btn_saveinfo.setOnClickListener(this);
		btn_saveinfo.setVisibility(View.GONE);
		btn_concel=(Button)findViewById(R.id.btn_cancel_userinfo);
		btn_concel.setOnClickListener(this);
	}
	private void initinfo() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
	         @Override
	         public void run() {
	        	 try {
	        	 ReturnData iis = ClientService.sendpost("_manager/GetUserProfile");
	        	 if(iis.mark.equals("ok")){
		        	 // 使用BufferedReader对象读取返回的数据流
				     // 按行读取，存储在StringBuider对象response中
				     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
				     //StringBuilder response = new StringBuilder();
				     String line;
				     JSONObject result = null;
				     while ((line = reader.readLine()) != null) {
				         //response.append(line);
				    	 try {
				    		 result = new JSONObject( line);
				    	 } catch (JSONException e) {
				    			// TODO 自动生成的 catch 块
				    		 e.printStackTrace();
				    	 }
				     }
				     String errorcode=result.getString("Error");
				     if(result.isNull("Error")){
				    	 result=result.getJSONObject("Result");
			    		 Declare.username=result.getString("UserName");
			    		 Declare.password=result.getString("UserPass");
			    		 usergrade=String.valueOf(result.getInt("UserLimit"));
			    		 Declare.danwei=result.getString("Description");
				    	 Message msg = new Message();
			             msg.what = 0;
			             handler.sendMessage(msg);
				     }else{
				    	 Message msg = new Message();
			             msg.what = 1;
			             msg.obj=errorcode;
			             handler.sendMessage(msg);
				     }
	        	 }else if(iis.mark.equals("outtime")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接超时";
		             handler.sendMessage(msg);
	        	 }else if(iis.mark.equals("error")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接错误";
		             handler.sendMessage(msg);
	        	 }else if(iis.mark.equals("未授权")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="操作失败，权限未认证";
		             handler.sendMessage(msg);
	        	 }else{
	        		 Message msg = new Message();
	        		 msg.what = 1;
	        		 msg.obj=iis.mark;
	        		 FragmentHistraydata.mHandler.sendMessage(msg);
        	     }
			  //..........
			  // 此处省略处理数据的代码
			  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
	        } catch (Exception e){
	             e.printStackTrace();
	        }// finally {}
	     }
	}).start();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btn_save_userinfo){
			if((!edt_password.getText().toString().equals(""))){
				if(layout_password2.getVisibility()==View.VISIBLE){
					if(edt_password2.getText().toString().equals("")){
						// TODO saveuserinfo
						Toasts.toast(this, "请输入新的密码", Toast.LENGTH_LONG);
						return;
					}
				}
				newpass=edt_password2.getText().toString();
				Declare.password=edt_password.getText().toString().trim();
				Declare.phonenumber=edt_phonenumber.getText().toString().trim();
				Declare.danwei=edt_danwei.getText().toString().trim();
				Declare.email=edt_email.getText().toString().trim();
				save_userinfo();//将修改后的信息进行保存-发送到后台。
				
			}else{
				Toast.makeText(this, "密码不能为空",Toast.LENGTH_SHORT).show();
				return ;
			}
			
			
		}else if(v.getId()==R.id.btn_cancel_userinfo){
			txt_password.setText("密码");
			edt_password2.setText("");
			layout_password2.setVisibility(View.GONE);
			finish();
		}else if(v.getId()==R.id.txt_editpassword){
			txt_password.setText("旧密码");
			layout_password2.setVisibility(View.VISIBLE);
			edt_password2.setText("");
			edt_password.setEnabled(true);
			btn_saveinfo.setVisibility(View.VISIBLE);
		}
		
	}
	private void save_userinfo() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
	         @Override
	         public void run() {
	        	 try {
	        	 ReturnData iis = ClientService.sendpost("ChangePass?pass="+URLEncoder.encode(Declare.password,"UTF-8")+"&newpass="+URLEncoder.encode(newpass,"UTF-8"));
	        	 if(iis.mark.equals("ok")){
		        	 // 使用BufferedReader对象读取返回的数据流
				     // 按行读取，存储在StringBuider对象response中
				     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
				     //StringBuilder response = new StringBuilder();
				     String line;
				     JSONObject result = null;
				     while ((line = reader.readLine()) != null) {
				         //response.append(line);
				    	 try {
				    		 result = new JSONObject( line);
				    	 } catch (JSONException e) {
				    			// TODO 自动生成的 catch 块
				    		 e.printStackTrace();
				    	 }
				     }
				     String errorcode=result.getString("Error");
				     if(result.isNull("Error")){
				    	 if(result.getBoolean("Result")){
					     	  
				    	 Message msg = new Message();
			             msg.what = 2;
			             msg.obj="密码修改成功";
			             handler.sendMessage(msg);
				    	 }
				     }else{
				    	 Message msg = new Message();
			             msg.what = 1;
			             msg.obj=errorcode;
			             handler.sendMessage(msg);
				     }
	        	 }else if(iis.mark.equals("outtime")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接超时";
		             handler.sendMessage(msg);
	        	 }else if(iis.mark.equals("error")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="连接错误";
		             handler.sendMessage(msg);
	        	 }else if(iis.mark.equals("未授权")){
	        		 Message msg = new Message();
		             msg.what = 1;
		             msg.obj="认证权限未授权";
		             handler.sendMessage(msg);
	        	 }else{
			    	  Message msg = new Message();
		              msg.what = 1;
		              msg.obj=iis.mark;
		              handler.sendMessage(msg);
			      }
			  //..........
			  // 此处省略处理数据的代码
			  // 若需要更新UI，需将数据传回主线程，具体可搜索android多线程编程
	        } catch (Exception e){
	             e.printStackTrace();
	             Message msg = new Message();
	             msg.what = 1;
	             msg.obj="操作失败";
	             handler.sendMessage(msg);
	        }// finally {}
	     }
	}).start();
	}

}
