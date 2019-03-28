package com.example.showMap.MainActivities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.*;
import com.amap.api.location.*;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.showMap.ActicityTools.MyActivityTools;
import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.ClientHttpService.InserthistoryPostService;
import com.example.showMap.GetSystemPermissions.PermissionsActivity;
import com.example.showMap.MainActivities.HistoryActivity.History_item;
import com.example.showMap.MainActivities.RingActivityBroadCastReceiver.AlarmReceiver;
import com.example.showMap.MainActivities.ShowWeather.BaseWeatherPanelView;
import com.example.showMap.MainActivities.ShowWeather.Util;
import com.example.showMap.MainActivities.ShowWeather.WeatherModel;
import com.example.showMap.MainActivities.ShowWeather.WeatherPanelView;
import com.example.showMap.R;
import com.google.gson.Gson;
import com.xw.repo.supl.ISlidingUpPanel;
import com.xw.repo.supl.SlidingUpPanelLayout;

//import org.litepal.LitePal;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xw.repo.supl.SlidingUpPanelLayout.COLLAPSED;
import static com.xw.repo.supl.SlidingUpPanelLayout.EXPANDED;
import static com.xw.repo.supl.SlidingUpPanelLayout.HIDDEN;


//实时信息显示界面
public class ShowInfoActivity extends PermissionsActivity implements LocationSource, AMapLocationListener,com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener,RouteSearch.OnRouteSearchListener, GeocodeSearch.OnGeocodeSearchListener{

    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    String buildingname = null;//搜索界面传进来的建筑物名
    String cityDistrict = null;//搜索界面传进来的行政区域名
    private String currentCity="佛山市";//当前城市 默认佛山
    private String currentRoad;//当前街道
    private String goalCity = "深圳市";//目的地城市  默认深圳

    Map<String,Integer> weatherPicture;//天气和图标对应

    private double currentLatitude = 23.05;//当前位置纬度 默认佛山
    private double currentLongitude = 113.11;//当前位置经度
    private double goalLatitude = 22.62;//目的地位置纬度   默认深圳
    private double goalLongitude = 114.07;//目的地位置经度
    private float wholeDistance = 1;//记录起点到终点的距离
    private long lastWholeTime = Long.MAX_VALUE;//记录上次的时间,初始值为LONG的最大值
    private boolean isFirstRouteSearch = true;//标记是否为第一次进行路线规划,第一次路线规划的长度作为总长
    private String[] currentCityInfo = new String[4];//当前城市的信息，string[0]为温度，string[1]为湿度,string[2]为风向,string[3]为天气状况
    private String[] goalCityInfo = new String[4];//目的地城市的信息，string[0]为温度，string[1]为湿度,string[2]为风向,string[3]为天气状况
    private boolean isSetClock = false;//标记是否设置了闹钟提醒
    private boolean isSetHistory = false;//标记是否插入了历史记录

    //进度条相关设置
    private ProgressBar progesss;
    private TextView progesssValue;
    private LinearLayout full;

    /**
     * 弹窗部分设置
     */
    @BindView(R.id.sliding_up_panel_layout)
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindView(R.id.bg_layout)
    View mBgLayout;
    private WeatherModel currentCityWeather = new WeatherModel("佛山","24","30","东风","<三级","中雨");
    private WeatherModel goalCityWeather = new WeatherModel("深圳","25","31","东风1","<三级1","多云");;
    private List<WeatherModel> mWeatherList = new ArrayList<>();
    private String lastCurrentCity="first",lastGoalCity="first";
    private String[] lastCurrentCityInfo = new String[]{"first","first","first","first"};//当前城市的信息，string[0]为温度，string[1]为湿度,string[2]为风向,string[3]为天气状况
    private String[] lastGoalCityInfo = new String[]{"first","first","first","first"};//目的地城市的信息，string[0]为温度，string[1]为湿度,string[2]为风向,string[3]为天气状况

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);

        Intent intent = getIntent();
        buildingname = intent.getStringExtra("0");
        cityDistrict = intent.getStringExtra("1");

        setContentView(R.layout.showinfoactivity_new_layout);

        //进度条
        progesss = (ProgressBar) findViewById(R.id.progesss1);
        progesssValue = (TextView) findViewById(R.id.progesss_value1);
        full = (LinearLayout) findViewById(R.id.full);

        setBoldText();//设置标题字体加粗
        setWeatherPicture();//初始化天气图标

        //对目的地经纬度进行地理正编码
        GeocodeSearch(cityDistrict+buildingname);

        initLoc();//定位,天气信息，路线规划初始化   ！！！旅程途中，更新数据时调用这个函数就ok了
        initNew();//弹窗界面的初始化设置

    }

    //设置目的地信息
    public void setGoalInfo(String goalCity){
        this.goalCity = goalCity;
    }

    //设置进度条
    private void setProgesssView(int pass) {
        progesss.setProgress(pass);
        progesssValue.setText(new StringBuffer().append(progesss.getProgress()).append("%"));
        setPosWay1();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setPos();
        }
    }
    private void setPosWay1() {
        progesssValue.post(new Runnable() {
            @Override
            public void run() {
                setPos();
            }
        });
    }
    //设置进度显示在对应的位置
    public void setPos() {
        int w = getWindowManager().getDefaultDisplay().getWidth();
        //Log.e("w=====", "" + w);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) progesssValue.getLayoutParams();
        int pro = progesss.getProgress();
        int tW = progesssValue.getWidth();
        if (w * pro / 100 + tW * 0.3 > w) {
            params.leftMargin = (int) (w - tW * 1.1);
        } else if (w * pro / 100 < tW * 0.7) {
            params.leftMargin = 0;
        } else {
            params.leftMargin = (int) (w * pro / 100 - tW * 0.7);
        }
        progesssValue.setLayoutParams(params);
    }

    //定位,计算路线
    private void initLoc(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息，默认返回
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认强制
        mLocationOption.setWifiScan(true);
        //设置定位间隔  ms
        mLocationOption.setInterval(2000);
        //设置定位一次
        //mLocationOption.setOnceLocation(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        //设置路线 起点(纬度，经度)，终点(纬度，经度)
    }

    //定位回调函数 必须重写
    public void onLocationChanged(AMapLocation aMapLocation){
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功
                //可在其中解析amapLocation获取相应内容。
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                aMapLocation.getAoiName();//获取当前定位点的AOI信息
                aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                aMapLocation.getFloor();//获取当前室内定位的楼层
                aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                //信息修改
                currentLatitude = aMapLocation.getLatitude();//当前纬度
                currentLongitude = aMapLocation.getLongitude();//当前经度

                currentCity = aMapLocation.getCity();//当前城市
                currentRoad = aMapLocation.getStreet();//当前街道

                setWeather(currentCity);//设置当前位置的天气信息 ！！！位置最好别动

                //设置弹窗部分的天气信息
                if(createAgain()){
                    loadData(currentCityWeather,goalCityWeather);
                }

                if(!isFirstRouteSearch){//非首次路径规划
                    setWeather(goalCity);//设置目的地位置的天气信息

                    //setWeatherRemind();//设置提醒语

                    //计算当前位置与目的地的行程信息
                    setRouteSearch(new LatLonPoint(currentLatitude,currentLongitude),new LatLonPoint(goalLatitude,goalLongitude));
                }
//                //获取定位时间
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener){
        mListener = listener;
    }

    //停止定位
    @Override
    public  void deactivate(){
        mListener = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        //mMapView.onDestroy();
        //当信息显示界面退出时取消闹钟提醒
        Intent intent = new Intent(ShowInfoActivity.this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);
        Toast.makeText(this,"闹钟被取消",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        //mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        //mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        //mMapView.onSaveInstanceState(outState);
    }

    /**
     * 设置当前城市和目的地城市的天气情况
     */
    private void loadData(WeatherModel currentCityWeather,WeatherModel goalCityWeather) {
        mWeatherList.clear();

        WeatherModel.setWeatherPicture();

        currentCityWeather.setCode(0);//设置不同城市的弹窗颜色
        mWeatherList.add(currentCityWeather);

        goalCityWeather.setCode(1);//设置不同城市的弹窗颜色
        mWeatherList.add(goalCityWeather);

        /**
         * 记录当前页面的城市天气信息
         */
        lastCurrentCity=currentCityWeather.getCity();
        lastGoalCity=goalCityWeather.getCity();
        lastCurrentCityInfo[0]=currentCityWeather.getTemperature();
        lastCurrentCityInfo[1]=currentCityWeather.getHumidity();
        lastCurrentCityInfo[2]=currentCityWeather.getWindDirection();
        lastCurrentCityInfo[3]=currentCityWeather.getWeatherNow();
        lastGoalCityInfo[0]=goalCityWeather.getTemperature();
        lastGoalCityInfo[1]=goalCityWeather.getHumidity();
        lastGoalCityInfo[2]=goalCityWeather.getWindDirection();
        lastGoalCityInfo[3]=goalCityWeather.getWeatherNow();


        mSlidingUpPanelLayout.setAdapter(new SlidingUpPanelLayout.Adapter() {

            private final int mSize = mWeatherList.size();

            @Override
            public int getItemCount() {
                return mSize;
            }

            @NonNull
            @Override
            public ISlidingUpPanel onCreateSlidingPanel(int position) {
                WeatherPanelView panel = new WeatherPanelView(ShowInfoActivity.this);
                panel.setFloor(mSize - position);
                panel.setPanelHeight(mSize == 1 ? Util.dp2px(120) : Util.dp2px(80));
                if (position == 0) {
                    panel.setSlideState(COLLAPSED);
                    panel.setEnabled(false);
                } else {
                    panel.setSlideState(COLLAPSED);
                    panel.setEnabled(true);
                }
                return panel;
            }

            @Override
            public void onBindView(final ISlidingUpPanel panel, int position) {
                if (mSize == 0)
                    return;

                BaseWeatherPanelView BasePanel = (BaseWeatherPanelView) panel;
                BasePanel.setWeatherModel(mWeatherList.get(position));
                BasePanel.setClickable(true);
                BasePanel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (panel.getSlideState() != EXPANDED) {
                            mSlidingUpPanelLayout.expandPanel();
                        } else {
                            mSlidingUpPanelLayout.collapsePanel();
                        }
                    }
                });
            }
        });
    }

    /**
     * 弹窗界面相关设置
     */
    private void initNew(){
        //transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        ButterKnife.bind(this);
        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListenerAdapter() {
            @Override
            public void onPanelExpanded(ISlidingUpPanel panel) {
                if (panel instanceof BaseWeatherPanelView) {
                    int count = mSlidingUpPanelLayout.getChildCount();
                    // 如果被展开的Panel不是距离屏幕顶部最近（floor值最大）那个，做如下处理，再被收起时已是距屏幕顶部最近
                    if (((BaseWeatherPanelView) panel).getFloor() != count - 1) {

                        mSlidingUpPanelLayout.removeView(panel.getPanelView());
                        mSlidingUpPanelLayout.addView(panel.getPanelView(), 1);

                        for (int i = 1; i < count; i++) {
                            BaseWeatherPanelView child = (BaseWeatherPanelView) mSlidingUpPanelLayout.getChildAt(i);
                            child.setFloor(count - i);
                        }
                        mSlidingUpPanelLayout.requestLayout();
                    }
                }

                int count = mSlidingUpPanelLayout.getChildCount();
                for (int i = 1; i < count; i++) {
                    ISlidingUpPanel panel2 = (ISlidingUpPanel) mSlidingUpPanelLayout.getChildAt(i);
                    if (panel2 == panel) {
                        panel2.getPanelView().setEnabled(false);
                    } else {
                        panel2.setSlideState(HIDDEN);
                        panel2.getPanelView().setEnabled(true);
                    }
                }
            }

            @Override
            public void onPanelCollapsed(ISlidingUpPanel panel) {
                int count = mSlidingUpPanelLayout.getChildCount();
                for (int i = 1; i < count; i++) {
                    panel = (ISlidingUpPanel) mSlidingUpPanelLayout.getChildAt(i);
                    panel.setSlideState(COLLAPSED);
                    panel.getPanelView().setEnabled(true);
                }
            }
        });
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resId);
        mBgLayout.setPadding(0, statusBarHeight, 0, 0);
    }

    /**
     * 判断是否重新加载弹窗部分
     */
    private boolean createAgain(){
        if(!lastCurrentCity.equals(currentCity)||!lastGoalCity.equals(goalCity)){
            return true;
        }
        for(int i=0;i<4;i++){
            if(i==2)
                continue;
            if(!lastCurrentCityInfo[i].equals(currentCityInfo[i])||!lastGoalCityInfo[i].equals(goalCityInfo[i])) {
                return true;
            }
        }
        return false;
    }

    //实时天气查询回调
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i == 1000) {
            LocalWeatherLive weatherLive=localWeatherLiveResult.getLiveResult();
            //查询天气信息
            String weatherCity = weatherLive.getCity();
            String weatherWeather = weatherLive.getWeather();
            String weatherTemperature = weatherLive.getTemperature();
            String weatherHumidity = weatherLive.getHumidity();
            String weatherWindDirection = weatherLive.getWindDirection();
            String weatherWindPower = weatherLive.getWindPower();

            String weatherInfoText = weatherCity+":"+weatherWeather+"\n";
            weatherInfoText+=("温度："+weatherTemperature+"℃\n");
            weatherInfoText+=("湿度："+weatherHumidity+"%\n");
            weatherInfoText+=("风向："+weatherWindDirection+"\n");
            weatherInfoText+=("风力："+weatherWindPower+"级\n");

            //设置当前位置和目的地位置的天气信息和图标
            boolean flag1 = false,flag2 = false;
            if(currentCity.equals(weatherLive.getCity())) {

                //存下当前所在地的天气信息，设置提醒语的时候使用
                this.currentCityInfo[0] = weatherTemperature;
                this.currentCityInfo[1] = weatherHumidity;
                this.currentCityInfo[2] = weatherWindPower;
                this.currentCityInfo[3] = weatherWeather;
                //设置弹窗界面的当前地天气
                currentCityWeather.setCode(0);
                currentCityWeather.setCity(weatherCity);
                currentCityWeather.setTemperature(weatherTemperature);
                currentCityWeather.setHumidity(weatherHumidity);
                currentCityWeather.setWindDirection(weatherWindDirection);
                currentCityWeather.setWindPower(weatherWindPower);
                currentCityWeather.setWeatherNow(weatherWeather);
                flag1 = true;
            }
            if(goalCity.equals(weatherLive.getCity())){

                //存在目标所在地的天气信息，设置提醒语的时候使用
                this.goalCityInfo[0] = weatherTemperature;
                this.goalCityInfo[1] = weatherHumidity;
                this.goalCityInfo[2] = weatherWindPower;
                this.goalCityInfo[3] = weatherWeather;
                //设置弹窗界面的当前地天气
                goalCityWeather.setCode(1);
                goalCityWeather.setCity(weatherCity);
                goalCityWeather.setTemperature(weatherTemperature);
                goalCityWeather.setHumidity(weatherHumidity);
                goalCityWeather.setWindDirection(weatherWindDirection);
                goalCityWeather.setWindPower(weatherWindPower);
                goalCityWeather.setWeatherNow(weatherWeather);
                flag2 = true;
            }
            if(!flag1 && !flag2){//既不是目的地也不是所在地
                Log.e("weather","onWeatherLiveSearched "+weatherCity);
                Log.e("weather","onWeatherLiveSearched error no city");
            }
        }
        else{
            Log.e("weather","回调error    "+i);
        }
    }

    //天气预测查询回调
    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    //设置提醒语
    private void setWeatherRemind(){
        if(this.goalCityInfo[3].equals("") || this.currentCityInfo[3].equals("")) return;
        String weatherRemind = "目的地";
        weatherRemind+=this.goalCityInfo[3];
        int goalnumber,currentnumber;
        try{
            goalnumber = Integer.parseInt(this.goalCityInfo[0]);
            currentnumber = Integer.parseInt(this.currentCityInfo[0]);
            if(goalnumber-currentnumber>4) weatherRemind+="，目的地温度稍高";
            else if(goalnumber-currentnumber>6) weatherRemind+="，目的地较热";
            else if(goalnumber-currentnumber<-4) weatherRemind+="，目的地温度稍低";
            else if(goalnumber-currentnumber<-6) weatherRemind+="，目的地较冷";

            goalnumber = Integer.parseInt(this.goalCityInfo[1]);
            currentnumber = Integer.parseInt(this.currentCityInfo[1]);
            if(goalnumber>80) weatherRemind+=",目的地较为潮湿";
            else if(goalnumber<20) weatherRemind+="，目的地较为干燥";

            goalnumber = Integer.parseInt(this.goalCityInfo[2].substring(1));
            currentnumber = Integer.parseInt(this.currentCityInfo[2].substring(1));
            if(goalnumber-currentnumber>3) weatherRemind+="，目的地风力较大";

            for(int i = 0;i<goalCityInfo[3].length();i++)
                if(goalCityInfo[3].charAt(i)=='雨') weatherRemind+="记得带伞和增添衣物";

            TextView RemindView = (TextView) findViewById(R.id.tips);
            RemindView.setText(weatherRemind);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Tip:","Error：提醒语错误");
        }
    }

    //设置天气查询，根据城市名称
    private void setWeather(String cityString){
        //获取天气信息
        WeatherSearchQuery weatherQuery = new WeatherSearchQuery(cityString,WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch weatherSearch = new WeatherSearch(this);
        weatherSearch.setQuery(weatherQuery);
        weatherSearch.setOnWeatherSearchListener(this);
        weatherSearch.searchWeatherAsyn();
    }

    //设置标题字体加粗
    private void setBoldText(){
        TextView textView = (TextView)findViewById(R.id.title);
        TextPaint textPaint = textView.getPaint();
        textPaint.setFakeBoldText(true);
    }

    //初始化天气图标
    private void setWeatherPicture(){
        weatherPicture = new ArrayMap<String,Integer>();
        weatherPicture.put("晴",Integer.valueOf(R.drawable.sunny));
        weatherPicture.put("多云",Integer.valueOf(R.drawable.cloudy));
        weatherPicture.put("阴",Integer.valueOf(R.drawable.overcast));
        weatherPicture.put("阵雨",Integer.valueOf(R.drawable.showerrain));
        weatherPicture.put("雷阵雨", Integer.valueOf(R.drawable.thundershower));
        weatherPicture.put("雷阵雨并伴有冰雹",Integer.valueOf(R.drawable.thundershowerwithhail));
        weatherPicture.put("雨夹雪",Integer.valueOf(R.drawable.sleet));
        weatherPicture.put("小雨",Integer.valueOf(R.drawable.lightrain));
        weatherPicture.put("中雨",Integer.valueOf(R.drawable.moderaterain));
        weatherPicture.put("大雨",Integer.valueOf(R.drawable.heavyrain));
        weatherPicture.put("暴雨",Integer.valueOf(R.drawable.storm));
        weatherPicture.put("大暴雨",Integer.valueOf(R.drawable.heavystorm));
        weatherPicture.put("特大暴雨",Integer.valueOf(R.drawable.severestorm));
        weatherPicture.put("阵雪",Integer.valueOf(R.drawable.snowflurry));
        weatherPicture.put("小雪",Integer.valueOf(R.drawable.lightsnow));
        weatherPicture.put("中雪",Integer.valueOf(R.drawable.moderatesnow));
        weatherPicture.put("大雪",Integer.valueOf(R.drawable.heavysnow));
        weatherPicture.put("暴雪",Integer.valueOf(R.drawable.snowstorm));
        weatherPicture.put("雾",Integer.valueOf(R.drawable.foggy));
        weatherPicture.put("冻雨",Integer.valueOf(R.drawable.freezingrain));
        weatherPicture.put("沙尘暴",Integer.valueOf(R.drawable.duststorm));
        weatherPicture.put("小雨-中雨",Integer.valueOf(R.drawable.moderaterain));
        weatherPicture.put("中雨-大雨",Integer.valueOf(R.drawable.heavyrain));
        weatherPicture.put("大雨-暴雨",Integer.valueOf(R.drawable.storm));
        weatherPicture.put("暴雨-大暴雨",Integer.valueOf(R.drawable.heavystorm));
        weatherPicture.put("大暴雨-特大暴雨",Integer.valueOf(R.drawable.severestorm));
        weatherPicture.put("小雪-中雪",Integer.valueOf(R.drawable.moderatesnow));
        weatherPicture.put("中雪-大雪",Integer.valueOf(R.drawable.heavysnow));
        weatherPicture.put("大雪-暴雪",Integer.valueOf(R.drawable.snowstorm));
        weatherPicture.put("浮尘",Integer.valueOf(R.drawable.dust));
        weatherPicture.put("扬沙",Integer.valueOf(R.drawable.sand));
        weatherPicture.put("强沙尘暴",Integer.valueOf(R.drawable.sandstorm));
        weatherPicture.put("飑",Integer.valueOf(R.drawable.tornado));
        weatherPicture.put("龙卷风",Integer.valueOf(R.drawable.tornado));
        weatherPicture.put("弱高吹雪",Integer.valueOf(R.drawable.heavysnow));
        weatherPicture.put("轻霾",Integer.valueOf(R.drawable.haze));
        weatherPicture.put("霾",Integer.valueOf(R.drawable.haze));
    }

    //路线规划，根据起点和终点的经纬度,默认选择驾车方式
    private void setRouteSearch(LatLonPoint from,LatLonPoint to) {
        RouteSearch routeSearch = new RouteSearch(this);
        //模拟起始点与目的经纬度
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(from,to);
        //驾车：第一个参数表示fromAndTo包含路径规划的起点和终点，drivingMode表示驾车模式(支持20种模式  -在PathPlanningStrategy类中定义)
        //第三个参数表示途经点（最多支持16个），第四个参数表示避让区域（最多支持32个），第五个参数表示避让道路
        //模式链接：http://lbs.amap.com/api/android-navi-sdk/guide/route-plan/drive-route-plan

        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
        routeSearch.setRouteSearchListener(this);
    }


    /**
     * 插入一条历史记录
     */
    private void insert_history(String totaltime){
        String username = MyActivityTools.currentusername;
        String password = MyActivityTools.currentpassword;

        //填充一个history对象
        History_item mhistory_item = new History_item();
        mhistory_item.setUsername(username);
        Date curdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateNowStr = sdf.format(curdate);
        mhistory_item.setTour_date(dateNowStr);
        mhistory_item.setStartpos(currentCity);
        mhistory_item.setEndpos(goalCity);
        mhistory_item.setStart_weather(currentCityInfo[3]);
        mhistory_item.setEnd_weather(goalCityInfo[3]);
        mhistory_item.setTotaltime(totaltime);
        mhistory_item.setTour_memory("");
        //将history对象转换成json格式的string
        Gson gson = new Gson();
        String history_json = gson.toJson(mhistory_item);
        new MyActivityTools.InserthistoryPostThread(username,password,history_json).start();
        Handler mhandler = MyActivityTools.myhandler;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    /**
     * 处理返回的GetHistoryThread的信息
     */
    public static void resolve_Gethistory_Handler(Message msg){
        //处理返回线程返回的信息
        if(msg.obj.toString().equals("SUCCEEDED")){
            Log.i("tag:","插入旅程历史记录成功！");
        }else{
            Log.i("tag","向服务器中插入历史记录失败");
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        if (i == 1000) {
            DrivePath drivePath = driveRouteResult.getPaths().get(0);
            //setDrivingRoute(drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
            //策略
            String strategy = drivePath.getStrategy();
            //距离 米：/1000转公里 1公里=1km
            float distance = drivePath.getDistance() / 1000;
            //时间 秒：、60转分
            long duration = drivePath.getDuration() / 60;//单位：min

            //向历史记录中插入一条旅程信息
            if(!isSetHistory && currentCityInfo[3] != null && goalCityInfo[3] != null) {//确保天气状况回调函数返回成功
                isSetHistory = true;
                /**
                 * 插入一条历史记录
                 */
                if(MyActivityTools.isConnectIntent(ShowInfoActivity.this)){
                    insert_history(Long.toString(duration));
                }else{
                    Toast.makeText(ShowInfoActivity.this,"插入旅程记录失败，检查是否已经登录或者是否连接上网络~",Toast.LENGTH_SHORT).show();
                }
            }

            //设置闹钟提醒
            final SharedPreferences.Editor editor = getSharedPreferences("wakeupData",MODE_PRIVATE).edit();
            final SharedPreferences sharedPreferences = getSharedPreferences("wakeupData",MODE_PRIVATE);

            int aheadTime,aheadMile,wakeupTime=10;
            if(sharedPreferences.getInt("wakeupTime",-1)==-1){
                aheadTime = 10;
                aheadMile = 10;
            }else {
                aheadTime = sharedPreferences.getInt("wakeupTime",10);
                aheadMile = sharedPreferences.getInt("wakeupMile",10);
            }
            /**
             * 判断是否要重新设置闹钟
             * 1.距离小于预设距离，重设
             * 2.当前剩余旅程时间小于上次旅程时间的1/2，重设
             */
            if(((distance<=aheadMile+1) || (duration<=lastWholeTime/2)) && !isSetClock){
                //设置提醒。若距离小于预设距离，闹钟设置为0分钟后；
                if(distance<=aheadMile+1 || duration<=aheadTime){
                    wakeupTime = (int)duration;
                    isSetClock = true;
                }else {
                    lastWholeTime = duration;
                    wakeupTime = aheadTime;
                }
                /**
                 * 设置闹钟
                 */
                int lefttime = (int)duration-wakeupTime;//提前wakeupTime分钟提醒
                lefttime = lefttime<0?0:lefttime;
                Toast.makeText(this,"设置闹钟成功，将会在"+Integer.toString(lefttime)+"分钟后提醒您",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShowInfoActivity.this, AlarmReceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());//将时间设定为系统目前的时间
                calendar.add(Calendar.MINUTE, lefttime);//系统时间推迟10分钟，如果为-10，那么就是比系统时间提前十分钟
                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                //根据不同API进行设置闹钟
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() ,sender);
                }else {
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),sender);
                }
            }

            //判断是否为首次路线规划
            if(isFirstRouteSearch){
                wholeDistance = distance;
                isFirstRouteSearch = false;
                //Log.d("wholeDistance:",Double.toString(wholeDistance));
            }
            //Log.d("Distance:",Double.toString(distance));

            float pass=((wholeDistance-distance)/wholeDistance)*100;//根据路程长度计算进度
            setProgesssView(Math.round(pass));//对结果四舍五入取整后调整进度条
            //setProgesssView(50); //对结果四舍五入取整后调整进度条
            //设置进程信息
            //格式化输出
            if(!isFirstRouteSearch) {
                DecimalFormat df = new DecimalFormat("0.00");
                String roadInfoText = "\n旅程进度：" + df.format(pass) + "%\n\n";
                roadInfoText += ("当前城市为：" + currentCity + "\n\n");
                roadInfoText += ("当前道路为：" + currentRoad + "\n\n");
                roadInfoText += ("预计到达时间为：" + duration + "分钟\n");
                TextView roadInfo = (TextView) findViewById(R.id.roadInfo);
                roadInfo.setText(roadInfoText);
            }
        } else {
            Log.e("car", "onDriveRouteSearched: 路线规划失败");
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    //实现GeocodeSearch.OnGeocodeSearchListener接口,根据地理名获取经纬度，正地理编码
    public void GeocodeSearch(String city) {
        //构造 GeocodeSearch 对象，并设置监听。
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        //通过GeocodeQuery设置查询参数,调用getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求。
        //address表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode都ok
        GeocodeQuery query = new GeocodeQuery(city, city);
        geocodeSearch.getFromLocationNameAsyn(query);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == 1000) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                String addressName = "经纬度值:" + address.getLatLonPoint()+"\n位置描述:"
                        + address.getFormatAddress();
                //获取到的经纬度
                goalLatitude = address.getLatLonPoint().getLatitude();
                goalLongitude = address.getLatLonPoint().getLongitude();

                //设置目的地所在城市,示例：广东省深圳市宝安区，需要截取出其中的深圳市,上海市浦东区，需要截取其中的上海市
                int start,end;
                for(end = 0;end<cityDistrict.length();end++)
                    if(cityDistrict.charAt(end)=='市') break;
                for(start = 0;start<cityDistrict.length();start++)
                    if(cityDistrict.charAt(start)=='省') break;
                if(start!=cityDistrict.length() && start<end) setGoalInfo(cityDistrict.substring(start+1,end+1));
                else setGoalInfo(cityDistrict.substring(0,end+1));
                //第一次调用
                setRouteSearch(new LatLonPoint(currentLatitude,currentLongitude),new LatLonPoint(goalLatitude,goalLongitude));
                //南宁市经纬度(22.8167300000,108.3669000000)
                //setRouteSearch(new LatLonPoint(22.8167300000,108.3669000000),new LatLonPoint(goalLatitude,goalLongitude));

                //Log.d("111",addressName);
            }
            else{
                Log.e("获取目的地经纬度Error：","");
            }
        }
    }
}

