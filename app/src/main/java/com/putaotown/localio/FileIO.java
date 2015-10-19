package com.putaotown.localio;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.security.MessageDigest;  
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;  

import com.putaotown.tools.BitmapUtil;
import com.putaotown.tools.LogUtil;

public class FileIO
{
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
		'A', 'B', 'C', 'D', 'E', 'F' };
	
	public static void copyByUri(Context context,String targetpath,Uri uri) {
		try {
			InputStream in = context.getContentResolver().openInputStream(uri);
			FileOutputStream out = new FileOutputStream(targetpath);
			byte[] bbuf = new byte[1024];
			int hasRead = 0;
			while ((hasRead = in.read(bbuf)) > 0) {
				out.write(bbuf,0,hasRead);
			}
			in.close();
			out.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	public static void copyBySourcePath(String targetpath,String sourcepath) {
		InputStream in;
		try {
			in = new FileInputStream(sourcepath);
			FileOutputStream out = new FileOutputStream(targetpath);
			byte[] buffer = new byte[1024];
			int hasRead = 0;
			while((hasRead = in.read(buffer)) > 0) {
				out.write(buffer,0,hasRead);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	/**
	 * 由uri获取bitmap
	 * @param context
	 * @param uri
	 * @return
	 */
	public static Bitmap decodeUriAsBitmap(Context context,Uri uri){
	    Bitmap bitmap = null;
	    try {
	    bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
	        } catch (FileNotFoundException e) {e.printStackTrace();return null;}
	    return bitmap;
	    }
	public static Bitmap decodeUriAsBitmap(Context context,Uri uri,int width){
	    Bitmap bitmap = null;
	    try {
	    bitmap = BitmapUtil.decodeSampledBitmapFromFileDescriptor(context.getContentResolver().openAssetFileDescriptor(uri, "r").getFileDescriptor(), width, width);
	        } catch (FileNotFoundException e) {e.printStackTrace();return null;}
	    return bitmap;
	    }
	public static Bitmap getBitmapFromPath(Context context,String path,int width) {
		Bitmap bitmap = null;
		FileInputStream fin;
		try {
			fin = new FileInputStream(path);
			bitmap = BitmapUtil.decodeSampledBitmapFromFileDescriptor(fin.getFD(), width, width);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogUtil.v("FileIO info: ", "getbitmap return null!");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.v("FileIO info: ", "getbitmap return null!");
			return null;
		}		
		
		return bitmap;
	}
	
	public static List<ContentValues> getDraftList(Context context) {
		List<ContentValues> list = new ArrayList<ContentValues>();
		SQLiteDatabase db = new DraftHelper(context,"Draft.db",null,1).getWritableDatabase();
		Cursor cursor = db.query("Draft", null, "isfinish=?", new String[]{"0"}, null, null, null);
		
		if (cursor.moveToFirst()) {
			do{
			LogUtil.v("Query table Draft: ", cursor.getString(1)+" "
					+cursor.getString(2)+" "
					+cursor.getString(3)+" "
					+cursor.getString(4)+" "
					+cursor.getString(5)+" "
					+cursor.getString(6)+" "
					+cursor.getString(7)+" "
					+cursor.getString(8)+" "
					+cursor.getString(9)+" "
					+cursor.getString(10)+" ");
			ContentValues values = new ContentValues();
			values.put("randomcode", cursor.getString(cursor.getColumnIndex("randomcode")));
			values.put("type", cursor.getInt(cursor.getColumnIndex("type")));
			values.put("title", cursor.getString(cursor.getColumnIndex("title")));
			values.put("cover", cursor.getString(cursor.getColumnIndex("cover")));
			values.put("content", cursor.getString(cursor.getColumnIndex("content")));
			values.put("images", cursor.getString(cursor.getColumnIndex("images")));
			values.put("townid", cursor.getString(cursor.getColumnIndex("townid")));
			values.put("freeaddr", cursor.getString(cursor.getColumnIndex("freeaddr")));
			values.put("isfinish", cursor.getString(cursor.getColumnIndex("isfinish")));
			values.put("geoinfo", cursor.getString(cursor.getColumnIndex("geoinfo")));
			list.add(values);			
			}while(cursor.moveToNext());
		}
		
		db.close();
		cursor.close();
		return list;
	}
	/**退出登录清空草稿*/
	public static void clearDraft(Context context) {
		SQLiteDatabase db = new DraftHelper(context,"Draft.db",null,DraftHelper.VERSION).getWritableDatabase();
		ContentValues dbvalues = new ContentValues();
		dbvalues.put("isfinish", 1);
		db.update("Draft", dbvalues, null, null);
		
	}
	/**
	 * 获取文件大小
	 * @param targetpath 文件路径
	 * @return
	 */
	public static int getFileSize(String targetpath) {
		File file = new File(targetpath);
		return (int)file.length();
	}
	/**
	 * 获取文件md5
	 * @param filepath
	 * @return
	 */
	public static String getMd5(String filepath) {	
		return md5sum(filepath);
	}
	
	public static String toHexString(byte[] b) {  
        StringBuilder sb = new StringBuilder(b.length * 2);  
        for (int i = 0; i < b.length; i++) {  
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);  
            sb.append(HEX_DIGITS[b[i] & 0x0f]);  
        }  
        return sb.toString();  
    }  
  
    public static String md5sum(String filename) {  
        InputStream fis;  
        byte[] buffer = new byte[1024];  
        int numRead = 0;  
        MessageDigest md5;  
        try{  
            fis = new FileInputStream(filename);  
            md5 = MessageDigest.getInstance("MD5");  
            while((numRead=fis.read(buffer)) > 0) {  
                md5.update(buffer,0,numRead);  
            }  
            fis.close();  
            return toHexString(md5.digest());     
        } catch (Exception e) {  
            System.out.println("error");  
            return null;  
        }  
    }
	
}