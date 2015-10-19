package com.putaotown.localio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.putaotown.CreatePutaoActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.net.objects.ApplyTown;
import com.putaotown.net.objects.PackagePutao;
import com.putaotown.tools.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserPreUtil
{
	private static Context context = PutaoApplication.getContext();
	private static SharedPreferences.Editor editor = context.getSharedPreferences("user", context.MODE_PRIVATE).edit();
	private static SharedPreferences readpre = context.getSharedPreferences("user", context.MODE_PRIVATE);
	
	/**
	 * 更新用户信息
	 * @param name
	 * @param token
	 * @param cover
	 */
	public static void updateUser(int userid,String name,String token,String cover,String email) {
		editor.putInt("userid", userid);
		editor.putString("name", name);
		editor.putString("token", token);
		editor.putString("cover", cover);
		editor.putString("email", email);
		
		editor.commit();
	}
	public static void updateUserName(String name){
		editor.putString("name", name);
		editor.commit();
	}
	public static void updateUser(int ptuserid,String ptoken,int logintype) {
		editor.putInt("ptuserid", ptuserid);
		editor.putString("ptoken", ptoken);
		editor.putInt("logintype", logintype);
		
		editor.commit();
		//更新readpre
		readpre = context.getSharedPreferences("user", context.MODE_PRIVATE);
	}
	public static void updateUserInfo(String uid,String name,String cover,String sex,String location) {
		/**uid为第三方id*/
		editor.putString("uid",uid);
		editor.putString("name", name);
		editor.putString("cover",cover);
		editor.putString("sex",sex);
		editor.putString("location",location);
		
		editor.commit();
		//更新readpre
		readpre = context.getSharedPreferences("user", context.MODE_PRIVATE);
	}
	public static String getPtoken() {
		String ptoken = readpre.getString("ptoken", "");
		return ptoken;
	}
	public static String getWbtoken() {
		String wbtoken = readpre.getString("wbtoken", "");
		return wbtoken;
	}
	public static String getCover() {
		String cover = readpre.getString("cover", "");
		return cover;
	}
	public static String getSex() {
		String sex = readpre.getString("sex", "");
		return sex;
	}
	public static String getLocation() {
		String location = readpre.getString("location","");
		return location;
	}
	public static void updateCover(String cover) {
		editor.putString("cover", cover);
		editor.commit();
	}
	public static void updateName(String name) {
		editor.putString("name", name);
		editor.commit();
	}
	public static void updateLocation(String location) {
		editor.putString("location",location);
		editor.commit();
	}
	public static void updateSex(String sex) {
		editor.putString("sex",sex);
		editor.commit();
	}
	public static void logout() {
		editor.putInt("ptuserid", 0);
		editor.putString("ptoken", "");
		editor.putInt("logintype", 0);
		editor.putString("name", "");
		editor.putString("mytowns", "");
		
		editor.commit();
		
		//更新readpre
		readpre = context.getSharedPreferences("user", context.MODE_PRIVATE);
		PutaoApplication.hasTowns = false;
	}
	public static void deleteOneTown(int townid) {
		Gson gson = new Gson();
		String tmpstr = readpre.getString("mytowns", null);
		LogUtil.v("UserPreUtil-Mytowns String info:", tmpstr+"");
		List<ApplyTown> list = null;
		if (tmpstr != null){
			list = gson.fromJson(tmpstr, new TypeToken<List<ApplyTown>>(){}.getType());
		} else {
			return;
		}
		for (Iterator<ApplyTown> i= list.iterator();i.hasNext();) {
			ApplyTown at = i.next();
			if (at.getTownid() == townid){
				i.remove();
				break;
			}
		}
		String jsonstr = gson.toJson(list);
		editor.putString("mytowns", jsonstr);
		editor.commit();
		if (list.size()>0)
			PutaoApplication.hasTowns = true;
	}
	
	
	
	
	/**
	 * 添加一个小镇，先解析成list再add再解析成jsonstr存储
	 * @param town
	 */
	public static void updateMyTowns(ApplyTown town) {
		Gson gson = new Gson();
		String tmpstr = readpre.getString("mytowns", null);
//		Log.d("UserPreUtil-Mytowns String info:", tmpstr+"");
		List<ApplyTown> newlist = new ArrayList<ApplyTown>();
		newlist.add(town);
		List<ApplyTown> list = null;
		if (tmpstr != null){
			list = gson.fromJson(tmpstr, new TypeToken<List<ApplyTown>>(){}.getType());
		} else {
			list = new ArrayList<ApplyTown>();
		}
		if (list != null && list.size()>0)
			newlist.addAll(list);
		String jsonstr = gson.toJson(newlist);
		editor.putString("mytowns", jsonstr);
		PutaoApplication.hasTowns = true;
		
		editor.commit();
	}
	public static void updateAllMyTowns(List<ApplyTown> towns) {
		Gson gson = new Gson();
		String jsonstr = gson.toJson(towns);
		editor.putString("mytowns", jsonstr);
		if (towns.size() > 0)
			PutaoApplication.hasTowns = true;
		
		editor.commit();
	}
	public static void updateStoryCount(int townid,int count) {
		Gson gson = new Gson();
		String tmpstr = readpre.getString("mytowns", null);
//		Log.d("UserPreUtil-Mytowns String info:", tmpstr+"");
		List<ApplyTown> list = null;
		if (tmpstr != null ){
			list = gson.fromJson(tmpstr, new TypeToken<List<ApplyTown>>(){}.getType());
		} else
			return;
		for (ApplyTown town: list) {
			if (town.getTownid() == townid) {
				town.setStorycount(count);
			}
		}
		String jsonstr = gson.toJson(list);
		editor.putString("mytowns", jsonstr);
		
		editor.commit();
	}
	public static void updateHasTown() {
		String tmpstr = readpre.getString("mytowns", "");
		if (tmpstr != null && tmpstr.length() > 0) {
			tmpstr = null;
			PutaoApplication.hasTowns = true;
		}
		else {
			tmpstr = null;
			PutaoApplication.hasTowns = false;
		}
	}
	
	public static List<ApplyTown> getMyTowns() {
		Gson gson = new Gson();
		String tmpstr = readpre.getString("mytowns", "");
		LogUtil.v("UserPre towns Info: ", tmpstr);
		List<ApplyTown> list = null;
		if (tmpstr != null && tmpstr.length()>0){
			list = gson.fromJson(tmpstr, new TypeToken<List<ApplyTown>>(){}.getType());
		} else {
			list = new ArrayList<ApplyTown>();
		}
		return list;
	}
	public static List<CreatePutaoActivity.SpinnerTowns> getSpinnerTowns() {
		List<CreatePutaoActivity.SpinnerTowns> objects = new ArrayList<CreatePutaoActivity.SpinnerTowns>();
		Gson gson = new Gson();
		String tmpstr = readpre.getString("mytowns", "");
		List<ApplyTown> list = null;
		if (tmpstr != null && tmpstr.length()>0){
			list = gson.fromJson(tmpstr, new TypeToken<List<ApplyTown>>(){}.getType());
		} else {
			list = new ArrayList<ApplyTown>();
		}
		if (list.size() > 0)
			for (ApplyTown at: list) {
				CreatePutaoActivity.SpinnerTowns town = new CreatePutaoActivity.SpinnerTowns();
				town.setTownid(at.getTownid());
				town.setTowname(at.getTownname());
				objects.add(town);
			}
		
		return objects;
	}
	
	public static String getUserCover() {
		String name = readpre.getString("cover", "");
		return name;
	}
	
	public static int getUserid() {
		int ptuserid = readpre.getInt("ptuserid", 0);
		LogUtil.v("UserPreUtil info-get UserID: ", ""+ptuserid);
		
		return ptuserid;
	}
	/**
	 * 获取用户名
	 * @return
	 */
	public static String getUsername() {
		String name = readpre.getString("name", "");
		return name;
	}
	/**
	 * 获取认证token
	 * @return
	 */
	public static String getToken() {
		String token = readpre.getString("ptoken", "");
		return token;
	}
	/**
	 * 获取用户信息
	 * @return
	 */
	public static Map getUserInfo() {
		Map map = new HashMap();
		String name = readpre.getString("name", "");
		String cover = readpre.getString("cover", "");
		String token = readpre.getString("token", "");
		String email = readpre.getString("email", "");
		
		map.put("name", name);
		map.put("cover", cover);
		map.put("email", email);
		map.put("token", token);
		return map;
	}
	
	public static boolean checklogin() {
		String name = readpre.getString("name", "");
		return name.equals("")?false:true;
	}
}