package com.sigrideducation.englishlearning.model.question;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.sigrideducation.englishlearning.helper.ParcelableHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This abstract class provides general structure for quizzes.
 */
public abstract class Question<A> implements Parcelable {

    private static final String TAG = "Question";
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            int ordinal = in.readInt();
            QuestionType type = QuestionType.values()[ordinal];
            try {
                Constructor<? extends Question> constructor = type.getType().getConstructor(Parcel.class);
                return constructor.newInstance(in);
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException | NoSuchMethodException e) {
                Log.e(TAG, "createFromParcel ", e);
            }
            throw new UnsupportedOperationException("Could not create Question");
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    private final String mQuestion;
    private final String mQuestionType;
    private A mAnswer;
    /**
     * Flag indicating whether this quiz has already been solved.
     * It does not give information whether the solution was correct or not.
     */
    private boolean mSolved;

    protected Question(String question, A answer, boolean solved) {
        mQuestion = question;
        mAnswer = answer;
        mQuestionType = getType().getJsonName();
        mSolved = solved;
    }

    protected Question(Parcel in) {
        mQuestion = in.readString();
        mQuestionType = getType().getJsonName();
        mSolved = ParcelableHelper.readBoolean(in);
    }

    /**
     * @return The {@link QuestionType} that represents this quiz.
     */
    public abstract QuestionType getType();

    /**
     * Implementations need to return a human readable version of the given answer.
     */
    public abstract String getStringAnswer();

    public String getQuestion() {
        return mQuestion;
    }

    public A getAnswer() {
        return mAnswer;
    }

    protected void setAnswer(A answer) {
        mAnswer = answer;
    }

    public boolean isAnswerCorrect(A answer) {
        return mAnswer.equals(answer);
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    /**
     * @return The id of this quiz.
     */
    public int getId() {
        return getQuestion().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableHelper.writeEnumValue(dest, getType());
        dest.writeString(mQuestion);
        ParcelableHelper.writeBoolean(dest, mSolved);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }

        Question quiz = (Question) o;

        if (mSolved != quiz.mSolved) {
            return false;
        }
        if (!mAnswer.equals(quiz.mAnswer)) {
            return false;
        }
        if (!mQuestion.equals(quiz.mQuestion)) {
            return false;
        }
        if (!mQuestionType.equals(quiz.mQuestionType)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mQuestion.hashCode();
        result = 31 * result + mAnswer.hashCode();
        result = 31 * result + mQuestionType.hashCode();
        result = 31 * result + (mSolved ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getType() + ": \"" + getQuestion() + "\"";
    }
}
