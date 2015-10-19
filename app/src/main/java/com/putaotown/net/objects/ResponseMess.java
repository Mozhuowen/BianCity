package com.putaotown.net.objects;

import java.util.List;

import com.putaotown.MessBoard;

public class ResponseMess
{
	private boolean stat;
	private int errcode;
	private List<ModelMessboard> mess;
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
	public List<ModelMessboard> getMess() {
		return mess;
	}
	public void setMess(List<ModelMessboard> mess) {
		this.mess = mess;
	}	
}