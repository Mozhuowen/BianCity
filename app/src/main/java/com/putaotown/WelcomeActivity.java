package com.putaotown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.UserRequest;
import com.putaotown.net.objects.ModelRegisteQQ;
import com.putaotown.net.objects.ResponseLogin;
import com.putaotown.net.objects.ResponseRegiste;
import com.putaotown.tools.LogUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class WelcomeActivity extends Activity
{
	private boolean mCurrLoginWb = true;
	private ImageView mBackgroud;	
	/**微博-控制是否需要重新登录*/
	private boolean needLogin = false;
	private Handler messhandler;
	private final String APP_KEY = "2562644072";
	private final String REDIRECT_URL = "http://api.weibo.com/oauth2/default.html";
	private final String SCOPE = "user";
	private final int CNAME = 200;
	private ImageView mLogin_sina;
	private ImageView mLogin_qq;	
	/**微博- 显示认证后的信息，如 AccessToken */
    private TextView mTokenText;   
    private AuthInfo mAuthInfo;  
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    /** 用户信息接口 */
    private UsersAPI mUsersAPI;    
    private UserRequest mRequest;
    
    /**qq相关成员变量*/
    private String QQAPP_ID = "1104330483";
    private Tencent mTencent;
    private UserInfo mQQInfo;
    private String mQQOpenid;
    private String mQQAccessToken;
    	
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		this.setContentView(R.layout.activity_welcomex);	
		
		// 创建微博实例
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
        
        
        //创建QQ实例
	     // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
	     // 其中APP_ID是分配给第三方应用的appid，类型为String。
//	     mTencent = Tencent.createInstance(QQAPP_ID, this.getApplicationContext());
	     // 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取
        
        initView();
	}
	
	private void initView() {
		this.mBackgroud = (ImageView)findViewById(R.id.activity_welcome_background);
		this.mLogin_sina = (ImageView)findViewById(R.id.activity_welcome_login_sina);
		this.mLogin_qq = (ImageView)findViewById(R.id.activity_welcome_login_qq);
		
		//判断是否需要登录
		String username = UserPreUtil.getUsername();
		if (username != null && username.length()>0) {
			this.mLogin_qq.setVisibility(View.GONE);
			this.mLogin_sina.setVisibility(View.GONE);
			
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
					WelcomeActivity.this.startActivity(intent);
					WelcomeActivity.this.finish();
				}
				
			}, 2000);
			return;
		}
		
		/**
		 * 微博登录监听
		 */
		this.mLogin_sina.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mCurrLoginWb = true;
				mSsoHandler = new SsoHandler(WelcomeActivity.this, mAuthInfo);
				mSsoHandler.authorize(new AuthListener());
			}
			
		});
		this.mLogin_qq.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mCurrLoginWb = false;
				mTencent = Tencent.createInstance(QQAPP_ID, WelcomeActivity.this.getApplicationContext());
				mTencent.login(WelcomeActivity.this, "all", new QQUiListener(true));
			}
			
		});
		
		//handler吐司处理
		messhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 200) {	//网络活动失败
//					onNetworkFail();
					Toast.makeText(WelcomeActivity.this, "网络错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 300){
//					onNetworkFail();
				} else {
					String mess = (String)msg.obj;
					Toast.makeText(WelcomeActivity.this, mess, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	/**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**修改用户名返回*/
        if (requestCode == CNAME && resultCode == CNAME) {
        	Intent intent = new Intent(this,MainActivity.class);
    		this.startActivity(intent);
    		this.finish();
        }       
        
        //隐藏登录VIEW
        this.mLogin_qq.setVisibility(View.GONE);
		this.mLogin_sina.setVisibility(View.GONE);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }       
        if( mTencent != null)
        	mTencent.onActivityResult(requestCode, resultCode, data);
        
    }
	
	class AuthListener implements WeiboAuthListener {
		 
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
            	//提交PtServer验证
            	ptserverCheck(true);
                // 显示 Token
                updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
                Toast.makeText(WelcomeActivity.this, 
                        "登录成功", Toast.LENGTH_SHORT).show();

            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "获取Token失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(WelcomeActivity.this, 
                    "取消登录", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WelcomeActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 显示当前 Token 信息。
     * 
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format);
        LogUtil.v("Login Token info: ", String.format(format, mAccessToken.getToken(),mAccessToken.getRefreshToken(), date,mAccessToken.getUid()));
//        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));
        
        String message = String.format(format, mAccessToken.getToken(),mAccessToken.getRefreshToken(), date,mAccessToken.getUid());
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
//        mTokenText.setText(message);
        LogUtil.v("Login Token info: ", message);
    }
    /**
     * 检验token,要区分是微博的还是QQ的，检查是否需要注册
     */
    private void ptserverCheck(boolean iswb) {
    	if (iswb) {
	    	long expirestime = mAccessToken.getExpiresTime();
	    	String wbtoken = mAccessToken.getToken();
	    	String uid = mAccessToken.getUid();
	    	
	    	mRequest = new UserRequest(this,messhandler,0,uid,wbtoken,expirestime);
	    	mRequest.login();
    	} else {	//提交QQ信息(改为在监听类中调用)
    		
    	}
    }
    /**
     * 完成ptserverCheck登录后调用
     * @param reslogin
     */
    public void onFinishLogin(ResponseLogin reslogin) {
    	if (reslogin.isNeedregiste()) {
    		LogUtil.v("WelcomActivity info: ", "is need registe!");
    		UserPreUtil.updateUser(reslogin.getPtuserid(), reslogin.getPtoken(),reslogin.getLogintype());
    		if (mCurrLoginWb) {	    		
	    		//获取用户微博信息
	    		mUsersAPI = new UsersAPI(WelcomeActivity.this, APP_KEY, mAccessToken);
	            long uid = Long.parseLong(mAccessToken.getUid());
	            mUsersAPI.show(uid, new wbUserListener(reslogin.getPtuserid(),reslogin.getPtoken(),reslogin.getLogintype()));
    		} else {
    			//获取用户qq信息并注册到ptserver
    			LogUtil.v("WelcomActivity info: ", "is need qqinfo!");
//    			this.mTencent.getUserInfo(new BaseUIListener(this,"get_user_info"));
    			this.mQQInfo = new UserInfo(this,this.mTencent.getQQToken());
    			this.mQQInfo.getUserInfo(new QQUiListener(false,reslogin.getPtuserid(),reslogin.getPtoken()));
    		}
    	} else if (reslogin.getNeedcname()) {
    		if (mCurrLoginWb) {
	    		UserPreUtil.updateUser(reslogin.getPtuserid(), reslogin.getPtoken(),reslogin.getLogintype());
	    		UserPreUtil.updateUserInfo(reslogin.getUid(), "", reslogin.getCover(), reslogin.getSex(), reslogin.getLocation());
	    		
	    		this.startActivityForResult(new Intent(this,CNameActivity.class), CNAME);
    		} else {
    			UserPreUtil.updateUser(reslogin.getPtuserid(), reslogin.getPtoken(),reslogin.getLogintype());
	    		UserPreUtil.updateUserInfo(reslogin.getUid(), "", reslogin.getCover(), reslogin.getSex(), reslogin.getLocation());
	    		
	    		this.startActivityForResult(new Intent(this,CNameActivity.class), CNAME);
    		}
    	} else {
    		UserPreUtil.updateUser(reslogin.getPtuserid(), reslogin.getPtoken(),reslogin.getLogintype());
    		UserPreUtil.updateUserInfo(reslogin.getUid(), reslogin.getName(), reslogin.getCover(), reslogin.getSex(), reslogin.getLocation());
    		UserPreUtil.updateAllMyTowns(reslogin.getMytowns());
    		
    		Intent intent = new Intent(this,MainActivity.class);
    		this.startActivity(intent);
    		this.finish();
    	}
    }
    public void onFinishRegiste(ResponseRegiste resregiste) {
    	UserPreUtil.updateUserInfo(resregiste.getUid(), resregiste.getName(), resregiste.getCover(), resregiste.getSex(), resregiste.getLocation());
		Toast.makeText(WelcomeActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
		//是否需要修改用户名
    	if (resregiste.isNeedchangename()) {
    		this.startActivityForResult(new Intent(this,CNameActivity.class), CNAME);
    	} else {  		
    		Intent intent = new Intent(this,MainActivity.class);
    		this.startActivity(intent);
    		this.finish();
    	}
    	   	
    }
    
    /**
     * 微博 OpenAPI 回调接口,获取用户信息，并上传putao server
     */
    private class wbUserListener implements RequestListener
    {
    	int ptuserid;
    	String ptoken;
    	int logintype;
    	
    	public wbUserListener(int ptuserid,String ptoken,int logintype) {
    		this.ptuserid = ptuserid;
    		this.ptoken = ptoken;
    		this.logintype = logintype;
    	}
		@Override
		public void onComplete(String response) {
			if (!TextUtils.isEmpty(response)) {
                LogUtil.i("Sina Auth User info: ", response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                	LogUtil.v("WelcomeActivity info: ", "获取User信息成功，用户昵称：" + user.screen_name);
                    mRequest.registe(user,null, ptuserid, ptoken,logintype);
                    
                } else {
//                    Toast.makeText(WelcomeActivity.this, response, Toast.LENGTH_LONG).show();
                	LogUtil.v("WelcomeActivity info: ", "wbUserListener onComplete user is null ! " + response);
                }
            }
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LogUtil.e("Sina Auth User info: ", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(WelcomeActivity.this, info.toString(), Toast.LENGTH_LONG).show();
		}
    }
    
    public static void startAction(Context context) {
    	Intent intent = new Intent(context,WelcomeActivity.class);
    	context.startActivity(intent);
    }
    
    /**QQ监听函数*/
    private class QQUiListener implements IUiListener {
    	private boolean islogin = false;
    	private int ptuserid;
    	private String ptoken;
    	
    	/**QQ登录和获取用户信息均用这个监听，所以要区分*/
    	public QQUiListener(boolean islogin) {
    		this.islogin = islogin;
    	}
    	public QQUiListener(boolean islogin,int ptuserid,String ptoken) {
    		this.ptuserid = ptuserid;
    		this.ptoken = ptoken;
    	}
    	
    	/**处理返回信息
    	 * @throws JSONException */
    	protected void doComplete(JSONObject values) throws JSONException {
    		LogUtil.v("WelcomexActivity QQ Login info: ", values.toString());
    		String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                    new java.util.Date(values.getLong("expires_in")));
    		LogUtil.v("QQ Login expire time: ", date);
    		mQQAccessToken = values.getString("access_token");
    		mQQOpenid = values.getString("openid");
    		//post to server
    		mRequest = new UserRequest(WelcomeActivity.this,messhandler,1,values.getString("openid"),values.getString("access_token"),Long.valueOf(values.getString("expires_in")));
	    	mRequest.login();
    	}
    	@Override
    	public void onError(UiError e) {
    		LogUtil.v("onError:", "code:" + e.errorCode + ", msg:"
    				+ e.errorMessage + ", detail:" + e.errorDetail);
    	}
    	@Override
    	public void onCancel() {
    		LogUtil.v("WelcomexActivity Qq Login info: ", "onCancel");
    	}
    	/**完成时回调*/
		@Override
		public void onComplete(Object arg0) {
			try {
				//登录
				if (this.islogin)
					doComplete((JSONObject)arg0);
				else 	//获取用户信息
					getUserinfo((JSONObject)arg0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		/**获取qq用户信息并上传注册*/
		protected void getUserinfo(JSONObject values) {
			LogUtil.v("Got QQ userinfo: ", values.toString());
			ModelRegisteQQ qq = new Gson().fromJson(values.toString(), ModelRegisteQQ.class);
			qq.setOpenid(mTencent.getOpenId());
			mRequest.registe(null,qq, ptuserid, ptoken,1);
		}
    	}
}