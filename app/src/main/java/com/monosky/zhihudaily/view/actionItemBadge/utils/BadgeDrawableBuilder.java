package com.monosky.zhihudaily.view.actionItemBadge.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;

import com.monosky.zhihudaily.R;

/**
 * Created by mikepenz on 02.07.15.
 */
public class BadgeDrawableBuilder {
    private int mColor = 0;
    private int mColorPressed = 0;

    public BadgeDrawableBuilder() {
    }

    public BadgeDrawableBuilder color(int color) {
        this.mColor = color;
        return this;
    }

    public BadgeDrawableBuilder colorPressed(int colorPressed) {
        this.mColorPressed = colorPressed;
        return this;
    }

    public StateListDrawable build(Context ctx) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        GradientDrawable normal = (GradientDrawable) UIUtil.getCompatDrawable(ctx, R.drawable.menu_badge_bg);
        GradientDrawable selected = (GradientDrawable) normal.getConstantState().newDrawable().mutate();

        normal.setColor(mColor);
        selected.setColor(mColorPressed);

        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, selected);
        stateListDrawable.addState(StateSet.WILD_CARD, normal);

        return stateListDrawable;
    }
}
