package com.putaotown.net.objects;

import com.putaotown.net.SLoadImage;

/**尺寸定义类，用于适应自定义尺寸-又拍云尺寸*/
public class ImgRecy
{
	public String imgname;	
	public String rank;
	public int irank;
	public int size;	//photo像素大小
	public boolean isShow = false;
	public boolean isheadcover = false;

	public ImgRecy(){}
	public ImgRecy(String imagename,int size) {
		this.imgname = imagename;
		this.size = size;
		if ( size >0 && size <=200) {
			rank = SLoadImage.STRSMALL;
			irank = SLoadImage.SMALL;
		} else if ( size >200 && size <=500) {
			rank = SLoadImage.STRMEDIUM;
			irank = SLoadImage.MEDIUM;
		} else {
			rank = SLoadImage.STRLARGE;
			irank = SLoadImage.LARGE;
		}
	}
	
	public ImgRecy(String imagename,int size,boolean isheadcover) {
		this.imgname = imagename;
		this.size = size;
		this.isheadcover = isheadcover;
	}
	
	public String getRecyStr() {
		return imgname + rank + size;
	}
}