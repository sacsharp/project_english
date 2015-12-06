package com.sigrideducation.englishlearning.widget.question;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.Question;


public abstract class TextInputQuestionView<Q extends Question> extends AbsQuestionView<Q>
        implements TextWatcher, TextView.OnEditorActionListener {

    public TextInputQuestionView(Context context, Lesson category, Q question) {
        super(context, category, question);
    }

    protected final EditText createEditText() {
        EditText editText = (EditText) getLayoutInflater().inflate(
                R.layout.question_edit_text, this, false);
        editText.addTextChangedListener(this);
        editText.setOnEditorActionListener(this);
        return editText;
    }

    @Override
    protected void submitAnswer() {
        hideKeyboard(this);
        super.submitAnswer();
    }

    /**
     * Convenience method to hide the keyboard.
     *
     * @param view A view in the hierarchy.
     */
    protected void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = getInputMethodManager();
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (TextUtils.isEmpty(v.getText())) {
            return false;
        }
        allowAnswer();
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            submitAnswer();
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
        allowAnswer(!TextUtils.isEmpty(s));
    }
}
