package com.sigrideducation.englishlearning.adapter;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Sachin on 12/29/2015.
 */
public class MultipleChoiceQuestionAdapter extends BaseAdapter{

    private String[] mOptions;
    private int mLayoutId;

    public MultipleChoiceQuestionAdapter(String[] options, @LayoutRes int layoutId){
        mOptions = options;
        mLayoutId=layoutId;
    }

    @Override
    public int getCount() {
        return mOptions.length;
    }

    @Override
    public Object getItem(int position) {
        return mOptions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(mLayoutId, parent, false);
        }
        String text = getItem(position).toString();
        ((TextView) convertView).setText(text);
        return convertView;
    }
}
