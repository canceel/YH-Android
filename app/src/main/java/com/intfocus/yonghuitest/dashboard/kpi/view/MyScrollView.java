package com.intfocus.yonghuitest.dashboard.kpi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyScrollView extends HorizontalScrollView {

	public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScrollView(Context context) {
		super(context);
	}

	private boolean action_down;

	public boolean isAction_down() {
		return action_down;
	}

	public void setAction_down(boolean action_down) {
		this.action_down = action_down;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			action_down = true;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			action_down = false;
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (arg0.getAction() == MotionEvent.ACTION_UP) {
			action_down = false;
		}
		return super.onTouchEvent(arg0);
	}
}
