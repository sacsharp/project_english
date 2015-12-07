package com.sigrideducation.englishlearning.widget.question;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.activity.QuestionActivity;
import com.sigrideducation.englishlearning.adapter.OptionsQuestionAdapter;
import com.sigrideducation.englishlearning.helper.AnswerHelper;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.SelectItemQuestion;
import com.sigrideducation.englishlearning.model.question.SpeechInputQuestion;

import java.util.Locale;

@SuppressLint("ViewConstructor")
public class SpeechInputQuestionView extends AbsQuestionView<SpeechInputQuestion> {

    private static final String KEY_ANSWERS = "ANSWER";

    private TextView mTextToSpeak;
    private TextView mTextSpeechInput;
    private ImageButton mBtnSpeak;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    public SpeechInputQuestionView(Context context, Lesson lesson, SpeechInputQuestion question) {
        super(context, lesson, question);
    }

    @Override
    protected View createQuestionContentView() {
        final Context context = getContext();
        RelativeLayout container = (RelativeLayout) getLayoutInflater().inflate(R.layout.question_speech_input,this,false);

        mTextToSpeak = (TextView) container.findViewById(R.id.txt_to_speak);
        mTextSpeechInput = (TextView) container.findViewById(R.id.txt_speech_input);
        mBtnSpeak = (ImageButton) container.findViewById(R.id.btn_speak);

        mBtnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput(context);
            }
        });

        return container;
    }

    private void promptSpeechInput(Context context) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_prompt);
        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,R.string.speech_not_supported, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected boolean isAnswerCorrect() {

        return getQuestion().isAnswerCorrect(mTextSpeechInput.getText().toString());
    }


}
