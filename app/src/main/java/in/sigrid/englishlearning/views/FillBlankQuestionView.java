package in.sigrid.englishlearning.views;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import in.sigrid.englishlearning.R;
import in.sigrid.englishlearning.model.Lesson;
import in.sigrid.englishlearning.model.question.FillBlankQuestion;


public class FillBlankQuestionView extends BaseQuestionView<FillBlankQuestion>
        implements TextWatcher, TextView.OnEditorActionListener {

    private static final String KEY_ANSWER = "ANSWER";
    private EditText mAnswerView;

    public FillBlankQuestionView(Context context, Lesson category, FillBlankQuestion question) {
        super(context, category, question);
    }

    @Override
    protected View createQuestionContentView() {
        if (null == mAnswerView) {
            mAnswerView = createEditText();
        }
        return mAnswerView;
    }

    @Override
    protected boolean isAnswerCorrect() {
        return getQuestion().isAnswerCorrect(mAnswerView.getText().toString());
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ANSWER, mAnswerView.getText().toString());
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }
        mAnswerView.setText(savedInput.getString(KEY_ANSWER));
    }

    protected final EditText createEditText() {
        EditText editText = (EditText) getLayoutInflater().inflate(R.layout.question_fill_blank, this, false);
        editText.addTextChangedListener(this);
        editText.setOnEditorActionListener(this);
        return editText;
    }

    @Override
    protected void checkAnswer() {
        hideKeyboard(this);
        super.checkAnswer();
    }

    protected void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = getInputMethodManager();
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (TextUtils.isEmpty(v.getText())) {
            return false;
        }
        allowCheckAnswer();
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkAnswer();
            hideKeyboard(v);
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        /* no-op */
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /* no-op */
    }

    @Override
    public void afterTextChanged(Editable s) {
        allowCheckAnswer(!TextUtils.isEmpty(s));
    }
}
