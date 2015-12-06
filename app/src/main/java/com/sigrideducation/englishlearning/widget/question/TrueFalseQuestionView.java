package com.sigrideducation.englishlearning.widget.question;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.TrueFalseQuestion;

@SuppressLint("ViewConstructor")
public class TrueFalseQuestionView extends AbsQuestionView<TrueFalseQuestion> {

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

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.answer_true:
                        mAnswer = true;
                        break;
                    case R.id.answer_false:
                        mAnswer = false;
                        break;
                }
                allowAnswer();
            }
        };

        mAnswerTrue = container.findViewById(R.id.answer_true);
        mAnswerTrue.setOnClickListener(clickListener);
        mAnswerFalse = container.findViewById(R.id.answer_false);
        mAnswerFalse.setOnClickListener(clickListener);
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
