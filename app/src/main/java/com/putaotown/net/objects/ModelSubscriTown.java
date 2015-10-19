package com.putaotown.net.objects;

public class ModelSubscriTown extends BaseRequest
{
	private int townid;
	/**订阅动作 0-订阅 1-取消订阅*/
	private int action;
	/**订阅总数*/
	private int subscris;
	/**当前用户是否订阅*/
	private boolean dosubscri;
	public int getTownid() {
		return townid;
	}
	public void setTownid(int townid) {
		this.townid = townid;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getSubscris() {
		return subscris;
	}
	public void setSubscris(int subscri) {
		this.subscris = subscri;
	}
	public boolean getDosubscri() {
		return dosubscri;
	}
	public void setDosubscri(boolean dosubscri) {
		this.dosubscri = dosubscri;
	}
}