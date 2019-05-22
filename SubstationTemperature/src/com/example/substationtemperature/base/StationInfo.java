package com.example.substationtemperature.base;

public class StationInfo {
	
	private  int StationID=0;
	private  String StationName="";
	public StationInfo(){
		
	}
	public  void setStationID(int id){
		StationID=id;
	}
	public  int getStationID(){
		return StationID;
	}
	public  void setStationName(String name){
		StationName=name;
	}
	public  String getStationName(){
		return StationName;
	}
}
