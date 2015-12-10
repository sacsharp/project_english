package com.sigrideducation.englishlearning.widget.question;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sigrideducation.englishlearning.GlobalApplication;
import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.TrueFalseQuestion;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

@SuppressLint("ViewConstructor")
public class TrueFalseQuestionView extends AbsQuestionView<TrueFalseQuestion> {

    public TourGuide mTutorialHandler;

    private static final String KEY_SELECTION = "SELECTION";
    private static final LinearLayout.LayoutParams LAYOUT_PARAMS =
            new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 1);

    static {
        LAYOUT_PARAMS.gravity = Gravity.CENTER;
    }

    private boolean mAnswer;
    private View mAnswerTrue;
    private View mAnswerFalse;

    public TrueFalseQuestionView(Context context, Lesson lesson, TrueFalseQuestion quiz) {
        super(context, lesson, quiz);
    }

    @Override
    protected View createQuestionContentView() {
        final ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.question_radio_group_true_false, this, false);

        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);

        if(!((GlobalApplication)((Activity) getContext()).getApplication()).isTrueGuideShown())
        mTutorialHandler = TourGuide.init((Activity)getContext()).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip()
                                .setTitle("Hey!")
                                .setDescription("Tap on me to answer the question TRUE!!")
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                );

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.answer_true:
                        mAnswer = true;
                        if(mTutorialHandler!=null)
                        {mTutorialHandler.cleanUp();
                        if(!((GlobalApplication)((Activity) getContext()).getApplication()).isFalseGuideShown())
                        {
                            mTutorialHandler.setToolTip(new ToolTip().setTitle("Hey there!").setDescription("Tap on me to answer the question FALSE!!").setGravity(Gravity.BOTTOM|Gravity.LEFT)).playOn(mAnswerFalse);
                            ((GlobalApplication) ((Activity) getContext()).getApplication()).setFalseGuideShown(true);

                        }}
                        break;
                    case R.id.answer_false:
                        mAnswer = false;
                        if(mTutorialHandler!=null)
                        mTutorialHandler.cleanUp();
                        break;
                }
                allowAnswer();
            }
        };

        mAnswerTrue = container.findViewById(R.id.answer_true);
        mAnswerTrue.setOnClickListener(clickListener);
        mAnswerFalse = container.findViewById(R.id.answer_false);
        mAnswerFalse.setOnClickListener(clickListener);

        if(mTutorialHandler != null && !((GlobalApplication)((Activity) getContext()).getApplication()).isTrueGuideShown())
        {
            mTutorialHandler.playOn(mAnswerTrue);
            ((GlobalApplication) ((Activity) getContext()).getApplication()).setTrueGuideShown(true);
        }


        return container;
    }

    @Override
    protected boolean isAnswerCorrect() {
        return getQuestion().isAnswerCorrect(mAnswer);
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SELECTION, mAnswer);
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }
        final boolean tmpAnswer = savedInput.getBoolean(KEY_SELECTION);
        performSelection(tmpAnswer ? mAnswerTrue : mAnswerFalse);
    }

    private void performSelection(View selection) {
        selection.performClick();
        selection.setSelected(true);
    }
}
