package com.intfocus.yonghuitest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.bean.tablechart.MainData;
import com.intfocus.yonghuitest.util.Utils;

/**
 * Created by CANC on 2017/4/6.
 */

public class TableContentItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private MainData mainData;
    private final LayoutInflater inflater;
    private int rowHeight;//1行,2行,3行
    public ContentItemListener listener;


    public TableContentItemAdapter(Context context, MainData mainData, int rowHeight, ContentItemListener listener) {
        this.context = context;
        this.mainData = mainData;
        this.inflater = LayoutInflater.from(context);
        this.rowHeight = rowHeight;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = inflater.inflate(R.layout.item_table_main_item, parent, false);
        return new TableHeadHolder(contentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TableHeadHolder viewHolder = (TableHeadHolder) holder;
        viewHolder.tvMain.setText(mainData.data.get(position));
        viewHolder.tvMain.getLayoutParams().height = Utils.dpToPx(context, 50 * rowHeight);
        viewHolder.tvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.ItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mainData == null || mainData.data == null) ? 0 : mainData.data.size();
    }

    public class TableHeadHolder extends RecyclerView.ViewHolder {
        TextView tvMain;

        public TableHeadHolder(View itemView) {
            super(itemView);
            tvMain = (TextView) itemView.findViewById(R.id.tv_main);
        }
    }

    public interface ContentItemListener {
        void ItemClick(int position);
    }

}
