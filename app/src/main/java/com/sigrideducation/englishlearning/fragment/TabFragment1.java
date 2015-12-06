package com.sigrideducation.englishlearning.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.activity.QuestionActivity;
import com.sigrideducation.englishlearning.adapter.LessonAdapter;
import com.sigrideducation.englishlearning.helper.TransitionHelper;
import com.sigrideducation.englishlearning.model.Lesson;

/**
 * Created by Sachin on 12/6/2015.
 */
public class TabFragment1 extends Fragment {

    ListView mListLessons;
    private LessonAdapter mLessonAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpQuizList((ListView) view.findViewById(R.id.list_lessons));
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpQuizList(ListView lessonsView) {
        lessonsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = getActivity();
                startQuizActivityWithTransition(activity, view.findViewById(R.id.lesson_title),
                        mLessonAdapter.getItem(position));
            }
        });
        mLessonAdapter = new LessonAdapter(getActivity());
        lessonsView.setAdapter(mLessonAdapter);
    }

    @Override
    public void onResume() {
        mLessonAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void startQuizActivityWithTransition(Activity activity, View toolbar, Lesson lesson) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        ActivityCompat.startActivity(getActivity(),
                QuestionActivity.getStartIntent(activity, lesson), transitionBundle);
    }
}
