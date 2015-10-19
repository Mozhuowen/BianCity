package com.putaotown.net.objects;

import java.util.List;

public class ResponsePutao
{
	private boolean stat;
	private int errcode;
	private List<PackagePutao> putao;
	
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
	public List<PackagePutao> getPutao() {
		return putao;
	}
	public void setPutao(List<PackagePutao> putao) {
		this.putao = putao;
	}
	
	
}