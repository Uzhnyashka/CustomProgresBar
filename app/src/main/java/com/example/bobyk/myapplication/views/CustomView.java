package com.example.bobyk.myapplication.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
    private Paint bigPaint = new Paint();
    private Paint paintLine = new Paint();
    private Paint littlePaint = new Paint();
    private ValueAnimator bigAlphaAnimator;
    private ValueAnimator littleAlphaAnimator;
    private Bitmap bitmapIcon;
    private ArrayList<Point> points;
    private Bitmap littleBitmapIcon;
    private Bitmap bigBitmapIcon;

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
        bigPaint.setAlpha(0);
        littlePaint.setAlpha(255);
        paintLine.setColor(color);
        paintLine.setStrokeWidth(4);
    }

    private void initAnimation(){
        bigAlphaAnimator = ValueAnimator.ofInt(0, 255);
        bigAlphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        bigAlphaAnimator.setRepeatCount(5);
        bigAlphaAnimator.setDuration(300);
        bigAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                bigPaint.setAlpha((int) valueAnimator.getAnimatedValue());
                CustomView.this.invalidate();
            }
        });

        littleAlphaAnimator = ValueAnimator.ofInt(0, 255);
        littleAlphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        littleAlphaAnimator.setRepeatCount(6);
        littleAlphaAnimator.setDuration(300);
        littleAlphaAnimator.setStartDelay(300);
        littleAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                littlePaint.setAlpha((int) valueAnimator.getAnimatedValue());
                CustomView.this.invalidate();
            }
        });

        AnimatorSet topLeftToCenter = moveTopLeftToCenter();
        AnimatorSet botLeftToCenter = moveBotLeftToCenter();
        botLeftToCenter.setStartDelay(200);
        AnimatorSet botRightToCenter = moveBotRightToCenter();
        botRightToCenter.setStartDelay(400);
        AnimatorSet topRightToCenter = moveTopRightToCenter();
        topRightToCenter.setStartDelay(600);

        AnimatorSet moveToCenterAnimator = new AnimatorSet();
        moveToCenterAnimator.playTogether(topLeftToCenter/*, botLeftToCenter, botRightToCenter, topRightToCenter*/);

        AnimatorSet topLeftFromCenter = moveTopLeftFromCenter();
        AnimatorSet botLeftFromCenter = moveBotLeftFromCenter();
        botLeftFromCenter.setStartDelay(200);
        AnimatorSet botRightFromCenter = moveBotRightFromCenter();
        botRightFromCenter.setStartDelay(400);
        AnimatorSet topRightFromCenter = moveTopRightFromCenter();
        topRightFromCenter.setStartDelay(600);

        AnimatorSet moveFromCenterAnimator = new AnimatorSet();
        moveFromCenterAnimator.playTogether(topLeftFromCenter/*, botLeftFromCenter, botRightFromCenter, topRightFromCenter*/);

        final AnimatorSet mainAnimator = new AnimatorSet();
        mainAnimator.playSequentially(bigAlphaAnimator, moveToCenterAnimator, littleAlphaAnimator);
       /* mainAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mainAnimator.start();
            }
        });*/
        mainAnimator.start();
    }

    private ValueAnimator moveDown(final int i){
        final int y = points.get(i).getPosY();
        System.out.println("y : " + y);
        ValueAnimator moveDownAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getHeight() / 2);
        moveDownAnimator.setDuration(500);
        moveDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosY(y + (int)valueAnimator.getAnimatedValue());
                CustomView.this.invalidate();
            }
        });
        return moveDownAnimator;
    }

    private ValueAnimator moveUp(final int i){
        final int y = points.get(i).getPosY();
        System.out.println("y : " + y);
        ValueAnimator moveDownAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getHeight() / 2);
        moveDownAnimator.setDuration(500);
        moveDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosY(y - (int)valueAnimator.getAnimatedValue());
                CustomView.this.invalidate();
            }
        });
        return moveDownAnimator;
    }

    private ValueAnimator moveRight(final int i){
        final int x = points.get(i).getPosX();
        System.out.println("x : " + x);
        ValueAnimator moveRightAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getWidth() / 2);
        moveRightAnimator.setDuration(500);
        moveRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosX((x + (int)valueAnimator.getAnimatedValue()));
                CustomView.this.invalidate();
            }
        });
        return moveRightAnimator;
    }

    private ValueAnimator moveLeft(final int i){
        final int x = points.get(i).getPosX();
        System.out.println("x : " + x);
        ValueAnimator moveRightAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getWidth() / 2);
        moveRightAnimator.setDuration(500);
        moveRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosX((x - (int)valueAnimator.getAnimatedValue()));
                CustomView.this.invalidate();
            }
        });
        return moveRightAnimator;
    }

    private AnimatorSet moveTopLeftToCenter(){
        ValueAnimator moveDownAnimator = moveDown(0);
        ValueAnimator moveRightAnimator = moveRight(0);
        moveRightAnimator.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveDownAnimator, moveRightAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotLeftToCenter(){
        ValueAnimator moveRightAnimator = moveRight(2);
        ValueAnimator moveUpAnimator = moveUp(2);
        moveUpAnimator.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveRightAnimator, moveUpAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotRightToCenter(){
        ValueAnimator moveUpAnimator = moveUp(3);
        ValueAnimator moveLeftAnimator = moveLeft(3);
        moveLeftAnimator.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveUpAnimator, moveLeftAnimator);
        return animatorSet;
    }

    private AnimatorSet moveTopRightToCenter(){
        ValueAnimator moveLeft = moveLeft(1);
        ValueAnimator moveDown = moveDown(1);
        moveDown.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveLeft, moveDown);
        return animatorSet;
    }

    private AnimatorSet moveTopLeftFromCenter(){
        ValueAnimator moveUpAnimator = moveUp(0);
        ValueAnimator moveLeftAnimator = moveLeft(0);
        moveUpAnimator.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveLeftAnimator, moveUpAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotLeftFromCenter(){
        ValueAnimator moveDownAnimator = moveDown(2);
        ValueAnimator moveRightAnimator = moveRight(2);
        moveDownAnimator.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveRightAnimator, moveDownAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotRightFromCenter(){
        ValueAnimator moveLeft = moveLeft(3);
        ValueAnimator moveDown = moveDown(3);
        moveLeft.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveDown, moveLeft);
        return animatorSet;
    }

    private AnimatorSet moveTopRightFromCenter(){
        ValueAnimator moveRightAnimator = moveRight(1);
        ValueAnimator moveUpAnimator = moveUp(1);
        moveRightAnimator.setStartDelay(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveUpAnimator, moveRightAnimator);
        return animatorSet;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < points.size() - 1; i++){
            for (int j = i + 1; j < points.size(); j++){
                Point p1 = points.get(i);
                Point p2 = points.get(j);
                canvas.drawLine(p1.getPosX(), p1.getPosY(), p2.getPosX(), p2.getPosY(), paintLine);
            }
        }

        for (Point point : points){
            canvas.drawBitmap(littleBitmapIcon, point.getPosX() - littleBitmapIcon.getWidth() / 2, point.getPosY() - littleBitmapIcon.getHeight() / 2, littlePaint);
        }

        canvas.drawBitmap(bigBitmapIcon, width / 2 - bigBitmapIcon.getWidth() / 2, height / 2 - bigBitmapIcon.getHeight() / 2, bigPaint);
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
        int bigRadius = Math.min(height, width) / 6;

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

        littleBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, littleRadius * 2, littleRadius * 2
                , false);
        bigBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, bigRadius * 2, bigRadius * 2, false);

        initAnimation();
    }
}
