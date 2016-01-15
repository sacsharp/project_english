package in.sigrid.englishlearning.database;

import android.provider.BaseColumns;

//chapter table

public interface ChapterTable {

    String NAME = "chapter";

    String COLUMN_ID = BaseColumns._ID;
    String COLUMN_NAME = "cname";
    String COLUMN_LESSONS= "lessons";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_LESSONS};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_LESSONS + " INT NOT NULL);";
}
