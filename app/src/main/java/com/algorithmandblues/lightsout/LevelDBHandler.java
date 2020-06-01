package com.algorithmandblues.lightsout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LevelDBHandler {

    private static final String TAG = LevelDBHandler.class.getSimpleName();

    private static LevelDBHandler sInstance;
    private DatabaseHelper databaseHelper;

    private LevelDBHandler(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    static synchronized LevelDBHandler getInstance(DatabaseHelper databaseHelper) {
        if (sInstance == null) {
            sInstance = new LevelDBHandler(databaseHelper);
        }
        return sInstance;
    }

    List<Level> fetchLevelsForGameMode(int gameMode) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        String tableName = DatabaseConstants.LevelTable.TABLE_NAME;
        String[] columns = DatabaseConstants.LevelTable.TABLE_COLUMNS;
        String selection = DatabaseConstants.LevelTable.GAME_MODE + " = ? ";
        String[] selectionArgs = {String.valueOf(gameMode)};

        List<Level> levels = new ArrayList<>();
        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);
        //ID, DIMENSION, NUMBER_OF_STARS, GAME_MODE, IS_LOCKED
        if (cursor.moveToFirst()) {
            do {
                Level level = new Level(){{
                    setId(cursor.getInt(0));
                    setDimension(cursor.getInt(1));
                    setNumberOfStars(cursor.getInt(2));
                    setGameMode(cursor.getInt(3));
                    setIsLocked(cursor.getInt(4));
                }};
                levels.add(level);
            } while (cursor.moveToNext());
        }
        db.close();
        Log.d(TAG, "fetched all levels for gameMode type " + gameMode + "-- total number: " + levels.size() );
        Log.d(TAG, levels.toString().replace("}", "}\n"));
        return levels;
    }

    public void updateLevelWithNewNumberOfStars(Level level) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        String tableName = DatabaseConstants.LevelTable.TABLE_NAME;
        String whereClause = DatabaseConstants.LevelTable.DIMENSION + " = ? AND "
                + DatabaseConstants.LevelTable.GAME_MODE + " = ?";
        String[] whereArgs = {String.valueOf(level.getDimension()), String.valueOf(level.getGameMode())};
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.LevelTable.DIMENSION, level.getDimension());
        contentValues.put(DatabaseConstants.LevelTable.NUMBER_OF_STARS, level.getNumberOfStars());
        contentValues.put(DatabaseConstants.LevelTable.GAME_MODE, level.getGameMode());
        contentValues.put(DatabaseConstants.LevelTable.IS_LOCKED, level.getIsLocked());
        db.update(tableName, contentValues, whereClause, whereArgs);
        Log.d(TAG, "Updated level data for dimension " + level.getDimension() +
                " with new number of stars " + level.getNumberOfStars() + ". Level isLocked=" + level.getIsLocked());
        db.close();
    }


    public Level getLevelFromDb(int gameMode, int dimension) {
        Log.d(TAG, "Fetching level data for dimension size: " + dimension + " by " + dimension + " of game type " + gameMode);
        boolean distinct = true;
        String tableName = DatabaseConstants.LevelTable.TABLE_NAME;
        String[] columns = DatabaseConstants.LevelTable.TABLE_COLUMNS;
        String whereClause = DatabaseConstants.LevelTable.DIMENSION + " = ? AND "
                + DatabaseConstants.LevelTable.GAME_MODE + " = ? ";
        String[] selectionArgs = {String.valueOf(dimension), String.valueOf(gameMode)};
        String limit = String.valueOf(1);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(distinct, tableName, columns, whereClause, selectionArgs, null, null, null, limit);

        // ID, DIMENSION, NUMBER_OF_STARS, GAME_MODE, IS_LOCKED
        if (cursor.getCount() == 0) {
            return null;
        } else {
            cursor.moveToFirst();
            Level level = new Level() {{
                setId(cursor.getInt(0));
                setDimension(cursor.getInt(1));
                setNumberOfStars(cursor.getInt(2));
                setGameMode(cursor.getInt(3));
                setIsLocked(cursor.getInt(4));
            }};
        }

        Log.d(TAG, "Found level in database for dimension " + dimension + "of game type " + gameMode + "--" + level.toString());
        db.close();

        return level;
    }
}
