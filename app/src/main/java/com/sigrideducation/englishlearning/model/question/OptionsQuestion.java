package com.sigrideducation.englishlearning.model.question;

import android.os.Parcel;

import java.util.Arrays;

/**
 * Base class holding details for questions with several potential answers.

 */
public abstract class OptionsQuestion<T> extends Question<Integer> {

    private T[] mOptions;

    public OptionsQuestion(String question, int answer, T[] options, boolean solved) {
        super(question, answer, solved);
        mOptions = options;
    }

    public OptionsQuestion(Parcel in) {
        super(in);
        final int answer = in.readInt();
        setAnswer(answer);
    }

    public T[] getOptions() {
        return mOptions;
    }

    protected void setOptions(T[] options) {
        mOptions = options;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(getAnswer());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(mOptions);
        return result;
    }
}
