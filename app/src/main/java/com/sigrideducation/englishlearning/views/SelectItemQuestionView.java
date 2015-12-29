package com.sigrideducation.englishlearning.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.adapter.OptionsQuestionAdapter;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.SelectItemQuestion;

@SuppressLint("ViewConstructor")
public class SelectItemQuestionView extends BaseQuestionView<SelectItemQuestion> {

    private static final String KEY_ANSWER = "ANSWER";

    private int mAnswer;
    private ListView mListView;

    public SelectItemQuestionView(Context context, Lesson lesson, SelectItemQuestion question) {
        super(context, lesson, question);
        mAnswer = getAnswer();
    }

    @Override
    protected View createQuestionContentView() {
        Context context = getContext();
        mListView = new ListView(context);
        mListView.setDivider(null);
        mListView.setSelector(R.drawable.selector_button);
        mListView.setAdapter(
                new OptionsQuestionAdapter(getQuestion().getOptions(), R.layout.item_answer_start, context, true));
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                allowCheckAnswer();
            }
        });
        return mListView;
    }

    @Override
    protected boolean isAnswerCorrect() {
        final Integer checkedItemPosition = mListView.getCheckedItemPosition();
        final int answer = getQuestion().getAnswer();
        return checkedItemPosition.equals(answer);
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ANSWER, mAnswer);
        return bundle;
    }

    @Override
     public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }
        mAnswer = savedInput.getInt(KEY_ANSWER);
        final ListAdapter adapter = mListView.getAdapter();
        mListView.performItemClick(mListView.getChildAt(mAnswer), mAnswer, adapter.getItemId(mAnswer));
    }

    private int getAnswer() {
        return mAnswer;
    }
}
