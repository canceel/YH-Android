package com.intfocus.yonghuitest.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.intfocus.yonghuitest.BaseActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.adapter.SimpleListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowListMsgActivity extends BaseActivity {
    private ListView pushListView;
    private TextView bannerTitle;
    private ArrayList<HashMap<String, Object>> listItem;
    private SimpleListAdapter mSimpleAdapter;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_msg);

        pushListView = (ListView) findViewById(R.id.pushListView);
        bannerTitle = (TextView) findViewById(R.id.bannerTitle);
        listItem = new ArrayList<>();
        Intent intent = getIntent();
        bannerTitle.setText(intent.getStringExtra("title"));
        if (intent.hasExtra("response")){
            response = intent.getStringExtra("response");
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ItemName", json.getString("name"));
                    if (json.getString("os").startsWith("iPhone")){
                        map.put("ItemContent", "iPhone" + "(" + json.getString("os_version") + ")");
                    }else {
                        map.put("ItemContent", "Android" + "(" + json.getString("os_version") + ")");
                    }
                    listItem.add(map);
                }
                mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.list_info_setting, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});
                pushListView.setAdapter(mSimpleAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (intent.hasExtra("pushMessage")){
            try {
                SharedPreferences sp = getSharedPreferences("allPushMessage", MODE_PRIVATE);
                JSONObject json = new JSONObject(sp.getString("message","false"));
                for (int i = 0; i < json.length(); i++){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ItemName", json.getString(""+i));
                    map.put("ItemContent", json.getString(""+i));
                    listItem.add(map);
                }
                mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.layout_push_list, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});
                pushListView.setAdapter(mSimpleAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void dismissActivity(View v) {
        ShowListMsgActivity.this.onBackPressed();
    }
}
