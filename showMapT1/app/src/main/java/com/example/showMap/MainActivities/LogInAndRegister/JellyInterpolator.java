package com.example.showMap.MainActivities.LogInAndRegister;

import android.view.animation.LinearInterpolator;

/**
 * Created by Freff on 2019/3/16.
 * 为了达到让画面更加动感的效果，这里使用了插值器
 */

public class JellyInterpolator extends LinearInterpolator {
    private float factor;

    public JellyInterpolator() {
        this.factor = 0.15f;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, -10 * input)
                * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
    }
}
