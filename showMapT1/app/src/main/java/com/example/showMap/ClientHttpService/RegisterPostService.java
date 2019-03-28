package com.example.showMap.ClientHttpService;

import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by asus on 2019/3/25.
 */

/**
 * 向服务器发送POST请求，申请注册一个账号
 */
public class RegisterPostService {
    static int REGISTER_FAILED = 2;
    static int REGISTER_SUCCEEDED = 3;
    public static int send(List<NameValuePair> params){
        //返回值
        int responseInt = REGISTER_FAILED;
        //定位服务器的Servlet
        String servlet = "RegisterServlet";
        //通过Post方式获取HTTP服务器数据
        String responseMsg;
        responseMsg = MyHttpPost.executeHttpPost(servlet,params);
        Log.i("tag","LoginService:responseMsg = " + responseMsg);
        //解析服务器数据，返回相应的Long值
        if(responseMsg.equals("SUCCEEDED")){
            responseInt = REGISTER_SUCCEEDED;
        }
        return responseInt;
    }
}
