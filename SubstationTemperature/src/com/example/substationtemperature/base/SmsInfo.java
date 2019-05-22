package com.example.substationtemperature.base;

public class SmsInfo {
	private int SmsHistoryID= 0;
	private String StationName="";
	private String PersonName;
	private String Telephone="";
	private String Message="";
	private String MessageTime="2019-01-03T00:49:34.008Z";
	private Boolean IsSuccess=true;
	
	public SmsInfo(){
		
	}
	public void setSmsHistoryID(int smshistoryid){
		SmsHistoryID=smshistoryid;
	}
	public int getSmsHistoryID(){
		return SmsHistoryID;
	}
	public void setTelephone(String telephone){
		Telephone=telephone;
	}
	public String getTelephone(){
		return Telephone;
	}
	public void setMessage(String message){
		Message=message;
	}
	public String getMessage(){
		return Message;
	}
	public void setMessageTime(String messagetime){
		MessageTime=messagetime;
	}
	public String getMessageTime(){
		return MessageTime;
	}
	public void setIsSuccess(Boolean issuccess){
		IsSuccess=issuccess;
	}
	public Boolean getIsSuccess(){
		return IsSuccess;
	}
	public void setStationName(String stationname){
		StationName=stationname;
	}
	public String getStationNamee(){
		return StationName;
	}
	public void setPersonName(String personname){
		PersonName=personname;
	}
	public String getPersonName(){
		return PersonName;
	}
}
