package com.example.substationtemperature.base;

public class AlertInfo {
	private long StationID=0;
	private String StationName="";
	private long SensorID=0;
	private String SensorName="";
	private long DataId=0;
	private String CollectTime="";
	private double TValue=0.0f;
	private String LogText="";
	public boolean isAlert=false;
	
	public void setStationId(long stationid){
		this.StationID=stationid;
	}
	public long getStation(){
		return StationID;
	}
	
	public void setStationName(String stationname){
		StationName=stationname;
	}
	public String getStationName(){
		return StationName;
	}
	
	public void setSensorID(long sensorid){
		this.SensorID=sensorid;
	}
	public long getSensorID(){
		return SensorID;
	}
	public void setSensorName(String sensorname){
		this.SensorName=sensorname;
	}
	public String getSensorName(){
		return SensorName;
	}
	public void setDataId(long dataid){
		this.DataId=dataid;
	}
	public long getDataId(){
		return DataId;
	}
	public void setCollectTime(String collecttime){
		this.CollectTime=collecttime;
	}
	public String getCollectTime(){
		return CollectTime;
	}
	public void setTValue(double tvalue){
		this.TValue=tvalue;
	}
	public double getTValue(){
		return TValue;
	}
	
	public void setLogText(String logtext){
		this.LogText=logtext;
	}
	public String getLogText(){
		return LogText;
	}

}
