package com.sigrideducation.englishlearning.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;


public class LessonViewHolder {

    protected TextView title;
    protected ImageView icon;

    public LessonViewHolder(LinearLayout container) {
        icon = (ImageView) container.findViewById(R.id.lesson_icon);
        title = (TextView) container.findViewById(R.id.lesson_title);
    }
}
