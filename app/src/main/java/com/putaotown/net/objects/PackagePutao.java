package com.putaotown.net.objects;

import java.io.Serializable;
import java.util.List;

public class PackagePutao extends BaseRequest implements Serializable
{
	private int userid; 	//故事创建者id		
	private int townid;		//故事所属的边城id
	private int putaoid;	//故事id
	private String title;	//故事标题
	private String content;	//故事内容
	private String cover;	//故事封面
	private String usercover;	//故事创建者头像
	private String username;	//故事创建者用户名
	private String createtime;	//故事创建时间
	private List<Image> images;	//故事包含的图片
	private List<String> imagenames;	//故事包含的图片名
	private int goods;			//故事获赞数
	public void setGoods(int g) {
		this.goods = g;
	}
	public int getGoods(){
		return this.goods;
	}
	public void setUsercover(String u) {
		this.usercover = u;
	}
	public String getUsercover() {
		return this.usercover;
	}
	public void setCreatetime(String c) {
		this.createtime = c;
	}
	public String getCreatetime() {
		return this.createtime;
	}
	public void setUsername(String u) {
		this.username = u;
	}
	public String getUsername() {
		return this.username;
	}
	public void setImagenames(List<String> i) {
		this.imagenames = i;
	}
	public List<String> getImagenames() {
		return this.imagenames;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getTownid() {
		return townid;
	}
	public void setTownid(int townid) {
		this.townid = townid;
	}
	public int getPutaoid() {
		return putaoid;
	}
	public void setPutaoid(int putaoid) {
		this.putaoid = putaoid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	
}