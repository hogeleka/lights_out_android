package com.algorithmandblues.lightsout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

class GameDataObjectDBHandler {

    private static final String TAG = GameDataObjectDBHandler.class.getSimpleName();

    private static GameDataObjectDBHandler sInstance;
    private DatabaseHelper databaseHelper;

    private GameDataObjectDBHandler(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    static synchronized GameDataObjectDBHandler getInstance(DatabaseHelper databaseHelper) {
        if (sInstance == null) {
            sInstance = new GameDataObjectDBHandler(databaseHelper);
        }
        return sInstance;
    }

    GameDataObject getMostRecentGameDataForGameType(int dimension, int gameMode) {
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
            // REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED
            cursor.moveToFirst();
            GameDataObject gameDataObject = new GameDataObject();
            gameDataObject.setId(cursor.getInt(0));
            gameDataObject.setDimension(cursor.getInt(1));
            gameDataObject.setOriginalStartState(cursor.getString(2));
            gameDataObject.setToggledBulbsState(cursor.getString(3));
            gameDataObject.setUndoStackString(cursor.getString(4));
            gameDataObject.setRedoStackString(cursor.getString(5));
            gameDataObject.setGameMode(cursor.getInt(6));
            gameDataObject.setHasSeenSolution(cursor.getInt(7) == 1);
            gameDataObject.setMoveCounter(cursor.getInt(8));
            gameDataObject.setNumberOfHintsUsed(cursor.getInt(9));
            Log.d(TAG, "Found data in database for dimension " + dimension + "of game type " + gameMode + "--" + gameDataObject.toString());
            db.close();
            return gameDataObject;
        }
    }

    void addGameDataObjectToDatabase(GameDataObject gameDataObject) {
        int dimension = gameDataObject.getDimension();
        int gameMode = gameDataObject.getGameMode();
        ContentValues contentValues = this.getContentValuesFromGameDataObject(gameDataObject);
        if (this.getMostRecentGameDataForGameType(dimension, gameMode) == null) {
            this.insertGameDatabaseObjectToDatabase(gameDataObject, contentValues);
        } else {
            this.updateGameDatabaseObjectToDatabase(gameDataObject, contentValues);
        }
    }

    private ContentValues getContentValuesFromGameDataObject(GameDataObject gameDataObject) {
        // ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, UNDO_STACK_STRING,
        // REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.MostRecentGameTable.DIMENSION, gameDataObject.getDimension());
        contentValues.put(DatabaseConstants.MostRecentGameTable.ORIGINAL_START_STATE, gameDataObject.getOriginalStartState());
        contentValues.put(DatabaseConstants.MostRecentGameTable.TOGGLED_BULBS, gameDataObject.getToggledBulbsState());
        contentValues.put(DatabaseConstants.MostRecentGameTable.UNDO_STACK_STRING, gameDataObject.getUndoStackString());
        contentValues.put(DatabaseConstants.MostRecentGameTable.REDO_STACK_STRING, gameDataObject.getRedoStackString());
        contentValues.put(DatabaseConstants.MostRecentGameTable.GAME_MODE, gameDataObject.getGameMode());
        contentValues.put(DatabaseConstants.MostRecentGameTable.HAS_SEEN_SOLUTION, gameDataObject.getHasSeenSolution() ? 1 : 0);
        contentValues.put(DatabaseConstants.MostRecentGameTable.MOVE_COUNTER, gameDataObject.getMoveCounter());
        contentValues.put(DatabaseConstants.MostRecentGameTable.NUMBER_OF_HINTS_USED, gameDataObject.getNumberOfHintsUsed());
        return contentValues;
    }


    private void updateGameDatabaseObjectToDatabase(GameDataObject gameDataObject, ContentValues contentValues) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.MostRecentGameTable.TABLE_NAME;
        String whereClause = DatabaseConstants.MostRecentGameTable.DIMENSION + " = ? AND "
                                + DatabaseConstants.MostRecentGameTable.GAME_MODE + " = ?";
        String[] whereArgs = {String.valueOf(gameDataObject.getDimension()), String.valueOf(gameDataObject.getGameMode())};
        db.update(tableName, contentValues, whereClause, whereArgs);
        Log.d(TAG, "Updated database for dimension " + gameDataObject.getDimension() + " of game mode " + gameDataObject.getGameMode()
        + " with value: " + gameDataObject.toString());
        db.close();

    }

    private void insertGameDatabaseObjectToDatabase(GameDataObject gameDataObject, ContentValues contentValues) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.MostRecentGameTable.TABLE_NAME;
        db.insert(tableName, null, contentValues);
        Log.d(TAG, "Added row to database for dimension " + gameDataObject.getDimension() + " of game mode " + gameDataObject.getGameMode()
                + " with value: " + gameDataObject.toString());
        db.close();
    }

    void deleteMostRecentGameDataObjectForDimensionAndGameMode(int dimension, int gameMode) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.MostRecentGameTable.TABLE_NAME;
        String whereClause = DatabaseConstants.MostRecentGameTable.DIMENSION + " = ? AND "
                + DatabaseConstants.MostRecentGameTable.GAME_MODE + " = ? ";
        String[] whereArgs = {String.valueOf(dimension), String.valueOf(gameMode)};
        db.delete(tableName, whereClause, whereArgs);
        db.close();
        Log.d(TAG, "Cleared data for " + dimension + " by " + dimension + " and game mode " + gameMode + " from database");
    }

//    List<GameDataObject> getAllGameDataObjectsForGameType(int gameMode) {
//
//    };
}
