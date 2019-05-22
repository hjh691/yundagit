package com.example.substationtemperature.base;

import java.util.Date;

public class MeasureData {
	private String type;
	private long stationid;
	private String stationname;
	private long sensorsid;
	private String sensorsname;
	private long dataid;
	private double value;
	private String time;
	
	public MeasureData(){
		
	}
	public String getType(){
		return type;
	}
	public void setType(String Type){
		type=Type;
	}
	public long getStationID(){
		return stationid;
	}
	public void setStationID(long stationId){
		stationid=stationId;
	}
	public String getStationName(){
		return stationname;
	}
	public void setStationName(String sta){
		this.stationname=sta;
	}
	public double getValue(){
		return value;
	}
	public void setValue(double d){
		this.value=d;
	}
	public long getSensorId(){
		return sensorsid;
	}
	public void setSensorId(long id){
		this.sensorsid=id;
	}
	public String getSensorsName(){
		return sensorsname;
	}
	public void setSensorsName(String name){
		this.sensorsname=name;
	}
	public long getdataId(){
		return dataid;
	}
	public void setDataID(long dataid){
		this.dataid=dataid;
	}
	public String getTime(){
		return time;
	}
	public void setTime(String time){
		this.time=time;
	}
}
