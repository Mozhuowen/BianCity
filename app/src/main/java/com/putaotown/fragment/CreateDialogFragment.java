package com.putaotown.fragment;

import com.putaotown.CreatePutaoActivity;
import com.putaotown.GetGeoActivity;
import com.putaotown.PutaoApplication;
import com.putaotown.R;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

public class CreateDialogFragment extends DialogFragment
{
	View mainview;
	@Override
	public View onCreateView(LayoutInflater layoutinflater,ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(layoutinflater, container, savedInstanceState);
		mainview = this.getActivity().getLayoutInflater().inflate(R.layout.dialog_create, null);
		this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.getDialog().setCanceledOnTouchOutside(true);
		View townv = mainview.findViewById(R.id.dialog_create_town);
		View storyv = mainview.findViewById(R.id.dialog_create_story);
		if (!PutaoApplication.getHasTowns())
			storyv.setVisibility(View.GONE);
		townv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				CreateDialogFragment.this.dismiss();
				GetGeoActivity.startAction(CreateDialogFragment.this.getActivity());
			}
			
		});
		storyv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				CreateDialogFragment.this.dismiss();
				CreatePutaoActivity.startAction(CreateDialogFragment.this.getActivity(), 0);
			}
			
		});
		return mainview;
	}
	
}