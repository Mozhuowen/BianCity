package com.putaotown.net.objects;

public class ResponseFavorite
{
	private boolean stat;
	private int errcode;
	private ModelFavorite favori;
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
	public ModelFavorite getFavori() {
		return favori;
	}
	public void setFavori(ModelFavorite favori) {
		this.favori = favori;
	}
}