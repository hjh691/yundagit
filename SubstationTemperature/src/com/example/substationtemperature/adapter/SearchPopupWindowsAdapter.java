package com.example.substationtemperature.adapter;

import com.example.substationtemperature.R;
import com.example.substationtemperature.base.Search;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchPopupWindowsAdapter extends CommonBaseAdapter<Search> {

    public SearchPopupWindowsAdapter(Context context) {
        super(context);
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_list_selector, viewGroup, false);
        if (view != null) {
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout_search);
            TextView textView = (TextView) view.findViewById(R.id.textView_listView_selector);
            final ImageView imageView = (ImageView) view.findViewById(R.id.image_search_check);
            imageView.setImageResource(itemList.get(i).isChecked() ? R.drawable.icon_selected : R.drawable.icon_unselected);
            textView.setText(itemList.get(i).getKeyWord());
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemList.get(i).setChecked(itemList.get(i).isChecked() ? false : true);
                    imageView.setImageResource(itemList.get(i).isChecked() ? R.drawable.icon_selected : R.drawable.icon_unselected);
                }
            });
        }
        return view;
    }

}
