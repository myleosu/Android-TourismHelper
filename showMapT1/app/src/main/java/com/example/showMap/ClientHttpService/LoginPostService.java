package com.example.showMap.ClientHttpService;

import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by asus on 2019/3/25.
 */

/**
 * 向服务器发送POST请求，申请登录账号
 */

public class LoginPostService {
    static int LOGIN_FAILED = 0;
    static int LOGIN_SUCCEEDED = 1;
    public static int send(List<NameValuePair> params){
        //返回值
        int responseInt = LOGIN_FAILED;
        //定位服务器的Servlet
        String servlet = "LoginServlet";
        //通过Post方式获取HTTP服务器数据
        String responseMsg;
        responseMsg = MyHttpPost.executeHttpPost(servlet,params);
        Log.i("tag","LoginService:responseMsg = " + responseMsg);
        //解析服务器数据，返回相应的Long值
        if(responseMsg.equals("SUCCEEDED")){
            responseInt = LOGIN_SUCCEEDED;
        }
        return responseInt;
    }
}
