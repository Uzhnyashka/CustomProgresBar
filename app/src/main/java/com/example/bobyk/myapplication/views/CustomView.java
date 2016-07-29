package com.example.bobyk.myapplication.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.bobyk.myapplication.R;

/**
 * Created by bobyk on 29.07.16.
 */
public class CustomView extends View {

    private int color;
    int height = 0;
    int width = 0;
    private Paint paint = new Paint();

    public CustomView(Context context){
        super(context);
        init(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defType){
        super(context, attrs, defType);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);

        try {
            color = a.getColor(R.styleable.CustomView_circlecolor, 0xff000000);
        }finally {
            a.recycle();
        }
        paint.setColor(color);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int littleRadius = Math.min(height, width) / 20;
        int bigRadius = Math.min(height, width) / 6;

        int pos1X, pos1Y, pos2X, pos2Y, pos3X, pos3Y, pos4X, pos4Y;


        if (height >= width){
            int diff = (height - width) / 2;
            canvas.drawCircle(littleRadius, littleRadius + diff, littleRadius, paint);
            canvas.drawCircle(width - littleRadius, littleRadius + diff, littleRadius, paint);
            canvas.drawCircle(littleRadius, height - littleRadius - diff, littleRadius, paint);
            canvas.drawCircle(width - littleRadius, height - littleRadius - diff, littleRadius, paint);


            canvas.drawCircle(width / 2, height / 2, bigRadius, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        System.out.println("Width: " + getMeasuredWidth()  + "  Height: " + getMeasuredHeight());
    }
}
