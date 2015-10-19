package com.putaotown.net.objects;

import java.io.Serializable;

public class Image implements Serializable
{
	private String imagename;	//图片名
	private String md5;			//图片md5值
	private int size;			//图片大小	
	private int list_index;		//图片在故事中的序列,0-7
	
	public void setList_index(int l) {
		this.list_index = l;
	}
	public int getList_index() {
		return this.list_index;
	}
	public Image(String name,String md5,int s,int index) {
		this.imagename = name;
		this.md5 = md5;
		this.size = s;
		this.list_index = index;
	}
	public String getImagename() {
		return imagename;
	}
	public void setImagename(String imagename) {
		this.imagename = imagename;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public boolean equals(Image image) {
		if (image.imagename.equals(imagename))
			return true;
		else
			return false;
	}
}