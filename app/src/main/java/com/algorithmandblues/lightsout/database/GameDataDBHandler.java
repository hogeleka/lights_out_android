package com.algorithmandblues.lightsout.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameDataDBHandler {

    private static final String TAG = GameDataDBHandler.class.getSimpleName();

    private static GameDataDBHandler sInstance;
    private DatabaseHelper databaseHelper;

    private GameDataDBHandler(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public static synchronized GameDataDBHandler getInstance(DatabaseHelper databaseHelper) {
        if (sInstance == null) {
            sInstance = new GameDataDBHandler(databaseHelper);
        }
        return sInstance;
    }

    public GameData getMostRecentGameDataForGameType(int dimension, int gameMode) {
        Log.d(TAG, "Fetching game data for dimension size: " + dimension + " by " + dimension + " of game type " + gameMode);
        boolean distinct = true;
        String tableName = DatabaseConstants.MostRecentGameTable.TABLE_NAME;
        String[] columns = DatabaseConstants.MostRecentGameTable.TABLE_COLUMNS;
        String whereClause = DatabaseConstants.MostRecentGameTable.DIMENSION + " = ? AND "
                + DatabaseConstants.MostRecentGameTable.GAME_MODE + " = ? ";
        String[] selectionArgs = {String.valueOf(dimension), String.valueOf(gameMode)};
        String limit = String.valueOf(1);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(distinct, tableName, columns, whereClause, selectionArgs, null, null, null, limit);

        if (cursor.getCount() == 0) {
            Log.d(TAG, "Nothing found in database for dimension " + dimension + " of game type: " + gameMode);
            return null;
        } else {
            // ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, UNDO_STACK_STRING,
            // REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED,
            // MOVE_COUNTER_PER_BULB_STRING, ORIGINAL_INDIVIDUAL_BULB_STATUS
            cursor.moveToFirst();
            GameData gameData = new GameData();
            gameData.setId(cursor.getInt(0));
            gameData.setDimension(cursor.getInt(1));
            gameData.setOriginalStartState(cursor.getString(2));
            gameData.setToggledBulbsState(cursor.getString(3));
            gameData.setUndoStackString(cursor.getString(4));
            gameData.setRedoStackString(cursor.getString(5));
            gameData.setGameMode(cursor.getInt(6));
            gameData.setHasSeenSolution(cursor.getInt(7) == 1);
            gameData.setMoveCounter(cursor.getInt(8));
            gameData.setNumberOfHintsUsed(cursor.getInt(9));
            gameData.setMoveCounterPerBulbString(cursor.getString(10));
            gameData.setOriginalIndividualBulbStatus(cursor.getString(11));
            Log.d(TAG, "Found data in database for dimension " + dimension + "of game type " + gameMode + "--" + gameData.toString());
            db.close();
            return gameData;
        }
    }

    public void addGameDataObjectToDatabase(GameData gameData) {
        int dimension = gameData.getDimension();
        int gameMode = gameData.getGameMode();
        ContentValues contentValues = this.getContentValuesFromGameDataObject(gameData);
        if (this.getMostRecentGameDataForGameType(dimension, gameMode) == null) {
            this.insertGameDatabaseObjectToDatabase(gameData, contentValues);
        } else {
            this.updateGameDatabaseObjectToDatabase(gameData, contentValues);
        }
    }

    private ContentValues getContentValuesFromGameDataObject(GameData gameData) {
        // ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, UNDO_STACK_STRING,
        // REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED, MOVE_COUNTER_PER_BULB_STRING
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.MostRecentGameTable.DIMENSION, gameData.getDimension());
        contentValues.put(DatabaseConstants.MostRecentGameTable.ORIGINAL_START_STATE, gameData.getOriginalStartState());
        contentValues.put(DatabaseConstants.MostRecentGameTable.TOGGLED_BULBS, gameData.getToggledBulbsState());
        contentValues.put(DatabaseConstants.MostRecentGameTable.UNDO_STACK_STRING, gameData.getUndoStackString());
        contentValues.put(DatabaseConstants.MostRecentGameTable.REDO_STACK_STRING, gameData.getRedoStackString());
        contentValues.put(DatabaseConstants.MostRecentGameTable.GAME_MODE, gameData.getGameMode());
        contentValues.put(DatabaseConstants.MostRecentGameTable.HAS_SEEN_SOLUTION, gameData.getHasSeenSolution() ? 1 : 0);
        contentValues.put(DatabaseConstants.MostRecentGameTable.MOVE_COUNTER, gameData.getMoveCounter());
        contentValues.put(DatabaseConstants.MostRecentGameTable.NUMBER_OF_HINTS_USED, gameData.getNumberOfHintsUsed());
        contentValues.put(DatabaseConstants.MostRecentGameTable.MOVE_COUNTER_PER_BULB_STRING, gameData.getMoveCounterPerBulbString());
        contentValues.put(DatabaseConstants.MostRecentGameTable.ORIGINAL_INDIVIDUAL_BULB_STATUS, gameData.getOriginalIndividualBulbStatus());
        return contentValues;
    }


    private void updateGameDatabaseObjectToDatabase(GameData gameData, ContentValues contentValues) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.MostRecentGameTable.TABLE_NAME;
        String whereClause = DatabaseConstants.MostRecentGameTable.DIMENSION + " = ? AND "
                                + DatabaseConstants.MostRecentGameTable.GAME_MODE + " = ?";
        String[] whereArgs = {String.valueOf(gameData.getDimension()), String.valueOf(gameData.getGameMode())};
        db.update(tableName, contentValues, whereClause, whereArgs);
        Log.d(TAG, "Updated database for dimension " + gameData.getDimension() + " of game mode " + gameData.getGameMode()
        + " with value: " + gameData.toString());
        db.close();

    }

    private void insertGameDatabaseObjectToDatabase(GameData gameData, ContentValues contentValues) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.MostRecentGameTable.TABLE_NAME;
        db.insert(tableName, null, contentValues);
        Log.d(TAG, "Added row to database for dimension " + gameData.getDimension() + " of game mode " + gameData.getGameMode()
                + " with value: " + gameData.toString());
        db.close();
    }

    public void deleteMostRecentGameDataObjectForDimensionAndGameMode(int dimension, int gameMode) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.MostRecentGameTable.TABLE_NAME;
        String whereClause = DatabaseConstants.MostRecentGameTable.DIMENSION + " = ? AND "
                + DatabaseConstants.MostRecentGameTable.GAME_MODE + " = ? ";
        String[] whereArgs = {String.valueOf(dimension), String.valueOf(gameMode)};
        db.delete(tableName, whereClause, whereArgs);
        db.close();
        Log.d(TAG, "Cleared data for " + dimension + " by " + dimension + " and game mode " + gameMode + " from database");
    }
}
