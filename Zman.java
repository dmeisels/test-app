package com.dmm.calendar.model;

public class Zman {
	private String zman=null;
	private String desc=null;
	
	public String getZman() {	return zman;}
	public void setZman(String zman) {	this.zman = zman;}
	public String getDesc() {return desc;}
	public void setDesc(String desc) {this.desc = desc;}
	
	public Zman(){}
			
	public Zman(String zman, String desc) {
		this.zman=zman;
		this.desc=desc;
	}
}
