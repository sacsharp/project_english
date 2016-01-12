package com.sigrideducation.englishlearning.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.adapter.GameQuestionAdapter;
import com.sigrideducation.englishlearning.database.ELDatabaseHelper;

/**
 * Created by Sachin on 1/4/2016.
 */
public class FragmentGameMakeSentence extends Fragment {

    private FloatingActionButton mFabSubmit;
    private FloatingActionButton mFabSkip;
    private GameQuestionAdapter mGameQuestionAdapter;
    private AdapterViewAnimator mGameQuestionView;
    private ProgressBar mProgressBar;
    private TextView mTextScore;
    Handler handler=new Handler();
    private int mQuestionsToShow;
    private int mScore=0;
    private String mType;

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

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mGameQuestionView = (AdapterViewAnimator) view.findViewById(R.id.question_view);
        mGameQuestionView.setAdapter(getQuestionAdapter());
        mTextScore=(TextView) view.findViewById(R.id.txt_score);
        mFabSubmit = (FloatingActionButton) view.findViewById(R.id.fab_submit);
        mFabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScore++;
                int nextItem = mGameQuestionView.getDisplayedChild() + 1;
                final int count = mGameQuestionAdapter.getCount();
                if (nextItem < count) {
                    mGameQuestionView.showNext();
                    mFabSubmit.setVisibility(View.INVISIBLE);
                }
                else {
                    mGameQuestionView.setVisibility(View.GONE);
                    mFabSubmit.setVisibility(View.GONE);
                    handler.removeCallbacks(r);
                    mTextScore.setText("" + mScore);
                    mTextScore.setTextSize(40);
                    ELDatabaseHelper.fillScore(getActivity(),mType,mScore);
                }

            }
        });
        handler.postDelayed(r, 3000);

        mFabSkip= (FloatingActionButton) view.findViewById(R.id.fab_skip);
        mFabSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = mGameQuestionView.getDisplayedChild() + 1;
                final int count = mGameQuestionAdapter.getCount();
                if (nextItem < count) {
                    mGameQuestionView.showNext();
                    mFabSubmit.setVisibility(View.INVISIBLE);
                }
                else {
                    mGameQuestionView.setVisibility(View.GONE);
                    mFabSubmit.setVisibility(View.GONE);
                    handler.removeCallbacks(r);
                    mTextScore.setText("" + mScore);
                    mTextScore.setTextSize(40);
                    ELDatabaseHelper.fillScore(getActivity(),mType,mScore);
                }
            }
        });
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
                    mType="unlimited";
                    break;
                case 1:
                    mGameQuestionAdapter = new GameQuestionAdapter(getActivity());
                    mType = "time";
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setMax(120);
                    new CountDownTimer(120000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            mProgressBar.setProgress(120-(int)millisUntilFinished/1000);
                            Log.i("Time",""+(120-(int)millisUntilFinished/1000));
                        }

                        public void onFinish() {
                            mGameQuestionView.setVisibility(View.GONE);
                            mFabSubmit.setVisibility(View.GONE);
                            handler.removeCallbacks(r);
                            mTextScore.setText("Time's up!!"+mScore);
                            ELDatabaseHelper.fillScore(getActivity(), mType, mScore);
                        }
                    }.start();
                    break;
                default:
                    mGameQuestionAdapter = new GameQuestionAdapter(getActivity(),mQuestionsToShow);
                    mType="limited";
            }
        }
        return mGameQuestionAdapter;
    }
}
