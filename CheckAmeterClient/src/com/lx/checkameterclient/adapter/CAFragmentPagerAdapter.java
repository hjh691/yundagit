package com.lx.checkameterclient.adapter;

/**
 * CA前缀的意思是Check Ameter
 * 这个类是电表校验界面导航条栏目对应的Fragment对象使用ViewPager时的适配器
 */

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class CAFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragments;
	private FragmentManager fm;

	public CAFragmentPagerAdapter(FragmentManager fm) {
		super(fm);

		this.fm = fm;
	}

	public CAFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
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
}
