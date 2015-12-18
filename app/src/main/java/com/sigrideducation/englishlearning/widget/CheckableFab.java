package com.sigrideducation.englishlearning.widget;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.sigrideducation.englishlearning.R;

/**
 * A {@link FloatingActionButton} that implements {@link Checkable} to allow display of different
 * icons in it's states.
 */
public class CheckableFab extends FloatingActionButton implements Checkable {

    private static final int[] CHECKED = {android.R.attr.state_checked};

    private boolean mIsChecked = true;


    public CheckableFab(Context context) {
        this(context, null);
    }

    public CheckableFab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableFab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setImageResource(R.drawable.answer_quiz_fab);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(++extraSpace);
        if (mIsChecked) {
            mergeDrawableStates(drawableState, CHECKED);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsChecked == checked) {
            return;
        }
        mIsChecked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mIsChecked);
    }
}
