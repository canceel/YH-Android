package com.intfocus.yonghuitest.adapter.table;

import android.content.Context;
import android.widget.TextView;


import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.util.Utils;

import java.util.List;

/**
 * Created by CANC on 2017/4/19.
 */

public class TableLeftListAdapter extends CommonAdapter<String> {

    private int rowHeight;//1行,2行,3行

    public TableLeftListAdapter(Context context, List<String> datas, int rowHeight) {
        super(context, datas);
        this.layoutId = R.layout.item_table_left;
        this.rowHeight = rowHeight;
    }

    @Override
    public void convert(ViewHolder holder, String s) {
        TextView tvHead = holder.getView(R.id.tv_left);
        tvHead.setText(s);
        tvHead.getLayoutParams().height = Utils.dpToPx(context, 50 * rowHeight);
    }
}
