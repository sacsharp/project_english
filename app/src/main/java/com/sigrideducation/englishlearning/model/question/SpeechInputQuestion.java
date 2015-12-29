package com.sigrideducation.englishlearning.model.question;

import android.os.Parcel;

public final class SpeechInputQuestion extends Question<String> {


    public SpeechInputQuestion(String question, String answer) {
        super(question, answer);
    }

    @SuppressWarnings("unused")
    public SpeechInputQuestion(Parcel in) {
        super(in);
        setAnswer(in.readString());
    }

    @Override
    public String getStringAnswer() {
        return getAnswer();
    }

    @Override
    public QuestionType getType() {
        return QuestionType.SPEECH_INPUT;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getAnswer());
    }
}
