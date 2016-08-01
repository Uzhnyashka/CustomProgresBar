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
    private Bitmap smallBitmapIcon;
    private Bitmap largeBitmapIcon;
    ValueAnimator largeIconAlpha;
    ValueAnimator smallIconAlpha;
    AnimatorSet moveToCenterAnimator;
    AnimatorSet moveFromCenterAnimator;

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

    public void setBigBitmapIcon(Bitmap bigBitmapIcon) {
        this.largeBitmapIcon = bigBitmapIcon;
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

        ObjectAnimator x = ObjectAnimator.ofInt(largePaint, "alpha", 0, 255);
        x.setDuration(1000);
        System.out.println(largeIconAlpha.getDuration());

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
        ValueAnimator moveDownAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallBitmapIcon.getHeight() / 2);
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
               points.get(i).setStartPosY(points.get(i).getStartPosY() + (Math.min(height, width) / 2 - smallBitmapIcon.getHeight() / 2));
            }
        });
        return moveDownAnimator;
    }

    private ValueAnimator moveUp(final int i){

        ValueAnimator moveUpAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallBitmapIcon.getHeight() / 2);
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
                points.get(i).setStartPosY(points.get(i).getStartPosY() - (Math.min(height, width) / 2 - smallBitmapIcon.getHeight() / 2));
            }
        });
        return moveUpAnimator;
    }

    private ValueAnimator moveRight(final int i){
        ValueAnimator moveRightAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallBitmapIcon.getWidth() / 2);
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
                points.get(i).setStartPosX(points.get(i).getStartPosX() + (Math.min(height, width) / 2 - smallBitmapIcon.getWidth() / 2));
            }
        });
        return moveRightAnimator;
    }

    private ValueAnimator moveLeft(final int i){
        ValueAnimator moveLeftAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallBitmapIcon.getWidth() / 2);
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
                points.get(i).setStartPosX(points.get(i).getStartPosX() - (Math.min(height, width) / 2 - smallBitmapIcon.getWidth() / 2));
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
            canvas.drawBitmap(smallBitmapIcon, point.getPosX() - smallBitmapIcon.getWidth() / 2, point.getPosY() - smallBitmapIcon.getHeight() / 2, smallPaint);
        }

        canvas.drawBitmap(largeBitmapIcon, width / 2 - largeBitmapIcon.getWidth() / 2, height / 2 - largeBitmapIcon.getHeight() / 2, largePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int height = getMeasuredHeight();
        final int width = getMeasuredWidth();

        int squareSize = Math.min(height, width);
        setMeasuredDimension(squareSize, squareSize);

        setMinimumHeight(100);
        setMinimumWidth(100);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
       // Log.d("TAG", "onSizeChanged: w: " + w + " h: " + h + "old w : " + oldw + "old h : " + oldh);
        points = new ArrayList<>();
        height = h;
        width = w;

        int littleRadius = Math.min(height, width) / 16;
        int bigRadius = Math.min(height, width) / 4;

        points.add(new Point(littleRadius, littleRadius, littleRadius, littleRadius));
        points.add(new Point(width - littleRadius, littleRadius, width - littleRadius, littleRadius));
        points.add(new Point(littleRadius, height - littleRadius, littleRadius, height - littleRadius));
        points.add(new Point(width - littleRadius, height - littleRadius, width - littleRadius, height - littleRadius));

        smallBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, littleRadius * 2, littleRadius * 2
                , false);
        largeBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, bigRadius * 2, bigRadius * 2, false);

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

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(minHeight);
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
