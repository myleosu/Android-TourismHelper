package com.example.showMap.MainActivities.ArroundActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.R;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

/**
 * Created by Freff on 2019/3/3.
 */

public class BeforeArroundSearchActivity extends Activity implements View.OnClickListener,LocationSource,AMapLocationListener {

    private static int index = 0;
    private static int[] images = new int[]{R.drawable.trip,R.drawable.oil_add_station,R.drawable.bus_station,R.drawable.bar,R.drawable.parking_lot,R.drawable.food,R.drawable.movie};
    static int index_info = 0;
    static String[] type = {"旅游景点","加油站","公交车站","酒吧","停车场","餐饮服务","电影院"};

    private Intent intent;

    private AMap aMap;
    private UiSettings uiSettings;
    private  MyLocationStyle myLocationStyle;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;

    private InputMethodManager imm;

    private double Current_Latitude = 0; //经度
    private double Current_Longitude = 0;   //纬度
    private LatLonPoint lp = null; //包裹经纬度
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private PoiResult poiResult;

    private String Current_City = "";
    private String Place_Name="";
    private String District="";
    private int Distance=0;

    private AutoCompleteTextView autoCompleteTextView;
    private ImageButton imageButton;
    private MapView mapView;
    private BoomMenuButton boomMenuButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.before_arround_search_layout);
        init();
        mapView.onCreate(savedInstanceState);
        TypeLocation();
            for (int i =0 ; i < boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
                SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder()
                        .normalImageRes(getImages())
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {
                                    Toast.makeText(BeforeArroundSearchActivity.this, "正在搜索附近的" + type[index], Toast.LENGTH_SHORT).show();
                                    intent = new Intent(BeforeArroundSearchActivity.this, ArroundSearchActivity.class);
                                    intent.putExtra("TYPE_ARROUND", type[index]);
                                    intent.putExtra("CURRENT_LATITUDE", Current_Latitude);
                                    intent.putExtra("CURRENT_LONGTITUDE", Current_Longitude);
                                    intent.putExtra("CURRENT_CITY", Current_City);
                                    startActivity(intent);
                            }
                        });
                boomMenuButton.addBuilder(builder);
        }
    }

    static int getImages(){
        if(index>=images.length)
            index = 0;
        return images[index++];
    }
    static String getType(){
        if(index_info>=type.length)
            index_info = 0;
        return type[index_info++];
    }

    private void init(){
        autoCompleteTextView=findViewById(R.id.before_arround_search_text_view);
        imageButton=findViewById(R.id.before_arround_search_backbtn);
        mapView=findViewById(R.id.before_arround_search_mapview);
        boomMenuButton=findViewById(R.id.before_arround_search_bmbtn);
        imageButton.setOnClickListener(this);
        autoCompleteTextView.setOnClickListener(this);
        autoCompleteTextView.setInputType(InputType.TYPE_NULL);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        boomMenuButton.setButtonEnum(ButtonEnum.SimpleCircle);
        boomMenuButton.setPiecePlaceEnum(PiecePlaceEnum.DOT_7_4);
        boomMenuButton.setButtonPlaceEnum(ButtonPlaceEnum.SC_7_4);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.before_arround_search_text_view:
                if(imm!=null) {
                    if (imm.isActive()) {
                        imm=null;
                    }
                }
                Toast.makeText(getApplicationContext(), "请点击下方红色按钮进行选择", Toast.LENGTH_SHORT).show();
                break;
            case R.id.before_arround_search_backbtn:
                Toast.makeText(getApplicationContext(),"未开发",Toast.LENGTH_SHORT).show();break;
        }
    }

    private void TypeLocation() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        uiSettings = aMap.getUiSettings();
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.interval(1000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        uiSettings.setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        aMap.moveCamera(CameraUpdateFactory.zoomTo(80));//设置地图的放缩级别
        //setting.setZoomControlsEnabled(false);//取消地图缩放按钮
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);//设置为true表示启动显示定位层并可触发定位，false表示隐藏定位蓝点并不可触发定位，默认是false
    }
    /**
     * 激活定位源，设置定位初始化及启动定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if(mlocationClient == null)     //若定位客户端未初始化
        {
            mlocationClient = new AMapLocationClient(getApplicationContext());//getApplicationContext()传入，全线程有效
            mLocationOption = new AMapLocationClientOption();//初始化定位参数
            mlocationClient.setLocationListener(this); //在开始过程先设置定位客户端的回调监听保证数据完整
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置为高精度定位模式
            mlocationClient.setLocationOption(mLocationOption); //设置定位参数
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }
    /**
     * 停止定位的相关调用
     */
    @Override
    public void deactivate() {
        mListener = null;//返回初始状态
        if (mlocationClient != null) {
            mlocationClient.stopLocation();     //停止定位后，本地定位服务并不会被销毁
            mlocationClient.onDestroy();// 销毁定位客户端。
            //销毁定位客户端之后，若要重新开启定位，需重新New一个AMapLocationClient对象。
        }
        mlocationClient = null;//返回初始状态
    }

    /**
     * 定位结束定位客户端的回调， mlocationClient.setLocationListener(this)发起
     *
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener!=null && aMapLocation!=null)  //aMapLocation是定位的结果类
        {
            Current_City = aMapLocation.getCity();
            Current_Latitude =  aMapLocation.getLatitude();
            Current_Longitude = aMapLocation.getLongitude();
            mListener.onLocationChanged(aMapLocation);  // 显示系统小蓝点
            if (aMapLocation.getErrorCode() == 0) {//解析aMapLocation获取相应内容
                autoCompleteTextView.setHint("搜索\""+aMapLocation.getPoiName()+"\"周边");
            }
            else {
                //定位失败,通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息
                Toast.makeText(BeforeArroundSearchActivity.this,"AmapError"+"\nlocation Error, ErrCode:" + aMapLocation.getErrorCode() + "\nerrInfo:" + aMapLocation.getErrorInfo(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;//返回初始状态
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        AppManager.getInstance().killActivity(this);
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
