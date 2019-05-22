package com.example.substationtemperature.base;

public class GraphicInfo {

	public GraphicInfo(){};
	private long GraphicID=-1;
	private String GraphicName="";
	private String GraphicContent="";
	private  long StationId=-1;
	
	public void setGraphicID(long graphicid){
		GraphicID=graphicid;
	}
	public long getGraphicID(){
		return GraphicID;
	}
	public void setGraphicName(String graphicname){
		GraphicName=graphicname;
	}
	public String getGraphicName(){
		return GraphicName;
	}
	public void setGraphicContent(String content){
		GraphicContent=content;
	}
	public String getGraphicContent(){
		return GraphicContent;
	}
	public void setStationid(long stationid){
		StationId=stationid;
	}
	public long getStationId(){
		return StationId;
	}
}
