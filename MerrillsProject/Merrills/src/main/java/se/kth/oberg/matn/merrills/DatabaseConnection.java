package se.kth.oberg.matn.merrills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    private void resetDatabase(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_NAME);
        database.execSQL(DatabaseHelper.DATABASE_CREATE);
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

    public List<SavedGame> getLoadList(Context context) {
        DatabaseConnection con = new DatabaseConnection(context);
        con.open();

        ArrayList<SavedGame> list = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, "*", null, null, null, null);

        while (cursor.moveToNext()) {
            SavedGame savedGame = new SavedGame();
            savedGame.name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            savedGame.id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            savedGame.state = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATE_MASK));
            list.add(savedGame);
        }

        con.close();

        return list;
    }

    public static class SavedGame {
        private String name;
        private int id;
        private long state;

        public String getName() {
            return name;
        }
    }
}
