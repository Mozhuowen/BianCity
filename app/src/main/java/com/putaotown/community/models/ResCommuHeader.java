package com.putaotown.community.models;

public class ResCommuHeader
{
	private boolean stat;
	private int errcode;
	private ModelCommuHeader header;
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
	public ModelCommuHeader getHeader() {
		return header;
	}
	public void setHeader(ModelCommuHeader header) {
		this.header = header;
	}
}