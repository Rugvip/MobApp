package se.kth.oberg.matn.merrills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseConnection {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_STATE_MASK
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

    public void saveGame(String name, long savedGameState) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_STATE_MASK, savedGameState);
        database.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    public long loadGame(int id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
                columns, DatabaseHelper.COLUMN_ID + "=" + id, null, null, null, null);
        cursor.moveToFirst();
        long save = cursorToSave(cursor);
        cursor.close();
        return save;
    }

    private long cursorToSave(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATE_MASK));
    }
}
