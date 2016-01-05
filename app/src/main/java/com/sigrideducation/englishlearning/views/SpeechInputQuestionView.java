package com.sigrideducation.englishlearning.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.question.SpeechInputQuestion;

import java.util.ArrayList;
import java.util.Locale;

@SuppressLint("ViewConstructor")
public class SpeechInputQuestionView extends BaseQuestionView<SpeechInputQuestion> {

    private static final String KEY_ANSWER = "ANSWER";

    private TextView mTextSpeechInput;


    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIsListening;

    String text;
    TextToSpeech tts;
    static TextToSpeech tts1;

    public SpeechInputQuestionView(Context context, Lesson lesson, SpeechInputQuestion question) {
        super(context, lesson, question);
    }

    @Override
    protected View createQuestionContentView() {
        final Context context = getContext();
        final ImageButton mBtnSpeak;
        final ImageButton mBtnListen;
        LinearLayout container = (LinearLayout) getLayoutInflater().inflate(R.layout.question_speech_input,this,false);

        mTextSpeechInput = (TextView) container.findViewById(R.id.txt_speech_input);
        mBtnSpeak = (ImageButton) container.findViewById(R.id.btn_speak);
        mBtnListen =(ImageButton) container.findViewById(R.id.btn_listen);

        tts=new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });
        tts1 = tts;

        mBtnListen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertTextToSpeech();
            }
        });



        mBtnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
                mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_prompt);


                SpeechRecognitionListener listener = new SpeechRecognitionListener();
                mSpeechRecognizer.setRecognitionListener(listener);

                if (!mIsListening)
                {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
            }
        });
        if(isAnswerCorrect())
        {
            mTextSpeechInput.setTextColor(Color.GREEN);
        }
        else
        mTextSpeechInput.setTextColor(Color.RED);


        return container;
    }



    private void ConvertTextToSpeech() {
        // TODO Auto-generated method stub
        text = getQuestion().getQuestion();
        if(text==null||"".equals(text))
        {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected boolean isAnswerCorrect() {

        return getQuestion().isAnswerCorrect(mTextSpeechInput.getText().toString());
    }

    @Override
    public Bundle getUserInput() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ANSWER, mTextSpeechInput.getText().toString());
        return bundle;
    }

    @Override
    public void setUserInput(Bundle savedInput) {
        if (savedInput == null) {
            return;
        }

        if (mTextSpeechInput == null) {
            return;
        }
    }

    public static void destroy(){
        if(tts1 != null)
        tts1.shutdown();
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d("Speaking", "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            mTextSpeechInput.setText(matches.get(0).toString());
            allowCheckAnswer();
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

}
