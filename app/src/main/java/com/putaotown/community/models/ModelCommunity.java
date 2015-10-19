package com.putaotown.community.models;

import com.putaotown.net.objects.BaseRequest;

public class ModelCommunity extends BaseRequest
{
	private int id;			//社区id
	private String cover;	//社区封面
	private String name;	//社区名
	private String address;	//地址
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}