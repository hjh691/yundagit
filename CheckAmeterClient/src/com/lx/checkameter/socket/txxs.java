package com.lx.checkameter.socket;



import com.lx.checkameterclient.Declare;
import com.lx.checkameterclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class txxs extends Activity {
	private boolean socket_flag=false;//socket�����Ϣˢ�±�־
	TextView socketinfo;
	private Thread mThreadinfo = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //ȫ��    
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        //setContentView(R.layout.txxs);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_txxs);
        
        socketinfo=(TextView)findViewById(R.id.socketinfo);
        socketinfo.setMovementMethod(ScrollingMovementMethod.getInstance()); 
        if(mThreadinfo==null)
        {
        socket_flag=true;
        mThreadinfo=new Thread(new socket_msg());
        mThreadinfo.start(); 
        }
	
	}
	public class socket_msg implements Runnable{
    	public void run() {
    		
    		while(socket_flag){
  
    			try{
    			if(Declare.Clientisrun==true){
    				
    				if(Declare.infotip==true)
    				{
    					Declare.infotip=false;

    					Message msg = new Message();
                        msg.what = 1;
        				mHandler.sendMessage(msg);	
    				}
    				
    			}
    			else{
    				
    				if(Declare.infotip==true)
    				{
    					Declare.infotip=false;
    					
    					Message msg = new Message();
                        msg.what = 1;
        				mHandler.sendMessage(msg);	
    				}
    			}
    			
    			if(Declare.Serverisrun==true){
    				
    				if(Declare.infotip1==true)
    				{	
    					Declare.infotip1=false;

    					Message msg = new Message();
                        msg.what = 0;
        				mHandler.sendMessage(msg);	
        				
    				}
    				
    			}
    			else{
    				
    				if(Declare.infotip1==true)
    				{
    					Declare.infotip1=false;
    
    					Message msg = new Message();
                        msg.what = 0;
        				mHandler.sendMessage(msg);	
    				}
    			}
    			
    			
    		}catch ( Exception e ){

    		System.out.println("��Ϣˢ�³���\n" + e.getMessage());
    		return;

    		}

	
    			
    		}
    		
    		
    		
    	}
 }
	Handler mHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {											
			  super.handleMessage(msg);			
			  if(msg.what == 0)
			  {
				  socketinfo.append("Server: "+Declare.recvMessageServer);	// ˢ��
			  }
			  else if(msg.what == 1)
			  {
				  socketinfo.append("Client: "+Declare.recvMessageClient);	// ˢ��

			  }
		  }									
	 };
	   @Override
	    public void onResume() {
	        super.onResume();
	        //Lock_Handle();
	        //Lock_Screen.lock_Screen(getApplicationContext(), this);
	        
	    }
	    @Override
	    public void onPause() {
	        super.onPause();
	        //Lock_Handle();
	        //Lock_Screen.lock_Screen(getApplicationContext(), this);
	        
	    }
	 @Override
		protected void onDestroy() {
	        // TODO Auto-generated method stub  
	        super.onDestroy();
	        socketinfo.setText("");
	        socket_flag=false;
	   	    if(mThreadinfo!=null){	   	    
	        mThreadinfo.interrupt();
	        mThreadinfo=null;
	   	    }
	    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
