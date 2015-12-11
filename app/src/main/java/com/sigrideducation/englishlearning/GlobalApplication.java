package com.sigrideducation.englishlearning;

import android.app.Application;

/**
 * Created by Sachin on 12/10/2015.
 */
public class GlobalApplication extends Application {
    private boolean submitAnswerGuideShown=false;
    private boolean trueGuideShown=false;
    private boolean falseGuideShown=false;
    private boolean lessonStartGuideShow=false;


    public boolean isSubmitAnswerGuideShown() {
        return submitAnswerGuideShown;
    }

    public void setSubmitAnswerGuideShown(boolean submitAnswerGuideShown) {
        this.submitAnswerGuideShown = submitAnswerGuideShown;
    }

    public boolean isTrueGuideShown() {
        return trueGuideShown;
    }

    public void setTrueGuideShown(boolean trueGuideShown) {
        this.trueGuideShown = trueGuideShown;
    }

    public boolean isFalseGuideShown() {
        return falseGuideShown;
    }

    public void setFalseGuideShown(boolean falseGuideShown) {
        this.falseGuideShown = falseGuideShown;
    }

    public boolean isLessonStartGuideShow() {
        return lessonStartGuideShow;
    }

    public void setLessonStartGuideShow(boolean lessonStartGuideShow) {
        this.lessonStartGuideShow = lessonStartGuideShow;
    }
}
