package com.example.substationtemperature;

import com.example.substationtemperature.adapter.LaunchSimpleAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class LaunchSimpleActivity extends Activity{
	private int[] lanuchImageArray = {
			R.drawable.guide_bg1, R.drawable.guide_bg2, R.drawable.guide_bg3, R.drawable.guide_bg4};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		ViewPager vp_launch = (ViewPager) findViewById(R.id.vp_launch);
		LaunchSimpleAdapter adapter = new LaunchSimpleAdapter(this, lanuchImageArray);
		vp_launch.setAdapter(adapter);
		vp_launch.setCurrentItem(0);
	}
}
