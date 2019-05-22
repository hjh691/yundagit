package com.example.substationtemperature.view;

import java.util.List;

import com.example.substationtemperature.R;
import com.example.substationtemperature.adapter.SearchPopupWindowsAdapter;
import com.example.substationtemperature.base.DensityUtil;
import com.example.substationtemperature.base.Search;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MultiSelectPopupWindows extends PopupWindow {

    private Context context;
    private View parent;
    private List<Search> data;
    private int yStart;
    private SearchPopupWindowsAdapter adapter;

    public MultiSelectPopupWindows(Context context, View parent, int yStart, List<Search> data) {
        this.context = context;
        this.parent = parent;
        this.yStart = yStart;
        this.data = data;
        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.popupwindows_multiselect, null);
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_slow));
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_selector);
        linearLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.list_top_in));
        ListView listView = (ListView) view.findViewById(R.id.listView_selector);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.TOP, 0, DensityUtil.dip2px(context, yStart));
        update();

        initListView(listView, data);
    }

    private void initListView(ListView listView, List<Search> data) {
        adapter = new SearchPopupWindowsAdapter(context);
        adapter.setItems(data);
        listView.setAdapter(adapter);
    }

    public List getItemList() {
        return adapter.getItemList();
    }
}
