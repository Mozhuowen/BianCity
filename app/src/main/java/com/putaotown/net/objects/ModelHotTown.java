package com.putaotown.net.objects;

import java.util.List;

public class ModelHotTown extends BaseRequest
{
	private List<Integer> rejectid;	//排除当前已经显示的ID列表
	public void setRejectid(List<Integer> r) {
		this.rejectid = r;
	}
	public List<Integer> getRejectid(){
		return this.rejectid;
	}
}