package com.sigrideducation.englishlearning.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.database.ELDatabaseHelper;
import com.sigrideducation.englishlearning.fragment.QuestionFragment;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.views.SpeechInputQuestionView;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;


public class QuestionActivity extends AppCompatActivity {

    private static final String TAG = "QuestionActivity";
    private static final String STATE_IS_PLAYING = "isPlaying";
    private static final int UNDEFINED = -1;
    private static final String FRAGMENT_TAG = "Question";

    private Interpolator mInterpolator;
    private String mLessonId;
    private QuestionFragment mQuestionFragment;
    private FloatingActionButton mQuestionFab;
    private LinearLayout mQuestionContainer;
    private TextView mTxtLessonName;
    private boolean mSavedStateIsPlaying;
    private Animator mCircularReveal;
    TourGuide mTourGuideHandler, mTourGuideHandler1;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.fab_question:
                    startLessonFromClickOn(v);
                    if(mTourGuideHandler !=null)
                    mTourGuideHandler.cleanUp();
                    break;

                case R.id.question_done:
                    ActivityCompat.finishAfterTransition(QuestionActivity.this);
                    break;

                case UNDEFINED:
                    final CharSequence contentDescription = v.getContentDescription();
                    if (contentDescription != null && contentDescription
                            .equals(getString(R.string.up))) {
                        onBackPressed();
                        break;
                    }

                default:
                    throw new UnsupportedOperationException(
                            "OnClick has not been implemented for " + getResources().getResourceName(v.getId()));
            }
        }
    };

    public static Intent getStartIntent(Context context, Lesson lesson) {
        Intent starter = new Intent(context, QuestionActivity.class);
        starter.putExtra(Lesson.TAG, lesson.getId());
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLessonId = getIntent().getStringExtra(Lesson.TAG);
        mInterpolator = new FastOutSlowInInterpolator();
        if (null != savedInstanceState) {
            mSavedStateIsPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING);
        }
        super.onCreate(savedInstanceState);
        populate(mLessonId);
    }

    @Override
    protected void onResume() {
        if (mSavedStateIsPlaying) {
            mQuestionFragment = (QuestionFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            findViewById(R.id.question_fragment_container).setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_IS_PLAYING, mQuestionFab.getVisibility() == View.GONE);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mQuestionFab == null) {
            // Skip the animation if icon or fab are not initialized.
            showAlert();
            return;
        }

        ViewCompat.animate(mQuestionFab)
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(mInterpolator)
                .setStartDelay(100)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onAnimationEnd(View view) {
                        if (isFinishing() ||
                                (( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                                        && isDestroyed())) {
                            return;
                        }
                        showAlert();
                    }
                })
                .start();
    }

    private void showAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Are you sure to exit?")
                .setCancelable(false)
                .setMessage("Your progress would get lost.")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        QuestionActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void startLessonFromClickOn(final View clickedView) {
        mTxtLessonName.setVisibility(View.GONE);
        initQuestionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.question_fragment_container, mQuestionFragment, FRAGMENT_TAG).commit();
        final View fragmentContainer = findViewById(R.id.question_fragment_container);
        revealFragmentContainer(clickedView, fragmentContainer);
    }

    private void revealFragmentContainer(final View clickedView, final View fragmentContainer) {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            revealFragmentContainerLollipop(clickedView, fragmentContainer);
        } else {
            fragmentContainer.setVisibility(View.VISIBLE);
            clickedView.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealFragmentContainerLollipop(final View clickedView,
                                                 final View fragmentContainer) {
        prepareCircularReveal(clickedView, fragmentContainer);
        ViewCompat.animate(clickedView)
                .scaleX(0)
                .scaleY(0)
                .setInterpolator(mInterpolator)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        fragmentContainer.setVisibility(View.VISIBLE);
                        mCircularReveal.start();
                        clickedView.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void prepareCircularReveal(View startView, View targetView) {
        int centerX = (startView.getLeft() + startView.getRight()) / 2;
        int centerY = (startView.getTop() + startView.getBottom()) / 2;
        float finalRadius = (float) Math.hypot((double) centerX, (double) centerY);
        mCircularReveal = ViewAnimationUtils.createCircularReveal(
                targetView, centerX, centerY, 0, finalRadius);

        mCircularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCircularReveal.removeListener(this);
            }
        });
    }

    private void initQuestionFragment() {
        mQuestionFragment = QuestionFragment.newInstance(mLessonId);
    }

    /**
     * Proceeds the quiz to it's next state.
     */
    public void proceed() {
        moveToNext();
    }

    private void moveToNext(){
        if (!mQuestionFragment.showNextPage()) {
            mQuestionFragment.showSummary(mLessonId);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechInputQuestionView.destroy();
    }

    private void populate(String lessonId) {
        if (null == lessonId) {
            Log.w(TAG, "Didn't find a lesson. Finishing");
            finish();
        }
        Lesson lesson = ELDatabaseHelper.getLessonWith(this, lessonId);
        setTheme(lesson.getTheme().getStyleId());
        if ( Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP ) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, lesson.getTheme().getPrimaryDarkColor()));
        }
        initLayout(lesson.getId());
    }

    private void initLayout(String lessonId) {
        setContentView(R.layout.activity_question);
        //noinspection PrivateResource
        mQuestionFab = (FloatingActionButton) findViewById(R.id.fab_question);
        mQuestionFab.setImageResource(R.drawable.ic_play_arrow_black_48dp);

        Lesson lesson = ELDatabaseHelper.getLessonWith(this, lessonId);
        mQuestionContainer = (LinearLayout)findViewById(R.id.question_container);
        mQuestionContainer.setBackgroundColor(ContextCompat.getColor(this, lesson.getTheme().getWindowBackgroundColor()));

        mTxtLessonName =(TextView) findViewById(R.id.txt_lesson_name);
        mTxtLessonName.setText(ELDatabaseHelper.getLessonWith(this, lessonId).getName());

        SharedPreferences sharedPreferences = getSharedPreferences("MY_PREF",MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("hasLessonStartGuideShown",false)){
            mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip().setTitle("Welcome!").setDescription("Click to start the lesson......").setGravity(Gravity.TOP | Gravity.LEFT))
                    .setOverlay(new Overlay())
                    .playOn(mQuestionFab);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hasLessonStartGuideShown",true);
            editor.commit();
        }

        if (mSavedStateIsPlaying) {
            mQuestionFab.hide();
            mTxtLessonName.setVisibility(View.GONE);
        } else {
            mQuestionFab.show();
            mTxtLessonName.setVisibility(View.VISIBLE);
        }
        mQuestionFab.setOnClickListener(mOnClickListener);
    }
}
