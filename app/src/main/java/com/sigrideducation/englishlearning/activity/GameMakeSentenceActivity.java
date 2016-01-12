package com.sigrideducation.englishlearning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.fragment.FragmentGameMakeSentence;

public class GameMakeSentenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_make_sentence);

        Intent intent = getIntent();
        Integer type = intent.getIntExtra("type",0);

        switch (type)
        {
            case 1:
                Fragment fragment = new FragmentGameMakeSentence();
                Bundle bundle = new Bundle();
                bundle.putInt("questions",5);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.frame_layout_game,fragment).commit();
                break;
            case 2:
                Fragment fragmentTime = new FragmentGameMakeSentence();
                Bundle bundleTime = new Bundle();
                bundleTime.putInt("questions",1);
                fragmentTime.setArguments(bundleTime);
                getFragmentManager().beginTransaction().replace(R.id.frame_layout_game,fragmentTime).commit();
                break;
            case 3:
                Fragment fragmentUnlimited = new FragmentGameMakeSentence();
                Bundle bundleUnlimited = new Bundle();
                bundleUnlimited.putInt("questions",0);
                fragmentUnlimited.setArguments(bundleUnlimited);
                getFragmentManager().beginTransaction().replace(R.id.frame_layout_game,fragmentUnlimited).commit();
                break;
            default:
                throw new IllegalArgumentException("Incorrect parameter");

        }
    }
}
