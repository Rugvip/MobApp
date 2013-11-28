package se.kth.oberg.matn.merrills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseConnection {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_TRUEMASK,
            DatabaseHelper.COLUMN_FALSEMASK,
            DatabaseHelper.COLUMN_TRUECOUNT,
            DatabaseHelper.COLUMN_FALSECOUNT,
            DatabaseHelper.COLUMN_TURN
    };

    public DatabaseConnection(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void resetDatabase(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_NAME);
        database.execSQL(DatabaseHelper.DATABASE_CREATE);
    }

    public void saveGame(String name, int trueMask, int falseMask, int trueCount, int falseCount, int turn) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_TRUEMASK, trueMask);
        values.put(DatabaseHelper.COLUMN_FALSEMASK, falseMask);
        values.put(DatabaseHelper.COLUMN_TRUECOUNT, trueCount);
        values.put(DatabaseHelper.COLUMN_FALSECOUNT, falseCount);
        values.put(DatabaseHelper.COLUMN_TURN, turn);
        database.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    public SavedGameState loadGame(int id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
                columns, DatabaseHelper.COLUMN_ID + "=" + id, null, null, null, null);
        cursor.moveToFirst();
        SavedGameState save = cursorToSave(cursor);
        cursor.close();
        return save;
    }

    private SavedGameState cursorToSave(Cursor cursor) {
        return new SavedGameState(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TURN)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TRUEMASK)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_FALSEMASK)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TRUECOUNT)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_FALSECOUNT))
        );
    }
}
