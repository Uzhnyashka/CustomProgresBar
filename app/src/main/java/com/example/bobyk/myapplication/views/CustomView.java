package com.example.bobyk.myapplication.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.bobyk.myapplication.Point;
import com.example.bobyk.myapplication.R;

import java.util.ArrayList;

/**
 * Created by bobyk on 29.07.16.
 */
public class CustomView extends View {

    private int color;
    int height = 0;
    int width = 0;
    private Paint paint = new Paint();
    private ValueAnimator moveAnimator;
    private ObjectAnimator anim;
    private Bitmap bitmapIcon;
    private ArrayList<Point> points;

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView);

        try {
            color = a.getColor(R.styleable.CustomView_barColor, 0xff000000);
            Drawable icon = a.getDrawable(R.styleable.CustomView_barIcon);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
            if (bitmapDrawable != null) {
                bitmapIcon = bitmapDrawable.getBitmap();
            }
            else {
                bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            }
        }finally {
            a.recycle();
        }
        paint.setColor(color);
        initAnimation();
    }

    private void initAnimation(){
        moveAnimator = ValueAnimator.ofInt(0, 1337);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int littleRadius = Math.min(height, width) / 20;
        int bigRadius = Math.min(height, width) / 6;

        Bitmap littleBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, littleRadius * 2, littleRadius * 2
                , false);
        Bitmap bigBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, bigRadius * 2, bigRadius * 2, false);
        for (Point point : points){
            canvas.drawBitmap(littleBitmapIcon, point.getPosX() - littleBitmapIcon.getWidth() / 2, point.getPosY() - littleBitmapIcon.getHeight() / 2, paint);
        }

        canvas.drawBitmap(bigBitmapIcon, width / 2 - bigBitmapIcon.getWidth() / 2, height / 2 - bigBitmapIcon.getHeight() / 2, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        points = new ArrayList<>();
        height = h;
        width = w;

        int littleRadius = Math.min(height, width) / 20;

        int diff = Math.abs(height - width) / 2;

        if (height >= width){
            points.add(new Point(littleRadius, littleRadius + diff));
            points.add(new Point(width - littleRadius, littleRadius + diff));
            points.add(new Point(littleRadius, height - littleRadius - diff));
            points.add(new Point(width - littleRadius, height - littleRadius - diff));
        }
        else {
            points.add(new Point(littleRadius + diff, littleRadius));
            points.add(new Point(width - littleRadius - diff, littleRadius));
            points.add(new Point(littleRadius + diff, height - littleRadius));
            points.add(new Point(width - littleRadius - diff, height - littleRadius));
        }

    }
}
