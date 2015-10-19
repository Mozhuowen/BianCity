package com.putaotown.community.models;

import java.util.List;

import com.putaotown.net.objects.BaseRequest;
import com.putaotown.net.objects.Image;
import com.putaotown.net.objects.ModelUser;
/**
 * 主题贴模型
 * @author awen
 *
 */
public class ModelTieTheme extends BaseRequest
{
	private int communityid;	//社区id
	private int tieid;	//帖子id
	private long time;	//发帖时间
	private int goodcou;	//赞数量
	private boolean dogood;	//当前用户是否对该帖子点赞
	private int commentcou;	//回复数量
	private String title;	//标题
	private String content;	//帖子内容
	private int imagecou;	//该帖子包含的照片数量
	private List<Image> images;	//包含的图片，上传时用
	private List<String> imagenames;	//包含的图片名，接收时用
	private int floot;			//帖子楼层
	private int userid;			//楼主id
	private String username;	//楼主用户名
	private String usercover;	//楼主头像
	private int top;		//0-不置顶，1-置顶
	public int getTieid() {
		return tieid;
	}
	public void setTieid(int tieid) {
		this.tieid = tieid;
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
	public int getCommentcou() {
		return commentcou;
	}
	public void setCommentcou(int commentcou) {
		this.commentcou = commentcou;
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
	public int getCommunityid() {
		return communityid;
	}
	public void setCommunityid(int communityid) {
		this.communityid = communityid;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public boolean isDogood() {
		return dogood;
	}
	public void setDogood(boolean dogood) {
		this.dogood = dogood;
	}
}