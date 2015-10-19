package com.putaotown.fragment;

import com.putaotown.R;
import com.putaotown.WelcomeActivity;
import com.putaotown.localio.UserPreUtil;
import com.putaotown.net.objects.ModelAppUpdate;
import com.putaotown.update.UpdateService;
import com.xiaomi.mipush.sdk.MiPushClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UpdateDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
	private ModelAppUpdate updateinfo;
	public UpdateDialogFragment(ModelAppUpdate update) {
		this.updateinfo = update;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		TextView contentview = new TextView(UpdateDialogFragment.this.getContext());
		contentview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		contentview.setPadding(10, 10, 10, 10);
		contentview.setText(updateinfo.getUpdateinfo());
		
		return new AlertDialog.Builder(getActivity())
		.setTitle(this.getResources().getString(R.string.diloag_title_newversion))
		.setView(contentview)
		.setPositiveButton(R.string.dialog_update_ok, this)
		.setNegativeButton(R.string.dialog_cancel, this)
		.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if (which == DialogInterface.BUTTON_POSITIVE){ //ok
			UpdateDialogFragment.this.dismiss();
			//then update
			Intent intent = new Intent(this.getActivity(),UpdateService.class);
			intent.putExtra("Key_App_Name","边城");
			intent.putExtra("Key_Down_Url",updateinfo.getDownloadurl());						
			this.getActivity().startService(intent);
		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			UpdateDialogFragment.this.dismiss();
		}
	}
}