package com.intfocus.yonghuitest.adapter.table;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.bean.table.Head;
import com.intfocus.yonghuitest.bean.table.MainData;
import com.intfocus.yonghuitest.util.Utils;

import java.util.List;

/**
 * Created by CANC on 2017/4/6.
 */

public class TableContentItemAdapter extends RecyclerView.Adapter<TableContentItemAdapter.TableHeadHolder> {
    private Context context;
    private List<MainData> mainData;
    private List<Head> heads;
    private final LayoutInflater inflater;
    private int rowHeight;//1行,2行,3行
    public ContentItemListener listener;


    public TableContentItemAdapter(Context context, List<Head> heads, List<MainData> mainData, int rowHeight, ContentItemListener listener) {
        this.context = context;
        this.mainData = mainData;
        this.inflater = LayoutInflater.from(context);
        this.rowHeight = rowHeight;
        this.listener = listener;
        this.heads = heads;
    }

    @Override
    public TableContentItemAdapter.TableHeadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = inflater.inflate(R.layout.item_table_main_item, parent, false);
        return new TableContentItemAdapter.TableHeadHolder(contentView);
    }

    public void setData(List<Head> heads, List<MainData> mainData, int rowHeight) {
        this.heads = heads;
        this.mainData = mainData;
        this.rowHeight = rowHeight;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(TableContentItemAdapter.TableHeadHolder holder, final int position) {
        if (!heads.get(position).isShow() || heads.get(position).isKeyColumn) {
            holder.tvMain.setVisibility(View.GONE);
        } else {
            holder.tvMain.setVisibility(View.VISIBLE);
            holder.tvMain.setText(mainData.get(position).getValue());
            holder.tvMain.getLayoutParams().height = Utils.dpToPx(context, 50 * rowHeight);
            holder.tvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.ItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (mainData == null) ? 0 : mainData.size();
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
