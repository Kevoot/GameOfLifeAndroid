package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import mobileappdevelopment.kevinholmes.gameoflife.SaveContract.SaveEntry;

/**
 * Created by Alex on 11/9/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "gameoflife.db";

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String SQL_CREATE_SAVE_TABLE = "CREATE TABLE" + SaveEntry.TABLE_NAME + " ("
                + SaveEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SaveEntry.COLUMN_SAVE_NAME + " TEXT NOT NULL, "
                + SaveEntry.COLUMN_SAVE_DATA + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_SAVE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //do stuff
    }
}
