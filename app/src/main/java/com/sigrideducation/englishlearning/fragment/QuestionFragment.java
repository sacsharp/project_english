package com.sigrideducation.englishlearning.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.adapter.QuestionAdapter;
import com.sigrideducation.englishlearning.helper.ApiLevelHelper;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.Theme;
import com.sigrideducation.englishlearning.model.question.Question;
import com.sigrideducation.englishlearning.persistence.ELDatabaseHelper;
import com.sigrideducation.englishlearning.widget.question.AbsQuestionView;

import java.util.List;

/**
 * Encapsulates Question solving and displays it to the user.
 */
public class QuestionFragment extends android.support.v4.app.Fragment {

    private static final String KEY_USER_INPUT = "USER_INPUT";
    private int mQuestionSize;
    private ProgressBar mProgressBar;
    private Lesson mLesson;
    private AdapterViewAnimator mQuestionView;
    private QuestionAdapter mQuestionAdapter;
    private SolvedStateListener mSolvedStateListener;

    public static QuestionFragment newInstance(String lessonId, SolvedStateListener solvedStateListener) {
        if (lessonId == null) {
            throw new IllegalArgumentException("The lesson can not be null");
        }
        Bundle args = new Bundle();
        args.putString(Lesson.TAG, lessonId);
        QuestionFragment fragment = new QuestionFragment();
        if (solvedStateListener != null) {
            fragment.mSolvedStateListener = solvedStateListener;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String lessonId = getArguments().getString(Lesson.TAG);
        mLesson = ELDatabaseHelper.getCategoryWith(getActivity(), lessonId);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mQuestionView = (AdapterViewAnimator) view.findViewById(R.id.question_view);
        decideOnViewToDisplay();
        setQuizViewAnimations();
        initProgressToolbar(view);
        super.onViewCreated(view, savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setQuizViewAnimations() {
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        mQuestionView.setInAnimation(getActivity(), R.animator.slide_in_bottom);
        mQuestionView.setOutAnimation(getActivity(), R.animator.slide_out_top);
    }

    private void initProgressToolbar(View view) {
        final List<Question> questions = mLesson.getQuestions();
        mQuestionSize = questions.size();
        mProgressBar = ((ProgressBar) view.findViewById(R.id.progress));
        mProgressBar.setMax(mQuestionSize);
        mProgressBar.setProgress(0);
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
            showSummary();
            if (null != mSolvedStateListener) {
                mSolvedStateListener.onLessonSolved();
            }
        } else {
            mQuestionView.setAdapter(getQuestionAdapter());
            //mQuestionView.setSelection(mLesson.getFirstUnsolvedQuizPosition());
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
                                       int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
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
        return false;
    }

    private void markLessonAttempted() {
        mLesson.setSolved(true);
        ELDatabaseHelper.updateLesson(getActivity(), mLesson);
    }

    public void showSummary() {
        @SuppressWarnings("ConstantConditions")
        final TextView scorecardView = (TextView) getView().findViewById(R.id.txt_score);
        scorecardView.setText("Correct Answers:");
        scorecardView.setVisibility(View.VISIBLE);
        mQuestionView.setVisibility(View.GONE);
    }

    /**
     * Interface definition for a callback to be invoked when the quiz is started.
     */
    public interface SolvedStateListener {

        /**
         * This method will be invoked when the category has been solved.
         */
        void onLessonSolved();
    }
}