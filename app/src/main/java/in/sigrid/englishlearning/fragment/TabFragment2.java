package in.sigrid.englishlearning.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andexert.library.RippleView;

import in.sigrid.englishlearning.R;
import in.sigrid.englishlearning.activity.VocabActivity;

/**
 * Created by Sachin on 12/6/2015.
 */
public class TabFragment2 extends Fragment {
    RippleView r1,r2,r3,r4;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_2,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        r1=(RippleView)getActivity().findViewById(R.id.more);
        r2=(RippleView)getActivity().findViewById(R.id.more1);
        r3=(RippleView)getActivity().findViewById(R.id.more2);
        r4=(RippleView)getActivity().findViewById(R.id.more3);
        r1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Activity activity = getActivity();
                Intent i=new Intent(activity,VocabActivity.class);
                startActivity(i);

            }
        });
    }
}
