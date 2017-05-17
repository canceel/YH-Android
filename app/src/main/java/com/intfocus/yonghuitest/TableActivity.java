package com.intfocus.yonghuitest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intfocus.yonghuitest.adapter.table.ColumAdapter;
import com.intfocus.yonghuitest.adapter.table.TableBarChartAdapter;
import com.intfocus.yonghuitest.adapter.table.TableContentItemAdapter;
import com.intfocus.yonghuitest.adapter.table.TableContentListAdapter;
import com.intfocus.yonghuitest.adapter.table.TableFilterItemAdapter;
import com.intfocus.yonghuitest.adapter.table.TableFilterListAdapter;
import com.intfocus.yonghuitest.adapter.table.TableLeftListAdapter;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.bean.table.Filter;
import com.intfocus.yonghuitest.bean.table.FilterItem;
import com.intfocus.yonghuitest.bean.table.Head;
import com.intfocus.yonghuitest.bean.table.MainData;
import com.intfocus.yonghuitest.bean.table.SortData;
import com.intfocus.yonghuitest.bean.table.TableBarChart;
import com.intfocus.yonghuitest.bean.table.TableChart;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.MyHorizontalScrollView;
import com.intfocus.yonghuitest.util.Utils;
import com.intfocus.yonghuitest.util.WidgetUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by CANC on 2017/3/30.
 */

public class TableActivity extends BaseActivity implements ColumAdapter.ColumnListener,
        TableFilterItemAdapter.FilterItemLisenter, TableContentItemAdapter.ContentItemListener,
        TableBarChartAdapter.TableBarChartLisenter {
    private static long DOUBLE_CLICK_TIME = 200;
    private static long mLastTime;
    private static long mCurTime;
    @BindView(R.id.tv_banner_name)
    TextView tvBannerName;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.content_horizontal_scroll_view)
    MyHorizontalScrollView contentHorizontalScrollView;
    @BindView(R.id.top_horizontal_scroll_view)
    MyHorizontalScrollView topHorizontalScrollView;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.left_listview)
    ListView leftListView;
    @BindView(R.id.content_list_view)
    ListView contentListView;
    @BindView(R.id.ll_bar_head)
    LinearLayout llBarHead;
    @BindView(R.id.bar_chart_list_view)
    ListView barChartListView;
    @BindView(R.id.tv_head)
    TextView tvHead;
    @BindView(R.id.anim_loading)
    RelativeLayout mAnimLoading;

    private ImageView ivCheckAll;
    private SwipeMenuRecyclerView recyclerView;

    private Gson gson;
    //原始数据保持不变
    private TableChart originTableData;
    //用于展示的数据
    private TableChart showTableData;
    //主表格数据
    private List<List<MainData>> mainDatas;
    //过滤后主表格数据
    private List<List<MainData>> filterMainDatas;
    //主表格适配器
    private TableContentListAdapter contentListAdapter;
    //左边数据
    private List<String> leftDatas;
    boolean isLeftListEnabled = false;
    boolean isRightListEnabled = false;
    //左边适配器
    private TableLeftListAdapter adapter;
    //头部数据
    private List<Head> headDatas;
    //弹框
    private Dialog commonDialog;
    //菜单
    private View contentView;
    private PopupWindow popupWindow;
    // 选列
    private ColumAdapter columnAdapter;
    //选列改变数据
    //主要用于记录选列时的head部分
    private TableChart changeTableChartData;
    //记录保留的行
    private List<Integer> showRow;
    //记录保留的列
    private List<Integer> showColum;
    //是否全选
    private boolean isSelectedAll = true;
    private int selectedNum = 0;
    private int rowHeight = 1;//初始行距
    private int currentHeight = 1;//当前行距
    //过滤字段
    private List<Filter> filters;
    private TableFilterListAdapter tableFilterListAdapter;
    //排序
    private boolean isAsc = false;
    private List<TextView> textViews;
    private List<TableBarChart> tableBarCharts;
    private int currentPosition;
    private int currentValuePosition;
    private int keyLinePosition;
    private TableBarChartAdapter tableBarChartAdapter;
    private Context mContext;
    private String[] mColorList;
    private int groupID;
    private String reportID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        ButterKnife.bind(this);

        mContext = this;

        mainDatas = new ArrayList<>();
        filterMainDatas = new ArrayList<>();
        headDatas = new ArrayList<>();
        showColum = new ArrayList<>();
        showRow = new ArrayList<>();
        filters = new ArrayList<>();
        textViews = new ArrayList<>();
        mColorList = new String[]{"#F2836B","#F2836B","#F2E1AC","#F2E1AC","#63A69F","#63A69F"};

        Intent intent = getIntent();
        groupID = intent.getIntExtra("groupID", -1);
        reportID = intent.getStringExtra("reportID");
        urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json", K.kBaseUrl, groupID, 5, reportID);
        new LoadReportData().execute();
    }

    /*
     * 下载数据
     */
    class LoadReportData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = null;
            String jsonFileName = String.format("group_%s_template_%s_report_%s.json", String.format("%d", groupID), 5, reportID);
//            String jsonFilePath = String.format("%s/assets/javascripts/%s", assetsPath, jsonFileName);
            String jsonFilePath = FileUtil.dirPath(mContext, K.kCachedDirName, jsonFileName);
            boolean dataState = ApiHelper.reportJsonData(mContext, String.format("%d", groupID), "5", reportID);
            if (dataState && new File(jsonFilePath).exists()) {
                response = FileUtil.readFile(jsonFilePath);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                fillTableData(response);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAnimLoading.setVisibility(View.GONE);
                }
            }, 300);
            super.onPostExecute(response);
        }
    }

    void fillTableData(String json) {
        gson = new Gson();
        JsonObject returnData = new JsonParser().parse(json).getAsJsonObject();
        originTableData = gson.fromJson(returnData, TableChart.class);
        changeTableChartData = gson.fromJson(returnData, TableChart.class);
        showTableData = gson.fromJson(returnData, TableChart.class);

        if (originTableData != null) {
            //过滤字段
            filters.addAll(originTableData.config.filter);
            //初始化数据时，记录head的初始位置，供以后选列使用
            List<Head> heads = new ArrayList<>();

            for (int i = 0; i < originTableData.table.head.size(); i++) {
                Head head = originTableData.table.head.get(i);
                head.originPosition = i;
                heads.add(head);
            }

            originTableData.table.head = heads;
            changeTableChartData.table.head = heads;
//            headDatas.addAll(heads);
            showTableData = generateShowTableData(showTableData);

            mainDatas.addAll(originTableData.table.main_data);
            filterMainDatas.addAll(mainDatas);
            tvBannerName.setText(originTableData.name);

            // 默认第一列为关键列
            originTableData.table.head.get(0).isKeyColumn = true;
            setKeyColumn(0);

            // 表格内容填充
            contentListAdapter = new TableContentListAdapter(mContext, originTableData.table.head, mainDatas, rowHeight, this);
            contentListView.setAdapter(contentListAdapter);

            textViews.clear();
            int textViewPosition = 0;
            int valuePosition = 0;
            for (int i = 0; i < originTableData.table.head.size(); i++) {
                final Head head = originTableData.table.head.get(i);
                final TextView textView = new TextView(mContext);
                if (!head.isShow() || head.isKeyColumn) {
                    // 隐藏列和关键列不展示
                }
                else if(head.isKeyColumn) {
                    keyLinePosition = i;
                }
                else{
                    LayoutParams params = new LayoutParams(Utils.dpToPx(mContext, 80), LayoutParams.MATCH_PARENT);
                    textView.setLayoutParams(params);
                    textView.setText(head.getValue());
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                    textView.setBackgroundResource(R.drawable.background_square_black_boder_white);
                    textView.setPadding(0, 0, Utils.dpToPx(mContext, 5), 0);
                    textView.setCompoundDrawablePadding(Utils.dpToPx(mContext, 5));
                    Drawable drawable = Utils.returnDrawable(mContext, R.drawable.icon_sort);
                    textView.setCompoundDrawables(null, null, drawable, null);
                    final int finalTextViewPosition = textViewPosition;
                    final int finalValuePosition = valuePosition;
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sort(finalValuePosition, finalTextViewPosition);
                            WidgetUtil.showToastShort(mContext, head.getValue() + ":排序");
                        }
                    });
                    headDatas.add(head);
                    textViews.add(textView);
                    llHead.addView(textView);
                    textViewPosition++;
                }
                valuePosition++;
                currentValuePosition = valuePosition;
            }
        }
        SlipMonitor();
        tvHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort(currentValuePosition, currentPosition);
            }
        });
    }

    void setKeyColumn(int keyColumn) {
        if (leftDatas == null) {
            leftDatas = new ArrayList<>();
        } else {
            leftDatas.clear();
        }

        //为默认关键列添加数据
        for (List<MainData> mainData : originTableData.table.main_data) {
            leftDatas.add(mainData.get(keyColumn).getValue());
        }

        // 默认第一列为关键列
        originTableData.table.head.get(keyColumn).isKeyColumn = true;
        tvTitle.setText(originTableData.table.head.get(keyColumn).getValue());

        // 关键列绑定适配器
        if (adapter == null) {
            adapter = new TableLeftListAdapter(mContext, leftDatas, rowHeight);
            leftListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    //滑动监听
    void SlipMonitor() {
        //左右滑动同步
        contentHorizontalScrollView.setMyScrollChangeListener(new MyHorizontalScrollView.MyScrollChangeListener() {
            @Override
            public void onscroll(MyHorizontalScrollView view, int x, int y, int oldx, int oldy) {
                topHorizontalScrollView.scrollTo(x, y);
            }
        });
        topHorizontalScrollView.setMyScrollChangeListener(new MyHorizontalScrollView.MyScrollChangeListener() {
            @Override
            public void onscroll(MyHorizontalScrollView view, int x, int y, int oldx, int oldy) {
                contentHorizontalScrollView.scrollTo(x, y);
            }
        });

        //上下滑动同步
        leftListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isRightListEnabled = false;
                    isLeftListEnabled = true;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    isRightListEnabled = true;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                View child = absListView.getChildAt(0);
                if (child != null && isLeftListEnabled) {
                    contentListView.setSelectionFromTop(firstVisibleItem, child.getTop());
                    barChartListView.setSelectionFromTop(firstVisibleItem, child.getTop());
                }
            }
        });

        //右侧ListView滚动时，控制左侧ListView滚动
        contentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isLeftListEnabled = false;
                    isRightListEnabled = true;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    isLeftListEnabled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                View c = view.getChildAt(0);
                if (c != null && isRightListEnabled) {
                    leftListView.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });

        //右侧ListView滚动时，控制左侧ListView滚动
        barChartListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isLeftListEnabled = false;
                    isRightListEnabled = true;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    isLeftListEnabled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                View c = view.getChildAt(0);
                if (c != null && isRightListEnabled) {
                    leftListView.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });
    }

    @OnClick(R.id.iv_menu)
    public void onClick() {
        showComplaintsPopWindow(ivMenu);
    }

    /**
     * 显示菜单
     *
     * @param clickView
     */
    void showComplaintsPopWindow(View clickView) {
        contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_v5, null);
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
        contentView.findViewById(R.id.ll_xuanlie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColumDialog();
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.ll_guolv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showFilterlog();
                WidgetUtil.showToastShort(mContext, "功能正在开发中");
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.ll_hangju).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRowHeightDialog();
                popupWindow.dismiss();
            }
        });
    }

    //显示选列弹框
    void showColumDialog() {
        commonDialog = new AlertDialog.Builder(mContext, R.style.CommonDialog).setTitle("选列").create();
        commonDialog.show();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_column, null);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
        TextView tvCheckAll = (TextView) view.findViewById(R.id.tv_check_all);
        ivCheckAll = (ImageView) view.findViewById(R.id.iv_check_all);
        ivCheckAll.setImageResource(isSelectedAll ? R.drawable.btn_selected : R.drawable.btn_unselected);
        recyclerView = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
//        menuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。
        // 触摸拖拽的代码在Adapter中：SwipeMenuRecyclerView#startDrag(ViewHolder);
        columnAdapter = new ColumAdapter(recyclerView, originTableData, this);
        recyclerView.setAdapter(columnAdapter);

        recyclerView.setLongPressDragEnabled(true); // 开启拖拽。
        recyclerView.setItemViewSwipeEnabled(false); // 关闭滑动删除。
//        recyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。
//        recyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener);
        commonDialog.setContentView(view);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
                commonDialog = null;
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSure();
            }
        });
        //全选
        tvCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickALl();
            }
        });
        //全选
        ivCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickALl();
            }
        });
    }

    /**
     * 当Item移动的时候。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(changeTableChartData.table.head, fromPosition, toPosition);
            columnAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            columnAdapter.notifyItemRemoved(position);
            Toast.makeText(mContext, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
//            mActionBar.setSubtitle("状态：拖拽");
                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
//            mActionBar.setSubtitle("状态：滑动删除");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
//            mActionBar.setSubtitle("状态：手指松开");
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(mContext, R.drawable.select_white));
                for (int i = 0; i < changeTableChartData.table.head.size(); i++) {
                    Head head = changeTableChartData.table.head.get(i);
                    if (head.isKeyColumn) {
//                        Collections.swap(changeTableChartData.table.head, i, 0);
                    }
                }
                columnAdapter.setDatas(changeTableChartData);
            }
        }
    };

    //选列
    @Override
    public void checkClick(int position, String info) {
        selectedNum = 0;
        for (Head head : changeTableChartData.table.head) {
            if (info.equalsIgnoreCase(head.getValue())) {
                head.setShow(!head.isShow());
            }
            if (head.isShow()) {
                selectedNum++;
            }
        }
        isSelectedAll = (selectedNum == changeTableChartData.table.head.size());
        ivCheckAll.setImageResource(isSelectedAll ? R.drawable.btn_selected : R.drawable.btn_unselected);
        //延时解决recyclerview正在计算时更新界面引起的异常
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                columnAdapter.setDatas(changeTableChartData);
            }
        }, 300);
    }

    //
    //选择关键列
    @Override
    public void markerClick(int position, String info) {
        for (Head head : changeTableChartData.table.head) {
            if (info.equalsIgnoreCase(head.getValue())) {
                head.isKeyColumn = true;
                head.setShow(true);
            } else {
                head.isKeyColumn = false;
            }
        }
//        Collections.swap(changeTableChartData.table.head, position, 0);
        columnAdapter.setDatas(changeTableChartData);
    }

    //点击全选
    void clickALl() {
        isSelectedAll = !isSelectedAll;
        ivCheckAll.setImageResource(isSelectedAll ? R.drawable.btn_selected : R.drawable.btn_unselected);
        for (Head head : changeTableChartData.table.head) {
            //不是关键列点击全选改变数据
            if (!head.isKeyColumn) {
                head.setShow(isSelectedAll);
            }
        }
        columnAdapter.setDatas(changeTableChartData);
    }

    //点击应用
    void clickSure() {
        mainDatas.clear();
        showColum.clear();
        //更改外部数据的值
        originTableData.table.head = changeTableChartData.table.head;
        //清除
        //移除head全部数据
        llHead.removeAllViews();
        textViews.clear();
        headDatas.clear();
        int textViewPosition = 0;
        int valuePosition = 0;
        for (int i = 0; i < changeTableChartData.table.head.size(); i++) {
            final Head head = changeTableChartData.table.head.get(i);
            //表格Head数据，只有isShow=true才展示
            if (!head.isShow()) {

            } else if (head.isKeyColumn) {
                keyLinePosition = i;
                setKeyColumn(i);
            } else {
                showColum.add(head.originPosition);
                final TextView textView = new TextView(mContext);
                LayoutParams params = new LayoutParams(Utils.dpToPx(mContext, 80), LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(params);
                textView.setText(head.getValue());
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                textView.setBackgroundResource(R.drawable.background_square_black_boder_white);
                textView.setPadding(0, 0, Utils.dpToPx(mContext, 5), 0);
                textView.setCompoundDrawablePadding(Utils.dpToPx(mContext, 5));
                Drawable drawable = Utils.returnDrawable(mContext, R.drawable.icon_sort);
                textView.setCompoundDrawables(null, null, drawable, null);
                final int finalI = i;
                final int finalTextViewPosition = textViewPosition;
                final int finalValuePosition = valuePosition;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //添加排序功能
                        sort(finalValuePosition, finalTextViewPosition);
                        WidgetUtil.showToastShort(mContext, head.getValue() + ":排序");
                    }
                });
                headDatas.add(head);
                textViews.add(textView);
                llHead.addView(textView);
                textViewPosition++;
            }
            valuePosition++;
        }

        mainDatas.addAll(changeTableChartData.table.main_data);
        contentListAdapter.notifyDataSetChanged();
//        filterSelected();
        if (commonDialog != null) {
            commonDialog.dismiss();
        }
    }

    //显示行距弹框
    void showRowHeightDialog() {
        currentHeight = rowHeight;
        commonDialog = new AlertDialog.Builder(mContext, R.style.CommonDialog).setTitle("选列").create();
        commonDialog.show();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_row_height, null);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
        final RelativeLayout rlOneLine = (RelativeLayout) view.findViewById(R.id.rl_one_line);
        final RelativeLayout rlTwoLine = (RelativeLayout) view.findViewById(R.id.rl_two_line);
        final RelativeLayout rlThreeLine = (RelativeLayout) view.findViewById(R.id.rl_three_line);
        commonDialog.setContentView(view);
        rowHeightCheck(rowHeight, rlOneLine, rlTwoLine, rlThreeLine);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRowHeight(currentHeight);
                commonDialog.dismiss();
            }
        });
        rlOneLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowHeightCheck(1, rlOneLine, rlTwoLine, rlThreeLine);
            }
        });
        rlTwoLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowHeightCheck(2, rlOneLine, rlTwoLine, rlThreeLine);
            }
        });
        rlThreeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowHeightCheck(3, rlOneLine, rlTwoLine, rlThreeLine);
            }
        });
    }

    //点击行距选项
    void rowHeightCheck(int currentHeight, RelativeLayout rlOne,
                        RelativeLayout rlTwo, RelativeLayout rlThree) {
        this.currentHeight = currentHeight;
        switch (currentHeight) {
            case 1:
                rlOne.setSelected(true);
                rlTwo.setSelected(false);
                rlThree.setSelected(false);
                break;
            case 2:
                rlOne.setSelected(false);
                rlTwo.setSelected(true);
                rlThree.setSelected(false);
                break;
            case 3:
                rlOne.setSelected(false);
                rlTwo.setSelected(false);
                rlThree.setSelected(true);
                break;
        }
    }

    //设置行距
    void setRowHeight(int type) {
        rowHeight = type;
        adapter = new TableLeftListAdapter(mContext, leftDatas, rowHeight);
        leftListView.setAdapter(adapter);
        contentListAdapter.updateDatas(rowHeight);
        if (tableBarChartAdapter != null) {
            tableBarChartAdapter.updateRowHeight(rowHeight);
        }
        if (commonDialog != null) {
            commonDialog.dismiss();
        }
    }

    //
//
//    //显示过滤弹框
//    void showFilterlog() {
//        commonDialog = new AlertDialog.Builder(mContext, R.style.CommonDialog).setTitle("选列").create();
//        commonDialog.show();
//        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_filter, null);
//        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
//        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
//        ListView listView = (ListView) view.findViewById(R.id.filter_list_view);
//        tableFilterListAdapter = new TableFilterListAdapter(mContext, filters, this);
//        listView.setAdapter(tableFilterListAdapter);
//        commonDialog.setContentView(view);
//        tvCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                commonDialog.dismiss();
//            }
//        });
//        tvConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                filterSelected();
//                commonDialog.dismiss();
//            }
//        });
//    }
//
    //过滤条件选中
    @Override
    public void updateFilter() {
        int i = 0;
        for (Filter filter : filters) {
            for (FilterItem filterItem : filter.items) {
                if (filterItem.isSelected) {
                    i++;
                }
            }
            filter.isAllcheck = (filter.items.size() == i);
        }
        tableFilterListAdapter.setData(filters);
    }

    //
//    //过滤
//    void filterSelected() {
//        filterMainDatas.clear();
//        leftDatas.clear();
//        int filterCount = 0;
//        List<String> filterValues = new ArrayList<>();
//        //提取出过滤字段
//        for (int i = 0; i < filters.size(); i++) {
//            Filter filter = filters.get(i);
//            if (filter.isSelected) {
//                filterCount++;
//                for (int j = 0; j < filter.items.size(); j++) {
//                    FilterItem filterItem = filter.items.get(j);
//                    if (filterItem.isSelected) {
//                        filterValues.add(filterItem.getValue());
//                    }
//
//                }
//            }
//        }
//        //获取过滤后得数据
//        if (filterCount > 0) {
//            //开启过滤，返回过滤数据
//            if (filterValues.size() > 0) {
//                for (String filterValue : filterValues) {
//                    for (MainData mainData : mainDatas) {
//                        if (filterValue.equals(mainData.district)) {
//                            filterMainDatas.add(mainData);
//                        }
//                    }
//                }
//            }
//        } else {
//            //未开启过滤，返回全部数据
//            filterMainDatas.addAll(mainDatas);
//        }
//        //左侧数据
//        for (MainData mainData : filterMainDatas) {
//            leftDatas.add(mainData.name);
//        }
//        adapter = new TableLeftListAdapter(mContext, leftDatas, rowHeight);
//        leftListView.setAdapter(adapter);
//        contentListAdapter.setData(filterMainDatas);
//        if (barChartListView.getVisibility() == View.VISIBLE) {
//            updateBarCgart(currentPosition);
//        }
//    }
//
    //排序
    void sort(int valuePosirion, int position) {
        // 表格内容排序
        // String 转 数值
        List<Double> datas = new ArrayList<>();
        for (List<MainData> mainData : mainDatas) {
            String mainDataStr = mainData.get(valuePosirion).getValue();

            if (mainDataStr.contains("%")) {
                mainDataStr = mainDataStr.replace("%", "");
            }

            if (Utils.isNumber(mainDataStr)) {
                datas.add(Double.parseDouble(mainDataStr));
            }
            else {
                WidgetUtil.showToastShort(mContext, "暂不支持该内容的排序");
                return;
            }
        }

        // 更改表头排序状态
        for (int i = 0; i < headDatas.size(); i++) {
            Head head = headDatas.get(i);
            if (i == position) {
                if (head.sort.equalsIgnoreCase("asc")) {
                    head.sort = "desc";
                    isAsc = false;
                } else if (head.sort.equalsIgnoreCase("desc")) {
                    head.sort = "asc";
                    isAsc = true;
                } else {
                    head.sort = "asc";
                    isAsc = true;
                }
                setDrawableRightImg(tvHead, head);
            } else {
                head.sort = "default";
            }

            setDrawableRightImg(textViews.get(i), head);
        }

        List<SortData> sortDataList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            SortData sortData = new SortData();
            sortData.setValue(datas.get(i));
            sortData.originPosition = i;
            sortDataList.add(sortData);
        }

        List<Integer> integers = Utils.sortData(sortDataList, isAsc);
        List<List<MainData>> sortMainDatas = new ArrayList<>();
        for (Integer integer : integers) {
            sortMainDatas.add(mainDatas.get(integer));
        }


        contentListAdapter.setData(sortMainDatas);
//        setKeyColumn(keyLinePosition);
        //为默认关键列添加数据
        leftDatas.clear();
        for (List<MainData> mainData : mainDatas) {
            leftDatas.add(mainData.get(keyLinePosition).getValue());
        }
        adapter.setData(leftDatas);

//        filterSelected();
    }

    //表格中数据点击
    @Override
    public void ItemClick(int position) {
        mLastTime = mCurTime;
        mCurTime = System.currentTimeMillis();
        //双击显示条形图
        if (mCurTime - mLastTime < DOUBLE_CLICK_TIME) {
            tableBarCharts = new ArrayList<>();
            for (List<MainData> mainData : filterMainDatas) {
                TableBarChart tableBarChart = new TableBarChart();
                tableBarChart.setData(mainData.get(position).getValue());
                if ("null".equals(String.valueOf(mainData.get(position).getColor()))) {
                    tableBarChart.setColor("#595b57");
                    Log.i("valueColor", "is null");
                }
                else {
                    tableBarChart.setColor(mColorList[mainData.get(position).getColor()]);
                    Log.i("valueColor", mColorList[mainData.get(position).getColor()]);
                }
                tableBarCharts.add(tableBarChart);
            }
            Double maxValue = Utils.getMaxValue(tableBarCharts);
            if (null == maxValue) {
                WidgetUtil.showToastLong(mContext, "该数值无法显示条形图");
                return;
            }
            currentPosition = position;
            barChartListView.setVisibility(View.VISIBLE);
            llBarHead.setVisibility(View.VISIBLE);
            contentHorizontalScrollView.setVisibility(View.GONE);
            topHorizontalScrollView.setVisibility(View.GONE);
            updateBarCgart(maxValue, position);
        }
    }

    //更新条形图
    void updateBarCgart(double maxValue, int position) {

        Head head = originTableData.table.head.get(position);
        //选列后，如果数据小于当前点击位置，则显示主表格
        if (position >= originTableData.table.head.size()) {
            barChartListView.setVisibility(View.GONE);
            llBarHead.setVisibility(View.GONE);
            contentHorizontalScrollView.setVisibility(View.VISIBLE);
            topHorizontalScrollView.setVisibility(View.VISIBLE);
            contentListAdapter.notifyDataSetChanged();
            return;
        }

        tvHead.setText(head.getValue());
        setDrawableRightImg(tvHead, head);
        tableBarChartAdapter = new TableBarChartAdapter(mContext, tableBarCharts, maxValue, rowHeight, this);
        barChartListView.setAdapter(tableBarChartAdapter);
    }

    //设置表头右侧排序图标
    void setDrawableRightImg(TextView textView, Head head) {
        int ImageId;
        if (head.sort.equalsIgnoreCase("asc")) {
            ImageId = R.drawable.icon_sort_asc;
        } else if (head.sort.equalsIgnoreCase("desc")) {
            ImageId = R.drawable.icon_sort_desc;
        } else if (head.sort.equalsIgnoreCase("default")) {
            ImageId = R.drawable.icon_sort;
        } else {
            ImageId = R.drawable.icon_sort;
        }
        Drawable drawable = Utils.returnDrawable(mContext, ImageId);
        textView.setCompoundDrawables(null, null, drawable, null);
    }

    //条形图点击
    @Override
    public void barChartClick() {
        mLastTime = mCurTime;
        mCurTime = System.currentTimeMillis();
        //双击显示表格
        if (mCurTime - mLastTime < DOUBLE_CLICK_TIME) {
            barChartListView.setVisibility(View.GONE);
            llBarHead.setVisibility(View.GONE);
            contentHorizontalScrollView.setVisibility(View.VISIBLE);
            topHorizontalScrollView.setVisibility(View.VISIBLE);
            //解决返回后数据没有更新
            contentListAdapter.notifyDataSetChanged();
        }
    }

    TableChart generateShowTableData(TableChart tableChart) {
        List<Head> heads = new ArrayList<>();
        for (int i = 0; i < tableChart.table.main_data.size(); i++) {
            for (int j = 0; j < tableChart.table.main_data.get(i).size(); j++) {
                if (!tableChart.table.head.get(j).isShow()) {
                    tableChart.table.main_data.get(i).remove(j);
                } else {
                    Head head = tableChart.table.head.get(j);
                    heads.add(head);
                }
            }
        }

        tableChart.table.head = heads;
        return tableChart;
    }

    /*
     * 返回
    */
    public void dismissActivity(View v) {
        TableActivity.this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
