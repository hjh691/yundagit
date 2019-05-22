package com.example.substationtemperature.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;
/**
 * 单例模式，为解决Http协议无状态，模拟一次持久的链接状态
 * 构造方法私有化  对外提供一个getHttpClient方法，
 * 并且让这个方法同步，来解决多线程访问的问题
 * @author dell
 *
 */
public class UserHttpClient {
    private static final String CHARSET = HTTP.UTF_8;
    private static HttpClient userHttpClient;

    private static final String TAG = "userHttpClient";
    //构造方法私有化
    private UserHttpClient() {
    }
    //对外提供getHttpClient方法
    public static synchronized HttpClient getHttpClient() {
        //如果userHttpClient == null  即原来没有 userHttpClient 第一次链接
        if (null == userHttpClient) {
            // This interface represents a collection of HTTP protocol parameters
            //这是一个Http协议的集合，里面存放了Http协议的相应参数信息
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  //设置协议的版本
            HttpProtocolParams.setContentCharset(params, CHARSET); //设置内容的编码集
            HttpProtocolParams.setUseExpectContinue(params, true); //
            //HttpProtocolParams
            //        .setUserAgent(
            //                params,
            //                "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
            //                        + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // 超时设置
            /* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 1000);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 4000);
         

            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            userHttpClient = new DefaultHttpClient(conMgr, params);
        }
        return userHttpClient;
    }
    /**
     * 
     * @param url:访问的地址
     * @param params ：要传的参数
     * @return ：服务器返回一个输入流
     */
    public static InputStream post(String url, List<NameValuePair> params) {
        try {
            // 编码参数
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
            if (params != null && params.size() > 0) {
                for (NameValuePair p : params) {
                    formparams.add(p);
                }
            }
            //创建一个实体，里面存放了要传送的数据 并且对数据进行了编码 ：CHARSET -- > utf-8
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,    CHARSET);
            // 创建POST请求
            HttpPost request = new HttpPost(url);
            request.addHeader("_token", "dddirnfag876");
            //在请求里把要传送的数据放进去
            request.setEntity(entity);
            
            
            // 获取唯一的一个HttpClient实例... 单例模式
            HttpClient client = getHttpClient();
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity = response.getEntity();
            return (resEntity == null) ? null : resEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
           // throw new RuntimeException("连接失败", e);
        	
        }
		return null;
    }
}
