package com.sigrideducation.englishlearning.widget.question;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.MakeSentenceQuestion;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressLint("ViewConstructor")
public class MakeSentenceQuestionView extends AbsQuestionView<MakeSentenceQuestion> {

    private static final String KEY_ANSWER = "ANSWER";

    private FlowLayout mFlowLayoutParts,mFlowLayoutSentence;
    private String[] mQuestionParts;


    public MakeSentenceQuestionView(Context context, Lesson lesson, MakeSentenceQuestion question, boolean isScroll) {
        super(context, lesson, question,isScroll);
    }


    @Override
    protected View createQuestionContentView() {
        final Context context = getContext();

        mQuestionParts = getQuestion().getQuestion().split(" ");
        final LinearLayout container = (LinearLayout) getLayoutInflater().inflate(R.layout.question_make_sentence, this, false);
        mFlowLayoutParts = (FlowLayout) container.findViewById(R.id.flow_layout_parts);
        mFlowLayoutSentence = (FlowLayout) container.findViewById(R.id.flow_layout_sentence);

        shuffleArray(mQuestionParts);

        Button.OnClickListener onClick = new Button.OnClickListener() {

            Button.OnClickListener onClickSentence = new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(GONE);
                    mFlowLayoutParts.findViewById((int)v.getTag()).setVisibility(VISIBLE);

                    boolean anyViewVisible = false;
                    for(int i=0;i<mFlowLayoutSentence.getChildCount();i++)
                    {
                        if(mFlowLayoutSentence.getChildAt(i).getVisibility() == VISIBLE)
                            anyViewVisible=true;
                    }
                    if(anyViewVisible)
                        allowCheckAnswer(true);
                    else
                        allowCheckAnswer(false);
                }
            };

            @Override
            public void onClick(View v) {
                v.setVisibility(INVISIBLE);
                final Button buttonSentence = new Button(getContext());
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 10, 10);
                buttonSentence.setLayoutParams(params);
                buttonSentence.setText(v.getTag().toString());
                buttonSentence.setTag(v.getId());
                buttonSentence.setOnClickListener(onClickSentence);
                mFlowLayoutSentence.addView(buttonSentence);
                allowCheckAnswer(true);
            }
        };
        int id=1000;
        for(String part: mQuestionParts)
        {

            final Button buttonPart = new Button(getContext());
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,10,10);
            buttonPart.setLayoutParams(params);
            buttonPart.setText(part);
            buttonPart.setTag(part);
            buttonPart.setId(id++);
            buttonPart.setOnClickListener(onClick);
            mFlowLayoutParts.addView(buttonPart);
        }
        return container;
    }

    static void shuffleArray(String[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }


    @Override
    protected boolean isAnswerCorrect() {
        String answer = "";
        for(int i=0;i<mFlowLayoutSentence.getChildCount();i++)
        {
            Button btn = (Button) mFlowLayoutSentence.getChildAt(i);
            if(mFlowLayoutSentence.getChildAt(i).getVisibility() == VISIBLE)
                answer +=  btn.getText().toString()+" ";
        }
        answer = answer.trim();
        return answer.toLowerCase().equals(getQuestion().getAnswer().toLowerCase());
    }

    @Override
    public Bundle getUserInput() {
        String answer = "";
        for(int i=0;i<mFlowLayoutSentence.getChildCount();i++)
        {
            if(mFlowLayoutSentence.getChildAt(i).getVisibility() == VISIBLE)
                answer += mFlowLayoutSentence.getChildAt(i).toString()+ " ";
        }
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ANSWER,answer.trim());
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }

    }

}
