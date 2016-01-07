package com.sigrideducation.englishlearning.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.activity.GameMakeSentenceActivity;

/**
 * Created by Sachin on 12/6/2015.
 */
public class TabFragment3 extends Fragment {
    
    Button mBtn10;
    Button mBtnTime;
    Button mBtnUnlimited;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_3,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mBtn10 = (Button) view.findViewById(R.id.btn_10);
        mBtn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),mBtn10.getText().toString(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),GameMakeSentenceActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });
        mBtnTime = (Button) view.findViewById(R.id.btn_time);
        mBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),mBtnTime.getText().toString(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),GameMakeSentenceActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
        mBtnUnlimited =(Button) view.findViewById(R.id.btn_unlimited);
        mBtnUnlimited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),mBtnUnlimited.getText().toString(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),GameMakeSentenceActivity.class);
                intent.putExtra("type",3);
                startActivity(intent);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
