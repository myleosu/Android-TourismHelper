package com.example.showMap.MainActivities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.SQLiteDateBaseHpler.MySQLiteOpenHelper;
import com.example.showMap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 陈伟钦 on 2018/8/9.
 *CharSequence是个接口，Sting实现了它，构造 InputtipsQuery 对象，通过 InputtipsQuery(java.lang.String keyword, java.lang.String city) 设置搜索条件。
 构造 Inputtips 对象，并设置监听。
 */

//搜索界面
public class SearchActivity extends Activity implements TextWatcher,View.OnClickListener
{
    public ListView listView;
    private ListView record_history_lv;
    private AutoCompleteTextView autoCompleteTextView;
    private Button btn;
    private Button delete_btn;
    private PoiSearch.Query query; //构造方法设置搜索条件
    private PoiSearch poiSearch;
    private ImageButton back_btn;
//    private PoiAdapter mpoiadapter;
    private PoiResult poiResult;
    private AMap aMap;
    private MapView mapView;
    private String keyword = "";
    private String POI_SEARCH_TYPE = "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务";
    private InputMethodManager inputMethodManager;
    private MySQLiteOpenHelper helper;
    private TextView record_tv;
    private SQLiteDatabase db;
    private Cursor cursor;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        AppManager.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.searchactivity_layout);
        init();
    }
    private void init(){
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.text_view);
        btn = (Button) findViewById(R.id.search_btn);
        back_btn = (ImageButton) findViewById(R.id.back);
        delete_btn = (Button) findViewById(R.id.delete_history_btn_on_SearchActivity);
        listView = (ListView) findViewById(R.id.list_view);
        record_history_lv = (ListView) findViewById(R.id.record_history_lv);
        record_tv = (TextView) findViewById(R.id.textView);
        helper = new MySQLiteOpenHelper(this,"Search_data.db",null,1);
        back_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        autoCompleteTextView.addTextChangedListener(this);
        if(autoCompleteTextView.getText().toString().trim().length()>0)//显示搜索按钮
            btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(this);
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override//设置软键盘上的搜索按钮的监听事件
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                listView.setVisibility(View.INVISIBLE);
                String content = autoCompleteTextView.getText().toString().trim();
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(content.length()<=0){
                        Toast.makeText(SearchActivity.this,"请输入搜索地址", Toast.LENGTH_SHORT).show();
                    }
                   // else QueryData(content);
                }
                return true;
            }
        });
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!s.toString().trim().equals("")) {
            btn.setVisibility(View.VISIBLE);
            String newText = s.toString().trim();
            InputtipsQuery inputtipsQuery = new InputtipsQuery(newText, "");
            //inputtipsQuery.setCityLimit(true);////限制在当前城市
            final Inputtips inputTips = new Inputtips(SearchActivity.this, inputtipsQuery);
            inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                @Override
                public void onGetInputtips(final List<Tip> tiplist, int i) {
                    if (i == AMapException.CODE_AMAP_SUCCESS) {
                        final List<HashMap<String, Object>> listStr = new ArrayList<HashMap<String, Object>>();
                        for (int m = 0; m < tiplist.size(); m++) {
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("img1", R.drawable.listview_left);
                            map.put("name", tiplist.get(m).getName());
                            map.put("address", tiplist.get(m).getDistrict());
                            map.put("img2", R.drawable.listview_right);
                            listStr.add(map);
                        }
                        SimpleAdapter adapter = new SimpleAdapter(SearchActivity.this, listStr, R.layout.listview_layout, new String[]{"img1", "name", "address", "img2"}, new int[]{R.id.img_left, R.id.poi_field_id, R.id.poi_value_id, R.id.img_right});
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                Intent intent = new Intent(SearchActivity.this,ShowInfoActivity.class); //实时信息显示界面
                                intent.putExtra("0",(String)tiplist.get(position).getName());
                                intent.putExtra("1",(String)tiplist.get(position).getDistrict());
                                startActivity(intent);
                            }
                        });
                        adapter.notifyDataSetChanged(); //更新数据
                    } else
                        Toast.makeText(getApplicationContext(), "似乎出了某些问题：" + i, Toast.LENGTH_SHORT).show();
                }
            });
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        /*输入完成还没按按钮，就进行模糊查询*/
        String content = autoCompleteTextView.getText().toString().trim();
        QueryData(content);
    }
    /*
        模糊查询，如果content为"",也就是输入框没内容，注意此时搜索到的模糊查询结果应该是所有的内容都匹配到的，此时，如果数据库
        有数据，就可以显示历史搜索
     */
    private void QueryData(String content){
        /*进行模糊查询数据库，输入完成可以查询模糊数据有没有在数据库中，尝试显示搜索列表（如果查询不到cursor会为空，则不会显示）*/
        cursor = helper.getReadableDatabase().rawQuery("select id as _id,keyword from search_records where keyword like ? order by id desc",new String[]{"%content%"});
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[]{"keyword"}, new int[]{R.id.textView}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        record_history_lv.setAdapter(adapter);
        record_history_lv.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        //Toast.makeText(MainActivity.this, "sss"+cursor.getCount(), Toast.LENGTH_SHORT).show();
        if(content.equals("")&&cursor.getCount()!=0) {
            delete_btn.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else
            delete_btn.setVisibility(View.INVISIBLE);
        cursor.close();
    }
    @Override
    public void onClick(View v) {
        //先将软键盘隐藏
//        inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        switch (v.getId()){
            case R.id.back:
                /*返回上一个活动,使用Intent*/
                //onDestroy();break;
                Toast.makeText(SearchActivity.this,"返回上一活动", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search_btn:
                insertData(autoCompleteTextView.getText().toString().trim());
                Intent intent = new Intent(SearchActivity.this,ShowInfoActivity.class); //实时信息显示界面
                startActivity(intent);
                break;
            case R.id.delete_history_btn_on_SearchActivity:
                DeleteRecords();
                delete_btn.setVisibility(View.GONE);
                break;
        }
    }
    private void DeleteRecords(){
        db = helper.getWritableDatabase();
        db.execSQL("delete from search_records");
        db.close();
    }
    private boolean hasDataRecords(String records){
        cursor = helper.getReadableDatabase().rawQuery("select keyword from search_records where keyword = ?",new String[]{records});
        return cursor.moveToNext();
    }
    private void insertData(String data) {
        if (!hasDataRecords(data)) {
            db = helper.getWritableDatabase();
            db.execSQL("insert into search_records(keyword) values('" + data + "')");
            db.close();
        }
    }
    //Poi没有搜索到数据，返回一些建议城市信息
    private void showSuggestCity(List<SuggestionCity> cities) {
        String information = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            information += "城市名称：" + cities.get(i).getCityName() + "城市区号：" + cities.get(i).getCityCode() + "城市编码：" + cities.get(i).getAdCode() + "\n";
        }
        Toast.makeText(SearchActivity.this, information, Toast.LENGTH_SHORT) .show();
    }

    protected void search() {
        keyword = autoCompleteTextView.getText().toString().trim();
        query = new PoiSearch.Query(keyword, POI_SEARCH_TYPE, "");//设置搜索条件：搜索的关键字、类型、搜索城市
        query.setPageSize(30);
        query.setPageNum(0);
        poiSearch = new PoiSearch(SearchActivity.this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int rCode){
            if (rCode == AMapException.CODE_AMAP_SUCCESS) //如果返回码表示查询成功
                {
                    if (result != null && result.getQuery() != null)  //query对象是我们输入要查询创建的对象，result的query对象可能不是我们要查询的对象
                    {
                        if (result.getQuery().equals(query)) {
                            poiResult = result;
                            List poiItems = result.getPois(); //返回一个PoiItem列表(第一页)
                            //如果搜索不到城市，那么就返回建议的城市
                            List<SuggestionCity> SuggestionCities = poiResult.getSearchSuggestionCitys();
                                if (poiItems != null && poiItems.size() > 0) {
                                //mpoiadapter = new PoiAdapter(MainAcivity.this, poiItems);
                                //listView.setAdapter(mpoiadapter);
                                listView.setVisibility(View.VISIBLE);
                            } else if (SuggestionCities != null) {
                                showSuggestCity(SuggestionCities);
                            } else
                                Toast.makeText(SearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override//每一个poiItem都会调用一次该方法
            public void onPoiItemSearched(PoiItem poiItem, int rCode) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }
}









