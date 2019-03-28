package com.example.showMap.MainActivities.ArroundActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Freff on 2019/3/3.
 * 上面是周边信息的地址名，
 * 左下为距离，右下为哪个区
 */

public class ArroundSearchActivity extends Activity implements PoiSearch.OnPoiSearchListener,View.OnClickListener,AdapterView.OnItemClickListener{
    private Intent intent;
    private String type_search="";
    private double current_latitude=0;
    private double current_longitude=0;
    private String current_city="";
    private android.support.v7.widget.Toolbar toolbar;
    private ListView listView;

    private PoiSearch poiSearch;
    private PoiSearch.Query query;
    private PoiResult poiResult;
    private List<PoiItem> poiItems;
    private SimpleAdapter simpleAdapter;
    private LatLonPoint lp;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.arround_search_layout);
        init();
    }

    private void init(){
        listView=findViewById(R.id.arround_search_listview);
        intent=getIntent();
        type_search=intent.getStringExtra("TYPE_ARROUND");
        current_city=intent.getStringExtra("CURRENT_CITY");
        current_latitude=intent.getDoubleExtra("CURRENT_LATITUDE",0);
        current_longitude=intent.getDoubleExtra("CURRENT_LONGTITUDE",0);
        SearchArround();
        listView.setOnItemClickListener(this);
    }
    private void SearchArround(){
        if(!current_city .equals("")) {
            query = new PoiSearch.Query("",type_search, current_city);
            query.setPageSize(20);
            query.setPageNum(0);
            query.setCityLimit(true);
            if(current_longitude == 0 && current_latitude == 0){
                Toast.makeText(ArroundSearchActivity.this, "位置信息未打开！", Toast.LENGTH_SHORT).show();
            }
            else {
                lp = new LatLonPoint(current_latitude,current_longitude);
                if(lp!=null) {
                    poiSearch = new PoiSearch(ArroundSearchActivity.this, query);
                    poiSearch.setBound(new PoiSearch.SearchBound(lp, 10000, true));
                    poiSearch.setOnPoiSearchListener(this);
                    poiSearch.searchPOIAsyn();
                }
            }
        }
        else
            Toast.makeText(ArroundSearchActivity.this,"似乎出了什么问题...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPoiSearched(PoiResult result, int i) {
        if(i == AMapException.CODE_AMAP_SUCCESS)
        {
            if(result != null && result.getQuery() != null)
            {
                if(result.getQuery().equals(query))
                {
                    poiResult = result;
                    poiItems = poiResult.getPois();
                    List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
                    if(poiItems!=null && poiItems.size()>0)
                    {
                        for(int k =0; k<poiItems.size(); k++)
                        {
                            Map<String, Object> item = new HashMap<String,Object>();
                            item.put("title",poiItems.get(k).getTitle());
                            item.put("distance",poiItems.get(k).getDistance()+"米");
                            item.put("address",poiItems.get(k).getAdName());
                            item.put("route_icon", R.drawable.want_to_go);
                            list.add(item);
                        }
                        simpleAdapter = new SimpleAdapter(ArroundSearchActivity.this,list, R.layout.arround_search_lv_item,new String[]{"title","distance","address","route_icon"},new int[]{R.id.local_tv_up, R.id.local_tv_down_left, R.id.local_lv_down_right, R.id.local_lv_route});
                        listView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
                    }
                    else
                        Toast.makeText(ArroundSearchActivity.this, "似乎出了什么问题...", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(ArroundSearchActivity.this, "搜索不到任何记录", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onClick(View view) {
        Toast.makeText(ArroundSearchActivity.this, "待开发", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(ArroundSearchActivity.this, "待开发", Toast.LENGTH_SHORT).show();
    }
}
