package com.example.substationtemperature.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class STFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragments;
	private FragmentManager fm;

	public STFragmentPagerAdapter(FragmentManager fm) {
		super(fm);

		this.fm = fm;
	}

	public STFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
		// TODO Auto-generated constructor stub
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int index) {
		return this.fragments.get(index);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		return obj;
	}
	//ViewPage上边的标题显示文字内容，对应于PagerTitleStrip或PagerTabStrip.
	@Override
	public CharSequence getPageTitle(int position){
		String[] itemNames = { "首页","实时状态", "实时数据", "历史数据", "趋势图", "告警信息", "短信日志" };//, "照相取证","文件列表"
		return itemNames[position];
	}
}
