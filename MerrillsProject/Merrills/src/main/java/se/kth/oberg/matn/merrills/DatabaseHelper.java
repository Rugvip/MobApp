package se.kth.oberg.matn.merrills;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqliteDatabase = null;
    public static final String DATABASE_NAME = "savedGames.db";
    public static final String TABLE_NAME = "games";
    public static final String COLUMN_ID = "gameId";
    public static final String COLUMN_NAME = "gameName";
    public static final String COLUMN_STATE_MASK = "stateMask";

    public static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_STATE_MASK + " integer not null "
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.e("DatabaseHelper", "DatabaseHelper onUpgrade should be run here, 404");
    }
}
