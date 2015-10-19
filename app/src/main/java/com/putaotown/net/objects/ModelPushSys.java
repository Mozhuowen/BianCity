package com.putaotown.net.objects;

public class ModelPushSys
{
	private int systype;	//消息类型 0-全局消息 1-别名消息
	private long time;
	private String content;
	public int getSystype() {
		return systype;
	}
	public void setSystype(int systype) {
		this.systype = systype;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}