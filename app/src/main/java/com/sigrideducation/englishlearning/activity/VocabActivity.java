package com.sigrideducation.englishlearning.activity;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sigrideducation.englishlearning.R;

public class VocabActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private DrawerLayout drawerlayout;
    private ListView listview;
    private ActionBarDrawerToggle mtoggle;
    String[] navigation_items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab);
        navigation_items = getResources().getStringArray(R.array.navigation_items);
        listview = (ListView) findViewById(R.id.drawerlist);
        listview.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navigation_items));
        listview.setOnItemClickListener(VocabActivity.this);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer);
        android.support.v7.widget.Toolbar toolbar1 = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(toolbar1);
        mtoggle = new ActionBarDrawerToggle(
                this,
                drawerlayout,
                toolbar1,
                R.string.drawer_open,
                R.string.drawer_close) {

        };
        drawerlayout.setDrawerListener(mtoggle);
        mtoggle.syncState();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}