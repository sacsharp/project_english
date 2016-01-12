package com.sigrideducation.englishlearning.database;

import android.provider.BaseColumns;


public interface ScoreTable {

    String NAME = "score";

    String COLUMN_ID = BaseColumns._ID;
    String COLUMN_TYPE="type";
    String COLUMN_SCORE="score";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_TYPE,COLUMN_SCORE};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TYPE + " TEXT NOT NULL, "
            + COLUMN_SCORE + " INTEGER NOT NULL);";
}
