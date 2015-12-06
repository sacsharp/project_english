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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.adapter.QuestionAdapter;
import com.sigrideducation.englishlearning.adapter.ScoreAdapter;
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
    private TextView mProgressText;
    private int mQuestionSize;
    private ProgressBar mProgressBar;
    private Lesson mLesson;
    private AdapterViewAnimator mQuizView;
    private ScoreAdapter mScoreAdapter;
    private QuestionAdapter mQuestionAdapter;
    private SolvedStateListener mSolvedStateListener;

    public static QuestionFragment newInstance(String categoryId,
                                           SolvedStateListener solvedStateListener) {
        if (categoryId == null) {
            throw new IllegalArgumentException("The category can not be null");
        }
        Bundle args = new Bundle();
        args.putString(Lesson.TAG, categoryId);
        QuestionFragment fragment = new QuestionFragment();
        if (solvedStateListener != null) {
            fragment.mSolvedStateListener = solvedStateListener;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String categoryId = getArguments().getString(Lesson.TAG);
        mLesson = ELDatabaseHelper.getCategoryWith(getActivity(), categoryId);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Create a themed Context and custom LayoutInflater
        // to get nicely themed views in this Fragment.
        final Theme theme = mLesson.getTheme();
        final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),
                theme.getStyleId());
        final LayoutInflater themedInflater = LayoutInflater.from(context);
        return themedInflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mQuizView = (AdapterViewAnimator) view.findViewById(R.id.question_view);
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
        mQuizView.setInAnimation(getActivity(), R.animator.slide_in_bottom);
        mQuizView.setOutAnimation(getActivity(), R.animator.slide_out_top);
    }

    private void initProgressToolbar(View view) {
        final int firstUnsolvedQuizPosition = mLesson.getFirstUnsolvedQuizPosition();
        final List<Question> questions = mLesson.getQuizzes();
        mQuestionSize = questions.size();
        mProgressText = (TextView) view.findViewById(R.id.progress_text);
        mProgressBar = ((ProgressBar) view.findViewById(R.id.progress));
        mProgressBar.setMax(mQuestionSize);

        setProgress(firstUnsolvedQuizPosition);
    }

    private void setProgress(int currentQuizPosition) {
        if (!isAdded()) {
            return;
        }
        mProgressText.setText(getString(R.string.quiz_of_quizzes, currentQuizPosition, mQuestionSize));
        mProgressBar.setProgress(currentQuizPosition);
    }

    private void decideOnViewToDisplay() {
        final boolean isSolved = mLesson.isSolved();
        if (isSolved) {
            showSummary();
            if (null != mSolvedStateListener) {
                mSolvedStateListener.onCategorySolved();
            }
        } else {
            mQuizView.setAdapter(getQuestionAdapter());
            mQuizView.setSelection(mLesson.getFirstUnsolvedQuizPosition());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        View focusedChild = mQuizView.getFocusedChild();
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
        mQuizView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                mQuizView.removeOnLayoutChangeListener(this);
                View currentChild = mQuizView.getChildAt(0);
                if (currentChild instanceof ViewGroup) {
                    final View potentialQuizView = ((ViewGroup) currentChild).getChildAt(0);
                    if (potentialQuizView instanceof AbsQuestionView) {
                        ((AbsQuestionView) potentialQuizView).setUserInput(savedInstanceState.
                                getBundle(KEY_USER_INPUT));
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
        if (null == mQuizView) {
            return false;
        }
        int nextItem = mQuizView.getDisplayedChild() + 1;
        setProgress(nextItem);
        final int count = mQuizView.getAdapter().getCount();
        if (nextItem < count) {
            mQuizView.showNext();
            ELDatabaseHelper.updateLesson(getActivity(), mLesson);
            return true;
        }
        markCategorySolved();
        return false;
    }

    private void markCategorySolved() {
        mLesson.setSolved(true);
        ELDatabaseHelper.updateLesson(getActivity(), mLesson);
    }

    public void showSummary() {
        @SuppressWarnings("ConstantConditions")
        final ListView scorecardView = (ListView) getView().findViewById(R.id.scorecard);
        mScoreAdapter = getScoreAdapter();
        scorecardView.setAdapter(mScoreAdapter);
        scorecardView.setVisibility(View.VISIBLE);
        mQuizView.setVisibility(View.GONE);
    }

    private ScoreAdapter getScoreAdapter() {
        if (null == mScoreAdapter) {
            mScoreAdapter = new ScoreAdapter(mLesson);
        }
        return mScoreAdapter;
    }

    /**
     * Interface definition for a callback to be invoked when the quiz is started.
     */
    public interface SolvedStateListener {

        /**
         * This method will be invoked when the category has been solved.
         */
        void onCategorySolved();
    }
}