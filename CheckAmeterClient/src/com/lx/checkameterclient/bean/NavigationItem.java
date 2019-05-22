package com.lx.checkameterclient.bean;

/*
 * 导航栏栏目类
 */
public class NavigationItem {

	//栏目对应ID
	private Integer id;

	//栏目对应NAME
	private String name;

	//栏目在整体中的排序顺序
	private Integer orderId;

	//栏目是否选中
	private Integer selected;

	public NavigationItem(Integer id, String name, int orderId, int selected) {
		super();
		this.id = id;
		this.name = name;
		this.orderId = orderId;
		this.selected = selected;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getSelected() {
		return selected;
	}

	public void setSelected(Integer selected) {
		this.selected = selected;
	}
	
	public String toString() {
		return "columnItem[id=" + this.id + ", name=" + this.name
				+ ", selected=" + this.selected + "]";
	}
	
	
}
