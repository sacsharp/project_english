package com.sigrideducation.englishlearning.model.question;

import android.os.Parcel;

public final class FillBlankQuestion extends Question<String> {

    private final String mStart;
    private final String mEnd;

    public FillBlankQuestion(String question, String answer, String start, String end, boolean solved) {
        super(question, answer, solved);
        mStart = start;
        mEnd = end;
    }

    @SuppressWarnings("unused")
    public FillBlankQuestion(Parcel in) {
        super(in);
        setAnswer(in.readString());
        mStart = in.readString();
        mEnd = in.readString();
    }

    @Override
    public String getStringAnswer() {
        return getAnswer();
    }

    public String getStart() {
        return mStart;
    }

    public String getEnd() {
        return mEnd;
    }

    @Override
    public QuestionType getType() {
        return QuestionType.FILL_BLANK;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getAnswer());
        dest.writeString(mStart);
        dest.writeString(mEnd);
    }
}
