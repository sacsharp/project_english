package com.sigrideducation.englishlearning.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.sigrideducation.englishlearning.helper.ParcelableHelper;
import com.sigrideducation.englishlearning.model.question.Question;

import java.util.ArrayList;
import java.util.List;

public class Lesson implements Parcelable {

    public static final String TAG = "Lesson";
    private final String mName;
    private final String mId;
    private final Theme mTheme;
    private List<Question> mQuestions;
    private boolean mSolved;


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

    public Lesson(String name, String id, Theme theme, List<Question> questions, boolean solved) {
        mName = name;
        mId = id;
        mTheme = theme;
        mQuestions = questions;
        mSolved = solved;
    }

    protected Lesson(Parcel in) {
        mName = in.readString();
        mId = in.readString();
        mTheme = Theme.values()[in.readInt()];
        mQuestions = new ArrayList<>();
        in.readTypedList(mQuestions, Question.CREATOR);
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
    public List<Question> getQuestions() {
        return mQuestions;
    }


    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        this.mSolved = solved;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "mName='" + mName + '\'' +
                ", mId='" + mId + '\'' +
                ", mTheme=" + mTheme +
                ", mQuestions=" + mQuestions +
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
        dest.writeTypedList(getQuestions());
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
