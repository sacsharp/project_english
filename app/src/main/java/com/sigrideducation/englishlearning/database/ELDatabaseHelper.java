package com.sigrideducation.englishlearning.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.sigrideducation.englishlearning.model.JsonParts;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.Theme;
import com.sigrideducation.englishlearning.model.question.ContentTipQuestion;
import com.sigrideducation.englishlearning.model.question.FillBlankQuestion;
import com.sigrideducation.englishlearning.model.question.MakeSentenceQuestion;
import com.sigrideducation.englishlearning.model.question.Question;
import com.sigrideducation.englishlearning.model.question.MultipleChoiceQuestion;
import com.sigrideducation.englishlearning.model.question.SpeechInputQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database for storing and retrieving info for lessons and questions
 */
public class ELDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "ELDatabaseHelper";
    private static final String DB_NAME = "englishLearning";
    private static final String DB_SUFFIX = ".db";
    private static final int DB_VERSION = 1;
    private static List<Lesson> mLessons;
    private static ELDatabaseHelper mInstance;

    public ELDatabaseHelper(Context context) {
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
    }

    private static ELDatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new ELDatabaseHelper(context);
        }
        return mInstance;
    }

    //Gets all lessons with their questions.
    public static List<Lesson> getLessons(Context context, boolean fromDatabase) {
        if (null == mLessons || fromDatabase) {
            SQLiteDatabase readableDatabase = getReadableDatabase(context);
            Cursor data = readableDatabase.query(LessonTable.NAME, LessonTable.PROJECTION, null, null, null, null, null);
            data.moveToFirst();
            List<Lesson> tmpLessons = new ArrayList<>(data.getCount());
            do {
                final Lesson lesson = getLesson(data, readableDatabase);
                tmpLessons.add(lesson);
            } while (data.moveToNext());
            mLessons = tmpLessons;
        }
        return mLessons;
    }


    //Gets a lesson from the given position of the cursor provided.
    private static Lesson getLesson(Cursor cursor, SQLiteDatabase readableDatabase) {
        final String id = cursor.getString(0);
        final String name = cursor.getString(2);
        final String themeName = cursor.getString(3);
        final Theme theme = Theme.valueOf(themeName);
        final String isSolved = cursor.getString(4);
        final boolean solved = getBooleanFromDatabase(isSolved);
        final String score = cursor.getString(5);

        final List<Question> questions = getQuestions(id, readableDatabase);
        return new Lesson(name, id, theme, questions, solved, score);
    }

    private static boolean getBooleanFromDatabase(String isSolved) {
        // json stores booleans as true/false strings, whereas SQLite stores them as 0/1 values
        return null != isSolved && isSolved.length() == 1 && Integer.valueOf(isSolved) == 1;
    }

    // Get a lesson with gived lessonId
    public static Lesson getLessonWith(Context context, String lessonId) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {lessonId};
        Cursor data = readableDatabase.query(LessonTable.NAME, LessonTable.PROJECTION, LessonTable.COLUMN_ID + "=?", selectionArgs, null, null, null);
        data.moveToFirst();
        return getLesson(data, readableDatabase);
    }
    

    // Updates values for a lesson.
    public static void updateLesson(Context context, Lesson lesson) {
        if (mLessons != null && mLessons.contains(lesson)) {
            final int location = mLessons.indexOf(lesson);
            mLessons.remove(location);
            mLessons.add(location, lesson);
        }
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put(LessonTable.COLUMN_SOLVED, lesson.isSolved());
        contentValues.put(LessonTable.COLUMN_SCORE,lesson.getScore());
        writableDatabase.update(LessonTable.NAME, contentValues, LessonTable.COLUMN_ID + "=?",
                new String[]{lesson.getId()});
    }

    //Resets the contents of  database to it's initial state.
    public static void reset(Context context,String data) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        writableDatabase.delete(ChapterTable.NAME,null,null);
        writableDatabase.delete(LessonTable.NAME, null, null);
        writableDatabase.delete(QuestionTable.NAME, null, null);
        getInstance(context).fillDatabase(writableDatabase, data);
    }
    
    //update the database with the given data.
    public static void update(Context context,String data) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        getInstance(context).fillDatabase(writableDatabase, data);
    }

    //Creates objects for questions according to a lesson id.
    private static List<Question> getQuestions(final String lessonId, SQLiteDatabase database) {
        final List<Question> questions = new ArrayList<>();
        final Cursor cursor = database.query(QuestionTable.NAME, QuestionTable.PROJECTION,
                QuestionTable.FK_LESSON + " LIKE ?", new String[]{lessonId}, null, null, null);
        cursor.moveToFirst();
        do {
            questions.add(createQuestion(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return questions;
    }

    //Creates a question corresponding to the projection provided from a cursor row.
    private static Question createQuestion(Cursor cursor) {
        //based on QuestionTable's PROJECTION
        final String type = cursor.getString(2);
        final String question = cursor.getString(3);
        final String answer = cursor.getString(4);
        final String options = cursor.getString(5);

        switch (type) {
            case JsonParts.QuestionType.FILL_BLANK: {
                return createFillBlankQuestion(question, answer);
            }
            case JsonParts.QuestionType.MULTIPLE_CHOICE: {
                return createSelectItemQuestion(question, answer, options);
            }
            case JsonParts.QuestionType.SPEECH_INPUT:{
                return createSpeechInputQuestion(question, answer);
            }
            case JsonParts.QuestionType.CONTENT_TIP:{
                return createContentTipQuestion(question, answer);
            }
            case JsonParts.QuestionType.MAKE_SENTENCE:{
                return createMakeSentenceQuestion(question,answer);
            }
            default: {
                throw new IllegalArgumentException("Question type " + type + " is not supported");
            }
        }
    }

    private static Question createFillBlankQuestion(String question, String answer) {
        return new FillBlankQuestion(question, answer);
    }

    private static Question createSelectItemQuestion(String question, String answer, String options) {
        final String[] optionsArray = jsonArrayToStringArray(options);
        return new MultipleChoiceQuestion(question, Integer.parseInt(answer), optionsArray);
    }

    private static String[] jsonArrayToStringArray(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            String[] stringArray = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                stringArray[i] = jsonArray.getString(i);
            }
            return stringArray;
        } catch (JSONException e) {
            Log.e(TAG, "Error during Json processing: ", e);
        }
        return new String[0];
    }



    private static Question createSpeechInputQuestion(String question, String answer) {

        return new SpeechInputQuestion(question, answer);
    }

    private static Question createContentTipQuestion(String question, String answer) {

        return new ContentTipQuestion(question, answer);
    }

    private static Question createMakeSentenceQuestion(String question, String answer) {

        return new MakeSentenceQuestion(question, answer);
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create all three tables.
        db.execSQL(ChapterTable.CREATE);
        db.execSQL(LessonTable.CREATE);
        db.execSQL(QuestionTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion)
        {
            switch (upgradeTo)
            {
                case 2:
                    db.execSQL("DROP TABLE IF EXISTS " + LessonTable.NAME);
                    db.execSQL("DROP TABLE IF EXISTS " + QuestionTable.NAME);
                    onCreate(db);
                    break;
            }
            upgradeTo++;
        }
    }

    private void fillDatabase(SQLiteDatabase db,String data) {
        try {
            db.beginTransaction();
            try {
                fillChaptersAndLessonsAndQuestions(db,data);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "FillDatabase", e);
        }
    }

    public void fillChaptersAndLessonsAndQuestions(SQLiteDatabase db,String data) throws JSONException, IOException {
        ContentValues values = new ContentValues();
        JSONArray chapterArray = new JSONArray(data);
        JSONObject chapter,lesson;
        for (int i = 0; i < chapterArray.length(); i++) {
            chapter = chapterArray.getJSONObject(i);
            final String chapterId = chapter.getString(JsonParts.CID);
            final JSONArray lessonArray = chapter.getJSONArray(JsonParts.LESSONS);
            fillChapter(db, values, chapter, chapterId,lessonArray.length());

            for(int j=0;j<lessonArray.length();j++){
                lesson = lessonArray.getJSONObject(j);
                final String lessonId = lesson.getString(JsonParts.LID);
                fillLesson(db, values, lesson, lessonId);
                final JSONArray questions = lesson.getJSONArray(JsonParts.QUESTIONS);
                fillQuestionsForLesson(db, values, questions, lessonId);
            }
        }
    }

    // To get the data from raw folder.
//    private String readChaptersFromResources() throws IOException {
//        StringBuilder lessonsJson = new StringBuilder();
//        InputStream dataLessons = mResources.openRawResource(R.raw.data);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(dataLessons));
//        String line;
//
//        while ((line = reader.readLine()) != null) {
//            lessonsJson.append(line);
//        }
//        return lessonsJson.toString();
//    }
    private void fillChapter(SQLiteDatabase db, ContentValues values, JSONObject chapter,
                            String chapterId,int lessons) throws JSONException {
        values.clear();
        values.put(ChapterTable.COLUMN_ID, chapterId);
        values.put(ChapterTable.COLUMN_NAME, chapter.getString(JsonParts.CNAME));
        values.put(ChapterTable.COLUMN_LESSONS, lessons);
        db.insert(ChapterTable.NAME, null, values);
    }

    private void fillLesson(SQLiteDatabase db, ContentValues values, JSONObject lesson,
                              String lessonId) throws JSONException {
        values.clear();
        values.put(LessonTable.COLUMN_ID, lessonId);
        values.put(LessonTable.COLUMN_NAME, lesson.getString(JsonParts.LNAME));
        values.put(LessonTable.COLUMN_THEME, lesson.getString(JsonParts.THEME));
        values.put(LessonTable.COLUMN_SOLVED, lesson.getString(JsonParts.SOLVED));
        values.put(LessonTable.COLUMN_SCORE,lesson.getString(JsonParts.SCORE));
        db.insert(LessonTable.NAME, null, values);
    }

    private void fillQuestionsForLesson(SQLiteDatabase db, ContentValues values, JSONArray questions,
                                        String lessonId) throws JSONException {
        JSONObject question;
        for (int i = 0; i < questions.length(); i++) {
            question = questions.getJSONObject(i);
            values.clear();
            values.put(QuestionTable.FK_LESSON, lessonId);
            values.put(QuestionTable.COLUMN_TYPE, question.getString(JsonParts.TYPE));
            values.put(QuestionTable.COLUMN_QUESTION, question.getString(JsonParts.QUESTION));
            values.put(QuestionTable.COLUMN_ANSWER, question.getString(JsonParts.ANSWER));
            putNonEmptyString(values, question, JsonParts.OPTIONS, QuestionTable.COLUMN_OPTIONS);
            db.insert(QuestionTable.NAME, null, values);
        }
    }

    /**
     * Puts a non-empty string to ContentValues provided.
     *
     *
     * @param values The place where the data should be put.
     * @param question The question potentially containing the data.
     * @param jsonKey The key to look for.
     * @param contentKey The key use for placing the data in the database.
     * @throws JSONException Thrown when there's an issue with JSON.
     */
    private void putNonEmptyString(ContentValues values, JSONObject question, String jsonKey,
                                   String contentKey) throws JSONException {
        final String stringToPut = question.optString(jsonKey, null);
        if (!TextUtils.isEmpty(stringToPut)) {
            values.put(contentKey, stringToPut);
        }
    }

}
