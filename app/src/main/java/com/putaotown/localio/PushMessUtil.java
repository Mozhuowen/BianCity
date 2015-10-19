package com.putaotown.localio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.putaotown.PutaoApplication;
import com.putaotown.net.objects.ModelPushComment;
import com.putaotown.net.objects.ModelPushGood;
import com.putaotown.net.objects.ModelPushSys;
import com.putaotown.net.objects.ModelPushTie;
import com.putaotown.tools.LogUtil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PushMessUtil
{
	/**写入一条消息至数据库*/
	public static void receiveMess(int type,Object object) {
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String objstr = new Gson().toJson(object);
		
		ContentValues dbvalues = new ContentValues();
		dbvalues.put("type", type);
		dbvalues.put("hasread", 0);
		dbvalues.put("receivetime", Calendar.getInstance().getTimeInMillis());
		dbvalues.put("userid", UserPreUtil.getUserid());
		switch(type) {
		case 0:										//0和1都是comment
			dbvalues.put("comment", objstr);
			break;
		case 1:
			dbvalues.put("comment", objstr);
			break;
		case 2:
			dbvalues.put("good", objstr);
			break;
		case 3:
			dbvalues.put("system", objstr);
			break;
		case 4:					//社区
			dbvalues.put("community", objstr);
			break;
		}
		
		//插入
		db.insert("Pushmess", null, dbvalues);
		db.close();
	}
	/**获取未读评论消息数量*/
	public static int getUnreadComCou() {
		int count = 0;
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from Pushmess where hasread=0 and type<2 and userid=?", new String[]{currUserid});
		cursor.moveToFirst();
//		LogUtil.v("PushMessUtil info: ", cursor.getString(0));
		count = cursor.getInt(0);
		
		db.close();
		cursor.close();
		return count;
	}
	/**获取未读赞数量*/
	public static int getUnreadGoodCou() {
		int count = 0;
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from Pushmess where hasread=0 and type=2 and userid=?", new String[]{currUserid});
		cursor.moveToFirst();
//		LogUtil.v("PushMessUtil info: ", cursor.getString(0));
		count = cursor.getInt(0);
		
		db.close();
		cursor.close();
		return count;
	}
	/**获取未读系统消息数量*/
	public static int getUnreadSysCou() {
		int count = 0;
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from Pushmess where hasread=0 and type=3 and userid=?", new String[]{currUserid});
		cursor.moveToFirst();
//		LogUtil.v("PushMessUtil info: ", cursor.getString(0));
		count = cursor.getInt(0);		
		db.close();
		cursor.close();
		return count;
	}
	/**获取未读社区消息数量*/
	public static int getUnreadBBSCou() {
		int count = 0;
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from Pushmess where hasread=0 and type=4 and userid=?", new String[]{currUserid});
		cursor.moveToFirst();
//		LogUtil.v("PushMessUtil info: ", cursor.getString(0));
		count = cursor.getInt(0);		
		db.close();
		cursor.close();
		return count;
	}
	/**获取所有未读消息数量*/
	public static int getAllUnreadCou() {
		int count = 0;
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from Pushmess where hasread=0 and userid=?", new String[]{currUserid});
		cursor.moveToFirst();
//		LogUtil.v("PushMessUtil info: ", cursor.getString(0));
		count = cursor.getInt(0);		
		db.close();
		cursor.close();
		return count;
	}

	/**获取所有评论*/
	public static List<ModelPushComment> getComments() {
		List<ModelPushComment> list = new ArrayList<ModelPushComment>();
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		//小于3都是评论
		Cursor cursor = db.query("Pushmess", null, "type<? and userid=?", new String[]{"2",currUserid}, null, null, "receivetime desc");
		if ( cursor.moveToFirst() ) {
			Gson gson = new Gson();
			do {
				String str = cursor.getString(cursor.getColumnIndex("comment"));
				if ( str != null && str.length() > 0 ) {
					list.add(gson.fromJson(str, ModelPushComment.class));
				}				
			} while( cursor.moveToNext() );
			gson = null;
		}
		cursor.close();
		db.close();		
		return list;
	}
	/**获取所有赞消息*/
	public static List<ModelPushGood> getGoods() {
		List<ModelPushGood> list = new ArrayList<ModelPushGood>();
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		//2是赞
		Cursor cursor = db.query("Pushmess", null, "type=? and userid=?", new String[]{"2",currUserid}, null, null, "receivetime desc");
		if ( cursor.moveToFirst() ) {
			Gson gson = new Gson();
			do {
				String str = cursor.getString(cursor.getColumnIndex("good"));
				if ( str != null && str.length() > 0 ) {
					list.add(gson.fromJson(str, ModelPushGood.class));
				}				
			} while( cursor.moveToNext() );
			gson = null;
		}
		cursor.close();
		db.close();		
		return list;
	}
	/**获取所有系统消息*/
	public static List<ModelPushSys> getSys() {
		List<ModelPushSys> list = new ArrayList<ModelPushSys>();
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		//2是赞
		Cursor cursor = db.query("Pushmess", null, "type=? and userid=?", new String[]{"3",currUserid}, null, null, "receivetime desc");
		if ( cursor.moveToFirst() ) {
			Gson gson = new Gson();
			do {
				String str = cursor.getString(cursor.getColumnIndex("system"));
				if ( str != null && str.length() > 0 ) {
					list.add(gson.fromJson(str, ModelPushSys.class));
				}				
			} while( cursor.moveToNext() );
			gson = null;
		}
		cursor.close();
		db.close();
		
		return list;
	}
	/**获取所有社区消息*/
	public static List<ModelPushTie> getCommunity() {
		List<ModelPushTie> list = new ArrayList<ModelPushTie>();
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());

		Cursor cursor = db.query("Pushmess", null, "type=? and userid=?", new String[]{"4",currUserid}, null, null, "receivetime desc");
		if ( cursor.moveToFirst() ) {
			Gson gson = new Gson();
			do {
				String str = cursor.getString(cursor.getColumnIndex("community"));
				if ( str != null && str.length() > 0 ) {
					list.add(gson.fromJson(str, ModelPushTie.class));
				}				
			} while( cursor.moveToNext() );
			gson = null;
		}
		cursor.close();
		db.close();
		
		return list;
	}
	
	public static void resetUnreadCom() {
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		ContentValues value = new ContentValues();
		value.put("hasread", 1);
		db.update("Pushmess", value, "type<? and userid=?", new String[]{"3",currUserid});
		db.close();
	}
	
	public static void resetUnreadGoods() {
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		ContentValues value = new ContentValues();
		value.put("hasread", 1);
		db.update("Pushmess", value, "type=2 and userid=?", new String[]{currUserid});
		db.close();
	}
	
	public static void resetUnreadSys() {
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		ContentValues value = new ContentValues();
		value.put("hasread", 1);
		db.update("Pushmess", value, "type=3 and userid=?", new String[]{currUserid});
		db.close();
	}
	
	public static void resetUnreadBBS() {
		SQLiteDatabase db = new PushMessHelper(PutaoApplication.getContext(),"Pushmess.db",null,2).getWritableDatabase();
		String currUserid = String.valueOf(UserPreUtil.getUserid());
		ContentValues value = new ContentValues();
		value.put("hasread", 1);
		db.update("Pushmess", value, "type=4 and userid=?", new String[]{currUserid});
		db.close();
	}
	
}