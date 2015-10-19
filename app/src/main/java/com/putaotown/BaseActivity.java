package com.putaotown;

import com.putaotown.net.objects.ModelFavorite;
import com.putaotown.net.objects.ModelGood;
import com.putaotown.net.objects.ModelSubscriTown;
import com.putaotown.net.objects.ResponseSimple;

public interface BaseActivity
{
	/**点赞网络访问结束回调*/
	void onGood(ModelGood good);
	/**订阅情况网络返回*/
	void onSubscri(ModelSubscriTown subscri);
	/**收藏情况网络返回*/
	void onFavorite(ModelFavorite favorite);
	/**删除网络返回*/
	void onDelete(ResponseSimple res);
}