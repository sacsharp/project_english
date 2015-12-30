package com.sigrideducation.englishlearning.model.question;

import com.sigrideducation.englishlearning.model.JsonParts;

/**
 * Available types of quizzes.
 */
public enum QuestionType {
    FILL_BLANK(JsonParts.QuestionType.FILL_BLANK, FillBlankQuestion.class),
    SINGLE_SELECT_ITEM(JsonParts.QuestionType.MULTIPLE_CHOICE, MultipleChoiceQuestion.class),
    SPEECH_INPUT(JsonParts.QuestionType.SPEECH_INPUT,SpeechInputQuestion.class),
    CONTENT_TIP(JsonParts.QuestionType.CONTENT_TIP,ContentTipQuestion.class),
    MAKE_SENTENCE(JsonParts.QuestionType.MAKE_SENTENCE,MakeSentenceQuestion.class);

    private final String mJsonName;
    private final Class<? extends Question> mType;

    QuestionType(final String jsonName, final Class<? extends Question> type) {
        mJsonName = jsonName;
        mType = type;
    }

    public String getJsonName() {
        return mJsonName;
    }

    public Class<? extends Question> getType() {
        return mType;
    }
}
