package com.algorithmandblues.lightsout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GameWinStateDBHandler {

    private static final String TAG = GameWinStateDBHandler.class.getSimpleName();

    private static GameWinStateDBHandler sInstance;
    private DatabaseHelper databaseHelper;

    private GameWinStateDBHandler(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public static synchronized GameWinStateDBHandler getInstance (DatabaseHelper databaseHelper) {
        if (sInstance == null) {
            sInstance = new GameWinStateDBHandler(databaseHelper);
        }
        return sInstance;
    }

    public int insertGameWinStateObjectToDatabase(GameWinState gameWinState) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.GameWinStateTable.TABLE_NAME;
        ContentValues contentValues = getContentValuesFromGameWinStateObject(gameWinState);
        int insertedId = (int) db.insert(tableName, null, contentValues);
        db.close();
        Log.d(TAG, "Added new game win state to database: " + gameWinState.toString() + "; new id: " + insertedId);
        return insertedId;
    }

    public List<GameWinState> fetchAllGameWinStatesInReverseChronological(int gameMode) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        String tableName = DatabaseConstants.GameWinStateTable.TABLE_NAME;
        String[] columns = DatabaseConstants.GameWinStateTable.TABLE_COLUMNS;
        String selection = DatabaseConstants.GameWinStateTable.GAME_MODE + " = ? " + " AND "
                + DatabaseConstants.GameWinStateTable.NUMBER_OF_STARS + " > ?";
        //we do not want games where the user got a zero-score; it means they cheated
        String[] selectionArgs = {String.valueOf(gameMode), String.valueOf(0)};
        String orderBy = DatabaseConstants.GameWinStateTable.TIME_STAMP_MS + " DESC";

        List<GameWinState> gameWinStates = new ArrayList<>();
        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, orderBy);

        //ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, ORIGINAL_BULB_CONFIGURATION, NUMBER_OF_MOVES,
        //NUMBER_OF_HINTS_USED, NUMBER_OF_STARS, GAME_MODE, TIME_STAMP_MS
        if (cursor.moveToFirst()) {
            do {
                GameWinState gameWinState = new GameWinState(){{
                    setId(cursor.getInt(0));
                    setDimension(cursor.getInt(1));
                    setOriginalStartState(cursor.getString(2));
                    setToggledBulbs(cursor.getString(3));
                    setOriginalBulbConfiguration(cursor.getString(4));
                    setNumberOfMoves(cursor.getInt(5));
                    setNumberOfHintsUsed(cursor.getInt(6));
                    setNumberOfStars(cursor.getInt(7));
                    setGameMode(cursor.getInt(8));
                    setTimeStampMs(cursor.getLong(9));
                    setMoveCounterPerBulbString(cursor.getString(10));
                    setOriginalBoardPower(cursor.getInt(11));
                }};
                gameWinStates.add(gameWinState);
            } while (cursor.moveToNext());
        }
        db.close();
        Log.d(TAG, "fetched all game win states of game mode type " + gameMode);
        Log.d(TAG, gameWinStates.toString().replace("}", "}\n"));
        return gameWinStates;
    }

    public int clearGameWinStats() {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.GameWinStateTable.TABLE_NAME;

        //According to Android documentation of SQLite open helper:
        // https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#delete(java.lang.String,%20java.lang.String,%20java.lang.String[])
        // To delete all rows and get a count pass, we should pass "1" as the whereClause string param
        int deletedNumberOfGamesWon = db.delete(tableName, "1", null);
        db.close();
        Log.d(TAG, "Deleted " + deletedNumberOfGamesWon + " rows from table " + tableName);
        return deletedNumberOfGamesWon;
    }

    private ContentValues getContentValuesFromGameWinStateObject(GameWinState gameWinState) {
        //ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, ORIGINAL_BULB_CONFIGURATION, NUMBER_OF_MOVES,
        //NUMBER_OF_HINTS_USED, NUMBER_OF_STARS, GAME_MODE, TIME_STAMP_MS
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.GameWinStateTable.DIMENSION, gameWinState.getDimension());
        contentValues.put(DatabaseConstants.GameWinStateTable.ORIGINAL_START_STATE, gameWinState.getOriginalStartState());
        contentValues.put(DatabaseConstants.GameWinStateTable.TOGGLED_BULBS, gameWinState.getToggledBulbs());
        contentValues.put(DatabaseConstants.GameWinStateTable.ORIGINAL_BULB_CONFIGURATION, gameWinState.getOriginalBulbConfiguration());
        contentValues.put(DatabaseConstants.GameWinStateTable.NUMBER_OF_MOVES, gameWinState.getNumberOfMoves());
        contentValues.put(DatabaseConstants.GameWinStateTable.NUMBER_OF_HINTS_USED, gameWinState.getNumberOfHintsUsed());
        contentValues.put(DatabaseConstants.GameWinStateTable.NUMBER_OF_STARS, gameWinState.getNumberOfStars());
        contentValues.put(DatabaseConstants.GameWinStateTable.GAME_MODE, gameWinState.getGameMode());
        contentValues.put(DatabaseConstants.GameWinStateTable.TIME_STAMP_MS, gameWinState.getTimeStampMs());
        contentValues.put(DatabaseConstants.GameWinStateTable.MOVE_COUNTER_PER_BULB_STRING, gameWinState.getMoveCounterPerBulbString());
        contentValues.put(DatabaseConstants.GameWinStateTable.ORIGINAL_BOARD_POWER, gameWinState.getOriginalBoardPower());
        return contentValues;
    }

}
