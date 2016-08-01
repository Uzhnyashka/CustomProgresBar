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
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.bobyk.myapplication.R;

import java.util.ArrayList;

/**
 * Created by bobyk on 29.07.16.
 */
public class CustomProgressBar extends View {

    private OnCustomBarClickListener onCustomBarClickListener;
    private OnCustomBarAnimationListener onCustomBarAnimationListener;
    int height = 0;
    int width = 0;
    private Paint largePaint = new Paint();
    private Paint paintLine = new Paint();
    private Paint smallPaint = new Paint();
    private long duration;
    private Bitmap bitmapIcon;
    private ArrayList<Point> points;
    private int smallSize;
    private int largeSize;
    ValueAnimator largeIconAlpha;
    ValueAnimator smallIconAlpha;
    AnimatorSet moveToCenterAnimator;
    AnimatorSet moveFromCenterAnimator;
    ObjectAnimator lar;

    AnimatorSet mainAnimator;

    public CustomProgressBar(Context context){
        super(context);
        init(context, null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defType){
        super(context, attrs, defType);
        init(context, attrs);
    }

    public void setDuration(long duration) {
        this.duration = duration;
        invalidate();
    }

    public long getDuration() {
        return duration;
    }

    public void setBitmapIcon(Bitmap bitmapIcon) {
        this.bitmapIcon = bitmapIcon;
    }

    public Bitmap getBitmapIcon() {
        return bitmapIcon;
    }

    public AnimatorSet getMainAnimator() {
        return CustomProgressBar.this.mainAnimator;
    }

    public void setMainAnimator(AnimatorSet mainAnimator) {
        this.mainAnimator = mainAnimator;
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar);

        int color;
        try {
            color = a.getColor(R.styleable.CustomProgressBar_barColor, 0xff000000);
            duration = a.getInt(R.styleable.CustomProgressBar_barDuration, 5000);
            Drawable icon = a.getDrawable(R.styleable.CustomProgressBar_barIcon);
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
        largePaint.setAlpha(0);
        smallPaint.setAlpha(255);
        paintLine.setColor(color);
        paintLine.setStrokeWidth(2);

        CustomProgressBar.this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (onCustomBarClickListener != null){
                    onCustomBarClickListener.onClick();
                }
                return false;
            }
        });
    }

    private void initAnimation(){

        /*lar = ObjectAnimator.ofFloat(largePaint, View.ALPHA, 0f, 1f);
        lar.setRepeatCount(5);
        lar.setDuration(duration / 4);
        lar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

            }
        });*/

        largeIconAlpha = ValueAnimator.ofInt(0, 255);
        largeIconAlpha.setRepeatMode(ValueAnimator.REVERSE);
        largeIconAlpha.setRepeatCount(5);
        largeIconAlpha.setDuration(duration / 4 / 5);
        largeIconAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                largePaint.setAlpha((int) valueAnimator.getAnimatedValue());
                CustomProgressBar.this.invalidate();
            }
        });

      /*  ObjectAnimator x = ObjectAnimator.ofInt(largePaint, "alpha", 0, 255);
        x.setDuration(1000);*/
       // System.out.println(largeIconAlpha.getDuration());

        smallIconAlpha = ValueAnimator.ofInt(255, 0);
        smallIconAlpha.setRepeatMode(ValueAnimator.REVERSE);
        smallIconAlpha.setRepeatCount(7);
        smallIconAlpha.setDuration(duration / 4 / 7);
        smallIconAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                smallPaint.setAlpha((int) valueAnimator.getAnimatedValue());
                CustomProgressBar.this.invalidate();
            }
        });


        AnimatorSet topLeftToCenter = moveTopLeftToCenter();
        AnimatorSet botLeftToCenter = moveBotLeftToCenter();
        botLeftToCenter.setStartDelay(duration / 4 / 9);
        AnimatorSet botRightToCenter = moveBotRightToCenter();
        botRightToCenter.setStartDelay(duration / 4 / 6);
        AnimatorSet topRightToCenter = moveTopRightToCenter();
        topRightToCenter.setStartDelay(duration / 4 / 3);

        moveToCenterAnimator = new AnimatorSet();
        moveToCenterAnimator.playTogether(topLeftToCenter, botLeftToCenter, botRightToCenter, topRightToCenter);

        AnimatorSet topLeftFromCenter = moveTopLeftFromCenter();
        AnimatorSet botLeftFromCenter = moveBotLeftFromCenter();
        botLeftFromCenter.setStartDelay(duration / 4 / 9);
        AnimatorSet botRightFromCenter = moveBotRightFromCenter();
        botRightFromCenter.setStartDelay(duration / 4 / 6);
        AnimatorSet topRightFromCenter = moveTopRightFromCenter();
        topRightFromCenter.setStartDelay(duration / 4 / 3);

        moveFromCenterAnimator = new AnimatorSet();
        moveFromCenterAnimator.playTogether(topLeftFromCenter, botLeftFromCenter, botRightFromCenter, topRightFromCenter);
    }

    private ValueAnimator moveDown(final int i){
        ValueAnimator moveDownAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallSize);
        moveDownAnimator.setDuration(duration / 4 / 3);
        moveDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosY(points.get(i).getStartPosY() + (int)valueAnimator.getAnimatedValue());
               invalidate();
            }
        });
        moveDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
               points.get(i).setStartPosY(points.get(i).getStartPosY() + (Math.min(height, width) / 2 - smallSize));
            }
        });
        return moveDownAnimator;
    }

    private ValueAnimator moveUp(final int i){

        ValueAnimator moveUpAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallSize);
        moveUpAnimator.setDuration(duration / 4 / 3);
        moveUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosY(points.get(i).getStartPosY() - (int)valueAnimator.getAnimatedValue());
                invalidate();
            }
        });
        moveUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                points.get(i).setStartPosY(points.get(i).getStartPosY() - (Math.min(height, width) / 2 - smallSize));
            }
        });
        return moveUpAnimator;
    }

    private ValueAnimator moveRight(final int i){
        ValueAnimator moveRightAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallSize);
        moveRightAnimator.setDuration(duration / 4 / 3);
        moveRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosX((points.get(i).getStartPosX() + (int)valueAnimator.getAnimatedValue()));
                invalidate();
            }
        });
        moveRightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                points.get(i).setStartPosX(points.get(i).getStartPosX() + (Math.min(height, width) / 2 - smallSize));
            }
        });
        return moveRightAnimator;
    }

    private ValueAnimator moveLeft(final int i){
        ValueAnimator moveLeftAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallSize);
        moveLeftAnimator.setDuration(duration / 4 / 3);
        moveLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosX((points.get(i).getStartPosX() - (int)valueAnimator.getAnimatedValue()));
                CustomProgressBar.this.invalidate();
            }
        });

        moveLeftAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                points.get(i).setStartPosX(points.get(i).getStartPosX() - (Math.min(height, width) / 2 - smallSize));
            }
        });
        return moveLeftAnimator;
    }

    private AnimatorSet moveTopLeftToCenter(){
        ValueAnimator moveDownAnimator = moveDown(0);
        ValueAnimator moveRightAnimator = moveRight(0);
        moveRightAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveDownAnimator, moveRightAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotLeftToCenter(){
        ValueAnimator moveRightAnimator = moveRight(2);
        ValueAnimator moveUpAnimator = moveUp(2);
        moveUpAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveRightAnimator, moveUpAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotRightToCenter(){
        ValueAnimator moveUpAnimator = moveUp(3);
        ValueAnimator moveLeftAnimator = moveLeft(3);
        moveLeftAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveUpAnimator, moveLeftAnimator);
        return animatorSet;
    }

    private AnimatorSet moveTopRightToCenter(){
        ValueAnimator moveLeftAnimator = moveLeft(1);
        ValueAnimator moveDownAnimator = moveDown(1);
        moveDownAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveLeftAnimator, moveDownAnimator);
        return animatorSet;
    }

    private AnimatorSet moveTopLeftFromCenter(){
        ValueAnimator moveUpAnimator = moveUp(0);
        ValueAnimator moveLeftAnimator = moveLeft(0);
        moveUpAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveLeftAnimator, moveUpAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotLeftFromCenter(){
        ValueAnimator moveDownAnimator = moveDown(2);
        ValueAnimator moveLeftAnimator = moveLeft(2);
        moveLeftAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveDownAnimator, moveLeftAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotRightFromCenter(){
        ValueAnimator moveRightAnimator = moveRight(3);
        ValueAnimator moveDownAnimator = moveDown(3);
        moveDownAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveRightAnimator, moveDownAnimator);
        return animatorSet;
    }

    private AnimatorSet moveTopRightFromCenter(){
        ValueAnimator moveRightAnimator = moveRight(1);
        ValueAnimator moveUpAnimator = moveUp(1);
        moveRightAnimator.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveUpAnimator, moveRightAnimator);
        return animatorSet;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d("DRAWTAG", "onDraw: ");
        for (int i = 0; i < points.size() - 1; i++){
            for (int j = i + 1; j < points.size(); j++){
                Point p1 = points.get(i);
                Point p2 = points.get(j);
                canvas.drawLine(p1.getPosX(), p1.getPosY(), p2.getPosX(), p2.getPosY(), paintLine);
            }
        }

        for (Point point : points){
            canvas.drawBitmap(getBitmapIconForSize(smallSize), point.getPosX() - smallSize, point.getPosY() - smallSize, smallPaint);
        }

        canvas.drawBitmap(getBitmapIconForSize(largeSize), width / 2 - largeSize, height / 2 - largeSize, largePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 0;
        int width = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d("TAG", "onMeasure: widthMode: " + widthMode + " heightMode: " + heightMode);

        switch (widthMode) {
        case MeasureSpec.EXACTLY:
            width = widthSize;
            height = heightSize;
            break;
        case MeasureSpec.AT_MOST:
            width = 50;
            height = 50;
            break;
        }

        Log.d("TAG", "onMeasure: w: " + width + " h: " + height);
        int squareSize = Math.min(height, width);
        setMeasuredDimension(squareSize, squareSize);
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(minWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("TAG", "onSizeChanged: w: " + w + " h: " + h + "old w : " + oldw + "old h : " + oldh);
        points = new ArrayList<>();
        height = h;
        width = w;

        smallSize = Math.min(height, width) / 16;
        largeSize = Math.min(height, width) / 4;

        points.add(new Point(smallSize, smallSize, smallSize, smallSize));
        points.add(new Point(width - smallSize, smallSize, width - smallSize, smallSize));
        points.add(new Point(smallSize, height - smallSize, smallSize, height - smallSize));
        points.add(new Point(width - smallSize, height - smallSize, width - smallSize, height - smallSize));


        /*smallBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, smallSize * 2, smallSize * 2
                , false);
        largeBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, largeSize * 2, largeSize * 2, false);*/

        initAnimation();
        mainAnimator = new AnimatorSet();
        mainAnimator.playSequentially(largeIconAlpha, moveToCenterAnimator, smallIconAlpha, moveFromCenterAnimator);
        mainAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onCustomBarAnimationListener != null){
                    onCustomBarAnimationListener.onAnimationEnd();
                }
                mainAnimator.start();
            }
        });
        mainAnimator.start();
    }

    private Bitmap getBitmapIconForSize(int size){
        return Bitmap.createScaledBitmap(bitmapIcon, size * 2, size * 2, false);
    }


    public interface OnCustomBarClickListener{
        void onClick();
    }

    public OnCustomBarClickListener getOnCustomBarClickListener() {
        return onCustomBarClickListener;
    }

    public void setOnCustomBarClickListener(OnCustomBarClickListener onCustomBarClickListener) {
        this.onCustomBarClickListener = onCustomBarClickListener;
    }

    public interface OnCustomBarAnimationListener{
        void onAnimationEnd();
    }

    public OnCustomBarAnimationListener getOnCustomBarAnimationListener() {
        return onCustomBarAnimationListener;
    }

    public void setOnCustomBarAnimationListener(OnCustomBarAnimationListener onCustomBarAnimationListener) {
        this.onCustomBarAnimationListener = onCustomBarAnimationListener;
    }

    public class Point {
        private int posX;
        private int posY;
        private int startPosX;
        private int startPosY;

        public Point(int posX, int posY, int startPosX, int startPosY){
            this.posX = posX;
            this.posY = posY;
            this.startPosX = startPosX;
            this.startPosY = startPosY;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }

        public int getStartPosX() {
            return startPosX;
        }

        public int getStartPosY() {
            return startPosY;
        }

        public void setPosX(int posX) {
            this.posX = posX;
        }

        public void setPosY(int posY) {
            this.posY = posY;
        }

        public void setStartPosX(int startPosX) {
            this.startPosX = startPosX;
        }

        public void setStartPosY(int startPosY) {
            this.startPosY = startPosY;
        }
    }

}
