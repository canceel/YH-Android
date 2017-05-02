package com.intfocus.yonghuitest.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.bean.tablechart.MainData;

import java.util.List;

/**
 * Created by CANC on 2017/4/19.
 */

public class TableContentListAdapter extends CommonAdapter<MainData> {
    private int rowHeight;//1行,2行,3行
    private TableContentItemAdapter.ContentItemListener listener;

    public TableContentListAdapter(Context context, List<MainData> datas, int rowHeight,
                                   TableContentItemAdapter.ContentItemListener listener) {
        super(context, datas);
        this.layoutId = R.layout.item_table_main;
        this.rowHeight = rowHeight;
        this.listener = listener;
    }

    public void updateDatas(int rowHeight) {
        this.rowHeight = rowHeight;
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder holder, MainData mainData) {
        RecyclerView recyclerView = holder.getView(R.id.recycler_view);
        TableContentItemAdapter adapter = new TableContentItemAdapter(context, mainData, rowHeight, listener);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }
}
