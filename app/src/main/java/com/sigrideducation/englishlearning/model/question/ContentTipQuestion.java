package com.sigrideducation.englishlearning.model.question;

import android.os.Parcel;

public final class ContentTipQuestion extends Question<String> {


    public ContentTipQuestion(String question, String answer) {
        super(question, answer);
    }

    @SuppressWarnings("unused")
    public ContentTipQuestion(Parcel in) {
        super(in);
        setAnswer(in.readString());
    }

    @Override
    public String getStringAnswer() {
        return getAnswer();
    }

    @Override
    public QuestionType getType() {
        return QuestionType.CONTENT_TIP;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getAnswer());
    }
}
