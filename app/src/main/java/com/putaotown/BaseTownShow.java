package com.putaotown;

import java.util.List;

import com.putaotown.geographic.GeoInfo;
import com.putaotown.net.objects.ApplyTown;

public interface BaseTownShow
{
	/**加载hot动作*/
	public void loadHot(List<Integer> rejectid);
	/**加载near动作*/
	public void loadNear(GeoInfo geo,List<Integer> rejectid);
	/**数据返回回调*/
	public void onReceive(List<ApplyTown> towns);
}