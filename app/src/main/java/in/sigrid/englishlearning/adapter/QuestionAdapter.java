package in.sigrid.englishlearning.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import in.sigrid.englishlearning.model.Lesson;
import in.sigrid.englishlearning.model.question.ContentTipQuestion;
import in.sigrid.englishlearning.model.question.FillBlankQuestion;
import in.sigrid.englishlearning.model.question.MakeSentenceQuestion;
import in.sigrid.englishlearning.model.question.MultipleChoiceQuestion;
import in.sigrid.englishlearning.model.question.Question;
import in.sigrid.englishlearning.model.question.SpeechInputQuestion;
import in.sigrid.englishlearning.views.BaseQuestionView;
import in.sigrid.englishlearning.views.ContentTipQuestionView;
import in.sigrid.englishlearning.views.FillBlankQuestionView;
import in.sigrid.englishlearning.views.MakeSentenceQuestionView;
import in.sigrid.englishlearning.views.MultipleChoiceQuestionView;
import in.sigrid.englishlearning.views.SpeechInputQuestionView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sachin on 12/30/2015.
 * Adapter for displaying the questions.
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
        mQuestions = lesson.getQuestions();
        mViewTypeCount = calculateViewTypeCount();
    }

    private int calculateViewTypeCount() {
        Set<String> typesSet = new HashSet<>();
        for (int i = 0; i < mQuestions.size(); i++) {
            typesSet.add(mQuestions.get(i).getType().getJsonName());
        }
        mQuestionType = new ArrayList<>(typesSet);
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
        convertView = createViewOf(question);
        return convertView;
    }

    private BaseQuestionView createViewOf(Question question) {
        if (null == question) {
            throw new IllegalArgumentException("Question must not be null");
        }
        else {
            switch (question.getType()) {
                case CONTENT_TIP:
                    return new ContentTipQuestionView(mContext,mLesson,(ContentTipQuestion) question,true);
                case FILL_BLANK:
                    return new FillBlankQuestionView(mContext, mLesson, (FillBlankQuestion) question);
                case MAKE_SENTENCE:
                    return new MakeSentenceQuestionView(mContext,mLesson,(MakeSentenceQuestion) question,true);
                case MULTIPLE_CHOICE:
                    return new MultipleChoiceQuestionView(mContext, mLesson, (MultipleChoiceQuestion) question);
                case SPEECH_INPUT:
                    return new SpeechInputQuestionView(mContext, mLesson, (SpeechInputQuestion) question);
            }
            throw new UnsupportedOperationException(
                    "Question of type " + question.getType() + " can not be displayed.");
        }
    }
    
}
