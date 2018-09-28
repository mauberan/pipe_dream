package com.hit.project.pipedream;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

public abstract class PipeAnimation extends AnimationDrawable {

    Handler mAnimationHandler;

    public PipeAnimation(AnimationDrawable aniDrawable, int levelFlowTimePerFrame) {

        int i = 0;

        for (; i < aniDrawable.getNumberOfFrames(); i++) {

            this.addFrame(aniDrawable.getFrame(i), levelFlowTimePerFrame);

        }
        this.setOneShot(true);

    }

    @Override
    public void start() {
        super.start();

        mAnimationHandler = new Handler();
        mAnimationHandler.post(new Runnable() {
            @Override
            public void run() {
                onAnimationStart();
            }
        });
        mAnimationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onAnimationFinish();
            }
        }, getTotalDuration());

    }

    public int getTotalDuration() {

        int iDuration = 0;

        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            iDuration += this.getDuration(i);
        }

        return iDuration;
    }

    public abstract void onAnimationFinish();

    public abstract void onAnimationStart();

    public Drawable SkipAnimation() {
        return this.getFrame(getNumberOfFrames() - 1);
    }


}
