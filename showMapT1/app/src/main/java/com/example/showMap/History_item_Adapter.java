package com.example.showMap;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by asus on 2019/3/2.
 */

//继承RecyclerView的Adapter
public class History_item_Adapter extends RecyclerView.Adapter<History_item_Adapter.ViewHolder>{

    //历史记录列表
    private List<History_item> mHistory_item_List;

    //继承Recycler的ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder{
        Button history_thinking_button;
        TextView history_textview;
        View history_view;

        public ViewHolder(View view){
            super(view);
            history_view = view;
            history_thinking_button = (Button) view.findViewById(R.id.history_item_Button);
            history_textview = (TextView) view.findViewById(R.id.history_item_TextView);
        }
    }

    public History_item_Adapter(List<History_item> historyList){
        mHistory_item_List = historyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        //注册旅程感想按钮监听事件
        holder.history_thinking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                History_item history_item = mHistory_item_List.get(position);
                Toast.makeText(view.getContext(),"你点击了第"+position+"个Button",Toast.LENGTH_SHORT ).show();
                final EditText inputhistory_edit_text = new EditText(parent.getContext());
//                inputhistory_edit_text.setInputType(InputType.TYPE_CLASS_TEXT);
                inputhistory_edit_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                inputhistory_edit_text.setText("佛山去深圳的路上，在虎门大桥处有点小塞车，好在塞得不算久，Lucky~~终于可以逃离佛山南海的阴霾天气啦:)拥抱深圳的小晴天~");
                inputhistory_edit_text.setText(History.history_item_List.get(position).getMemory());
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setTitle("旅程感想")
                        .setView(inputhistory_edit_text).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //设置显示历史记录Textview的内容
        History_item history_item = mHistory_item_List.get(position);
        String item_text = history_item.getDate()+":"+history_item.getStartpos()+"("+
                            history_item.getStart_weather()+")--"+history_item.getEndpos()+"("+
                            history_item.getEnd_weather()+")"+"\n"+"旅程总时间: "+history_item.getTotaltime();
        holder.history_textview.setText(item_text );
    }

    //告诉外部布局有多少个历史记录
    @Override
    public int getItemCount() {
        return mHistory_item_List.size();
    }
}
