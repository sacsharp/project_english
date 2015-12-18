package com.sigrideducation.englishlearning.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.Theme;
import com.sigrideducation.englishlearning.database.ELDatabaseHelper;

import java.util.List;

/**
 * An adapter that allows display of {@link com.sigrideducation.englishlearning.model.Lesson} data.
 */
public class LessonAdapter extends BaseAdapter {

    public static final String DRAWABLE = "drawable";
    private final Resources mResources;
    private final String mPackageName;
    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;
    private List<Lesson> mLessons;

    public LessonAdapter(Activity activity) {
        mResources = activity.getResources();
        mActivity = activity;
        mPackageName = mActivity.getPackageName();
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
        updateCategories(activity);
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
        holder.title.setTextColor(getColor(theme.getTextPrimaryColor()));
        holder.title.setBackgroundColor(getColor(theme.getPrimaryColor()));
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
        updateCategories(mActivity);
    }

    private void updateCategories(Activity activity) {
        mLessons = ELDatabaseHelper.getLessons(activity, true);
    }


    /**
     * Convenience method for color loading.
     *
     * @param colorRes The resource id of the color to load.
     * @return The loaded color.
     */
    private int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(mActivity, colorRes);
    }
}
