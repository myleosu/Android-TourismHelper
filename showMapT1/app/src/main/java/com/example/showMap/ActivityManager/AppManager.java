package com.example.showMap.ActivityManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by asus on 2019/3/27.
 */

/**
 * Activity管理类
 */
public class AppManager {

    private static CopyOnWriteArrayList<Activity> mActivityStack ;
    private static AppManager mAppManager ;
    private AppManager(){
    }
    /**
     * 单一实例
     */
    public static AppManager getInstance(){
        if(mAppManager == null){
            mAppManager = new AppManager();
        }
        return mAppManager;
    }
    /**
     * 添加Activity到堆栈中
     */
    public void addActivity(Activity activity){
        if(mActivityStack == null){
            mActivityStack = new CopyOnWriteArrayList<Activity>();
        }
        mActivityStack.add(activity);
    }
    /**
     * 结束指定的Activity
     */
    public void killActivity(Activity activity){
        try{
            if(activity != null){
                mActivityStack.remove(activity);
                activity.finish();
                activity = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 结束指定类名的Activity
     */
    public void killActivity(Class<?> cls){
        for(Activity activity : mActivityStack){
            if(activity.getClass().equals(cls)){
                killActivity(activity);
            }
        }
    }
    /**
     * 结束所有的Activity
     */
    public void killAllActivity(){
        for(Activity activity : mActivityStack){
            killActivity(activity);
        }
        mActivityStack.clear();
    }
    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context){
        try{
            killAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从堆栈中获得指定Activity的context
     * @param cls
     * @return
     */
    public Context getActivitycontext(Class<?> cls){
        for(Activity activity:mActivityStack){
            if(activity.getClass().equals(cls)){
                return activity;
            }
        }
        return null;
    }
}
