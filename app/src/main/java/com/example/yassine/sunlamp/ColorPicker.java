package com.example.yassine.sunlamp; /**
 * Created by YassIne on 06/09/2015.

*/

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker extends Dialog {

    public interface OnColorChangedListener {
        void colorChanged(String key, int color);
    }

    private OnColorChangedListener mListener;
    private int mInitialColor, mDefaultColor;
    private String mKey;

    public static class ColorPickerView extends View {
        private Paint mPaint;
        private Paint mPointer;
        private Paint mTrianglePointer;
        private Paint mCirlePointerShadow;
        private Paint mCirlePointer;

        private Context context;

        private float mCurrentHue = 0;
        private int mCurrentX = 0, mCurrentY = 0;
        private int mCurrentColor, mDefaultColor;
        private OnColorChangedListener mListener;
        private int[] mColorArray ;
        Bitmap trianglePointerBitmap;
        float mTriangleWidth;
        float mTriangleHeight;



        //final float mColorBarHeight = 60;
        final float mColorBarHeight = 35;
        final float mColorBarWidth = 480;

        float mXOnTouch = mColorBarHeight;

        //dimensione della view
        float mViewWidth;
        float mViewHeight;

        /*
        This is used to create the linear gradient of colors
         */
        private Shader mLinearGradientShader;

        ColorPickerView(Context c, OnColorChangedListener l, int color, int defaultColor, int [] colorsArray) {
            super(c);
            context = c;
            mListener = l;
            mDefaultColor = defaultColor;
            mColorArray = colorsArray;



            // Get the current hue from the current color and update the main
            // color field
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            mCurrentHue = hsv[0];

            mCurrentColor = color;

            // Initializes the Paint that will draw the View
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(12);

            //--------------------------------------------
            // CIRCLE POINTER (O)
            //--------------------------------------------
            mCirlePointerShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCirlePointerShadow.setColor(Color.WHITE);
            mCirlePointerShadow.setAlpha(0x50);

            mCirlePointer = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCirlePointer.setColor(colorsArray[0]);

            //----------------------------------------------
            //  TRIANGLE POINTER
            //----------------------------------------------
            /*
            mPointer = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPointer.setColor(Color.BLACK);
            mPointer.setAlpha(0x70);
            mTrianglePointer = new Paint(Paint.ANTI_ALIAS_FLAG);

            trianglePointerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.color_pointer);
            mTriangleWidth = trianglePointerBitmap.getWidth()/2;
            mTriangleHeight = trianglePointerBitmap.getHeight();
            */

            /*
            Initialisation of the lineargradient

            float   startX = mTriangleWidth,
                    startY = 0,
                    endX = mColorBarWidth,
                    endY = 0;
            */
            float   startX = mColorBarHeight,
                    startY = 0,
                    endX = mColorBarWidth,
                    endY = 0;

            mLinearGradientShader = new LinearGradient(startX,startY,endX,endY,mColorArray,null, Shader.TileMode.REPEAT);

        }


        @Override
        protected void onDraw(Canvas canvas) {

            mPaint.setShader(mLinearGradientShader);
            //canvas.drawRect(mTriangleWidth, 0, mColorBarWidth + mTriangleWidth, mColorBarHeight, mPaint);
            float   left = mColorBarHeight,
                    top = mColorBarHeight/2;


            canvas.drawRect(left, top , mColorBarWidth + left, mColorBarHeight + top, mPaint);

            //Disegna il triangolo di selezione

            //canvas.drawBitmap(trianglePointerBitmap, mXOnTouch , mColorBarHeight - 2 , mTrianglePointer);

            //----------------------------------------------
            // DRAW CIRCLE POINTER
            //----------------------------------------------

            float circlePointerYPosition = (float) mColorBarHeight;

            float circlePointerSize = mColorBarHeight;

            canvas.drawCircle(mXOnTouch, circlePointerYPosition, circlePointerSize , mCirlePointerShadow);

            canvas.drawCircle(mXOnTouch, circlePointerYPosition, circlePointerSize - 3 , mCirlePointer);

            }


            /*
            // Display the circle around the currently selected color in the
            // main field
            if (mCurrentX != 0 && mCurrentY != 0) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.BLACK);
                canvas.drawCircle(mCurrentX, mCurrentY, 10, mPaint);
            }

            // Draw a 'button' with the currently selected color
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mCurrentColor);
            canvas.drawRect(10, 316, 138, 356, mPaint);

            // Set the text color according to the brightness of the color
            if (Color.red(mCurrentColor) + Color.green(mCurrentColor) + Color.blue(mCurrentColor) < 384)
                mPaint.setColor(Color.WHITE);
            else
                mPaint.setColor(Color.BLACK);
            canvas.drawText("Pick", 74, 340, mPaint);

            // Draw a 'button' with the default color
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mDefaultColor);
            canvas.drawRect(138, 316, 266, 356, mPaint);

            // Set the text color according to the brightness of the color
            if (Color.red(mDefaultColor) + Color.green(mDefaultColor)
                    + Color.blue(mDefaultColor) < 384)
                mPaint.setColor(Color.WHITE);
            else
                mPaint.setColor(Color.BLACK);
            canvas.drawText("Pick", 202, 340,
                    mPaint);
        }*/

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            mViewWidth = mColorBarWidth + mColorBarHeight;
            //mViewHeight = mColorBarHeight; //+ mTriangleHeight;
            mViewHeight = mColorBarHeight * 2;
            setMeasuredDimension((int) mViewWidth, (int) mViewHeight);

        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();


            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
                float minY =  mColorBarHeight / 2,
                        maxY = mColorBarHeight * 2 - minY;
                if (x >= mColorBarHeight && x <= (mColorBarWidth - mColorBarHeight)&& (y >= minY && y <= maxY)) {
                    mXOnTouch = x + mColorBarHeight;
                    int actualDivFactor = (int) mColorBarWidth / mColorArray.length;
                    int currentColorSelected =(int) (x / actualDivFactor);

                    if(currentColorSelected < mColorArray.length) {
                        mCirlePointer.setColor(mColorArray[currentColorSelected]);
                        //mListener.colorChanged("", mColorArray[currentColorSelected]);
                    }
                    invalidate();
                }
            }

            /*

            // If the touch event is located in the hue bar
            if (x > 10 && x < 266 && y > 0 && y < 40) {
                // Update the main field colors
                mCurrentHue = (255 - x) * 360 / 255;

                // Update the current selected color
                int transX = mCurrentX - 10;
                int transY = mCurrentY - 60;
                int index = 256 * (transY - 1) + transX;
                if (index > 0 && index < mMainColors.length)
                    mCurrentColor = mMainColors[256 * (transY - 1) + transX];

                // Force the redraw of the dialog
                invalidate();
            }

            // If the touch event is located in the main field
            if (x > 10 && x < 266 && y > 50 && y < 306) {
                mCurrentX = (int) x;
                mCurrentY = (int) y;
                int transX = mCurrentX - 10;
                int transY = mCurrentY - 60;
                int index = 256 * (transY - 1) + transX;
                if (index > 0 && index < mMainColors.length) {
                    // Update the current color
                    mCurrentColor = mMainColors[index];
                    // Force the redraw of the dialog
                    invalidate();
                }
            }

            // If the touch event is located in the left button, notify the
            // listener with the current color
            if (x > 10 && x < 138 && y > 316 && y < 356)
                mListener.colorChanged("", mCurrentColor);

            // If the touch event is located in the right button, notify the
            // listener with the default color
            if (x > 138 && x < 266 && y > 316 && y < 356)
                mListener.colorChanged("", mDefaultColor);
            */
            return true;
        }
    }

    public ColorPicker(Context context, OnColorChangedListener listener,
                       String key, int initialColor, int defaultColor) {
        super(context);

        mListener = listener;
        mKey = key;
        mInitialColor = initialColor;
        mDefaultColor = defaultColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(String key, int color) {
                mListener.colorChanged(mKey, color);
                dismiss();
            }
        };

        setContentView(new ColorPickerView(getContext(), l, mInitialColor,
                mDefaultColor, null));
        setTitle("Pick a color");

    }
}