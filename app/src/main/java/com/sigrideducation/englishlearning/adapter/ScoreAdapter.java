package com.sigrideducation.englishlearning.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.Question;

import java.util.List;

/**
 * Adapter for displaying score cards.
 */
public class ScoreAdapter extends BaseAdapter {

    private final Lesson mLesson;
    private final int count;
    private final List<Question> mQuestionList;

    private Drawable mSuccessIcon;
    private Drawable mFailedIcon;

    public ScoreAdapter(Lesson lesson) {
        mLesson = lesson;
        mQuestionList = mLesson.getQuizzes();
        count = mQuestionList.size();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Question getItem(int position) {
        return mQuestionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position > count || position < 0) {
            return AbsListView.INVALID_POSITION;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = createView(parent);
        }

        final Question question = getItem(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mQuestionView.setText(question.getQuestion());
        viewHolder.mAnswerView.setText(question.getStringAnswer());
        setSolvedStateForQuestion(viewHolder.mSolvedState, position);
        return convertView;
    }

    private void setSolvedStateForQuestion(ImageView solvedState, int position) {
        final Context context = solvedState.getContext();
        final Drawable tintedImage;
        if (mLesson.isSolvedCorrectly(getItem(position))) {
            tintedImage = getSuccessIcon(context);
        } else {
            tintedImage = getFailedIcon(context);
        }
        solvedState.setImageDrawable(tintedImage);
    }

    private Drawable getSuccessIcon(Context context) {
        if (null == mSuccessIcon) {
            mSuccessIcon = loadAndTint(context, R.drawable.ic_tick, R.color.theme_green_primary);
        }
        return mSuccessIcon;
    }

    private Drawable getFailedIcon(Context context) {
        if (null == mFailedIcon) {
            mFailedIcon = loadAndTint(context, R.drawable.ic_cross, R.color.theme_red_primary);
        }
        return mFailedIcon;
    }

    /**
     * Convenience method to aid tintint of vector drawables at runtime.
     *
     * @param context The {@link Context} for this app.
     * @param drawableId The id of the drawable to load.
     * @param tintColor The tint to apply.
     * @return The tinted drawable.
     */
    private Drawable loadAndTint(Context context, @DrawableRes int drawableId,
                                 @ColorRes int tintColor) {
        Drawable imageDrawable = ContextCompat.getDrawable(context, drawableId);
        if (imageDrawable == null) {
            throw new IllegalArgumentException("The drawable with id " + drawableId
                    + " does not exist");
        }
        DrawableCompat.setTint(imageDrawable, tintColor);
        return imageDrawable;
    }

    private View createView(ViewGroup parent) {
        View convertView;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup scorecardItem = (ViewGroup) inflater.inflate(
                R.layout.item_scorecard, parent, false);
        convertView = scorecardItem;
        ViewHolder holder = new ViewHolder(scorecardItem);
        convertView.setTag(holder);
        return convertView;
    }

    private class ViewHolder {

        final TextView mAnswerView;
        final TextView mQuestionView;
        final ImageView mSolvedState;

        public ViewHolder(ViewGroup scorecardItem) {
            mQuestionView = (TextView) scorecardItem.findViewById(R.id.question);
            mAnswerView = (TextView) scorecardItem.findViewById(R.id.answer);
            mSolvedState = (ImageView) scorecardItem.findViewById(R.id.solved_state);
        }

    }
}
