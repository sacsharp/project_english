package com.sigrideducation.englishlearning.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.activity.QuestionActivity;
import com.sigrideducation.englishlearning.adapter.LessonsAdapter;

/**
 * Created by Sachin on 12/6/2015.
 */
public class TabFragment1 extends Fragment {

    private LessonsAdapter mLessonsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpLessionList((ListView) view.findViewById(R.id.list_lessons));
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpLessionList(ListView lessonsView) {
        lessonsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = getActivity();
                startActivity(QuestionActivity.getStartIntent(activity, mLessonsAdapter.getItem(position)));
            }
        });
        mLessonsAdapter = new LessonsAdapter(getActivity());
        lessonsView.setAdapter(mLessonsAdapter);
    }

    @Override
    public void onResume() {
        mLessonsAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
