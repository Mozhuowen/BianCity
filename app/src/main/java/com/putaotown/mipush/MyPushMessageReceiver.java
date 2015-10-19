package com.putaotown.mipush;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.putaotown.MainActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.R;
import com.putaotown.localio.PushMessUtil;
import com.putaotown.net.objects.ModelPush;
import com.putaotown.net.objects.ModelPushComment;
import com.putaotown.net.objects.ModelPushGood;
import com.putaotown.net.objects.ModelPushSys;
import com.putaotown.net.objects.ModelPushTie;
import com.putaotown.tools.LogUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

/**
 * 1、PushMessageReceiver是个抽象类，该类继承了BroadcastReceiver。
 * 2、需要将自定义的DemoMessageReceiver注册在AndroidManifest.xml文件中 <receiver
 * android:exported="true"
 * android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"> <intent-filter>
 * <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" /> </intent-filter>
 * <intent-filter> <action android:name="com.xiaomi.mipush.ERROR" />
 * </intent-filter> </receiver>
 * 3、DemoMessageReceiver的onCommandResult方法用来接收客户端向服务器发送命令后的响应结果
 * 4、DemoMessageReceiver的onReceiveMessage方法用来接收服务器向客户端发送的消息
 * 5、onReceiveMessage和onCommandResult方法运行在非UI线程中
 * 
 * @author wangkuiwei
 */
public class MyPushMessageReceiver extends PushMessageReceiver {

    @Override
    public void onReceiveMessage(Context context, MiPushMessage message) {
        LogUtil.v(PutaoApplication.TAG,
                "onReceiveMessage is called. " + message.toString());
        //打印
        String log = "get message content: " + message.getContent();
        LogUtil.v("onReceiveMessage: ", log);
        //处理信息保存与传递
        Gson gson = new Gson();
        ModelPush pushmess = gson.fromJson(message.getContent(), ModelPush.class);
        if (pushmess.getType() < 2) {
	        ModelPushComment pushcomment = null;
	        pushcomment = (ModelPushComment)pushmess.getMess();
	        if (pushcomment != null) {
	        	LogUtil.v("Got Message parse to myObject: ", pushcomment.getContent());
	        	Message msg = Message.obtain();
	            msg.obj = pushcomment;
	            msg.arg1 = pushmess.getType();
	            PutaoApplication.getHandler().sendMessage(msg);
	        }
        } else if (pushmess.getType() == 2 ) {
        	ModelPushGood pushgood = (ModelPushGood) pushmess.getGoodmess();
        	if (pushgood != null) {
        		LogUtil.v("Got Message parse to ModelPushGood: ", pushgood.getUsername());
	        	Message msg = Message.obtain();
	            msg.obj = pushgood;
	            msg.arg1 = pushmess.getType();
	            PutaoApplication.getHandler().sendMessage(msg);
        	}
        } else if (pushmess.getType() == 3 ) {
        	ModelPushSys pushsys = (ModelPushSys) pushmess.getSysmess();
        	if (pushsys != null ) {
        		LogUtil.v("Got Message parse to ModelPushSys", pushsys.getContent());
        		Message msg = Message.obtain();
        		msg.obj = pushsys;
        		msg.arg1 = pushmess.getType();
        		PutaoApplication.getHandler().sendMessage(msg);
        	}
        } else if (pushmess.getType() ==4 ) {
        	ModelPushTie pushcommunity = (ModelPushTie) pushmess.getTiemess();
        	Message msg = Message.obtain();
    		msg.obj = pushcommunity;
    		msg.arg1 = pushmess.getType();
    		PutaoApplication.getHandler().sendMessage(msg);
        }
        
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        LogUtil.v(PutaoApplication.TAG,
                "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log = "";
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                log = context.getString(R.string.set_alias_success, cmdArg1);
            } else {
                log = context.getString(R.string.set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                log = context.getString(R.string.unset_alias_success, cmdArg1);
            } else {
                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                log = context.getString(R.string.subscribe_topic_success, cmdArg1);
            } else {
                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                log = context.getString(R.string.unsubscribe_topic_success, cmdArg1);
            } else {
                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                log = context.getString(R.string.set_accept_time_success, cmdArg1, cmdArg2);
                
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = (cmdArg1.equals(cmdArg2))?0:1;
                PutaoApplication.getHandler().sendMessage(msg);
            } else {
                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
        }

        LogUtil.v("get Push message: ", log);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    public static class PushMessHandler extends Handler {
        private Context context;
        public PushMessHandler(Context context) {
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
        	ModelPushComment messcom = null;
        	ModelPushGood messgood = null;
        	ModelPushSys messsys = null;
        	ModelPushTie messtie = null;
        	if (msg.arg1 < 2) {
        		messcom = (ModelPushComment) msg.obj; 
        		if (messcom != null)
        			this.postMess(msg.arg1, messcom);
        	} else if (msg.arg1 == 2) {
        		messgood = (ModelPushGood)msg.obj;
        		if (messgood != null)
        			this.postMess(msg.arg1, messgood);
        	} else if (msg.arg1 == 3) {
        		messsys = (ModelPushSys)msg.obj;
        		if (messsys != null)
        			this.postMess(msg.arg1,messsys);
        	} else if (msg.arg1 == 4) {
        		messtie = (ModelPushTie) msg.obj;
        		if (messtie != null )
        			this.postMess(msg.arg1, messtie);
        	}
        }
        /**消息保存与显示*/
        private void postMess(int type,Object messcom) {
        	PushMessUtil.receiveMess(type, messcom);
            postToMainActivity(type,messcom);
        }
        
        private void postToMainActivity(int type,Object object) {
        	MainActivity.postMess(type,object);
        }
    }
}
