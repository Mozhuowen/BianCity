package com.putaotown.net.objects;

public class ModelDelete extends BaseRequest
{
	/**删除对象类型 0-边城 1-故事*/
	private int type;
	/**对应对象的id*/
	private int id;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}