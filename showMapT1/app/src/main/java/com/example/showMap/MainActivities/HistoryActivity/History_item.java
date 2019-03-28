package com.example.showMap.MainActivities.HistoryActivity;

/**
 * Created by asus on 2019/3/2.
 */

public class History_item {
    /*
        每一个历史感想包括id和1日期2起始地地点3目的地地点4起始地天气5目的地天气6旅程总耗时
     */
    private int id;
    private String username;
    private String tour_date;
    private String startpos;
    private String endpos;
    private String startweather;
    private String endweather;
    private String totaltime;
    private String tour_memory;

    public History_item() {
    }

    @Override
    public String toString() {
        return "History_item{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", tour_date='" + tour_date + '\'' +
                ", startpos='" + startpos + '\'' +
                ", endpos='" + endpos + '\'' +
                ", startweather='" + startweather + '\'' +
                ", endweather='" + endweather + '\'' +
                ", totaltime='" + totaltime + '\'' +
                ", tour_memory='" + tour_memory + '\'' +
                '}';
    }

    public History_item(int id, String username, String tour_date, String startpos, String endpos, String start_weather, String end_weather, String totaltime, String tour_memory) {
        this.id = id;
        this.username = username;
        this.tour_date = tour_date;
        this.startpos = startpos;
        this.endpos = endpos;
        this.startweather = start_weather;
        this.endweather = end_weather;
        this.totaltime = totaltime;
        this.tour_memory = tour_memory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTour_date() {
        return tour_date;
    }

    public void setTour_date(String tour_date) {
        this.tour_date = tour_date;
    }

    public String getStartpos() {
        return startpos;
    }

    public void setStartpos(String startpos) {
        this.startpos = startpos;
    }

    public String getEndpos() {
        return endpos;
    }

    public void setEndpos(String endpos) {
        this.endpos = endpos;
    }

    public String getStart_weather() {
        return startweather;
    }

    public void setStart_weather(String start_weather) {
        this.startweather = start_weather;
    }

    public String getEnd_weather() {
        return endweather;
    }

    public void setEnd_weather(String end_weather) {
        this.endweather = end_weather;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public String getTour_memory() {
        return tour_memory;
    }

    public void setTour_memory(String tour_memory) {
        this.tour_memory = tour_memory;
    }
}
