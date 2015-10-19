package com.putaotown.community;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.putaotown.BaseActivity;
import com.putaotown.R;
import com.putaotown.community.adapter.TieThemeAdapter;
import com.putaotown.community.models.ModelTie;
import com.putaotown.community.models.ModelTieTheme;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.GoodUtil;
import com.putaotown.net.community.CommunityTieUtil;
import com.putaotown.net.community.DelTieUtil;
import com.putaotown.net.community.NewTieUtil;
import com.putaotown.net.community.ToTopUtil;
import com.putaotown.net.objects.ModelFavorite;
import com.putaotown.net.objects.ModelGood;
import com.putaotown.net.objects.ModelSubscriTown;
import com.putaotown.net.objects.ResponseSimple;
import com.putaotown.tools.LogUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

public class ThemeTieActivity extends SelectPicActivity implements IXListViewListener,OnTouchListener,BaseActivity
{
	private static ModelTieTheme MainTie;
	private static int adminId;
	private int louzhuId;	//楼主id
	private static boolean isTop = false;
	private XListView mXlist;
	private View editboxlayout;
	private View replyimage;
	private View onselectimg;
	private View settingicon;
	private View settingview;
	private ImageView goodview;
	private View redpos;
	private TextView totopview;
	private TextView deltie;
	private EditText replycontent;
	private View btnreply;
	private TieThemeAdapter xListAdapter;
	private SystemBarTintManager mTintManager;
	InputMethodManager imm ;	//全局输入法控制
	private Dialog dialog2;
	private Builder builder;
	
	private int currPosition;	//记录当前回复贴位置
	private MenuItem mMenuZD;	//置顶菜单项
	private MenuItem mMenuDelete; //删帖菜单项
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_theme);

		context = this;				
		initViews();
		super.setImageLayout();
		
		loadReplyTie();
		getGoods();
	}
	
	public void loadReplyTie() {
		CommunityTieUtil.get(this, MainTie.getTieid(),this.currPosition);
	}
	
	public void getGoods() {
		GoodUtil.getGoods(this, 4, MainTie.getTieid());
	}
	
	/**提交回复*/
	public void submitTie() {
		String content = this.replycontent.getText().toString();
		if (content != null && content.length() > 0) {
			LogUtil.v("ThemeTieActivity info: ", "submitTie content: "+content);
			super.submit();
			NewTieUtil req = new NewTieUtil(this,messhandler,MainTie.getCommunityid(),MainTie.getTieid(),content,imagenames);
			req.submit();
		} else {
			Toast.makeText(this, "请填写内容", Toast.LENGTH_SHORT).show();
		}
	}
	/**回复完成响应*/
	public void onFinishReply(boolean result) {
		super.finishSubmit();
		if (result) {
			toggleEditbox();
			Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();			
		} else 
			Toast.makeText(this, "回复失败", Toast.LENGTH_SHORT).show();		
		//加载更多
		this.loadReplyTie();
	}
	/**获取帖子响应*/
	public void onGetTies(List<ModelTie> ties) {
		this.currPosition += ties.size();
		this.onLoad();
		if (ties.size() < 15)
			this.mXlist.setFooterHint("暂无更多");
		this.xListAdapter.loadTies(ties);
	}
	/**置顶响应*/
	public void onToTop(boolean result) {
		if (result){
			if (!this.isTop) {
				isTop = true;
				Toast.makeText(this, "置顶成功", Toast.LENGTH_SHORT).show();	
			} else {
				isTop = false;
				Toast.makeText(this, "取消置顶成功", Toast.LENGTH_SHORT).show();	
			}
		}
	}
	/**删除帖子响应*/
	public void onDelTie(boolean result) {
		if (result) {
			setResult(RESULT_OK);
			Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
			this.finish();
		}
	}
	
	public void toggleEditbox() {
		if (this.editboxlayout.getVisibility() == View.VISIBLE) {
			this.editboxlayout.setVisibility(View.GONE);
			this.replycontent.setText("");
			super.finishUpload();
		}
	}
	
	public void initViews() {
		this.currPosition = 0;
		this.isTop = MainTie.getTop() == 1?true:false;
		this.mXlist = (XListView)findViewById(R.id.activity_community_theme_xlist);
		this.mXlist.setPullLoadEnable(true);
		this.mXlist.setPullRefreshEnable(true);
		this.mXlist.setXListViewListener(this);
		this.mXlist.setActivityTouchEvent(this);
		xListAdapter = new TieThemeAdapter(this,MainTie);
		this.mXlist.setAdapter(xListAdapter);
		xListAdapter.notifyDataSetChanged();
		
		this.replycontent = (EditText)findViewById(R.id.activity_community_theme_replycontent);
		this.btnreply = findViewById(R.id.activity_community_theme_btnreply);
		this.editboxlayout = findViewById(R.id.activity_community_theme_editboxlayout);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		onselectimg = findViewById(R.id.activity_community_theme_selectimg);
		//in the super class
		selectImageLayout = (LinearLayout)findViewById(R.id.activity_community_theme_images);
		hintview = findViewById(R.id.activity_community_them_redpos);;

		onselectimg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
				if (selectImageLayout.getVisibility() == View.GONE)
					selectImageLayout.setVisibility(View.VISIBLE);
				else
					selectImageLayout.setVisibility(View.GONE);
			}		
		});
		this.btnreply.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				submitTie();
			}			
		});
		//set actionbar
		this.setActionBar();
	}
	
	public static void startAction(Activity context,ModelTieTheme tie,int adminid) {
		MainTie = tie;
		adminId = adminid;
		isTop = MainTie.getTop() == 1?true:false;
//		context.startActivity(new Intent(context,ThemeTieActivity.class));
		context.startActivityForResult(new Intent(context,ThemeTieActivity.class), 200);
	}
	public void backEvent(View source){
		finish();
	}
	/**结束下拉上拉动画*/
	private void onLoad() {
		this.mXlist.stopRefresh();
		this.mXlist.stopLoadMore();
		String datestr = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		this.mXlist.setRefreshTime(datestr);
	}
	
	public void alerDeleteDialog() {
		builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);	
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
		View delete = view.findViewById(R.id.dialog_delete_delete);
		View cancel = view.findViewById(R.id.dialog_delete_cancel);
		delete.setOnClickListener(new OnDelTie(this,MainTie.getTieid()));
		cancel.setOnClickListener(new OnDelTie(this));
		
		builder.setView(view);
		dialog2 = builder.create();
		dialog2.setCanceledOnTouchOutside(true);
		dialog2.show();	
	}
	public void onCancleDel() {
		dialog2.dismiss();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		this.currPosition = 0;
		this.xListAdapter.readToUpdate();
		this.loadReplyTie();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		this.loadReplyTie();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
		this.editboxlayout.setVisibility(View.GONE);
//		this.settingview.setVisibility(View.GONE);
		return false;
	}

	@Override
	public void onGood(ModelGood good) {
		if(good.getDogood())
			this.goodview.setImageResource(R.drawable.iconfont_good_selected);
		else
			this.goodview.setImageResource(R.drawable.iconfont_good_normal);
	}

	@Override
	public void onSubscri(ModelSubscriTown subscri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFavorite(ModelFavorite favorite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDelete(ResponseSimple res) {
	}
	
	public class OnDelTie implements OnClickListener
	{
		private ThemeTieActivity context;
		private int tieid = 0;
		public OnDelTie(ThemeTieActivity context){
			this.context = context;
		}
		public OnDelTie(ThemeTieActivity context,int tiethid) 
		{
			this.context = context;
			this.tieid = tiethid;
		}
		@Override
		public void onClick(View v) {
			if (tieid==0){
				context.onCancleDel();
			} else {
				DelTieUtil.submit(context, tieid);
			}
			
		}
		
	}
	
	/**设置actionbar*/
	public void setActionBar() {
		this.setOverflowShowingAlways();
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(true);
		View v = this.getLayoutInflater().inflate(R.layout.actionbar_bbs_theme, null);
		ActionBar.LayoutParams param = new ActionBar.LayoutParams(Gravity.RIGHT);
		actionbar.setCustomView(v, param);
		//set actionbar event
		this.goodview = (ImageView) v.findViewById(R.id.actionbar_good);
		this.goodview.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				GoodUtil.doGood(ThemeTieActivity.this, 4, MainTie.getTieid(), 0);
			}
		});
		View commentv = v.findViewById(R.id.actionbar_talk);
		commentv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (editboxlayout.getVisibility() == View.VISIBLE)
					editboxlayout.setVisibility(View.GONE);
				else
					editboxlayout.setVisibility(View.VISIBLE);
			}
			
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (adminId == UserPreUtil.getUserid())
			getMenuInflater().inflate(R.menu.mythemetie, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	    	finish();
	        return true; 
	    case R.id.actionbar_delete:
	    	alerDeleteDialog();
	    	return true;
	    case R.id.actionbar_zhiding:
	    	ToTopUtil.submit(ThemeTieActivity.this, MainTie.getTieid());
	    	return true;
	        default:
	        	return true;
	    }  
	}
	@Override  
    public boolean onPrepareOptionsMenu(Menu menu) { 
		LogUtil.v("ThemeTieActivity info:", "menu size: "+menu.size());
		if (menu.findItem(R.id.actionbar_zhiding) != null ) {
			if (this.mMenuZD == null ) {
				this.mMenuZD = menu.findItem(R.id.actionbar_zhiding);
				this.mMenuDelete = menu.findItem(R.id.actionbar_delete);
			}
			if (isTop) {
				this.mMenuZD.setTitle("取消置顶");
			}else {
				this.mMenuZD.setTitle("置顶");
			}
		}
        return super.onPrepareOptionsMenu(menu);  
    } 

	/*设置隐藏菜单按钮总是显示*/
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}