package com.sigrideducation.englishlearning.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Interpolator;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.fragment.QuestionFragment;
import com.sigrideducation.englishlearning.helper.ApiLevelHelper;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.persistence.ELDatabaseHelper;


public class QuestionActivity extends AppCompatActivity {

    private static final String TAG = "QuestionActivity";
    private static final String STATE_IS_PLAYING = "isPlaying";
    private static final int UNDEFINED = -1;
    private static final String FRAGMENT_TAG = "Quiz";

    private Interpolator mInterpolator;
    private String mLessonId;
    private QuestionFragment mQuestionFragment;
    private Toolbar mToolbar;
    private FloatingActionButton mQuestionFab;
    private boolean mSavedStateIsPlaying;
    private Animator mCircularReveal;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.fab_question:
                    startQuizFromClickOn(v);
                    break;
                case R.id.submitAnswer:
                    submitAnswer();
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
                            "OnClick has not been implemented for " + getResources().
                                    getResourceName(v.getId()));
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
            findViewById(R.id.quiz_fragment_container).setVisibility(View.VISIBLE);
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
            super.onBackPressed();
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
                                (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR1)
                                        && isDestroyed())) {
                            return;
                        }
                        QuestionActivity.super.onBackPressed();
                    }
                })
                .start();
    }

    private void startQuizFromClickOn(final View clickedView) {
        initQuestionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.quiz_fragment_container, mQuestionFragment, FRAGMENT_TAG).commit();
        final View fragmentContainer = findViewById(R.id.quiz_fragment_container);
        revealFragmentContainer(clickedView, fragmentContainer);
        // the toolbar should not have more elevation than the content while playing
        setToolbarElevation(false);
    }

    private void revealFragmentContainer(final View clickedView, final View fragmentContainer) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
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

    public void setToolbarElevation(boolean shouldElevate) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            mToolbar.setElevation(shouldElevate ?
                    getResources().getDimension(R.dimen.elevation_header) : 0);
        }
    }

    private void initQuestionFragment() {
        mQuestionFragment = QuestionFragment.newInstance(mLessonId,
                new QuestionFragment.SolvedStateListener() {
                    @Override
                    public void onLessonSolved() {
                        setToolbarElevation(true);
                        displayDoneFab();
                    }

                    private void displayDoneFab() {
                        /* We're re-using the already existing fab and give it some
                         * new values. This has to run delayed due to the queued animation
                         * to hide the fab initially.
                         */
                        if (null != mCircularReveal && mCircularReveal.isRunning()) {
                            mCircularReveal.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    showQuizFabWithDoneIcon();
                                    mCircularReveal.removeListener(this);
                                }
                            });
                        } else {
                            showQuizFabWithDoneIcon();
                        }
                    }

                    private void showQuizFabWithDoneIcon() {
                        mQuestionFab.setImageResource(R.drawable.ic_tick);
                        mQuestionFab.setId(R.id.question_done);
                        mQuestionFab.setVisibility(View.VISIBLE);
                        mQuestionFab.setScaleX(0f);
                        mQuestionFab.setScaleY(0f);
                        ViewCompat.animate(mQuestionFab)
                                .scaleX(1)
                                .scaleY(1)
                                .setInterpolator(mInterpolator)
                                .setListener(null)
                                .start();
                    }
                });
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            // the toolbar should not have more elevation than the content while playing
            setToolbarElevation(false);
        }
    }

    /**
     * Proceeds the quiz to it's next state.
     */
    public void proceed() {
        submitAnswer();
    }
    

    private void submitAnswer() {
        if (!mQuestionFragment.showNextPage()) {
            mQuestionFragment.showSummary();
            return;
        }
        setToolbarElevation(false);
    }

    private void populate(String categoryId) {
        if (null == categoryId) {
            Log.w(TAG, "Didn't find a lesson. Finishing");
            finish();
        }
        Lesson lesson = ELDatabaseHelper.getCategoryWith(this, categoryId);
        setTheme(lesson.getTheme().getStyleId());
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this,
                    lesson.getTheme().getPrimaryDarkColor()));
        }
        initLayout(lesson.getId());
        initToolbar(lesson);
    }

    private void initLayout(String categoryId) {
        setContentView(R.layout.activity_question);
        //noinspection PrivateResource
        mQuestionFab = (FloatingActionButton) findViewById(R.id.fab_question);
        mQuestionFab.setImageResource(R.drawable.ic_play);
        if (mSavedStateIsPlaying) {
            mQuestionFab.hide();
        } else {
            mQuestionFab.show();
        }
        mQuestionFab.setOnClickListener(mOnClickListener);
    }

    private void initToolbar(Lesson lesson) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_quiz);
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, lesson.getTheme().getPrimaryColor()));
        mToolbar.setTitle(lesson.getName());
        mToolbar.setNavigationOnClickListener(mOnClickListener);
        if (mSavedStateIsPlaying) {
            // the toolbar should not have more elevation than the content while playing
            setToolbarElevation(false);
        }
    }

}
