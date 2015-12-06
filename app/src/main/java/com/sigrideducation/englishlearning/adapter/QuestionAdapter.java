package com.sigrideducation.englishlearning.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.FillBlankQuestion;
import com.sigrideducation.englishlearning.model.question.Question;
import com.sigrideducation.englishlearning.model.question.SelectItemQuestion;
import com.sigrideducation.englishlearning.model.question.TrueFalseQuestion;
import com.sigrideducation.englishlearning.widget.question.AbsQuestionView;
import com.sigrideducation.englishlearning.widget.question.FillBlankQuestionView;
import com.sigrideducation.englishlearning.widget.question.SelectItemQuestionView;
import com.sigrideducation.englishlearning.widget.question.TrueFalseQuestionView;

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
    private List<String> mQuestionType;

    public QuestionAdapter(Context context, Lesson lesson) {
        mContext = context;
        mLesson = lesson;
        mQuestions = lesson.getQuizzes();
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
        final Question quiz = getItem(position);
        if (convertView instanceof AbsQuestionView) {
            if (((AbsQuestionView) convertView).getQuestion().equals(quiz)) {
                return convertView;
            }
        }
        convertView = getViewInternal(quiz);
        return convertView;
    }

    private AbsQuestionView getViewInternal(Question quiz) {
        if (null == quiz) {
            throw new IllegalArgumentException("Question must not be null");
        }
        return createViewFor(quiz);
    }

    private AbsQuestionView createViewFor(Question quiz) {
        switch (quiz.getType()) {
            case FILL_BLANK:
                return new FillBlankQuestionView(mContext, mLesson, (FillBlankQuestion) quiz);
            case SINGLE_SELECT_ITEM:
                return new SelectItemQuestionView(mContext, mLesson, (SelectItemQuestion) quiz);
            case TRUE_FALSE:
                return new TrueFalseQuestionView(mContext, mLesson, (TrueFalseQuestion) quiz);
        }
        throw new UnsupportedOperationException(
                "Question of type " + quiz.getType() + " can not be displayed.");
    }
}
