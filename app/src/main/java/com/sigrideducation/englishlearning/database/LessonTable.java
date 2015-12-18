package com.sigrideducation.englishlearning.database;

import android.provider.BaseColumns;

/**
 * Structure of the category table.
 */
public interface LessonTable {

    String NAME = "lesson";

    String COLUMN_ID = BaseColumns._ID;
    String COLUMN_NAME = "name";
    String COLUMN_THEME = "theme";
    String COLUMN_SOLVED = "solved";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME,
            COLUMN_THEME, COLUMN_SOLVED};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_THEME + " TEXT NOT NULL, "
            + COLUMN_SOLVED + " TEXT NOT NULL);";
}
