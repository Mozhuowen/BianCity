package com.photoselector.util;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;


public class AnimationUtil implements AnimationListener {

	private Animation animation;
	private OnAnimationEndListener animationEndListener; //
	private OnAnimationStartListener animationStartListener; //
	private OnAnimationRepeatListener animationRepeatListener;

	public AnimationUtil(Context context, int resId) {
		this.animation = AnimationUtils.loadAnimation(context, resId);
		this.animation.setAnimationListener(this);
	}

	public AnimationUtil(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta,
				toYDelta);
	}

	public AnimationUtil setStartOffSet(long startOffset) {
		animation.setStartOffset(startOffset);
		return this;
	}

	public AnimationUtil setInterpolator(Interpolator i) {
		animation.setInterpolator(i);
		return this;
	}
	
	public AnimationUtil setLinearInterpolator() {
		animation.setInterpolator(new LinearInterpolator());
		return this;
	}

	public void startAnimation(View view) {
		view.startAnimation(animation);
	}

	public static void startAnimation(int resId, View view) {
		view.setBackgroundResource(resId);
		((AnimationDrawable) view.getBackground()).start();
	}

	public AnimationUtil setDuration(long durationMillis) {
		animation.setDuration(durationMillis);
		return this;
	}

	public AnimationUtil setFillAfter(boolean fillAfter) {
		animation.setFillAfter(fillAfter);
		return this;
	}

	public interface OnAnimationEndListener {
		void onAnimationEnd(Animation animation);
	}

	public interface OnAnimationStartListener {
		void onAnimationStart(Animation animation);
	}

	public interface OnAnimationRepeatListener {
		void onAnimationRepeat(Animation animation);
	}

	public AnimationUtil setOnAnimationEndLinstener(
			OnAnimationEndListener listener) {
		this.animationEndListener = listener;
		return this;
	}

	public AnimationUtil setOnAnimationStartLinstener(
			OnAnimationStartListener listener) {
		this.animationStartListener = listener;
		return this;
	}

	public AnimationUtil setOnAnimationRepeatLinstener(
			OnAnimationRepeatListener listener) {
		this.animationRepeatListener = listener;
		return this;
	}

	public void setAnimationListener(AnimationListener animationListener) {
		animation.setAnimationListener(animationListener);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (this.animationStartListener != null) {
			this.animationStartListener.onAnimationStart(animation);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (this.animationEndListener != null) {
			this.animationEndListener.onAnimationEnd(animation);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		if (this.animationRepeatListener != null) {
			this.animationRepeatListener.onAnimationRepeat(animation);
		}
	}

}
