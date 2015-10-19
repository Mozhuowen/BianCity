package com.putaotown.net.objects;

import java.util.List;
/**
 * 用于获取订阅情况，ModelSubscritown获取对某一town是否订阅，
 * List<AppleTown>获取某一用户所有的订阅town集合
 * @author awen
 *
 */
public class ResponseSubscri
{
	private boolean stat;
	private int errcode;
	private ModelSubscriTown subscri;
	private List<ApplyTown> towns;
	public void setTowns(List<ApplyTown> t) {
		this.towns = t;
	}
	public List<ApplyTown> getTowns() {
		return this.towns;
	}
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
	public ModelSubscriTown getSubscri() {
		return subscri;
	}
	public void setSubscri(ModelSubscriTown subscri) {
		this.subscri = subscri;
	}
}