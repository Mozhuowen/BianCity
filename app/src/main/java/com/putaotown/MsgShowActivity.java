package com.putaotown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.putaotown.localio.PushMessUtil;
import com.putaotown.net.CommentRequest;
import com.putaotown.net.objects.ModelPushComment;
import com.putaotown.net.objects.ModelUser;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;
/***
 * 显示评论消息
 * @author awen
 *
 */
public class MsgShowActivity extends AppCompatActivity implements OnClickListener
{
	private static View mReplyView;
	private static EditText mEditView;
	private static View mButtonReply;
	private static int mReplyStoryId;
	private static int mReplyTownId;
	private static int mReplyCommentId;
	private ListView mListview;
	private TextView hintview;
	private List<ModelPushComment> mCommentList;
	
	InputMethodManager imm ;	//全局输入法控制
	private SystemBarTintManager mTintManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msgshow);
		
  		mCommentList = PushMessUtil.getComments();
  		LogUtil.v("MsgShowAcitivty info: ", "comment list size: "+ mCommentList.size());
		initView();		
	}
	
	private void initView(){
		this.setActionBar();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		mReplyView = findViewById(R.id.activity_msgshow_commentedit);
		this.mButtonReply = findViewById(R.id.activity_msgshow_submitcomment);
		this.mEditView = (EditText)findViewById(R.id.activity_msgshow_commentcontent);
		mListview = (ListView) findViewById(R.id.activity_msgshow_xlistview);
		this.hintview = (TextView)findViewById(R.id.activity_msgshow_hint);
		
		this.mButtonReply.setOnClickListener(this);
		
		if (mCommentList.size() > 0)
			this.hintview.setVisibility(View.GONE);
		MsgAdapter msgadapter = new MsgAdapter(this,mCommentList);
		mListview.setAdapter(msgadapter);
//		mListview.setOnScrollListener(msgadapter);
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,MsgShowActivity.class);
		context.startActivity(intent);
	}
	public void backEvent(View source){
		finish();
	}
	/**提交回复*/
	public void commitReply() {
		String content = mEditView.getText().toString();
		if (content != null && content.length() > 0) {
			CommentRequest postrequest = new CommentRequest(MsgShowActivity.this,null,mReplyStoryId,mReplyTownId,content,mReplyCommentId);
			postrequest.submitComment();
			//隐藏输入法
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
			//清空内容
			mEditView.setText("");
		}
	}
	/**列表item点击回复事件绑定类*/
	public static class MsgShowListener implements OnClickListener
	{
		private int beReplyId;
		private String bereplyname;
		private int restoryid;
		private int retownid;
		
		public MsgShowListener(int bereplyid,String bereplyname,int replystoryid,int replytownid) {
			this.beReplyId = bereplyid;
			this.bereplyname = bereplyname;
			this.restoryid = replystoryid;
			this.retownid = replytownid;
		}
		
		@Override
		public void onClick(View v) {
			LogUtil.v("MsgShowActivity info: ", "MsgShowListener onclick! view id: "+v.getId());
			if (v.getId() == R.id.listitem_msg_comment_content) {
				if (mReplyView.getVisibility() == View.GONE){
					mReplyView.setVisibility(View.VISIBLE);
					mEditView.setHint("回复 "+bereplyname);
					//更新回复相关参数
					mReplyCommentId = beReplyId;
					mReplyTownId = retownid;
					mReplyStoryId = restoryid;
				}
				else
					mReplyView.setVisibility(View.GONE);
			}
		}
		
	}
	/**列表点击打开故事事件绑定*/
	public static class MsgOpenStoryListener implements OnClickListener
	{
		private Activity context;
		private PackagePutao story;
		public MsgOpenStoryListener(Activity context, PackagePutao story) {
			this.context = context;
			this.story = story;
		}

		@Override
		public void onClick(View v) {
			PutaoxActivity.startAction(context, story, false);
		}
		
	}
	/**列表点击头像打开用户信息*/
	public static class MsgOpenUserListener implements OnClickListener
	{
		ModelUser targetuser;
		Activity context;
		public MsgOpenUserListener(Activity context,ModelUser user) {
			this.targetuser = user;
			this.context = context;
		}
		@Override
		public void onClick(View v) {
			if (targetuser != null)
				UserActivity.startAction(context, targetuser);
		}
		
	}
	/**响应提交回复评论点击*/
	@Override
	public void onClick(View v) {		
		switch(v.getId()) {
		case R.id.activity_msgshow_submitcomment:
			commitReply();
			mReplyView.setVisibility(View.GONE);
			break;
		}
	}
	
	public static void onScroll() {
		mReplyView.setVisibility(View.GONE);
	}
	/**设置actionbar*/
	public void setActionBar() {
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
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