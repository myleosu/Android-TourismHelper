package com.example.showMap.MainActivities.ShowWeather;

import android.util.ArrayMap;

import com.example.showMap.R;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Created by woxingxiao on 2017-07-10.
 */

public class WeatherModel {


    private int code;
    private String describe;
    private String tempNow;
    private String tempMin;
    private String tempMax;
    private String aqiDesc;
    private List<WeatherModel> forecasts;

    private String city;
    private String temperature;//温度
    private String humidity;//湿度
    private String windDirection;//风向
    private String windPower;//风力
    private String weatherNow;//当前天气情况
    private static Map<String,Integer> weatherPicture;//天气和图标对应
    private Integer returnPicture;

    public WeatherModel(String city, int code, String tempNow, String tempMin, String tempMax, String aqiDesc) {
        this.city = city;
        this.code = code;
        this.tempNow = tempNow;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.aqiDesc = aqiDesc;
    }

    public WeatherModel(String city, String temperature, String humidity, String windDirection, String windPower,String weatherNow){
        this.city=city;
        this.temperature=temperature;
        this.humidity=humidity;
        this.windDirection=windDirection;
        this.windPower=windPower;
        this.weatherNow=weatherNow;
    }

    public WeatherModel(int code, String tempMin, String tempMax) {
        this.code = code;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescribe() {
        if (code == 1) {
            describe = "多云";
        } else if (code == 2) {
            describe = "雨";
        } else {
            describe = "晴";
        }
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTempNow() {
        return tempNow;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public String getWeatherNow() {
        return weatherNow;
    }

    public void setWeatherNow(String weatherNow) {
        this.weatherNow = weatherNow;
    }

    public Integer getReturnPicture(String weatherQuery) {
        return weatherPicture.get(weatherQuery);
    }

    public void setReturnPicture(Integer returnPicture) {
        this.returnPicture = returnPicture;
    }

    //初始化天气图标
    public static void setWeatherPicture(){
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

}
