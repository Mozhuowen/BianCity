package com.putaotown.net.objects;

public class ModelFavorite extends BaseRequest
{
	private int putaoid;
	/**收藏动作 0-收藏 1-取消收藏*/
	private int action;
	/**当前是否收藏*/
	private boolean dofavori;
	public int getPutaoid() {
		return putaoid;
	}
	public void setPutaoid(int putaoid) {
		this.putaoid = putaoid;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public boolean getDofavori() {
		return dofavori;
	}
	public void setDofavori(boolean dofavori) {
		this.dofavori = dofavori;
	}
}