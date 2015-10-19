package com.putaotown.community.models;

import com.putaotown.net.objects.BaseRequest;

/**
 * 社区列表头置顶贴模型
 * @author awen
 *
 */
public class ModelTopTie extends BaseRequest
{
	private int tieid;	//帖子ID
	private String title;	//帖子标题
	private long time;		//发帖时间
	public int getTieid() {
		return tieid;
	}
	public void setTieid(int tieid) {
		this.tieid = tieid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}