package com.putaotown.community;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.putaotown.OpenImageBrower;
import com.putaotown.R;
import com.putaotown.community.models.ModelTie;
import com.putaotown.community.models.ModelTieReply;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.GoodUtil;
import com.putaotown.net.community.BianImageLoader;
import com.putaotown.net.community.CommunityTieReplyUtil;
import com.putaotown.net.community.NewTieReplyUtil;
import com.putaotown.net.community.ToTopUtil;
import com.putaotown.tools.LogUtil;
import com.putaotown.tools.OpenUserSpan;
import com.putaotown.tools.TimeShowUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class ReplyTieActivity extends AppCompatActivity implements OnClickListener
{
	private static int flootid;
	private static ModelTie thistie;
	private TextView titleText;
	private ImageView usercover;
	private TextView username;
	private TextView flootview;
	private TextView timeview;
	private View btnreply;
	private TextView content;
	private LinearLayout imageslayout;
	private LinearLayout replylayout;
	private static View editlayout;
	private static EditText replycontent;
	private View btnsendreply;
	private static int bereplyid = 0;
	private SystemBarTintManager mTintManager;
	InputMethodManager imm ;	//全局输入法控制
	private static boolean receive_flag = false;	//false不从服务器获取
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_community_replytie);
					
		initViews();
		
	}
	
	public void initViews() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
//		this.titleText = (TextView) findViewById(R.id.activity_community_replytie_titletext);
//		if (flootid >0)
//			this.titleText.setText(getString(R.string.bbs_lou,flootid+""));
//		else 
//			this.titleText.setVisibility(View.GONE);
		this.usercover = (ImageView)findViewById(R.id.activity_tietheme_tie_usercover);
		this.username = (TextView)findViewById(R.id.activity_tietheme_tie_username);
		this.flootview = (TextView)findViewById(R.id.activity_tietheme_tie_lou);
		this.timeview = (TextView)findViewById(R.id.activity_tietheme_tie_time);
		this.content = (TextView)findViewById(R.id.activity_tietheme_tie_content);
		this.imageslayout = (LinearLayout)findViewById(R.id.activity_tietheme_tie_imagelayout);
		this.replylayout = (LinearLayout)findViewById(R.id.activity_tietheme_tie_replys);
		this.replycontent = (EditText)findViewById(R.id.activity_community_replytie_replycontent);
		this.btnsendreply = findViewById(R.id.activity_community_replytie_btnreply);
//		this.btnreply = findViewById(R.id.activity_replytie_k);
		this.editlayout = findViewById(R.id.activity_community_replytie_editboxlayout);
		//set data
		this.username.setText(thistie.getUsername());
		if (flootid > 0)
			this.flootview.setText(getString(R.string.bbs_lou,flootid+""));
		else
			this.flootview.setVisibility(View.GONE);
		this.timeview.setText(TimeShowUtil.showGoodTime(thistie.getTime()));
		this.content.setText(thistie.getContent());
		
		this.loadImage(usercover, thistie.getUsercover(), true);
		this.loadImages(imageslayout, thistie.getImagenames());
		
		this.btnsendreply.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String content = replycontent.getText().toString();
				if (content!=null && content.length() > 0) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
					bereplyid = 0;
					replycontent.setText("");
					editlayout.setVisibility(View.GONE);					
					NewTieReplyUtil.submit(ReplyTieActivity.this, thistie.getTieid(), 0, content);
				} else {
					Toast.makeText(ReplyTieActivity.this, "请填写内容", Toast.LENGTH_SHORT).show();
				}
			}			
		});
		this.setActionBar();
		//set replys
		if (receive_flag)
			this.loadReplysFromServ(thistie);
		else
			this.setReplys(replylayout, thistie.getReplys());
	}
	
	public void onReply(boolean stat,ModelTieReply tiereply) {
		if (stat) {
			this.addOneReply(replylayout, tiereply);			
		} else
			Toast.makeText(this, "回复失败", Toast.LENGTH_SHORT).show();
	}
	
	public static void startAction(Context context,int floot,ModelTie tie) {
		flootid = floot;
		thistie = tie;
		receive_flag = false;
		context.startActivity(new Intent(context,ReplyTieActivity.class));
	}
	public static void startAction(Context context,int floor,ModelTie tie,boolean receive_f) {
		flootid = floor;
		thistie = tie;
		receive_flag = receive_f;
		context.startActivity(new Intent(context,ReplyTieActivity.class));
	}
	public void backEvent(View source){
		finish();
	}
	private void loadImage(ImageView imageview,String uri,boolean isusercover) {
		if (isusercover)
			BianImageLoader.getInstance().loadImage(imageview, uri,250);
		else
			BianImageLoader.getInstance().loadImage(imageview, uri,850);
	}
	private void loadImages(LinearLayout imagelayout,List<String> imagenames) {
		if (imagenames.size()>0) {
			imagelayout.setVisibility(View.VISIBLE);
			for (int i=0;i<imagenames.size();i++) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 20, 0, 0);
				ImageView view = new ImageView(this);
				view.setLayoutParams(lp);
				view.setScaleType(ImageView.ScaleType.CENTER_CROP);
				view.setOnClickListener(new OpenImageBrower(this,i,imagenames));
				imagelayout.addView(view);
				this.loadImage(view, imagenames.get(i),false);
			}
		}
	}
	
	private void setReplys(LinearLayout layout,List<ModelTieReply> replys) {
		for(int i=0;i<replys.size();i++) {
			TextView spanview = new TextView(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 21, 0, 0);
			spanview.setLayoutParams(lp);
			spanview.setTextSize(11);
			spanview.setTextColor(this.getResources().getColor(R.color.grey_light));
			SpannableString spStr = new SpannableString(replys.get(i).getUsername());
			OpenUserSpan span = new OpenUserSpan(this,replys.get(i).getUserid(),replys.get(i).getUsername(),replys.get(i).getUsercover());
			spStr.setSpan(span, 0, spStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spanview.append(spStr);
			spanview.append(": "+replys.get(i).getContent()+"  "+TimeShowUtil.showGoodTime(replys.get(i).getTime()));
			spanview.setMovementMethod(LinkMovementMethod.getInstance());
			spanview.setTag(replys.get(i));
			spanview.setOnClickListener(new OnSimpleReply(spanview));
			layout.addView(spanview);
		}
	}
	private void loadReplysFromServ(ModelTie tie) {
		CommunityTieReplyUtil.getReplys(this, tie.getTieid());
	}
	/**从服务器接收回复贴*/
	public void onReceiveReplys(List<ModelTieReply> replys) {
		this.setReplys(this.replylayout, replys);
	}
	
	private void addOneReply(LinearLayout layout,ModelTieReply reply) {
		TextView spanview = new TextView(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 21, 0, 0);
		spanview.setLayoutParams(lp);
		spanview.setTextSize(11);
		spanview.setTextColor(this.getResources().getColor(R.color.grey_light));
		SpannableString spStr = new SpannableString(reply.getUsername());
		OpenUserSpan span = new OpenUserSpan(this,reply.getUserid(),reply.getUsername(),reply.getUsercover());
		spStr.setSpan(span, 0, spStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spanview.append(spStr);
		spanview.append(": "+reply.getContent()+"  "+TimeShowUtil.showGoodTime(reply.getTime()));
		spanview.setMovementMethod(LinkMovementMethod.getInstance());
		spanview.setTag(reply);
		spanview.setOnClickListener(new OnSimpleReply(spanview));
		layout.addView(spanview);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public static class OnSimpleReply implements OnClickListener
	{
		ModelTieReply tie;
		public OnSimpleReply(View v) {
			this.tie = (ModelTieReply)v.getTag();
		}
		@Override
		public void onClick(View v) {
			editlayout.setVisibility(View.VISIBLE);
			bereplyid = tie.getTieid();
			String str = "回复"+tie.getUsername()+": ";
			replycontent.setText(str);
			replycontent.setSelection(str.length());
		}
		
	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		if (flootid >0) {
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setTitle(getString(R.string.bbs_lou,flootid+""));
		}else 
			actionbar.setDisplayShowTitleEnabled(false);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_bbs_replytie, null);
		ActionBar.LayoutParams param = new ActionBar.LayoutParams(Gravity.RIGHT);
		actionbar.setCustomView(v, param);
		//set actionbar event
		this.btnreply = v.findViewById(R.id.actionbar_talk);
		this.btnreply.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (editlayout.getVisibility() == View.GONE)
					editlayout.setVisibility(View.VISIBLE);
				else {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
					replycontent.setText("");
					editlayout.setVisibility(View.GONE);
				}
			}
			
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	    	finish();
	        return true;
	        default:
	        	return true;
	    }  
	}
}