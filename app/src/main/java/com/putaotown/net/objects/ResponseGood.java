package com.putaotown.net.objects;

public class ResponseGood
{
	private boolean stat;
	private int errcode;
	private ModelGood good;
	public boolean isStat() {
		return stat;
	}
	public void setStat(boolean stat) {
		this.stat = stat;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public ModelGood getGood() {
		return good;
	}
	public void setGood(ModelGood good) {
		this.good = good;
	}
}