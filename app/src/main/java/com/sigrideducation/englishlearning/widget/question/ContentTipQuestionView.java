package com.sigrideducation.englishlearning.widget.question;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.ContentTipQuestion;

@SuppressLint("ViewConstructor")
public class ContentTipQuestionView extends AbsQuestionView<ContentTipQuestion> {

    private static final String KEY_ANSWER = "ANSWER";

    private TextView mTextData;


    public ContentTipQuestionView(Context context, Lesson lesson, ContentTipQuestion question,boolean isScroll) {
        super(context, lesson, question,isScroll);
    }

    @Override
    protected View createQuestionContentView() {
        final Context context = getContext();
        LinearLayout container = (LinearLayout) getLayoutInflater().inflate(R.layout.question_content_tip, this, false);
        mTextData =(TextView) container.findViewById(R.id.text_data);

        container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                allowAnswer(true);
                return true;
            }
        });
        mTextData.setText(getQuestion().getAnswer());
        mTextData.setTextSize(30);
        return container;
    }



    @Override
    protected boolean isAnswerCorrect() {

        return true;
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ANSWER,"data");
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }

    }

}
