package com.example.substationtemperature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.substationtemperature.adapter.DatabaseContext;
import com.example.substationtemperature.adapter.UserDBHelper;
import com.example.substationtemperature.base.Declare;
import com.example.substationtemperature.base.ReturnData;
import com.example.substationtemperature.base.UserInfo;
import com.example.substationtemperature.network.ClientService;
import com.example.substationtemperature.network.Networkutils;
import com.example.substationtemperature.view.Toasts;

public class Login extends Activity implements OnClickListener{
	private EditText edt_login_username,edt_login_password;
	private ImageView img_ifshow;
	private CheckBox chb_remember,chb_autologin;
	private Button btn_login,btn_login_cancel;
	private TextView txt_forget;
	//private JSONObject json;
	//private SharedPreferences mPerferences;
	private String username,password;

	private  DatabaseContext dbcontext=new DatabaseContext(this);
	
	private Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	    //requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.login);
	    handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case Declare.STATUS_SUCCESS:
                    Toast.makeText(Login.this, "欢迎您"+Declare.username,Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor mEditor =MainActivity.mPerferences.edit();
            		mEditor.putString("username",Declare.username);
            		mEditor.putString("password", Declare.password);
            		mEditor.putBoolean("isremmember",Declare.isremember_pw);
            		mEditor.putBoolean("isautologin", Declare.isautologin);
            		mEditor.putString("homeurl",Declare.home_url);
            		mEditor.commit();
                    ClientService.getstation();
                    FragmentPicture.getGraphics();
                    finish();
                    break;
                case Declare.STATUS_ERROR:
                    String info = (String) msg.obj;
                    Toast.makeText(Login.this, info,Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                	info = (String) msg.obj;
                    Toast.makeText(Login.this, info,Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        };
	    
        MainActivity.mUserHelper=UserDBHelper.getInstance(dbcontext, 2);
		MainActivity.mUserHelper.openWriteLink();
		initview();
	   
	}
	

	@SuppressWarnings("unused")
	private void netinit() {
		// TODO Auto-generated method stub
		ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(Networkutils.isNetworkConnected(this)){
			Toast.makeText(this, "网络已连接",Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this,"当前无可用网络连接", Toast.LENGTH_SHORT).show();
		}
	}

	private void initview() {
		// TODO Auto-generated method stub
		edt_login_username=(EditText)findViewById(R.id.edt_login_username);
		edt_login_username.setText(Declare.username);
		edt_login_username.setFocusable(true);
		//edt_login_username.setFocusableInTouchMode(true);
		Editable etable = edt_login_username.getText();//光标在文本的末尾
		Selection.setSelection(etable, etable.length());
		//edt_login_username.setOnFocusChangeListener(new mOnFocus_Listener());
		edt_login_password=(EditText)findViewById(R.id.edt_login_password);
		edt_login_password.setOnClickListener(this);
		
		img_ifshow=(ImageView)findViewById(R.id.img_ifshow);
		img_ifshow.setOnClickListener(this);
		chb_remember=(CheckBox)findViewById(R.id.chb_remember);
		if(Declare.isremember_pw){
			edt_login_password.callOnClick();
			chb_remember.setChecked(true);
		}else{chb_remember.setChecked(false);}
		chb_autologin=(CheckBox)findViewById(R.id.chb_autologin);
		if(Declare.isautologin){
			chb_autologin.setChecked(true);
		}else{chb_autologin.setChecked(false);}
		btn_login=(Button)findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		btn_login_cancel=(Button)findViewById(R.id.btn_login_cancel);
		btn_login_cancel.setOnClickListener(this);
		txt_forget=(TextView)findViewById(R.id.txt_forget);
		txt_forget.setOnClickListener(this);
	}

	@SuppressWarnings("unused")
	private class mOnFocus_Listener implements OnFocusChangeListener{
		@Override 
			public void onFocusChange(View v, boolean hasFocus) {
				String name=edt_login_username.getText().toString();
				if(v.getId()==R.id.edt_login_username){
					if(name.length()>0 && hasFocus==false){
						UserInfo info=MainActivity.mUserHelper.queryByName(name);
						if(info!=null){
							edt_login_password.setText(info.password);
							chb_remember.setChecked(true);
						}else{
							edt_login_password.setText("");
							chb_remember.setChecked(false);
						}
					}else{
						edt_login_password.setText("");
					}
				}
			}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.img_ifshow:
			//Log.d("tag",img_ifshow.getDrawable().toString());
			//Log.d("tag",getResources().getDrawable(0x7f02001b).toString());
			Drawable.ConstantState t1= img_ifshow.getDrawable().getCurrent().getConstantState();
			@SuppressWarnings("deprecation")
			Drawable.ConstantState t2=getResources().getDrawable(R.drawable.eye_48x36).getConstantState();
			if(t1.equals(t2)){
				img_ifshow.setImageResource(R.drawable.eye_close);
				edt_login_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			}else{
				img_ifshow.setImageResource(R.drawable.eye_48x36);
				edt_login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			Editable etable = edt_login_password.getText();
			Selection.setSelection(etable, etable.length());
			break;
		case R.id.btn_login:
			//判断用户名和密码项是否为空	
			if(edt_login_username.getText().toString().equals("")){
					Toasts.toast(this,"请输入用户名称",Toast.LENGTH_SHORT);
					return;
				}else if(edt_login_password.getText().toString().equals("")){
					Toasts.toast(this,"请输入登录密码",Toast.LENGTH_SHORT);
					return;
				}else{
					//如果记住密码，则保存到本地数据库，
					if(chb_remember.isChecked()){
						SharedPreferences.Editor editor=MainActivity.mPerferences.edit();
						editor.putString("username", edt_login_username.getText().toString());
						editor.putString("password", edt_login_password.getText().toString());
						editor.putBoolean("isremember", true);
						editor.commit();
						//UserInfo info=new UserInfo();
						username=edt_login_username.getText().toString();
						MainActivity.userinfo.name=username;
						password=edt_login_password.getText().toString();
						MainActivity.userinfo.password=password;
						MainActivity.userinfo.isremember=true;
						MainActivity.mUserHelper.insert(MainActivity.userinfo);
						Declare.isremember_pw=true;
						Declare.username=username;
						Declare.password=password;
						//记住密码，看是否选择自动登录，如果是，则置自动登录标志
						if(chb_autologin.isChecked()){
							Declare.isautologin=true;
						}else{
							Declare.isautologin=false;
						}
					}else{
						username=edt_login_username.getText().toString();
						Declare.username=username;
						password=edt_login_password.getText().toString();
						Declare.isremember_pw=false;
						Declare.password="";//edt_login_password.getText().toString();
					}
					//符合条件，判断网络，发送登录命令
					if(Networkutils.isNetworkConnected(this)){
						logintoserver();//登录
					}else{
						Toasts.toast(this,"当前没有可用的网络，请确认网络是否连接!");
					}
				}
			
			break;
		case R.id.btn_login_cancel:
			edt_login_password.setText("");
			finish();
			break;
		case R.id.txt_forget:
			//lookforpassword();
			break;
		case R.id.edt_login_password:
			String name=edt_login_username.getText().toString();
			if(name.length()>0){
				UserInfo info=MainActivity.mUserHelper.queryByName(name);
				if(info!=null){
					edt_login_password.setText(info.password);
				}else{
					edt_login_password.setText("");
				}
			}else{
				edt_login_password.setText("");
			}
			
		}
	}

	@SuppressWarnings("unused")
	private void lookforpassword() {
		// TODO Auto-generated method stub
		//http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100
		if(!Networkutils.isNetworkConnected(this)){
			Toast.makeText(this,"当前无可用网络连接", Toast.LENGTH_SHORT).show();
		}else{	
			 new Thread(new Runnable() {
		         @Override
		         public void run() {
		        	 try {
		        	 //UserHttpClient YcHttpClient = null;
		        	 ReturnData iis = ClientService.sendpost("http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100");
		        	 if(iis.mark.equals("ok")){
			        	 // 使用BufferedReader对象读取返回的数据流
					     // 按行读取，存储在StringBuider对象response中
					     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
					     StringBuilder response = new StringBuilder();
					     String line;
					     JSONObject result = null;
					     //JSONArray jsArray=null;
					     while ((line = reader.readLine()) != null) {
					         response.append(line);
					    	 try {
					    		 result = new JSONObject( line);
					    	 } catch (JSONException e) {
					    			// TODO 自动生成的 catch 块
					    		 e.printStackTrace();
					    	 }
					      }
					      String token="";
					      String errorcode=result.getString("Error");
					      if(errorcode.equals("null")){
					    	  result=result.getJSONObject("Result");
					    	  try {
						    	  token = result.getString("Token");
						    	  Declare.islogin=true;
						    	  Message msg = new Message();
					              msg.what = 0;
					              handler.sendMessage(msg);
						      } catch (JSONException e) {
										// TODO 自动生成的 catch 块
						    	  e.printStackTrace();
						      }  
						      if(token!=null){
						    	  Declare._token=token;
						      }
						      //
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
		        	 }else{
				    	  Message msg = new Message();
			              msg.what = 1;
			              msg.obj=iis.mark;
			              handler.sendMessage(msg);
				      }
		        } catch (Exception e){
		             e.printStackTrace();
		        }// finally {}
		     }
		}).start();
		}

	}


	private void logintoserver() {
		// TODO Auto-generated method stub
		/*try {
			ClientService.sendpost(Declare.LOGIN+"?name="+URLEncoder.encode(Declare.username,"UTF-8")+"&pass="+URLEncoder.encode(Declare.password,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		if(!Networkutils.isNetworkConnected(this)){
			Toast.makeText(this,"当前无可用网络连接", Toast.LENGTH_SHORT).show();
		}else{	
			 new Thread(new Runnable() {
		         @Override
		         public void run() {
		        	 try {
		        	 //UserHttpClient YcHttpClient = null;
		        	 ReturnData iis = ClientService.sendpost(Declare.LOGIN+"?name="+URLEncoder.encode(username,"UTF-8")+"&pass="+URLEncoder.encode(password,"UTF-8"));
		        	 if(iis.mark.equals("ok")){
			        	 // 使用BufferedReader对象读取返回的数据流
					     // 按行读取，存储在StringBuider对象response中
					     BufferedReader reader = new BufferedReader(new InputStreamReader(iis.is));
					     //StringBuilder response = new StringBuilder();
					     String line;
					     JSONObject result = null;
					     //JSONArray jsArray=null;
					     while ((line = reader.readLine()) != null) {
					         //response.append(line);
					    	 try {
					    		 result = new JSONObject( line);
					    	 } catch (JSONException e) {
					    			// TODO 自动生成的 catch 块
					    		 e.printStackTrace();
					    	 }
					      }
					      String token="";
					      String errorcode=result.getString("Error");
					      if(errorcode.equals("null")){
					    	  result=result.getJSONObject("Result");
					    	  try {
						    	  token = result.getString("Token");
						    	  Declare.islogin=true;
						    	  Message msg = new Message();
					              msg.what = 0;
					              handler.sendMessage(msg);
						      } catch (JSONException e) {
										// TODO 自动生成的 catch 块
						    	  e.printStackTrace();
						      }  
						      if(token!=null){
						    	  Declare._token=token;
						      }
						      //
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
		        }// finally {}
		     }
		}).start();
	}
		
	}
	@Override
	protected void onResume(){
		super.onResume();
		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		MainActivity.mUserHelper.closeLink();
	}
	
	
	
}
