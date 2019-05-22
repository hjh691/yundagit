package com.lx.checkameterclient.bean;

import java.util.Date;

public class AmeterData {

	private String ameterNo;
	private Date testDate;
	private String userName;
	private String userAddress;
	private String line; // 线路
	private String station; // 台站
	private String inspector; // 检验员
	private String corector; // 校核员
	private String ameterSytle;//电表制式
	private String ameterClass;//电表等级
	private String voltage_range;//电压量程
	private String current_range;//电流量程
	private String dianbiaochangshu;//电表常数
	private String jiaobiaoquanshu;//校表圈数
	private String dianyaxishu;//电压系数
	private String dianliuxishu;//电流系数
	private String fenpinxishu;//分频系数
	private String maichongfangshi;//脉冲方式
	private String ameterType;//电表型号
	private String ameterFactory;//电表厂家
	private String fengshidianliang;//峰时电量
	private String pingshidianliang;//平时电量
	private String gushidianliang;//谷时电量
	private String zhengzongyougong;//正向总有功
	private String zhengzongwugong;//正向总无功
	private String fanxiangwugong;//反向无功

	
	
	public AmeterData(){
		
	}
	public AmeterData(String ameterNo, Date testDate, String userName, String userAddress, String line, String station,
			String inspector, String corector, String ameterSytle,String ameterClass,String voltage_range,
			String current_range,String dianbiaochangshu,String jiaobiaoquanshu, String dianyaxishu, String dianliuxishu,
			String fenpinxishu,String maichongfangshi,String ameterType,String ameterFactory,String fengshidianliang,
			String pingshidianliang,String gushidianliang,String zhengzongyougong,String zhengzongwugong,String fanxiangwugong) {
		super();
		this.ameterNo = ameterNo;
		this.testDate = testDate;
		this.userName = userName;
		this.userAddress = userAddress;
		this.line = line;
		this.station = station;
		this.inspector = inspector;
		this.corector = corector;
		this.ameterSytle=ameterSytle;
		this.ameterClass=ameterClass;
		this.voltage_range=voltage_range;
		this.current_range=voltage_range;
		this.dianbiaochangshu=dianbiaochangshu;//电表常数
		this.jiaobiaoquanshu=jiaobiaoquanshu;//校表圈数
		this.dianyaxishu=dianyaxishu;//电压系数
		this.dianliuxishu=dianliuxishu;//电流系数
		this.fenpinxishu=fenpinxishu;//分频系数
		this.maichongfangshi=maichongfangshi;//脉冲方式
		this.ameterType=ameterType;//电表型号
		this.ameterFactory=ameterFactory;//电表厂家
		this.fengshidianliang=fengshidianliang;//峰时电量
		this.pingshidianliang=pingshidianliang;//平时电量
		this.gushidianliang=gushidianliang;//谷时电量
		this.zhengzongyougong=zhengzongyougong;//正向总有功
		this.zhengzongwugong=zhengzongwugong;//正向总无功
		this.fanxiangwugong=fanxiangwugong;//反向无功
		
	}

	public String getAmeterNo() {
		return ameterNo;
	}

	public void setAmeterNo(String ameterNo) {
		this.ameterNo = ameterNo;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getInspector() {
		return inspector;
	}

	public void setInspector(String inspector) {
		this.inspector = inspector;
	}

	public String getCorector() {
		return corector;
	}

	public void setCorector(String corector) {
		this.corector = corector;
	}
	public String getAmeterSytle() {
		return ameterSytle;
	}

	public void setAmeterSytle(String ameterSytle) {
		this.ameterSytle = ameterSytle;
	}
	public String getAmeterClass() {
		return ameterClass;
	}

	public void setAmeterClass(String ameterClass) {
		this.ameterClass = ameterClass;
	}
	public String getVoltagerange() {
		return voltage_range;
	}

	public void setVoltagerange(String voltagerange) {
		this.voltage_range = voltagerange;
	}
	
	public String getCurrentrange() {
		return current_range;
	}

	public void setCurrentrange(String currentrange) {
		this.current_range = currentrange;
	}
	public String getDianbiaochangshu() {
		return dianbiaochangshu;
	}

	public void setDianbiaochangshu(String dianbiaochangshu) {
		this.dianbiaochangshu = dianbiaochangshu;
	}
	public String getJiaobiaoquanshu() {
		return jiaobiaoquanshu;
	}

	public void setJiaobiaoquanshu(String jiaobiaoquanshu) {
		this.jiaobiaoquanshu = jiaobiaoquanshu;
	}
	public String getDianyaxishu() {
		return dianyaxishu;
	}

	public void setDianyaxishu(String dianyaxishu) {
		this.dianyaxishu = dianyaxishu;
	}
	
	public String getDianliuxishu() {
		return dianliuxishu;
	}

	public void setDianliuxishu(String dianliuxishu) {
		this.dianliuxishu = dianliuxishu;
	}
	
	public String getFenpinxishu() {
		return fenpinxishu;
	}

	public void setFenpinxishu(String fenpinxishu) {
		this.fenpinxishu = fenpinxishu;
	}
	
	public String getMaichongfangshi() {
		return maichongfangshi;
	}

	public void setMaichongfangshi(String maichongfangshi) {
		this.maichongfangshi = maichongfangshi;
	}
	
	public String getAmeterType() {
		return ameterType;
	}

	public void setAmeterType(String ametertype) {
		this.ameterType = ametertype;
	}
	
	public String getAmeterFactory() {
		return ameterFactory;
	}

	public void setAmeterFactory(String ameterfactory) {
		this.ameterFactory = ameterfactory;
	}
	
	public String getFengshidianliang() {
		return fengshidianliang;
	}

	public void setFengshidianliang(String fengshidianliang) {
		this.fengshidianliang = fengshidianliang;
	}
	
	public String getPingshidianliang() {
		return pingshidianliang;
	}

	public void setPingshidianliang(String pingshidianliang) {
		this.pingshidianliang = pingshidianliang;
	}
	
	public String getGushidianliang() {
		return gushidianliang;
	}

	public void setGushidianliang(String gushidianliang) {
		this.gushidianliang = gushidianliang;
	}
	
	public String getZhengzongyougong() {
		return zhengzongyougong;
	}

	public void setZhengzongyougong(String zhengzongyougong) {
		this.zhengzongyougong = zhengzongyougong;
	}
	
	public String getZhengzongwugong() {
		return zhengzongwugong;
	}

	public void setZhengzongwugong(String zhengzongwugong) {
		this.zhengzongwugong = zhengzongwugong;
	}
	
	public String getFanxiangwugong() {
		return fanxiangwugong;
	}

	public void setFanxiangwugong(String fanxiangwugong) {
		this.fanxiangwugong = fanxiangwugong;
	}

}
