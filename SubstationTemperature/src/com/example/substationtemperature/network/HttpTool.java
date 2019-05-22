package com.example.substationtemperature.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTool {
    public static void sendRequest(final String address, 
            final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(6000);
                    connection.setReadTimeout(6000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();      String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调方法 onFinish()
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        // 回调方法 onError()
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    
    //系统调用格式：
   /* String url = "http://www.cnblogs.com/gzdaijie";
	HttpTool.sendRequest(url,new HttpCallbackListener(){
	    @Override  
	    public void onFinish(String response) {
	        // ...省略对返回结果的处理代码  
	    }  
	    
	    @Override  
	    public void onError(Exception e) {   
	        // ...省略请求失败的处理代码
	    } 
	});*/
}  
