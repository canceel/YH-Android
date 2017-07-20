package com.intfocus.yonghuitest.subject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intfocus.yonghuitest.CommentActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.subject.metrics.MetricsAdapter;
import com.intfocus.yonghuitest.subject.metrics.ProductListAdapter;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.ImageUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.util.ValueFormatter;
import com.intfocus.yonghuitest.util.WidgetUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.yonghui.homemetrics.data.response.HomeData;
import com.yonghui.homemetrics.data.response.HomeMetrics;
import com.yonghui.homemetrics.data.response.Item;
import com.yonghui.homemetrics.data.response.Product;
import com.yonghui.homemetrics.utils.ReorganizeTheDataUtils;
import com.yonghui.homemetrics.utils.Utils;

import org.json.JSONException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intfocus.yonghuitest.util.URLs.kBannerName;

/**
 * Created by CANC on 2017/4/6.
 */

public class HomeTricsActivity extends BaseActivity implements ProductListAdapter.ProductListListener
        , MetricsAdapter.MetricsListener, OnChartValueSelectedListener {
    @ViewInject(R.id.product_recycler_view)
    RecyclerView productRecyclerView;
    @ViewInject(R.id.tv_title)
    TextView tvTitle;
    @ViewInject(R.id.anim_loading)
    RelativeLayout mAnimLoading;
    @ViewInject(R.id.metrics_recycler_view)
    RecyclerView metricsRecyclerView;
    @ViewInject(R.id.iv_back)
    ImageView ivBack;
    @ViewInject(R.id.rl_title)
    RelativeLayout rlTitle;
    @ViewInject(R.id.tv_data_title)
    TextView tvDataTitle;
    @ViewInject(R.id.ll_bottom)
    LinearLayout llBottom;
    @ViewInject(R.id.tv_date_time)
    TextView tvDateTime;
    @ViewInject(R.id.iv_warning)
    ImageView ivWarning;
    @ViewInject(R.id.ll_data_title)
    RelativeLayout llDataTitle;
    @ViewInject(R.id.tv_sale_sort)
    TextView tvSaleSort;
    @ViewInject(R.id.tv_name_sort)
    TextView tvNameSort;
    @ViewInject(R.id.iv_name_sort)
    ImageView ivNameSort;
    @ViewInject(R.id.iv_sale_sort)
    ImageView ivSaleSort;
    @ViewInject(R.id.combined_chart)
    CombinedChart combinedChart;
    @ViewInject(R.id.rl_chart)
    LinearLayout rlChart;
    @ViewInject(R.id.tv_main_data)
    TextView tvMainData;
    @ViewInject(R.id.tv_main_data_name)
    TextView tvMainDataName;
    @ViewInject(R.id.tv_sub_data)
    TextView tvSubData;
    @ViewInject(R.id.tv_rate_of_change)
    TextView tvRateOfChange;
    @ViewInject(R.id.iv_rate_of_change)
    ImageView ivRateOfChange;
    @ViewInject(R.id.bannerSetting)
    ImageView mBannerSetting;

    private Gson gson;
    private List<HomeMetrics> datas;
    //供排序后使用
    private List<HomeMetrics> homeMetricses;
    private ProductListAdapter adapter;
    private MetricsAdapter metricsAdapter;

    private HomeData homeData;
    //当前展示的区间数据
    private HomeMetrics homeMetrics;
    private List<Product> products;
    //区间数据中的选中数据，用于上面指标展示
    private Product product;
    //是否升序，默认降序排序
    private boolean isAsc;
    //选中区域的数据
    private int dateSelected;
    //选中的单条数据
    private int productSelected;
    //上次选中的单条数据
    private int lastProductSelected;
    //记录当前
    private int page;
    //记录选中的指标
    private int itemSelected;
    //当前数据得最大值
    private double maxValue;
    //报表使用的数据
    private List<Item> items;
    //报表X轴值
    private List<String> xAxisList;
    private String[] xAxisValue;
    private boolean isShowChartData;
    private boolean isDoubleClick;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    private PopupWindow popupWindow;
    private PopupWindow mMenuWindow;
    private Context mContext;
    private String urlString;
    private String tvBannerName;
    private int groupID;
    private String reportID;
    private String selectedProductName;
    private int objectID, objectType;


    private ArrayList<HashMap<String, Object>> listItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hometrics);
        x.view().inject(this);
        mContext = this;

        initUserIDColorView();

        Intent intent = getIntent();
        urlString = intent.getStringExtra("urlString");
        groupID = intent.getIntExtra("groupID", -1);
        reportID = intent.getStringExtra("reportID");
        tvBannerName = intent.getStringExtra(kBannerName);
        objectID = intent.getIntExtra(URLs.kObjectId, -1);
        objectType = intent.getIntExtra(URLs.kObjectType, -1);
        tvTitle.setText(tvBannerName);
        new LoadReportData().execute();
    }

    /*
     * 下载数据
     */
    class LoadReportData extends AsyncTask<String, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(String... params) {
            Map<String, String> response = HttpUtil.httpGet(urlString, new HashMap<String, String>());
            return response;
        }

        @Override
        protected void onPostExecute(Map<String, String> response) {
            String jsonFileName = String.format("group_%s_template_%s_report_%s.json", String.format("%d", groupID), 3, reportID);
            String jsonFilePath = FileUtil.dirPath(mContext, K.kCachedDirName, jsonFileName);
            if (response.get("code").equals("200") || response.get("code").equals("304")) {
                initView();
                initData("{\"data\":" + response.get("body") + "}");
//                initData(response.get("body"));
                setData(false, true);
                mAnimLoading.setVisibility(View.GONE);
                try {
                    FileUtil.writeFile(jsonFilePath, "{\"data\":" + response.get("body") + "}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                if (new File(jsonFilePath).exists()) {
                    initView();
                    initData(FileUtil.readFile(jsonFilePath));
                    setData(false, true);
                    mAnimLoading.setVisibility(View.GONE);
                }
            }
            super.onPostExecute(response);
        }
    }

    private void initView() {
        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        mBannerSetting.setVisibility(View.VISIBLE);
        ivNameSort.setVisibility(View.GONE);

        metricsAdapter = new MetricsAdapter(mContext, null, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        metricsRecyclerView.setLayoutManager(mLayoutManager);
        metricsRecyclerView.setAdapter(metricsAdapter);

        adapter = new ProductListAdapter(mContext, null, this);
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(this);
        mLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(mLayoutManager1);
        productRecyclerView.setAdapter(adapter);
        rlChart.setVisibility(View.GONE);
    }

    private void initData(String mReportData) {
        isShowChartData = false;
        isDoubleClick = false;
        items = new ArrayList<>();
        xAxisList = new ArrayList<>();
        products = new ArrayList<>();
        homeMetricses = new ArrayList<>();
        gson = new Gson();
        //初始化,获取所有数据

        JsonObject returnData = new JsonParser().parse(mReportData).getAsJsonObject();
        homeData = gson.fromJson(returnData, HomeData.class);
        datas = homeData.data;
        homeMetricses.addAll(datas);
        //默认取第1个区域的第1条数据的第1个指标用来展示
        dateSelected = datas.size() - 1;
        productSelected = 0;
        lastProductSelected = 0;
        itemSelected = 0;
        //默认降序显示
        isAsc = false;
        //因为Y轴数据默认向右便宜1个单位，所以X轴数据默认加1
        xAxisList.add("0");
        int count = 1;
        for (HomeMetrics homeMetrics : datas) {
            if (count == 1) {
                xAxisList.add(homeMetrics.getPeriod());
            } else if (count == datas.size()) {
                xAxisList.add(homeMetrics.getPeriod());
            } else {
                xAxisList.add("");
            }
            count++;
        }
        xAxisValue = xAxisList.toArray(new String[xAxisList.size()]);
    }

    @Event(value = {R.id.iv_warning, R.id.tv_name_sort, R.id.tv_sale_sort, R.id.iv_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_warning:
                if (isShowChartData) {
                    metricsRecyclerView.setVisibility(View.VISIBLE);
                    rlChart.setVisibility(View.GONE);
                    ivWarning.setImageResource(R.drawable.btn_inf);
                    isShowChartData = false;
//                    setData(false,true);
                } else {
                    showPopWindows();
                }
                break;
            case R.id.tv_name_sort:
                Toast.makeText(mContext, "暂不支持名字排序", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_sale_sort:
                isAsc = !isAsc;
                products = ReorganizeTheDataUtils.sortData(products, itemSelected, isAsc);
                adapter.setDatas(products, itemSelected, maxValue);
                ivSaleSort.setRotation(isAsc ? 0 : 180);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    /*
     * 标题栏点击设置按钮显示下拉菜单
     */
    public void launchDropMenuActivity(View v) {
        showComplaintsPopWindow(v);
    }

    /**
     * 显示菜单
     *
     * @param clickView
     */
    void showComplaintsPopWindow(View clickView) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_v3, null);
        //设置弹出框的宽度和高度
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        //进入退出的动画
//        popupWindow.setAnimationStyle(R.style.AnimationPopupwindow);
        popupWindow.showAsDropDown(clickView);

        contentView.findViewById(R.id.ll_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnimLoading.setVisibility(View.VISIBLE);
                if (isShowChartData) {
                    metricsRecyclerView.setVisibility(View.VISIBLE);
                    rlChart.setVisibility(View.GONE);
                    ivWarning.setImageResource(R.drawable.btn_inf);
                    isShowChartData = false;
                }
                new LoadReportData().execute();
            }
        });
        contentView.findViewById(R.id.ll_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //分享
                actionShare2Weixin();
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.ll_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //评论
                actionLaunchCommentActivity();
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void productSelected(String productName, int position) {
        lastProductSelected = productSelected;
        productSelected = position;
        selectedProductName = productName;
        for (HomeMetrics homeMetrics : homeMetricses) {
            //设置下方选中
            for (int i = 0; i < homeMetrics.products.size(); i++) {
                if (homeMetrics.products.get(i).getName().equals(selectedProductName)) {
                    homeMetrics.products.get(i).isSelected = true;
                } else {
                    homeMetrics.products.get(i).isSelected = false;
                }
            }
        }
//        itemSelected = 0;
        setData(isAsc, true);
    }

    @Override
    public void itemSelected(int page, int position, boolean isDoubleClick) {
        this.isDoubleClick = isDoubleClick;
        //设置排序栏数据
        itemSelected = page * 4 + position;
        setData(false, true);
        if (isDoubleClick) {
            showCombinedChart();
        }
    }

    /**
     * @param isNeedSort    是否排序
     * @param showAnimation 是否展示动画
     */
    private void setData(final boolean isNeedSort, final boolean showAnimation) {
        /**
         * homeMetrics 一个时间维度
         * products 一个时间维度所有数据的集合
         * items 一个时间维度的一个门店的数据
         * dataSelect 选择是时间维度
         * productSelect 选择的一个时间维度某个门店
         */
        items.clear();
        homeMetrics = datas.get(dateSelected);
        if (isNeedSort) {
            products = ReorganizeTheDataUtils.sortData(homeMetrics.products, itemSelected, isAsc);
        } else {
            products = homeMetrics.products;
        }
        //排序数据，获取当前数据最大值
        List<Product> sortProducts = new ArrayList<>();
        sortProducts.addAll(products);
        maxValue = ReorganizeTheDataUtils.sortData(sortProducts, itemSelected, false).get(0).items.get(itemSelected).main_data.getData();
        if (null == selectedProductName) {
            selectedProductName = products.get(0).getName();
        }
        if (product == null || lastProductSelected != productSelected) {
            for (HomeMetrics homeMetrics : homeMetricses) {
                //设置下方选中
                for (int i = 0; i < homeMetrics.products.size(); i++) {
                    if (homeMetrics.products.get(i).getName().equals(selectedProductName)) {
                        homeMetrics.products.get(i).isSelected = true;
                        productSelected = i;
                    } else {
                        homeMetrics.products.get(i).isSelected = false;
                    }
                }
            }

            for (Product product1 : homeMetrics.products) {
                if (product1.getName().equals(selectedProductName)) {
                    product = product1;
                }
            }
            lastProductSelected = productSelected;
        }


        //给报表使用的数据,已经排序，则提取得报表数据也要排序后得字段
        for (HomeMetrics homeMetrics : homeMetricses) {
            int selectedNumber = productSelected;
            for (int i = 0; i < homeMetrics.products.size(); i++) {
                if (homeMetrics.products.get(i).getName().equals(selectedProductName)) {
                    selectedNumber = i;
                }
            }
            Product product = ReorganizeTheDataUtils.sortData(homeMetrics.products, itemSelected, isAsc).get(selectedNumber);
//            Product product = homeMetrics.products.get(productSelected);
            Item item = product.items.get(itemSelected);
            items.add(item);
        }
        initChart(showAnimation);
        combinedChart.invalidate();
        tvNameSort.setText(homeMetrics.getHead());
        tvDateTime.setText(homeMetrics.getPeriod());
        tvDataTitle.setText(selectedProductName);
        //设置指标选中状态
        for (int i = 0; i < product.items.size(); i++) {
            if (i == itemSelected) {
                product.items.get(i).isSelected = true;
            } else {
                product.items.get(i).isSelected = false;
            }
        }
        //重组指标数据
        HomeMetrics homeMetrics1 = ReorganizeTheDataUtils.groupByNumber(product, 4, true);
        metricsAdapter.setDatas(homeMetrics1);
        //设置排序栏显示文字
        tvSaleSort.setText(product.items.get(itemSelected).getName());
        adapter.setDatas(products, itemSelected, maxValue);
    }

    //显示报表
    void showCombinedChart() {
        isShowChartData = true;
        ivWarning.setImageResource(R.drawable.pop_close);
        metricsRecyclerView.setVisibility(View.GONE);
        combinedChart.animateY(2000);
        rlChart.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化报表
     *
     * @param showAnimation 是否显示动画
     */
    void initChart(boolean showAnimation) {

        int screenWidth = Utils.getScreenWidth(mContext);
        combinedChart.getLayoutParams().height = screenWidth * 300 / 640;

        //启用缩放和拖动
        combinedChart.setDragEnabled(true);//拖动
        combinedChart.setScaleEnabled(false);//缩放
        combinedChart.setOnChartValueSelectedListener(this);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setBackgroundColor(ContextCompat.getColor(mContext, R.color.co10));
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);

        Legend l = combinedChart.getLegend();
        l.setForm(Legend.LegendForm.NONE);//底部样式
        l.setTextColor(ContextCompat.getColor(mContext, R.color.transparent));
        l.setWordWrapEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextColor(ContextCompat.getColor(mContext, R.color.co4));

        updateTitle(items.get(dateSelected));
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setEnabled(true);//设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setTextSize(10f); //设置X轴文字大小
        xAxis.setGranularityEnabled(true);//是否允许X轴上值重复出现
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.co4));//设置X轴文字颜色
//        //设置竖线的显示样式为虚线
//        //lineLength控制虚线段的长度
//        //spaceLength控制线之间的空间
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMinimum(0f);//设置x轴的最小值
        xAxis.setAxisMaximum(mDatas.length);//设置最大值
        xAxis.setAvoidFirstLastClipping(true);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setLabelRotationAngle(0f);//设置x轴标签字体的旋转角度
//        设置x轴显示标签数量  还有一个重载方法第二个参数为布尔值强制设置数量 如果启用会导致绘制点出现偏差
        xAxis.setLabelCount(10);
        xAxis.setGridLineWidth(10f);//设置竖线大小
//        xAxis.setGridColor(Color.RED);//设置竖线颜色
        xAxis.setAxisLineColor(Color.GRAY);//设置x轴线颜色
        xAxis.setAxisLineWidth(1f);//设置x轴线宽度

        CombinedData combinedData = new CombinedData();
        combinedData.setData(generateLineData());
        combinedData.setData(generateBarData());
        combinedData.setValueTypeface(mTfLight);
        xAxis.setAxisMaximum(combinedData.getXMax() + 0.25f);
        //X轴的数据格式
        ValueFormatter xAxisFormatter = new ValueFormatter(combinedChart);
        xAxisFormatter.setmValues(xAxisValue);
        xAxis.setValueFormatter(xAxisFormatter);//格式化x轴标签显示字符
        combinedChart.setData(combinedData);
        combinedChart.invalidate();
        if (showAnimation) {
            combinedChart.animateY(2000);
        }
    }

    /**
     * 曲线
     */
    private LineData generateLineData() {
        LineData lineData = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            entries.add(new Entry(index + 1f, (float) items.get(index).sub_data.getData()));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "对比数据");
        lineDataSet.setValues(entries);
        lineDataSet.setDrawValues(false);//是否在线上显示值
        lineDataSet.setColor(ContextCompat.getColor(mContext, R.color.co3));
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCircleColor(ContextCompat.getColor(mContext, R.color.co3));
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);//设置线条类型
        //set.setDrawHorizontalHighlightIndicator(false);//隐藏选中线
        //set.setDrawVerticalHighlightIndicator(false);//隐藏选中线条
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(false);
        lineData.setHighlightEnabled(false);
        lineData.addDataSet(lineDataSet);
        return lineData;
    }

    /**
     * 柱形图数据
     */
    private BarData generateBarData() {
        BarData barData = new BarData();
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            entries1.add(new BarEntry(index + 1f, (float) items.get(index).main_data.getData()));
        }
        BarDataSet barDataSet = new BarDataSet(entries1, "当前数据");
        barDataSet.setValues(entries1);
        barDataSet.setDrawValues(false);//是否在线上显示值
        barDataSet.setColor(Color.rgb(230, 230, 230));
        barDataSet.setHighLightColor(Color.parseColor(items.get(dateSelected).state.getColor()));
        barDataSet.setValueTextColor(Color.rgb(60, 220, 78));
        barDataSet.setValueTextSize(10f);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        float barWidth = 0.45f;
        barData.addDataSet(barDataSet);
        barData.setBarWidth(barWidth);
        return barData;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d("X:", e.getX() + "");
        Log.d("Y:", e.getX() + "");
        dateSelected = ((int) e.getX() - 1);
        changeDate(dateSelected);
        updateTitle(items.get(dateSelected));
    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * 更改最外层数据
     */
    private void changeDate(int position) {
        if (dateSelected < datas.size() - 1) {
            dateSelected++;
        } else {
            dateSelected = 0;
        }
        dateSelected = position;
        product = null;
        lastProductSelected = 0;
        setData(false, false);
    }

    /**
     * 获取格式化的图表颜色
     *
     * @param position
     * @return
     */
    private List<Integer> formateColors(int position) {
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (i == itemSelected) {
                colors.add(Color.parseColor(items.get(position).state.getColor()));
            } else {
                colors.add(ContextCompat.getColor(mContext, R.color.co4));
            }
        }
        return colors;
    }

    /**
     * 更新表格顶部数据
     *
     * @param item
     */
    private void updateTitle(Item item) {
        String result;
        tvMainDataName.setText(item.getName());
        BigDecimal bigDecimal = new BigDecimal(item.main_data.getData());
        double mainData = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tvMainData.setText(mainData + "");
        BigDecimal bigDecimal1 = new BigDecimal(item.sub_data.getData());
        double subData = bigDecimal1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tvSubData.setText(subData + "");
        BigDecimal bigDecimal2;
        if (bigDecimal1.compareTo(BigDecimal.ZERO) == 0) {
            bigDecimal2 = bigDecimal.subtract(bigDecimal1).multiply(new BigDecimal(100));
        } else {
            bigDecimal2 = bigDecimal.subtract(bigDecimal1).multiply(new BigDecimal(100)).divide(bigDecimal1, 2);
        }
        double f1 = bigDecimal2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        result = f1 + "%";
        tvRateOfChange.setText(result);
        if ("up".equalsIgnoreCase(item.state.getArrow())) {
            switch (item.state.getColor()) {
                case "#F4BC45":
                    ivRateOfChange.setImageResource(R.drawable.arrow_yellow_up);
                    break;

                case "#F57685":
                    ivRateOfChange.setImageResource(R.drawable.arrow_red_up);
                    break;

                case "#91C941":
                    ivRateOfChange.setImageResource(R.drawable.arrow_green_up);
                    break;

                default:
                    ivRateOfChange.setVisibility(View.GONE);
                    break;
            }
        } else {
            switch (item.state.getColor()) {
                case "#F4BC45":
                    ivRateOfChange.setImageResource(R.drawable.arrow_yellow_down);
                    break;

                case "#F57685":
                    ivRateOfChange.setImageResource(R.drawable.arrow_red_down);
                    break;

                case "#91C941":
                    ivRateOfChange.setImageResource(R.drawable.arrow_green_down);
                    break;

                default:
                    ivRateOfChange.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * 显示图表说明
     */
    private void showPopWindows() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_warning, null);
        popupWindow = new PopupWindow(view, Utils.getScreenWidth(mContext), ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    /*
     * 返回
     */
    public void dismissActivity(View v) {
        HomeTricsActivity.this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    protected float[] mDatas = new float[]{
            29, 33, 50, 59, 10, 5, 50, 18, 29, 19
    };

    /*
     * 初始化用户信息
     */
    private void initUserIDColorView() {
        String userConfigPath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kUserConfigFileName);
        if ((new File(userConfigPath)).exists()) {
            try {
                user = FileUtil.readConfigFile(userConfigPath);
                if (user.has(URLs.kIsLogin) && user.getBoolean(URLs.kIsLogin)) {
                    userID = user.getInt("user_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 分享截图至微信
     */
    public void actionShare2Weixin() {
        Bitmap bmpScrennShot = ImageUtil.takeScreenShot(HomeTricsActivity.this);
        if (bmpScrennShot == null) {toast("截图失败");}
        UMImage image = new UMImage(this, bmpScrennShot);
        new ShareAction(this)
                .withText("截图分享")
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setDisplayList(SHARE_MEDIA.WEIXIN)
                .withMedia(image)
                .setCallback(umShareListener)
                .open();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
            WidgetUtil.showToastShort(mContext, "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 评论
     */
    public void actionLaunchCommentActivity() {
        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra(URLs.kBannerName, tvBannerName);
        intent.putExtra(URLs.kObjectId, objectID);
        intent.putExtra(URLs.kObjectType, objectType);
        mContext.startActivity(intent);
    }
}
