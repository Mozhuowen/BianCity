package com.putaotown.community.models;

public class ResJoinBBS
{
	private boolean stat;
	private int errcode;
	private int memebercount;
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
	public int getMemebercount() {
		return memebercount;
	}
	public void setMemebercount(int memebercount) {
		this.memebercount = memebercount;
	}
}