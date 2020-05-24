package com.algorithmandblues.lightsout;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConstants {

    static Map<String, String> databaseTableNamesAndCreationStrings = new HashMap<String, String>() {{
        put(MostRecentGameTable.TABLE_NAME, MostRecentGameTable.getStringToCreateTable());
        put(GameWinStateTable.TABLE_NAME, GameWinStateTable.getStringToCreateTable());
    }};

    static Map<String, String[]> databaseTableNamesAndColumns = new HashMap<String, String[]>() {{
        put(MostRecentGameTable.TABLE_NAME, MostRecentGameTable.TABLE_COLUMNS);
        put(GameWinStateTable.TABLE_NAME, GameWinStateTable.TABLE_COLUMNS);
    }};



    static class MostRecentGameTable {
        static final String TABLE_NAME = "Board";
        static final String ID = "id";
        static final String DIMENSION = "dimension";
        static final String ORIGINAL_START_STATE = "originalStartState";
        static final String TOGGLED_BULBS = "toggledBulbs";
        static final String UNDO_STACK_STRING = "undoStackString";
        static final String REDO_STACK_STRING = "redoStackString";
        static final String GAME_MODE = "gameMode";
        static final String HAS_SEEN_SOLUTION = "hasSeenSolution";
        static final String MOVE_COUNTER = "moveCounter";
        static final String NUMBER_OF_HINTS_USED = "numberOfHintsUsed";

        public static final String[] TABLE_COLUMNS = {
                ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, UNDO_STACK_STRING,
                REDO_STACK_STRING, GAME_MODE, HAS_SEEN_SOLUTION, MOVE_COUNTER, NUMBER_OF_HINTS_USED
        };

        public static String getStringToCreateTable() {
            String sqlStringToCreateTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DIMENSION + " INTEGER, " +
                    ORIGINAL_START_STATE + " TEXT, " +
                    TOGGLED_BULBS + " TEXT, " +
                    UNDO_STACK_STRING + " TEXT, " +
                    REDO_STACK_STRING + " TEXT, " +
                    GAME_MODE + " INTEGER, " +
                    HAS_SEEN_SOLUTION + " INTEGER, " +
                    MOVE_COUNTER + " INTEGER, " +
                    NUMBER_OF_HINTS_USED + " INTEGER )";

            return sqlStringToCreateTable;
        }
    }

    static class GameWinStateTable {
        static final String TABLE_NAME = "GameWinState";
        static final String ID = "id";
        static final String DIMENSION = "dimension";
        static final String ORIGINAL_START_STATE = "originalStartState";
        static final String TOGGLED_BULBS = "toggledBulbs";
        static final String NUMBER_OF_MOVES = "numberOfMoves";
        static final String NUMBER_OF_STARS = "score";
        static final String GAME_MODE = "gameMode";
        static final String TIME_STAMP_MS = "timeStampMs";

        public static final String[] TABLE_COLUMNS = {
                ID, DIMENSION, ORIGINAL_START_STATE, TOGGLED_BULBS, NUMBER_OF_MOVES, NUMBER_OF_STARS, GAME_MODE, TIME_STAMP_MS
        };

        public static String getStringToCreateTable() {
            String sqlString = "CREATE TABLE " + TABLE_NAME + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DIMENSION + " INTEGER, " +
                    ORIGINAL_START_STATE + " STRING, " +
                    TOGGLED_BULBS + " STRING, " +
                    NUMBER_OF_MOVES + " INTEGER, " +
                    NUMBER_OF_STARS + " INTEGER, " +
                    GAME_MODE + " INTEGER, " +
                    TIME_STAMP_MS + " INTEGER )";

            return sqlString;
        }

    }
}
