package mobileappdevelopment.kevinholmes.gameoflife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import java.util.ArrayList;

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
                + SaveEntry.COLUMN_SAVE_DATA + " BLOB NOT NULL); ";

        db.execSQL(SQL_CREATE_SAVE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //do stuff
    }

    // TODO: Implement stub functions
    public boolean saveSelection(Pair<boolean[][], int[][]> grids) {
        return saveGrid(grids);
    }

    //Save function for saving an amount of the grid
    //TODO: needs testing
    public boolean saveGrid(Pair<boolean[][], int[][]> grids) {
        // Using this class ensures all values are in valid range
        SerializableCellGrid grid = new SerializableCellGrid(grids.first, grids.second);
        byte[] bytes = serializeCellGrid(grid);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SaveEntry.COLUMN_SAVE_NAME, "DefaultName");
        values.put(SaveEntry.COLUMN_SAVE_DATA, bytes);

        long rowid = db.insert(SaveEntry.TABLE_NAME, null, values);

        if(rowid == -1) {
            return false;
        }
        else return true;
    }

    //Allows you to request a save from the DB using its name
    //TODO: needs testing
    public Pair<boolean[][], int[][]> requestGrid(String name){
        // Execute SQL to retrieve thing with proper name
        // the new byte array will be replaced by actual data once this is working

        SQLiteDatabase db = this.getReadableDatabase();

        String requestString = "SELECT * FROM " + SaveEntry.TABLE_NAME + " WHERE " +
                               SaveEntry.COLUMN_SAVE_NAME + "=" + name;

        Cursor result = db.rawQuery(requestString, null);
        Pair<boolean[][], int[][]> grid = null;

        //Make sure this is SOME data
        //TODO: Need to add error checking here
        if(result.moveToFirst()) {
            byte[] resultArray = result.getBlob(2);

            SerializableCellGrid serializableCellGrid = deserializeCellGrid(resultArray);

            grid = new Pair<>(serializableCellGrid.getCellGrid(),
                    serializableCellGrid.getColorGrid());
        }
        return grid;
    }

    //Returns the list of all the names of the saves.
    public ArrayList<String> getSaveNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        String requestString = "SELECT * FROM " + SaveEntry.TABLE_NAME;

        Cursor result = db.rawQuery(requestString, null);
        ArrayList<String> names = new ArrayList<>();

        if(result.moveToFirst()){
            do{
                names.add(result.getString(1));
            }while(result.moveToNext());
        }

        return names;
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
