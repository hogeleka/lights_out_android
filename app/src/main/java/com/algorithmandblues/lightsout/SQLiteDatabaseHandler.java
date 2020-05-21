package com.algorithmandblues.lightsout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static String TAG = SQLiteDatabaseHandler.class.getSimpleName();

    private static SQLiteDatabaseHandler sInstance;

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

    public static synchronized SQLiteDatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteDatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    private SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LIGHTSOUT_TABLE = "CREATE TABLE " + GAME_BOARD_TABLE + " ( " + KEY_ID_DIMENSION + " INTEGER PRIMARY KEY, " + ORIGINAL_START_STATE + " TEXT, " + TOGGLED_BULBS + " TEXT, " + UNDO_STACK_STRING + " TEXT, " + REDO_STACK_STRING + " TEXT )";
        db.execSQL(CREATE_LIGHTSOUT_TABLE);
        String CREATE_RANDOMIZE_STATE_TABLE = "CREATE TABLE " + CURRENT_BOARD_PREFERENCE_TABLE + " ( " + KEY_ID_SHOULD_RANDOMIZE_STATE + " INTEGER PRIMARY KEY, " + KEY_SHOULD_RANDOMIZE + " INTEGER )";
        db.execSQL(CREATE_RANDOMIZE_STATE_TABLE);
        Log.d(TAG, "Created two tables: " + GAME_BOARD_TABLE + " and " + CURRENT_BOARD_PREFERENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GAME_BOARD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CURRENT_BOARD_PREFERENCE_TABLE);
        this.onCreate(db);
    }

    public boolean checkPreferenceForRandomState() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + CURRENT_BOARD_PREFERENCE_TABLE +" WHERE " + KEY_ID_SHOULD_RANDOMIZE_STATE + " = " + Integer.toString(ROW_ID_SHOULD_RANDOMIZE) + " LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        int result;
        if (cursor.getCount() != 0) {
            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getInt(1);
                    Log.d(TAG, "Checked for Randomize: Found preference in DB: " + Integer.toString(result));
                    i++;
                } while (i<1);
            } else {
                result = 0;
            }
        } else {
            result = 0;
            Log.d(TAG, "Checked for Randomize: " + Integer.toString(result) + " --No Preference found in db");
        }
        db.close();
        return result==1;
    }

    public boolean haveSavedPreferenceForRandomize() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                CURRENT_BOARD_PREFERENCE_TABLE, COLUMNS_RANDOMIZE_TABLE, " " + KEY_ID_SHOULD_RANDOMIZE_STATE + " = ?", new String[] {String.valueOf(ROW_ID_SHOULD_RANDOMIZE)}, null, null, null, null, null);
        return cursor.getCount() != 0;
    }

    public void updateSetRandomStatePreference(boolean setRandomStatePreference) {
        SQLiteDatabase db = this.getWritableDatabase();
        int updateVal = setRandomStatePreference ? 1 : 0;
        ContentValues values = new ContentValues();
        values.put(KEY_SHOULD_RANDOMIZE, updateVal);
        if (!haveSavedPreferenceForRandomize()) {
            values.put(KEY_ID_SHOULD_RANDOMIZE_STATE, ROW_ID_SHOULD_RANDOMIZE);
            long id = db.insert(CURRENT_BOARD_PREFERENCE_TABLE, null, values);
            Log.d(TAG, "Inserted preference of Randomization with value " + Integer.toString(updateVal) );
        } else {
            values.put(KEY_SHOULD_RANDOMIZE, updateVal);
            int i = db.update(CURRENT_BOARD_PREFERENCE_TABLE, values, KEY_ID_SHOULD_RANDOMIZE_STATE + " = ?", new String[] {String.valueOf(ROW_ID_SHOULD_RANDOMIZE)});
            Log.d(TAG, "Updated Randomization preference with value "  + Integer.toString(updateVal));
        }
        db.close();
    }

    public GameData getGameData(int dimension) {
        Log.d(TAG, "Fetching game data for dimension size: " + Integer.toString(dimension) + " by " + Integer.toString(dimension));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                GAME_BOARD_TABLE, GAME_BOARD_TABLE_COLUMNS, " " + KEY_ID_DIMENSION + " = ?", new String[] {String.valueOf(dimension)}, null, null, null, null, null);

        if (cursor.getCount() == 0) {
            Log.d(TAG, "Nothing found in database for " + Integer.toString(dimension) + " by " + Integer.toString(dimension));
            return null;
        } else {
            cursor.moveToFirst();
            GameData gameData = new GameData();
            gameData.setDimension(Integer.parseInt(cursor.getString(0)));
            gameData.setOriginalStartState(cursor.getString(1));
            gameData.setToggledBulbsState(cursor.getString(2));
            gameData.setUndoStackString(cursor.getString(3));
            gameData.setRedoStackString(cursor.getString(4));
            Log.d(TAG, "Found data in database for " + Integer.toString(dimension) + " by " + Integer.toString(dimension) + "-- " + gameData.toString());
            return gameData;
        }
    }


    public void addGameData(GameData gameData) {
        if (this.getGameData(gameData.getDimension()) == null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ID_DIMENSION, gameData.getDimension());
            values.put(ORIGINAL_START_STATE, gameData.getOriginalStartState());
            values.put(TOGGLED_BULBS, gameData.getToggledBulbsState());
            values.put(UNDO_STACK_STRING, gameData.getUndoStackString());
            values.put(REDO_STACK_STRING, gameData.getRedoStackString());
            db.insert(GAME_BOARD_TABLE, null, values);
            db.close();
            Log.d(TAG, "Inserted data for " + Integer.toString(gameData.getDimension()) + " by " + Integer.toString(gameData.getDimension()) + " into database: " + gameData.toString());
        } else {
            this.updateGameData(gameData);
        }
    }


    private int updateGameData(GameData gameData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ORIGINAL_START_STATE, gameData.getOriginalStartState());
        values.put(TOGGLED_BULBS, gameData.getToggledBulbsState());
        values.put(UNDO_STACK_STRING, gameData.getUndoStackString());
        values.put(REDO_STACK_STRING, gameData.getRedoStackString());

        int i = db.update(GAME_BOARD_TABLE, values, KEY_ID_DIMENSION + " = ?", new String[] {String.valueOf(gameData.getDimension())});

        Log.d(TAG, "Updated game data for " + Integer.toString(gameData.getDimension()) + " by " + Integer.toString(gameData.getDimension()) + " with value: " + gameData.toString());
        db.close();
        return i;
    }

    public void deleteRowForSpecificDimension(int dimension) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GAME_BOARD_TABLE,
                KEY_ID_DIMENSION + " = ? ",
                new String[] { Integer.toString(dimension) });
        db.close();
        Log.d(TAG, "Cleared data for " + Integer.toString(dimension) + " by " + Integer.toString(dimension) + " from database");
    }

    public List<GameData> allGameDataInstancesForAllBoardSizes() {
        List<GameData> gameDataList = new LinkedList<>();
        String query = "SELECT * FROM " + GAME_BOARD_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        GameData gameData = null;
        if (cursor.moveToFirst()) {
            do {
                gameData = new GameData();
                gameData.setDimension(Integer.parseInt(cursor.getString(0)));
                gameData.setOriginalStartState(cursor.getString(1));
                gameData.setToggledBulbsState(cursor.getString(2));
                gameData.setUndoStackString(cursor.getString(3));
                gameData.setRedoStackString(cursor.getString(4));
                gameDataList.add(gameData);

            } while (cursor.moveToNext());
        }

        return gameDataList;
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + GAME_BOARD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CURRENT_BOARD_PREFERENCE_TABLE);
        Log.d(TAG, "Cleared all databases");
        this.onCreate(db);
        db.close();
    }
}
