package com.sigrideducation.englishlearning.fragment;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.activity.QuestionActivity;
import com.sigrideducation.englishlearning.adapter.QuestionAdapter;
import com.sigrideducation.englishlearning.database.ELDatabaseHelper;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.Theme;
import com.sigrideducation.englishlearning.model.question.Question;
import com.sigrideducation.englishlearning.views.AbsQuestionView;

import java.util.List;

/**
 * Encapsulates Question solving and displays it to the user.
 */
public class QuestionFragment extends android.support.v4.app.Fragment {

    private static final String KEY_USER_INPUT = "USER_INPUT";
    private int mQuestionSize;
    private ProgressBar mProgressBar;
    private Toolbar mProgressToolbar;
    private ImageButton mImgBtnClose;
    private Lesson mLesson;
    private AdapterViewAnimator mQuestionView;
    private QuestionAdapter mQuestionAdapter;
    private TextView mTextAnswerStatus;
    private TextView mScoreCard;
    private FloatingActionButton mRestartLesson;
    private FloatingActionButton mNextLesson;
    private String mLessonId;

    public static QuestionFragment newInstance(String lessonId) {
        if (lessonId == null) {
            throw new IllegalArgumentException("The lesson can not be null");
        }
        Bundle args = new Bundle();
        args.putString(Lesson.TAG, lessonId);
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String lessonId = getArguments().getString(Lesson.TAG);
        mLessonId = ""+(Integer.parseInt(lessonId)+1);
        Log.i("LessonId",mLessonId);
        mLesson = ELDatabaseHelper.getLessonWith(getActivity(), lessonId);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Create a themed Context and custom LayoutInflater
        // to get nicely themed views in this Fragment.
        final Theme theme = mLesson.getTheme();
        final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),theme.getStyleId());
        final LayoutInflater themedInflater = LayoutInflater.from(context);
        return themedInflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        mQuestionView = (AdapterViewAnimator) view.findViewById(R.id.question_view);
        mRestartLesson = (FloatingActionButton) view.findViewById(R.id.restart_lesson);
        mRestartLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Do you want to retake this test?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                resetLesson();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
        mRestartLesson.setVisibility(View.INVISIBLE);

        mNextLesson = (FloatingActionButton) view.findViewById(R.id.next_lesson);
        mNextLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(mLessonId)!=0){
                    startActivity(QuestionActivity.getStartIntent(getContext(), ELDatabaseHelper.getLessonWith(getContext(), mLessonId)));
                    getActivity().finish();
                }

            }
        });
        mNextLesson.setVisibility(View.INVISIBLE);

        decideOnViewToDisplay();
        setQuestionViewAnimations();
        initProgressToolbar(view);
        super.onViewCreated(view, savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setQuestionViewAnimations() {
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
            return;
        }
        mQuestionView.setInAnimation(getActivity(), R.animator.slide_in_bottom);
        mQuestionView.setOutAnimation(getActivity(), R.animator.slide_out_top);
    }

    private void initProgressToolbar(View view) {
        final List<Question> questions = mLesson.getQuestions();
        mQuestionSize = questions.size();
        mProgressToolbar =(Toolbar) view.findViewById(R.id.progress_toolbar);
        mProgressBar = ((ProgressBar) view.findViewById(R.id.progress));
        mImgBtnClose = (ImageButton) view.findViewById(R.id.img_btn_close);
        mScoreCard = (TextView) view.findViewById(R.id.txt_score);
        mProgressBar.setMax(mQuestionSize);
        mProgressBar.setProgress(0);

        mImgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setProgress(int currentQuestionPosition) {
        if (!isAdded()) {
            return;
        }
        mProgressBar.setProgress(currentQuestionPosition);
    }

    private void decideOnViewToDisplay() {
        final boolean isSolved = mLesson.isSolved();
        if (isSolved) {
            showSummary(mLesson.getId());
        } else {
            mQuestionView.setAdapter(getQuestionAdapter());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        View focusedChild = mQuestionView.getFocusedChild();
        if (focusedChild instanceof ViewGroup) {
            View currentView = ((ViewGroup) focusedChild).getChildAt(0);
            if (currentView instanceof AbsQuestionView) {
                outState.putBundle(KEY_USER_INPUT, ((AbsQuestionView) currentView).getUserInput());
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        restoreQuizState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    private void restoreQuizState(final Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            return;
        }
        mQuestionView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mQuestionView.removeOnLayoutChangeListener(this);
                View currentChild = mQuestionView.getChildAt(0);
                if (currentChild instanceof ViewGroup) {
                    final View potentialQuizView = ((ViewGroup) currentChild).getChildAt(0);
                    if (potentialQuizView instanceof AbsQuestionView) {
                        ((AbsQuestionView) potentialQuizView).setUserInput(savedInstanceState.getBundle(KEY_USER_INPUT));
                    }
                }
            }
        });
    }

    private QuestionAdapter getQuestionAdapter() {
        if (null == mQuestionAdapter) {
            mQuestionAdapter = new QuestionAdapter(getActivity(), mLesson);
        }
        return mQuestionAdapter;
    }

    /**
     * Displays the next page.
     *
     * @return <code>true</code> if there's another quiz to solve, else <code>false</code>.
     */
    public boolean showNextPage() {
        if (null == mQuestionView) {
            return false;
        }
        int nextItem = mQuestionView.getDisplayedChild() + 1;
        setProgress(nextItem);
        final int count = mQuestionView.getAdapter().getCount();
        if (nextItem < count) {
            mQuestionView.showNext();
            ELDatabaseHelper.updateLesson(getActivity(), mLesson);
            return true;
        }
        markLessonAttempted();
        showSummary(mLesson.getId());
        return false;
    }

    private void markLessonAttempted() {
        mLesson.setSolved(true);
        ELDatabaseHelper.updateLesson(getActivity(), mLesson);
    }

    public void showSummary(String lessonId) {
        if(mProgressToolbar != null)
            mProgressToolbar.setVisibility(View.GONE);
        else{
            mProgressToolbar = (Toolbar)getView().findViewById(R.id.progress_toolbar);
            mProgressToolbar.setVisibility(View.GONE);
        }

//        int score;
//        if(mQuestionAdapter !=null){
//            score = mQuestionAdapter.getScore();
//        }
//        else {
//            mQuestionAdapter = getQuestionAdapter();
//            score = mQuestionAdapter.getScore();
//        }
//        if(mScoreCard !=null)
//        mScoreCard.setText("Correct Answers:"+ score);
//        else{
//            mScoreCard = (TextView) getView().findViewById(R.id.txt_score);
//            mScoreCard.setText("Correct Answers:"+ score);
//        }
        mScoreCard.setVisibility(View.VISIBLE);
        mQuestionView.setVisibility(View.GONE);
        mRestartLesson.setVisibility(View.VISIBLE);
        mRestartLesson.setImageResource(R.drawable.ic_refresh_black_48dp);
        mNextLesson.setVisibility(View.VISIBLE);
        mNextLesson.setImageResource(R.drawable.ic_arrow_forward_black_48dp);
    }

    private void resetLesson()
    {
        mQuestionView.setAdapter(getQuestionAdapter());
        mQuestionView.setVisibility(View.VISIBLE);
        mProgressToolbar.setVisibility(View.VISIBLE);
        mScoreCard.setVisibility(View.INVISIBLE);
        mProgressBar.setProgress(0);
    }
}