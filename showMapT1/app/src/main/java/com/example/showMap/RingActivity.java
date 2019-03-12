package com.example.showMap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by asus on 2019/2/1.
 */

//响铃界面
public class RingActivity extends Activity {

    private MediaPlayer media;//音乐播放器
    private Vibrator vibrator;//设置震动
    private PowerManager.WakeLock mWakelock;//设置屏幕唤醒

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置屏幕唤醒
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //播放音乐
        startMedia();
        //设置震动
        startVibrator();
        //设置弹窗
        createDialog();
    }

    //播放音乐
    private void startMedia(){
        try{
            media = new MediaPlayer();
            media.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            media.prepare();
            media.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //设置震动
    private void startVibrator(){
        vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        long[] pattern = { 500, 1000, 500, 1000 }; // 停止 开启 停止 开启
        //第一个参数pattern表示震动频率，第二个参数0表示循环播放
        vibrator.vibrate(pattern, 0);
    }

    //显示弹窗
    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("闹钟提醒")
                .setMessage("车辆即将抵达目的地")
                .setCancelable(false)//点击窗口外部不退出窗口
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(media!=null)
                            media.stop();
                        vibrator.cancel();
                        finish();
                        intent=new Intent(RingActivity.this,BeforeArroundSearchActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }

    //锁屏状态下唤醒屏幕，要在OnResume()方法中启动，并在OnPause()中释放,不然会出bug，并且在AndroidManifest.xml要添加息屏权限不然会报权限错误
    @Override
    protected void onResume() {
        super.onResume();
        //唤醒屏幕
        acquireWakeLock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //media.release();
        //释放屏幕
        releaseWakeLock();
    }

    /**
     * 唤醒屏幕
     */
    private void acquireWakeLock(){
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        mWakelock.acquire();
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock(){
        mWakelock.release();
    }
}
