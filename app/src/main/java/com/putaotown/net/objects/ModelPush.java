package com.putaotown.net.objects;

public class ModelPush
{
	private int type;   //0-评论 1-回复评论 2-点赞 3-系统消息
	private ModelPushComment mess;
	private ModelPushGood goodmess;
	private ModelPushSys sysmess;
	private ModelPushTie tiemess;	//社区消息
	private int messid;

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ModelPushComment getMess() {
		return mess;
	}
	public void setMess(ModelPushComment mess) {
		this.mess = mess;
	}
	public int getMessid() {
		return messid;
	}
	public void setMessid(int messid) {
		this.messid = messid;
	}
	public ModelPushGood getGoodmess() {
		return goodmess;
	}
	public void setGoodmess(ModelPushGood goodmess) {
		this.goodmess = goodmess;
	}
	public ModelPushSys getSysmess() {
		return sysmess;
	}
	public void setSysmess(ModelPushSys sysmess) {
		this.sysmess = sysmess;
	}
	public ModelPushTie getTiemess() {
		return tiemess;
	}
	public void setTiemess(ModelPushTie tiemess) {
		this.tiemess = tiemess;
	}
}