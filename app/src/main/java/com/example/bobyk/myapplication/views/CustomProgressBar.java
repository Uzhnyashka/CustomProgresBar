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
import android.os.Handler;

/**
 * Created by bobyk on 29.07.16.
 */
public class CustomProgressBar extends View {

    final int topLeftNumber = 0;
    final int topRightNumber = 1;
    final int botLeftNumber = 2;
    final int botRightNumber = 3;

    final int countOfAnimationParts = 4;

    final int countRepeatLargeIconAlpha = 5;
    final int countRepeatSmallIconAlpha = 7;

    final int durationPartMoveSmallIcon = 3;
    final int durationPartBetweenMoves = 40;
    final int durationPartForSecondMove = 9;
    final int durationPartForThirdMove = 6;
    final int durationPartForFourthMove = 3;

    final int wrapContentSize = 50;

    private Runnable invalidateRunnable;
    private Handler handler = new Handler();
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
        largeIconAlpha.setRepeatCount(countRepeatLargeIconAlpha);
        largeIconAlpha.setDuration(duration / countOfAnimationParts / countRepeatLargeIconAlpha);
        largeIconAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                largePaint.setAlpha((int) valueAnimator.getAnimatedValue());
            }
        });

      /*  ObjectAnimator x = ObjectAnimator.ofInt(largePaint, "alpha", 0, 255);
        x.setDuration(1000);*/
       // System.out.println(largeIconAlpha.getDuration());

        smallIconAlpha = ValueAnimator.ofInt(255, 0);
        smallIconAlpha.setRepeatMode(ValueAnimator.REVERSE);
        smallIconAlpha.setRepeatCount(countRepeatSmallIconAlpha);
        smallIconAlpha.setDuration(duration / countOfAnimationParts / countRepeatSmallIconAlpha);
        smallIconAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                smallPaint.setAlpha((int) valueAnimator.getAnimatedValue());
            }
        });


        AnimatorSet topLeftToCenter = moveTopLeftToCenter();
        AnimatorSet botLeftToCenter = moveBotLeftToCenter();
        botLeftToCenter.setStartDelay(duration / countOfAnimationParts / durationPartForSecondMove);
        AnimatorSet botRightToCenter = moveBotRightToCenter();
        botRightToCenter.setStartDelay(duration / countOfAnimationParts / durationPartForThirdMove);
        AnimatorSet topRightToCenter = moveTopRightToCenter();
        topRightToCenter.setStartDelay(duration / countOfAnimationParts / durationPartForFourthMove);

        moveToCenterAnimator = new AnimatorSet();
        moveToCenterAnimator.playTogether(topLeftToCenter, botLeftToCenter, botRightToCenter, topRightToCenter);

        AnimatorSet topLeftFromCenter = moveTopLeftFromCenter();
        AnimatorSet botLeftFromCenter = moveBotLeftFromCenter();
        botLeftFromCenter.setStartDelay(duration / countOfAnimationParts / durationPartForSecondMove);
        AnimatorSet botRightFromCenter = moveBotRightFromCenter();
        botRightFromCenter.setStartDelay(duration / countOfAnimationParts / durationPartForThirdMove);
        AnimatorSet topRightFromCenter = moveTopRightFromCenter();
        topRightFromCenter.setStartDelay(duration / countOfAnimationParts / durationPartForFourthMove);

        moveFromCenterAnimator = new AnimatorSet();
        moveFromCenterAnimator.playTogether(topLeftFromCenter, botLeftFromCenter, botRightFromCenter, topRightFromCenter);

        invalidateRunnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
                handler.postDelayed(this, 20);
            }
        };
    }

    private ValueAnimator moveDown(final int i){
        ValueAnimator moveDownAnimator = ValueAnimator.ofInt(0, Math.min(height, width) / 2 - smallSize);
        moveDownAnimator.setDuration(duration / countOfAnimationParts / durationPartMoveSmallIcon);
        moveDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosY(points.get(i).getStartPosY() + (int)valueAnimator.getAnimatedValue());
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
        moveUpAnimator.setDuration(duration / countOfAnimationParts / durationPartMoveSmallIcon);
        moveUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosY(points.get(i).getStartPosY() - (int)valueAnimator.getAnimatedValue());
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
        moveRightAnimator.setDuration(duration / countOfAnimationParts / durationPartMoveSmallIcon);
        moveRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosX((points.get(i).getStartPosX() + (int)valueAnimator.getAnimatedValue()));
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
        moveLeftAnimator.setDuration(duration / countOfAnimationParts / durationPartMoveSmallIcon);
        moveLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                points.get(i).setPosX((points.get(i).getStartPosX() - (int)valueAnimator.getAnimatedValue()));
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
        ValueAnimator moveDownAnimator = moveDown(topLeftNumber);
        ValueAnimator moveRightAnimator = moveRight(topLeftNumber);
        moveRightAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveDownAnimator, moveRightAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotLeftToCenter(){
        ValueAnimator moveRightAnimator = moveRight(botLeftNumber);
        ValueAnimator moveUpAnimator = moveUp(botLeftNumber);
        moveUpAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveRightAnimator, moveUpAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotRightToCenter(){
        ValueAnimator moveUpAnimator = moveUp(botRightNumber);
        ValueAnimator moveLeftAnimator = moveLeft(botRightNumber);
        moveLeftAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveUpAnimator, moveLeftAnimator);
        return animatorSet;
    }

    private AnimatorSet moveTopRightToCenter(){
        ValueAnimator moveLeftAnimator = moveLeft(topRightNumber);
        ValueAnimator moveDownAnimator = moveDown(topRightNumber);
        moveDownAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveLeftAnimator, moveDownAnimator);
        return animatorSet;
    }

    private AnimatorSet moveTopLeftFromCenter(){
        ValueAnimator moveUpAnimator = moveUp(topLeftNumber);
        ValueAnimator moveLeftAnimator = moveLeft(topLeftNumber);
        moveUpAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveLeftAnimator, moveUpAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotLeftFromCenter(){
        ValueAnimator moveDownAnimator = moveDown(botLeftNumber);
        ValueAnimator moveLeftAnimator = moveLeft(botLeftNumber);
        moveLeftAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveDownAnimator, moveLeftAnimator);
        return animatorSet;
    }

    private AnimatorSet moveBotRightFromCenter(){
        ValueAnimator moveRightAnimator = moveRight(botRightNumber);
        ValueAnimator moveDownAnimator = moveDown(botRightNumber);
        moveDownAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveRightAnimator, moveDownAnimator);
        return animatorSet;
    }

    private AnimatorSet moveTopRightFromCenter(){
        ValueAnimator moveRightAnimator = moveRight(topRightNumber);
        ValueAnimator moveUpAnimator = moveUp(topRightNumber);
        moveRightAnimator.setStartDelay(duration / countOfAnimationParts / durationPartBetweenMoves);

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
            width = wrapContentSize;
            height = wrapContentSize;
            break;
        }

        Log.d("TAG", "onMeasure: w: " + width + " h: " + height);
        int squareSize = Math.min(height, width);
        setMeasuredDimension(squareSize, squareSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        points = new ArrayList<>();
        height = h;
        width = w;

        smallSize = Math.min(height, width) / 16;
        largeSize = Math.min(height, width) / 4;

        points.add(new Point(smallSize, smallSize, smallSize, smallSize));
        points.add(new Point(width - smallSize, smallSize, width - smallSize, smallSize));
        points.add(new Point(smallSize, height - smallSize, smallSize, height - smallSize));
        points.add(new Point(width - smallSize, height - smallSize, width - smallSize, height - smallSize));

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
        invalidateRunnable.run();
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
