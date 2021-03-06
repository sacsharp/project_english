package in.sigrid.englishlearning.database;

import android.provider.BaseColumns;

//lesson table.

public interface LessonTable {

    String NAME = "lesson";

    String COLUMN_ID = BaseColumns._ID;
    String FK_CHAPTER = "fk_chapter";
    String COLUMN_NAME = "lname";
    String COLUMN_THEME = "ltheme";
    String COLUMN_SOLVED = "solved";
    String COLUMN_SCORE = "score";

    String[] PROJECTION = new String[]{COLUMN_ID, FK_CHAPTER, COLUMN_NAME, COLUMN_THEME, COLUMN_SOLVED, COLUMN_SCORE};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + FK_CHAPTER + " REFERENCES "
            + ChapterTable.NAME + "(" + ChapterTable.COLUMN_ID + "), "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_THEME + " TEXT NOT NULL, "
            + COLUMN_SOLVED + " TEXT NOT NULL,"
            + COLUMN_SCORE +" INT);";
}
