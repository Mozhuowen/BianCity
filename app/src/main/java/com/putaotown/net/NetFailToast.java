package com.putaotown.net;

import com.putaotown.PutaoApplication;

import android.content.Context;
import android.widget.Toast;

public class NetFailToast
{
	private static Context context;
	
	public static final int LOGIN_ANOTHERPLACE = 100;
	public static final int NAME_EXIST = 101;
	public static final int EMAIL_EXIST = 102;
	public static final int NAME_NOTEXIST = 103;
	public static final int PASSWORD_ERROR = 104;
	public static final int USER_NOTEXIST = 105;
	public static final int TOWNNAME_EXIST = 106;
	public static final int TOKEN_ERROR = 107;
	public static final int SIGNATURE_ERROR = 108;
	public static final int NOTJOIN_COMMUNITY = 109;
	public static final int NO_PERMISSION = 110;
	
	public static final int SERVER_ERROR = 201;
	
	public static void show(int code) {
		context = PutaoApplication.getContext();
		Toast toast = null;
		switch(code) {
		case LOGIN_ANOTHERPLACE:
			Toast.makeText(context, "您的帐号在其他地方登录，请退出后重新登录", Toast.LENGTH_SHORT).show();
			break;
		case NAME_EXIST:
			Toast.makeText(context, "用户名已被注册，请换一个用户名", Toast.LENGTH_SHORT).show();
			break;
		case EMAIL_EXIST:
			Toast.makeText(context, "电子邮箱已被注册，请换一个电子邮箱", Toast.LENGTH_SHORT).show();
			break;
		case NAME_NOTEXIST:
			Toast.makeText(context, "用户名不存在", Toast.LENGTH_SHORT).show();
			break;
		case PASSWORD_ERROR:
			Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
			break;
		case SERVER_ERROR:
			Toast.makeText(context, "服务器错误", Toast.LENGTH_SHORT).show();
			break;
		case USER_NOTEXIST:
			Toast.makeText(context, "用户不存在", Toast.LENGTH_SHORT).show();
			break;
		case TOWNNAME_EXIST:
			Toast.makeText(context, "边城名已经存在，请换一个新的吧", Toast.LENGTH_SHORT).show();
			break;
		case TOKEN_ERROR:
			Toast.makeText(context, "token错误", Toast.LENGTH_SHORT).show();
			break;
		case SIGNATURE_ERROR:
			Toast.makeText(context, "签名错误", Toast.LENGTH_SHORT).show();
			break;
		case NOTJOIN_COMMUNITY:
			Toast.makeText(context, "你还没有加入社区", Toast.LENGTH_SHORT).show();
			break;
		case NO_PERMISSION:
			Toast.makeText(context, "没有权限", Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(context, "网络不给力", Toast.LENGTH_SHORT).show();
		}
	}
	
}