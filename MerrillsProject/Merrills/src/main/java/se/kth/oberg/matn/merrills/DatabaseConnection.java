package se.kth.oberg.matn.merrills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] columns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_STATE_MASK
    };

    private DatabaseConnection(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    private void open() {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
        dbHelper.close();
    }

    public static void resetDatabase(Context context){
        DatabaseConnection con = new DatabaseConnection(context);
        con.open();
        con.database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_NAME);
        con.database.execSQL(DatabaseHelper.DATABASE_CREATE);
        con.close();
    }

    public static void deleteSave(Context context, SavedGame savedGame) {
        DatabaseConnection con = new DatabaseConnection(context);
        con.open();
        con.database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = ?", new String[] {"" + savedGame.id});
        con.close();
    }

    public static void saveGame(Context context, String name, long savedGameState) {
        DatabaseConnection con = new DatabaseConnection(context);
        con.open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_STATE_MASK, savedGameState);
        con.database.insert(DatabaseHelper.TABLE_NAME, null, values);
        con.close();
    }

    public static List<SavedGame> getLoadList(Context context) {
        ArrayList<SavedGame> list = new ArrayList<>();

        try {
            DatabaseConnection con = new DatabaseConnection(context);
            con.open();

            Cursor cursor = con.database.query(DatabaseHelper.TABLE_NAME, con.columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                SavedGame savedGame = new SavedGame();
                savedGame.name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                savedGame.id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                savedGame.state = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATE_MASK));
                list.add(savedGame);
            }

            con.close();

        } catch (SQLiteException e) {
        }

        return list;
    }

    public static class SavedGame {
        private String name;
        private int id;
        private long state;

        public String toString() {
            return name;
        }

        public long getState() {
            return state;
        }
    }
}
