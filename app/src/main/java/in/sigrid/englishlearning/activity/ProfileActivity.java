package in.sigrid.englishlearning.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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

import in.sigrid.englishlearning.R;
import in.sigrid.englishlearning.database.ELDatabaseHelper;


public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;

    private boolean mSignInClicked = false;
    final String TAG = "EL";
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private boolean mResolvingError = false;

    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    private Button mBtnGoogleSignIn;
    private Button mBtnGoogleSignOut;
    private Button mBtnGoogleShowAchievements;

    private Button mRefresh;

    private TextView mTxtPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).addScope(Plus.SCOPE_PLUS_PROFILE)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .setViewForPopups(findViewById(android.R.id.content))
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

        mRefresh = (Button) findViewById(R.id.btn_refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForAchievements("");
                pushAccomplishments();
            }
        });
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
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_UNUSED);
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

    public void checkForAchievements(String type) {
        Log.i("Achievement","counting");
        if(ELDatabaseHelper.getScore(this,"",0)) {
            mOutbox.mWayToGoAchievement = true;
            achievementToast(getString(R.string.achievement_waytogo_toast));
        }
        if(ELDatabaseHelper.getScore(this,type,10) && type.equals("limited")) {
            mOutbox.mPerfectTenAchievement = true;
            achievementToast(getString(R.string.achievement_perfect_ten_toast));
        }
        if(ELDatabaseHelper.getScore(this,type,10) && type.equals("time")) {
            mOutbox.mSuperFastAchievement = true;
            achievementToast(getString(R.string.achievement_superfast_toast));
        }
        if(ELDatabaseHelper.getScore(this,type,20) && type.equals("unlimited")) {
            mOutbox.mUntirableAchievement = true;
            achievementToast(getString(R.string.achievement_untirable_toast));
        }
        pushAccomplishments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                Toast.makeText(this,requestCode+" "+resultCode,Toast.LENGTH_SHORT).show();
            }
        }
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
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            (new AlertDialog.Builder(this)).setMessage(""+connectionResult.getErrorCode())
                    .setNeutralButton(android.R.string.ok, null).create().show();
            mResolvingError = true;
        }

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
