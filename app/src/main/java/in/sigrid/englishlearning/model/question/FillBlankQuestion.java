package in.sigrid.englishlearning.model.question;

import android.os.Parcel;

public final class FillBlankQuestion extends Question<String> {

    public FillBlankQuestion(String question, String answer) {
        super(question, answer);
    }

    @SuppressWarnings("unused")
    public FillBlankQuestion(Parcel in) {
        super(in);
        setAnswer(in.readString());
    }

    @Override
    public String getStringAnswer() {
        return getAnswer();
    }

    @Override
    public QuestionType getType() {
        return QuestionType.FILL_BLANK;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getAnswer());
    }
}
