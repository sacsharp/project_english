package com.sigrideducation.englishlearning.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.helper.JsonHelper;
import com.sigrideducation.englishlearning.model.JsonAttributes;
import com.sigrideducation.englishlearning.model.Lesson;
import com.sigrideducation.englishlearning.model.Theme;
import com.sigrideducation.englishlearning.model.question.ContentTipQuestion;
import com.sigrideducation.englishlearning.model.question.FillBlankQuestion;
import com.sigrideducation.englishlearning.model.question.MakeSentenceQuestion;
import com.sigrideducation.englishlearning.model.question.Question;
import com.sigrideducation.englishlearning.model.question.SelectItemQuestion;
import com.sigrideducation.englishlearning.model.question.SpeechInputQuestion;
import com.sigrideducation.englishlearning.model.question.TrueFalseQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private final Resources mResources;

    private ELDatabaseHelper(Context context) {
        //prevents external instance creation
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
        mResources = context.getResources();
    }



    private static ELDatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new ELDatabaseHelper(context);
        }
        return mInstance;
    }

    /**
     * Gets all lessons with their questions.
     *
     * @param context The context this is running in.
     * @param fromDatabase <code>true</code> if a data refresh is needed, else <code>false</code>.
     * @return All lessons stored in the database.
     */
    public static List<Lesson> getLessons(Context context, boolean fromDatabase) {
        if (null == mLessons || fromDatabase) {
            mLessons = loadLessons(context);
        }
        return mLessons;
    }

    private static List<Lesson> loadLessons(Context context) {
        Cursor data = ELDatabaseHelper.getCategoryCursor(context);
        List<Lesson> tmpLessons = new ArrayList<>(data.getCount());
        final SQLiteDatabase readableDatabase = ELDatabaseHelper.getReadableDatabase(context);
        do {
            final Lesson lesson = getLesson(data, readableDatabase);
            tmpLessons.add(lesson);
        } while (data.moveToNext());
        return tmpLessons;
    }


    /**
     * Gets all lessons wrapped in a {@link Cursor} positioned at it's first element.
     * <p>There are <b>no questions</b> within the lessons obtained from this cursor</p>
     *
     * @param context The context this is running in.
     * @return All lessons stored in the database.
     */
    private static Cursor getCategoryCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        Cursor data = readableDatabase
                .query(LessonTable.NAME, LessonTable.PROJECTION, null, null, null, null, null);
        data.moveToFirst();
        return data;
    }

    /**
     * Gets a lesson from the given position of the cursor provided.
     *
     * @param cursor The Cursor containing the data.
     * @param readableDatabase The database that contains the questions.
     * @return The found lesson.
     */
    private static Lesson getLesson(Cursor cursor, SQLiteDatabase readableDatabase) {
        // "magic numbers" based on LessonTable#PROJECTION
        final String id = cursor.getString(0);
        final String name = cursor.getString(1);
        final String themeName = cursor.getString(2);
        final Theme theme = Theme.valueOf(themeName);
        final String isSolved = cursor.getString(3);
        final boolean solved = getBooleanFromDatabase(isSolved);

        final List<Question> questions = getQuizzes(id, readableDatabase);
        return new Lesson(name, id, theme, questions, solved);
    }

    private static boolean getBooleanFromDatabase(String isSolved) {
        // json stores booleans as true/false strings, whereas SQLite stores them as 0/1 values
        return null != isSolved && isSolved.length() == 1 && Integer.valueOf(isSolved) == 1;
    }

    /**
     * Looks for a lesson with a given id.
     *
     * @param context The context this is running in.
     * @param lessonId Id of the lesson to look for.
     * @return The found lesson.
     */
    public static Lesson getCategoryWith(Context context, String lessonId) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {lessonId};
        Cursor data = readableDatabase
                .query(LessonTable.NAME, LessonTable.PROJECTION, LessonTable.COLUMN_ID + "=?",
                        selectionArgs, null, null, null);
        data.moveToFirst();
        return getLesson(data, readableDatabase);
    }
    

    /**
     * Updates values for a lesson.
     *
     * @param context The context this is running in.
     * @param lesson The lesson to update.
     */
    public static void updateLesson(Context context, Lesson lesson) {
        if (mLessons != null && mLessons.contains(lesson)) {
            final int location = mLessons.indexOf(lesson);
            mLessons.remove(location);
            mLessons.add(location, lesson);
        }
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        ContentValues lessonValues = createContentValuesFor(lesson);
        writableDatabase.update(LessonTable.NAME, lessonValues, LessonTable.COLUMN_ID + "=?",
                new String[]{lesson.getId()});
        final List<Question> questions = lesson.getQuestions();
        updateQuestions(writableDatabase, questions);
    }

    /**
     * Updates a list of given questions.
     *
     * @param writableDatabase The database to write the questions to.
     * @param questions The questions to write.
     */
    private static void updateQuestions(SQLiteDatabase writableDatabase, List<Question> questions) {
        Question question;
        ContentValues questionValues = new ContentValues();
        String[] questionArgs = new String[1];
        for (int i = 0; i < questions.size(); i++) {
            question = questions.get(i);
            questionValues.clear();
            questionValues.put(QuestionTable.COLUMN_SOLVED, question.isSolved());

            questionArgs[0] = question.getQuestion();
            writableDatabase.update(QuestionTable.NAME, questionValues, QuestionTable.COLUMN_QUESTION + "=?",
                    questionArgs);
        }
    }

    /**
     * Resets the contents of englishLearning's database to it's initial state.
     *
     * @param context The context this is running in.
     */
    public static void reset(Context context) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        writableDatabase.delete(LessonTable.NAME, null, null);
        writableDatabase.delete(QuestionTable.NAME, null, null);
        getInstance(context).preFillDatabase(writableDatabase);
    }

    /**
     * Creates objects for questions according to a lesson id.
     *
     * @param lessonId The lesson to create questions for.
     * @param database The database containing the questions.
     * @return The found questions or an empty list if none were available.
     */
    private static List<Question> getQuizzes(final String lessonId, SQLiteDatabase database) {
        final List<Question> questions = new ArrayList<>();
        final Cursor cursor = database.query(QuestionTable.NAME, QuestionTable.PROJECTION,
                QuestionTable.FK_LESSON + " LIKE ?", new String[]{lessonId}, null, null, null);
        cursor.moveToFirst();
        do {
            questions.add(createQuizDueToType(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return questions;
    }

    /**
     * Creates a question corresponding to the projection provided from a cursor row.
     * Currently only {@link QuestionTable#PROJECTION} is supported.
     *
     * @param cursor The Cursor containing the data.
     * @return The created question.
     */
    private static Question createQuizDueToType(Cursor cursor) {
        // "magic numbers" based on QuestionTable#PROJECTION
        final String type = cursor.getString(2);
        final String question = cursor.getString(3);
        final String answer = cursor.getString(4);
        final String options = cursor.getString(5);
        final boolean solved = getBooleanFromDatabase(cursor.getString(8));

        switch (type) {
            case JsonAttributes.QuestionType.FILL_BLANK: {
                return createFillBlankQuestion(cursor, question, answer, solved);
            }
            case JsonAttributes.QuestionType.SINGLE_SELECT_ITEM: {
                return createSelectItemQuestion(question, answer, options, solved);
            }
            case JsonAttributes.QuestionType.TRUE_FALSE: {
                return createTrueFalseQuestion(question, answer, solved);
            }
            case JsonAttributes.QuestionType.SPEECH_INPUT:{
                return createSpeechInputQuestion(question, answer, solved);
            }
            case JsonAttributes.QuestionType.CONTENT_TIP:{
                return createContentTipQuestion(question, answer, solved);
            }
            case JsonAttributes.QuestionType.MAKE_SENTENCE:{
                return createMakeSentenceQuestion(question,answer,solved);
            }
            default: {
                throw new IllegalArgumentException("Question type " + type + " is not supported");
            }
        }
    }

    private static Question createFillBlankQuestion(Cursor cursor, String question,
                                            String answer, boolean solved) {
        final String start = cursor.getString(6);
        final String end = cursor.getString(7);
        return new FillBlankQuestion(question, answer, start, end, solved);
    }

    private static Question createSelectItemQuestion(String question, String answer,
                                             String options, boolean solved) {
        final int[] answerArray = JsonHelper.jsonArrayToIntArray(answer);
        final String[] optionsArray = JsonHelper.jsonArrayToStringArray(options);
        return new SelectItemQuestion(question, answerArray, optionsArray, solved);
    }
    

    private static Question createTrueFalseQuestion(String question, String answer, boolean solved) {
    /*
     * parsing json with the potential values "true" and "false"
     * see res/raw/lessons.json for reference
     */
        final boolean answerValue = "true".equals(answer);
        return new TrueFalseQuestion(question, answerValue, solved);
    }

    private static Question createSpeechInputQuestion(String question, String answer, boolean solved) {

        return new SpeechInputQuestion(question, answer, solved);
    }

    private static Question createContentTipQuestion(String question, String answer, boolean solved) {

        return new ContentTipQuestion(question, answer, solved);
    }

    private static Question createMakeSentenceQuestion(String question, String answer, boolean solved) {

        return new MakeSentenceQuestion(question, answer, solved);
    }

    /**
     * Creates the content values to update a lesson in the database.
     *
     * @param lesson The lesson to update.
     * @return ContentValues containing updatable data.
     */
    private static ContentValues createContentValuesFor(Lesson lesson) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LessonTable.COLUMN_SOLVED, lesson.isSolved());
        return contentValues;
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * create the lesson table first, as question table has a foreign key
         * constraint on lesson id
         */
        db.execSQL(LessonTable.CREATE);
        db.execSQL(QuestionTable.CREATE);
        preFillDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* no-op */
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

    private void preFillDatabase(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                fillLessonsAndQuestions(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "preFillDatabase", e);
        }
    }

    private void fillLessonsAndQuestions(SQLiteDatabase db) throws JSONException, IOException {
        ContentValues values = new ContentValues(); // reduce, reuse
        JSONArray jsonArray = new JSONArray(readLessonsFromResources());
        JSONObject lesson;
        for (int i = 0; i < jsonArray.length(); i++) {
            lesson = jsonArray.getJSONObject(i);
            final String lessonId = lesson.getString(JsonAttributes.ID);
            fillLesson(db, values, lesson, lessonId);
            final JSONArray questions = lesson.getJSONArray(JsonAttributes.QUIZZES);
            fillQuestionsForLesson(db, values, questions, lessonId);
        }
    }

    private String readLessonsFromResources() throws IOException {
        StringBuilder lessonsJson = new StringBuilder();
        InputStream dataLessons = mResources.openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataLessons));
        String line;

        while ((line = reader.readLine()) != null) {
            lessonsJson.append(line);
        }
        return lessonsJson.toString();
    }

    private void fillLesson(SQLiteDatabase db, ContentValues values, JSONObject lesson,
                              String lessonId) throws JSONException {
        values.clear();
        values.put(LessonTable.COLUMN_ID, lessonId);
        values.put(LessonTable.COLUMN_NAME, lesson.getString(JsonAttributes.NAME));
        values.put(LessonTable.COLUMN_THEME, lesson.getString(JsonAttributes.THEME));
        values.put(LessonTable.COLUMN_SOLVED, lesson.getString(JsonAttributes.SOLVED));
        db.insert(LessonTable.NAME, null, values);
    }

    private void fillQuestionsForLesson(SQLiteDatabase db, ContentValues values, JSONArray questions,
                                        String lessonId) throws JSONException {
        JSONObject question;
        for (int i = 0; i < questions.length(); i++) {
            question = questions.getJSONObject(i);
            values.clear();
            values.put(QuestionTable.FK_LESSON, lessonId);
            values.put(QuestionTable.COLUMN_TYPE, question.getString(JsonAttributes.TYPE));
            values.put(QuestionTable.COLUMN_QUESTION, question.getString(JsonAttributes.QUESTION));
            values.put(QuestionTable.COLUMN_ANSWER, question.getString(JsonAttributes.ANSWER));
            putNonEmptyString(values, question, JsonAttributes.OPTIONS, QuestionTable.COLUMN_OPTIONS);
            putNonEmptyString(values, question, JsonAttributes.START, QuestionTable.COLUMN_START);
            putNonEmptyString(values, question, JsonAttributes.END, QuestionTable.COLUMN_END);
            db.insert(QuestionTable.NAME, null, values);
        }
    }

    /**
     * Puts a non-empty string to ContentValues provided.
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
