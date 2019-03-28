package com.example.showMap.ClientHttpService;

import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by asus on 2019/3/28.
 */

/**
 * 向服务器发送更新history的Post请求
 */

public class UpdatePostService {
    static int HISTORY_FAILED = 4;
    static int HISTORY_SUCCEEDED = 5;
    public static int send(List<NameValuePair> params){
        //返回值
        int responseint = HISTORY_FAILED;
        //定位服务器的Servlet
        String servlet = "UpdatehistoryServlet";
        //通过Post方式获取HTTP服务器数据
        String responseMsg;
        responseMsg = MyHttpPost.executeHttpPost(servlet,params);
        Log.i("tag","UpdatehistoryService:responseMsg = " + responseMsg);
        //解析服务器数据，返回相应的Long值
        if(responseMsg.equals("SUCCEEDED")){
            responseint = HISTORY_SUCCEEDED;
        }
        return responseint;
    }
}
