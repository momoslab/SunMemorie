package com.example.yassine.sunlamp.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by YassIne on 07/11/2015.
 */
public class RoundedFrameLayout extends FrameLayout {
    Path mPath;
    float mCornerRadius;

    public RoundedFrameLayout(Context context) {
        super(context);
    }

    public RoundedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCornerRadius = 10;

    }

    public void draw(Canvas canvas){
        canvas.save();
        canvas.clipPath(mPath);
        super.draw(canvas);
        canvas.restore();
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        RectF r = new RectF(0, 0, w, h);
        mPath = new Path();
        mPath.addRoundRect(r, mCornerRadius, mCornerRadius, Path.Direction.CW);
        mPath.close();
    }


    public void setCornerRadius(int radius){
        mCornerRadius = radius;
        invalidate();
    }



}
