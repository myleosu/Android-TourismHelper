package com.example.showMap;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.io.HttpResponseParser;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Created by asus on 2019/3/25.
 */

public class MyHttpPost {
    //服务器地址
    private static String SERVER = "http://47.103.1.213";
    //项目地址
    private static String PROJECT = "/javaweb/";
    //请求超时
    private static final int REQUESET_TIMEOUT = 0;
    //读取超时
    private static final int SO_TIMEOUT = 0;

    //通过POST方式获取HTTP服务器数据
    public static String executeHttpPost(String servlet, List<NameValuePair> params){
        String baseURL = SERVER + PROJECT + servlet;
        Log.i("tag",baseURL);
        String resonseMsg = "FAILED";
        try{
            //连接到服务器端相应的Servlet
            HttpPost request = new HttpPost(baseURL);
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,REQUESET_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams,SO_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode()==200){//是否成功接收信息
                resonseMsg = EntityUtils.toString(response.getEntity());
            }else{
                Log.i("tag","没有成功接收信息");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.i("tag","responMsg = "+resonseMsg);
        return resonseMsg;
    }
}
