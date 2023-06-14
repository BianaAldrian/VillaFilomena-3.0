package com.example.villafilomena.subclass;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class ResizableImageView extends androidx.appcompat.widget.AppCompatImageView {
    public ResizableImageView(Context context) {
        super(context);
    }

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Get the width of the screen
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Set the height of the view based on the screen width
        int height = screenWidth * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(screenWidth, height);
    }
}
