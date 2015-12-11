package com.sigrideducation.englishlearning.model.question;

import com.sigrideducation.englishlearning.model.JsonAttributes;

/**
 * Available types of quizzes.
 */
public enum QuestionType {
    FILL_BLANK(JsonAttributes.QuestionType.FILL_BLANK, FillBlankQuestion.class),
    SINGLE_SELECT_ITEM(JsonAttributes.QuestionType.SINGLE_SELECT_ITEM, SelectItemQuestion.class),
    //TOGGLE_TRANSLATE(JsonAttributes.QuestionType.TOGGLE_TRANSLATE, ToggleTranslateQuestion.class),
    TRUE_FALSE(JsonAttributes.QuestionType.TRUE_FALSE, TrueFalseQuestion.class),
    SPEECH_INPUT(JsonAttributes.QuestionType.SPEECH_INPUT,SpeechInputQuestion.class),
    CONTENT_TIP(JsonAttributes.QuestionType.CONTENT_TIP,ContentTipQuestion.class),
    MAKE_SENTENCE(JsonAttributes.QuestionType.MAKE_SENTENCE,MakeSentenceQuestion.class);

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
