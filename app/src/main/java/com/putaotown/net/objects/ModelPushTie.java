package com.putaotown.net.objects;

import com.putaotown.community.models.ModelTie;
import com.putaotown.community.models.ModelTieTheme;

public class ModelPushTie
{
	private int tietype;	//0-主题帖 1-普通贴
	private long time;		//时间
	private int floot;		//楼层，暂时弃用
	private ModelTieTheme tieth;	//当tietype=0时该字段由值
	private ModelTie tie;			//当tietype=1时该字段有值
	private int adminid;			//帖子所属社区的管理员（创建者）userid
	private String tieth_title;		//新增，主题贴题目
	public String getTieth_title() {
		return tieth_title;
	}
	public void setTieth_title(String tieth_title) {
		this.tieth_title = tieth_title;
	}
	public int getTietype() {
		return tietype;
	}
	public void setTietype(int tietype) {
		this.tietype = tietype;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getFloot() {
		return floot;
	}
	public void setFloot(int floot) {
		this.floot = floot;
	}
	public ModelTieTheme getTieth() {
		return tieth;
	}
	public void setTieth(ModelTieTheme tieth) {
		this.tieth = tieth;
	}
	public ModelTie getTie() {
		return tie;
	}
	public void setTie(ModelTie tie) {
		this.tie = tie;
	}
	public int getAdminid() {
		return adminid;
	}
	public void setAdminid(int adminid) {
		this.adminid = adminid;
	}
}