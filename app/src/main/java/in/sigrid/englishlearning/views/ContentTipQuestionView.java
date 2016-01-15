package in.sigrid.englishlearning.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.sigrid.englishlearning.R;
import in.sigrid.englishlearning.model.Lesson;
import in.sigrid.englishlearning.model.question.ContentTipQuestion;

@SuppressLint("ViewConstructor")
public class ContentTipQuestionView extends BaseQuestionView<ContentTipQuestion> {

    private static final String KEY_ANSWER = "ANSWER";




    public ContentTipQuestionView(Context context, Lesson lesson, ContentTipQuestion question,boolean isScroll) {
        super(context, lesson, question,isScroll);
    }

    @Override
    protected View createQuestionContentView() {
        final Context context = getContext();
        final TextView mTextData;
        LinearLayout container = (LinearLayout) getLayoutInflater().inflate(R.layout.question_content_tip, this, false);
        mTextData =(TextView) container.findViewById(R.id.text_data);

        container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                allowCheckAnswer(true);
                return true;
            }
        });
        mTextData.setText(getQuestion().getAnswer());
        mTextData.setTextSize(30);
        return container;
    }



    @Override
    protected boolean isAnswerCorrect() {

        return true;
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ANSWER,"data");
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }

    }

}
