package com.putaotown.community.models;

import java.util.List;

import com.putaotown.net.objects.BaseRequest;

public class ModelSimple extends BaseRequest
{
	private int communityid;
	private int zhulouid;
	private int position;
	private List<Integer> rejectids;
	public int getCommunityid() {
		return communityid;
	}
	public void setCommunityid(int communityid) {
		this.communityid = communityid;
	}
	public int getZhulouid() {
		return zhulouid;
	}
	public void setZhulouid(int zhulouid) {
		this.zhulouid = zhulouid;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public List<Integer> getRejectids() {
		return rejectids;
	}
	public void setRejectids(List<Integer> rejectids) {
		this.rejectids = rejectids;
	}
}