package com.sigrideducation.englishlearning.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.ContentTipQuestion;
import com.sigrideducation.englishlearning.model.question.FillBlankQuestion;
import com.sigrideducation.englishlearning.model.question.MakeSentenceQuestion;
import com.sigrideducation.englishlearning.model.question.Question;
import com.sigrideducation.englishlearning.model.question.MultipleChoiceQuestion;
import com.sigrideducation.englishlearning.model.question.SpeechInputQuestion;
import com.sigrideducation.englishlearning.views.BaseQuestionView;
import com.sigrideducation.englishlearning.views.ContentTipQuestionView;
import com.sigrideducation.englishlearning.views.FillBlankQuestionView;
import com.sigrideducation.englishlearning.views.MakeSentenceQuestionView;
import com.sigrideducation.englishlearning.views.MultipleChoiceQuestionView;
import com.sigrideducation.englishlearning.views.SpeechInputQuestionView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter to display quizzes.
 */
public class QuestionAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Question> mQuestions;
    private final Lesson mLesson;
    private final int mViewTypeCount;
    private int mScore=0;
    private List<String> mQuestionType;

    public QuestionAdapter(Context context, Lesson lesson) {
        mContext = context;
        mLesson = lesson;
        mQuestions = lesson.getQuestions();
        mViewTypeCount = calculateViewTypeCount();

    }

    private int calculateViewTypeCount() {
        Set<String> tmpTypes = new HashSet<>();
        for (int i = 0; i < mQuestions.size(); i++) {
            tmpTypes.add(mQuestions.get(i).getType().getJsonName());
        }
        mQuestionType = new ArrayList<>(tmpTypes);
        return mQuestionType.size();
    }

    @Override
    public int getCount() {
        return mQuestions.size();
    }

    @Override
    public Question getItem(int position) {
        return mQuestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mQuestions.get(position).getId();
    }

    @Override
    public int getViewTypeCount() {
        return mViewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        return mQuestionType.indexOf(getItem(position).getType().getJsonName());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Question question = getItem(position);
        if (convertView instanceof BaseQuestionView) {
            if (((BaseQuestionView) convertView).getQuestion().equals(question)) {
                return convertView;
            }
        }
        convertView = getViewInternal(question);
        return convertView;
    }

    private BaseQuestionView getViewInternal(Question question) {
        if (null == question) {
            throw new IllegalArgumentException("Question must not be null");
        }
        return createViewFor(question);
    }

    private BaseQuestionView createViewFor(Question question) {
        switch (question.getType()) {
            case FILL_BLANK:
                return new FillBlankQuestionView(mContext, mLesson, (FillBlankQuestion) question);
            case SINGLE_SELECT_ITEM:
                return new MultipleChoiceQuestionView(mContext, mLesson, (MultipleChoiceQuestion) question);
            case SPEECH_INPUT:
                return new SpeechInputQuestionView(mContext, mLesson, (SpeechInputQuestion) question);
            case CONTENT_TIP:
                return new ContentTipQuestionView(mContext,mLesson,(ContentTipQuestion) question,true);
            case MAKE_SENTENCE:
                return new MakeSentenceQuestionView(mContext,mLesson,(MakeSentenceQuestion) question,true);
        }
        throw new UnsupportedOperationException(
                "Question of type " + question.getType() + " can not be displayed.");
    }
    
}
