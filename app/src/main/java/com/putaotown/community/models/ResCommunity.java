package com.putaotown.community.models;

import java.util.List;

public class ResCommunity
{
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
	public List<ModelCommunity> getComunity() {
		return comunity;
	}
	public void setComunity(List<ModelCommunity> comunity) {
		this.comunity = comunity;
	}
	private boolean stat;
	private int errcode;
	private List<ModelCommunity> comunity;
}