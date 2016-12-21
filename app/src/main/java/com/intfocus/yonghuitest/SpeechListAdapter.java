package com.intfocus.yonghuitest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

/**
 * Created by liuruilin on 2016/12/6.
 */

public class SpeechListAdapter extends ArrayAdapter<String> {
    private static SpeechListAdapter mListAttayAdapter;
    private int resourceId;

    public SpeechListAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
        this.resourceId = textViewResourceId;
        mListAttayAdapter = this;
    }

    public static SpeechListAdapter getAdapter() {
        return mListAttayAdapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position).trim();
        LinearLayout listItem = new LinearLayout(getContext());
        String inflater = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
        vi.inflate(resourceId, listItem, true);
        TextView viewItem = (TextView) listItem.findViewById(R.id.speechSelectorItem);
        ImageView imgPlaying = (ImageView) listItem.findViewById(R.id.icon_isPlaying);
        viewItem.setText(item);
        imgPlaying.setVisibility(View.GONE);

        if (position == SpeechReport.speechNum) {
            viewItem.setTextColor(Color.parseColor("#53A93F"));
            imgPlaying.setVisibility(View.VISIBLE);
        }

        return listItem;
    }
}

