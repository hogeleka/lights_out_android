package com.algorithmandblues.lightsout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.LinkedList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static SQLiteDatabaseHandler sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LightsOutDB";
    private static final String TABLE_NAME = "Board";
    private static final String KEY_ID_DIMENSION = "idDimension";
    private static final String KEY_START_STATE = "startState";
    private static final String KEY_TOGGLED_BULBS = "toggledBulbs";
    private static final String KEY_UNDO_STACK_STRING = "undoStackString";
    private static final String KEY_REDO_STACK_STRING = "redoStackString";
    private static final String[] COLUMNS = {
            KEY_ID_DIMENSION, KEY_START_STATE, KEY_TOGGLED_BULBS, KEY_UNDO_STACK_STRING, KEY_REDO_STACK_STRING
    };

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
        String CREATE_LIGHTSOUT_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + KEY_ID_DIMENSION + " INTEGER PRIMARY KEY, " + KEY_START_STATE + " TEXT, " + KEY_TOGGLED_BULBS + " TEXT, " + KEY_UNDO_STACK_STRING + " TEXT, " + KEY_REDO_STACK_STRING + " TEXT )";
        db.execSQL(CREATE_LIGHTSOUT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public GameData getGameData(int dimension) {
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + KEY_ID_DIMENSION + " = " + dimension + "", null);
        Cursor cursor = db.query(true,
                TABLE_NAME, COLUMNS, " " + KEY_ID_DIMENSION + " = ?", new String[] {String.valueOf(dimension)}, null, null, null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        } else {
            cursor.moveToFirst();
            GameData gameData = new GameData();
            gameData.setDimension(Integer.parseInt(cursor.getString(0)));
            gameData.setStartState(cursor.getString(1));
            gameData.setToggledBulbsState(cursor.getString(2));
            gameData.setUndoStackString(cursor.getString(3));
            gameData.setRedoStackString(cursor.getString(4));
            return gameData;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void addGameData(GameData gameData) {
        if (this.getGameData(gameData.getDimension()) == null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ID_DIMENSION, gameData.getDimension());
            values.put(KEY_START_STATE, gameData.getStartState());
            values.put(KEY_TOGGLED_BULBS, gameData.getToggledBulbsState());
            values.put(KEY_UNDO_STACK_STRING, gameData.getUndoStackString());
            values.put(KEY_REDO_STACK_STRING, gameData.getRedoStackString());
            db.insert(TABLE_NAME, null, values);
            db.close();
        } else {
            this.updateGameData(gameData);
        }
    }


    private int updateGameData(GameData gameData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_START_STATE, gameData.getStartState());
        values.put(KEY_TOGGLED_BULBS, gameData.getToggledBulbsState());
        values.put(KEY_UNDO_STACK_STRING, gameData.getUndoStackString());
        values.put(KEY_REDO_STACK_STRING, gameData.getRedoStackString());

        int i = db.update(TABLE_NAME, values, KEY_ID_DIMENSION + " = ?", new String[] {String.valueOf(gameData.getDimension())});
        db.close();

        return i;
    }

    public List<GameData> allGameDataInstancesForAllBoardSizes() {
        List<GameData> gameDataList = new LinkedList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        GameData gameData = null;

        if (cursor.moveToFirst()) {
            do {
                gameData = new GameData();
                gameData.setDimension(Integer.parseInt(cursor.getString(0)));
                gameData.setStartState(cursor.getString(1));
                gameData.setToggledBulbsState(cursor.getString(2));
                gameData.setUndoStackString(cursor.getString(3));
                gameData.setRedoStackString(cursor.getString(4));
                gameDataList.add(gameData);

            } while (cursor.moveToNext());
        }

        return gameDataList;
    }


}
