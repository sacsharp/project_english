package com.sigrideducation.englishlearning.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.database.ELDatabaseHelper;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.Theme;

import java.util.List;

public class LessonAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;
    private List<Lesson> mLessons;

    public LessonAdapter(Activity activity) {
        mActivity = activity;
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
        updateLessions(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_lesson, parent, false);
            convertView.setTag(new LessonViewHolder((LinearLayout) convertView));
        }
        LessonViewHolder holder = (LessonViewHolder) convertView.getTag();
        Lesson lesson = getItem(position);
        Theme theme = lesson.getTheme();
        convertView.setBackgroundColor(getColor(theme.getWindowBackgroundColor()));
        holder.title.setText(lesson.getName());
        holder.title.setTextColor(Color.GRAY);
        return convertView;
    }

    @Override
    public int getCount() {
        return mLessons.size();
    }

    @Override
    public Lesson getItem(int position) {
        return mLessons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mLessons.get(position).getId().hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        updateLessions(mActivity);
    }

    private void updateLessions(Activity activity) {
        mLessons = ELDatabaseHelper.getLessons(activity, true);
    }
    
    private int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(mActivity, colorRes);
    }
}
