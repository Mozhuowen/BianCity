package com.putaotown.fragment;

import com.putaotown.CreatePutaoActivity;
import com.putaotown.R;
import com.putaotown.WelcomeActivity;
import com.putaotown.localio.UserPreUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DrafExitWithoutSavedDailogFragent extends DialogFragment implements DialogInterface.OnClickListener
{
	private CreatePutaoActivity context;
	public DrafExitWithoutSavedDailogFragent(CreatePutaoActivity context) {
		this.context = context;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
		.setTitle(this.getResources().getString(R.string.dialog_exitwithoutsavedtodraft))
		.setPositiveButton(R.string.dialog_ok, this)
		.setNegativeButton(R.string.dialog_cancel, this)
		.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE){ //ok
			this.dismiss();
			context.finish();
		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			this.dismiss();
		}
	}
	
}