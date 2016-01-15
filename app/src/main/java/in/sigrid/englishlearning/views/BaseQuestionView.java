package in.sigrid.englishlearning.views;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
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

import in.sigrid.englishlearning.R;
import in.sigrid.englishlearning.activity.QuestionActivity;
import in.sigrid.englishlearning.model.JsonParts;
import in.sigrid.englishlearning.model.Lesson;
import in.sigrid.englishlearning.model.question.Question;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * This is the base class for displaying a Question
 */
public abstract class BaseQuestionView<Q extends Question> extends FrameLayout {

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
    private FloatingActionButton mCheckAnswer;
    private FloatingActionButton mMoveToNext;
    private Handler mHandler;
    private Runnable mHideFabRunnable;
    private Runnable mMoveOffScreenRunnable;
    private InputMethodManager mInputMethodManager;
    private Context mContext;
    private TextView mTextAnswerStatus;

    TourGuide mTourGuideHandler,mTourGuideHandler1;
    SharedPreferences sharedPreferences;

    public BaseQuestionView(Context context, Lesson lesson, Q question) {
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
                if(!mQuestion.getType().toString().equals(JsonParts.QuestionType.CONTENT_TIP))
                {
                    Log.i("Type", mQuestion.getType().toString());
                    addFloatingActionButton(mCheckAnswer);
                    addFloatingActionButton(mMoveToNext);
                    addAnswerStatusText();
                }
                else {
                    addFloatingActionButton(mMoveToNext);
                    mMoveToNext.setVisibility(VISIBLE);
                }

            }
        });
    }

    public BaseQuestionView(Context context, Lesson lesson, Q question, final boolean isScroll) {
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
                if(!mQuestion.getType().toString().equals("CONTENT_TIP"))
                {
                    Log.i("Type", mQuestion.getType().toString());
                    addFloatingActionButton(mCheckAnswer);
                    addFloatingActionButton(mMoveToNext);
                    addAnswerStatusText();
                }
                else {
                    addFloatingActionButton(mMoveToNext);
                    mMoveToNext.setVisibility(VISIBLE);
                }
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
        return questionContentView;
    }

    private void addContentView(LinearLayout container, View questionContentView) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        container.addView(mQuestionView, layoutParams);
        container.addView(questionContentView, layoutParams);
        addView(container, layoutParams);
    }

    private void addFloatingActionButton(FloatingActionButton fab) {
        final int fabSize = getResources().getDimensionPixelSize(R.dimen.size_fab);
        final LayoutParams fabLayoutParams = new LayoutParams(fabSize, fabSize, Gravity.CENTER | Gravity.BOTTOM);
        fabLayoutParams.setMargins(0, // left
                mSpacingDouble, //top
                0, // right
                mSpacingDouble); // bottom
        MarginLayoutParamsCompat.setMarginEnd(fabLayoutParams, mSpacingDouble);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Account for the fab's emulated shadow.
            fabLayoutParams.topMargin -= (fab.getPaddingTop() / 2);
        }
        addView(fab, fabLayoutParams);
    }

    private void addAnswerStatusText() {
        final LayoutParams textLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        addView(mTextAnswerStatus, textLayoutParams);
    }

    private FloatingActionButton  getCheckAnswerButton() {
        if (null == mCheckAnswer) {
            mCheckAnswer = (FloatingActionButton) getLayoutInflater().inflate(R.layout.check_answer, this, false);
            mCheckAnswer.hide();
            mCheckAnswer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer();
                    sharedPreferences = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("CheckAnswerTipShown", true)) {
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
        return mCheckAnswer;
    }

    private FloatingActionButton  getMoveToNextButton() {
        if (null == mMoveToNext) {
            mMoveToNext = (FloatingActionButton) getLayoutInflater().inflate(R.layout.move_to_next, this, false);
            mMoveToNext.hide();
            mMoveToNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToNext();
                    sharedPreferences = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
                    if (sharedPreferences.getBoolean("MoveNextTipShown", true)) {
                        if (null != mTourGuideHandler1) {
                            mTourGuideHandler1.cleanUp();
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextAnswerStatus.setElevation(4);
            }
            mTextAnswerStatus.setPadding(10,10,10,10);
            mTextAnswerStatus.setTextColor(ContextCompat.getColor(getContext(),R.color.White));
            mTextAnswerStatus.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        ClipData data = ClipData.newPlainText("", "");
                        DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
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

    protected LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    //To implement the content view of a particular question type.
    protected abstract View createQuestionContentView();

    //To check whether answer is correct or not
    protected abstract boolean isAnswerCorrect();

    //To save the user input for orientation changes.
    public abstract Bundle getUserInput();

    //To restore user's input.
    public abstract void setUserInput(Bundle savedInput);

    public Q getQuestion() {
        return mQuestion;
    }

    protected boolean isAnswered() {
        return mAnswered;
    }

    //This allows the user to check the answer after he has selected an answer
    protected void allowCheckAnswer(final boolean answered) {
        if (null != mCheckAnswer) {
            if (answered) {
                mCheckAnswer.show();
                sharedPreferences = getContext().getSharedPreferences("MY_PREF",Context.MODE_PRIVATE);
                if(!sharedPreferences.getBoolean("CheckAnswerTipShown",false))
                {
                    mTourGuideHandler = TourGuide.init((Activity)mContext).with(TourGuide.Technique.Click)
                            .setPointer(new Pointer())
                            .setToolTip(new ToolTip().setTitle("Done??").setGravity(Gravity.TOP).setDescription("Click on this button to submit the answer!!"))
                            .playOn(mCheckAnswer);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("CheckAnswerTipShown",true);
                    editor.commit();
                }

            } else {
                mCheckAnswer.hide();
                if(mTourGuideHandler!=null)
                mTourGuideHandler.cleanUp();
            }
            mAnswered = answered;
        }
    }

    //To allow the user to check answer without doing anything
    protected void allowCheckAnswer() {
        if (!isAnswered()) {
            allowCheckAnswer(true);
        }
    }

    //This would check whether the user has given a correct answer on tapping checkAnswer button
    protected void checkAnswer() {
        final boolean answerCorrect = isAnswerCorrect();
        if(answerCorrect){
            Integer tempScore =Integer.parseInt(mLesson.getScore());
            Log.i("score",tempScore.toString());
            tempScore++;
            mLesson.setScore(tempScore.toString());
        }
        else {
            Log.i("score",mLesson.getScore());
        }
        mTextAnswerStatus.setVisibility(VISIBLE);
        mTextAnswerStatus.setTextSize(30);
        mTextAnswerStatus.setBackgroundColor(Color.GRAY);
        if(answerCorrect)
            mTextAnswerStatus.setText("Correct");
        else
            mTextAnswerStatus.setText("It's Wrong!!");

        mCheckAnswer.hide();
        sharedPreferences = getContext().getSharedPreferences("MY_PREF",Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("MoveNextTipShown", false))
        {
            mTourGuideHandler1 = TourGuide.init((Activity)mContext).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip().setTitle("Done??").setGravity(Gravity.TOP).setDescription("Click on this button to move to next question!!"))
                    .playOn(mMoveToNext);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("MoveNextTipShown",true);
            editor.commit();
        }
        mMoveToNext.show();
    }

    //To move to the new Lesson
    private void moveToNext() {
        if(mTourGuideHandler1!=null)
            mTourGuideHandler1.cleanUp();
        ((QuestionActivity) getContext()).proceed();
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
}
