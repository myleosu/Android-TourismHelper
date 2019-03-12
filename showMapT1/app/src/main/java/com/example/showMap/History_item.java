package com.example.showMap;

/**
 * Created by asus on 2019/3/2.
 */

public class History_item {
    /*
        每一个历史感想包括1日期2起始地地点3目的地地点4起始地天气5目的地天气6旅程总耗时
     */
    private String date;
    private String startpos;
    private String endpos;
    private String start_weather;
    private String end_weather;
    private String totaltime;
    private String memory;

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public History_item(String date, String startpos, String endpos, String start_weather, String end_weather, String totaltime) {
        this.date = date;
        this.startpos = startpos;
        this.endpos = endpos;
        this.start_weather = start_weather;
        this.end_weather = end_weather;
        this.totaltime = totaltime;
    }

    public History_item(){}

    public String getDate() {
        return date;
    }

    public String getStartpos() {
        return startpos;
    }

    public String getEndpos() {
        return endpos;
    }

    public String getStart_weather() {
        return start_weather;
    }

    public String getEnd_weather() {
        return end_weather;
    }

    public String getTotaltime() {
        return totaltime;
    }

}
