package com.putaotown.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import com.putaotown.AttentionActivity;
import com.putaotown.LoginActivity;
import com.putaotown.MainActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.R;
import com.putaotown.RegisteActivity;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.SLoadImage;
import com.putaotown.tools.BitmapUtil;
import com.putaotown.tools.DensityUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MineFragment extends Fragment
{
	private ViewGroup loginlayout;
	private Context context = PutaoApplication.getContext();
	private Context activity;
	
	LayoutInflater layoutInflater;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		View layout = inflater.inflate(R.layout.fragment_minex, container,false);
//		loginlayout = (ViewGroup)layout.findViewById(R.id.fragment_mine_loginlayout);
		this.activity = this.getActivity();
		layoutInflater = LayoutInflater.from(context);
		
//		updateLogin();
		
//		return layout;
		return null;
	}
	
	public void updateLogin() {
		Map map = UserPreUtil.getUserInfo();
		if (!map.get("name").equals("") && map.get("name") != null) {
			//longinlayout重新设置layoutparam
			loginlayout.removeAllViews();
			loginlayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			loginlayout.setOnClickListener((MainActivity)this.getActivity());
			//获取登录信息
			String name =(String) map.get("name");
			String cover = (String)map.get("cover");
			CircleImageView imageview = (CircleImageView)layoutInflater.inflate(R.layout.view_circleimage, null);
			imageview.setLayoutParams(new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 32),DensityUtil.dip2px(context, 32)));
			TextView textview = new TextView(context);
			//获取头像
			/*String imagepath = context.getFilesDir() + "/image/" + cover;
			File file = new File(imagepath);
			Uri imageuri = Uri.fromFile(file);
			
			Bitmap bitmap = decodeUriAsBitmap(imageuri);
			imageview.setImageBitmap(bitmap);*/
			//load image
//			SLoadImage sloadimage = SLoadImage.getInstance();
//			sloadimage.loadImage(imageview, cover, 90, 90);
			
			textview.setText(name);
			textview.setTextSize(DensityUtil.dip2px(context, 10));
			
			loginlayout.addView(imageview);
			loginlayout.addView(textview);
		} else {	//设置状态为未登录
			loginlayout.removeAllViews();
			loginlayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));	
			ViewGroup loginview = (ViewGroup)layoutInflater.inflate(R.layout.view_login, null);
			ViewGroup registeiview = (ViewGroup)layoutInflater.inflate(R.layout.view_registe, null);
			View dividerview = layoutInflater.inflate(R.layout.view_divider, null);
			
			//设置事件监听
			loginview.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					LoginActivity.startAction(activity,Activity.RESULT_OK);
				}
				
			});
			registeiview.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					RegisteActivity.startAction(activity);
				}
				
			});
			
			loginlayout.addView(loginview);
			loginlayout.addView(dividerview);
			loginlayout.addView(registeiview);
		}
	}
	
	public void onClick() {
		Log.d("MineFragment", "enter onClick!");
		Intent intent = new Intent(this.getActivity(),AttentionActivity.class);
		startActivity(intent);
	}
	
	/**
	 *  将URI地址转换为bitmap图片
	 * @param uri
	 * @return
	 */
	private Bitmap decodeUriAsBitmap(Uri uri){
	    Bitmap bitmap = null;
	    try {
	    bitmap = BitmapFactory.decodeStream(this.getActivity().getContentResolver().openInputStream(uri));
	        } catch (FileNotFoundException e) {e.printStackTrace();
	        return null;
	        }
	    if (bitmap==null){
	    	Log.d("Registeactivity", "bitmap is null!");
	    	Toast.makeText(context, "获取照片失败", Toast.LENGTH_SHORT).show();
	    	return null;
	    }
	    return bitmap;
	    }
}