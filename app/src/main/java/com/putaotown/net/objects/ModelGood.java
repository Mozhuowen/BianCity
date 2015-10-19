package com.putaotown.net.objects;

public class ModelGood extends BaseRequest
{
	/**被赞对象类型0-边城 1-故事 2-评论 3-留言*/
	private int type;
	/**0-加赞 1-减赞*/
	private int action;
	/**赞总数*/
	private int goods;
	/**被赞对象类型的id*/
	private int targetid;
	/**当前用户对该对象类型是否为赞状态*/
	private boolean dogood;
	public void setDogood(boolean d) {
		this.dogood = d;
	}
	public boolean getDogood(){
		return this.dogood;
	}
	public void setTargetid(int t){
		this.targetid = t;
	}
	public int getTargetid(){
		return this.targetid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getGoods() {
		return goods;
	}
	public void setGoods(int goods) {
		this.goods = goods;
	}
}