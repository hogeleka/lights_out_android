package com.algorithmandblues.lightsout;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = DatabaseHelper.class.getSimpleName();

    private static DatabaseHelper sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LightsOutDB";


    static synchronized DatabaseHelper getInstance(Context context) {
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
        insertDefaultValuesForLevelsAndNumberOfStars(db);
        Log.d(TAG, "Created " + DatabaseConstants.databaseTableNamesAndColumns.entrySet().size()
        + " tables: " + DatabaseConstants.databaseTableNamesAndColumns.keySet().toString());
    }

    private void insertDefaultValuesForLevelsAndNumberOfStars(SQLiteDatabase db) {
//        SQLiteDatabase db = this.getWritableDatabase();
        List<Level> defaultLevels = DatabaseConstants.LevelTable.defaultLevelValues();
        Log.d(TAG, "inserting the following levels: " + defaultLevels.toString());
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Level level : defaultLevels) {
                values.put(DatabaseConstants.LevelTable.DIMENSION, level.getDimension());
                values.put(DatabaseConstants.LevelTable.NUMBER_OF_STARS, level.getNumberOfStars());
                values.put(DatabaseConstants.LevelTable.GAME_MODE, level.getGameMode());
                db.insert(DatabaseConstants.LevelTable.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Log.d(TAG, "Added " + defaultLevels.size() + " new levels to level table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tableName : DatabaseConstants.databaseTableNamesAndColumns.keySet()) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        this.onCreate(db);
    }

    void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String tableName : DatabaseConstants.databaseTableNamesAndColumns.keySet()) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
        Log.d(TAG, "Cleared all tables from database");
        this.onCreate(db);
        db.close();
    }
}

