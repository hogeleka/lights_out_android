package com.algorithmandblues.lightsout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GameWinStateDao {

    private static final String TAG = GameWinStateDao.class.getSimpleName();

    private static GameWinStateDao sInstance;
    private DatabaseHelper databaseHelper;

    private GameWinStateDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public static synchronized  GameWinStateDao getInstance (DatabaseHelper databaseHelper) {
        if (sInstance == null) {
            sInstance = new GameWinStateDao(databaseHelper);
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
        String selection = DatabaseConstants.GameWinStateTable.GAME_MODE + " = ? ";
        String[] selectionArgs = {String.valueOf(gameMode)};
        String orderBy = DatabaseConstants.GameWinStateTable.TIME_STAMP_MS + " DESC";

        List<GameWinState> gameWinStates = new ArrayList<>();
        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, orderBy);

        //ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, NUMBER_OF_MOVES, NUMBER_OF_STARS, GAME_MODE, TIME_STAMP_MS
        if (cursor.moveToFirst()) {
            do {
                GameWinState gameWinState = new GameWinState(){{
                    setId(cursor.getInt(0));
                    setDimension(cursor.getInt(1));
                    setOriginalStartState(cursor.getString(2));
                    setToggledBulbs(cursor.getString(3));
                    setNumberOfMoves(cursor.getInt(4));
                    setNumberOfStars(cursor.getInt(5));
                    setGameMode(cursor.getInt(6));
                    setTimeStampMs(cursor.getLong(7));
                }};
                gameWinStates.add(gameWinState);
            } while (cursor.moveToNext());
        }
        db.close();
        Log.d(TAG, "fetched all game win states of game mode type " + gameMode);
        Log.d(TAG, gameWinStates.toString());
        return gameWinStates;
    }

    public int clearGameWinStats() {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.GameWinStateTable.TABLE_NAME;
        int deletedNumberOfGamesWon = db.delete(tableName, "1", null);
        db.close();
        Log.d(TAG, "Deleted " + deletedNumberOfGamesWon + " rows from table " + tableName);
        return deletedNumberOfGamesWon;
    }

    private ContentValues getContentValuesFromGameWinStateObject(GameWinState gameWinState) {
        //ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, NUMBER_OF_MOVES, NUMBER_OF_STARS, GAME_MODE, TIME_STAMP_MS
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.GameWinStateTable.DIMENSION, gameWinState.getDimension());
        contentValues.put(DatabaseConstants.GameWinStateTable.ORIGINAL_START_STATE, gameWinState.getOriginalStartState());
        contentValues.put(DatabaseConstants.GameWinStateTable.TOGGLED_BULBS, gameWinState.getToggledBulbs());
        contentValues.put(DatabaseConstants.GameWinStateTable.NUMBER_OF_MOVES, gameWinState.getNumberOfMoves());
        contentValues.put(DatabaseConstants.GameWinStateTable.NUMBER_OF_STARS, gameWinState.getNumberOfStars());
        contentValues.put(DatabaseConstants.GameWinStateTable.GAME_MODE, gameWinState.getGameMode());
        contentValues.put(DatabaseConstants.GameWinStateTable.TIME_STAMP_MS, gameWinState.getTimeStampMs());
        return contentValues;

    }

}
