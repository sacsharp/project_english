package com.sigrideducation.englishlearning.model.question;

import android.os.Parcel;


public final class SelectItemQuestion extends OptionsQuestion<String> {

    public SelectItemQuestion(String question, int answer, String[] options) {
        super(question, answer, options);
    }

    @SuppressWarnings("unused")
    public SelectItemQuestion(Parcel in) {
        super(in);
        String[] options = in.createStringArray();
        setOptions(options);
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
    }
}
