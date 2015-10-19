package com.putaotown.community.models;

import com.putaotown.community.models.ModelTieReply;

public class ResSubmiTieReply
{
	private boolean stat;
	private int errcode;
	private ModelTieReply tiereply;
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
	public ModelTieReply getTiereply() {
		return tiereply;
	}
	public void setTiereply(ModelTieReply tiereply) {
		this.tiereply = tiereply;
	}
}