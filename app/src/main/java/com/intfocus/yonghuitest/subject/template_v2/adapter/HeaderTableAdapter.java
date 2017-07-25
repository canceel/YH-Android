package com.intfocus.yonghuitest.subject.template_v2.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by zbaoliang on 17-5-18.
 */
public class HeaderTableAdapter extends BaseAdapter {

    ArrayList<View> tableItems;

    public HeaderTableAdapter(ArrayList<View> tableItems) {
        this.tableItems = tableItems;
    }

    @Override
    public int getCount() {
        if (tableItems == null)
            return 0;
        else return tableItems.size();
    }

    @Override
    public Object getItem(int position) {
        return tableItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return tableItems.get(position);
    }
}
