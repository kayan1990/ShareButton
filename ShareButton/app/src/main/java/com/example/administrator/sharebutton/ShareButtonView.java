package com.example.administrator.sharebutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * Created by liuzheng on 2016/9/7.
 * Email: liuzheng123445@gmail.com
 */

public class ShareButtonView extends View {
    private static final String TAG = "ShareButtonView";
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF008CFF;

    private final int PADDING = dip2px(24);
    private final int ITEM_MARGIN = dip2px(30);
    private final int CLOSE_ICON_SIZE = dip2px(15);
    private final int BUTTON_MARGIN_BOTTOM = dip2px(20);
    private final int BUTTON_HEIGHT = dip2px(40);
    private final int BUTTON_ROUND_RADIUS = dip2px(20);
    private final int MIN_BACKGROUND_ROUND_RECT_RADIUS = dip2px(10);
    private final int MAX_BACKGROUND_ROUND_RECT_RADIUS = dip2px(30);

    private static final String SHOW_CONTENT = "SHOW_CONTENT";
    private static final String mTitleText = "SHARE";
    private static final String mButtonText = "COPY LINK";
    private String mStatus = "INIT";

    private int mWidth = 0;
    private int mHeight = 0;

    private Paint mBackgroundRectPaint;
    private Paint mCloseIconPaint;
    private Paint mTitleTextPaint;
    private Paint mRevealPaint;
    private Paint mButtonRectPaint;
    private Paint mButtonTextPaint;
    private Paint mContentItemOnePaint;
    private Paint mContentItemTwoPaint;
    private Paint mContentItemThreePaint;

    private Path mBackgroundRectPath;

    private int MIN_WIDTH = dip2px(160);
    private int MAX_WITH = dip2px(300);
    private int MIN_HEIGHT = dip2px(50);
    private int MAX_HEIGHT = dip2px(240);

    private int mBackgroundRectWidth;
    private int mBackgroundRectHeight;
    private int mBgRoundRectRadius;
    private float mTitleTextMoveDistance;

    private float mTitleTextWidth;
    private float mTitleTextHeight;

    private float mButtonTextWidth;
    private float mButtonTextHeight;
    private int mButtonWidth;

    private int mCloseIconRotateDegree;
    private int mContentTextMoveDistance;

    private int mRevealHeight;

    AnimatorSet mContentItemAlphaAnimSet;
    ValueAnimator mHeightAnim;
    ValueAnimator mRevealAnim;
    ValueAnimator mContentTextMoveAnim;
    ValueAnimator mButtonWidthAnim;
    ValueAnimator mButtonAlphaAnim;

    private RectF mBackgroundRectF = new RectF();
    private RectF mRevealClipRectF = new RectF();
    private RectF mRevealRectF = new RectF();

    public ShareButtonView(Context context) {
        this(context, null, 0);
    }

    public ShareButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initPaints();

        mTitleTextWidth = getTextWidth(mTitleText, mTitleTextPaint);
        mTitleTextHeight = getTextHeight(mTitleText, mTitleTextPaint);
        mButtonTextWidth = getTextWidth(mButtonText, mButtonTextPaint);
        mButtonTextHeight = getTextHeight(mButtonText, mButtonTextPaint);

        mBackgroundRectPath = new Path();
        mBackgroundRectPath.reset();

        initValues();
    }

    private void initValues() {
        mRevealHeight = MAX_HEIGHT;
        mButtonWidth = dip2px(50);

        mTitleTextMoveDistance = 0;
        mContentTextMoveDistance = dip2px(40);

        mBackgroundRectWidth = MIN_WIDTH;
        mBackgroundRectHeight = MIN_HEIGHT;
        mBgRoundRectRadius = MAX_BACKGROUND_ROUND_RECT_RADIUS;

        mCloseIconPaint.setAlpha(0);
        mButtonRectPaint.setAlpha(0);
        mButtonTextPaint.setAlpha(0);

        mContentItemOnePaint.setAlpha(0);
        mContentItemTwoPaint.setAlpha(0);
        mContentItemThreePaint.setAlpha(0);
    }

    private void initPaints() {
        mBackgroundRectPaint = new Paint();
        mBackgroundRectPaint.setColor(DEFAULT_BACKGROUND_COLOR);
        mBackgroundRectPaint.setAntiAlias(true);
        mBackgroundRectPaint.setStrokeWidth(5);

        mCloseIconPaint = new Paint();
        mCloseIconPaint.setColor(Color.WHITE);
        mCloseIconPaint.setAntiAlias(true);
        mCloseIconPaint.setStrokeWidth(2);

        mTitleTextPaint = new Paint();
        mTitleTextPaint.setColor(Color.WHITE);
        mTitleTextPaint.setTextSize(30);

        mButtonRectPaint = new Paint();
        mButtonRectPaint.setColor(Color.WHITE);
        mButtonRectPaint.setStyle(Paint.Style.STROKE);
        mButtonRectPaint.setStrokeWidth(3);

        mButtonTextPaint = new Paint();
        mButtonTextPaint.setTextSize(30);
        mButtonTextPaint.setColor(Color.WHITE);

        mContentItemOnePaint = new Paint();
        mContentItemOnePaint.setColor(Color.WHITE);
        mContentItemOnePaint.setTextSize(25);

        mContentItemTwoPaint = new Paint();
        mContentItemTwoPaint.setColor(Color.WHITE);
        mContentItemTwoPaint.setTextSize(25);

        mContentItemThreePaint = new Paint();
        mContentItemThreePaint.setColor(Color.WHITE);
        mContentItemThreePaint.setTextSize(25);

        mRevealPaint = new Paint();
        mRevealPaint.setColor(0xFF007AF3);
        mRevealPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int rectLeft = (mWidth - mBackgroundRectWidth) / 2;
        int rectRight = (mWidth + mBackgroundRectWidth) / 2;
        mBackgroundRectF.set(rectLeft, 0, rectRight, mBackgroundRectHeight);

        canvas.drawRoundRect(mBackgroundRectF, mBgRoundRectRadius, mBgRoundRectRadius, mBackgroundRectPaint);
        canvas.drawText(mTitleText, rectLeft + (mBackgroundRectWidth - mTitleTextWidth) / 2 - mTitleTextMoveDistance, (MIN_HEIGHT + mTitleTextHeight) / 2, mTitleTextPaint);
        drawCloseIcon(canvas, rectRight - PADDING - CLOSE_ICON_SIZE, MIN_HEIGHT / 2, CLOSE_ICON_SIZE, mCloseIconRotateDegree);

        if (TextUtils.equals(mStatus, SHOW_CONTENT)) {
            drawContentItem(canvas);
            int top = MAX_HEIGHT - BUTTON_MARGIN_BOTTOM - BUTTON_HEIGHT;
            drawRoundButton(canvas, top, mButtonWidth, BUTTON_HEIGHT, BUTTON_ROUND_RADIUS);
            canvas.drawText(mButtonText, (mWidth - mButtonTextWidth) / 2, top + (BUTTON_HEIGHT + mButtonTextHeight) / 2, mButtonTextPaint);
        }
    }

    private void drawContentItem(Canvas canvas) {
        mBackgroundRectPath.reset();
        mRevealClipRectF.set(0, 0, MAX_WITH, mBackgroundRectHeight);
        mBackgroundRectPath.addRoundRect(mRevealClipRectF, MIN_BACKGROUND_ROUND_RECT_RADIUS, MIN_BACKGROUND_ROUND_RECT_RADIUS, Path.Direction.CW);
        canvas.clipPath(mBackgroundRectPath);
        mRevealRectF.set(0, mRevealHeight, MAX_WITH, MAX_HEIGHT);
        canvas.drawRect(mRevealRectF, mRevealPaint);

        drawContentItemsText(canvas, "Facebook", "137", 1, mContentItemOnePaint);
        drawContentItemsText(canvas, "Twitter", "35", 2, mContentItemTwoPaint);
        drawContentItemsText(canvas, "Google+", "13", 3, mContentItemThreePaint);
    }

    private void drawContentItemsText(Canvas canvas, String type, String number, int itemIndex, Paint paint) {
        int itemMarginTop = itemIndex * ITEM_MARGIN + mContentTextMoveDistance;
        canvas.drawText(type, PADDING, itemMarginTop + MIN_HEIGHT, paint);
        canvas.drawText(number, MAX_WITH - PADDING - getTextWidth(number, paint), itemMarginTop + MIN_HEIGHT, paint);
    }

    void drawRoundButton(Canvas canvas, int top, int width, int height, int roundRaidus) {
        canvas.drawArc(new RectF((MAX_WITH - width) / 2 - roundRaidus, top, (MAX_WITH - width) / 2 + roundRaidus, height + top), 90, 180, false, mButtonRectPaint);
        canvas.drawLine((MAX_WITH - width) / 2, top, (MAX_WITH + width) / 2, top, mButtonRectPaint);
        canvas.drawArc(new RectF((MAX_WITH + width) / 2 - roundRaidus, top, (MAX_WITH + width) / 2 + roundRaidus, height + top), -90, 180, false, mButtonRectPaint);
        canvas.drawLine((MAX_WITH + width) / 2, top + height, (MAX_WITH - width) / 2, top + height, mButtonRectPaint);
    }

    void drawCloseIcon(Canvas canvas, int startX, int startY, int size, int rotateDegree) {
        canvas.save();
        canvas.rotate(rotateDegree, startX + size / 2, startY);
        canvas.rotate(45, startX + size / 2, startY);
        canvas.drawLine(startX, startY, startX + size, startY, mCloseIconPaint);
        canvas.rotate(90, startX + size / 2, startY);
        canvas.drawLine(startX, startY, startX + size, startY, mCloseIconPaint);
        canvas.restore();
    }

    private int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void startRevealAnim() {
        mStatus = SHOW_CONTENT;

        mContentItemAlphaAnimSet = new AnimatorSet();
        AnimatorSet revealAnimSet = new AnimatorSet();

        mHeightAnim = ValueAnimator.ofInt(MIN_HEIGHT, MAX_HEIGHT);
        mHeightAnim.setDuration(460);
        mHeightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundRectHeight = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mRevealAnim = ValueAnimator.ofInt(MAX_HEIGHT, MIN_HEIGHT);
        mRevealAnim.setDuration(460);
        mRevealAnim.setStartDelay(200);
        mRevealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mRevealAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRevealHeight = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator contentItemOneAlphaAnim = ValueAnimator.ofInt(0, 255);
        contentItemOneAlphaAnim.setDuration(500);
        contentItemOneAlphaAnim.setStartDelay(250);
        contentItemOneAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mContentItemOnePaint.setAlpha((int) animation.getAnimatedValue());
                invalidate();
            }
        });

        ValueAnimator contentItemTwoAlphaAnim = ValueAnimator.ofInt(0, 255);
        contentItemTwoAlphaAnim.setDuration(320);
        contentItemTwoAlphaAnim.setStartDelay(290);
        contentItemTwoAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mContentItemTwoPaint.setAlpha((int) animation.getAnimatedValue());
            }
        });

        ValueAnimator contentItemThreeAlphaAnim = ValueAnimator.ofInt(0, 255);
        contentItemThreeAlphaAnim.setDuration(200);
        contentItemThreeAlphaAnim.setStartDelay(350);
        contentItemThreeAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mContentItemThreePaint.setAlpha((int) animation.getAnimatedValue());
                invalidate();
            }
        });

        mContentTextMoveAnim = ValueAnimator.ofInt(dip2px(40), 0);
        mContentTextMoveAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mContentTextMoveAnim.setDuration(500);
        mContentTextMoveAnim.setStartDelay(300);
        mContentTextMoveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mContentTextMoveDistance = ((int) animation.getAnimatedValue());
            }
        });

        mButtonWidthAnim = ValueAnimator.ofInt(dip2px(50), MAX_WITH - 4 * PADDING);
        mButtonWidthAnim.setDuration(360);
        mButtonWidthAnim.setStartDelay(520);
        mButtonWidthAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mButtonWidthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mButtonWidth = ((int) animation.getAnimatedValue());
            }
        });

        mButtonAlphaAnim = ValueAnimator.ofInt(0, 255);
        mButtonAlphaAnim.setDuration(360);
        mButtonAlphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mButtonAlphaAnim.setStartDelay(560);
        mButtonAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int buttonAlpha = ((int) animation.getAnimatedValue());
                mButtonRectPaint.setAlpha(buttonAlpha);
                mButtonTextPaint.setAlpha(buttonAlpha);
                invalidate();
            }
        });

        revealAnimSet.playTogether(mHeightAnim, mRevealAnim, contentItemOneAlphaAnim, contentItemTwoAlphaAnim, contentItemThreeAlphaAnim, mContentTextMoveAnim, mButtonAlphaAnim, mButtonWidthAnim);
        revealAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnimSet.start();
    }

    public void reset() {
        mStatus = "init";
        initValues();
        invalidate();
    }

    public void startAnimation() {
        AnimatorSet backgroundAnimSet = new AnimatorSet();

        ValueAnimator widthAnim = ValueAnimator.ofInt(MIN_WIDTH, MAX_WITH);
        widthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundRectWidth = (int) animation.getAnimatedValue();
            }
        });

        ValueAnimator textMoveAnim = ValueAnimator.ofFloat(0, (MAX_WITH - mTitleTextWidth) / 2 - PADDING);
        textMoveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTitleTextMoveDistance = (float) animation.getAnimatedValue();
            }
        });

        ValueAnimator closeIconRotateAnim = ValueAnimator.ofInt(0, 180);
        closeIconRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCloseIconRotateDegree = (int) animation.getAnimatedValue();
            }
        });

        ValueAnimator closeIconPaintAlphaAnim = ValueAnimator.ofInt(0, 255);
        closeIconPaintAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int alpha = (int) animation.getAnimatedValue();
                mCloseIconPaint.setAlpha(alpha);
                invalidate();
            }
        });

        ValueAnimator roundRectRadiusAnim = ValueAnimator.ofInt(MAX_BACKGROUND_ROUND_RECT_RADIUS, MIN_BACKGROUND_ROUND_RECT_RADIUS);
        roundRectRadiusAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBgRoundRectRadius = (int) animation.getAnimatedValue();
            }
        });

        backgroundAnimSet.playTogether(widthAnim, textMoveAnim, closeIconRotateAnim, closeIconPaintAlphaAnim, roundRectRadiusAnim);
        backgroundAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startRevealAnim();
            }
        });
        backgroundAnimSet.setDuration(500);
        backgroundAnimSet.start();
    }

    private float getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height() / 1.1f;
    }

    private float getTextWidth(String text, Paint paint) {
        return paint.measureText(text);
    }
}
