package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import mobileappdevelopment.kevinholmes.gameoflife.SaveContract.SaveEntry;

/**
 * Created by Alex on 11/9/2017.
 */

// TODO: (Alex): During SerializableCellGrid creation, use some kind of inner property here
    // to assign each saved segment an id.
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

    // TODO: Implement stub functions
    public boolean saveSelection(Pair<boolean[][], int[][]> grids) {
        // Using this class ensures all values are in valid range
        SerializableCellGrid grid = new SerializableCellGrid(grids.first, grids.second);
        byte[] bytes = serializeCellGrid(grid);

        // TODO: (Alex): try saving to local db, if success return true, else false
        if(true) {
            return true;
        }
        else return true;
    }

    public boolean saveGrid(Pair<boolean[][], int[][]> grids) {
        // Using this class ensures all values are in valid range
        SerializableCellGrid grid = new SerializableCellGrid(grids.first, grids.second);
        byte[] bytes = serializeCellGrid(grid);

        // TODO (Alex): try saving to local db, if success return true, else false
        if(true) {
            return true;
        }
        else return true;
    }

    // Allows us the ability to convert the entire grid and stats to a bytestream for saving to sql
    public static byte[] serializeCellGrid(SerializableCellGrid o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();

            // Get the bytes of the serialized object
            return bos.toByteArray();
        } catch (IOException ioe) {
            Log.e("serializeObject", "error", ioe);

            return null;
        }
    }

    // Gets bytestream from sql and deserializes (hopefully) into a SerializableCellGrid object
    // TODO: Test this!
    public static SerializableCellGrid deserializeCellGrid(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Object object = in.readObject();
            in.close();

            // TODO: Validate this gives us back a valid grid, otherwise will have to iteratively
            // TODO: populate a new boolean[][] object manually
            return (SerializableCellGrid)object;
        } catch (ClassNotFoundException cnfe) {
            Log.e("deserializeObject", "class not found error", cnfe);

            return null;
        } catch (IOException ioe) {
            Log.e("deserializeObject", "io error", ioe);

            return null;
        }
    }
}
