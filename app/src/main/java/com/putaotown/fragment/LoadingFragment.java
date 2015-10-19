package com.putaotown.fragment;

import com.putaotown.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoadingFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentLayout = inflater.inflate(R.layout.fragment_loading, container,
				false);
		return fragmentLayout;
	}
}