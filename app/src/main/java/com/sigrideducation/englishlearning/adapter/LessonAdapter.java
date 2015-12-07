package com.sigrideducation.englishlearning.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.Theme;
import com.sigrideducation.englishlearning.persistence.ELDatabaseHelper;

import java.util.List;

/**
 * An adapter that allows display of {@link com.sigrideducation.englishlearning.model.Lesson} data.
 */
public class LessonAdapter extends BaseAdapter {

    public static final String DRAWABLE = "drawable";
    private static final String ICON_CATEGORY = "icon_category_";
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
     * Loads an icon that indicates that a lesson has already been solved.
     *
     * @param lesson The solved lesson to display.
     * @param categoryImageResource The lesson's identifying image.
     * @return The icon indicating that the lesson has been solved.
     */
    private LayerDrawable loadSolvedIcon(Lesson lesson, int categoryImageResource) {
        final Drawable done = loadTintedDoneDrawable();
        final Drawable categoryIcon = loadTintedCategoryDrawable(lesson, categoryImageResource);
        Drawable[] layers = new Drawable[]{categoryIcon, done}; // ordering is back to front
        return new LayerDrawable(layers);
    }

    /**
     * Loads and tints a drawable.
     *
     * @param lesson The lesson providing the tint color
     * @param categoryImageResource The image resource to tint
     * @return The tinted resource
     */
    private Drawable loadTintedCategoryDrawable(Lesson lesson, int categoryImageResource) {
        final Drawable categoryIcon = ContextCompat.getDrawable(mActivity, categoryImageResource);
        DrawableCompat.setTint(categoryIcon, getColor(lesson.getTheme().getPrimaryColor()));
        return categoryIcon;
    }

    /**
     * Loads and tints a check mark.
     *
     * @return The tinted check mark
     */
    private Drawable loadTintedDoneDrawable() {
        final Drawable done = ContextCompat.getDrawable(mActivity, R.drawable.ic_tick);
        DrawableCompat.setTint(done, getColor(android.R.color.white));
        return done;
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
