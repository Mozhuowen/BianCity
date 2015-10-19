package com.putaotown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TestActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		
		
	}
	
	public static void startAction(Context context) {
		Intent intent = new Intent(context,TestActivity.class);
		context.startActivity(intent);
	}
}