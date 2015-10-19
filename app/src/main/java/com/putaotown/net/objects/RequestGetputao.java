package com.putaotown.net.objects;

public class RequestGetputao extends BaseRequest
{
	private int townid;		//目标边城id
	private int position;	//开始获取的故事数量,刚开始获取时为0，服务器每次返回15个，下次获取为15，以此类推

	public void setPosition(int p) {
		this.position = p;
	}
	public int getPosition() {
		return this.position;
	}
	public int getTownid() {
		return townid;
	}

	public void setTownid(int townid) {
		this.townid = townid;
	}

}