package com.sigrideducation.englishlearning.model.question;

import android.os.Parcel;

import java.util.Arrays;


public final class MultipleChoiceQuestion extends Question<Integer> {

    private String[] mOptions;
    public MultipleChoiceQuestion(String question, int answer, String[] options) {
        super(question, answer);
        mOptions = options;
    }

    @SuppressWarnings("unused")
    public MultipleChoiceQuestion(Parcel in) {
        super(in);
        String[] options = in.createStringArray();
        final int answer = in.readInt();
        setOptions(options);
        setAnswer(answer);
    }

    @Override
    public QuestionType getType() {
        return QuestionType.SINGLE_SELECT_ITEM;
    }

    @Override
    public String getStringAnswer() {
        return getAnswer().toString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringArray(getOptions());
        dest.writeInt(getAnswer());
    }

    public String[] getOptions() {
        return mOptions;
    }

    protected void setOptions(String[] options) {
        mOptions = options;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(mOptions);
        return result;
    }
}
