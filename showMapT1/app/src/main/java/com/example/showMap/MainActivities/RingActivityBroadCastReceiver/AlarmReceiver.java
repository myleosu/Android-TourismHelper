package com.example.showMap.MainActivities.RingActivityBroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.MainActivities.RingActivity;
import com.example.showMap.MainActivities.ShowInfoActivity;

/**
 * Created by asus on 2019/1/30.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "设置闹钟成功~~~", Toast.LENGTH_LONG).show();
        Intent alarmIntent = new Intent(context,RingActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }

}
