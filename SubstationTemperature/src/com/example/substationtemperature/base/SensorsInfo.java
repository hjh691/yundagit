package com.example.substationtemperature.base;

public class SensorsInfo {
	public SensorsInfo(){
		
	}
	private  int mStationID=0;
	private  String mStationName="";
	private  int mSensorsID=0;
	private  String mSensorsName="";
	private  int mSensorsnum=0;
	private  int mVoiceID=0;
	private  int mCollectorId;
	private  int mAddress;
	public void setSensorID(int id){
		mSensorsID=id;
	}
	public int getSensorID(){
		return mSensorsID;
	}
	public void setSensorName(String name){
		mSensorsName=name;
	}
	public String getSensorName(){
		return mSensorsName;
	}
	public void setStationID(int id){
		mStationID=id;
	}
	public int getStationID(){
		return mStationID;
	}
	public void setSensorNum(int num){
		mSensorsnum=num;
	}
	public int getSensorsNum(){
		return mSensorsnum;
	}
	public void setStationName(String stname){
		mStationName=stname;
	}
	public String getStationName(){
		return mStationName;
	}
	public void setVoiceID(int id){
		mVoiceID=id;
	}
	public int getVoiceID(){
		return mVoiceID;
	}
	public void setCollectorId(int id){
		mCollectorId=id;
	}
	public int getmCollectorId(){
		return mCollectorId;
	}
	public void setAddress(int address){
		mAddress=address;
	}
	public int getAddress(){
		return mAddress;
	}
}
