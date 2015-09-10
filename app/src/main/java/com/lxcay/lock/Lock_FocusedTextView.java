package com.lxcay.lock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class Lock_FocusedTextView extends TextView{


	public Lock_FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Lock_FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Lock_FocusedTextView(Context context) {
		super(context);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
