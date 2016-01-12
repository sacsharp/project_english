package com.sigrideducation.englishlearning.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;

import com.sigrideducation.englishlearning.R;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;

    private boolean mSignInClicked = false;
    final String TAG = "EL";
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;
    private boolean mResolvingError = false;

    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    private Button mBtnGoogleSignIn;
    private Button mBtnGoogleSignOut;
    private Button mBtnGoogleShowAchievements;

    private TextView mTxtPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        mBtnGoogleSignIn = (Button)findViewById(R.id.btn_google_sign_in);
        mBtnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }
        });
        mBtnGoogleSignOut =(Button) findViewById(R.id.btn_google_sign_out);
        mBtnGoogleSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignOutButtonClicked();
            }
        });
        mBtnGoogleShowAchievements =  (Button) findViewById(R.id.btn_google_show_achievements);
        mBtnGoogleShowAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowAchievementsRequested();
            }
        });

        mTxtPlayerName = (TextView) findViewById(R.id.txt_player_name);

        mOutbox.loadLocal(this);

    }


    public void onSignOutButtonClicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivity(Games.Achievements.getAchievementsIntent(mGoogleApiClient));
        } else {
           Toast.makeText(this,getString(R.string.achievements_not_available),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String displayName;
        if (p == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
        mTxtPlayerName.setText(displayName);
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    void checkForAchievements() {


    }

    void achievementToast(String achievement) {
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal(this);
            return;
        }
        if (mOutbox.mPerfectTenAchievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_perfect_10));
            mOutbox.mPerfectTenAchievement = false;
        }
        if (mOutbox.mSuperFastAchievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_superfast));
            mOutbox.mSuperFastAchievement = false;
        }
        if (mOutbox.mUntirableAchievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_untirable));
            mOutbox.mUntirableAchievement = false;
        }
        if (mOutbox.mLittleStepAchievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_little_step));
            mOutbox.mLittleStepAchievement = false;
        }
        if (mOutbox.mWayToGoAchievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_way_to_go));
            mOutbox.mWayToGoAchievement = false;
        }
        mOutbox.saveLocal(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed!!!!! :( :( :( :(");
    }

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    class AccomplishmentsOutbox {
        boolean mPerfectTenAchievement = false;
        boolean mSuperFastAchievement = false;
        boolean mUntirableAchievement = false;
        boolean mLittleStepAchievement = false;
        boolean mWayToGoAchievement = false;

        boolean isEmpty() {
            return !mPerfectTenAchievement && !mSuperFastAchievement && !mUntirableAchievement &&
                    !mLittleStepAchievement && mWayToGoAchievement ;
        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }

}
