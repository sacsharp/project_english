package com.sigrideducation.englishlearning.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.adapter.GameQuestionAdapter;

/**
 * Created by Sachin on 1/4/2016.
 */
public class FragmentGameMakeSentence extends Fragment {

    private FloatingActionButton mFabSubmit;
    private GameQuestionAdapter mGameQuestionAdapter;
    private AdapterViewAnimator mGameQuestionView;
    private TextView mTextScore;
    Handler handler=new Handler();
    private int mQuestionsToShow;
    private int score;

    public FragmentGameMakeSentence(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        mQuestionsToShow=bundle.getInt("questions",1);
        return inflater.inflate(R.layout.fragment_game_make_sentence, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mGameQuestionView = (AdapterViewAnimator) view.findViewById(R.id.question_view);
        mGameQuestionView.setAdapter(getQuestionAdapter());
        mTextScore=(TextView) view.findViewById(R.id.txt_score);
        mFabSubmit = (FloatingActionButton) view.findViewById(R.id.fab_submit);
        mFabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = mGameQuestionView.getDisplayedChild() + 1;
                final int count = mGameQuestionView.getAdapter().getCount();
                if (nextItem < count) {
                    mGameQuestionView.showNext();
                    mFabSubmit.setVisibility(View.INVISIBLE);
                }
                else {
                    mGameQuestionView.setVisibility(View.GONE);
                    mFabSubmit.setVisibility(View.GONE);
                    handler.removeCallbacks(r);
                    mTextScore.setText(""+count);
                    mTextScore.setTextSize(40);
                }

            }
        });
        handler.postDelayed(r, 3000);
        super.onViewCreated(view, savedInstanceState);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            showFab();
        }
    };

    private void showFab() {
        if(mGameQuestionAdapter.isAnswerCorrect())
            mFabSubmit.setVisibility(View.VISIBLE);
        else
            mFabSubmit.setVisibility(View.INVISIBLE);
        handler.postDelayed(r, 1000);

    }

    private GameQuestionAdapter getQuestionAdapter() {
        if (null == mGameQuestionAdapter) {
            switch (mQuestionsToShow){
                case 0:
                    mGameQuestionAdapter = new GameQuestionAdapter(getActivity());
                    break;
                default:
                    mGameQuestionAdapter = new GameQuestionAdapter(getActivity(),mQuestionsToShow);
            }
        }
        return mGameQuestionAdapter;
    }
}
