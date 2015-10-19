package com.putaotown.community.models;

import java.util.List;

public class ResGetTie
{
	private boolean stat;
	private int errcode;
	private List<ModelTie> ties;
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
	public List<ModelTie> getTies() {
		return ties;
	}
	public void setTies(List<ModelTie> ties) {
		this.ties = ties;
	}
}