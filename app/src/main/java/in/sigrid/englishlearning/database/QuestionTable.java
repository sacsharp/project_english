package in.sigrid.englishlearning.database;

import android.provider.BaseColumns;

//question table.
public interface QuestionTable {

    String NAME = "question";

    String COLUMN_ID = BaseColumns._ID;
    String FK_LESSON = "fk_lesson";
    String COLUMN_TYPE = "type";
    String COLUMN_QUESTION = "question";
    String COLUMN_ANSWER = "answer";
    String COLUMN_OPTIONS = "options";

    String[] PROJECTION = new String[]{COLUMN_ID, FK_LESSON, COLUMN_TYPE,
            COLUMN_QUESTION, COLUMN_ANSWER, COLUMN_OPTIONS};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + FK_LESSON + " REFERENCES "
            + LessonTable.NAME + "(" + LessonTable.COLUMN_ID + "), "
            + COLUMN_TYPE + " TEXT NOT NULL, "
            + COLUMN_QUESTION + " TEXT NOT NULL, "
            + COLUMN_ANSWER + " TEXT NOT NULL, "
            + COLUMN_OPTIONS + " TEXT);";
}