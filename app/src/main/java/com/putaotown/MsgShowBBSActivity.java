package com.putaotown;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.putaotown.community.ReplyTieActivity;
import com.putaotown.community.ThemeTieActivity;
import com.putaotown.localio.PushMessUtil;
import com.putaotown.net.objects.ModelPushSys;
import com.putaotown.net.objects.ModelPushTie;
import com.putaotown.tools.LogUtil;
import com.putaotown.tools.TimeShowUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MsgShowBBSActivity extends AppCompatActivity
{
	private TextView titletext;
	private SystemBarTintManager mTintManager;
	private List<ModelPushTie> tiemess;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_sysmsg);
  		this.setActionBar();
  		tiemess = PushMessUtil.getCommunity();
  		setSimpleListView();
  		
	}
	
	public void setSimpleListView() {
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		
		for (int i=0;i<tiemess.size();i++) {
			Map<String,Object> listitem = new HashMap<String,Object>();
			if (tiemess.get(i).getTietype() == 0)
				listitem.put("content", tiemess.get(i).getTieth_title());
			else
				listitem.put("content", tiemess.get(i).getTie().getContent());
			listitem.put("time", TimeShowUtil.showGoodTime(tiemess.get(i).getTime()));
			listItems.add(listitem);
		}
		SimpleAdapter simpleadapter = new SimpleAdapter(this
				,listItems
				,R.layout.listitem_msg_community
				,new String[]{"content","time"}
				,new int[]{R.id.listitem_msg_community_content,R.id.listitem_msg_community_time});
		
		ListView list = (ListView)findViewById(R.id.listview_sysmsg_xlistview);
		list.setAdapter(simpleadapter);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ModelPushTie tie = tiemess.get(position);
				if (tie.getTietype() ==0 ) {
					if (tie.getTie()!= null && tie.getAdminid() > 0 ) {
//						ThemeTieActivity.startAction(MsgShowBBSActivity.this, tie.getTieth(), tie.getAdminid());
						ReplyTieActivity.startAction(MsgShowBBSActivity.this, tie.getFloot(), tie.getTie(),true);
					}
				} else 
					if (tie.getTie() != null )
						ReplyTieActivity.startAction(MsgShowBBSActivity.this, tie.getFloot(), tie.getTie(),true);
			}
			
		});
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,MsgShowBBSActivity.class);
		context.startActivity(intent);
	}
	public void backEvent(View source){
		finish();
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