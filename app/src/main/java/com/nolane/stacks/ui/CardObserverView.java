package com.nolane.stacks.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nolane.stacks.R;

public class CardObserverView extends FrameLayout {
    private boolean displayFront;
    private String front;
    private String back;

    private float cardX;

    private CardView one;
    private CardView two;
    private TextView textView;

    AnimatorSet animatorSet;

    float elevation;

    public CardObserverView(Context context) {
        this(context, null);
    }

    public CardObserverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        displayFront = true;
    }

    public void initView(@NonNull Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.view_card_observer, this);
        one = (CardView) getChildAt(0);
        textView = (TextView)((ViewGroup) one.getChildAt(0)).getChildAt(0);
        two = (CardView) getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            two.setX(cardX - getWidth());
            cardX = one.getX();
        }
    }

    public void setCard(@NonNull String front, @NonNull String back) {
        this.front = front;
        this.back = back;
        textView.setText(displayFront ? front : back);
    }

    public boolean isFlipped() {
        return !displayFront;
    }

    public void nextCard(@NonNull String front, @NonNull String back) {
        this.front = front;
        this.back = back;
        this.displayFront = true;

        if (null != animatorSet) animatorSet.cancel();
        animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AnticipateOvershootInterpolator());
        animatorSet.setDuration(700);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(one, "X", cardX, cardX + getWidth());
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(two, "X", cardX - getWidth(), cardX);
        animatorSet.playTogether(anim1, anim2);
        final View v1 = one;
        final View v2 = two;
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setX(cardX - getWidth());
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                v1.setX(cardX - getWidth());
                v2.setX(cardX);
            }
            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        animatorSet.start();

        CardView t = one;
        one = two;
        two = t;
        textView = (TextView)((ViewGroup) one.getChildAt(0)).getChildAt(0);
        textView.setText(front);
    }

    public void flip() {
        if (null != animatorSet) animatorSet.cancel();
        animatorSet = new AnimatorSet();

        elevation = one.getCardElevation();
        one.setCardElevation(0);
        float scale = getContext().getResources().getDisplayMetrics().density;
        one.setCameraDistance(2000 * scale);

        animatorSet.setDuration(500);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(one, "rotationX", 0.f, 90.f);
        anim1.setInterpolator(new AnticipateInterpolator());
        anim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                displayFront = !displayFront;
                textView.setText(displayFront ? front : back);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }
            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(one, "rotationX", -90.0f, 0.f);
        anim2.setInterpolator(new OvershootInterpolator());

        ObjectAnimator anim3 = ObjectAnimator.ofFloat(one, "scaleX", 1.f, 0.675f);
        anim3.setInterpolator(new LinearInterpolator());
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(one, "scaleY", 1.f, 0.675f);
        anim4.setInterpolator(new LinearInterpolator());

        ObjectAnimator anim5 = ObjectAnimator.ofFloat(one, "scaleX", 0.674f, 1.f);
        anim5.setInterpolator(new LinearInterpolator());
        ObjectAnimator anim6 = ObjectAnimator.ofFloat(one, "scaleY", 0.674f, 1.f);
        anim6.setInterpolator(new LinearInterpolator());


        animatorSet.play(anim1).with(anim3).with(anim4)
                .before(anim2).before(anim5).before(anim6);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                one.setCardElevation(elevation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                one.setCardElevation(elevation);
                one.setRotationX(0);
                one.setScaleX(1);
                one.setScaleY(1);
            }
            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        animatorSet.start();
    }
}
