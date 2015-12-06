package com.sigrideducation.englishlearning.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sigrideducation.englishlearning.helper.ParcelableHelper;
import com.sigrideducation.englishlearning.model.question.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lesson implements Parcelable {

    public static final String TAG = "Lesson";
    public static final Creator<Lesson> CREATOR = new Creator<Lesson>() {
        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };
    private static final int SCORE = 8;
    private static final int NO_SCORE = 0;
    private final String mName;
    private final String mId;
    private final Theme mTheme;
    private final int[] mScores;
    private List<Question> mQuestions;
    private boolean mSolved;

    public Lesson(String name, String id, Theme theme, List<Question> questions, boolean solved) {
        mName = name;
        mId = id;
        mTheme = theme;
        mQuestions = questions;
        mScores = new int[questions.size()];
        mSolved = solved;
    }

    public Lesson(String name, String id, Theme theme, List<Question> questions,
                  int[] scores, boolean solved) {
        mName = name;
        mId = id;
        mTheme = theme;
        if (questions.size() == scores.length) {
            mQuestions = questions;
            mScores = scores;
        } else {
            throw new IllegalArgumentException("Quizzes and scores must have the same length");
        }
        mSolved = solved;
    }

    protected Lesson(Parcel in) {
        mName = in.readString();
        mId = in.readString();
        mTheme = Theme.values()[in.readInt()];
        mQuestions = new ArrayList<>();
        in.readTypedList(mQuestions, Question.CREATOR);
        mScores = in.createIntArray();
        mSolved = ParcelableHelper.readBoolean(in);
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    public Theme getTheme() {
        return mTheme;
    }

    @NonNull
    public List<Question> getQuizzes() {
        return mQuestions;
    }

    /**
     * Updates a score for a provided quiz within this category.
     *
     * @param which The quiz to rate.
     * @param correctlySolved <code>true</code> if the quiz was solved else <code>false</code>.
     */
    public void setScore(Question which, boolean correctlySolved) {
        int index = mQuestions.indexOf(which);
        Log.d(TAG, "Setting score for " + which + " with index " + index);
        if (-1 == index) {
            return;
        }
        mScores[index] = correctlySolved ? SCORE : NO_SCORE;
    }

    public boolean isSolvedCorrectly(Question quiz) {
        return getScore(quiz) == SCORE;
    }

    /**
     * Gets the score for a single quiz.
     *
     * @param which The quiz to look for
     * @return The score if found, else 0.
     */
    public int getScore(Question which) {
        try {
            return mScores[mQuestions.indexOf(which)];
        } catch (IndexOutOfBoundsException ioobe) {
            return 0;
        }
    }

    /**
     * @return The sum of all quiz scores within this category.
     */
    public int getScore() {
        int categoryScore = 0;
        for (int quizScore : mScores) {
            categoryScore += quizScore;
        }
        return categoryScore;
    }

    public int[] getScores() {
        return mScores;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        this.mSolved = solved;
    }

    /**
     * Checks which quiz is the first unsolved within this category.
     *
     * @return The position of the first unsolved quiz.
     */
    public int getFirstUnsolvedQuizPosition() {
        if (mQuestions == null) {
            return -1;
        }
        for (int i = 0; i < mQuestions.size(); i++) {
            if (!mQuestions.get(i).isSolved()) {
                return i;
            }
        }
        return mQuestions.size();
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "mName='" + mName + '\'' +
                ", mId='" + mId + '\'' +
                ", mTheme=" + mTheme +
                ", mQuestions=" + mQuestions +
                ", mScores=" + Arrays.toString(mScores) +
                ", mSolved=" + mSolved +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mId);
        dest.writeInt(mTheme.ordinal());
        dest.writeTypedList(getQuizzes());
        dest.writeIntArray(mScores);
        ParcelableHelper.writeBoolean(dest, mSolved);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lesson category = (Lesson) o;

        if (!mId.equals(category.mId)) {
            return false;
        }
        if (!mName.equals(category.mName)) {
            return false;
        }
        if (!mQuestions.equals(category.mQuestions)) {
            return false;
        }
        if (mTheme != category.mTheme) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mId.hashCode();
        result = 31 * result + mTheme.hashCode();
        result = 31 * result + mQuestions.hashCode();
        return result;
    }
}
