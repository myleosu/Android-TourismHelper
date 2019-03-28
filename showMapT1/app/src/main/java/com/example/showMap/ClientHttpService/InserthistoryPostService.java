package com.example.showMap.ClientHttpService;

import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by asus on 2019/3/27.
 */

/**
 * 向服务器发送POST请求，申请插入一条数据
 */

public class InserthistoryPostService {
    static int HISTORY_FAILED = 4;
    static int HISTORY_SUCCEEDED = 5;
    public static int send(List<NameValuePair> params){
        //返回值
        int resultint = HISTORY_FAILED;
        //定位服务器的Servlet
        String servlet = "InserthistoryServlet";
        //通过Post方式获取HTTP服务器数据
        String responseMsg;
        responseMsg = MyHttpPost.executeHttpPost(servlet,params);
        Log.i("tag","InserthistoryService:responseMsg = " + responseMsg);
        //解析服务器数据，返回相应的Long值
        if(responseMsg.equals("SUCCEEDED")){
            resultint = HISTORY_SUCCEEDED;
        }
        return resultint;
    }
}
