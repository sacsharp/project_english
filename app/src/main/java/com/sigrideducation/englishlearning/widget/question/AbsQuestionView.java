package com.sigrideducation.englishlearning.widget.question;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Property;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sigrideducation.englishlearning.GlobalApplication;
import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.activity.QuestionActivity;
import com.sigrideducation.englishlearning.helper.ApiLevelHelper;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.Question;
import com.sigrideducation.englishlearning.widget.fab.CheckableFab;

import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * This is the base class for displaying a Question
 */
public abstract class AbsQuestionView<Q extends Question> extends FrameLayout {

    private static final int ANSWER_HIDE_DELAY = 500;
    private static final int FOREGROUND_COLOR_CHANGE_DELAY = 750;
    protected final int mMinHeightTouchTarget;
    private final int mSpacingDouble;
    private final LayoutInflater mLayoutInflater;
    private final Lesson mLesson;
    private final Q mQuestion;
    private Interpolator mLinearOutSlowInInterpolator;
    private boolean mAnswered;
    private TextView mQuestionView;
    private CheckableFab mSubmitAnswer;
    private Handler mHandler;
    private Runnable mHideFabRunnable;
    private Runnable mMoveOffScreenRunnable;
    private InputMethodManager mInputMethodManager;
    private Context mContext;

    TourGuide mTourGuideHandler;
    /**
     * Enables creation of views for questionzes.
     *
     * @param context  The context for this view.
     * @param lesson The {@link Lesson} this view is running in.
     * @param question     The actual {@link Question} that is going to be displayed.
     */
    public AbsQuestionView(Context context, Lesson lesson, Q question) {
        super(context);
        mContext=context;
        mQuestion = question;
        mLesson = lesson;
        mSpacingDouble = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        mLayoutInflater = LayoutInflater.from(context);
        mSubmitAnswer = getSubmitButton();
        mMinHeightTouchTarget = getResources()
                .getDimensionPixelSize(R.dimen.min_height_touch_target);
        mLinearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
        mHandler = new Handler();
        mInputMethodManager = (InputMethodManager) context.getSystemService
                (Context.INPUT_METHOD_SERVICE);

        setId(question.getId());
        setUpQuestionView();
        LinearLayout container = createContainerLayout(context);
        View questionContentView = getInitializedContentView();
        addContentView(container, questionContentView);
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                addFloatingActionButton(R.id.question_view);
            }
        });
    }

    public AbsQuestionView(Context context, Lesson lesson, Q question, final int viewId) {
        super(context);
        mContext=context;
        mQuestion = question;
        mLesson = lesson;
        mSpacingDouble = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        mLayoutInflater = LayoutInflater.from(context);
        mSubmitAnswer = getSubmitButton();
        mMinHeightTouchTarget = getResources().getDimensionPixelSize(R.dimen.min_height_touch_target);
        mLinearOutSlowInInterpolator = new LinearOutSlowInInterpolator();
        mHandler = new Handler();
        mInputMethodManager = (InputMethodManager) context.getSystemService
                (Context.INPUT_METHOD_SERVICE);

        ScrollView scrollView = new ScrollView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        View questionContentView = getInitializedContentView();
        scrollView.addView(questionContentView);
        addView(scrollView, layoutParams);
        scrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                allowAnswer(true);
                return false;
            }
        });
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                addFloatingActionButton(viewId);
            }
        });
    }



    /**
     * Sets the behaviour for all question views.
     */
    private void setUpQuestionView() {
        mQuestionView = (TextView) mLayoutInflater.inflate(R.layout.question, this, false);
        mQuestionView.setBackgroundColor(ContextCompat.getColor(getContext(), mLesson.getTheme().getPrimaryColor()));
        mQuestionView.setText(getQuestion().getQuestion());
    }

    private LinearLayout createContainerLayout(Context context) {
        LinearLayout container = new LinearLayout(context);
        container.setId(R.id.absQuestionViewContainer);
        container.setOrientation(LinearLayout.VERTICAL);
        return container;
    }

    private View getInitializedContentView() {
        View questionContentView = createQuestionContentView();
        questionContentView.setId(R.id.question_content);
        questionContentView.setSaveEnabled(true);
        setDefaultPadding(questionContentView);
        if (questionContentView instanceof ViewGroup) {
            ((ViewGroup) questionContentView).setClipToPadding(false);
        }
        setMinHeightInternal(questionContentView, R.dimen.min_height_question);
        return questionContentView;
    }

    private void addContentView(LinearLayout container, View questionContentView) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        container.addView(mQuestionView, layoutParams);
        container.addView(questionContentView, layoutParams);
        addView(container, layoutParams);
    }

    private void addFloatingActionButton(int id) {
        final int fabSize = getResources().getDimensionPixelSize(R.dimen.size_fab);
        int bottomOfQuestionView = findViewById(id).getBottom();
        final LayoutParams fabLayoutParams = new LayoutParams(fabSize, fabSize,
                Gravity.END | Gravity.TOP);
        final int halfAFab = fabSize / 2;
        fabLayoutParams.setMargins(0, // left
                bottomOfQuestionView - halfAFab, //top
                0, // right
                mSpacingDouble); // bottom
        MarginLayoutParamsCompat.setMarginEnd(fabLayoutParams, mSpacingDouble);
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            // Account for the fab's emulated shadow.
            fabLayoutParams.topMargin -= (mSubmitAnswer.getPaddingTop() / 2);
        }
        addView(mSubmitAnswer, fabLayoutParams);
    }

    private CheckableFab  getSubmitButton() {
        if (null == mSubmitAnswer) {

            mSubmitAnswer = (CheckableFab) getLayoutInflater().inflate(R.layout.answer_submit, this, false);

            mSubmitAnswer.hide();
            mSubmitAnswer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitAnswer(v);
                    if(((GlobalApplication)((Activity) getContext()).getApplication()).isSubmitAnswerGuideShown())
                    {
                        if(null != mTourGuideHandler)
                        {
                            mTourGuideHandler.cleanUp();
                        }
                    }

                    if (mInputMethodManager.isAcceptingText()) {
                        mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
        }
        return mSubmitAnswer;
    }

    private void setDefaultPadding(View view) {
        view.setPadding(mSpacingDouble, mSpacingDouble, mSpacingDouble, mSpacingDouble);
    }

    protected LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    /**
     * Implementations should create the content view for the type of
     * Question they want to display.
     *
     * @return the created view to solve the question.
     */
    protected abstract View createQuestionContentView();

    /**
     * Implementations must make sure that the answer provided is evaluated and correctly rated.
     *
     * @return <code>true</code> if the question has been correctly answered, else
     * <code>false</code>.
     */
    protected abstract boolean isAnswerCorrect();

    /**
     * Save the user input to a bundle for orientation changes.
     *
     * @return The bundle containing the user's input.
     */
    public abstract Bundle getUserInput();

    /**
     * Restore the user's input.
     *
     * @param savedInput The input that the user made in a prior instance of this view.
     */
    public abstract void setUserInput(Bundle savedInput);

    public Q getQuestion() {
        return mQuestion;
    }

    protected boolean isAnswered() {
        return mAnswered;
    }

    /**
     * Sets the question to answered or unanswered.
     *
     * @param answered <code>true</code> if an answer was selected, else <code>false</code>.
     */
    protected void allowAnswer(final boolean answered) {
        if (null != mSubmitAnswer) {
            if (answered) {
                mSubmitAnswer.show();
                if(!((GlobalApplication)((Activity) getContext()).getApplication()).isSubmitAnswerGuideShown())
                {
                    mTourGuideHandler = TourGuide.init((Activity)mContext).with(TourGuide.Technique.Click)
                            .setPointer(new Pointer())
                            .setToolTip(new ToolTip().setTitle("Done??").setDescription("Click on this button to submit the answer!!"))
                            .playOn(mSubmitAnswer);
                    ((GlobalApplication) ((Activity) getContext()).getApplication()).setSubmitAnswerGuideShown(true);
                }

            } else {
                mSubmitAnswer.hide();
                mTourGuideHandler.cleanUp();
            }
            mAnswered = answered;
        }
    }

    /**
     * Sets the question to answered if it not already has been answered.
     * Otherwise does nothing.
     */
    protected void allowAnswer() {
        if (!isAnswered()) {
            allowAnswer(true);
        }
    }

    /**
     * Allows children to submit an answer via code.
     */
    protected void submitAnswer() {
        submitAnswer(findViewById(R.id.submitAnswer));
    }

    @SuppressWarnings("UnusedParameters")
    private void submitAnswer(final View v) {
        final boolean answerCorrect = isAnswerCorrect();
        mQuestion.setSolved(true);
        performScoreAnimation(answerCorrect);
    }

    /**
     * Animates the view nicely when the answer has been submitted.
     *
     * @param answerCorrect <code>true</code> if the answer was correct, else <code>false</code>.
     */
    private void performScoreAnimation(final boolean answerCorrect) {
        // Decide which background color to use.
        final int backgroundColor = ContextCompat.getColor(getContext(),
                answerCorrect ? R.color.green : R.color.red);
        adjustFab(answerCorrect, backgroundColor);
        resizeView();
        moveViewOffScreen(answerCorrect);
        // Animate the foreground color to match the background color.
        // This overlays all content within the current view.
    }

    private void adjustFab(boolean answerCorrect, int backgroundColor) {
        mSubmitAnswer.setChecked(answerCorrect);
        mSubmitAnswer.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        mHideFabRunnable = new Runnable() {
            @Override
            public void run() {
                mSubmitAnswer.hide();
            }
        };
        mHandler.postDelayed(mHideFabRunnable, ANSWER_HIDE_DELAY);
    }

    private void resizeView() {
        final float widthHeightRatio = (float) getHeight() / (float) getWidth();
        // Animate X and Y scaling separately to allow different start delays.
        // object animators for x and y with different durations and then run them independently
        resizeViewProperty(View.SCALE_X, .5f, 200);
        resizeViewProperty(View.SCALE_Y, .5f / widthHeightRatio, 250);
    }

    private void resizeViewProperty(Property<View, Float> property,
                                    float targetScale, int durationOffset) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, property,
                1f, targetScale);
        animator.setInterpolator(mLinearOutSlowInInterpolator);
        animator.setStartDelay(FOREGROUND_COLOR_CHANGE_DELAY + durationOffset);
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHideFabRunnable != null) {
            mHandler.removeCallbacks(mHideFabRunnable);
        }
        if (mMoveOffScreenRunnable != null) {
            mHandler.removeCallbacks(mMoveOffScreenRunnable);
        }
        super.onDetachedFromWindow();
    }


    private void moveViewOffScreen(final boolean answerCorrect) {
        // Move the current view off the screen.
        mMoveOffScreenRunnable = new Runnable() {
            @Override
            public void run() {
                mLesson.setScore(getQuestion(), answerCorrect);
                if (getContext() instanceof QuestionActivity) {
                    ((QuestionActivity) getContext()).proceed();
                }
            }
        };
        mHandler.postDelayed(mMoveOffScreenRunnable,
                FOREGROUND_COLOR_CHANGE_DELAY * 2);
    }

    private void setMinHeightInternal(View view, @DimenRes int resId) {
        view.setMinimumHeight(getResources().getDimensionPixelSize(resId));
    }
}
