package com.sigrideducation.englishlearning.views;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.DragEvent;
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
import com.sigrideducation.englishlearning.widget.CheckableFab;

import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * This is the base class for displaying a Question
 */
public abstract class AbsQuestionView<Q extends Question> extends FrameLayout {

    private static final int ANSWER_HIDE_DELAY = 100;
    private static final int FOREGROUND_COLOR_CHANGE_DELAY = 250;
    protected final int mMinHeightTouchTarget;
    private final int mSpacingDouble;
    private final LayoutInflater mLayoutInflater;
    private final Lesson mLesson;
    private final Q mQuestion;
    private Interpolator mLinearOutSlowInInterpolator;
    private boolean mAnswered;
    private TextView mQuestionView;
    private CheckableFab mCheckAnswer;
    private CheckableFab mMoveToNext;
    private Handler mHandler;
    private Runnable mHideFabRunnable;
    private Runnable mMoveOffScreenRunnable;
    private InputMethodManager mInputMethodManager;
    private Context mContext;
    private TextView mTextAnswerStatus;

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
        mCheckAnswer = getCheckAnswerButton();
        mMoveToNext = getMoveToNextButton();
        mTextAnswerStatus = getAnswerStatusView();
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
                addFloatingActionButton(mCheckAnswer);
                addFloatingActionButton(mMoveToNext);
                addAnswerStatusText();
            }
        });
    }

    public AbsQuestionView(Context context, Lesson lesson, Q question, final boolean isScroll) {
        super(context);
        mContext=context;
        mQuestion = question;
        mLesson = lesson;
        mSpacingDouble = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        mLayoutInflater = LayoutInflater.from(context);
        mCheckAnswer = getCheckAnswerButton();
        mMoveToNext = getMoveToNextButton();
        mTextAnswerStatus = getAnswerStatusView();
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
                allowCheckAnswer(true);
                return false;
            }
        });
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                addFloatingActionButton(mCheckAnswer);
                addFloatingActionButton(mMoveToNext);
                addAnswerStatusText();
            }
        });
    }



    /**
     * Sets the behaviour for all question views.
     */
    private void setUpQuestionView() {
        mQuestionView = (TextView) mLayoutInflater.inflate(R.layout.question, this, false);
        mQuestionView.setTextColor(Color.BLACK);
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

    private void addFloatingActionButton(CheckableFab fab) {
        final int fabSize = getResources().getDimensionPixelSize(R.dimen.size_fab);
        final LayoutParams fabLayoutParams = new LayoutParams(fabSize, fabSize, Gravity.CENTER | Gravity.BOTTOM);
        fabLayoutParams.setMargins(0, // left
                0, //top
                0, // right
                mSpacingDouble); // bottom
        MarginLayoutParamsCompat.setMarginEnd(fabLayoutParams, mSpacingDouble);
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            // Account for the fab's emulated shadow.
            fabLayoutParams.topMargin -= (fab.getPaddingTop() / 2);
        }
        addView(fab, fabLayoutParams);
    }

    private void addAnswerStatusText() {
        final LayoutParams textLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        addView(mTextAnswerStatus, textLayoutParams);
    }

    private CheckableFab  getCheckAnswerButton() {
        if (null == mCheckAnswer) {

            mCheckAnswer = (CheckableFab) getLayoutInflater().inflate(R.layout.answer_submit, this, false);
            mCheckAnswer.hide();
            mCheckAnswer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer(v);
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
        return mCheckAnswer;
    }

    private CheckableFab  getMoveToNextButton() {
        if (null == mMoveToNext) {

            mMoveToNext = (CheckableFab) getLayoutInflater().inflate(R.layout.move_to_next, this, false);
            mMoveToNext.setImageResource(R.drawable.ic_arrow_forward_black_48dp);
            mMoveToNext.hide();
            mMoveToNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToText(v);
                    if (((GlobalApplication) ((Activity) getContext()).getApplication()).isSubmitAnswerGuideShown()) {
                        if (null != mTourGuideHandler) {
                            mTourGuideHandler.cleanUp();
                        }
                    }

                    if (mInputMethodManager.isAcceptingText()) {
                        mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
        }
        return mMoveToNext;
    }

    private TextView  getAnswerStatusView() {
        if (null == mTextAnswerStatus) {

            mTextAnswerStatus = new TextView(getContext());
            mTextAnswerStatus.setElevation(4);
            mTextAnswerStatus.setPadding(10,10,10,10);
            mTextAnswerStatus.setTextColor(ContextCompat.getColor(getContext(),R.color.White));
            mTextAnswerStatus.setBackgroundColor(ContextCompat.getColor(getContext(),mLesson.getTheme().getPrimaryColor()));
            mTextAnswerStatus.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        ClipData data = ClipData.newPlainText("", "");
                        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        //start dragging the item touched
                        v.startDrag(data, shadowBuilder, v, 0);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            getRootView().setOnDragListener(new OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            //no action necessary
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            //no action necessary
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            //no action necessary
                            break;
                        case DragEvent.ACTION_DROP:
                            View view = (View) event.getLocalState();
                            view.setVisibility(VISIBLE);

                            float x = event.getX();
                            float y = event.getY();
                            final LayoutParams textLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            textLayoutParams.setMargins(0, (int) y, 0, 0);
                            mTextAnswerStatus.setLayoutParams(textLayoutParams);

                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            mTextAnswerStatus.setVisibility(GONE);
        }
        return mTextAnswerStatus;
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
    protected void allowCheckAnswer(final boolean answered) {
        if (null != mCheckAnswer) {
            if (answered) {
                mCheckAnswer.show();
                if(!((GlobalApplication)((Activity) getContext()).getApplication()).isSubmitAnswerGuideShown())
                {
                    mTourGuideHandler = TourGuide.init((Activity)mContext).with(TourGuide.Technique.Click)
                            .setPointer(new Pointer())
                            .setToolTip(new ToolTip().setTitle("Done??").setGravity(Gravity.TOP).setDescription("Click on this button to submit the answer!!"))
                            .playOn(mCheckAnswer);
                    ((GlobalApplication) ((Activity) getContext()).getApplication()).setSubmitAnswerGuideShown(true);
                }

            } else {
                mCheckAnswer.hide();
                mTourGuideHandler.cleanUp();
            }
            mAnswered = answered;
        }
    }

    /**
     * Sets the question to answered if it not already has been answered.
     * Otherwise does nothing.
     */
    protected void allowCheckAnswer() {
        if (!isAnswered()) {
            allowCheckAnswer(true);
        }
    }

    /**
     * Allows children to submit an answer via code.
     */
    protected void checkAnswer() {
        checkAnswer(findViewById(R.id.checkAnswer));
    }

    @SuppressWarnings("UnusedParameters")
    private void checkAnswer(final View v) {
        final boolean answerCorrect = isAnswerCorrect();
        mQuestion.setSolved(true);
        mTextAnswerStatus.setVisibility(VISIBLE);
        mTextAnswerStatus.setTextSize(30);
        mTextAnswerStatus.setBackgroundColor(Color.GRAY);
        if(answerCorrect)
            mTextAnswerStatus.setText("Correct");
        else
            mTextAnswerStatus.setText("It's Wrong!!");

        mCheckAnswer.hide();
        mMoveToNext.show();
    }

    /**
     * Allows children to submit an answer via code.
     */
    protected void moveToText() {
        moveToText(findViewById(R.id.moveToNext));
    }

    @SuppressWarnings("UnusedParameters")
    private void moveToText(final View v) {
        final boolean answerCorrect = isAnswerCorrect();
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
        moveViewOffScreen(answerCorrect);
        // Animate the foreground color to match the background color.
        // This overlays all content within the current view.
    }

    private void adjustFab(boolean answerCorrect, int backgroundColor) {
        mCheckAnswer.setChecked(answerCorrect);
        mCheckAnswer.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        mHideFabRunnable = new Runnable() {
            @Override
            public void run() {
                mCheckAnswer.hide();
            }
        };
        mHandler.postDelayed(mHideFabRunnable, ANSWER_HIDE_DELAY);
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
                if (getContext() instanceof QuestionActivity) {
                    ((QuestionActivity) getContext()).proceed();
                }
            }
        };
        mHandler.postDelayed(mMoveOffScreenRunnable, FOREGROUND_COLOR_CHANGE_DELAY);
    }

    private void setMinHeightInternal(View view, @DimenRes int resId) {
        view.setMinimumHeight(getResources().getDimensionPixelSize(resId));
    }
}
