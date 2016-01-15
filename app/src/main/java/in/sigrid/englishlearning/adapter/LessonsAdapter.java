package in.sigrid.englishlearning.adapter;

import android.app.Activity;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import in.sigrid.englishlearning.R;
import in.sigrid.englishlearning.database.ELDatabaseHelper;
import in.sigrid.englishlearning.model.Lesson;
import in.sigrid.englishlearning.model.Theme;

/**
 * Created by Sachin on 12/30/2015.
 */
public class LessonsAdapter extends BaseAdapter {

    private List<Lesson> mLessons;
    private Activity mActivity;
    public LessonsAdapter(Activity activity) {
        mLessons = ELDatabaseHelper.getLessons(activity, true);
        mActivity =activity;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.lesson_list_item, parent, false);
        }
        TextView mLessonListItem = (TextView)convertView.findViewById(R.id.lesson_title);
        Lesson lesson = getItem(position);
        Theme theme = lesson.getTheme();
        mLessonListItem.setText(lesson.getName());
        convertView.setBackgroundColor(getColor(theme.getWindowBackgroundColor()));
        return convertView;
    }

    private int getColor(@ColorRes int colorRes) {
        return mActivity.getResources().getColor(colorRes);
    }

    @Override
    public void notifyDataSetChanged() {
        mLessons = ELDatabaseHelper.getLessons(mActivity,true);
        super.notifyDataSetChanged();
    }
}
