package com.putaotown.community.models;

import java.util.List;

public class ResGetTieReplys
{
	private boolean stat;
	private int errcode;
	private List<ModelTieReply> replys;
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
	public List<ModelTieReply> getReplys() {
		return replys;
	}
	public void setReplys(List<ModelTieReply> replys) {
		this.replys = replys;
	}	
}