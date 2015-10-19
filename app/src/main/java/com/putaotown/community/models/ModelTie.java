package com.putaotown.community.models;

import java.util.List;

import com.putaotown.net.objects.BaseRequest;
import com.putaotown.net.objects.Image;
/**
 * 普通回复贴模型
 * @author awen
 *
 */
public class ModelTie extends BaseRequest
{
	private int tieid;	//帖子id
	private int userid;	//发帖者id
	private int communityid;	//社区id
	private int zhulouid;		//主楼（第一楼）帖子id
	private String username;	//发帖者用户名
	private String usercover;	//发帖者头像
	private long time;			//发帖时间
	private long lastreturntime; 	//最新回复时间
	private int goodcou;		//赞数量
	private String content;		//内容
	private int imagecou;		//包含的图片数量
	private List<Image> images;	//包含的图片，上传用
	private List<String> imagenames;	//包含的图片名，接收用
	private int floot;			//帖子楼层
	private List<ModelTieReply> replys;	//本帖包含的快速回复
	public int getTieid() {
		return tieid;
	}
	public void setTieid(int tieid) {
		this.tieid = tieid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsercover() {
		return usercover;
	}
	public void setUsercover(String usercover) {
		this.usercover = usercover;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getGoodcou() {
		return goodcou;
	}
	public void setGoodcou(int goodcou) {
		this.goodcou = goodcou;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getImagecou() {
		return imagecou;
	}
	public void setImagecou(int imagecou) {
		this.imagecou = imagecou;
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	public List<String> getImagenames() {
		return imagenames;
	}
	public void setImagenames(List<String> imagenames) {
		this.imagenames = imagenames;
	}
	public int getFloot() {
		return floot;
	}
	public void setFloot(int floot) {
		this.floot = floot;
	}
	public List<ModelTieReply> getReplys() {
		return replys;
	}
	public void setReplys(List<ModelTieReply> replys) {
		this.replys = replys;
	}
	public long getLastreturntime() {
		return lastreturntime;
	}
	public void setLastreturntime(long lastreturntime) {
		this.lastreturntime = lastreturntime;
	}
	public int getCommunityid() {
		return communityid;
	}
	public void setCommunityid(int communityid) {
		this.communityid = communityid;
	}
	public int getZhulouid() {
		return zhulouid;
	}
	public void setZhulouid(int zhulouid) {
		this.zhulouid = zhulouid;
	}
}