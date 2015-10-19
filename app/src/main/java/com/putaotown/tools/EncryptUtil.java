package com.putaotown.tools;

public class EncryptUtil
{
	static {
		System.loadLibrary("EncryptMsg");
	}

	public static native byte[] getImportantInfoByJNI(String str,int jint);
}