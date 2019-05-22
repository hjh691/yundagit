package com.example.substationtemperature.base;

public class UserInfo {

	public int user_id;
	public String name;
	public String password;
	public Boolean isremember;
	public String phone;
	public String danwei;
	
	public UserInfo(){
		user_id=0;
		name="";
		password="";
		isremember=false;
		phone="";
		danwei="";
	}
}
