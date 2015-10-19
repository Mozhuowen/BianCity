package com.putaotown.net.objects;

import java.util.List;

import com.putaotown.geographic.GeoInfo;

public class ModelNearTown extends BaseRequest
{
	private List<Integer> rejectid;
	private GeoInfo geo;
	public List<Integer> getRejectid() {
		return rejectid;
	}
	public void setRejectid(List<Integer> rejectid) {
		this.rejectid = rejectid;
	}
	public GeoInfo getGeo() {
		return geo;
	}
	public void setGeo(GeoInfo geo) {
		this.geo = geo;
	}
}