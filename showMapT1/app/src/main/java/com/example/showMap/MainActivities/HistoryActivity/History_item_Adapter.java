package com.example.showMap.MainActivities.HistoryActivity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.showMap.R;

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
                Intent intent = new Intent(parent.getContext(), TripThoughts.class);
                intent.putExtra("Listhistoryid",position);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                parent.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //设置显示历史记录Textview的内容
        History_item history_item = mHistory_item_List.get(position);
        String item_text = history_item.getTour_date()+"\n"
                +history_item.getStartpos()+"("+history_item.getStart_weather()+")"
                +"→"+history_item.getEndpos()+"("+history_item.getEnd_weather()+")"
                +"\n"+"旅程时间："+history_item.getTotaltime()+"min";
        holder.history_textview.setText(item_text );
    }

    //告诉外部布局有多少个历史记录
    @Override
    public int getItemCount() {
        return mHistory_item_List.size();
    }
}
