package com.putaotown.net.objects;

import java.util.List;

public class ResponseComment
{
	private boolean stat;
	private int errcode;
	private List<PackageComment> comments;
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
	public List<PackageComment> getComments() {
		return comments;
	}
	public void setComments(List<PackageComment> comments) {
		this.comments = comments;
	}
}