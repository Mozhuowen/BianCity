package com.putaotown.fragment;

import com.putaotown.R;
import com.putaotown.WelcomeActivity;
import com.putaotown.localio.UserPreUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class LogOutDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new AlertDialog.Builder(getActivity())
		.setTitle(this.getResources().getString(R.string.dialog_exit))
		.setPositiveButton(R.string.dialog_ok, this)
		.setNegativeButton(R.string.dialog_cancel, this)
		.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if (which == DialogInterface.BUTTON_POSITIVE){ //ok
			LogOutDialogFragment.this.dismiss();
			UserPreUtil.logout();
			//注销小米推送alias
	  		MiPushClient.setAlias(this.getContext(), "0", null);
	  		this.getActivity().startActivity(new Intent(this.getActivity(),WelcomeActivity.class));
			this.getActivity().finish();
		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			LogOutDialogFragment.this.dismiss();
		}
	}
	
}