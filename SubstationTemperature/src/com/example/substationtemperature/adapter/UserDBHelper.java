package com.example.substationtemperature.adapter;

import java.util.ArrayList;

import com.example.substationtemperature.base.UserInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBHelper extends SQLiteOpenHelper{

	private static final String TAG="UserDBHelper";
	private static final String DB_NAME="user_db";
	private static final int DB_VERSION=1;
	private static UserDBHelper mHelper=null;
	private SQLiteDatabase mDB=null;
	private static final String TABLE_NAME="user_info";
	
	public UserDBHelper(Context context){
		super(context,DB_NAME,null,DB_VERSION);
	}
	public UserDBHelper(Context context,int version){
		super(context,DB_NAME,null,version);
	}
	
	public UserDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public static UserDBHelper getInstance(Context context,int version){
		if(version>0 && mHelper==null){
			mHelper=new UserDBHelper(context,version);
		}else if(mHelper==null){
			mHelper=new UserDBHelper(context);
		}
		return mHelper;
	}
	
	public SQLiteDatabase openReadLink(){
		if(mDB==null||mDB.isOpen()!=true){
			mDB=mHelper.getReadableDatabase();
		}
		return mDB;
	}
	
	public SQLiteDatabase openWriteLink(){
		if(mDB==null || mDB.isOpen()!=true){
			mDB=mHelper.getWritableDatabase();
		}
		return mDB;
	}
	
	public void closeLink(){
		if(mDB!=null && mDB.isOpen()==true){
			mDB.close();
			mDB=null;
		}
	}
	
	public String getDBName(){
		if(mHelper!=null){
			return mHelper.getDatabaseName();
		}else{
			return DB_NAME;
		}
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.d(TAG,"on_create database");
		String drop_sql="DROP TABLE IF EXISTS "+TABLE_NAME+";";
		db.execSQL(drop_sql);
		String creat_sql="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("
		+"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
		+"name VARCHAR NOT NULL,"+"password VARCHAR,isremember INTEGER NOT NULL,"
		+"phone VARCHAR(20),danwei VARCHAR,email VARCHAR)";
		
		db.execSQL(creat_sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onUpdata oleVersion="+oldVersion+",newVersion="+newVersion);
		if(newVersion>1){
			//Android 的ALERT 命令不支持一次添加多列，只能分多次添加
			String alter_sql="ALTER TABLE"+TABLE_NAME+"ADD COLUMN"+"phone VARCHAR;";
			db.execSQL(alter_sql);
			alter_sql="ALTER TABLE"+TABLE_NAME+"ADD COLUMN"+"danwei VARCHAR;";
			db.execSQL(alter_sql);
			alter_sql="ALTER TABLE"+TABLE_NAME+"ADD COLUMN"+"e_mail VARCHAR;";
			db.execSQL(alter_sql);
		}
	}
	
	public int delete(String condition){
		int count=mDB.delete(TABLE_NAME, condition, null);
		return count;
	}
	
	public int deleteAll(){
		int count=mDB.delete(TABLE_NAME, "1=1", null);
		return count;
	}
	
	public long insert(UserInfo info) {
		ArrayList<UserInfo> infoArray = new ArrayList<UserInfo>();
		infoArray.add(info);
		return insert(infoArray);
	}
	public long insert(ArrayList<UserInfo>infoArray){
		long result=-1;
		for(int i=0;i<infoArray.size();i++){
			UserInfo info=infoArray.get(i);
			ArrayList<UserInfo> tempArray=new ArrayList<UserInfo>();
			//如果存在同名记录，就更新记录，注意条件语句的等号后面要用单引号括起来
			if(info.name!=null && info.name.length()>0){
				String condition=String.format("name='%s'", info.name);
				tempArray=query(condition);
				if(tempArray.size()>0){
					update(info,condition);
					result=tempArray.get(0).user_id;
					continue;
				}
			}
			//如果不存在唯一性重复的记录，就插入新记录
			ContentValues cv=new ContentValues();
			//cv.put("_id",null);
			cv.put("name", info.name);
			cv.put("password", info.password);
			cv.put("isremember", info.isremember);
			cv.put("phone", info.phone);
			cv.put("danwei", info.danwei);
			result=mDB.insert(TABLE_NAME, null, cv);
			if(result==-1){
				return result;
			}
		}
		return result;
	}
	public int update(UserInfo info, String condition) {
		// TODO Auto-generated method stub
		ContentValues cv=new ContentValues();
		//cv.put("_id",1);
		cv.put("name", info.name);
		cv.put("password", info.password);
		cv.put("isremember", info.isremember);
		cv.put("phone", info.phone);
		cv.put("danwei", info.danwei);
		int count=mDB.update(TABLE_NAME, cv ,condition,null);
		return count;
	}
	
	public int update(UserInfo info){
		return update(info,"user_id"+info.user_id);
	}
	private ArrayList<UserInfo> query(String condition) {
		// TODO Auto-generated method stub
		String sql=String.format("select _id,name,password,phone,danwei from %s where %s;", TABLE_NAME,condition);
		ArrayList<UserInfo> infoArray=new ArrayList<UserInfo>();
		Cursor cursor=mDB.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			for(;;cursor.moveToNext()){
				UserInfo info=new UserInfo();
				//info.user_id=cursor.getInt(0);
				info.name=cursor.getString(1);
				info.password=cursor.getString(2);
				info.danwei =cursor.getString(4);
				infoArray.add(info);
				if(cursor.isLast()==true){
					break;
				}
			}
		}
		cursor.close();
		return infoArray;
	}
	public UserInfo queryByName(String username){
		UserInfo info =null;
		ArrayList<UserInfo> infoArray=query(String.format("name='%s'", username));
		if(infoArray.size()>0){
			info=infoArray.get(0);
		}
		return info;
	}
	
	public UserInfo queryByPhone(String phone){
		UserInfo info=null;
		ArrayList<UserInfo> infoArray=query(String.format("phone='%s'", phone));
		if(infoArray.size()>0){
			info=infoArray.get(0);
		}
		return info;
	}

}
