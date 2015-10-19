package com.putaotown;

import java.util.List;

import com.putaotown.mipush.MyPushMessageReceiver.PushMessHandler;
import com.putaotown.tools.LogUtil;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 做一些初始化工作，包括小米推送的客户端注册
 * @author awen
 *
 */
public class PutaoApplication extends Application
{
	//xiaomi message user your appid the key.
    public static final String APP_ID = "2882303761517336169";
    //xiaomi message user your appid the key.
    public static final String APP_KEY = "5191733681169";
    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    public static final String TAG = "com.xiaomi.mipushdemo";
    private static PushMessHandler handler = null;
    
	private static Context context;
	private static String username;
	private static String token;
	private static int screenWidth;
	public static boolean hasTowns = false;
	
	@Override
	public void onCreate() {
		context = getApplicationContext();
		SharedPreferences readpre = context.getSharedPreferences("user", context.MODE_PRIVATE);
		username = readpre.getString("name", "");
		token = readpre.getString("cover", "");
		DisplayMetrics dm = new DisplayMetrics();
		
		screenWidth = new DisplayMetrics().widthPixels;
		
		// 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                LogUtil.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
        if (handler == null)
            handler = new PushMessHandler(getApplicationContext());
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static String getUsername() {
		return username;
	}
	
	public static String getToken() {
		return token;
	}
	
	public static int getScreenWidth() {
		return screenWidth;
	}
	
	public static boolean getHasTowns() {
		return hasTowns;
	}
	
	private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static PushMessHandler getHandler() {
        return handler;
    }
}