package com.example.bobyk.myapplication.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.bobyk.myapplication.models.Point;
import com.example.bobyk.myapplication.R;

import java.util.ArrayList;

/**
 * Created by bobyk on 29.07.16.
 */
public class CustomProgressBar extends View {

    private OnCustomBarClickListener onCustomBarClickListener;
    private OnCustomBarAnimationListener onCustomBarAnimationListener;
    private int color;
    int height = 0;
    int width = 0;
    private Paint bigPaint = new Paint();
    private Paint paintLine = new Paint();
    private Paint littlePaint = new Paint();
    private ValueAnimator bigAlphaAnimator;
    private ValueAnimator littleAlphaAnimator;
    private long duration;
    private Bitmap bitmapIcon;
    private ArrayList<Point> points;
    private Bitmap littleBitmapIcon;
    private Bitmap bigBitmapIcon;

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
    }

    public long getDuration() {
        return duration;
    }

    public void setBigBitmapIcon(Bitmap bigBitmapIcon) {
        this.bigBitmapIcon = bigBitmapIcon;
    }

    public Bitmap getBitmapIcon() {
        return bitmapIcon;
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar);

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
        bigPaint.setAlpha(0);
        littlePaint.setAlpha(255);
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
        bigAlphaAnimator = ValueAnimator.ofInt(0, 255);
        bigAlphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        bigAlphaAnimator.setRepeatCount(5);
        bigAlphaAnimator.setDuration(duration / 4 / 5);
        bigAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                bigPaint.setAlpha((int) valueAnimator.getAnimatedValue());
                CustomProgressBar.this.invalidate();
            }
        });

        littleAlphaAnimator = ValueAnimator.ofInt(255, 0);
        littleAlphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        littleAlphaAnimator.setRepeatCount(7);
        littleAlphaAnimator.setDuration(duration / 4 / 7);
        littleAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                littlePaint.setAlpha((int) valueAnimator.getAnimatedValue());
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

        AnimatorSet moveToCenterAnimator = new AnimatorSet();
        moveToCenterAnimator.playTogether(topLeftToCenter, botLeftToCenter, botRightToCenter, topRightToCenter);

        AnimatorSet topLeftFromCenter = moveTopLeftFromCenter();
        AnimatorSet botLeftFromCenter = moveBotLeftFromCenter();
        botLeftFromCenter.setStartDelay(duration / 4 / 9);
        AnimatorSet botRightFromCenter = moveBotRightFromCenter();
        botRightFromCenter.setStartDelay(duration / 4 / 6);
        AnimatorSet topRightFromCenter = moveTopRightFromCenter();
        topRightFromCenter.setStartDelay(duration / 4 / 3);

        AnimatorSet moveFromCenterAnimator = new AnimatorSet();
        moveFromCenterAnimator.playTogether(topLeftFromCenter, botLeftFromCenter, botRightFromCenter, topRightFromCenter);

        final AnimatorSet mainAnimator = new AnimatorSet();
        mainAnimator.playSequentially(bigAlphaAnimator, moveToCenterAnimator, littleAlphaAnimator, moveFromCenterAnimator);
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

    private ValueAnimator moveDown(final int i){
        ValueAnimator moveDownAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getHeight() / 2);
        moveDownAnimator.setDuration(duration / 4 / 3);
        moveDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int y = CustomProgressBar.this.points.get(i).getStartPosY();
                points.get(i).setPosY(y + (int)valueAnimator.getAnimatedValue());
               invalidate();
            }
        });
        moveDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
               points.get(i).setStartPosY(points.get(i).getStartPosY() + (Math.min(height, width) / 2 - littleBitmapIcon.getHeight() / 2));
            }
        });
        return moveDownAnimator;
    }

    private ValueAnimator moveUp(final int i){
        ValueAnimator moveUpAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getHeight() / 2);
        moveUpAnimator.setDuration(duration / 4 / 3);
        moveUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int y = points.get(i).getStartPosY();
                points.get(i).setPosY(y - (int)valueAnimator.getAnimatedValue());
                invalidate();
            }
        });
        moveUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                points.get(i).setStartPosY(points.get(i).getStartPosY() - (Math.min(height, width) / 2 - littleBitmapIcon.getHeight() / 2));
            }
        });
        return moveUpAnimator;
    }

    private ValueAnimator moveRight(final int i){
        ValueAnimator moveRightAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getWidth() / 2);
        moveRightAnimator.setDuration(duration / 4 / 3);
        moveRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = points.get(i).getStartPosX();
                points.get(i).setPosX((x + (int)valueAnimator.getAnimatedValue()));
                invalidate();
            }
        });
        moveRightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                points.get(i).setStartPosX(points.get(i).getStartPosX() + (Math.min(height, width) / 2 - littleBitmapIcon.getWidth() / 2));
            }
        });
        return moveRightAnimator;
    }

    private ValueAnimator moveLeft(final int i){
        ValueAnimator moveLeftAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - littleBitmapIcon.getWidth() / 2);
        moveLeftAnimator.setDuration(duration / 4 / 3);
        moveLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = points.get(i).getStartPosX();
                points.get(i).setPosX((x - (int)valueAnimator.getAnimatedValue()));
                CustomProgressBar.this.invalidate();
            }
        });

        moveLeftAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                points.get(i).setStartPosX(points.get(i).getStartPosX() - (Math.min(height, width) / 2 - littleBitmapIcon.getWidth() / 2));
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
        ValueAnimator moveLeft = moveLeft(1);
        ValueAnimator moveDown = moveDown(1);
        moveDown.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveLeft, moveDown);
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
        ValueAnimator moveRight = moveRight(3);
        ValueAnimator moveDown = moveDown(3);
        moveDown.setStartDelay(duration / 4 / 40);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveRight, moveDown);
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

        int littleRadius = Math.min(height, width) / 16;
        int bigRadius = Math.min(height, width) / 4;

        int diff = Math.abs(height - width) / 2;

        if (height >= width){
            points.add(new Point(littleRadius, littleRadius + diff, littleRadius, littleRadius + diff));
            points.add(new Point(width - littleRadius, littleRadius + diff, width - littleRadius, littleRadius + diff));
            points.add(new Point(littleRadius, height - littleRadius - diff, littleRadius, height - littleRadius - diff));
            points.add(new Point(width - littleRadius, height - littleRadius - diff, width - littleRadius, height - littleRadius - diff));
        }
        else {
            points.add(new Point(littleRadius + diff, littleRadius, littleRadius + diff, littleRadius));
            points.add(new Point(width - littleRadius - diff, littleRadius, width - littleRadius - diff, littleRadius));
            points.add(new Point(littleRadius + diff, height - littleRadius, littleRadius + diff, height - littleRadius));
            points.add(new Point(width - littleRadius - diff, height - littleRadius, width - littleRadius - diff, height - littleRadius));
        }

        littleBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, littleRadius * 2, littleRadius * 2
                , false);
        bigBitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, bigRadius * 2, bigRadius * 2, false);

        initAnimation();
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
}
