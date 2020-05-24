package com.algorithmandblues.lightsout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = DatabaseHelper.class.getSimpleName();

    private static DatabaseHelper sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LightsOutDB";
    private static final String GAME_BOARD_TABLE = "Board";
    private static final String KEY_ID_DIMENSION = "idDimension";
    private static final String ORIGINAL_START_STATE = "originalStartState";
    private static final String TOGGLED_BULBS = "toggledBulbs";
    private static final String UNDO_STACK_STRING = "undoStackString";
    private static final String REDO_STACK_STRING = "redoStackString";

    private static final String CURRENT_BOARD_PREFERENCE_TABLE = "CurrentBoardPreference";
    private static final String KEY_ID_SHOULD_RANDOMIZE_STATE = "idRandomizeState";
    private static final int ROW_ID_SHOULD_RANDOMIZE = 1;
    private static final String KEY_SHOULD_RANDOMIZE = "randomize";

    private static final String[] GAME_BOARD_TABLE_COLUMNS = {
            KEY_ID_DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, UNDO_STACK_STRING, REDO_STACK_STRING
    };

    private static final String[] COLUMNS_RANDOMIZE_TABLE = {KEY_ID_SHOULD_RANDOMIZE_STATE, KEY_SHOULD_RANDOMIZE};

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        for (Map.Entry<String, String> entry : DatabaseConstants.databaseTableNamesAndCreationStrings.entrySet()) {
            String sqlQuery = entry.getValue();
            db.execSQL(sqlQuery);
        }
        Log.d(TAG, "Created " + DatabaseConstants.databaseTableNamesAndColumns.entrySet().size()
        + " tables: " + DatabaseConstants.databaseTableNamesAndColumns.values().toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GAME_BOARD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CURRENT_BOARD_PREFERENCE_TABLE);
        this.onCreate(db);
    }



    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String tableName : DatabaseConstants.databaseTableNamesAndColumns.keySet()) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        Log.d(TAG, "Cleared all tables from database");
        this.onCreate(db);
        db.close();
    }
}

