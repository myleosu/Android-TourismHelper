package com.example.showMap.MainActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.MainActivities.HistoryActivity.History;
import com.example.showMap.MainActivities.LogInAndRegister.HasLogIn;
import com.example.showMap.MainActivities.LogInAndRegister.LogIn;
import com.example.showMap.GetSystemPermissions.PermissionsActivity;
import com.example.showMap.R;

/**
 * Created by 陈伟钦 on 2018/9/28.
 */


//软件的第一个界面，显示地图和搜索栏
public class FirstActivityShowMap extends PermissionsActivity implements View.OnClickListener,LocationSource,AMapLocationListener {
    /**
     * 保存当前用户信息
     */
    static public String username;
    static public String password;

    private InputMethodManager inputMethodManager;
    private MapView mapView;
    private AutoCompleteTextView first_tv;
    private AMap aMap;  //地图的操作
    private MyLocationStyle myLocationStyle; //定位蓝点
    private AMapLocationClient mLocationClient = null; //定位服务类（定位发起的客户端类，可以被缓存）。此类提供单次定位、持续定位、最后位置相关功能。用于初始化定位，setLocationOption()方法传入AMapLocationClientOption类对象可以设置定位参数；
    private AMapLocationClientOption mLocationClientOption = null; //设置定位是否返回地址信息，定位是否等待WIFI列表刷新等,用于初始化定位参数
    private LocationSource.OnLocationChangedListener mListener;  //定位过程的监听器，是activate回调函数的参数
    private UiSettings settings;//设置是否显示定位按钮等

    //外面用DrawerLayout画布布局
    private DrawerLayout mdrawerLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        //转到主界面,从Activity堆栈中移除Login
        AppManager.getInstance().killActivity(LogIn.class);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.firstactivityshowmap_layout);

        //设置acticity启动的时候输入法默认不开启
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /**
         * 创建userdata_sp文件，查看用户是否登录
         */
        SharedPreferences sharedPreferences = getSharedPreferences("userdata_sp",MODE_PRIVATE);
        username = sharedPreferences.getString("username",null);
        password = sharedPreferences.getString("password",null);

        //DrawerLayout实例化
        mdrawerLayout = (DrawerLayout)  findViewById(R.id.drawer_layout);

        //Navigation实例化
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //解决图片加载不出来的问题
        navigationView.setItemIconTintList(null);

        //设置图标按钮
        ImageButton user_imageButton = (ImageButton) findViewById(R.id.first_map_view_user_button);
        user_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开侧栏
                mdrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //设置默认选中用户这一栏
        navigationView.setCheckedItem(R.id.username);
        //设置侧栏的监听事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_user:
                        //查看用户是否已经登录
                        if(username != null && password != null && !username.equals("") && !password.equals(""))
                            startActivity(new Intent(FirstActivityShowMap.this, HasLogIn.class));
                        break;
                    case R.id.nav_history:
                        //启动旅程感想界面记录
                        startActivity(new Intent(FirstActivityShowMap.this,History.class));
                        break;
                    case R.id.nav_setting:
                        startActivity(new Intent(FirstActivityShowMap.this,Setting.class));
                        break;
                    default:
                        mdrawerLayout.closeDrawers();
                }
                return true;
            }
        });

        mapView = (MapView)findViewById(R.id.first_map_view);//找到地图控件
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        first_tv = (AutoCompleteTextView)findViewById(R.id.first_map_view_text_view);
        first_tv.setOnClickListener(this);
        TypeMap();
    }

    @Override
    public void onClick(View v) {
        inputMethodManager =(InputMethodManager)getSystemService(FirstActivityShowMap.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Intent intent = new Intent(FirstActivityShowMap.this,SearchActivity.class);
        startActivity(intent);
    }

    private void TypeMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        //aMap = mapView.getMap();//初始化地图控制器对象
        //设置显示定位按钮 并且可以点击
        settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        //是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        //是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);

        //初始化定位蓝点样式类
        myLocationStyle = new MyLocationStyle();
        //myLocationStyle.interval(1000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        // myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//蓝点只定位一次。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //settings.setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));//设置地图的放缩级别
        //setting.setZoomControlsEnabled(false);//取消地图缩放按钮
        //aMap.setLocationSource(this);
        //aMap.setMyLocationEnabled(true);//设置为true表示启动显示定位层并可触发定位，false表示隐藏定位蓝点并不可触发定位，默认是false

        //开始定位
        initLoc();
    }

    //定位
    private void initLoc() {

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);

        //初始化参数定位
        mLocationClientOption = new AMapLocationClientOption();
        //设置定位模式
        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息，默认返回
        mLocationClientOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认强制
        mLocationClientOption.setWifiScan(true);
        //设置定位间隔  ms
        mLocationClientOption.setInterval(2000);
        //设置定位一次
        mLocationClientOption.setOnceLocation(true);

        //给定客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationClientOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 激活定位源，设置定位初始化及启动定位
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }
    /**
     * 停止定位的相关调用
     */
    @Override
    public void deactivate() {
        mListener = null;//返回初始状态
        if (mLocationClient != null) {
            mLocationClient.stopLocation();     //停止定位后，本地定位服务并不会被销毁
            mLocationClient.onDestroy();// 销毁定位客户端。
            //销毁定位客户端之后，若要重新开启定位，需重新New一个AMapLocationClient对象。
        }
        mLocationClient = null;//返回初始状态
    }

    /**
     * 定位结束定位客户端的回调， mlocationClient.setLocationListener(this)发起
     *
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener!=null && aMapLocation!=null)  //aMapLocation是定位的结果类
        {
            mListener.onLocationChanged(aMapLocation);  // 显示系统小蓝点
            if (aMapLocation.getErrorCode() == 0) {//解析aMapLocation获取相应内容
                //Toast.makeText(FirstActivity.this, "定位成功", Toast.LENGTH_SHORT).show();
            }
            else {
                //定位失败
                Toast.makeText(FirstActivityShowMap.this,"定位系统未打开！" , Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;//返回初始状态
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();//暂停地图的绘制
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }
}
