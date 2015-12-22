package com.sigrideducation.englishlearning.model;

import android.support.annotation.ColorRes;
import android.support.annotation.StyleRes;

import com.sigrideducation.englishlearning.R;

public enum Theme {
    blue(R.color.theme_blue_primary, R.color.theme_blue_primary_dark,
            R.color.theme_blue_background, R.style.AppTheme_Blue),
    green(R.color.theme_green_primary, R.color.theme_green_primary_dark,
            R.color.theme_green_background,R.style.AppTheme_Green),
    purple(R.color.theme_purple_primary, R.color.theme_purple_primary_dark,
            R.color.theme_purple_background, R.style.AppTheme_Purple),
    red(R.color.theme_red_primary, R.color.theme_red_primary_dark,
            R.color.theme_red_background, R.style.AppTheme_Red),
    yellow(R.color.theme_yellow_primary, R.color.theme_yellow_primary_dark,
            R.color.theme_yellow_background, R.style.AppTheme_Yellow);

    private final int mColorPrimaryId;
    private final int mWindowBackgroundColorId;
    private final int mColorPrimaryDarkId;
    private final int mStyleId;

    Theme(final int colorPrimaryId, final int colorPrimaryDarkId,
          final int windowBackgroundColorId, final int styleId) {
        mColorPrimaryId = colorPrimaryId;
        mWindowBackgroundColorId = windowBackgroundColorId;
        mColorPrimaryDarkId = colorPrimaryDarkId;
        mStyleId = styleId;
    }

    @ColorRes
    public int getWindowBackgroundColor() {
        return mWindowBackgroundColorId;
    }

    @ColorRes
    public int getPrimaryColor() {
        return mColorPrimaryId;
    }

    @ColorRes
    public int getPrimaryDarkColor() {
        return mColorPrimaryDarkId;
    }

    @StyleRes
    public int getStyleId() {
        return mStyleId;
    }
}
