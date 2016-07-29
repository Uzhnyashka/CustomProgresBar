package com.example.bobyk.myapplication.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.bobyk.myapplication.R;

/**
 * Created by bobyk on 29.07.16.
 */
public class CustomView extends View {

    private int color;
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
        canvas.drawCircle(100, 100, 30, paint);
    }


}
