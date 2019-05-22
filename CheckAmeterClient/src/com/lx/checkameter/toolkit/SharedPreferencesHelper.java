/*
 * 添加sharedPreferences配置文件
 * 
 * 内容如下
 * 
 * <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
 * <map>
 * <boolean name="show_gude_page" value="true" />
 * </map>
 * 
 */

package com.lx.checkameter.toolkit;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesHelper {
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	String fileName = "my_configuration";
	public final static String SHOW_GUIDE_PAGE = "show_gude_page";
	public final static String CONFIGURATION_FILE_NAME = "my_configuration";
	

	Context context;

	public SharedPreferencesHelper(Context c, String name) {
		context = c;
		sp = context.getSharedPreferences(name, 0);
		editor = sp.edit();
	}

	public void putValue(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getValue(String key) {
		return sp.getString(key, null);
	}
	
	public void putGuidePageSwitchValue(boolean value){
		editor = sp.edit();
		editor.putBoolean(SHOW_GUIDE_PAGE, value);
		editor.commit();
	}
	
	public boolean getGuidePageSwitchValue(){
		return sp.getBoolean(SHOW_GUIDE_PAGE, true); //第二个是默认值
	}
}
