package com.sigrideducation.englishlearning.database;

import android.provider.BaseColumns;


public interface GameMakeSentenceTable {

    String NAME = "gamemakesentence";

    String COLUMN_ID = BaseColumns._ID;
    String COLUMN_IMAGE_URL = "imageurl";
    String COLUMN_QUESTION= "question";
    String COLUMN_ANSWER= "answer";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_IMAGE_URL, COLUMN_QUESTION,COLUMN_ANSWER};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_IMAGE_URL + " TEXT NOT NULL, "
            + COLUMN_QUESTION + " TEXT NOT NULL, "
            + COLUMN_ANSWER + " TEXT NOT NULL);";
}
