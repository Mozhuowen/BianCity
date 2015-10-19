package com.putaotown.net.objects;

import java.util.List;

public class ResSearch
{
	private boolean stat;
	private int errcode;
	private List<ApplyTown> towns;
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
	public List<ApplyTown> getTowns() {
		return towns;
	}
	public void setTowns(List<ApplyTown> towns) {
		this.towns = towns;
	}
}