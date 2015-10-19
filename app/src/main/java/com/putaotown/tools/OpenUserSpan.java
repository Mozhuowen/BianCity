package com.putaotown.tools;

import com.putaotown.PutaoApplication;
import com.putaotown.R;
import com.putaotown.UserActivity;
import com.putaotown.net.objects.ModelUser;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class OpenUserSpan extends ClickableSpan
{
	private Context context;
	private int userid;
	private String username;
	private String usercover;
	public OpenUserSpan(Context context,int userid,String username,String usercover) {
		this.context = context;
		this.userid = userid;
		this.username = username;
		this.usercover = usercover;
	}
	
	@Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(context.getResources().getColor(R.color.ThinkBlue));
        ds.setUnderlineText(false); //去掉下划线
    }

	@Override
	public void onClick(View widget) {
		ModelUser user = new ModelUser();
		user.setUserid(userid);
		user.setName(username);
		user.setCover(usercover);
		UserActivity.startAction(context, user);
	}
	
}